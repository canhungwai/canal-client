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

/**
 * 实体处理器
 *
 * @param <T>
 * @author canhungwai
 * @since 2021-09-14
 */
public interface EntryHandler<T> {

    /**
     * 新增数据
     *
     * @param t
     */
    default void insert(T t) {
    }

    /**
     * 修改数据
     *
     * @param before 修改前的数据
     * @param after  修改后的数据
     */
    default void update(T before, T after) {
    }

    /**
     * 删除数据
     *
     * @param t
     */
    default void delete(T t) {
    }
}
