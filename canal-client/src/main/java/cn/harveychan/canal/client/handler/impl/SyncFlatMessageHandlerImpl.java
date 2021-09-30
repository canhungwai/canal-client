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

package cn.harveychan.canal.client.handler.impl;

import cn.harveychan.canal.client.handler.AbstractFlatMessageHandler;
import cn.harveychan.canal.client.handler.EntryHandler;
import cn.harveychan.canal.client.handler.RowDataHandler;
import com.alibaba.otter.canal.protocol.FlatMessage;

import java.util.List;
import java.util.Map;

/**
 * 同步消息处理器（Flat）
 *
 * @author canhungwai
 * @since 2021-09-15
 */
public class SyncFlatMessageHandlerImpl extends AbstractFlatMessageHandler {

    public SyncFlatMessageHandlerImpl(List<? extends EntryHandler> entryHandlers, RowDataHandler<List<Map<String, String>>> rowDataHandler) {
        super(entryHandlers, rowDataHandler);
    }

    @Override
    public void handleMessage(FlatMessage flatMessage) {
        super.handleMessage(flatMessage);
    }
}
