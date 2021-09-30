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

import cn.harveychan.canal.client.handler.EntryHandler;

import javax.persistence.Table;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类属性相关操作工具类
 *
 * @author canhungwai
 * @since 2021-09-14
 */
public class GenericUtil {

    private static final Map<Class<? extends EntryHandler>, Class<?>> cache = new ConcurrentHashMap<>();

    private GenericUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 获取实体处理对应表实体类中定义的表名
     *
     * @param entryHandler
     * @return
     */
    static String getTableGenericProperties(EntryHandler entryHandler) {
        Class<Object> tableClass = getTableClass(entryHandler);
        if (tableClass != null) {
            Table table = tableClass.getAnnotation(Table.class);
            if (table != null) {
                return table.name();
            }
        }
        return null;
    }

    /**
     * 获取实体处理器对应表实体类
     *
     * @param entryHandler
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getTableClass(EntryHandler entryHandler) {
        Class<? extends EntryHandler> handlerClass = entryHandler.getClass();
        Class tableClass = cache.get(handlerClass);
        if (tableClass == null) {
            Type[] interfaceTypes = handlerClass.getGenericInterfaces(); // 实现接口的类型
            for (Type type : interfaceTypes) {
                Class clazz = (Class) ((ParameterizedType) type).getRawType(); // 原始类
                if (clazz.equals(EntryHandler.class)) {
                    tableClass = (Class<T>) ((ParameterizedType) type).getActualTypeArguments()[0]; // 泛型类
                    cache.putIfAbsent(handlerClass, tableClass);
                }
            }
        }
        return tableClass;
    }
}
