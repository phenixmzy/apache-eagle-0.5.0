package org.apache.eagle.hadoop.queue.model.scheduler.fair;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FairSchedulerInfo {
    private String type;

    private FairChildQueues rootQueue;

    public FairChildQueues getRootQueue() {
        return rootQueue;
    }

    public void setRootQueue(FairChildQueues rootQueue) {
        this.rootQueue = rootQueue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
