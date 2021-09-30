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

package cn.harveychan.canal.client.factory;

import cn.harveychan.canal.client.enums.TableNameEnum;
import cn.harveychan.canal.client.handler.EntryHandler;
import cn.harveychan.canal.client.util.GenericUtil;
import cn.harveychan.canal.client.util.HandlerUtil;

import java.util.Set;

/**
 * 数据工厂基类
 *
 * @author canhungwai
 * @since 2021-09-15
 */
public abstract class AbstractModelFactory<T> implements IModelFactory<T> {

    @Override
    public <R> R newInstance(EntryHandler entryHandler, T t) throws Exception {
        String canalTableName = HandlerUtil.getCanalTableName(entryHandler);
        if (TableNameEnum.ALL.name().toLowerCase().equals(canalTableName)) {
            return (R) t;
        }
        Class<R> tableClass = GenericUtil.getTableClass(entryHandler);
        if (tableClass != null) {
            return newInstance(tableClass, t);
        }
        return null;
    }

    abstract <R> R newInstance(Class<R> clazz, T t) throws Exception;
}
