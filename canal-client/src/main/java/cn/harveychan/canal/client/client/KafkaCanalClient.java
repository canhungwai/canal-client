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
import com.alibaba.otter.canal.client.kafka.KafkaCanalConnector;
import com.alibaba.otter.canal.protocol.FlatMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * kafka 模式客户端
 *
 * @author canhungwai
 * @since 2021-09-15
 */
@Slf4j
public class KafkaCanalClient extends AbstractCanalClient {

    public static Builder builder() {
        return Builder.builder();
    }

    /**
     * 处理数据
     */
    @Override
    public void process() {
        KafkaCanalConnector connector = (KafkaCanalConnector) getConnector();
        MessageHandler messageHandler = getMessageHandler();
        while (flag) {
            try {
                connector.connect();
                connector.subscribe(filter);
                while (flag) {
                    try {
                        List<FlatMessage> messages = connector.getFlatListWithoutAck(timeout, unit);
                        log.info("canal client get message: {}", messages);
                        if (messages != null && messages.size() > 0) {
                            for (FlatMessage flatMessage : messages) {
                                messageHandler.handleMessage(flatMessage);
                            }
                        }
                        connector.ack();
                    } catch (Exception e) {
                        log.error("canal client exception consumed: {}", e.getMessage(), e);
                    }
                }
            }catch (Exception e) {
                log.error("canal client exception occurred: {}", e.getMessage(), e);
            }
        }
    }

    public static final class Builder {
        private String filter = StringUtils.EMPTY;
        private Integer batchSize = 1;
        private Long timeout = 1L;
        private TimeUnit unit = TimeUnit.SECONDS;
        private String servers;
        private String topic;
        private Integer partition;
        private String groupId;
        private MessageHandler messageHandler;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder servers(String servers) {
            this.servers = servers;
            return this;
        }

        public Builder topic(String topic) {
            this.topic = topic;
            return this;
        }

        public Builder partition(Integer partition) {
            this.partition = partition;
            return this;
        }

        public Builder groupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder filter(String filter) {
            this.filter = filter;
            return this;
        }

        public Builder batchSize(Integer batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        public Builder timeout(Long timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder unit(TimeUnit unit) {
            this.unit = unit;
            return this;
        }

        public Builder messageHandler(MessageHandler messageHandler) {
            this.messageHandler = messageHandler;
            return this;
        }

        public KafkaCanalClient build() {
            KafkaCanalConnector canalConnector = new KafkaCanalConnector(servers, topic, partition, groupId, batchSize, true);
            KafkaCanalClient kafkaCanalClient = new KafkaCanalClient();
            kafkaCanalClient.setConnector(canalConnector);
            kafkaCanalClient.setMessageHandler(messageHandler);
            kafkaCanalClient.filter = this.filter;
            kafkaCanalClient.batchSize = this.batchSize;
            kafkaCanalClient.timeout = this.timeout;
            kafkaCanalClient.unit = this.unit;
            return kafkaCanalClient;
        }
    }
}
