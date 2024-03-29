/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.eagle.alert.engine.coordinator.impl;

import org.apache.eagle.alert.coordination.model.AlertBoltSpec;
import org.apache.eagle.alert.coordination.model.PublishSpec;
import org.apache.eagle.alert.coordination.model.RouterSpec;
import org.apache.eagle.alert.coordination.model.SpoutSpec;
import org.apache.eagle.alert.engine.coordinator.IMetadataChangeNotifyService;
import org.apache.eagle.alert.engine.coordinator.MetadataType;
import org.apache.eagle.alert.engine.coordinator.PolicyDefinition;
import org.apache.eagle.alert.engine.coordinator.StreamDefinition;
import org.apache.eagle.alert.engine.publisher.AlertPublishSpecListener;
import org.apache.eagle.alert.engine.router.AlertBoltSpecListener;
import org.apache.eagle.alert.engine.router.SpoutSpecListener;
import org.apache.eagle.alert.engine.router.StreamRouterBoltSpecListener;
import com.google.common.base.Preconditions;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * notify 3 components of metadata change Spout, StreamRouterBolt and AlertBolt.
 */
@SuppressWarnings( {"serial"})
public abstract class AbstractMetadataChangeNotifyService implements IMetadataChangeNotifyService, Closeable, Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractMetadataChangeNotifyService.class);
    private final List<StreamRouterBoltSpecListener> streamRouterBoltSpecListeners = new ArrayList<>();
    private final List<SpoutSpecListener> spoutSpecListeners = new ArrayList<>();
    private final List<AlertBoltSpecListener> alertBoltSpecListeners = new ArrayList<>();
    private final List<AlertPublishSpecListener> alertPublishSpecListeners = new ArrayList<>();
    protected MetadataType type;

    @Override
    public void init(Config config, MetadataType type) {
        this.type = type;
    }

    @Override
    public void registerListener(AlertPublishSpecListener listener) {
        LOG.info("registerListener AlertPublishSpecListener");
        synchronized (alertBoltSpecListeners) {
            Preconditions.checkNotNull(alertPublishSpecListeners, "Not initialized yet");
            LOG.info("Register {}", listener);
            alertPublishSpecListeners.add(listener);
        }
    }

    @Override
    public void registerListener(StreamRouterBoltSpecListener listener) {
        LOG.info("registerListener StreamRouterBoltSpecListener");
        synchronized (streamRouterBoltSpecListeners) {
            streamRouterBoltSpecListeners.add(listener);
        }
    }

    @Override
    public void registerListener(AlertBoltSpecListener listener) {
        LOG.info("registerListener AlertBoltSpecListener");
        synchronized (alertBoltSpecListeners) {
            alertBoltSpecListeners.add(listener);
        }
    }

    @Override
    public void registerListener(SpoutSpecListener listener) {
        LOG.info("registerListener SpoutSpecListener");
        synchronized (spoutSpecListeners) {
            spoutSpecListeners.add(listener);
        }
    }

    protected void notifySpout(SpoutSpec spoutSpec, Map<String, StreamDefinition> sds) {
        LOG.info("notifySpout SDS.size:{}", sds.size());
        spoutSpecListeners.forEach(s -> s.onSpoutSpecChange(spoutSpec, sds));
    }

    protected void notifyStreamRouterBolt(RouterSpec routerSpec, Map<String, StreamDefinition> sds) {
        LOG.info("notifyStreamRouterBolt SDS.size:{}", sds.size());
        streamRouterBoltSpecListeners.forEach(s -> s.onStreamRouteBoltSpecChange(routerSpec, sds));
    }

    protected void notifyAlertBolt(AlertBoltSpec alertBoltSpec, Map<String, StreamDefinition> sds) {
        LOG.info("notifyAlertBolt SDS.size:{}", sds.size());
        alertBoltSpecListeners.forEach(s -> s.onAlertBoltSpecChange(alertBoltSpec, sds));
    }

    protected void notifyAlertPublishBolt(PublishSpec alertPublishSpec, Map<String, StreamDefinition> sds) {
        LOG.info("notifyAlertPublishBolt SDS.size:{}", sds.size());
        alertPublishSpecListeners.forEach(s -> s.onAlertPublishSpecChange(alertPublishSpec, sds));
    }

    protected void notifyAlertPublishBolt(Map<String, PolicyDefinition> pds, Map<String, StreamDefinition> sds) {
        LOG.info("notifyAlertPublishBolt SDS.size:{}", sds.size());
        alertPublishSpecListeners.forEach(s -> s.onAlertPolicyChange(pds, sds));
    }

    public void close() throws IOException {
        LOG.info("Closed");
    }
}