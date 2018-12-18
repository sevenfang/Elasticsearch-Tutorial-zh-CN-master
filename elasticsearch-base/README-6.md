# 博文内容

- 博文标题：1.6 Elasticsearch DSL 批量操作
- 博文地址：<http://www.youmeek.com/elasticsearch-dsl-batch/>


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
PUT /product_index/product/1
{
  "product_name": "PHILIPS toothbrush HX6730/02",
  "product_desc": "【3?9 元，前 1000 名赠刷头，6 月 1 日 0 点火爆开抢，618 开门红巅峰 48 小时，抢先加入购物车】飞利浦畅销款，万千好评！深入净齿，智能美白！",
  "price": 399.00,
  "tags": [
    "toothbrush",
    "PHILIPS"
  ]
}

PUT /product_index/product/2
{
  "product_name": "Braun toothbrush 2000 3D",
  "product_desc": "6 月 1 日 16 点秒杀，仅 329 元！限量 1000 支，抢完即止！带压力感应提醒，保护牙龈，高效清洁",
  "price": 499.00,
  "tags": [
    "toothbrush",
    "Braun"
  ]
}

PUT /product_index/product2/1
{
  "product_name": "Braun toothbrush 2000 3D type2",
  "product_desc": "6 月 1 日 16 点秒杀，仅 329 元！限量 1000 支，抢完即止！带压力感应提醒，保护牙龈，高效清洁",
  "price": 499.00,
  "tags": [
    "toothbrush",
    "Braun"
  ]
}

PUT /product_index2/product2/2
{
  "product_name": "iphone7 shell",
  "product_desc": "一说到星空，就有太多美好的记忆，美丽的浩瀚宇宙，有太多说不清的神秘之处，星空太美丽，太绚烂！",
  "price": 36.00,
  "tags": [
    "iphone7",
    "phone",
    "shell"
  ]
}
```

## 批量操作的重要性

- 批量操作最重要的地方就是：可以减少大量的网络开销和连接资源。


## mget 批量查询

- 根据 index 名称，type 名称，ID 进行查询（可以是不同 index、type、id）：

``` json
GET /_mget
{
  "docs": [
    {
      "_index": "product_index",
      "_type": "product",
      "_id": 1
    },
    {
      "_index": "product_index2",
      "_type": "product2",
      "_id": 1
    }
  ]
}
```

- 根据相同 index 名称，不同 type 名称，ID 进行查询：

``` json
GET /product_index/_mget
{
  "docs": [
    {
      "_type": "product",
      "_id": 1
    },
    {
      "_type": "product2",
      "_id": 1
    }
  ]
}
```

- 根据相同 index 名称，相同 type 名称，ID 进行查询：

``` json
GET /product_index/product/_mget
{
  "ids": [1, 2]
}
```

## bulk 批量增删改

- **特别注意：bulk 对 JSON 有严格的要求，每个整体的 json 串不能换行，只能同一行。多个整体的 json 串和 json 串之间，必须要换行。可能 kibana Dev Tools 语法解析上会提示有错误，但是不用管它。**
- bulk 的格式要求官网资料：[Bulk API](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-bulk.html)、[Cheaper in Bulk](https://www.elastic.co/guide/en/elasticsearch/guide/current/bulk.html)
- 可以是多个 JSON 组合起来，按 JSON 顺序执行。
- 顺序执行过程中，前面的操作失败，不会影响后续的操作。
- 为了更加清晰表达，我这里不组合起来，拆分开来，但是你们可以考虑组合起来。
- 删除操作：

``` json
POST /_bulk
{"delete": {"_index": "product_index","_type": "product","_id": "1"}}
```

- 新增操作（product_name 开始是单独一行）：

``` json
POST /_bulk
{ "create": { "_index": "product_index", "_type": "product", "_id": "333" } }
{ "product_name": "iphone7 shell2", "product_desc": "一说到星空，就有太多美好的记忆，美丽的浩瀚宇宙，有太多说不清的神秘之处，星空太美丽，太绚烂！", "price": 36.00, "tags": [ "iphone7", "phone", "shell" ] }
```

- 更新操作（doc 属性开始是单独一行）：

``` json
POST /_bulk
{"update":{"_index": "product_index","_type": "product","_id": "1"}}
{"doc":{"product_name": "iphone7 shell2222"}}
```


## 其他资料辅助




