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

import cn.harveychan.canal.client.factory.IModelFactory;
import cn.harveychan.canal.client.handler.EntryHandler;
import cn.harveychan.canal.client.handler.RowDataHandler;
import com.alibaba.otter.canal.protocol.CanalEntry;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据处理器（实体）
 *
 * @author canhungwai
 * @since 2021-09-14
 */
public class RowDataHandlerImpl implements RowDataHandler<CanalEntry.RowData> {

    private IModelFactory<List<CanalEntry.Column>> modelFactory;

    public RowDataHandlerImpl(IModelFactory modelFactory) {
        this.modelFactory = modelFactory;
    }

    @Override
    public <R> void handleRowData(CanalEntry.RowData rowData, EntryHandler<R> entryHandler, CanalEntry.EventType eventType) throws Exception {
        if (entryHandler != null) {
            switch (eventType) {
                case INSERT: // 新增
                    R addObj = modelFactory.newInstance(entryHandler, rowData.getAfterColumnsList());
                    entryHandler.insert(addObj);
                    break;
                case UPDATE: // 修改
                    Set<String> updateColumnSet = rowData.getAfterColumnsList().stream()
                            .filter(CanalEntry.Column::getUpdated)
                            .map(CanalEntry.Column::getName)
                            .collect(Collectors.toSet());
                    // 变更前数据
                    R before = modelFactory.newInstance(entryHandler, rowData.getBeforeColumnsList(), updateColumnSet);
                    // 变更后数据
                    R after = modelFactory.newInstance(entryHandler, rowData.getAfterColumnsList());
                    entryHandler.update(before, after);
                    break;
                case DELETE: // 删除
                    R delObj = modelFactory.newInstance(entryHandler, rowData.getBeforeColumnsList());
                    entryHandler.delete(delObj);
                    break;
                default:
                    break;
            }
        }
    }
}
