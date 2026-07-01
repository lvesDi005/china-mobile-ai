package cn.it.chinamobileai.controller;

/**
 * @Description ChatController
 * @Author kight-tom
 * @Date 2026-07-01  10:19
 */

import cn.it.chinamobileai.dto.ChatDTO;
import cn.it.chinamobileai.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public String chat(@RequestBody ChatDTO chatDTO) {
        return chatService.chat(chatDTO.getQuestion(), chatDTO.getSessionId());
    }


    private final VectorStore vectorStore;

    /**
     * 搜索向量数据库
     *
     * @param query 搜索关键字
     */
    @PostMapping("/search")
    public List<Document> search(@RequestParam("query") String query) {
        return vectorStore.similaritySearch(SearchRequest.builder()
                .query(query) // 设置查询条件
                .topK(3) // 设置最多返回的文档数量
                .similarityThreshold(0.5) // 设置相似度阈值要大于这个值才会被搜索出来
                .build());
    }
}