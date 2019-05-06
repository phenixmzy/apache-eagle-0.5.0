/*
 *
 *  * Licensed to the Apache Software Foundation (ASF) under one or more
 *  * contributor license agreements.  See the NOTICE file distributed with
 *  * this work for additional information regarding copyright ownership.
 *  * The ASF licenses this file to You under the Apache License, Version 2.0
 *  * (the "License"); you may not use this file except in compliance with
 *  * the License.  You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.apache.eagle.alert.engine.scheme;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.storm.spout.Scheme;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Expects flat Json scheme.
 */
public class JsonScheme implements Scheme {
    private static final long serialVersionUID = -8352896475656975577L;
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(JsonScheme.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private String topic;

    @SuppressWarnings("rawtypes")
    public JsonScheme(String topic, Map conf) {
        this.topic = topic;
    }

    public static String deserializeString(ByteBuffer buffer) {
        if (buffer.hasArray()) {
            int base = buffer.arrayOffset();
            return new String(buffer.array(), base + buffer.position(), buffer.remaining());
        } else {
            return new String(Utils.toByteArray(buffer), StandardCharsets.UTF_8);
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<Object> deserialize(ByteBuffer ser) {
        try {
            if (ser != null) {
                Map map = mapper.readValue(deserializeString(ser), Map.class);
                return Arrays.asList(topic, map);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Content is null, ignore");
                }
            }
        } catch (IOException e) {
            try {
                LOG.error("Failed to deserialize as JSON: {}", new String(ser.array(), "UTF-8"), e);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
        return null;
    }

    @Override
    public Fields getOutputFields() {
        return new Fields("f1");
    }

    /*
    @SuppressWarnings("rawtypes")
    public List<Object> deserialize(byte[] ser) {
        try {
            if (ser != null) {
                Map map = mapper.readValue(ser, Map.class);
                return Arrays.asList(topic, map);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Content is null, ignore");
                }
            }
        } catch (IOException e) {
            try {
                LOG.error("Failed to deserialize as JSON: {}", new String(ser, "UTF-8"), e);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
        return null;
    }*/
}