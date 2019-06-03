/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.eagle.hadoop.queue.model.applications;

import java.util.HashMap;
import java.util.Map;

public class AppStreamInfo {
    public static final String SITE = "site";
    public static final String ID = "id";
    public static final String USER = "user";
    public static final String QUEUE = "queue";
    private static final String NAME = "appName";
    private static final String STATE = "state";
    private static final String STARTEDTIME = "startTime";
    private static final String ELAPSEDTIME = "elapsedTime";
    private static final String QUEUE_USAGE_PERCENTAGE = "queueUsagePercentage";
    private static final String CLUSTER_USAGE_PERCENTAGE = "clusterUsagePercentage";
    private static final String TRACKING_URL = "trackingUrl";
    private static final String PROGRESS = "progress";
    private static final String APPLICATION_TYPE = "applicationType";
    private static final String ALLOCATED_MB = "allocatedMB";
    private static final String ALLOCATED_VCORES = "allocatedVCores";
    private static final String RUNNING_CONTAINERS = "runningContainers";

    public static Map<String, Object> convertAppToStream(YarnAppAPIEntity appAPIEntity) {
        Map<String, Object> queueStreamInfo = new HashMap<>();
        queueStreamInfo.put(SITE, appAPIEntity.getTags().get(SITE));
        queueStreamInfo.put(ID, appAPIEntity.getTags().get(ID));
        queueStreamInfo.put(USER, appAPIEntity.getTags().get(USER));
        queueStreamInfo.put(QUEUE, appAPIEntity.getTags().get(QUEUE));
        queueStreamInfo.put(NAME, appAPIEntity.getAppName());
        queueStreamInfo.put(STATE, appAPIEntity.getState());
        queueStreamInfo.put(ELAPSEDTIME, appAPIEntity.getElapsedTime());
        queueStreamInfo.put(STARTEDTIME, appAPIEntity.getStartedTime());
        queueStreamInfo.put(QUEUE_USAGE_PERCENTAGE, appAPIEntity.getQueueUsagePercentage());
        queueStreamInfo.put(CLUSTER_USAGE_PERCENTAGE, appAPIEntity.getClusterUsagePercentage());
        queueStreamInfo.put(TRACKING_URL, appAPIEntity.getTrackingUrl());
        queueStreamInfo.put(PROGRESS, appAPIEntity.getProgress());
        queueStreamInfo.put(APPLICATION_TYPE, appAPIEntity.getApplicationType());
        queueStreamInfo.put(ALLOCATED_MB, appAPIEntity.getAllocatedMB());
        queueStreamInfo.put(ALLOCATED_VCORES, appAPIEntity.getAllocatedVCores());
        queueStreamInfo.put(RUNNING_CONTAINERS, appAPIEntity.getRunningContainers());

        return queueStreamInfo;
    }

}
