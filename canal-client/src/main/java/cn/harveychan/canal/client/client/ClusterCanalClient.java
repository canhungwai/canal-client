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
import com.alibaba.otter.canal.client.CanalConnectors;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * cluster 模式客户端
 *
 * @author canhungwai
 * @since 2021-09-15
 */
public class ClusterCanalClient extends AbstractCanalClient {

    public static Builder builder() {
        return Builder.builder();
    }

    public static final class Builder {
        private String filter = StringUtils.EMPTY;
        private Integer batchSize = 1;
        private Long timeout = 1L;
        private TimeUnit unit = TimeUnit.SECONDS;
        private String canalServers;
        private String destination;
        private String userName;
        private String password;
        private MessageHandler messageHandler;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder canalServers(String canalServers) {
            this.canalServers = canalServers;
            return this;
        }

        public Builder destination(String destination) {
            this.destination = destination;
            return this;
        }

        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
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

        public ClusterCanalClient build() {
            List<InetSocketAddress> addresses = Stream.of(canalServers.split(","))
                    .map(address -> {
                        String[] split = address.split(":");
                        return new InetSocketAddress(split[0], Integer.parseInt(split[1]));
                    }).collect(Collectors.toList());
            CanalConnector canalConnector = CanalConnectors.newClusterConnector(addresses, destination, userName, password);
            ClusterCanalClient clusterCanalClient = new ClusterCanalClient();
            clusterCanalClient.setConnector(canalConnector);
            clusterCanalClient.setMessageHandler(messageHandler);
            clusterCanalClient.filter = this.filter;
            clusterCanalClient.batchSize = this.batchSize;
            clusterCanalClient.timeout = this.timeout;
            clusterCanalClient.unit = this.unit;
            return clusterCanalClient;
        }
    }
}
