package org.apache.eagle.alert.engine.publisher.external.rms;

import org.apache.eagle.alert.engine.model.AlertStreamEvent;
import org.apache.eagle.common.DateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

public class RMSGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(RMSGenerator.class);

    private static final long MAX_TIMEOUT_MS = 60000;

    private ThreadPoolExecutor executorPool;
    private String key;
    private String pointCode;
    private String errorCode;
    private String serverIp;
    private String serverName;
    private String serverURL;

    public boolean sendAlertRMS(AlertStreamEvent event) {

        if (this.executorPool == null) {
            throw new IllegalStateException("Invoking thread executor pool but it's is not set yet");
        }
        RMSContext context = buildAlertContext(event);
        RMSSender sender = new RMSSender(context);
        Future<?> future = this.executorPool.submit(sender);
        Boolean status;
        try {
            future.get(MAX_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            status = true;
            LOG.info(String.format("Successfully send msg to RMS Server %s", context.getRmsServerUrl()));
        } catch (InterruptedException | ExecutionException e) {
            status = false;
            LOG.error(String.format("Failed to send alertInfo %s to RMS %s, due to %s", context.toString(), this.serverURL, e), e);
        } catch (TimeoutException e) {
            status = false;
            LOG.error(String.format("Failed to send alertInfo %s to RMS %s, due to timeout exception, max timeout: %s ms ", context.toString(), this.serverURL, MAX_TIMEOUT_MS), e);
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

    public void setKey(String key) {
        this.key = key;
    }

    public void setPointCode(String pointCode) {
        this.pointCode = pointCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    private RMSContext buildAlertContext(AlertStreamEvent event) {
        int level = RMSContext.getLevelByAlertSeverity(event.getSeverity());
        String info = String.format("[%s-P%s]%s[%s] in %s.", event.getSeverity(), level, event.getSubject(), event.getSiteId(),event.getBody());
        RMSContext context = new RMSContext();
        context.setKey(this.key);
        context.setErrorCode(this.errorCode);
        context.setPointCode(this.pointCode);
        context.setServerIp(this.serverIp);
        context.setServerName(this.serverName);
        context.setRmsServerUrl(this.serverURL);
        context.setNoticeTime(DateTimeUtil.secondsToHumanDate(event.getTimestamp() / 1000));
        context.setContent(event.getDataMap().toString());
        context.setInfo(info);
        context.setLevel(level);
        return context;
    }
}
