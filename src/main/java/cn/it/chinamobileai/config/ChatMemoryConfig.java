package cn.it.chinamobileai.config;

/**
 * @Description ChatMemory
 * @Author kight-tom
 * @Date 2026-07-01  10:58
 */

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 聊天记忆配置 —— 创建并返回聊天记忆管理器的Spring Bean（基于内存实现）
 */
@Configuration
public class ChatMemoryConfig {

    /**
     * 创建并返回基于内存实现的聊天记忆管理器。
     *
     * @return InMemoryChatMemory 实例，用于存储聊天上下文信息
     */
    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }
}
