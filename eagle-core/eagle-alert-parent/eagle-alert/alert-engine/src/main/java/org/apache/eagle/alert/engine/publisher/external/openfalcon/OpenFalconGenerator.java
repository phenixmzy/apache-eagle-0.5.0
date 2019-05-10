package org.apache.eagle.alert.engine.publisher.external.openfalcon;

import org.apache.eagle.alert.engine.model.AlertStreamEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

public class OpenFalconGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(OpenFalconGenerator.class);

    private static final long MAX_TIMEOUT_MS = 60000;

    private ThreadPoolExecutor executorPool;

    private String serverURL;

    private String endpoint;

    public boolean sendAlertOpenFalcon(AlertStreamEvent event) {

        if (this.executorPool == null) {
            throw new IllegalStateException("Invoking thread executor pool but it's is not set yet");
        }
        OpenFalconContext context = buildAlertContext(event);
        OpenFalconSender sender = new OpenFalconSender(context);
        Future<?> future = this.executorPool.submit(sender);
        Boolean status;
        try {
            future.get(MAX_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            status = true;
            //LOG.info(String.format("Successfully send email to %s", recipients));
        } catch (InterruptedException | ExecutionException e) {
            status = false;
            LOG.error(String.format("Failed to send alertInfo to %s, due to:%s", this.serverURL, e), e);
        } catch (TimeoutException e) {
            status = false;
            LOG.error(String.format("Failed to send alertInfo to %s due to timeout exception, max timeout: %s ms ", this.serverURL, MAX_TIMEOUT_MS), e);
        }
        return status;
    }

    private String getAlertBody(AlertStreamEvent event) {
        if (event.getBody() == null) {
            return String.format("Alert policy \"%s\" was triggered: %s", event.getPolicyId(), generateAlertDataDesc(event));
        } else {
            return event.getBody();
        }
    }

    private String generateAlertDataDesc(AlertStreamEvent event) {
        if (event.getDataMap() == null) {
            return "N/A";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : event.getDataMap().entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append(" ");
        }
        return sb.toString();
    }

    public void setExecutorPool(ThreadPoolExecutor executorPool) {
        this.executorPool = executorPool;
    }

    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    private OpenFalconContext buildAlertContext(AlertStreamEvent event) {
        LOG.info("siteId:{}, subject:{}, timestamp:{}, data:{}, dataMap:{}, endpoint:{}, serverUrl:{}",
                event.getSiteId(), event.getSubject(), event.getTimestamp(),
                event.getData().toString(), event.getDataMap().toString(),
                this.endpoint,this.serverURL
        );
        OpenFalconContext context = new OpenFalconContext();
        String metric = event.getSiteId() + "-" + event.getSubject();
        context.setOpenFalconTag(event.getDataMap().toString());
        context.setOpenFalconMetric(metric);
        context.setOpenFalconEndpoint(this.endpoint);
        context.setOpenFalconTimestamp(event.getTimestamp());
        context.setOpenFalconServerUrl(this.serverURL);
        context.setOpenFalconStep(OpenFalconContant.OPEN_FALCON_STEP_DEFAULT_VALUE);
        context.setOpenFalconValue(OpenFalconContant.OPEN_FALCON_VALUE_DEFAULT_VALUE);
        context.setOpenFalconCounterType(OpenFalconContant.OPEN_FALCON_COUNT_TYPE_DEFAULT_VALUE);
        LOG.info("buildAlertContext AlertStreamEvent{}",event.toString());
        LOG.info("buildAlertContext OpenFalconContext{}", context.toString());
        return context;
    }
}
