package cn.it.chinamobileai.service.impl;

/**
 * @Description ChatService
 * @Author kight-tom
 * @Date 2026-07-01  10:10
 */

import cn.hutool.core.date.DateUtil;
import cn.it.chinamobileai.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Override
    public String chat(String question, String sessionId) {
        // 创建搜索请求，用于搜索相关文档
        SearchRequest searchRequest = SearchRequest.builder()
                .query(question) // 设置查询条件
                .topK(3) // 设置最多返回的文档数量
                .similarityThreshold(0.5) // 设置相似度阈值要大于这个值才会被搜索出来
                .build();

        // 调用聊天客户端处理用户问题并获取响应内容
        String content = chatClient.prompt()
                // .system(Constant.SYSTEM_ROLE) // 设置系统角色
                .system(prompt -> prompt.param("now", DateUtil.now())) // 设置系统角色参数
                // 设置会话记忆参数
                .advisors(advisor -> advisor
                        .advisors(new QuestionAnswerAdvisor(vectorStore, searchRequest)) // 设置RAG的Advisor
                        .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, sessionId))
                .user(question)
                .call()
                .content();
        log.info("question: {}, content: {}", question, content);
        return content;
    }
}