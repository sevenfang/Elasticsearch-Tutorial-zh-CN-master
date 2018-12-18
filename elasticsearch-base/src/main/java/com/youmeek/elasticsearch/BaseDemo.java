package com.youmeek.elasticsearch;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequestBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BaseDemo {
	private static final Logger logger = LogManager.getLogger(BaseDemo.class);

	@SuppressWarnings({"unchecked", "resource"})
	public static void main(String[] args) throws IOException {
		// 先构建client，两个参数分别是：cluster.name 固定参数代表后面参数的含义，集群名称
		// client.transport.sniff 表示设置自动探查集群的集群节点
		Settings settings = Settings.builder()
				.put("cluster.name", "youmeek-cluster")
				.put("client.transport.sniff", true)
				.build();

		//单个节点的写法
		TransportClient transportClient = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.1.127"), 9300));

		//======================================================

		create(transportClient);
		batchCreate(transportClient);
		batchUpdate(transportClient);
		batchDelete(transportClient);
		update(transportClient);
		query(transportClient);
		queryByMatchOneParam(transportClient);
		queryByMatchMoreParam(transportClient);
		queryByTerm(transportClient);
		queryByPrefix(transportClient);
		queryByBool(transportClient);
		queryMore(transportClient);
		queryByMultiGet(transportClient);
		queryByScroll(transportClient);
		queryByTemplate(transportClient);
		delete(transportClient);
		aggregate(transportClient);

		//======================================================

		transportClient.close();
	}


	/**
	 * 创建
	 *
	 * @param transportClient
	 */
	private static void create(TransportClient transportClient) throws IOException {
		IndexResponse indexResponse = transportClient.prepareIndex("product_index", "product", "1").setSource(XContentFactory.jsonBuilder()
				.startObject()
				.field("product_name", "飞利浦电动牙刷 HX6700-1")
				.field("product_desc", "前 1000 名赠刷头，6 月 1 日 0 点火爆开抢，618 开门红巅峰 48 小时")
				.field("price", 399.00)
				.field("created_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
				.field("last_modified_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
				.field("version", 1)
				.endObject()).get();

		IndexResponse indexResponse2 = transportClient.prepareIndex("product_index", "product", "2").setSource(XContentFactory.jsonBuilder()
				.startObject()
				.field("product_name", "飞利浦电动牙刷 HX6700-2")
				.field("product_desc", "前 1000 名赠刷头，6 月 1 日 0 点火爆开抢，618 开门红巅峰 48 小时")
				.field("price", 399.00)
				.field("created_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
				.field("last_modified_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
				.field("version", 1)
				.endObject()).get();

		IndexResponse indexResponse3 = transportClient.prepareIndex("product_index", "product", "3").setSource(XContentFactory.jsonBuilder()
				.startObject()
				.field("product_name", "飞利浦电动牙刷 HX6700-3")
				.field("product_desc", "前 1000 名赠刷头，6 月 1 日 0 点火爆开抢，618 开门红巅峰 48 小时")
				.field("price", 399.00)
				.field("created_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
				.field("last_modified_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
				.field("version", 1)
				.endObject()).get();

		IndexResponse indexResponse4 = transportClient.prepareIndex("product_index", "product", "4").setSource(XContentFactory.jsonBuilder()
				.startObject()
				.field("product_name", "飞利浦电动牙刷 HX6700-4")
				.field("product_desc", "前 1000 名赠刷头，6 月 1 日 0 点火爆开抢，618 开门红巅峰 48 小时")
				.field("price", 399.00)
				.field("created_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
				.field("last_modified_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
				.field("version", 1)
				.endObject()).get();

		IndexResponse indexResponse5 = transportClient.prepareIndex("product_index", "product", "5").setSource(XContentFactory.jsonBuilder()
				.startObject()
				.field("product_name", "飞利浦电动牙刷 HX6700-5")
				.field("product_desc", "前 1000 名赠刷头，6 月 1 日 0 点火爆开抢，618 开门红巅峰 48 小时")
				.field("price", 399.00)
				.field("created_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
				.field("last_modified_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
				.field("version", 1)
				.endObject()).get();
	}

	/**
	 * 批量创建
	 *
	 * @param transportClient
	 */
	private static void batchCreate(TransportClient transportClient) throws IOException {
		BulkRequestBuilder bulkRequestBuilder = transportClient.prepareBulk();

		IndexRequestBuilder indexRequestBuilder1 = transportClient.prepareIndex("product_index", "product", "1")
				.setSource(XContentFactory.jsonBuilder()
						.startObject()
						.field("product_name", "飞利浦电动牙刷 HX6700-1")
						.field("product_desc", "前 1000 名赠刷头，6 月 1 日 0 点火爆开抢，618 开门红巅峰 48 小时")
						.field("price", 399.00)
						.field("created_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
						.field("last_modified_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
						.field("version", 1)
						.endObject());

		IndexRequestBuilder indexRequestBuilder2 = transportClient.prepareIndex("product_index", "product", "2")
				.setSource(XContentFactory.jsonBuilder()
						.startObject()
						.field("product_name", "飞利浦电动牙刷 HX6700-2")
						.field("product_desc", "前 1000 名赠刷头，6 月 1 日 0 点火爆开抢，618 开门红巅峰 48 小时")
						.field("price", 399.00)
						.field("created_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
						.field("last_modified_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
						.field("version", 1)
						.endObject());

		IndexRequestBuilder indexRequestBuilder3 = transportClient.prepareIndex("product_index", "product", "3")
				.setSource(XContentFactory.jsonBuilder()
						.startObject()
						.field("product_name", "飞利浦电动牙刷 HX6700-3")
						.field("product_desc", "前 1000 名赠刷头，6 月 1 日 0 点火爆开抢，618 开门红巅峰 48 小时")
						.field("price", 399.00)
						.field("created_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
						.field("last_modified_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
						.field("version", 1)
						.endObject());


		bulkRequestBuilder.add(indexRequestBuilder1);
		bulkRequestBuilder.add(indexRequestBuilder2);
		bulkRequestBuilder.add(indexRequestBuilder3);

		BulkResponse bulkResponse = bulkRequestBuilder.get();
		for (BulkItemResponse bulkItemResponse : bulkResponse.getItems()) {
			logger.info("--------------------------------version= " + bulkItemResponse.getVersion());
		}
	}

	/**
	 * 批量更新
	 *
	 * @param transportClient
	 */
	private static void batchUpdate(TransportClient transportClient) throws IOException {
		BulkRequestBuilder bulkRequestBuilder = transportClient.prepareBulk();

		UpdateRequestBuilder updateRequestBuilder1 = transportClient.prepareUpdate("product_index", "product", "1")
				.setDoc(XContentFactory.jsonBuilder()
						.startObject()
						.field("product_name", "更新后的商品名称1")
						.endObject());

		UpdateRequestBuilder updateRequestBuilder2 = transportClient.prepareUpdate("product_index", "product", "2")
				.setDoc(XContentFactory.jsonBuilder()
						.startObject()
						.field("product_name", "更新后的商品名称2")
						.endObject());

		UpdateRequestBuilder updateRequestBuilder3 = transportClient.prepareUpdate("product_index", "product", "3")
				.setDoc(XContentFactory.jsonBuilder()
						.startObject()
						.field("product_name", "更新后的商品名称3")
						.endObject());


		bulkRequestBuilder.add(updateRequestBuilder1);
		bulkRequestBuilder.add(updateRequestBuilder2);
		bulkRequestBuilder.add(updateRequestBuilder3);

		BulkResponse bulkResponse = bulkRequestBuilder.get();
		for (BulkItemResponse bulkItemResponse : bulkResponse.getItems()) {
			logger.info("--------------------------------version= " + bulkItemResponse.getVersion());
		}
	}

	/**
	 * 批量删除
	 *
	 * @param transportClient
	 */
	private static void batchDelete(TransportClient transportClient) throws IOException {
		BulkRequestBuilder bulkRequestBuilder = transportClient.prepareBulk();

		DeleteRequestBuilder deleteRequestBuilder1 = transportClient.prepareDelete("product_index", "product", "1");
		DeleteRequestBuilder deleteRequestBuilder2 = transportClient.prepareDelete("product_index", "product", "2");
		DeleteRequestBuilder deleteRequestBuilder3 = transportClient.prepareDelete("product_index", "product", "3");

		bulkRequestBuilder.add(deleteRequestBuilder1);
		bulkRequestBuilder.add(deleteRequestBuilder2);
		bulkRequestBuilder.add(deleteRequestBuilder3);

		BulkResponse bulkResponse = bulkRequestBuilder.get();
		for (BulkItemResponse bulkItemResponse : bulkResponse.getItems()) {
			logger.info("--------------------------------version= " + bulkItemResponse.getVersion());
		}

	}

	/**
	 * 获取单个对象（ID）
	 *
	 * @param transportClient
	 * @throws IOException
	 */
	private static void query(TransportClient transportClient) throws IOException {
		GetResponse getResponse = transportClient.prepareGet("product_index", "product", "1").get();
		logger.info("--------------------------------：" + getResponse.getSourceAsString());
	}

	/**
	 * 查询 match 单个字段
	 *
	 * @param transportClient
	 * @throws IOException
	 */
	private static void queryByMatchOneParam(TransportClient transportClient) throws IOException {
		SearchResponse searchResponse = transportClient.prepareSearch("product_index").setTypes("product")
				.setQuery(QueryBuilders.matchQuery("product_name", "飞利浦"))
				.get();

		for (SearchHit searchHit : searchResponse.getHits().getHits()) {
			logger.info("--------------------------------：" + searchHit.getSourceAsString());
		}
	}

	/**
	 * 查询 match 多个字段
	 *
	 * @param transportClient
	 * @throws IOException
	 */
	private static void queryByMatchMoreParam(TransportClient transportClient) throws IOException {
		SearchResponse searchResponse = transportClient.prepareSearch("product_index").setTypes("product")
				.setQuery(QueryBuilders.multiMatchQuery("飞利浦", "product_name", "product_desc"))
				.get();

		for (SearchHit searchHit : searchResponse.getHits().getHits()) {
			logger.info("--------------------------------：" + searchHit.getSourceAsString());
		}
	}

	/**
	 * 查询 term
	 *
	 * @param transportClient
	 * @throws IOException
	 */
	private static void queryByTerm(TransportClient transportClient) throws IOException {
		SearchResponse searchResponse = transportClient.prepareSearch("product_index").setTypes("product")
				.setQuery(QueryBuilders.termQuery("product_name.keyword", "飞利浦"))
				.get();

		for (SearchHit searchHit : searchResponse.getHits().getHits()) {
			logger.info("--------------------------------：" + searchHit.getSourceAsString());
		}
	}

	/**
	 * 查询 prefix
	 *
	 * @param transportClient
	 * @throws IOException
	 */
	private static void queryByPrefix(TransportClient transportClient) throws IOException {
		SearchResponse searchResponse = transportClient.prepareSearch("product_index").setTypes("product")
				.setQuery(QueryBuilders.prefixQuery("product_name", "飞利"))
				.get();

		for (SearchHit searchHit : searchResponse.getHits().getHits()) {
			logger.info("--------------------------------：" + searchHit.getSourceAsString());
		}
	}

	/**
	 * 查询 bool
	 *
	 * @param transportClient
	 * @throws IOException
	 */
	private static void queryByBool(TransportClient transportClient) throws IOException {
		QueryBuilder queryBuilder = QueryBuilders.boolQuery()
				.must(QueryBuilders.matchQuery("product_name", "飞利浦"))
				.should(QueryBuilders.rangeQuery("created_date_time").gte("2017-01-01").lte("2017-12-31"))
				.filter(QueryBuilders.rangeQuery("price").gte(150).lte(400));

		SearchResponse searchResponse = transportClient.prepareSearch("product_index").setTypes("product")
				.setQuery(queryBuilder)
				.get();

		for (SearchHit searchHit : searchResponse.getHits().getHits()) {
			logger.info("--------------------------------：" + searchHit.getSourceAsString());
		}
	}

	/**
	 * 获取多个对象（根据ID）
	 *
	 * @param transportClient
	 * @throws IOException
	 */
	private static void queryByMultiGet(TransportClient transportClient) throws IOException {

		MultiGetResponse multiGetItemResponses = transportClient.prepareMultiGet()
				.add("product_index", "product", "1")
				.add("product_index", "product", "2")
				.add("product_index", "product", "3")
				.add("product_index", "product", "4")
				.add("product_index", "product", "5")
				.get();

		String resultJSON = null;
		for (MultiGetItemResponse multiGetItemResponse : multiGetItemResponses) {
			GetResponse getResponse = multiGetItemResponse.getResponse();
			if (getResponse.isExists()) {
				resultJSON = getResponse.getSourceAsString();
			}
		}
		logger.info("--------------------------------：" + resultJSON);
	}

	/**
	 * Scroll 获取多个对象
	 *
	 * @param transportClient
	 * @throws IOException
	 */
	private static void queryByScroll(TransportClient transportClient) throws IOException {

		//setSize 是设置每批查询多少条数据
		SearchResponse searchResponse = transportClient.prepareSearch("product_index").setTypes("product")
				.setQuery(QueryBuilders.termQuery("product_name", "飞利浦"))
				.setScroll(new TimeValue(60000))
				.setSize(3)
				.get();

		int count = 0;

		do {
			for (SearchHit searchHit : searchResponse.getHits().getHits()) {
				//打印查询结果，或者做其他处理
				logger.info("count=" + ++count);
				logger.info(searchHit.getSourceAsString());
			}

			searchResponse = transportClient.prepareSearchScroll(searchResponse.getScrollId()).setScroll(new TimeValue(60000))
					.execute()
					.actionGet();
		} while (searchResponse.getHits().getHits().length != 0);
	}

	/**
	 * 通过模板文件进行查询
	 *
	 * @param transportClient
	 * @throws IOException
	 */
	private static void queryByTemplate(TransportClient transportClient) throws IOException {
		//模板文件需要放在：Elasticsearch 安装目录下的：config/scripts 目录下，比如我有一个：query_template_1.mustache
		//传递传输到模板中，参数是按顺序的，模板内容如下。
		/*
		{
			"from": {{from}},
			"size": {{size}},
			"query": {
				"match": {
					"product_name": "{{product_name}}"
				}
			}
		}
		*/

		Map<String, Object> scriptParams = new HashMap<String, Object>();
		scriptParams.put("from", 0);
		scriptParams.put("size", 10);
		scriptParams.put("product_name", "飞利浦");

		SearchResponse searchResponse = new SearchTemplateRequestBuilder(transportClient)
				.setScript("query_template_1")
				.setScriptType(ScriptType.FILE)
				.setScriptParams(scriptParams)
				.setRequest(new SearchRequest("product_index").types("product"))
				.get()
				.getResponse();

		for (SearchHit searchHit : searchResponse.getHits().getHits()) {
			logger.info("--------------------------------：" + searchHit.getSourceAsString());
		}
	}

	/**
	 * 修改
	 *
	 * @param transportClient
	 * @throws IOException
	 */
	private static void update(TransportClient transportClient) throws IOException {
		UpdateResponse updateResponse = transportClient.prepareUpdate("product_index", "product", "1")
				.setDoc(XContentFactory.jsonBuilder()
						.startObject()
						.field("product_name", "飞利浦电动牙刷 HX6700 促销优惠")
						.endObject())
				.get();
		logger.info("--------------------------------：" + updateResponse.getResult());

	}

	/**
	 * 删除
	 *
	 * @param transportClient
	 * @throws IOException
	 */
	private static void delete(TransportClient transportClient) throws IOException {
		DeleteResponse deleteResponse = transportClient.prepareDelete("product_index", "product", "1").get();
		logger.info("--------------------------------：" + deleteResponse.getResult());
	}

	//============================================================================================================

	/**
	 * 多个条件查询
	 *
	 * @param transportClient
	 * @throws IOException
	 */
	private static void queryMore(TransportClient transportClient) throws IOException {
		SearchResponse searchResponse = transportClient.prepareSearch("product_index").setTypes("product")
				.setQuery(QueryBuilders.matchQuery("product_name", "飞利浦"))
				.setPostFilter(QueryBuilders.rangeQuery("price").from(300).to(400))
				.setFrom(0).setSize(1)
				.get();

		SearchHit[] searchHits = searchResponse.getHits().getHits();
		for (int i = 0; i < searchHits.length; i++) {
			logger.info("--------------------------------：" + searchHits[i].getSourceAsString());
		}
	}

	//============================================================================================================

	/**
	 * 聚合分析
	 * 1. 先分组
	 * 2. 子分组
	 * 3. 最后算出子分组的平均值
	 *
	 * @param transportClient
	 * @throws IOException
	 */
	private static void aggregate(TransportClient transportClient) throws IOException {

		SearchResponse searchResponse = transportClient.prepareSearch("product_index").setTypes("product")
				.addAggregation(AggregationBuilders.terms("product_group_by_price").field("price")
						.subAggregation(AggregationBuilders.dateHistogram("product_group_by_created_date_time").field("created_date_time")
								.dateHistogramInterval(DateHistogramInterval.YEAR)
								.subAggregation(AggregationBuilders.avg("product_avg_price").field("price")))
				).execute().actionGet();

		Map<String, Aggregation> aggregationMap = searchResponse.getAggregations().asMap();

		StringTerms productGroupByPrice = (StringTerms) aggregationMap.get("product_group_by_price");
		Iterator<Terms.Bucket> productGroupByPriceIterator = productGroupByPrice.getBuckets().iterator();
		while (productGroupByPriceIterator.hasNext()) {
			Terms.Bucket productGroupByPriceBucket = productGroupByPriceIterator.next();
			logger.info("--------------------------------：" + productGroupByPriceBucket.getKey() + ":" + productGroupByPriceBucket.getDocCount());

			Histogram productGroupByPrice1 = (Histogram) productGroupByPriceBucket.getAggregations().asMap().get("product_group_by_price");
			Iterator<org.elasticsearch.search.aggregations.bucket.histogram.Histogram.Bucket> groupByCreateDateTimeIterator = productGroupByPrice1.getBuckets().iterator();
			while (groupByCreateDateTimeIterator.hasNext()) {
				Histogram.Bucket groupByCreateDateTimeBucket = groupByCreateDateTimeIterator.next();
				logger.info("--------------------------------：" + groupByCreateDateTimeBucket.getKey() + ":" + groupByCreateDateTimeBucket.getDocCount());

				Avg avgPrice = (Avg) groupByCreateDateTimeBucket.getAggregations().asMap().get("product_avg_price");
				logger.info("--------------------------------：" + avgPrice.getValue());
			}
		}


	}


}
