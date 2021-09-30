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

package cn.harveychan.canal.example.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体类（测试）
 *
 * @author canhungwai
 * @since 2021-09-16
 */
@Data
@Table(name = "t_user")
public class User implements Serializable {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 用户名
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 性别（1：男；2：女；3：未知）
     */
    private Integer gender;

    /**
     * 国家 id
     */
    @Column(name = "country_id")
    private Integer countryId;

    /**
     * 出生日期
     */
    private Date birthday;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;
}
