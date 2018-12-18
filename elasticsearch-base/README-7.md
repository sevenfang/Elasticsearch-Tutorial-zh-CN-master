# 博文内容

- 博文标题：1.7 Elasticsearch 锁相关介绍
- 博文地址：<http://www.youmeek.com/elasticsearch-lock/>


## 课程环境

- **CentOS 7.3 x64**
- JDK 版本：1.8（最低要求），主推：**JDK 1.8.0_121**
- Elasticsearch 版本：**5.2.0**
- 相关软件包百度云下载地址（密码：0yzd）：<http://pan.baidu.com/s/1qXQXZRm>
- **注意注意：** Elasticsearch、Kibana 安装过程请移步到我 Github 上的这套 Linux 教程：<https://github.com/judasn/Linux-Tutorial/blob/master/ELK-Install-And-Settings.md>
- Elasticsearch 和 Kibana 都要安装。后面的教程都是在 Kibana 的 Dev Tools 工具上执行的命令。

------------------------


## 数据准备

- 先删除前面章节的索引：`DELETE /product_index?pretty`
- 创建带有 Tags 的索引数据：

``` json
PUT /product_index/product/111
{
  "product_name": "PHILIPS toothbrush HX6730/02",
  "product_desc": "【3?9 元，前 1000 名赠刷头，6 月 1 日 0 点火爆开抢，618 开门红巅峰 48 小时，抢先加入购物车】飞利浦畅销款，万千好评！深入净齿，智能美白！",
  "price": 399.00,
  "tags": [
    "toothbrush",
    "PHILIPS"
  ]
}

PUT /product_index/product/222
{
  "product_name": "PHILIPS toothbrush HX6730/02",
  "product_desc": "【3?9 元，前 1000 名赠刷头，6 月 1 日 0 点火爆开抢，618 开门红巅峰 48 小时，抢先加入购物车】飞利浦畅销款，万千好评！深入净齿，智能美白！",
  "price": 399.00,
  "custom_version": 1,
  "tags": [
    "toothbrush",
    "PHILIPS"
  ]
}
```

## index 锁

- 适用于：不是频繁操作的地方
- 官网：<https://www.elastic.co/guide/cn/elasticsearch/guide/current/concurrency-solutions.html>
- 其两个界面都执行下面内容，会报：[lock][global]: version conflict, document already exists (current version [1])

``` json
PUT /product_index/lock/global/_create
{
}
```

- 上锁后，业务执行完成，要删除锁：

``` json
DELETE /product_index/lock/global
```

## document 锁

- 官网：<https://www.elastic.co/guide/cn/elasticsearch/guide/current/concurrency-solutions.html>
- 用脚本进行上锁
- 在 Elasticsearch 安装目录下的 config/scripts 目录下创建一个：custom-lock.groovy，脚本内容：`if ( ctx._source.process_id != process_id ) { assert false }; ctx.op = 'noop';`
- process_id 建议为自定义一个 UUID

``` json
POST /product_index/lock/1/_update
{
  "upsert": {
    "process_id": "AAASSS"
  },
  "script": {
    "lang": "groovy",
    "file": "custom-lock",
    "params": {
      "process_id": "AAASSS"
    }
  }
}
```

- 这时候其他地方是无法再上锁的。
- 然后可以开始业务操作
- 业务处理好，开始解锁：
- 先 _refresh：`POST /product_index/_refresh`
- 删除单条/多条上锁数据：
- 先查询当前 process_id 上锁的数据有哪些：

``` json
GET /product_index/lock/_search?scroll=1m
{
  "query": {
    "term": {
      "process_id": "AAASSS"
    }
  }
}
```

- 根据查询出来的 document id 进行删除：

``` json
PUT /product_index/lock/_bulk
{ "delete": { "_id": 1 } }
{ "delete": { "_id": 2 } }
{ "delete": { "_id": 3 } }
{ "delete": { "_id": 4 } }
```


## 乐观锁相关概念


## 乐观锁操作

### Elasticsearch 默认的 _version 控制

- 核心：
	- Elasticsearch 内部的 _version 只要等于当前数据存储的 _version 值即可修改成功。

- 下面是全量更新的操作测试：
- 客户端 1 执行：

``` json
PUT /product_index/product/111?version=1
{
  "product_name": "PHILIPS toothbrush HX6730/02 update1",
  "product_desc": "【3?9 元，前 1000 名赠刷头，6 月 1 日 0 点火爆开抢，618 开门红巅峰 48 小时，抢先加入购物车】飞利浦畅销款，万千好评！深入净齿，智能美白！",
  "price": 399.00,
  "tags": [
    "toothbrush",
    "PHILIPS"
  ]
}
```

- 客户端 2 执行

``` json
PUT /product_index/product/111?version=1
{
  "product_name": "PHILIPS toothbrush HX6730/02 update2",
  "product_desc": "【3?9 元，前 1000 名赠刷头，6 月 1 日 0 点火爆开抢，618 开门红巅峰 48 小时，抢先加入购物车】飞利浦畅销款，万千好评！深入净齿，智能美白！",
  "price": 399.00,
  "tags": [
    "toothbrush",
    "PHILIPS"
  ]
}
```

- 客户端 2 会执行失败，因为 version 已经不等于 1 了，这时候需要重新 GET 一次获取到最新的数据，然后再重新带上最新的 version 值进行更新。



### Elasticsearch 自定义的 version 控制

- 核心：
	- Elasticsearch 内部的 _version 只要大于当前数据存储的 _version 值即可（不能等于）。

- 客户端 1 执行：

``` json
PUT /product_index/product/222?version=3&version_type=external
{
  "product_name": "PHILIPS toothbrush HX6730/02 update3",
  "product_desc": "【3?9 元，前 1000 名赠刷头，6 月 1 日 0 点火爆开抢，618 开门红巅峰 48 小时，抢先加入购物车】飞利浦畅销款，万千好评！深入净齿，智能美白！",
  "price": 399.00,
  "tags": [
    "toothbrush",
    "PHILIPS"
  ]
}
```

- 客户端 2 执行：

``` json
PUT /product_index/product/222?version=5&version_type=external
{
  "product_name": "PHILIPS toothbrush HX6730/02 update5",
  "product_desc": "【3?9 元，前 1000 名赠刷头，6 月 1 日 0 点火爆开抢，618 开门红巅峰 48 小时，抢先加入购物车】飞利浦畅销款，万千好评！深入净齿，智能美白！",
  "price": 399.00,
  "tags": [
    "toothbrush",
    "PHILIPS"
  ]
}
```

- partial update 更新方式内置乐观锁并发控制



















## 其他资料辅助




