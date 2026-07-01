package cn.it.chinamobileai.service;

import reactor.core.publisher.Flux;

/**
 * @Description ChatService
 * @Author kight-tom
 * @Date 2026-07-01  10:10
 */

public interface ChatService {

    /**
     * 普通聊天
     *
     * @param question 用户提问
     * @return 大模型的回答
     */
    // String chat(String question);
    String chat(String question, String sessionId);

}