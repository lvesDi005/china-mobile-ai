package cn.it.chinamobileai.config;

/**
 * @Description SpringAIConfig
 * @Author kight-tom
 * @Date 2026-07-01  10:09
 */


import cn.it.chinamobileai.constants.Constant;
import cn.it.chinamobileai.constants.SensitiveConstant;
import cn.it.chinamobileai.tool.MealTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringAIConfig {

    /**
     * 创建并返回一个SafeGuardAdvisor的Spring Bean实例。
     * 用于敏感词拦截，用户输入包含敏感词时立即返回拦截响应，不调用大模型。
     *
     * @return SafeGuardAdvisor 实例
     */
    @Bean
    public SafeGuardAdvisor safeGuardAdvisor() {
        return SafeGuardAdvisor.builder()
                .sensitiveWords(SensitiveConstant.SENSITIVE_WORDS)
                .build();
    }

    /**
     * 创建并返回一个PromptChatMemoryAdvisor的Spring Bean实例。
     * 用于管理聊天记忆，将历史对话以文本形式拼接到系统提示中。
     *
     * @param chatMemory 聊天记忆存储器
     * @return PromptChatMemoryAdvisor 实例
     */
    @Bean
    public PromptChatMemoryAdvisor promptChatMemoryAdvisor(ChatMemory chatMemory) {
        return new PromptChatMemoryAdvisor(chatMemory);
    }

    /**
     * 创建并返回一个SimpleLoggerAdvisor的Spring Bean实例。
     * 用于记录AI对话的请求与响应日志。
     */
    @Bean
    public SimpleLoggerAdvisor simpleLoggerAdvisor() {
        return new SimpleLoggerAdvisor();
    }

    /**
     * 创建并返回一个VectorStore的Spring Bean实例。
     *
     * @param embeddingModel 向量模型
     */
    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    /**
     * 创建并返回一个ChatClient的Spring Bean实例。
     *
     * @param builder 用于构建ChatClient实例的构建者对象
     * @return 构建好的ChatClient实例
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder,
                                 Advisor simpleLoggerAdvisor,
                                 //Advisor messageChatMemoryAdvisor,
                                 Advisor promptChatMemoryAdvisor,
                                 Advisor safeGuardAdvisor,
                                 MealTools mealTools
    ) {
        return builder
                .defaultSystem(Constant.SYSTEM_ROLE) // 设置默认的系统角色
                .defaultAdvisors(simpleLoggerAdvisor, promptChatMemoryAdvisor, safeGuardAdvisor) // 设置默认的Advisor
                .defaultTools(mealTools) // 设置默认的Tool
                .build();
    }
}