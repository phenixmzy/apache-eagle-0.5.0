package org.apache.eagle.service.jpm;

import java.util.Map;

public class AppPerformanceResponse {
    private String deElephantUrl;
    public Map<String, String> configs;

    public String getDeElephantUrl() {
        return deElephantUrl;
    }

    public void setDeElephantUrl(String deElephantUrl) {
        this.deElephantUrl = deElephantUrl;
    }

    public Map<String, String> getConfigs() {
        return configs;
    }

    public void setConfigs(Map<String, String> configs) {
        this.configs = configs;
    }
}
