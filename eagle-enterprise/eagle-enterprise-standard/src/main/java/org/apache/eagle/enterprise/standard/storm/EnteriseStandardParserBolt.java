package org.apache.eagle.enterprise.standard.storm;

import com.typesafe.config.Config;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class EnteriseStandardParserBolt extends BaseRichBolt {
    private static Logger LOG = LoggerFactory.getLogger(EnteriseStandardParserBolt.class);
    private static final String DATASOURCE_TIMEZONE_PATH = "dataSourceConfig.timeZone";

    private OutputCollector collector;

    public EnteriseStandardParserBolt(Config config) {

    }

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple tuple) {

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }
}
