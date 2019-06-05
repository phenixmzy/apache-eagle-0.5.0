package org.apache.eagle.hadoop.queue.model.applications;

import org.apache.eagle.dataproc.impl.storm.ValuesArray;
import org.apache.eagle.hadoop.queue.common.HadoopClusterConstants;
import org.apache.eagle.hadoop.queue.storm.HadoopQueueMessageId;
import org.apache.eagle.log.base.taggedlog.TaggedLogAPIEntity;
import org.apache.eagle.log.entity.GenericMetricEntity;
import org.apache.storm.spout.SpoutOutputCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ApplicationInfoParseListener {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationInfoParseListener.class);
    private final List<TaggedLogAPIEntity> runningAppAPIEntities = new ArrayList<>();

    private String site;
    private SpoutOutputCollector collector;


    public ApplicationInfoParseListener(String site, SpoutOutputCollector collector) {
        this.site = site;
        this.collector = collector;
    }

    public void onStored(AppsWrapper appsWrapper) {
        for (App app : appsWrapper.getApps().getApp()) {
            createAPIEntity(app);
        }
    }

    private void createAPIEntity(App app) {
        YarnAppAPIEntity entity = new YarnAppAPIEntity();
        this.runningAppAPIEntities.add(entity);
    }

    public void flush() {
        HadoopQueueMessageId messageId = new HadoopQueueMessageId(HadoopClusterConstants.DataType.ENTITY, HadoopClusterConstants.DataSource.RUNNING_APPS, System.currentTimeMillis());
        List<TaggedLogAPIEntity> entities = new ArrayList<>(runningAppAPIEntities);
        collector.emit(new ValuesArray(HadoopClusterConstants.DataSource.RUNNING_APPS, HadoopClusterConstants.DataType.ENTITY, entities), messageId);
        this.runningAppAPIEntities.clear();
    }
}
