# 博文内容

- 博文标题：1.1 Elasticsearch 介绍 + CentOS 7 下安装部署
- 博文地址：<http://www.youmeek.com/elasticsearch-introduction-and-install/>


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


## Elasticsearch 场景

- 只要用到搜索功能的地方都可以
- 日志数据分析
- BI、大数据系统（听说业界有 PB 级别的应用）
- 数据分析
- NoSQL 数据库


## Elasticsearch 经历

- 搜索系统无处不在，对于一般程序员来讲，更多的是自己业务系统内的搜索。
- 传统数据库的搜索功能，在数据量大的情况下性能很差，所以必须要有一个搜索功能来替代这个。
- 过去大家的方案是：Solr，现在大家的方案是：Elasticsearch。后者比前者新，能力更强，更符合现在时代。
- 不管是 Solr 或是 Elasticsearch 底层都是：Lucene（比较复杂、底层）
- Lucene 的根本：全文检索、倒排索引


## Elasticsearch 优点

- 自带高可用，有冗余副本
- 自带分布式，支持分片，可分配到多机上，所以数据很大也扛得住
- 封装了很多高级功能，方便我们调用


## Elasticsearch 核心概念

- 官网介绍：<https://www.elastic.co/guide/en/elasticsearch/reference/5.2/_basic_concepts.html#_near_realtime_nrt>
- Near Realtime（NRT）：近实时（Elasticsearch 是有小延迟的，一般为 1 秒，一般情况是感受不到这个延迟的）。
- Cluster：集群，可以一个或多个节点，这些节点共同保存这个集群的数据。集群有一个名称，很重要，各个节点的配置就需要用到这个名称，节点是用集群名称加入到集群中的。理论上单节点是最优方案，可惜只适合小数据量。对于各个环节的集群名称建议这样命名：youmeek-dev、youmeek-prod、youmeek-test
- Node：节点，归属集群。如果整个集群就一个节点，那这个节点也就是这个集群本身。节点也有名称（默认是在启动的时候随机分配的 UUID），节点名称也可以自定义，一般都建议自定义，节点名称很重要编译运维中进行管理。
- **Index：索引，类似数据库结构中的库，是一堆 document 的集合。索引名称必须全部是小写，不能用下划线开头，不能包含逗号，推荐格式：youmeek_index**。
- **Type：类型，类似数据库结构中的表**。虽然现在一个 Index 可以有多个 Type，但是正在开发的 Elasticsearch 6 打算废弃这个特性了，一个 Index 只能有一个 Type，具体看：<https://elasticsearch.cn/article/158> 
- **Document：文档，Elasticsearch 中的最小数据单元，类似数据库结构中的一行数据**。所以一行数据中也会有多个 field 也就是字段。Document 通常用 JSON 格式来表示。
- Shard：分片。全称 primary shards（一般用在写操作）。Elasticsearch 可以将一个 Index 中的 Document 数据切分为多个 shard，分布在多台服务器上存储。每个 shard 都是一个 Lucene index，最多能有 Document 这么多（官网原文）：the limit is 2147483519 (Integer.MAX_VALUE - 128) documents。
- Replica：副本。全称 replica shards（一般读操作可以被分配到进行使用）。Replica 主要用来保证高可用（故障转移）、数据备份、增强高吞吐的并行搜索。
- 假设我们现在有 2 台机子，要做一个这样的环境：2 台机子当做 2 个 Node 组成 1 个 Cluster。现在创建 1 个 Index，默认会有 5 个 primary shards（创建的时候也可以指定其他数量，创建完成后是不可修改的，因为跟数据存储时候的路由功能有关系，具体可以看：[路由字段资料](https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-routing-field.html)、[自定义路由字段](https://www.elastic.co/blog/customizing-your-document-routing)）。5 个 primary shards 分片各自有 1 个 Replica 副本分片用作备份，则这两台机子分片最终结果是：有 5 个 primary shards，有 5 个 replica shards 与之对应。
- 一般最小的 **高可用配置**，是 2 台服务器。而一般推荐是 5 台机子，最好是奇数台机子。
- 如果是 5 台机子可以这样规划：两台节点作为 master ，这两个节点都是作为 commander 统筹集群层面的事务，取消这两台的 data 权利。然后在规划出三个节点的 data 集群，取消这三个节点的 master 权利。让他们安心的做好数据存储和检索服务。这样做的好处，就是职责分明，可以最大限度的防止 master 节点有事 data 节点，导致不稳定因素发生。比如 data 节点的数据复制，数据平衡，路由等等，直接影响 master 的稳定性。进而可能会发生脑裂问题。


## 其他资料辅助

- 基础
	- [Elasticsearch 学习，请先看这一篇](http://blog.csdn.net/laoyang360/article/details/52244917)
	- [ElasticSearch 大数据分布式弹性搜索引擎使用—从 0 到 1](https://my.oschina.net/learnbo/blog/775458)
	- [Elasticsearch 学习笔记](https://geosmart.github.io/2016/07/22/Elasticsearch%E5%AD%A6%E4%B9%A0%E7%AC%94%E8%AE%B0/)
	- [Elasticsearch 集群部署](https://geosmart.github.io/2016/07/23/Elasticsearch%E9%9B%86%E7%BE%A4%E9%83%A8%E7%BD%B2/)
	- [Elasticsearch Java API 深入详解](http://blog.csdn.net/laoyang360/article/details/72793210)
- 中级
	- [聊聊基于 Lucene 的搜索引擎核心技术实践](http://www.weidu8.net/wx/1019149606257294)
	- [亿级规模的 Elasticsearch 优化实战](http://mp.weixin.qq.com/s?__biz=MzAwMDU1MTE1OQ==&mid=209488723&idx=1&sn=d60c0637d7a9f4a4b981a69f10c6b90a)
	- [时下最火搜索引擎：ElasticSearch 详解与优化设计](http://mp.weixin.qq.com/s?__biz=MzI4NTA1MDEwNg==&mid=401883509&idx=1&sn=a6de3b6307db8ffc8802c4abfdc0313d&scene=2&srcid=0105aJnDy9nu932SxMPsZ9yW&from=timeline&isappinstalled=0#wechat_redirect)
	- [Elasticsearch: 权威指南（中文版）](https://www.elastic.co/guide/cn/elasticsearch/guide/current/index.html)
	- [一张图理清楚关系型 / 非关系型数据库与 Elasticsearch 同步](http://blog.csdn.net/laoyang360/article/details/72792865)
	- [解剖 Elasticsearch 集群 - 之一](http://www.cnblogs.com/richaaaard/p/6273916.html)
	- [Richaaaard Elasticsearch 相关博文](http://www.cnblogs.com/richaaaard/category/783901.html)



