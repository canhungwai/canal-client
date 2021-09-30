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

package cn.harveychan.canal.client.util;

import cn.harveychan.canal.client.annotation.CanalTable;
import cn.harveychan.canal.client.enums.TableNameEnum;
import cn.harveychan.canal.client.handler.EntryHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实体处理器工具类
 *
 * @author canhungwai
 * @since 2021-09-14
 */
public class HandlerUtil {

    private HandlerUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 根据表名获取对应的实体处理器，如果没有，则返回统一的实体处理器
     *
     * @param entryHandlers
     * @param tableName
     * @return
     */
    public static EntryHandler getEntryHandler(List<? extends EntryHandler> entryHandlers, String tableName) {
        EntryHandler globalHandler = null;
        for (EntryHandler handler : entryHandlers) {
            String canalTableName = getCanalTableName(handler);
            if (TableNameEnum.ALL.name().toLowerCase().equals(canalTableName)) {
                globalHandler = handler;
                continue;
            }
            if (tableName.equals(canalTableName)) {
                return handler;
            }
            String name = GenericUtil.getTableGenericProperties(handler);
            if (tableName.equals(name)) {
                return handler;
            }
        }
        return globalHandler;
    }

    /**
     * 处理表名与实体处理器的对应关系
     *
     * @param entryHandlers
     * @return
     */
    public static Map<String, EntryHandler> getTableHandlerMap(List<? extends EntryHandler> entryHandlers) {
        Map<String, EntryHandler> map = new ConcurrentHashMap<>();
        if (entryHandlers != null && entryHandlers.size() > 0) {
            for (EntryHandler handler : entryHandlers) {
                String canalTableName = getCanalTableName(handler);
                if (canalTableName != null) {
                    map.putIfAbsent(canalTableName.toLowerCase(), handler);
                } else {
                    String name = GenericUtil.getTableGenericProperties(handler);
                    if (name != null) {
                        map.putIfAbsent(name.toLowerCase(), handler);
                    }
                }
            }
        }
        return map;
    }

    /**
     * 根据表名获取对应的实体处理器，如果没有，则返回统一的实体处理器
     *
     * @param map
     * @param tableName
     * @return
     */
    public static EntryHandler getEntryHandler(Map<String, EntryHandler> map, String tableName) {
        EntryHandler entryHandler = map.get(tableName);
        if (entryHandler == null) {
            return map.get(TableNameEnum.ALL.name().toLowerCase());
        }
        return entryHandler;
    }

    /**
     * 获取实体处理器中定义的表名
     *
     * @param entryHandler
     * @return
     */
    public static String getCanalTableName(EntryHandler entryHandler) {
        CanalTable annotation = entryHandler.getClass().getAnnotation(CanalTable.class);
        if (annotation != null) {
            return annotation.value();
        }
        return null;
    }
}
