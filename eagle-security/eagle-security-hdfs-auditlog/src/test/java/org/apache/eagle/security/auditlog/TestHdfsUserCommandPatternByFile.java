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

package org.apache.eagle.security.auditlog;

import org.apache.eagle.security.entity.HdfsUserCommandPatternEntity;
import org.junit.Test;

import java.util.List;

/**
 * test pattern download and parse
 */
public class TestHdfsUserCommandPatternByFile {
    @Test
    // not qualified for unit test as it connects to local service
    public void testPatternDownload() throws Exception{
        HdfsUserCommandPatternByFileImpl impl = new HdfsUserCommandPatternByFileImpl();
        List<HdfsUserCommandPatternEntity> list = impl.findAllPatterns();
        for(HdfsUserCommandPatternEntity entity : list){
            System.out.println(entity.getPattern());
            System.out.println(entity.getFieldSelector());
            System.out.println(entity.getFieldModifier());
        }
    }
}
