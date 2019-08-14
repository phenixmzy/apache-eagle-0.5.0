package org.apache.eagle.hadoop.queue.model.scheduler.fair;

import org.apache.eagle.hadoop.queue.HadoopQueueRunningAppConfig;
import org.apache.eagle.hadoop.queue.crawler.ClusterMetricsCrawler;
import org.apache.eagle.hadoop.queue.crawler.RunningAppsCrawler;
import org.apache.eagle.hadoop.queue.crawler.SchedulerInfoCrawler;
import org.apache.eagle.hadoop.queue.model.applications.ClusterApplicationMetricsCrawler;
import org.apache.eagle.jpm.util.Constants;
import org.apache.eagle.jpm.util.resourcefetch.ha.HAURLSelector;
import org.apache.eagle.jpm.util.resourcefetch.ha.HAURLSelectorImpl;
import org.apache.storm.spout.SpoutOutputCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class HadoopFairQueueRunningExtractor {
    private static final Logger LOGGER = LoggerFactory.getLogger(HadoopFairQueueRunningExtractor.class);
    private static final int MAX_NUM_THREADS = 10;
    private static final int MAX_WAIT_TIME = 10;

    private String site;
    private String urlBases;

    private HAURLSelector urlSelector;
    private ExecutorService executorService;
    private SpoutOutputCollector collector;

    public HadoopFairQueueRunningExtractor(HadoopQueueRunningAppConfig eagleConf, SpoutOutputCollector collector) {
        site = eagleConf.eagleProps.site;
        urlBases = eagleConf.dataSourceConfig.rMEndPoints;
        if (urlBases == null) {
            throw new IllegalArgumentException(site + ".baseUrl is null");
        }
        String[] urls = urlBases.split(",");
        urlSelector = new HAURLSelectorImpl(urls, Constants.CompressionType.NONE);
        executorService = Executors.newFixedThreadPool(MAX_NUM_THREADS);
        this.collector = collector;
    }

    public void crawl() {
        try {
            urlSelector.checkUrl();
        } catch (IOException e) {
            LOGGER.error("{}", e.getMessage(), e);
        }

        List<Future<?>> futures = new ArrayList<>();
        futures.add(executorService.submit(new ClusterMetricsCrawler(site, urlSelector.getSelectedUrl(), collector)));
        futures.add(executorService.submit(new ClusterApplicationMetricsCrawler(site, urlSelector.getSelectedUrl(), collector)));
        futures.add(executorService.submit(new FairSchedulerInfoCrawler(site, urlSelector.getSelectedUrl(), collector)));

        futures.forEach(future -> {
            try {
                future.get(MAX_WAIT_TIME * 1000, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                LOGGER.error("Caught an overtime exception with message" + e.getMessage());
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("{}", e.getMessage(), e);
            }
        });
    }
}
