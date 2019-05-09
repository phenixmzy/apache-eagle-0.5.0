package org.apache.eagle.alert.engine.publisher.external.openfalcon;

import com.typesafe.config.Config;
import org.apache.eagle.alert.engine.coordinator.Publishment;
import org.apache.eagle.alert.engine.coordinator.PublishmentType;
import org.apache.eagle.alert.engine.model.AlertStreamEvent;
import org.apache.eagle.alert.engine.publisher.AlertPublishPluginProvider;
import org.apache.eagle.alert.engine.publisher.impl.AbstractPublishPlugin;
import org.apache.eagle.alert.engine.publisher.impl.PublishStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AlertOpenFalconPublisher extends AbstractPublishPlugin implements AlertPublishPluginProvider {
    private static final Logger LOG = LoggerFactory.getLogger(AlertOpenFalconPublisher.class);
    private static final int DEFAULT_THREAD_POOL_CORE_SIZE = 4;
    private static final int DEFAULT_THREAD_POOL_MAX_SIZE = 8;
    private static final long DEFAULT_THREAD_POOL_SHRINK_TIME = 60000L; // 1 minute
    private transient ThreadPoolExecutor executorPool;
    private String serverUrl;
    private String endpoint;
    private OpenFalconGenerator generator;

    @Override
    public void init(Config config, Publishment publishment, Map conf) throws Exception {
        super.init(config, publishment, conf);
        executorPool = new ThreadPoolExecutor(DEFAULT_THREAD_POOL_CORE_SIZE, DEFAULT_THREAD_POOL_MAX_SIZE, DEFAULT_THREAD_POOL_SHRINK_TIME, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        this.serverUrl = config.hasPath(OpenFalconContant.OPEN_FALCON_SERVER_URL)
                ? config.getString(OpenFalconContant.OPEN_FALCON_SERVER_URL) : "localhost";
        this.endpoint = config.hasPath(OpenFalconContant.OPEN_FALCON_ENDPOINT)
                ? config.getString(OpenFalconContant.OPEN_FALCON_ENDPOINT) : "localhost";
        LOG.info("Creating Open-Falcon Generator... ");
        if (publishment.getProperties() != null) {
            this.generator = createOpenFalconGenerator();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AlertOpenFalconPublisher)) {
            return false;
        }
        return true;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    public void close() {
        this.executorPool.shutdown();
    }

    @Override
    public void onAlert(AlertStreamEvent event) throws Exception {
        if (generator == null) {
            LOG.warn("OpenFalconGenerator is null due to the incorrect configurations");
            return;
        }
        List<AlertStreamEvent> outputEvents = dedup(event);
        if (outputEvents == null) {
            return;
        }
        boolean isSuccess = true;
        for (AlertStreamEvent outputEvent : outputEvents) {
            if (!generator.sendAlertOpenFalcon(outputEvent)) {
                isSuccess = false;
            }
        }
        PublishStatus status = new PublishStatus();
        if (!isSuccess) {
            status.errorMessage = "Failed to send Open-Falcon";
            status.successful = false;
        } else {
            status.errorMessage = "";
            status.successful = true;
        }
        this.status = status;
    }

    @Override
    public PublishmentType getPluginType() {
        return new PublishmentType.Builder()
                .name("Open-Falcon")
                .type(AlertOpenFalconPublisher.class)
                .description("Open-Falcon alert publisher")
                .build();
    }

    private OpenFalconGenerator createOpenFalconGenerator() {
        OpenFalconGenerator generator = OpenFalconGeneratorBuilder.newBuilder()
                .withServerUrl(this.serverUrl)
                .withEndpoint(this.endpoint)
                .withExecutorPool(executorPool).build();
        return generator;
    }

}
