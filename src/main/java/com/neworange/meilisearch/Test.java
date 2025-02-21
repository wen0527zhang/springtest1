package com.neworange.meilisearch;

import com.alibaba.fastjson2.JSON;
import com.apifan.common.random.source.PersonInfoSource;
import com.github.javafaker.Faker;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.model.Task;
import com.meilisearch.sdk.model.TaskInfo;
import com.neworange.entity.Movies;
import com.neworange.isapi.utils.CommonMethod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author winter
 * @version 1.0.0
 * @ date 2024/12/30 16:36
 * @ description
 */
public class Test {
    private static Client client = new Client(new Config("http://10.8.0.42:7700", "xincheng191213"));

    /**
     * enqueued：任务已收到，即将处理
     * processing：任务正在处理中
     * Succeeded：任务处理成功
     * failed：处理任务时失败。未对数据库进行任何更改
     * canceled：任务已取消
     * 当任务队列达到其限制（大约 10GiB）时，它将引发错误。用户需要使用 delete tasks 端点删除任务才能继续写入操作。no_space_left_on_device
     * Meilisearch 认为某些任务是高优先级的，并始终将它们放在队列的前面。
     * 以下类型的任务始终会尽快处理：
     * taskCancelation
     * taskDeletion
     * snapshotCreation
     * dumpCreation
     *
     * @param args
     */
    public static void main(String[] args) {

//        System.out.println(faker.number().randomDouble(1, 1, 5));
//        System.out.println(faker.avatar().image());
        //Meilisearch 中的大多数数据库操作都是异步的。API 请求不是立即处理，而是添加到队列中并一次处理一个。
        //查看任务状态
//        final Task task = client.getTask(0);
//        System.out.println(JSON.toJSONString(task));
//         SearchResult search = client.index("movies").search("Renoir");
//        List<HashMap<String, Object>> hits = search.getHits();
//        System.out.println(JSON.toJSONString(search));
        //hitsPerPage 每页结果数    limit单个查询返回的最大文档数
//        SearchRequest searchRequest = SearchRequest.builder().q("*").limit(2).hitsPerPage(15).build();
//        final Searchable search1 = client.index("movies").search(searchRequest);
//        System.out.println(JSON.toJSONString(search1));
//        final Task task = client.getTask(116);
//        System.out.println(JSON.toJSONString(task));
//        TasksQuery query = new TasksQuery().setStatuses(new String[] {"failed", "canceled"});
//        TasksResults tasks = client.getTasks(query);

//        TasksQuery query =
//                new TasksQuery()
//                        .setStatuses(new String[] {"processing"})
//                        .setTypes(new String[] {"documentAdditionOrUpdate", "documentDeletion"})
//                        .setIndexUids(new String[] {"movies"});
//        TasksQuery query = new TasksQuery()
//                .setLimit(2)
//                .setFrom(10);
//        final TasksResults tasks = client.index("movies").getTasks(query);
//        System.out.println(JSON.toJSONString(tasks));
            //增加过滤条件
//         TaskInfo movies = client.index("movies").updateFilterableAttributesSettings(new String[]{"genres", "name"});
//        System.out.println(JSON.toJSONString(movies));
//
//         Task task = client.getTask(117);
//        System.out.println(JSON.toJSONString(task));
//        SearchRequest searchRequest = SearchRequest.builder().q("Jazz").filter(new String[] {"name = '齐复付'"}).build();
//        final Searchable search = client.index("movies").search(searchRequest);
//        System.out.println(JSON.toJSONString(search));
//        addDate();
//        final TaskInfo taskInfo = client.index("movie_ratings").updateFilterableAttributesSettings(new String[]{"genres", "director", "language"});
//        System.out.println(JSON.toJSONString(taskInfo));
//        SearchRequest searchRequest = SearchRequest.builder().q("classic").facets(new String[]
//                {
//                        "genres",
//
//                        "language"
//                }).build();
//        Faceting newFaceting = new Faceting();
//        HashMap<String, FacetSortValue> facetSortValues = new HashMap<>();
//        facetSortValues.put("genres", FacetSortValue.COUNT);
//        newFaceting.setSortFacetValuesBy(facetSortValues);
//        final Searchable search = client.index("books").search(searchRequest);
//        System.out.println(JSON.toJSONString(search));
        // 加入筛选条件
//        final TaskInfo taskInfo = client.index("movies1").updateFilterableAttributesSettings(new String[]{"release_date"});
        //按时间排序
//        Settings settings = new Settings();
//        settings.setSortableAttributes(new String[] {"release_date"});
//        final TaskInfo taskInfo = client.index("movies1").updateSettings(settings);
//        System.out.println(JSON.toJSONString(taskInfo));
//        final Task task = client.getTask(126);
//        System.out.println(JSON.toJSONString(task));
//        SearchRequest searchRequest = SearchRequest.builder().q("").filter(new String[] {"release_date >= 1514761200 AND release_date < 1672527600"}).build();
//        SearchRequest searchRequest = SearchRequest.builder().q("").sort(new String[] {"release_date:desc"}).build();
//        final Searchable search = client.index("movies1").search(searchRequest);
//        System.out.println(JSON.toJSONString(search));
//         TaskInfo taskInfo = client.index("books").updateSortableAttributesSettings(new String[]{"rating", "author"});
//         System.out.println(JSON.toJSONString(taskInfo));
//        Settings settings = new Settings();
//        settings.setRankingRules(new String[]
//                {
//                        "words",
//                        "sort",
//                        "typo",
//                        "proximity",
//                        "attribute",
//                        "exactness"
//                });
//        final TaskInfo taskInfo = client.index("books").updateSettings(settings);
//        SearchRequest searchRequest = SearchRequest.builder().q("Roald Dahl").sort(new String[] {"author:asc"}).build();
//        final Searchable search = client.index("books").search(searchRequest);
//        System.out.println(JSON.toJSONString(search));
//        getTask(taskInfo.getTaskUid());

        final String health = client.health();
        System.out.println(health);
    }
    private static void getTask(int id) {
        final Task task1 = client.getTask(id);
        System.out.println(JSON.toJSONString(task1));
    }

    private static void createIndex() {
        TaskInfo movies = client.createIndex("movies", null);
        System.out.println(JSON.toJSONString(movies));
    }
    private static void deleteIndex() {
        TaskInfo movies = client.deleteIndex("movies");
        System.out.println(JSON.toJSONString(movies));
    }

    private static void addDate(){
         String resFileAbsPath = CommonMethod.getResFileAbsPath("json/books.json");
        Path fileName= Path.of(resFileAbsPath);
        try {
             String  moviesJson = Files.readString(fileName);
             Index movies = client.index("books");
             TaskInfo taskInfo = movies.addDocuments(moviesJson);
            System.out.println(JSON.toJSONString(taskInfo));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static void addMovies() {
        Faker faker = new Faker(new Locale("zh", "CN"));
        List<Movies> moviesList = new ArrayList<>();
        for (int i = 700000; i < 1000000; i++) {
            Movies movies1 = new Movies();
            movies1.setId(i);
            movies1.setTitle(faker.artist().name());
            double v = faker.number().randomDouble(1, 1, 5);
            movies1.setPrice(v);
            List<String> strings = new ArrayList<>();
            strings.add(faker.music().genre());
            strings.add(faker.music().genre());
            movies1.setGenres(strings);
            movies1.setStartTime(new java.util.Date());
            movies1.setName(PersonInfoSource.getInstance().randomChineseName());
            moviesList.add(movies1);
        }
        String jsonString = JSON.toJSONString(moviesList);
        final TaskInfo[] movies = client.index("movies").addDocumentsInBatches(jsonString, 10000, null);
        for (TaskInfo movie : movies) {
            System.out.println(JSON.toJSONString(movie));
        }

    }
}
