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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.eagle.hadoop.queue.model.scheduler.fair;

import com.codahale.metrics.health.HealthCheck;
import com.typesafe.config.Config;
import org.apache.eagle.app.spi.AbstractApplicationProvider;
import org.apache.eagle.hadoop.queue.HadoopQueueRunningApp;
import org.apache.eagle.hadoop.queue.HadoopQueueRunningApplicationHealthCheck;

import java.util.Optional;

public class HadoopFairQueueRunningAppProvider extends AbstractApplicationProvider<HadoopFairQueueRunningApp> {
    public HadoopFairQueueRunningApp getApplication() {
        return new HadoopFairQueueRunningApp();
    }

    @Override
    public Optional<HealthCheck> getManagedHealthCheck(Config config) {
        return Optional.of(new HadoopQueueRunningApplicationHealthCheck(config));
    }
}
