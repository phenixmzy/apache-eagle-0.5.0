package org.apache.eagle.hadoop.queue.model.scheduler.fair;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FairChildQueues {
    private int maxApps;
    private Resources usedResources;
    private String schedulingPolicy;
    private Resources minResources;
    private Resources maxResources;
    private Resources steadyFairResources;
    private Resources fairResources;
    private Resources clusterResources;
    private String queueName;
    private int pendingContainers;
    private int allocatedContainers;
    private int reservedContainers;
    private boolean preemptable;
    private int numActiveApps;
    private int numPendingApps;
    private List<FairChildQueue> childQueues;

    public int getMaxApps() {
        return maxApps;
    }

    public void setMaxApps(int maxApps) {
        this.maxApps = maxApps;
    }

    public int getNumPendingApps() {
        return numPendingApps;
    }

    public void setNumPendingApps(int numPendingApps) {
        this.numPendingApps = numPendingApps;
    }

    public List<FairChildQueue> getChildQueues() {
        return childQueues;
    }

    public void setChildQueues(List<FairChildQueue> childQueues) {
        this.childQueues = childQueues;
    }

    public Resources getUsedResources() {
        return usedResources;
    }

    public void setUsedResources(Resources usedResources) {
        this.usedResources = usedResources;
    }

    public String getSchedulingPolicy() {
        return schedulingPolicy;
    }

    public void setSchedulingPolicy(String schedulingPolicy) {
        this.schedulingPolicy = schedulingPolicy;
    }

    public Resources getMinResources() {
        return minResources;
    }

    public void setMinResources(Resources minResources) {
        this.minResources = minResources;
    }

    public Resources getMaxResources() {
        return maxResources;
    }

    public void setMaxResources(Resources maxResources) {
        this.maxResources = maxResources;
    }

    public Resources getSteadyFairResources() {
        return steadyFairResources;
    }

    public void setSteadyFairResources(Resources steadyFairResources) {
        this.steadyFairResources = steadyFairResources;
    }

    public Resources getFairResources() {
        return fairResources;
    }

    public void setFairResources(Resources fairResources) {
        this.fairResources = fairResources;
    }

    public Resources getClusterResources() {
        return clusterResources;
    }

    public void setClusterResources(Resources clusterResources) {
        this.clusterResources = clusterResources;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public int getPendingContainers() {
        return pendingContainers;
    }

    public void setPendingContainers(int pendingContainers) {
        this.pendingContainers = pendingContainers;
    }

    public int getAllocatedContainers() {
        return allocatedContainers;
    }

    public void setAllocatedContainers(int allocatedContainers) {
        this.allocatedContainers = allocatedContainers;
    }

    public int getReservedContainers() {
        return reservedContainers;
    }

    public void setReservedContainers(int reservedContainers) {
        this.reservedContainers = reservedContainers;
    }

    public boolean isPreemptable() {
        return preemptable;
    }

    public void setPreemptable(boolean preemptable) {
        this.preemptable = preemptable;
    }

    public int getNumActiveApps() {
        return numActiveApps;
    }

    public void setNumActiveApps(int numActiveApps) {
        this.numActiveApps = numActiveApps;
    }
}
