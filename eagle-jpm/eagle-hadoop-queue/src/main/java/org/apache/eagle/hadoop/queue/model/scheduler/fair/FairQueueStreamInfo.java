package org.apache.eagle.hadoop.queue.model.scheduler.fair;

import org.apache.eagle.hadoop.queue.common.HadoopClusterConstants;

import java.util.HashMap;
import java.util.Map;

public class FairQueueStreamInfo {
    private static final String TIMESTAMP = "timestamp";
    private static final String QUEUE_SITE = "site";
    public static final String QUEUE_NAME = "queue";
    private static final String QUEUE_STATE = "state";
    private static final String QUEUE_SCHEDULER = "scheduler";
    private static final String QUEUE_ALLOCATED_CONTAINERS = "allocatedContainers";
    private static final String QUEUE_PENDING_CONTAINERS = "pendingContainers";
    private static final String QUEUE_RESERVED_CONTAINERS = "reservedContainers";
    private static final String QUEUE_CLUSTER_MEM = "clusterMem";
    private static final String QUEUE_CLUSTER_VCORES = "clusterVcores";
    private static final String QUEUE_MIM_MEM = "minMem";
    private static final String QUEUE_MIN_VCORES = "minVcores";
    private static final String QUEUE_MAX_MEM = "maxMem";
    private static final String QUEUE_MAX_VCORES = "maxVcores";
    private static final String QUEUE_USED_MEMORY = "usedMem";
    private static final String QUEUE_USED_VCORES = "usedVcores";
    private static final String QUEUE_NUM_ACTIVE_APPS = "numActiveApps";
    private static final String QUEUE_NUM_PENDING_APPS = "numPendingApps";
    private static final String QUEUE_MAX_APPS = "maxApps";


    public static Map<String, Object> convertEntityToStream(FairRunningQueueAPIEntity queueAPIEntity) {
        Map<String, Object> queueInfoMap = new HashMap<>();
        queueInfoMap.put(FairQueueStreamInfo.QUEUE_SITE, queueAPIEntity.getTags().get(HadoopClusterConstants.TAG_SITE));
        queueInfoMap.put(FairQueueStreamInfo.QUEUE_NAME, queueAPIEntity.getTags().get(HadoopClusterConstants.TAG_QUEUE));
        queueInfoMap.put(FairQueueStreamInfo.QUEUE_ALLOCATED_CONTAINERS, queueAPIEntity.getAllocatedContainers());
        queueInfoMap.put(FairQueueStreamInfo.QUEUE_PENDING_CONTAINERS, queueAPIEntity.getPendingContainers());
        queueInfoMap.put(FairQueueStreamInfo.QUEUE_RESERVED_CONTAINERS, queueAPIEntity.getReservedContainers());
        queueInfoMap.put(FairQueueStreamInfo.QUEUE_MAX_APPS, queueAPIEntity.getMaxApps());
        queueInfoMap.put(FairQueueStreamInfo.QUEUE_NUM_ACTIVE_APPS, queueAPIEntity.getNumActiveApps());
        queueInfoMap.put(FairQueueStreamInfo.QUEUE_NUM_PENDING_APPS, queueAPIEntity.getNumPendingApps());
        queueInfoMap.put(FairQueueStreamInfo.QUEUE_SCHEDULER, queueAPIEntity.getScheduler());
        queueInfoMap.put(FairQueueStreamInfo.QUEUE_STATE, queueAPIEntity.getState());


        queueInfoMap.put(FairQueueStreamInfo.QUEUE_USED_MEMORY, queueAPIEntity.getUsedMemory());
        queueInfoMap.put(FairQueueStreamInfo.QUEUE_USED_VCORES, queueAPIEntity.getUsedVcores());

        queueInfoMap.put(FairQueueStreamInfo.QUEUE_MIM_MEM, queueAPIEntity.getMinMemory());
        queueInfoMap.put(FairQueueStreamInfo.QUEUE_MIN_VCORES, queueAPIEntity.getMinVcores());

        queueInfoMap.put(FairQueueStreamInfo.QUEUE_MAX_MEM, queueAPIEntity.getMaxMemory());
        queueInfoMap.put(FairQueueStreamInfo.QUEUE_MAX_VCORES, queueAPIEntity.getMaxVcores());

        queueInfoMap.put(FairQueueStreamInfo.QUEUE_CLUSTER_MEM, queueAPIEntity.getClusterMemory());
        queueInfoMap.put(FairQueueStreamInfo.QUEUE_CLUSTER_VCORES, queueAPIEntity.getClusterVcores());

        queueInfoMap.put(FairQueueStreamInfo.TIMESTAMP, queueAPIEntity.getTimestamp());

        return queueInfoMap;
    }

    private static double calculatequeueUsedCapacity(boolean isDRF, long usedMem, long usedVcores, long clusterMem, long clusterVcores) {
        if (isDRF) {
            return Math.max(usedMem / clusterMem, usedVcores / clusterVcores);
        } else {
            return usedMem / clusterMem;
        }
    }
}
