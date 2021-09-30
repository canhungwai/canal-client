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

package cn.harveychan.canal.example.handler;

import cn.harveychan.canal.client.annotation.CanalTable;
import cn.harveychan.canal.client.context.CanalContext;
import cn.harveychan.canal.client.handler.EntryHandler;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Param;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

/**
 * 默认实体处理器
 *
 * @author canhungwai
 * @since 2021-09-16
 */
@Slf4j
@CanalTable(value = "all")
@Component
public class DefaultEntryHandler implements EntryHandler<Map<String, String>> {

    @Resource
    private DSLContext dsl;

    @Override
    public void insert(Map<String, String> map) {
        log.info("insert message:{}", map);
        String table = CanalContext.getModel().getTable();
        List<Field<Object>> fields = map.keySet().stream().map(DSL::field).collect(Collectors.toList());
        List<Param<String>> values = map.values().stream().map(DSL::value).collect(Collectors.toList());
        int execute = dsl.insertInto(table(table)).columns(fields).values(values).execute();
        log.info("executed result:{}", execute);
    }

    @Override
    public void update(Map<String, String> before, Map<String, String> after) {
        log.info("update before:{}", before);
        log.info("update after:{}", after);
        String table = CanalContext.getModel().getTable();
        Map<Field<Object>, String> map = after.entrySet().stream().filter(entry -> before.get(entry.getKey()) != null)
                .collect(Collectors.toMap(entry -> field(entry.getKey()), Map.Entry::getValue));
        dsl.update(table(table)).set(map).where(field("id").eq(after.get("id"))).execute();
    }

    @Override
    public void delete(Map<String, String> map) {
        log.info("delete message:{}", map);
        String table = CanalContext.getModel().getTable();
        dsl.delete(table(table)).where(field("id").eq(map.get("id"))).execute();
    }
}
