package com.neworange.meilisearch;

import com.alibaba.fastjson2.JSONArray;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.model.Searchable;

/**
 * @author winter
 * @version 1.0.0
 * @ date 2024/12/28 18:30
 * @ description
 */
public class TestMeilisearch {

    public static void main(String[] args) {

        JSONArray array = new JSONArray();
        //Faker faker = new Faker();
//        for (int i = 110000; i <150000; i++) {
//            JSONObject jsonObject1 = new JSONObject();
//            jsonObject1.put("id",i);
//            jsonObject1.put("name", PersonInfoSource.getInstance().randomChineseName());
//            jsonObject1.put("nickName",PersonInfoSource.getInstance().randomChineseNickName(8));
//            jsonObject1.put("card",  PersonInfoSource.getInstance().randomMaleIdCard("北京", 18));
//            jsonObject1.put("phone",PersonInfoSource.getInstance().randomChineseMobile());
//            jsonObject1.put("date", DateTimeSource.getInstance().randomDate(2024, "yyyy-MM-dd"));
//            jsonObject1.put("address", AreaSource.getInstance().randomAddress());
//            jsonObject1.put("college", EducationSource.getInstance().randomCollege());
//            jsonObject1.put("info", OtherSource.getInstance().randomChineseSentence());
//            jsonObject1.put("qq",PersonInfoSource.getInstance().randomQQAccount());
//            jsonObject1.put("plateNo", OtherSource.getInstance().randomPlateNumber());
//            JSONObject geo = new JSONObject();
//            geo.put("lat", AreaSource.getInstance().randomLatitude());
//            geo.put("lng", AreaSource.getInstance().randomLongitude());
//            jsonObject1.put("_geo",geo);
//            array.add(jsonObject1);
//        }

        //array.put(items);
        String documents = array.toString();
//        System.out.println(documents);
        Client client = new Client(new Config("http://10.8.0.42:7700", "xincheng191213"));
//        Index index = client.index("person");
//        index.addDocuments(documents);
//        Settings settings = new Settings();
//        settings.setFilterableAttributes(new String[] {"_geo"});
//        client.index("person").updateSettings(settings);
//        client.index("person").updateSortableAttributesSettings(new String[] {"date"});
        SearchRequest searchRequest = SearchRequest.builder().q("").sort(new String[] {"date:desc"}).filter(new String[] {"_geoRadius(43.572735, 87.354019, 200000)"}).build();
        final Searchable search = client.index("person").search(searchRequest);
        System.out.println(search.getProcessingTimeMs());
        search.getHits().forEach(System.out::println);
        // An index is where the documents are stored.
//        Index index = client.index("person");

        // If the index 'movies' does not exist, Meilisearch creates it when you first add the documents.
//        index.addDocuments(documents); // => { "taskUid": 0 }
//        SearchResult results = index.search("carlo");
//        System.out.println(results);
//        Searchable results = index.search(
//                new SearchRequest("of")
//                        .setShowMatchesPosition(true)
//                        .setAttributesToHighlight(new String[]{"title"})
//        );
//        System.out.println(results.getHits());

        //System.out.println(results.getHits());

    }


//    @Autowired
//    private Client meiliclient;

    /**
     * 从Meilisearch中搜索文章
     *
     * @param query 关键字
     * @param page  页码
     * @param size  条数
     * @param sort  排序规则，如：id:desc
     * @return 文章列表
     */
//    public void searchArticles(String query, Integer page, Integer size, String sort) {
//        long start = System.currentTimeMillis();
//        SearchResult searchResult = null;
//        try {
//            //指定索引
//            Index index = client.getIndex("articles");
//            //封装查询请求
//            SearchRequest searchRequest = new SearchRequest();
//            // 关键字
//            searchRequest.setQ(query);
//            //游标，跟mysql的limit查询一样传参
//            searchRequest.setOffset(size * (page - 1));
//            //每页查询的条数
//            searchRequest.setLimit(size);
//            // 排序
//            searchRequest.setSort(new String[]{sort});
//            // 指定高亮的字段
//            searchRequest.setAttributesToHighlight(new String[]{"title", "tags", "text"});
//            //发送查询请求
//            searchResult = index.search(searchRequest);
//        } catch (Exception e) {
//            log.info("Meilisearch查询文章失败: {}", ExceptionUtils.getStackTrace(e));
//        }
//        log.info("Meilisearch查询文章总耗时: {}s", (System.currentTimeMillis() - start) / 1000f);
//        if (searchResult == null) {
//            return null;
//        }
//        MeilisearchResult result = new MeilisearchResult<>();
//        //预估搜索的文档数，可以当作总条数来用，但需注意它不是一个精准的值
//        int nbHits = searchResult.getNbHits();
//        result.setTotal(nbHits % size == 0 ? nbHits / size : nbHits / size + 1);
//        //hits就是查询结果集，它是一个hashmap集合
//        result.setItems(transferArticlesFromJson(searchResult.getHits()));
//        return result;
//    }

}
