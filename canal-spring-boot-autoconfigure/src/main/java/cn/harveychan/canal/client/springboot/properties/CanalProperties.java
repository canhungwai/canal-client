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

package cn.harveychan.canal.client.springboot.properties;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * canal 配置属性
 *
 * @author canhungwai
 * @since 2021-09-15
 */
@Getter
@Setter
public class CanalProperties {

    public static final String CANAL_PREFIX = "canal";

    public static final String CANAL_ASYNC = CANAL_PREFIX + "." + "async";

    public static final String CANAL_MODE = CANAL_PREFIX + "." + "mode";

    /**
     * 模式，可选：simple, cluster, zookeeper, kafka, rocketMQ
     */
    private String mode;

    /**
     * 是否异步处理
     */
    private Boolean async;

    /**
     * 服务端地址
     */
    private String server;

    /**
     *
     */
    private String destination;

    /**
     * 记录订阅规则
     */
    private String filter = StringUtils.EMPTY;

    /**
     * 每次获取记录的数量
     */
    private Integer batchSize = 1;

    /**
     * 超时时间
     */
    private Long timeout = 1L;

    /**
     * 超时单位
     */
    private TimeUnit unit = TimeUnit.SECONDS;
}
