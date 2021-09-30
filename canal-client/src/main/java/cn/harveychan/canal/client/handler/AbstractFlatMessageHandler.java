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

package cn.harveychan.canal.client.handler;

import cn.harveychan.canal.client.context.CanalContext;
import cn.harveychan.canal.client.model.CanalModel;
import cn.harveychan.canal.client.util.HandlerUtil;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.FlatMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 消息处理器基类（Flat）
 *
 * @author canhungwai
 * @since 2021-09-15
 */
@Slf4j
public abstract class AbstractFlatMessageHandler implements MessageHandler<FlatMessage> {

    private Map<String, EntryHandler> tableHandlerMap;

    private RowDataHandler<List<Map<String, String>>> rowDataHandler;

    public AbstractFlatMessageHandler(List<? extends EntryHandler> entryHandlers, RowDataHandler<List<Map<String, String>>> rowDataHandler) {
        this.tableHandlerMap = HandlerUtil.getTableHandlerMap(entryHandlers);
        this.rowDataHandler = rowDataHandler;
    }

    @Override
    public void handleMessage(FlatMessage flatMessage) {
        log.debug("handled message:{}", flatMessage);
        List<Map<String, String>> data = flatMessage.getData();
        if (data != null && data.size() > 0) {
            for (int i = 0; i < data.size(); i++) {
                CanalEntry.EventType eventType = CanalEntry.EventType.valueOf(flatMessage.getType());
                List<Map<String, String>> maps;
                if (eventType.equals(CanalEntry.EventType.UPDATE)) {
                    Map<String, String> map = data.get(i);
                    Map<String, String> oldMap = flatMessage.getOld().get(i);
                    maps = Stream.of(map, oldMap).collect(Collectors.toList());
                } else {
                    maps = Stream.of(data.get(i)).collect(Collectors.toList());
                }
                try {
                    EntryHandler<?> entryHandler = HandlerUtil.getEntryHandler(tableHandlerMap, flatMessage.getTable());
                    if (entryHandler != null) {
                        CanalModel model = CanalModel.Builder.builder()
                                .id(flatMessage.getId())
                                .database(flatMessage.getDatabase())
                                .table(flatMessage.getTable())
                                .executeTime(flatMessage.getEs())
                                .createTime(flatMessage.getTs())
                                .build();
                        CanalContext.setModel(model);
                        log.debug("send message to handler:{}-{}", eventType, maps);
                        rowDataHandler.handleRowData(maps, entryHandler, eventType);
                    }
                } catch (Exception e) {
                    log.error("message handling exception: {}", e.getMessage(), e);
                    throw new RuntimeException("parse event has an error, data:" + data.toString(), e);
                } finally {
                    CanalContext.removeModel();
                }
            }
        }
    }
}
