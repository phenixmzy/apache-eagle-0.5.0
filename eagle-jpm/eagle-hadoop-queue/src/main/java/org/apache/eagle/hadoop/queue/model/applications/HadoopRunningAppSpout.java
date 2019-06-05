package org.apache.eagle.hadoop.queue.model.applications;

import org.apache.eagle.hadoop.queue.HadoopQueueRunningAppConfig;
import org.apache.eagle.hadoop.queue.common.HadoopClusterConstants;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class HadoopRunningAppSpout extends BaseRichSpout {
    private static final Logger LOG = LoggerFactory.getLogger(HadoopRunningAppSpout.class);
    private HadoopRunningAppExtractor extractor;
    private HadoopQueueRunningAppConfig config;
    private long fetchIntervalSec;
    private long lastFetchTime = 0;

    public HadoopRunningAppSpout(HadoopQueueRunningAppConfig config) {
        this.config = config;
        this.fetchIntervalSec = Long.parseLong(config.dataSourceConfig.fetchIntervalSec);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(HadoopClusterConstants.FIELD_DATASOURCE,
                HadoopClusterConstants.FIELD_DATATYPE,
                HadoopClusterConstants.FIELD_DATA));
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        extractor = new HadoopRunningAppExtractor(config, collector);
    }

    @Override
    public void nextTuple() {
        try {
            long fetchTime = System.currentTimeMillis();
            if (fetchTime > this.fetchIntervalSec * 1000 + this.lastFetchTime) {
                extractor.crawl();
                lastFetchTime = fetchTime;
            }
        } catch (Exception ex) {
            LOG.error("Fail crawling running queue resources and continue ...", ex);
        }
    }
}
