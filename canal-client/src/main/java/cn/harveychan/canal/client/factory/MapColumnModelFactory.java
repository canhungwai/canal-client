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

import cn.harveychan.canal.client.util.EntryUtil;
import cn.harveychan.canal.client.util.FieldUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * 对象数据工厂（Map）
 *
 * @author canhungwai
 * @since 2021-09-15
 */
public class MapColumnModelFactory extends AbstractModelFactory<Map<String, String>> {

    @Override
    <R> R newInstance(Class<R> clazz, Map<String, String> valueMap) throws Exception {
        R object = clazz.newInstance();
        Map<String, String> columnNames = EntryUtil.getFieldName(object.getClass());
        for (Map.Entry<String, String> entry : valueMap.entrySet()) {
            String fieldName = columnNames.get(entry.getKey());
            if (StringUtils.isNotEmpty(fieldName)) {
                FieldUtil.setFieldValue(object, fieldName, entry.getValue());
            }
        }
        return object;
    }
}
