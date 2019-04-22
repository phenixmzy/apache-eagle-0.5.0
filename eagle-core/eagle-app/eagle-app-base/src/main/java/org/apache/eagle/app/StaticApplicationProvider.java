/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.eagle.app;

import org.apache.eagle.app.spi.AbstractApplicationProvider;

/**
 * Static Web Application Provider.
 */
public abstract class StaticApplicationProvider extends AbstractApplicationProvider<StaticApplication> {
    @Override
    public StaticApplication getApplication() {
        return new StaticApplication(this.getApplicationDesc().getType());
    }

    @Override
    public final Class<StaticApplication> getApplicationClass() {
        return StaticApplication.class;
    }
}