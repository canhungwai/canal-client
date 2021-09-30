/*
 * Copyright (c) 2015-2021, Harvey Chan. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.harveychan.canal.client.client;

import cn.harveychan.canal.client.handler.MessageHandler;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * canal 操作客户端通用封装
 *
 * @author canhungwai
 * @since 2021-09-15
 */
@Slf4j
public abstract class AbstractCanalClient implements CanalClient {

    protected volatile boolean flag;

    /**
     * 处理数据工作线程
     */
    private Thread workThread;

    @Getter
    @Setter
    private CanalConnector connector;

    /**
     * 记录订阅规则
     */
    protected String filter = StringUtils.EMPTY;

    /**
     * 每次获取记录的数量
     */
    protected Integer batchSize = 1;

    /**
     * 超时时间
     */
    protected Long timeout = 1L;

    /**
     * 超时单位
     */
    protected TimeUnit unit = TimeUnit.SECONDS;

    /**
     * 消息处理器
     */
    @Getter
    @Setter
    private MessageHandler messageHandler;

    /**
     * 开启数据操作
     */
    @Override
    public void start() {
        log.info("start canal client");
        workThread = new Thread(this::process);
        workThread.setName("canal-client-thread");
        flag = true;
        workThread.start();
    }

    /**
     * 停止数据操作
     */
    @Override
    public void stop() {
        log.info("stop canal client");
        flag = false;
        if (workThread != null) {
            workThread.interrupt();
        }
    }

    /**
     * 处理数据
     */
    @Override
    public void process() {
        while (flag) {
            try {
                connector.connect(); // 开启连接
                connector.subscribe(filter);
                while (flag) {
                    Message message = connector.getWithoutAck(batchSize, timeout, unit);
                    log.info("canal client get message: {}", message);
                    long batchId = message.getId();
                    if (message.getId() != -1 && message.getEntries().size() != 0) {
                        messageHandler.handleMessage(message);
                    }
                    connector.ack(batchId);
                }
            } catch (Exception e) {
                log.error("canal client exception occurred: {}", e.getMessage(), e);
            } finally {
                connector.disconnect(); // 关闭连接
            }
        }
    }
}
