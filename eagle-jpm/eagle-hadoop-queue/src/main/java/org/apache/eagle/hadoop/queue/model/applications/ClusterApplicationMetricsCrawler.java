package org.apache.eagle.hadoop.queue.model.applications;

import org.apache.eagle.hadoop.queue.common.HadoopYarnResourceUtils;
import org.apache.eagle.hadoop.queue.common.YarnClusterResourceURLBuilder;
import org.apache.storm.spout.SpoutOutputCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ClusterApplicationMetricsCrawler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ClusterApplicationMetricsCrawler.class);
    private String urlString;
    private ClusterApplicationsMetricsListener listener;

    public ClusterApplicationMetricsCrawler(String site, String urlBase, SpoutOutputCollector collector) {
        this.listener = new ClusterApplicationsMetricsListener(site, collector);
        this.urlString = YarnClusterResourceURLBuilder.buildRunningAppsURL(urlBase);
    }

    @Override
    public void run() {
        try {
            logger.info("Start to crawl cluster applications info from {}", this.urlString);
            AppsWrapper appWrapper = (AppsWrapper) HadoopYarnResourceUtils.getObjectFromUrlStream(urlString, AppsWrapper.class);
            if (appWrapper == null) {
                logger.error("Failed to crawl scheduler info with url = " + this.urlString);
            } else if (appWrapper.getApps().getApp() != null) {
                logger.info("Crawled " + appWrapper.getApps().getApp().size() + " application.");
                long currentTimestamp = System.currentTimeMillis();
                listener.onMetric(appWrapper, currentTimestamp);
            } else {
                logger.info("Yarn was Not Any Running Of Application.");
            }
        } catch (IOException e) {
            logger.error("Got IO exception while connecting to " + this.urlString + " : " + e.getMessage());
        } catch (Exception e) {
            logger.error("Got exception while crawling queues:" + e.getMessage(), e);
        } catch (Throwable e) {
            logger.error("Got throwable exception while crawling queues:" + e.getMessage(), e);
        } finally {
            listener.flush();
        }
    }
}
