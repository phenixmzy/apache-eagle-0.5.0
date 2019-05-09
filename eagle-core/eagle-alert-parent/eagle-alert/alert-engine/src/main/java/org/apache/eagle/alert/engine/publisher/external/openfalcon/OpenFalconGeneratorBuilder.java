package org.apache.eagle.alert.engine.publisher.external.openfalcon;

import java.util.concurrent.ThreadPoolExecutor;

public class OpenFalconGeneratorBuilder {
    private OpenFalconGenerator generator;

    private OpenFalconGeneratorBuilder() {
        generator = new OpenFalconGenerator();
    }

    public static OpenFalconGeneratorBuilder newBuilder() {
        return new OpenFalconGeneratorBuilder();
    }

    public OpenFalconGeneratorBuilder withExecutorPool(ThreadPoolExecutor threadPoolExecutor) {
        generator.setExecutorPool(threadPoolExecutor);
        return this;
    }

    public OpenFalconGeneratorBuilder withServerUrl(String serverUrl) {
        generator.setServerURL(serverUrl);
        return this;
    }

    public OpenFalconGeneratorBuilder withEndpoint(String endpoint) {
        generator.setEndpoint(endpoint);
        return this;
    }

    public OpenFalconGenerator build() {
        return this.generator;
    }
}
