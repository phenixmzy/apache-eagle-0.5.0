package org.apache.eagle.alert.engine.publisher.external.openfalcon;

public class OpenFalconContext {
    //subject
    private String openFalconMetric;
    private String openFalconEndpoint;
    private long openFalconTimestamp;
    private int openFalconStep;
    private int openFalconValue;
    private String openFalconTag;
    private String openFalconServerUrl;

    public String getOpenFalconCounterType() {
        return openFalconCounterType;
    }

    public void setOpenFalconCounterType(String openFalconCounterType) {
        this.openFalconCounterType = openFalconCounterType;
    }

    private String openFalconCounterType;

    public String getOpenFalconMetric() {
        return openFalconMetric;
    }

    public void setOpenFalconMetric(String openFalconMetric) {
        this.openFalconMetric = openFalconMetric;
    }

    public String getOpenFalconEndpoint() {
        return openFalconEndpoint;
    }

    public void setOpenFalconEndpoint(String openFalconEndpoint) {
        this.openFalconEndpoint = openFalconEndpoint;
    }

    public long getOpenFalconTimestamp() {
        return openFalconTimestamp;
    }

    public void setOpenFalconTimestamp(long openFalconTimestamp) {
        this.openFalconTimestamp = openFalconTimestamp;
    }

    public int getOpenFalconStep() {
        return openFalconStep;
    }

    public void setOpenFalconStep(int openFalconStep) {
        this.openFalconStep = openFalconStep;
    }

    public int getOpenFalconValue() {
        return openFalconValue;
    }

    public void setOpenFalconValue(int openFalconValue) {
        this.openFalconValue = openFalconValue;
    }

    public String getOpenFalconTag() {
        return openFalconTag;
    }

    public void setOpenFalconTag(String openFalconTag) {
        this.openFalconTag = openFalconTag;
    }

    public String getOpenFalconServerUrl() {
        return openFalconServerUrl;
    }

    public void setOpenFalconServerUrl(String openFalconServerUrl) {
        this.openFalconServerUrl = openFalconServerUrl;
    }

    public String toString() {
        return String.format("OpenFalconContext[metric:{}, endpoint:{}, timestamp:{}, step:{}, value:{}, tag:{},serverUrl:{} ]",
                this.openFalconMetric, this.openFalconEndpoint, this.openFalconTimestamp,
                this.openFalconStep, this.openFalconValue, this.openFalconTag, this.openFalconServerUrl);
    }
}
