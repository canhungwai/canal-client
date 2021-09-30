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
import com.alibaba.otter.canal.protocol.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 消息处理器基类
 *
 * @author canhungwai
 * @since 2021-09-15
 */
@Slf4j
public abstract class AbstractMessageHandler implements MessageHandler<Message> {

    private Map<String, EntryHandler> tableHandlerMap;

    private RowDataHandler<CanalEntry.RowData> rowDataHandler;

    public AbstractMessageHandler(List<? extends EntryHandler> entryHandlers, RowDataHandler<CanalEntry.RowData> rowDataHandler) {
        this.tableHandlerMap = HandlerUtil.getTableHandlerMap(entryHandlers);
        this.rowDataHandler = rowDataHandler;
    }

    @Override
    public void handleMessage(Message message) {
        log.debug("handled message:{}", message);
        List<CanalEntry.Entry> entries = message.getEntries();
        for (CanalEntry.Entry entry : entries) {
            if (entry.getEntryType().equals(CanalEntry.EntryType.ROWDATA)) {
                try {
                    EntryHandler<?> entryHandler = HandlerUtil.getEntryHandler(tableHandlerMap, entry.getHeader().getTableName());
                    if (entryHandler != null) {
                        CanalModel model = CanalModel.Builder.builder()
                                .id(message.getId())
                                .database(entry.getHeader().getSchemaName())
                                .table(entry.getHeader().getTableName())
                                .executeTime(entry.getHeader().getExecuteTime())
                                .build();
                        CanalContext.setModel(model);
                        CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
                        List<CanalEntry.RowData> rowDataList = rowChange.getRowDatasList();
                        CanalEntry.EventType eventType = rowChange.getEventType();
                        for (CanalEntry.RowData rowData : rowDataList) {
                            rowDataHandler.handleRowData(rowData, entryHandler, eventType);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException("parse event has an error, data:" + entry.toString(), e);
                } finally {
                    CanalContext.removeModel();
                }
            }
        }
    }
}
