![maven](https://img.shields.io/maven-central/v/cn.harveychan/canal.svg)
![license](https://img.shields.io/github/license/canhungwai/canal-client.svg)

# canal 客户端

## 介绍

[canal](https://github.com/alibaba/canal) 是阿里巴巴 MySQL binlog 增量订阅&消费组件，使用该客户端前请先了解。

canal 自身提供了简单的客户端，如果要转换为数据库的实体对象，处理消费数据要每次进行对象转换。该客户端直接将 canal 的数据原始类型转换为各个数据表的实体对象，并解耦数据的增删改操作，方便给业务使用。

## 要求

java:8+

canal:v1.1.4-

## 特性

- 解耦单表增删操作
- simple, cluster, zookeeper, kafka 客户端支持
- 同步异步处理支持
- spring boot 开箱即用

## 如何使用

### 如何引入依赖

spring boot 方式：

```xml
<dependency>
  <groupId>cn.harveychan</groupId>
  <artifactId>canal-spring-boot-starter</artifactId>
  <version>1.0.0</version>
</dependency>
```

java 方式：

```xml
<dependency>
  <groupId>cn.harveychan</groupId>
  <artifactId>canal-client</artifactId>
  <version>1.0.0</version>
</dependency>
```

### 订阅数据库的增删改操作

实现 `EntryHandler` 接口，泛型为想要订阅的数据库表的实体对象，该接口的方法为 java 8 的 default 方法，方法可以不实现，如果只要监听增加操作，只实现增加方法即可。

下面以一个 t_user 表的 User 实体对象为例，默认情况下，将使用实体对象的 jpa 注解 @Table 中的表名来转换为 EntryHandler 中的泛型对象

```java
@Component
public class UserHandler implements EntryHandler<User> {
}
```

如果实体类没有使用 jpa 的 @Table 注解，也可以使用 @CanalTable 注解在 EntryHandler 来标记表名

```java
@CanalTable(value = "t_user")
@Component
public class UserHandler implements EntryHandler<User> {

    /**
     * 新增数据
     *
     * @param user
     */
    @Override
    public void insert(User user) {
        // 业务逻辑
        log.info("insert user:{}", user);
    }

    /**
     * 修改数据
     *
     * @param before 修改前的数据(数据发生变更的字段才有值)
     * @param after  修改后的数据
     */
    @Override
    public void update(User before, User after) {
        // 业务逻辑
        log.info("update before:{}", before);
        log.info("update after:{}", after);
    }

    /**
     * 删除数据
     *
     * @param user
     */
    @Override
    public void delete(User user) {
        // 业务逻辑
        log.info("delete user:{}", user);
    }
}
```

同时也支持统一的处理 @CanalTable(value = "all") , 除了存在 EntryHandler 的表以外, 其他所有表的处理将通过该处理器, 统一转为 Map<String, String> 对象

```java
@CanalTable(value = "all")
@Component
public class DefaultEntryHandler implements EntryHandler<Map<String, String>> {

    @Resource
    private DSLContext dsl;

    @Override
    public void insert(Map<String, String> map) {

    }

    @Override
    public void update(Map<String, String> before, Map<String, String> after) {

    }

    @Override
    public void delete(Map<String, String> map) {

    }
}
```

如果需要获取除实体类信息外的其他信息，可以使用

```java
CanalModel canal = CanalContext.getModel();
```

具体使用可以参考项目 demo 示例

https://github.com/canhungwai/canal-client/tree/master/canal-example

## 配置说明

| 属性              | 描述                                                         | 默认值 |
| ----------------- | ------------------------------------------------------------ | ------ |
| canal.mode        | canal 客户端类型, 目前支持4中类型: simple, cluster, zk, kafka(kafka 目前支持 flatMessage 格式) | simple |
| canal.server      | canal 服务端地址, 多个地址以逗号分割, 格式: ${host}:${port}  | null   |
| canal.user-name   | canal 的用户名                                               | null   |
| canal.password    | canal 的密码                                                 | null   |
| canal.filter      | canal 过滤的表名称，如配置则只订阅配置的表                   | ""     |
| canal.destination | canal 的 instance 名称, kafka 模式为 topic 名称              | null   |
| canal.batch-size  | 消息的数量, 超过该次数将进行一次消费                         | 1(个)  |
| canal.timeout     | 消费的时间间隔(s)                                            | 1s     |
| canal.async       | 是否是异步消费, 异步消费时, 消费时异常将导致消息不会回滚和不保证顺序性 | true   |
| canal.group-id    | kafka groupId 消费者订阅消息时可使用, kafka canal 客户端     | null   |
| canal.partition   | kafka partition                                              | null   |

