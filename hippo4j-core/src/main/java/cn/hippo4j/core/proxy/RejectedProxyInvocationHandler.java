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

package cn.hippo4j.core.proxy;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.core.executor.ThreadPoolNotifyAlarmHandler;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Rejected proxy invocation handler.
 */
@AllArgsConstructor
public class RejectedProxyInvocationHandler implements InvocationHandler {

    private final Object target;

    private final String threadPoolId;

    private final AtomicLong rejectCount;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        rejectCount.incrementAndGet();
        if (ApplicationContextHolder.getInstance() != null) {
            ThreadPoolNotifyAlarmHandler alarmHandler = ApplicationContextHolder.getBean(ThreadPoolNotifyAlarmHandler.class);
            alarmHandler.checkPoolRejectedAlarm(threadPoolId);
        }
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }
}
