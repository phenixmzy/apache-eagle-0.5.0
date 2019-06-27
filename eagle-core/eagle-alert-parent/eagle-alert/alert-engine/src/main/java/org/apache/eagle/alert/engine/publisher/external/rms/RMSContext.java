package org.apache.eagle.alert.engine.publisher.external.rms;

import org.apache.eagle.alert.engine.coordinator.AlertSeverity;

public class RMSContext {
    //Come Form eagle.conf
    private String key;
    private String pointCode;
    private String errorCode;

    private String serverIp;
    private String serverName;
    private String rmsServerUrl;

    //Alert Event Value
    private String noticeTime;
    private String content;

    //Come Form Alert Policies Set
    private int level;

    private String info;

    public RMSContext() {
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPointCode() {
        return pointCode;
    }

    public void setPointCode(String pointCode) {
        this.pointCode = pointCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getNoticeTime() {
        return noticeTime;
    }

    public void setNoticeTime(String noticeTime) {
        this.noticeTime = noticeTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getRmsServerUrl() {
        return this.rmsServerUrl;
    }

    public void setRmsServerUrl(String rmsServerUrl) {
        this.rmsServerUrl = rmsServerUrl;
    }

    public static int getLevelByAlertSeverity(AlertSeverity severity) {
        return severity.ordinal();
    }

    public String toString() {
        return String.format("RMSContext[key:%s, pointCode:%s, serverIp:%s, serverName:%s, noticeTime:%s,content:%s, level:%s, rmsServerUrl:%s ]",
                this.key, this.pointCode, this.serverIp, this.serverName, this.noticeTime, this.content, this.level, this.rmsServerUrl);
    }
}
