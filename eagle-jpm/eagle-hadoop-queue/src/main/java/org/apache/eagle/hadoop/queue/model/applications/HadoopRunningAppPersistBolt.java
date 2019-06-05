package org.apache.eagle.hadoop.queue.model.applications;

import org.apache.eagle.hadoop.queue.HadoopQueueRunningAppConfig;
import org.apache.eagle.hadoop.queue.common.HadoopClusterConstants;
import org.apache.eagle.hadoop.queue.model.scheduler.fair.FairQueueStreamInfo;
import org.apache.eagle.hadoop.queue.model.scheduler.fair.FairRunningQueueAPIEntity;
import org.apache.eagle.log.base.taggedlog.TaggedLogAPIEntity;
import org.apache.eagle.log.entity.GenericServiceAPIResponseEntity;
import org.apache.eagle.service.client.IEagleServiceClient;
import org.apache.eagle.service.client.impl.EagleServiceClientImpl;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class HadoopRunningAppPersistBolt extends BaseRichBolt {

    private static final Logger LOG = LoggerFactory.getLogger(HadoopRunningAppPersistBolt.class);
    private HadoopQueueRunningAppConfig config;
    private IEagleServiceClient client;
    private OutputCollector collector;

    public HadoopRunningAppPersistBolt(HadoopQueueRunningAppConfig config) {
        this.config = config;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        HadoopQueueRunningAppConfig.EagleProps.EagleService eagleService = config.eagleProps.eagleService;
        this.client = new EagleServiceClientImpl(
                eagleService.host,
                eagleService.port,
                eagleService.username,
                eagleService.password);
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        if (input == null) {
            return;
        }
        HadoopClusterConstants.DataSource dataSource = (HadoopClusterConstants.DataSource) input.getValueByField(HadoopClusterConstants.FIELD_DATASOURCE);
        HadoopClusterConstants.DataType dataType = (HadoopClusterConstants.DataType) input.getValueByField(HadoopClusterConstants.FIELD_DATATYPE);
        Object data = input.getValueByField(HadoopClusterConstants.FIELD_DATA);

        List<TaggedLogAPIEntity> entities = (List<TaggedLogAPIEntity>) data;

        for (TaggedLogAPIEntity entity : entities) {
            if (entity instanceof YarnAppAPIEntity) {
                YarnAppAPIEntity app = (YarnAppAPIEntity) entity;
                if (app.getState().equals("RUNNING")) {
                    writeEntities(entities, dataType, dataSource);
                    collector.emit(new Values(app.getQueue(), AppStreamInfo.convertAppToStream(app)));
                }
            }
        }
        this.collector.ack(input);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("f1", "message"));
    }

    @Override
    public void cleanup() {
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    private void writeEntities(List<TaggedLogAPIEntity> entities, HadoopClusterConstants.DataType dataType, HadoopClusterConstants.DataSource dataSource) {
        try {
            GenericServiceAPIResponseEntity response = client.create(entities);
            if (!response.isSuccess()) {
                LOG.error("Got exception from eagle service: " + response.getException());
            } else {
                LOG.info("Successfully wrote {} items of {} for {}", entities.size(), dataType, dataSource);
            }
        } catch (Exception e) {
            LOG.error("cannot create {} entities", entities.size(), e);
        }
        entities.clear();
    }
}
