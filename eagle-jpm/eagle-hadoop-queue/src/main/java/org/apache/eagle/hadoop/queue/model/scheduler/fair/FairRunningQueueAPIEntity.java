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

package org.apache.eagle.hadoop.queue.model.scheduler.fair;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.eagle.hadoop.queue.common.HadoopClusterConstants;
import org.apache.eagle.hadoop.queue.model.scheduler.UserWrappers;
import org.apache.eagle.log.base.taggedlog.TaggedLogAPIEntity;
import org.apache.eagle.log.entity.meta.*;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Table("running_fair_queue")
@ColumnFamily("f")
@Prefix("rqueue")
@Service(HadoopClusterConstants.RUNNING_FAIR_QUEUE_SERVICE_NAME)
@TimeSeries(true)
@Partition( {"site"})
public class FairRunningQueueAPIEntity extends TaggedLogAPIEntity {
    @Column("a")
    private String state;
    @Column("b")
    private int pendingContainers;
    @Column("c")
    private int allocatedContainers;
    @Column("d")
    private int reservedContainers;
    @Column("e")
    private long usedMemory;
    @Column("f")
    private long usedVcores;
    @Column("g")
    private long minMemory;
    @Column("h")
    private long minVcores;
    @Column("i")
    private long maxMemory;
    @Column("j")
    private long maxVcores;
    @Column("k")
    private long clusterMemory;
    @Column("l")
    private long clusterVcores;
    @Column("m")
    private long steadyFairMemory;
    @Column("n")
    private long steadyFairVcores;
    @Column("o")
    private int numActiveApps;
    @Column("p")
    private int numPendingApps;
    @Column("q")
    private int maxApps;
    @Column("r")
    private String scheduler;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
        valueChanged("state");
    }

    public int getPendingContainers() {
        return pendingContainers;
    }

    public void setPendingContainers(int pendingContainers) {
        this.pendingContainers = pendingContainers;
        valueChanged("pendingContainers");
    }

    public int getAllocatedContainers() {
        return allocatedContainers;
    }

    public void setAllocatedContainers(int allocatedContainers) {
        this.allocatedContainers = allocatedContainers;
        valueChanged("allocatedContainers");
    }

    public int getReservedContainers() {
        return reservedContainers;
    }

    public void setReservedContainers(int reservedContainers) {
        this.reservedContainers = reservedContainers;
        valueChanged("reservedContainers");
    }


    public long getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(long usedMemory) {
        this.usedMemory = usedMemory;
        valueChanged("usedMemory");
    }

    public long getUsedVcores() {
        return usedVcores;
    }

    public void setUsedVcores(long usedVcores) {
        this.usedVcores = usedVcores;
        valueChanged("usedVcores");
    }

    public long getMinMemory() {
        return minMemory;
    }

    public void setMinMemory(long minMemory) {
        this.minMemory = minMemory;
        valueChanged("minMemory");
    }

    public long getMinVcores() {
        return minVcores;
    }

    public void setMinVcores(long minVcores) {
        this.minVcores = minVcores;
        valueChanged("minVcores");
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(long maxMemory) {
        this.maxMemory = maxMemory;
        valueChanged("maxMemory");
    }

    public long getMaxVcores() {
        return maxVcores;
    }

    public void setMaxVcores(long maxVcores) {
        this.maxVcores = maxVcores;
        valueChanged("maxVcores");
    }

    public long getClusterMemory() {
        return clusterMemory;
    }

    public void setClusterMemory(long clusterMemory) {
        this.clusterMemory = clusterMemory;
        valueChanged("clusterMemory");
    }

    public long getClusterVcores() {
        return clusterVcores;
    }

    public void setClusterVcores(long clusterVcores) {
        this.clusterVcores = clusterVcores;
        valueChanged("clusterVcores");
    }

    public long getSteadyFairMemory() {
        return steadyFairMemory;
    }

    public void setSteadyFairMemory(long steadyFairMemory) {
        this.steadyFairMemory = steadyFairMemory;
        valueChanged("steadyFairMemory");
    }

    public long getSteadyFairVcores() {
        return steadyFairVcores;
    }

    public void setSteadyFairVcores(long steadyFairVcores) {
        this.steadyFairVcores = steadyFairVcores;
        valueChanged("steadyFairVcores");
    }

    public int getNumActiveApps() {
        return numActiveApps;
    }

    public void setNumActiveApps(int numActiveApps) {
        this.numActiveApps = numActiveApps;
        valueChanged("numActiveApps");
    }

    public int getNumPendingApps() {
        return numPendingApps;
    }

    public void setNumPendingApps(int numPendingApps) {
        this.numPendingApps = numPendingApps;
        valueChanged("numPendingApps");
    }

    public int getMaxApps() {
        return maxApps;
    }

    public void setMaxApps(int maxApps) {
        this.maxApps = maxApps;
        valueChanged("maxApps");
    }

    public String getScheduler() {
        return scheduler;
    }

    public void setScheduler(String scheduler) {
        this.scheduler = scheduler;
        valueChanged("scheduler");
    }
}
