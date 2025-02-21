package com.neworange.es.service;

import ai.djl.ModelException;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.translate.TranslateException;
import ai.onnxruntime.OrtException;
import com.alibaba.fastjson2.JSONObject;
import com.neworange.es.config.EsConfig;
import com.neworange.es.entity.ImageSearch;
import com.neworange.es.response.SearchResult;
import com.neworange.utils.FileUtil;
import com.neworange.utils.ImageFeatureUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@Slf4j
public class ImageSearchService {
    private final RestHighLevelClient restHighLevelClient;

    private final FileUtil fileUtil;

    public ImageSearchService(RestHighLevelClient restHighLevelClient, FileUtil fileUtil) {
        this.restHighLevelClient = restHighLevelClient;
        this.fileUtil = fileUtil;
    }

    public void add(String imageId, MultipartFile file) throws IOException, ModelException, TranslateException, OrtException {
        List<ImageSearch> imageSearchList = new ArrayList<>();
        ImageSearch search = new ImageSearch();
        String path = fileUtil.getPath(file);
        search.setImageId(imageId);
        search.setUrl(path);
        imageSearchList.add(search);
        batchAdd(imageSearchList);
    }

    //图片及展示k个结果
    public List<SearchResult> search(InputStream input, int k) throws ModelException, TranslateException, IOException, OrtException {
        Image image = ImageFactory.getInstance().fromInputStream(input);
        float[] vector = ImageFeatureUtil.runOcr(image);
        log.info(Arrays.toString(vector));
        SearchRequest searchRequest = new SearchRequest(EsConfig.IMAGE_SEARCH_INDEX);
        Script script = new Script(ScriptType.INLINE, "painless",
                "cosineSimilarity(params.queryVector, doc['vector'])",
                Collections.singletonMap("queryVector", vector));
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(
                QueryBuilders.matchAllQuery(),
                ScoreFunctionBuilders.scriptFunction(script));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(functionScoreQueryBuilder)
                .fetchSource(null, "vector") //不返回vector字段，太多了没用还耗时
                .size(k);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        List<SearchResult> list = new ArrayList<>();
        for (SearchHit hit : hits) {
            // 处理搜索结果
            log.info("处理搜索结果----->{}", hit.toString());
            SearchResult result = new SearchResult((String) hit.getSourceAsMap().get("url"), (String) hit.getSourceAsMap().get("imageId"), hit.getScore());
            list.add(result);
        }
        return list;
    }

    private void batchAdd(List<ImageSearch> imageSearchList) throws IOException, ModelException, TranslateException, OrtException {
        //批量上传请求
        BulkRequest bulkRequest = new BulkRequest(EsConfig.IMAGE_SEARCH_INDEX);
        for (ImageSearch imageSearch : imageSearchList) {
            // 构建文档
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("url", imageSearch.getUrl());
            jsonMap.put("vector", ImageFeatureUtil.runOcr(imageSearch.getUrl()));
            jsonMap.put("imageId", imageSearch.getImageId());
            log.info("上传图片信息----->{}", JSONObject.toJSONString(jsonMap));
            IndexRequest request = new IndexRequest(EsConfig.IMAGE_SEARCH_INDEX).source(jsonMap, XContentType.JSON);
            bulkRequest.add(request);
        }
        restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }
}
