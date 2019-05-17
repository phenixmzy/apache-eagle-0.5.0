package org.apache.eagle.hadoop.queue.model.scheduler.fair;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Resources {
    private long memory;
    private long vCores;

    public long getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public long getvCores() {
        return vCores;
    }

    public void setvCores(long vCores) {
        this.vCores = vCores;
    }
}
