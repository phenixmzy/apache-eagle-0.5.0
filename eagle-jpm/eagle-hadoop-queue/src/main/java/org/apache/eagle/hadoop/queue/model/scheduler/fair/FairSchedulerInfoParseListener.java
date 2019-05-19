package org.apache.eagle.hadoop.queue.model.scheduler.fair;

import org.apache.eagle.dataproc.impl.storm.ValuesArray;
import org.apache.eagle.hadoop.queue.common.HadoopClusterConstants;
import org.apache.eagle.hadoop.queue.crawler.SchedulerInfoParseListener;
import org.apache.eagle.hadoop.queue.model.scheduler.*;
import org.apache.eagle.hadoop.queue.storm.HadoopQueueMessageId;
import org.apache.eagle.log.base.taggedlog.TaggedLogAPIEntity;
import org.apache.eagle.log.entity.GenericMetricEntity;
import org.apache.storm.spout.SpoutOutputCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FairSchedulerInfoParseListener {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulerInfoParseListener.class);
    //private final static long AGGREGATE_INTERVAL = DateTimeUtil.ONEMINUTE;
    //private int MAX_CACHE_COUNT = 1000;

    private final List<TaggedLogAPIEntity> runningQueueAPIEntities = new ArrayList<>();
    private final List<GenericMetricEntity> metricEntities = new ArrayList<>();

    private String site;
    private String scheduler;
    private SpoutOutputCollector collector;

    public FairSchedulerInfoParseListener(String site, SpoutOutputCollector collector) {
        this.site = site;
        this.collector = collector;
    }

    public void onMetric(FairSchedulerInfo fairScheduler, long currentTimestamp) throws Exception {
        Map<String, String> tags = buildMetricTags(null, null);
        createMetric(HadoopClusterConstants.MetricName.HADOOP_CLUSTER_CAPACITY, tags, currentTimestamp, 0 /*scheduler.getCapacity()*/);
        createMetric(HadoopClusterConstants.MetricName.HADOOP_CLUSTER_USED_CAPACITY, tags, currentTimestamp, 0/* scheduler.getUsedCapacity()*/);
        for (FairChildQueues childQueue : fairScheduler.getRootQueue().getChildQueues()) {
            createQueues(childQueue, currentTimestamp, fairScheduler, null);
        }
    }

    public void flush() {
        LOG.info("Flushing {} RunningQueue metrics in memory", metricEntities.size());
        HadoopQueueMessageId messageId = new HadoopQueueMessageId(HadoopClusterConstants.DataType.METRIC, HadoopClusterConstants.DataSource.SCHEDULER, System.currentTimeMillis());
        List<GenericMetricEntity> metrics = new ArrayList<>(metricEntities);
        collector.emit(new ValuesArray(HadoopClusterConstants.DataSource.SCHEDULER, HadoopClusterConstants.DataType.METRIC, metrics), messageId);

        LOG.info("Flushing {} RunningQueueEntities in memory", runningQueueAPIEntities.size());
        messageId = new HadoopQueueMessageId(HadoopClusterConstants.DataType.ENTITY, HadoopClusterConstants.DataSource.SCHEDULER, System.currentTimeMillis());
        List<TaggedLogAPIEntity> entities = new ArrayList<>(runningQueueAPIEntities);
        collector.emit(new ValuesArray(HadoopClusterConstants.DataSource.SCHEDULER, HadoopClusterConstants.DataType.ENTITY, entities), messageId);

        runningQueueAPIEntities.clear();
        metricEntities.clear();

    }

    private Map<String, String> buildMetricTags(String queueName, String parentQueueName) {
        Map<String, String> tags = new HashMap<>();
        tags.put(HadoopClusterConstants.TAG_SITE, this.site);
        if (queueName != null) {
            tags.put(HadoopClusterConstants.TAG_QUEUE, queueName);
        }
        if (parentQueueName != null) {
            tags.put(HadoopClusterConstants.TAG_PARENT_QUEUE, parentQueueName);
        }
        return tags;
    }

    private void createMetric(String metricName, Map<String, String> tags, long timestamp, double value) throws Exception {
        GenericMetricEntity e = new GenericMetricEntity();
        e.setPrefix(metricName);
        e.setTimestamp(timestamp);
        e.setTags(tags);
        e.setValue(new double[] {value});
        this.metricEntities.add(e);
    }

    private List<String> createQueues(FairChildQueues queue, long currentTimestamp, FairSchedulerInfo scheduler, String parentQueueName) throws Exception {

        FairRunningQueueAPIEntity _entity = new FairRunningQueueAPIEntity();
        Map<String, String> _tags = buildMetricTags(queue.getQueueName(), parentQueueName);
        _entity.setTags(_tags);

        _entity.setScheduler(scheduler.getType());
        _entity.setTimestamp(currentTimestamp);

        runningQueueAPIEntities.add(_entity);

        createMetric(HadoopClusterConstants.MetricName.HADOOP_QUEUE_NUMPENDING_JOBS, _tags, currentTimestamp, queue.getNumActiveApps());
        createMetric(HadoopClusterConstants.MetricName.HADOOP_QUEUE_USED_CAPACITY, _tags, currentTimestamp, queue.getUsedResources().getMemory());
        createMetric("root.allocated.containers", _tags, currentTimestamp, queue.getAllocatedContainers());
        createMetric("root.pending.containers", _tags, currentTimestamp, queue.getPendingContainers());
        if (queue.getUsedResources().getMemory() == 0 && queue.getUsedResources().getvCores() == 0) {
            createMetric(HadoopClusterConstants.MetricName.HADOOP_QUEUE_USED_CAPACITY_RATIO, _tags, currentTimestamp, 0);
        } else {
            if (queue.getSchedulingPolicy().toLowerCase().equals("fair")) {
                createMetric(HadoopClusterConstants.MetricName.HADOOP_QUEUE_USED_CAPACITY_RATIO, _tags, currentTimestamp, queue.getUsedResources().getMemory() / queue.getClusterResources().getMemory());
            } else if (queue.getSchedulingPolicy().toLowerCase().equals("drf")) {
                double resourceRatio = Math.max(
                        (queue.getUsedResources().getMemory() / queue.getClusterResources().getMemory()),
                        queue.getUsedResources().getvCores() / queue.getClusterResources().getvCores());
                createMetric(HadoopClusterConstants.MetricName.HADOOP_QUEUE_USED_CAPACITY_RATIO, _tags, currentTimestamp, resourceRatio);
            }
        }

        List<String> subQueues = new ArrayList<>();
        List<String> allSubQueues = new ArrayList<>();

        if (queue.getChildQueues() != null) {
            for (FairChildQueues subQueue : queue.getChildQueues()) {
                subQueues.add(subQueue.getQueueName());
                allSubQueues.add(subQueue.getQueueName());
                List<String> queues = createQueues(subQueue, currentTimestamp, scheduler, queue.getQueueName());
                allSubQueues.addAll(queues);
            }
        }
        QueueStructureAPIEntity queueStructureAPIEntity = new QueueStructureAPIEntity();
        queueStructureAPIEntity.setTags(_tags);
        queueStructureAPIEntity.setSubQueues(subQueues);
        queueStructureAPIEntity.setAllSubQueues(allSubQueues);
        queueStructureAPIEntity.setLastUpdateTime(currentTimestamp);
        runningQueueAPIEntities.add(queueStructureAPIEntity);
        return allSubQueues;
    }
}
