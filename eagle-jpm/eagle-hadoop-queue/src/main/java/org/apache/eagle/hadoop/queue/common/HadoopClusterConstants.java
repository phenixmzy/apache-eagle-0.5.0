/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.eagle.hadoop.queue.common;

public class HadoopClusterConstants {

    public enum AggregateFunc {
        MAX, AVG
    }

    public enum DataType {
        METRIC, ENTITY
    }

    public enum DataSource {
        CLUSTER_METRIC, RUNNING_APPS, SCHEDULER
    }

    public enum AppState {
        RUNNING, ACCEPTED
    }

    public static class MetricName {

        // Metrics from running apps
        public static final String HADOOP_APPS_ALLOCATED_MB = "hadoop.%s.allocatedmb";
        public static final String HADOOP_APPS_ALLOCATED_VCORES = "hadoop.%s.allocatedvcores";
        public static final String HADOOP_APPS_RUNNING_CONTAINERS = "hadoop.%s.runningcontainers";

        // metrics from cluster metrics
        public static final String HADOOP_CLUSTER_NUMPENDING_JOBS = "hadoop.cluster.numpendingjobs";
        public static final String HADOOP_CLUSTER_ALLOCATED_MEMORY = "hadoop.cluster.allocatedmemory";
        public static final String HADOOP_CLUSTER_TOTAL_MEMORY = "hadoop.cluster.totalmemory";
        public static final String HADOOP_CLUSTER_AVAILABLE_MEMORY = "hadoop.cluster.availablememory";
        public static final String HADOOP_CLUSTER_RESERVED_MEMORY = "hadoop.cluster.reservedmemory";
        public static final String HADOOP_CLUSTER_TOTAL_VIRTUAL_CORES = "hadoop.cluster.totalvirtualcores";
        public static final String HADOOP_CLUSTER_ALLOCATED_VIRTUAL_CORES = "hadoop.cluster.allocatedvcores";
        public static final String HADOOP_CLUSTER_RUNNING_CONTAINERS = "hadoop.cluster.runningcontainers";

        // metrics from scheduler info
        public static final String HADOOP_CLUSTER_CAPACITY = "hadoop.cluster.capacity";
        public static final String HADOOP_CLUSTER_USED_CAPACITY = "hadoop.cluster.usedcapacity";

        public static final String HADOOP_QUEUE_NUMPENDING_JOBS = "hadoop.queue.numpendingjobs";
        public static final String HADOOP_QUEUE_USED_CAPACITY = "hadoop.queue.usedcapacity";
        public static final String HADOOP_QUEUE_USED_CAPACITY_RATIO = "hadoop.queue.usedcapacityratio";

        public static final String HADOOP_USER_NUMPENDING_JOBS = "hadoop.user.numpendingjobs";
        public static final String HADOOP_USER_USED_MEMORY = "hadoop.user.usedmemory";
        public static final String HADOOP_USER_USED_MEMORY_RATIO = "hadoop.user.usedmemoryratio";

        // metrics from cluster metrics of fair scheduler
        public static final String HADOOP_CLUSTER_FAIR_NUMACTIVE_APPS = "hadoop.cluster.fair.numActiveApps";
        public static final String HADOOP_CLUSTER_FAIR_PENDING_APPS = "hadoop.cluster.fair.numPendingApps";

        public static final String HADOOP_CLUSTER_FAIR_RESERVED_CONTAINERS = "hadoop.cluster.fair.reservedContainers";
        public static final String HADOOP_CLUSTER_FAIR_ALLOCATED_CONTAINERS = "hadoop.cluster.fair.allocatedContainers";
        public static final String HADOOP_CLUSTER_FAIR_PENDING_CONTAINERS = "hadoop.cluster.fair.pendingContainers";

        public static final String HADOOP_CLUSTER_QUEUE_CLUSTER_RESOURCE_MEM = "hadoop.cluster.fair.resource.mem";
        public static final String HADOOP_CLUSTER_QUEUE_CLUSTER_RESOURCE_VCORE = "hadoop.cluster.fair.resource.vcore";

        public static final String HADOOP_CLUSTER_QUEUE_USED_RESOURCE_MEM = "hadoop.cluster.fair.used.resource.mem";
        public static final String HADOOP_CLUSTER_QUEUE_USED_RESOURCE_VCORE = "hadoop.cluster.fair.used.resource.vcore";

        // metrics from scheduler info of fair scheduler
        public static final String HADOOP_FAIR_QUEUE_MAX_APPS = "hadoop.fair.queue.maxApps";
        public static final String HADOOP_FAIR_QUEUE_NUM_ACTIVE_APPS = "hadoop.fair.queue.numActiveApps";
        public static final String HADOOP_FAIR_QUEUE_NUM_PENDING_APPS = "hadoop.fair.queue.numPendingApps";
        public static final String HADOOP_FAIR_QUEUE_RESERVED_CONTAINERS = "hadoop.fair.queue.reservedContainers";
        public static final String HADOOP_FAIR_QUEUE_ALLOCATED_CONTAINERS = "hadoop.fair.queue.allocatedContainers";
        public static final String HADOOP_FAIR_QUEUE_PENDING_CONTAINERS = "hadoop.fair.queue.pendingContainers";

        public static final String HADOOP_FAIR_QUEUE_MIN_RESOURCE_MEM = "hadoop.fair.queue.minResources.mem";
        public static final String HADOOP_FAIR_QUEUE_MIN_RESOURCE_VCORE = "hadoop.fair.queue.minResources.vcore";

        public static final String HADOOP_FAIR_QUEUE_MAX_RESOURCE_MEM = "hadoop.fair.queue.maxResources.mem";
        public static final String HADOOP_FAIR_QUEUE_MAX_RESOURCE_VCORE = "hadoop.fair.queue.maxResources.vcore";

        public static final String HADOOP_FAIR_QUEUE_USED_RESOURCE_MEM = "hadoop.fair.queue.usedResources.mem";
        public static final String HADOOP_FAIR_QUEUE_USED_ESOURCE_VCORE = "hadoop.fair.queue.usedResources.vcore";

        public static final String HADOOP_FAIR_QUEUE_STEADY_RESOURCE_MEM = "hadoop.fair.queue.steadyFairResources.mem";
        public static final String HADOOP_FAIR_QUEUE_STEADY_RESOURCE_VCORE = "hadoop.fair.queue.steadyFairResources.vcore";

        public static final String HADOOP_FAIR_QUEUE_FAIR_RESOURCE_MEM = "hadoop.fair.queue.fairResources.mem";
        public static final String HADOOP_FAIR_QUEUE_FAIR_RESOURCE_VCORE = "hadoop.fair.queue.fairResources.vcore";

    }

    public static final String RUNNING_CAPACITY_QUEUE_SERVICE_NAME = "RunningCapacityQueueService";
    public static final String RUNNING_FAIR_QUEUE_SERVICE_NAME = "RunningFairQueueService";
    public static final String QUEUE_MAPPING_SERVICE_NAME = "QueueMappingService";
    public static final String RUNNING_APPLICATION_SERVICE_NAME = "RunningApplicationService";

    // tag constants
    public static final String TAG_PARENT_QUEUE = "parentQueue";
    public static final String TAG_QUEUE = "queue";
    public static final String TAG_USER = "user";
    public static final String TAG_SITE = "site";
    public static final String TAG_CLUSTER = "cluster";

    // field constants
    public static final String FIELD_DATASOURCE = "dataSource";
    public static final String FIELD_DATATYPE = "dataType";
    public static final String FIELD_DATA = "data";

}
