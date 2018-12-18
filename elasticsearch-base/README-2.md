# 博文内容

- 博文标题：1.2 Elasticsearch 索引集群的管理
- 博文地址：<http://www.youmeek.com/elasticsearch-cluster/>


## 课程环境

- **CentOS 7.3 x64**
- JDK 版本：1.8（最低要求），主推：**JDK 1.8.0_121**
- Elasticsearch 版本：**5.2.0**
- 相关软件包百度云下载地址（密码：0yzd）：<http://pan.baidu.com/s/1qXQXZRm>
- **注意注意：** Elasticsearch、Kibana 安装过程请移步到我 Github 上的这套 Linux 教程：<https://github.com/judasn/Linux-Tutorial/blob/master/ELK-Install-And-Settings.md>
- Elasticsearch 和 Kibana 都要安装。后面的教程都是在 Kibana 的 Dev Tools 工具上执行的命令。

------------------------

## Elasticsearch 介绍

- 当前（201705）最新版本为：5.4
- 官网：<https://www.elastic.co/>
- Github 地址：<https://github.com/elastic/elasticsearch>
- Elasticsearch 5.2 官网文档：<https://www.elastic.co/guide/en/elasticsearch/reference/5.2/index.html>

## Elasticsearch 索引简单操作

- 上一篇章我们已经安装了 Elasticsearch 和 Kibana，所以我们现在启动 Elasticsearch 和 Kibana 访问：<http://192.168.1.127:5601/app/kibana#/dev_tools/console?_g=()>，如下图：
- 需要注意的细节是：Kibana Dev Tools 上面可以写多条 DSL 语句，光标放在哪一条上面，那一条后面有一个播放号可以单独执行，所以没必要删掉旧的语句。

<a href= "http://img.youmeek.com/2017/elasticsearch-cluster-1.jpg" class="foobox"><img src="http://img.youmeek.com/2017/elasticsearch-cluster-1.jpg" alt="Kibana Dev Tools"></a>

- 查询集群健康状况：`GET /_cat/health?v`
- 查询集群中有哪些索引：`GET /_cat/indices?v`
- 简单的索引操作：
	- 新增索引：`PUT /product_index`
	- 删除指定索引：`DELETE /product_index`
	- 删除指定多个索引：`DELETE /product_index,order_index`
	- 删除匹配符索引：`DELETE /product_*`
	- 删除所有索引：`DELETE /_all`
	- 查询索引配置信息：`GET /product_index/_settings`
	- 查询多个索引配置信息：`GET /product_index,order_index/_settings`
	- 查询所有索引配置信息：`GET /_all/_settings`

## Elasticsearch 索引较复杂操作

- 新增索引，并指定 primary shards 和 replica shards 数量。

``` json
PUT /order_index
{
  "settings": {
    "index": {
      "number_of_shards": 5,
      "number_of_replicas": 1
    }
  }
}
```

- 新增完索引后，更改 replica shards 数量：

``` json
PUT /order_index/_settings
{
	"number_of_replicas": 2
}
```

- 新增索引并设置 mapping（Dynamic Mapping）：
- mapping 你可以理解为是传统数据库中的设置表结构一样的作用，比如有个字段叫做 introduce，传统数据库文本字段你会考虑设置为：char、varchar、text，是否为空，是否有默认值等。
- Elasticsearch 中的 mapping 类似上面，因为你一样要考虑比如这个字段：article_title 是否设置为 text 类型，要不要分词等。 
- 下面的 mapping 使用了 ik 分词器（5.2.0 版本）。field 新增后是不能修改的。

``` json
PUT /product_index
{
  "settings": {
    "refresh_interval": "5s",
    "number_of_shards": 5,
    "number_of_replicas": 1
  },
  "mappings": {
    "product": {
      "properties": {
        "id": {
          "type": "text",
          "index": "not_analyzed"
        },
        "product_name": {
          "type": "text",
          "store": "no",
          "term_vector": "with_positions_offsets",
          "analyzer": "ik_max_word",
          "search_analyzer": "ik_max_word",
          "boost": 5,
          "fields": {
            "keyword": {
                "type": "keyword",
                "ignore_above": 256
            }
          }
        },
        "product_desc": {
          "type": "text",
          "index": "not_analyzed"
        },
        "price": {
          "type": "double",
          "index": "not_analyzed"
        },
        "created_date_time": {
          "type": "date",
          "index": "not_analyzed",
          "format": "basic_date_time"
        },
        "last_modified_date_time": {
          "type": "date",
          "index": "not_analyzed",
          "format": "basic_date_time"
        },
        "version": {
          "type": "long",
          "index": "no"
        }
      }
    }
  }
}
```

## 其他资料辅助





