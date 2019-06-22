package org.apache.eagle.alert.engine.publisher.external.rms;

import java.util.concurrent.ThreadPoolExecutor;

public class RMSGeneratorBuilder {
    private RMSGenerator generator;

    private RMSGeneratorBuilder() {
        generator = new RMSGenerator();
    }

    public static RMSGeneratorBuilder newBuilder() {
        return new RMSGeneratorBuilder();
    }

    public RMSGeneratorBuilder withExecutorPool(ThreadPoolExecutor threadPoolExecutor) {
        generator.setExecutorPool(threadPoolExecutor);
        return this;
    }

    public RMSGeneratorBuilder withServerUrl(String serverUrl) {
        generator.setServerURL(serverUrl);
        return this;
    }

    public RMSGeneratorBuilder withKey(String key) {
        generator.setKey(key);
        return this;
    }

    public RMSGeneratorBuilder withErrorCode(String errorCode) {
        generator.setErrorCode(errorCode);
        return this;
    }

    public RMSGeneratorBuilder withPointCode(String pointCode) {
        generator.setPointCode(pointCode);
        return this;
    }

    public RMSGeneratorBuilder withServerIp(String serverIp) {
        generator.setServerIp(serverIp);
        return this;
    }

    public RMSGeneratorBuilder withServerName(String serverName) {
        generator.setServerName(serverName);
        return this;
    }

    public RMSGenerator build() {
        return this.generator;
    }
}
