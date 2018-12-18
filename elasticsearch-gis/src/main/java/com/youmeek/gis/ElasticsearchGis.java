package com.youmeek.gis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ElasticsearchGis {

	private static final Logger logger = LogManager.getLogger(ElasticsearchGis.class);


	@SuppressWarnings({"unchecked", "resource"})
	public static void main(String[] args) throws Exception {
		// 先构建client，两个参数分别是：cluster.name 固定参数代表后面参数的含义，集群名称
		// client.transport.sniff 表示设置自动探查集群的集群节点
		Settings settings = Settings.builder()
				.put("cluster.name", "youmeek-cluster")
				.put("client.transport.sniff", true)
				.build();

		//单个节点的写法
		TransportClient transportClient = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.1.127"), 9300));

		//batchCreate(transportClient);
		query(transportClient);

		transportClient.close();
	}

	/**
	 * 批量创建
	 * 资料：http://blog.csdn.net/loveisnull/article/details/45914115
	 * 纬度在前，经度在后
	 *
	 * @param transportClient
	 */
	private static void batchCreate(TransportClient transportClient) throws IOException {


		BulkRequestBuilder bulkRequestBuilder = transportClient.prepareBulk();

		IndexRequestBuilder indexRequestBuilder1 = transportClient.prepareIndex("shop_index", "shop", "1")
				.setSource(XContentFactory.jsonBuilder()
						.startObject()
						.field("shop_name", "麻辣香锅1")
						.latlon("location", 40.12, -71.34)
						.field("created_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
						.field("last_modified_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
						.field("version", 1)
						.endObject());

		IndexRequestBuilder indexRequestBuilder2 = transportClient.prepareIndex("shop_index", "shop", "2")
				.setSource(XContentFactory.jsonBuilder()
						.startObject()
						.field("shop_name", "麻辣香锅2")
						.latlon("location", 40.12, -72.34)
						.field("created_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
						.field("last_modified_date_time", new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSSZ").format(new Date()))
						.field("version", 1)
						.endObject());

		IndexRequestBuilder indexRequestBuilder3 = transportClient.prepareIndex("shop_index", "shop", "3")
				.setSource(XContentFactory.jsonBuilder()
						.startObject()
						.field("shop_name", "麻辣香锅3")
						.latlon("location", 40.12, -73.34)
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
	 * 查询
	 * 资料：资料：http://blog.csdn.net/mdjros/article/details/71747679
	 *
	 * @param transportClient
	 * @throws IOException
	 */
	private static void query(TransportClient transportClient) throws IOException {

		//搜索两个坐标点的区域
		SearchResponse searchResponse1 = transportClient.prepareSearch("shop_index").setTypes("shop")
				.setQuery(QueryBuilders.geoBoundingBoxQuery("location").setCorners(40.73, -74.1, 40.01, -71.12))
				.get();

		for (SearchHit searchHit : searchResponse1.getHits().getHits()) {
			logger.info(searchHit.getSourceAsString());
		}

		logger.info("=======================================================================================");

		//由三个坐标组成一个区域
		List<GeoPoint> points = new ArrayList<GeoPoint>();
		points.add(new GeoPoint(40.73, -74.1));
		points.add(new GeoPoint(40.01, -71.12));
		points.add(new GeoPoint(50.56, -90.58));

		SearchResponse searchResponse2 = transportClient.prepareSearch("shop_index").setTypes("shop")
				.setQuery(QueryBuilders.geoPolygonQuery("location", points))
				.get();

		for (SearchHit searchHit : searchResponse2.getHits().getHits()) {
			logger.info(searchHit.getSourceAsString());
		}

		logger.info("=======================================================================================");

		//搜索离当前坐标位置12公里内的店铺
		SearchResponse searchResponse3 = transportClient.prepareSearch("shop_index").setTypes("shop")
				.setQuery(QueryBuilders.geoDistanceQuery("location").point(40, -70).distance(12, DistanceUnit.KILOMETERS))
				.get();

		for (SearchHit searchHit : searchResponse3.getHits().getHits()) {
			logger.info(searchHit.getSourceAsString());
		}
	}

}
