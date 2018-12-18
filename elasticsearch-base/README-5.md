# 博文内容

- 博文标题：1.5 Elasticsearch DSL 聚合语法介绍
- 博文地址：<http://www.youmeek.com/elasticsearch-dsl-aggregation/>


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

PUT /product_index/product/3
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

## 简单分析案例

### 计算每个 tag 下的商品数量，自己取一个分组聚合结果名称：product_group_by_tags

``` json
GET /product_index/product/_search
{
  "aggs": {
    "product_group_by_tags": {
      "terms": {
        "field": "tags"
      }
    }
  }
}

GET /product_index/product/_search
{
  "size": 0, ## 不显示 hits 原数据，只显示聚合统计结果
  "aggs": {
    "product_group_by_tags": {
      "terms": {
        "field": "tags"
      }
    }
  }
}
```

- 聚合得到的结果如下：

``` json
{
  "took": 9,
  "timed_out": false,
  "_shards": {
    "total": 5,
    "successful": 5,
    "failed": 0
  },
  "hits": {
    "total": 3,
    "max_score": 0,
    "hits": []
  },
  "aggregations": {
    "product_group_by_tags": {
      "doc_count_error_upper_bound": 0,
      "sum_other_doc_count": 0,
      "buckets": [
        {
          "key": "toothbrush",
          "doc_count": 2
        },
        {
          "key": "braun",
          "doc_count": 1
        },
        {
          "key": "iphone7",
          "doc_count": 1
        },
        {
          "key": "philips",
          "doc_count": 1
        },
        {
          "key": "phone",
          "doc_count": 1
        },
        {
          "key": "shell",
          "doc_count": 1
        }
      ]
    }
  }
}
```


- 默认情况下你应该会报一个这样的错误

> Fielddata is disabled on text fields by default. Set fielddata=true on [tags] in order to load fielddata in memory by uninverting the inverted index. Note that this can however use significant memory.

- 解决办法，给 tags 字段的 fielddata 设置为 true：

``` json
PUT /product_index/_mapping/product
{
  "properties": {
    "tags": {
      "type": "text",
      "fielddata": true
    }
  }
}
```


### 搜索商品名称中包含 toothbrush 的商品结果中，计算每个 tag 下的商品数量

``` json
GET /product_index/product/_search
{
  "size": 0,
  "query": {
    "match": {
      "product_name": "toothbrush"
    }
  },
  "aggs": {
    "query_product_group_by_tags": {
      "terms": {
        "field": "tags"
      }
    }
  }
}
```


### 搜索商品名称中包含 toothbrush 的商品结果中，先用 tags 字段进行分组，然后再计算每组中商品价格的平均值


``` json
GET /product_index/product/_search
{
  "size": 0,
  "query": {
    "match": {
      "product_name": "toothbrush"
    }
  },
  "aggs": {
    "query_product_group_by_tags_and_avg": {
      "terms": {
        "field": "tags"
      },
      "aggs": {
        "product_price_avg_price": {
          "avg": {
            "field": "price"
          }
        }
      }
    }
  }
}
```


### 搜索商品名称中包含 toothbrush 的商品结果中，先用 tags 字段进行分组，然后再计算每组中商品价格的平均值，并按平均价格进行倒序


``` json
GET /product_index/product/_search
{
  "size": 0,
  "query": {
    "match": {
      "product_name": "toothbrush"
    }
  },
  "aggs": {
    "query_product_group_by_tags_and_avg": {
      "terms": {
        "field": "tags",
        "order": {
          "product_price_avg_price": "desc"
        }
      },
      "aggs": {
        "product_price_avg_price": {
          "avg": {
            "field": "price"
          }
        }
      }
    }
  }
}
```

### 搜索商品名称中包含 toothbrush 的商品结果中，按照指定的价格范围区间进行分组聚合，然后再按 tag 进行分组，最后再计算每组的平均价格

``` json
GET /product_index/product/_search
{
  "size": 0,
  "query": {
    "match": {
      "product_name": "toothbrush"
    }
  },
  "aggs": {
    "proudct_group_by_price": {
      "range": {
        "field": "price",
        "ranges": [
          {
            "from": 0,
            "to": 300
          },
          {
            "from": 300,
            "to": 400
          },
          {
            "from": 400,
            "to": 1000
          }
        ]
      },
      "aggs": {
        "product_group_by_tags": {
          "terms": {
            "field": "tags"
          },
          "aggs": {
            "product_average_price": {
              "avg": {
                "field": "price"
              }
            }
          }
        }
      }
    }
  }
}
```


## 其他资料辅助




