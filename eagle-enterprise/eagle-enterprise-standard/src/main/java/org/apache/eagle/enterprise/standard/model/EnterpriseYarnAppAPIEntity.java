package org.apache.eagle.enterprise.standard.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.eagle.log.base.taggedlog.TaggedLogAPIEntity;
import org.apache.eagle.log.entity.meta.*;

import static org.apache.eagle.jpm.util.Constants.ACCEPTED_APP_SERVICE_NAME;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Table("yarn_app_enterprise")
@ColumnFamily("f")
@Prefix("accepted")
@Service(ACCEPTED_APP_SERVICE_NAME)
@TimeSeries(true)
@Partition({"site"})
@Tags({"site", "id", "user", "queue"})
public class EnterpriseYarnAppAPIEntity extends TaggedLogAPIEntity {
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
    @Column("p")
    private String releaseStatus;
    @Column("q")
    private String queueDepartmentName;
    @Column("r")
    private String queueProductLinetName;
    @Column("x")
    private String appDepartmentName;
    @Column("z")
    private String appProductLinetName;

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

    public long getRunningTimeLenSecond() {
        return runningTimeLenSecond;
    }

    public void setRunningTimeLenSecond(long runningTimeLenSecond) {
        this.runningTimeLenSecond = runningTimeLenSecond;
        valueChanged("runningTimeLenSecond");
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
        valueChanged("appId");
    }

    public String getReleaseStatus() {
        return releaseStatus;
    }

    public void setReleaseStatus(String releaseStatus) {
        this.releaseStatus = releaseStatus;
        valueChanged("releaseStatus");
    }

    public String getQueueDepartmentName() {
        return queueDepartmentName;
    }

    public void setQueueDepartmentName(String queueDepartmentName) {
        this.queueDepartmentName = queueDepartmentName;
        valueChanged("queueDepartmentName");
    }

    public String getQueueProductLinetName() {
        return queueProductLinetName;
    }

    public void setQueueProductLinetName(String queueProductLinetName) {
        this.queueProductLinetName = queueProductLinetName;
        valueChanged("queueProductLinetName");
    }

    public String getAppDepartmentName() {
        return appDepartmentName;
    }

    public void setAppDepartmentName(String appDepartmentName) {
        this.appDepartmentName = appDepartmentName;
        valueChanged("appDepartmentName");
    }

    public String getAppProductLinetName() {
        return appProductLinetName;
    }

    public void setAppProductLinetName(String appProductLinetName) {
        this.appProductLinetName = appProductLinetName;
        valueChanged("appProductLinetName");
    }
}
