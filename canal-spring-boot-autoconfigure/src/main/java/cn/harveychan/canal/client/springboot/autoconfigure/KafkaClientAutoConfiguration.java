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

package cn.harveychan.canal.client.springboot.autoconfigure;

import cn.harveychan.canal.client.client.KafkaCanalClient;
import cn.harveychan.canal.client.factory.MapColumnModelFactory;
import cn.harveychan.canal.client.handler.EntryHandler;
import cn.harveychan.canal.client.handler.MessageHandler;
import cn.harveychan.canal.client.handler.RowDataHandler;
import cn.harveychan.canal.client.handler.impl.AsyncFlatMessageHandlerImpl;
import cn.harveychan.canal.client.handler.impl.MapRowDataHandlerImpl;
import cn.harveychan.canal.client.handler.impl.SyncFlatMessageHandlerImpl;
import cn.harveychan.canal.client.springboot.properties.CanalKafkaProperties;
import cn.harveychan.canal.client.springboot.properties.CanalProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * kafka 模式客户端自动装配
 *
 * @author canhungwai
 * @since 2021-09-15
 */
@Configuration
@EnableConfigurationProperties(CanalKafkaProperties.class)
@ConditionalOnBean(value = {EntryHandler.class})
@ConditionalOnProperty(value = CanalProperties.CANAL_MODE, havingValue = "kafka")
@Import(ThreadPoolAutoConfiguration.class)
public class KafkaClientAutoConfiguration {

    private CanalKafkaProperties canalKafkaProperties;

    public KafkaClientAutoConfiguration(CanalKafkaProperties canalKafkaProperties) {
        this.canalKafkaProperties = canalKafkaProperties;
    }

    @Bean
    public RowDataHandler<List<Map<String, String>>> rowDataHandler() {
        return new MapRowDataHandlerImpl(new MapColumnModelFactory());
    }

    @Bean
    @ConditionalOnProperty(value = CanalProperties.CANAL_ASYNC, havingValue = "true", matchIfMissing = true)
    public MessageHandler messageHandler(RowDataHandler<List<Map<String, String>>> rowDataHandler, List<EntryHandler> entryHandlers,
                                         ExecutorService executorService) {
        return new AsyncFlatMessageHandlerImpl(entryHandlers, rowDataHandler, executorService);
    }

    @Bean
    @ConditionalOnProperty(value = CanalProperties.CANAL_ASYNC, havingValue = "false")
    public MessageHandler messageHandler(RowDataHandler<List<Map<String, String>>> rowDataHandler, List<EntryHandler> entryHandlers) {
        return new SyncFlatMessageHandlerImpl(entryHandlers, rowDataHandler);
    }

    public KafkaCanalClient kafkaCanalClient(MessageHandler messageHandler) {
        return KafkaCanalClient.builder()
                .servers(canalKafkaProperties.getServer())
                .topic(canalKafkaProperties.getDestination())
                .groupId(canalKafkaProperties.getGroupId())
                .filter(canalKafkaProperties.getFilter())
                .batchSize(canalKafkaProperties.getBatchSize())
                .timeout(canalKafkaProperties.getTimeout())
                .unit(canalKafkaProperties.getUnit())
                .messageHandler(messageHandler)
                .build();
    }
}
