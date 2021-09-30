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
import cn.harveychan.canal.client.handler.EntryHandler;
import cn.harveychan.canal.example.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 用户实体处理器
 *
 * @author canhungwai
 * @since 2021-09-16
 */
@Slf4j
@CanalTable(value = "t_user")
@Component
public class UserHandler implements EntryHandler<User> {

    @Override
    public void insert(User user) {
        // 你的逻辑
        log.info("insert user:{}", user);
    }

    @Override
    public void update(User before, User after) {
        // 你的逻辑
        log.info("update before:{}", before);
        log.info("update after:{}", after);
    }

    @Override
    public void delete(User user) {
        // 你的逻辑
        log.info("delete user:{}", user);
    }
}
