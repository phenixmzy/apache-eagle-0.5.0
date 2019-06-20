package org.apache.eagle.alert.engine.publisher.external.rms;

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

public class AlertRMSPublisher extends AbstractPublishPlugin implements AlertPublishPluginProvider {
    private static final Logger LOG = LoggerFactory.getLogger(AlertRMSPublisher.class);
    private static final int DEFAULT_THREAD_POOL_CORE_SIZE = 4;
    private static final int DEFAULT_THREAD_POOL_MAX_SIZE = 8;
    private static final long DEFAULT_THREAD_POOL_SHRINK_TIME = 60000L; // 1 minute

    private transient ThreadPoolExecutor executorPool;
    private String token;
    private String pointCode;
    private String errorCode;
    private String serverIp;
    private String serverName;
    private String rmsServerUrl;

    private RMSGenerator generator;

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    public void close() {
        this.executorPool.shutdown();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AlertRMSPublisher)) {
            return false;
        }
        return true;
    }

    @Override
    public void onAlert(AlertStreamEvent event) throws Exception {
        if (generator == null) {
            LOG.warn("RMSGenerator is null due to the incorrect configurations");
            return;
        }

        List<AlertStreamEvent> outputEvents = dedup(event);
        if (outputEvents == null) {
            return;
        }
        boolean isSuccess = true;
        for (AlertStreamEvent outputEvent : outputEvents) {
            if (!generator.sendAlertRMS(outputEvent)) {
                isSuccess = false;
            }
        }

        PublishStatus status = new PublishStatus();
        if (!isSuccess) {
            status.errorMessage = "Failed to send RMS";
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
                .name("RMS")
                .type(AlertRMSPublisher.class)
                .description("RMS alert publisher")
                .build();
    }

    @Override
    public void init(Config config, Publishment publishment, Map conf) throws Exception {
        super.init(config, publishment, conf);

        Config rmsConfig;
        if (config.hasPath(RMSContant.RMS_CONFIG_ROOT)) {
            rmsConfig = config.getConfig(RMSContant.RMS_CONFIG_ROOT);
        } else {
            throw new Exception("must be config application.rms.* in eagle.config");
        }

        executorPool = new ThreadPoolExecutor(DEFAULT_THREAD_POOL_CORE_SIZE, DEFAULT_THREAD_POOL_MAX_SIZE, DEFAULT_THREAD_POOL_SHRINK_TIME, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        this.rmsServerUrl = rmsConfig.hasPath(RMSContant.RMS_CONFIG_SERVER_URL)
                ? rmsConfig.getString(RMSContant.RMS_CONFIG_SERVER_URL) : "localhost";

        this.token = rmsConfig.hasPath(RMSContant.RMS_CONFIG_TOKEN)
                ? rmsConfig.getString(RMSContant.RMS_CONFIG_SERVER_URL) : "no token";

        this.errorCode = rmsConfig.hasPath(RMSContant.RMS_CONFIG_ERROR_CODE)
                ? rmsConfig.getString(RMSContant.RMS_CONFIG_ERROR_CODE) : "no errorCode";

        this.pointCode = rmsConfig.hasPath(RMSContant.RMS_CONFIG_PRINT_CODE)
                ? rmsConfig.getString(RMSContant.RMS_CONFIG_PRINT_CODE) : "no pointCode";

        this.serverIp = rmsConfig.hasPath(RMSContant.RMS_CONFIG_SERVER_IP)
                ? rmsConfig.getString(RMSContant.RMS_CONFIG_SERVER_IP) : "no serverIp";

        this.serverIp = rmsConfig.hasPath(RMSContant.RMS_CONFIG_SERVER_IP)
                ? rmsConfig.getString(RMSContant.RMS_CONFIG_SERVER_IP) : "no serverIp";

        this.serverName = rmsConfig.hasPath(RMSContant.RMS_CONFIG_SERVER_NAME)
                ? rmsConfig.getString(RMSContant.RMS_CONFIG_SERVER_NAME) : "no serverName";

        if (publishment.getProperties() != null) {
            this.generator = createRMSGenerator();
        }
    }

    private RMSGenerator createRMSGenerator() {
        RMSGenerator generator = RMSGeneratorBuilder.newBuilder()
                .withServerUrl(this.rmsServerUrl)
                .withToken(this.token)
                .withPointCode(this.pointCode)
                .withErrorCode(this.errorCode)
                .withServerIp(this.serverIp)
                .withServerName(this.serverName)
                .withExecutorPool(executorPool).build();
        return generator;
    }
}