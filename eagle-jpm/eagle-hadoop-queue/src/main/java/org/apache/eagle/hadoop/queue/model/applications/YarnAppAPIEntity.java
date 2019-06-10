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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.eagle.log.base.taggedlog.TaggedLogAPIEntity;
import org.apache.eagle.log.entity.meta.*;

import static org.apache.eagle.jpm.util.Constants.ACCEPTED_APP_SERVICE_NAME;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Table("yarn_app")
@ColumnFamily("f")
@Prefix("accepted")
@Service(ACCEPTED_APP_SERVICE_NAME)
@TimeSeries(true)
@Partition({"site"})
@Tags({"site", "id", "user", "queue"})
public class YarnAppAPIEntity extends TaggedLogAPIEntity {
    @Column("a")
    private String appName;
    @Column("b")
    private String state;
    @Column("c")
    private long startedTime;
    @Column("d")
    private long elapsedTime;
    @Column("e")
    private String trackingUrl;
    @Column("f")
    private double queueUsagePercentage;
    @Column("g")
    private double clusterUsagePercentage;
    @Column("h")
    private String applicationType;
    @Column("i")
    private int allocatedMB;
    @Column("j")
    private int allocatedVCores;
    @Column("k")
    private int runningContainers;
    @Column("l")
    private double progress;
    @Column("m")
    private String queue;
    @Column("n")
    private long runningTimeLenSecond;
    @Column("o")
    private String appId;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
        valueChanged("appId");
    }

    public long getRunningTimeLenSecond() {
        return runningTimeLenSecond;
    }

    public void setRunningTimeLenSecond(long runningTimeLenSecond) {
        this.runningTimeLenSecond = runningTimeLenSecond;
        valueChanged("runningTimeLenSecond");
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
        valueChanged("applicationType");
    }

    public int getAllocatedMB() {
        return allocatedMB;
    }

    public void setAllocatedMB(int allocatedMB) {
        this.allocatedMB = allocatedMB;
        valueChanged("allocatedMB");
    }

    public int getAllocatedVCores() {
        return allocatedVCores;
    }

    public void setAllocatedVCores(int allocatedVCores) {
        this.allocatedVCores = allocatedVCores;
        valueChanged("allocatedVCores");
    }

    public int getRunningContainers() {
        return runningContainers;
    }

    public void setRunningContainers(int runningContainers) {
        this.runningContainers = runningContainers;
        valueChanged("runningContainers");
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
        valueChanged("progress");
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
        valueChanged("queue");
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
        valueChanged("appName");
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
        valueChanged("state");
    }

    public long getStartedTime() {
        return startedTime;
    }

    public void setStartedTime(long startedTime) {
        this.startedTime = startedTime;
        valueChanged("startedTime");
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
        valueChanged("elapsedTime");
    }

    public String getTrackingUrl() {
        return trackingUrl;
    }

    public void setTrackingUrl(String trackingUrl) {
        this.trackingUrl = trackingUrl;
        valueChanged("trackingUrl");
    }

    public double getQueueUsagePercentage() {
        return queueUsagePercentage;
    }

    public void setQueueUsagePercentage(double queueUsagePercentage) {
        this.queueUsagePercentage = queueUsagePercentage;
        valueChanged("queueUsagePercentage");
    }

    public double getClusterUsagePercentage() {
        return clusterUsagePercentage;
    }

    public void setClusterUsagePercentage(double clusterUsagePercentage) {
        this.clusterUsagePercentage = clusterUsagePercentage;
        valueChanged("clusterUsagePercentage");
    }

    public String toString() {
        return String.format("YarnAppAPIEntity[appName=%s, state=%s, startedTime=%s, elapsedTime=%s, trackingUrl=%s, " +
                "queueUsagePercentage=%s, clusterUsagePercentage=%s, applicationType=%s, allocatedMB=%s," +
                " allocatedVCores=%s, runningContainers=%s, progress=%s, queue=%s, runningTimeLenSecond=%s, appId=%s]",
                appName, state, startedTime, elapsedTime, trackingUrl,
                queueUsagePercentage, clusterUsagePercentage, applicationType, allocatedMB,
                allocatedVCores, runningContainers, progress, queue, runningTimeLenSecond, appId);
    }
}
