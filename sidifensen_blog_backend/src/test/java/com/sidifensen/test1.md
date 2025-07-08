## 这是一篇测试文章
> 你看见这篇文章说明后台发布文章功能成功

```mermaid
gantt
title A Gantt Diagram
dateFormat  YYYY-MM-DD
section Section
A task  :a1, 2014-01-01, 30d
Another task  :after a1, 20d
```

### 图表
```mermaid
classDiagram
  class Animal
  Vehicle <|-- Car
```
| col | col | col | col |
| - | - | - | - |
| content | content | content | content |
| content | content | content | content |
| content | content | content | content |
```mermaid
erDiagram
  CAR ||--o{ NAMED-DRIVER : allows
  PERSON ||--o{ NAMED-DRIVER : is
```
```mermaid
journey
  title My working day
  section Go to work
    Make tea: 5: Me
    Go upstairs: 3: Me
    Do work: 1: Me, Cat
  section Go home
    Go downstairs: 5: Me
    Sit down: 5: Me
```


##### ElasticSearch

- 依赖和配置类和环境搭建

```java
// 依赖
<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
 </dependency>
// 配置类
   elasticsearch:
    uris: 192.168.88.128:9200
    connection-timeout: 10000
    socket-timeout: 30s
```

- 索引(**映射实体类的数据**)z
























