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

import cn.harveychan.canal.client.client.ClusterCanalClient;
import cn.harveychan.canal.client.factory.EntryColumnModelFactory;
import cn.harveychan.canal.client.handler.EntryHandler;
import cn.harveychan.canal.client.handler.MessageHandler;
import cn.harveychan.canal.client.handler.RowDataHandler;
import cn.harveychan.canal.client.handler.impl.AsyncMessageHandlerImpl;
import cn.harveychan.canal.client.handler.impl.RowDataHandlerImpl;
import cn.harveychan.canal.client.handler.impl.SyncMessageHandlerImpl;
import cn.harveychan.canal.client.springboot.properties.CanalProperties;
import cn.harveychan.canal.client.springboot.properties.CanalSimpleProperties;
import com.alibaba.otter.canal.protocol.CanalEntry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * cluster 模式客户端自动装配
 *
 * @author canhungwai
 * @since 2021-09-15
 */
@Configuration
@EnableConfigurationProperties(CanalSimpleProperties.class)
@ConditionalOnBean(value = {EntryHandler.class})
@ConditionalOnProperty(value = CanalProperties.CANAL_MODE, havingValue = "cluster")
@Import(ThreadPoolAutoConfiguration.class)
public class ClusterClientAutoConfiguration {

    private CanalSimpleProperties canalSimpleProperties;

    public ClusterClientAutoConfiguration(CanalSimpleProperties canalSimpleProperties) {
        this.canalSimpleProperties = canalSimpleProperties;
    }

    @Bean
    public RowDataHandler<CanalEntry.RowData> rowDataHandler() {
        return new RowDataHandlerImpl(new EntryColumnModelFactory());
    }

    @Bean
    @ConditionalOnProperty(value = CanalProperties.CANAL_ASYNC, havingValue = "true", matchIfMissing = true)
    public MessageHandler messageHandler(RowDataHandler<CanalEntry.RowData> rowDataHandler, List<EntryHandler> entryHandlers,
                                         ExecutorService executorService) {
        return new AsyncMessageHandlerImpl(entryHandlers, rowDataHandler, executorService);
    }

    @Bean
    @ConditionalOnProperty(value = CanalProperties.CANAL_ASYNC, havingValue = "false")
    public MessageHandler messageHandler(RowDataHandler<CanalEntry.RowData> rowDataHandler, List<EntryHandler> entryHandlers) {
        return new SyncMessageHandlerImpl(entryHandlers, rowDataHandler);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public ClusterCanalClient clusterCanalClient(MessageHandler messageHandler) {
        return ClusterCanalClient.builder()
                .canalServers(canalSimpleProperties.getServer())
                .destination(canalSimpleProperties.getDestination())
                .userName(canalSimpleProperties.getUserName())
                .password(canalSimpleProperties.getPassword())
                .filter(canalSimpleProperties.getFilter())
                .batchSize(canalSimpleProperties.getBatchSize())
                .timeout(canalSimpleProperties.getTimeout())
                .unit(canalSimpleProperties.getUnit())
                .messageHandler(messageHandler)
                .build();
    }
}
