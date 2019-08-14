package org.apache.eagle.hadoop.queue.model.applications;

import org.apache.eagle.common.DateTimeUtil;
import org.apache.eagle.dataproc.impl.storm.ValuesArray;
import org.apache.eagle.hadoop.queue.common.HadoopClusterConstants;
import org.apache.eagle.hadoop.queue.storm.HadoopQueueMessageId;
import org.apache.eagle.hadoop.queue.common.HadoopClusterConstants.MetricName;
import org.apache.eagle.log.entity.GenericMetricEntity;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.apache.storm.spout.SpoutOutputCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;


public class ClusterApplicationsMetricsListener {
    private static final Logger LOG = LoggerFactory.getLogger(ClusterApplicationsMetricsListener.class);

    private Map<ClusterApplicationsMetricsListener.MetricKey, GenericMetricEntity> clusterMetricEntities = new HashMap<>();
    private Map<ClusterApplicationsMetricsListener.MetricKey, Integer> clusterMetricCounts = new HashMap<>();

    private static final long AGGREGATE_INTERVAL = DateTimeUtil.ONEMINUTE;
    private static final long HOLD_TIME_WINDOW = 2 * DateTimeUtil.ONEMINUTE;

    private long maxTimestamp;
    private String site;
    private SpoutOutputCollector collector;

    public ClusterApplicationsMetricsListener(String site, SpoutOutputCollector collector) {
        this.site = site;
        this.collector = collector;
    }

    public void onMetric(AppsWrapper appsWrapper, long timestamp) {
        if (timestamp > maxTimestamp) {
            maxTimestamp = timestamp;
        }

        int runningContainers = 0;
        for (App app : appsWrapper.getApps().getApp()) {
            runningContainers += app.getRunningContainers();
        }

        createMetric(MetricName.HADOOP_CLUSTER_RUNNING_CONTAINERS, timestamp, runningContainers, HadoopClusterConstants.AggregateFunc.AVG);
    }

    public void flush() {
        HadoopQueueMessageId messageId = new HadoopQueueMessageId(HadoopClusterConstants.DataType.METRIC, HadoopClusterConstants.DataSource.CLUSTER_METRIC, System.currentTimeMillis());
        List<GenericMetricEntity> metrics = new ArrayList<>(clusterMetricEntities.values());
        this.collector.emit(new ValuesArray(HadoopClusterConstants.DataSource.CLUSTER_METRIC, HadoopClusterConstants.DataType.METRIC, metrics), messageId);
        reset();
    }

    private void createMetric(String metricName, long timestamp, double value, HadoopClusterConstants.AggregateFunc aggFunc) {
        timestamp = timestamp / AGGREGATE_INTERVAL * AGGREGATE_INTERVAL;
        ClusterApplicationsMetricsListener.MetricKey key = new ClusterApplicationsMetricsListener.MetricKey("", timestamp);
        GenericMetricEntity entity = clusterMetricEntities.get(key);
        if (entity == null) {
            entity = new GenericMetricEntity();
            entity.setTags(buildMetricTags());
            entity.setTimestamp(timestamp);
            entity.setPrefix(metricName);
            entity.setValue(new double[]{0.0});
            clusterMetricEntities.put(key, entity);
        }
        clusterMetricCounts.putIfAbsent(key, 0);
        updateEntityAggValue(entity, aggFunc, value, clusterMetricCounts.get(key));
        clusterMetricCounts.put(key, clusterMetricCounts.get(key) + 1);
    }

    private void reset() {
        maxTimestamp = 0;
        clearOldCache();
    }

    private void clearOldCache() {
        List<ClusterApplicationsMetricsListener.MetricKey> removedkeys =
                clusterMetricEntities.keySet().stream().filter(key -> key.createTime < maxTimestamp - HOLD_TIME_WINDOW)
                        .collect(Collectors.toList());
        for (ClusterApplicationsMetricsListener.MetricKey key : removedkeys) {
            clusterMetricEntities.remove(key);
        }
    }

    private Map<String, String> buildMetricTags() {
        Map<String, String> tags = new HashMap<String, String>();
        tags.put(HadoopClusterConstants.TAG_SITE, site);
        return tags;
    }

    private void updateEntityAggValue(GenericMetricEntity entity,
                                      HadoopClusterConstants.AggregateFunc aggFunc,
                                      double value,
                                      double count) {
        double lastValue = entity.getValue()[0];
        switch (aggFunc) {
            case MAX:
                entity.setValue(new double[]{Math.max(lastValue, value)});
                return;
            case AVG:
                long avgValue = (long) ((lastValue * count + value) / (count + 1));
                entity.setValue(new double[]{avgValue});
                return;
            default:
                throw new IllegalArgumentException("Illegal aggregation function: " + aggFunc);
        }
    }

    private class MetricKey {
        String metricName;
        Long createTime;

        public MetricKey(String metricName, Long timestamp) {
            this.metricName = metricName;
            this.createTime = timestamp;
        }

        public boolean equals(Object obj) {
            if (obj instanceof ClusterApplicationsMetricsListener.MetricKey) {
                ClusterApplicationsMetricsListener.MetricKey key = (ClusterApplicationsMetricsListener.MetricKey) obj;
                if (key == null) {
                    return false;
                }
                return Objects.equals(metricName, key.metricName) & Objects.equals(createTime, key.createTime);
            }
            return false;
        }

        public int hashCode() {
            return new HashCodeBuilder().append(metricName).append(createTime).toHashCode();
        }
    }
}
