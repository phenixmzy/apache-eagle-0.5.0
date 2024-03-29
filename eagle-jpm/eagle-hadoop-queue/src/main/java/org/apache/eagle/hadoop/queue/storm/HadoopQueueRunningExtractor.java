/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.eagle.hadoop.queue.storm;

import org.apache.eagle.hadoop.queue.model.applications.ClusterApplicationMetricsCrawler;
import org.apache.eagle.hadoop.queue.model.scheduler.fair.FairSchedulerInfoCrawler;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.eagle.hadoop.queue.HadoopQueueRunningAppConfig;
import org.apache.eagle.hadoop.queue.crawler.ClusterMetricsCrawler;
import org.apache.eagle.hadoop.queue.crawler.RunningAppsCrawler;
import org.apache.eagle.hadoop.queue.crawler.SchedulerInfoCrawler;
import org.apache.eagle.jpm.util.Constants;
import org.apache.eagle.jpm.util.resourcefetch.ha.HAURLSelector;
import org.apache.eagle.jpm.util.resourcefetch.ha.HAURLSelectorImpl;
import org.apache.eagle.jpm.util.resourcefetch.url.RmActiveTestURLBuilderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class HadoopQueueRunningExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(HadoopQueueRunningExtractor.class);
    private static final int MAX_NUM_THREADS = 10;
    private static final int MAX_WAIT_TIME = 10;

    private String site;
    private String urlBases;

    private HAURLSelector urlSelector;
    private ExecutorService executorService;
    private SpoutOutputCollector collector;

    public HadoopQueueRunningExtractor(HadoopQueueRunningAppConfig eagleConf, SpoutOutputCollector collector) {
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
        futures.add(executorService.submit(new SchedulerInfoCrawler(site, urlSelector.getSelectedUrl(), collector)));

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
