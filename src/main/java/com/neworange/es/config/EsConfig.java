package com.neworange.es.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Data
@Component
@Configuration
@ConfigurationProperties(prefix = "es")
public class EsConfig {
	public static final RequestOptions COMMON_OPTIONS;

	public static final String IMAGE_SEARCH_INDEX = "image_search_index";

	private String[] address;

	private String username;

	private String password;

	// 通用设置项
	static {
		RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
		COMMON_OPTIONS = builder.build();
	}
	/**
	 * 初始化es 索引
	 * 创建索引库和映射表结构
	 * 注意：索引一般不会怎么创建
	 */
	@Bean
	public RestHighLevelClient restHighLevelClient() {
		RestHighLevelClient restHighLevelClient = null;
		try {
			HttpHost[] httpHosts = Arrays.stream(address).map(HttpHost::create).toArray(HttpHost[]::new);
			if(StringUtils.isEmpty(username)||StringUtils.isEmpty(password)){
				restHighLevelClient = new RestHighLevelClient(
						RestClient.builder(httpHosts));
			}else {
				// 创建用户名和密码的凭据提供者
				BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
				credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
				restHighLevelClient = new RestHighLevelClient(
						RestClient.builder(httpHosts)
								.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
										.setDefaultCredentialsProvider(credentialsProvider)));
			}
		} catch (Exception e) {
			log.error("初始化es连接失败：{}", e.getMessage());
		}

		try {
			if (restHighLevelClient != null) {
				IndicesClient indicesClient = restHighLevelClient.indices();
				// 创建get请求
				GetIndexRequest doctorIndexGet = new GetIndexRequest(IMAGE_SEARCH_INDEX);
				// 判断索引库是否存在，不存在则创建
				if (!indicesClient.exists(doctorIndexGet, RequestOptions.DEFAULT)) {
					CreateIndexRequest doctorIndex = new CreateIndexRequest(IMAGE_SEARCH_INDEX);
					//指定映射
					doctorIndex.settings(Settings.builder()
							.put("index.number_of_shards", 3)
							.put("index.number_of_replicas", 2)
					);
					doctorIndex.mapping("{\n" +
							"    \"properties\": {\n" +
							"      \"vector\": {\n" +
							"        \"type\": \"dense_vector\",\n" +
							"        \"dims\": 1024\n" +
							"      },\n" +
							"      \"url\" : {\n" +
							"        \"type\" : \"keyword\"\n" +
							"      },\n" +
							"      \"imageId\": {\n" +
							"          \"type\": \"keyword\"\n" +
							"      }\n" +
							"    }\n" +
							"  }\n", XContentType.JSON);
					indicesClient.create(doctorIndex, RequestOptions.DEFAULT);
				}
			}
		} catch (Exception e) {
			log.warn("初始化es索引失败：{}", e.getMessage());
		}
		return restHighLevelClient;
	}
}
