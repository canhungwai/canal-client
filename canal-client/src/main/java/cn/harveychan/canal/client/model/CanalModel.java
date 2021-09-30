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

package cn.harveychan.canal.client.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * canal 消息通用数据
 *
 * @author canhungwai
 * @since 2021-09-14
 */
@Getter
@Setter
@ToString
public class CanalModel {

    /**
     * 消息 id
     */
    private long id;

    /**
     * 数据库名
     */
    private String database;

    /**
     * 表名
     */
    private String table;

    /**
     * binlog 数据执行时间
     */
    private Long executeTime;

    /**
     * dml 语句创建时间
     */
    private Long createTime;

    public static final class Builder {
        private long id;
        private String database;
        private String table;
        private Long executeTime;
        private Long createTime;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder database(String database) {
            this.database = database;
            return this;
        }

        public Builder table(String table) {
            this.table = table;
            return this;
        }

        public Builder executeTime(Long executeTime) {
            this.executeTime = executeTime;
            return this;
        }

        public Builder createTime(Long createTime) {
            this.createTime = createTime;
            return this;
        }

        public CanalModel build() {
            CanalModel canalModel = new CanalModel();
            canalModel.setId(id);
            canalModel.setDatabase(database);
            canalModel.setTable(table);
            canalModel.setExecuteTime(executeTime);
            canalModel.setCreateTime(createTime);
            return canalModel;
        }
    }
}
