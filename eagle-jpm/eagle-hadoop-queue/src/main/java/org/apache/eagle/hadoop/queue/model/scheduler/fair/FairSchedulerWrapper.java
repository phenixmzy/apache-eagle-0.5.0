package org.apache.eagle.hadoop.queue.model.scheduler.fair;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FairSchedulerWrapper {
    private FairScheduler scheduler;

    public FairScheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(FairScheduler scheduler) {
        this.scheduler = scheduler;
    }


}
