package org.apache.eagle.enterprise.standard.storm;

import com.typesafe.config.Config;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.eagle.app.StormApplication;
import org.apache.eagle.app.environment.impl.StormEnvironment;
import org.apache.eagle.app.messaging.StormStreamSink;
import org.apache.eagle.common.config.EagleConfigConstants;
import org.apache.eagle.dataproc.impl.storm.kafka.KafkaSpoutProvider;
import org.apache.eagle.dataproc.impl.storm.partition.DataDistributionDao;
import org.apache.eagle.dataproc.impl.storm.partition.PartitionAlgorithm;
import org.apache.eagle.dataproc.impl.storm.partition.PartitionStrategy;
import org.apache.eagle.dataproc.impl.storm.partition.PartitionStrategyImpl;
import org.apache.eagle.security.partition.DataDistributionDaoImpl;
import org.apache.eagle.security.partition.GreedyPartitionAlgorithm;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.BoltDeclarer;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseRichBolt;

public class EnteriseYarnApplication extends StormApplication {
    public final static String SPOUT_TASK_NUM = "topology.numOfSpoutTasks";
    public final static String PARSER_TASK_NUM = "topology.numOfParserTasks";
    public final static String SINK_TASK_NUM = "topology.numOfSinkTasks";

    @Override
    public StormTopology execute(Config config, StormEnvironment environment) {
        TopologyBuilder builder = new TopologyBuilder();
        KafkaSpoutProvider provider = new KafkaSpoutProvider();
        IRichSpout spout = provider.getSpout(config);
        int numOfSpoutTasks = config.getInt(SPOUT_TASK_NUM);
        int numOfParserTasks = config.getInt(PARSER_TASK_NUM);
        int numOfSinkTasks = config.getInt(SINK_TASK_NUM);
        builder.setSpout("ingest", spout, numOfSpoutTasks).setNumTasks(numOfSpoutTasks);
        BaseRichBolt parserBolt = getParserBolt(config);
        BoltDeclarer boltDeclarer = builder.setBolt("parserBolt", parserBolt, numOfParserTasks).setNumTasks(numOfParserTasks).shuffleGrouping("ingest");
        boltDeclarer.shuffleGrouping("ingest");

        StormStreamSink sinkBolt = environment.getStreamSink("yarn_app_enterprise_standard_stream", config);
        BoltDeclarer kafkaBoltDeclarer = builder.setBolt("kafkaSink", sinkBolt, numOfSinkTasks).setNumTasks(numOfSinkTasks);
        kafkaBoltDeclarer.shuffleGrouping("parserBolt");
        return builder.createTopology();
    }

    public BaseRichBolt getParserBolt(Config config) {
        return new EnteriseStandardParserBolt(config);
    }

    public static PartitionStrategy createStrategy(Config config) {
        // TODO: Refactor configuration structure to avoid repeated config processing configure ~ hao
        String host = config.getString(EagleConfigConstants.EAGLE_PROPS + "." + EagleConfigConstants.EAGLE_SERVICE + "." + EagleConfigConstants.HOST);
        Integer port = config.getInt(EagleConfigConstants.EAGLE_PROPS + "." + EagleConfigConstants.EAGLE_SERVICE + "." + EagleConfigConstants.PORT);
        String username = config.getString(EagleConfigConstants.EAGLE_PROPS + "." + EagleConfigConstants.EAGLE_SERVICE + "." + EagleConfigConstants.USERNAME);
        String password = config.getString(EagleConfigConstants.EAGLE_PROPS + "." + EagleConfigConstants.EAGLE_SERVICE + "." + EagleConfigConstants.PASSWORD);
        String topic = config.getString("dataSourceConfig.topic");
        DataDistributionDao dao = new DataDistributionDaoImpl(host, port, username, password, topic);
        PartitionAlgorithm algorithm = new GreedyPartitionAlgorithm();
        String key1 = EagleConfigConstants.EAGLE_PROPS + ".partitionRefreshIntervalInMin";
        Integer partitionRefreshIntervalInMin = config.hasPath(key1) ? config.getInt(key1) : 60;
        String key2 = EagleConfigConstants.EAGLE_PROPS + ".kafkaStatisticRangeInMin";
        Integer kafkaStatisticRangeInMin = config.hasPath(key2) ? config.getInt(key2) : 60;
        PartitionStrategy strategy = new PartitionStrategyImpl(dao, algorithm, partitionRefreshIntervalInMin * DateUtils.MILLIS_PER_MINUTE, kafkaStatisticRangeInMin * DateUtils.MILLIS_PER_MINUTE);
        return strategy;
    }
}
