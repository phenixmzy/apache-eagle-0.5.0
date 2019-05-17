package org.apache.eagle.hadoop.queue.model.scheduler.fair;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FairScheduler {
    public FairSchedulerInfo getSchedulerInfo() {
        return schedulerInfo;
    }
    public void setSchedulerInfo(FairSchedulerInfo schedulerInfo) {
        this.schedulerInfo = schedulerInfo;
    }
    private FairSchedulerInfo schedulerInfo;
}
