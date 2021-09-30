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

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 实体工具类
 *
 * @author canhungwai
 * @since 2021-09-14
 */
public class EntryUtil {

    private static final Map<Class<?>, Map<String, String>> cache = new ConcurrentHashMap<>();

    private EntryUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 获取表字段名称和实体类属性的对应关系
     *
     * @param clazz 实体类对象
     * @return
     */
    public static Map<String, String> getFieldName(Class<?> clazz) {
        Map<String, String> map = cache.get(clazz);
        if (map == null) {
            List<Field> fields = FieldUtils.getAllFieldsList(clazz);
            // 如果属性存在 column 注解，则使用注解的名称为表字段名
            map = fields.stream()
                    .filter(EntryUtil::notTransient)
                    .filter(field -> !Modifier.isStatic(field.getModifiers()))
                    .collect(Collectors.toMap(EntryUtil::getColumnName, Field::getName));
            cache.putIfAbsent(clazz, map);
        }
        return map;
    }

    /**
     * 获取属性对应的表字段名
     *
     * @param field
     * @return
     */
    private static String getColumnName(Field field) {
        Column annotation = field.getAnnotation(Column.class);
        return annotation != null ? annotation.name().replaceAll("`", "")
                : CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName()); // userName -> user_name
    }

    /**
     * 校验属性是否属于持久化
     *
     * @param field
     * @return
     */
    private static boolean notTransient(Field field) {
        Transient annotation = field.getAnnotation(Transient.class);
        return annotation == null;
    }
}
