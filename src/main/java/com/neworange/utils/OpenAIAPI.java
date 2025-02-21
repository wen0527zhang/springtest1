package com.neworange.utils;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.experimental.UtilityClass;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*** @author Astar* ClassName:OpenAIAPI.java* date:2023-03-03 16:49* Description:*/
@UtilityClass
public class OpenAIAPI {
    /*** 聊天端点*/
    static final String chatEndpoint = "https://api.openai.com/v1/chat/completions";
    /*** api密匙*/
    static final String apiKey = "Bearer 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";



    /*** 发送消息** @param txt 内容* @return {@link String}*/
    public static String chat(String txt) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("model", "gpt-3.5-turbo-16k");
        List<Map<String, String>> dataList = new ArrayList<>();
        dataList.add(new HashMap<String, String>() {{
            put("role", "user");
            put("content", txt);
        }});
        paramMap.put("messages", dataList);
        JSONObject message = null;
        try {
            String jsonString = JSONObject.toJSONString(paramMap);
            WebClient webClient = WebClient.builder().baseUrl(chatEndpoint).build();
            Mono<String> body = webClient.post().header("Authorization", apiKey).header("Content-Type", "application/json")
                    .body(BodyInserters.fromObject(jsonString)).retrieve().bodyToMono(String.class);

            // String body = HttpRequest.post(chatEndpoint).header("Authorization", apiKey).header("Content-Type", "application/json").body(JSONObject.toJSONString(paramMap)).execute().body();
            JSONObject jsonObject = JSONObject.parseObject(body.block());
            JSONArray choices = jsonObject.getJSONArray("choices");
            JSONObject result = choices.getJSONObject(0);
            //JSONObject result = choices.get(0, JSONObject.class, Boolean.TRUE);
            message = result.getJSONObject("message");
        } catch (Exception e) {
            e.printStackTrace();
            return "出现了异常";
        }
        return (String) message.get("content");
    }

    public static String chat(String key, String txt) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("model", "gpt-3.5-turbo-16k");
        List<Map<String, String>> dataList = new ArrayList<>();
        dataList.add(new HashMap<String, String>() {{
            put("role", "user");
            put("content", txt);
        }});
        paramMap.put("messages", dataList);
        JSONObject message = null;
        try {
            String jsonString = JSONObject.toJSONString(paramMap);
            WebClient webClient = WebClient.builder().baseUrl(chatEndpoint).build();
            Mono<String> body = webClient.post().header("Authorization", "Bearer " + key).header("Content-Type", "application/json")
                    .body(BodyInserters.fromObject(jsonString)).retrieve().bodyToMono(String.class);
           // String body = HttpRequest.post(chatEndpoint).header("Authorization", "Bearer " + key).header("Content-Type", "application/json").body(JSONObject.toJSONString(paramMap)).execute().body();
            JSONObject jsonObject = JSONObject.parseObject(body.block());
            JSONArray choices = jsonObject.getJSONArray("choices");
            JSONObject result = choices.getJSONObject(0);
            //JSONObject result = choices.getObject(0, JSONObject.class, Boolean.TRUE);
            message = result.getJSONObject("message");
        }  catch (Exception e) {
            e.printStackTrace();
            return "出现了异常";
        }
        return (String) message.get("content");
    }

    public static void main(String[] args) {
        System.out.println(chat("Hello，一个小浪吴啊"));
    }
}