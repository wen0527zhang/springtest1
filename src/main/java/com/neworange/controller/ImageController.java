package com.neworange.controller;


import com.neworange.es.response.SearchResult;
import com.neworange.es.service.ImageSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author gc.x
 * @date 2023/11/11
 **/
@Slf4j
@RestController
@RequestMapping("/image")
@Tag(name = "以图搜图应用")
public class ImageController {

    @Autowired
    private ImageSearchService imageSearchService;
    @PostMapping("/add")
    @Operation(summary = "添加")
    public void add(String imageId, MultipartFile file, HttpServletRequest request) throws Exception {
         imageSearchService.add(imageId,file);
    }
    @PostMapping("/search")
    @Operation(summary = "查询k个结果")
    public List<SearchResult> search(Integer k, MultipartFile file, HttpServletRequest request) throws Exception {
        return imageSearchService.search(file.getInputStream(),k);
    }
}
