package cn.it.chinamobileai.embedding;

/**
 * @Description MealsEmbedding
 * @Author kight-tom
 * @Date 2026-07-01  15:52
 */

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 移动套餐信息向量化处理组件
 * 在应用启动时将套餐数据文件meals.txt转换为向量并存储到向量数据库
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MealsEmbedding {

    /**
     * 向量存储服务，用于持久化套餐文档向量
     */
    private final VectorStore vectorStore;

    /**
     * 套餐数据文件资源路径（classpath:meals.txt）
     */
    @Value("classpath:meals.txt")
    private Resource resource;

    /**
     * 应用启动时初始化方法
     * 1. 读取套餐数据文件
     * 2. 拆分文本为小块文档
     * 3. 将拆分后的套餐文档向量化并存储
     */
    @PostConstruct
    public void init() throws Exception {
        // 1. 读取文件完整文本
        TextReader textReader = new TextReader(resource);
        String fullText = textReader.read().toString();
        textReader.getCustomMetadata().put("filename", "meals.txt");
        Map<String, Object> meta = textReader.getCustomMetadata();

        // 2. 按换行分割，每行一条套餐，去除空行
        List<String> lineList = Arrays.stream(fullText.split("\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .toList();

        //分割

        // 3. 每行单独构建Document
        List<Document> documentList = lineList.stream()
                .map(line -> new Document(line, meta))
                .toList();

        // 4. 分片器（单条套餐很短，分割器仅兜底，不会合并多条套餐）
        TextSplitter textSplitter = new TokenTextSplitter(200, 10, 20, 10000, false);
        List<Document> splitDocuments = textSplitter.apply(documentList);

        // 5. 入库
        vectorStore.add(splitDocuments);
        log.info("套餐数据写入向量库成功，数据条数：{}", splitDocuments.size());
    }
}