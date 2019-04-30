package org.apache.eagle.app.messaging;

import org.apache.storm.spout.Scheme;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.eagle.alert.engine.spout.SchemeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.storm.kafka.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class KafkaStreamSource extends StormStreamSource<KafkaStreamSourceConfig> {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaStreamSource.class);
    private KafkaSpout spout;

    @Override
    public void init(String streamId, KafkaStreamSourceConfig config) {
        this.spout = createKafkaSpout(config);
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.spout.open(conf, context, collector);
    }

    @Override
    public void nextTuple() {
        this.spout.nextTuple();
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        this.spout.declareOutputFields(declarer);
    }

    @Override
    public void close() {
        this.spout.close();
    }

    @Override
    public void activate() {
        this.spout.activate();
    }

    @Override
    public void deactivate() {
        this.spout.deactivate();
    }

    @Override
    public void ack(Object msgId) {
        this.spout.ack(msgId);
    }

    @Override
    public void fail(Object msgId) {
        this.spout.fail(msgId);
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return this.spout.getComponentConfiguration();
    }

    // ----------------
    //  Helper Methods
    // ----------------

    private static KafkaSpout createKafkaSpout(KafkaStreamSourceConfig config) {

        // the following is for fetching data from one topic
        // Kafka topic
        String topic = config.getTopicId();
        // Kafka broker zk connection
        String zkConnString = config.getBrokerZkQuorum();
        // Kafka fetch size
        int fetchSize = config.getFetchSize();
        LOG.info(String.format("Use topic : %s, zkQuorum : %s , fetchSize : %d", topic, zkConnString, fetchSize));

        /*
         the following is for recording offset for processing the data
         the zk path to store current offset is comprised of the following
         offset zkPath = zkRoot + "/" + topic + "/" + consumerGroupId + "/" + partition_Id

         consumerGroupId is for differentiating different consumers which consume the same topic
        */
        // transaction zkRoot
        String zkRoot = config.getTransactionZKRoot();
        // Kafka consumer group id
        String groupId = config.getConsumerGroupId();
        String brokerZkPath = config.getBrokerZkPath();

        BrokerHosts hosts;
        if (StringUtils.isNotBlank(brokerZkPath)) {
            hosts = new ZkHosts(zkConnString, brokerZkPath);
        } else {
            hosts = new ZkHosts(zkConnString);
        }

        SpoutConfig spoutConfig = new SpoutConfig(hosts,
                topic,
                zkRoot + "/" + topic,
                groupId);

        // transaction zkServers to store kafka consumer offset. Default to use storm zookeeper
        if (StringUtils.isNotBlank(config.getTransactionZkServers())) {
            String[] txZkServers = config.getTransactionZkServers().split(",");
            spoutConfig.zkServers = Arrays.stream(txZkServers).map(server -> server.split(":")[0]).collect(Collectors.toList());
            spoutConfig.zkPort = Integer.parseInt(txZkServers[0].split(":")[1]);
            LOG.info("txZkServers:" + spoutConfig.zkServers + ", zkPort:" + spoutConfig.zkPort);
        }

        // transaction update interval
        spoutConfig.stateUpdateIntervalMs = config.getTransactionStateUpdateMS();
        // Kafka fetch size
        spoutConfig.fetchSizeBytes = fetchSize;
        spoutConfig.startOffsetTime = kafka.api.OffsetRequest.LatestTime();

        // "startOffsetTime" is for test usage, prod should not use this
        if (config.getStartOffsetTime() >= 0) {
            spoutConfig.startOffsetTime = config.getStartOffsetTime();
        }
        // "forceFromStart" is for test usage, prod should not use this
        if (config.isForceFromStart()) {
            spoutConfig.startOffsetTime = kafka.api.OffsetRequest.EarliestTime();
        }

        Preconditions.checkNotNull(config.getSchemaClass(), "schemaClass is null");
        try {
            Scheme s = config.getSchemaClass().newInstance();
            spoutConfig.scheme = new SchemeAsMultiScheme(s);
        } catch (Exception ex) {
            LOG.error("Error instantiating scheme object");
            throw new IllegalStateException(ex);
        }
        return new KafkaSpout(spoutConfig);
    }
}