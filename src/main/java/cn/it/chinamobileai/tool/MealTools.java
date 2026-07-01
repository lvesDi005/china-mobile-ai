package cn.it.chinamobileai.tool;
/**
 * @Description MealTools 移动套餐查询工具
 * @Author kight-tom
 * @Date 2026-07-01  11:58
 */

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.text.StrSplitter;
import cn.it.chinamobileai.dto.MealsDTO;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class MealTools {

    // 构造注入向量库
    private final VectorStore vectorStore;
    public MealTools(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Value("classpath:meals.txt")
    private Resource mealsResource;

    // 缓存：套餐名称 -> 完整行文本，项目启动加载一次，避免重复读文件
    private final Map<String, String> mealCache = new ConcurrentHashMap<>();

    /**
     * 项目启动一次性加载全部套餐到内存缓存
     */
    @PostConstruct
    public void loadMealCache() {
        // 修复点：ClassPathResource不能转File，改用流读取
        try (InputStream inputStream = mealsResource.getInputStream()) {
            String fileContent = IoUtil.read(inputStream, StandardCharsets.UTF_8);
            List<String> lineList = StrSplitter.split(fileContent, "\n", true, true);
            mealCache.clear();
            for (String line : lineList) {
                String[] splitArr = line.split(":", 2);
                String name = splitArr[0].trim();
                mealCache.put(name, line);
            }
        } catch (Exception e) {
            throw new RuntimeException("读取meals.txt资源失败，请检查文件是否存在于resources目录", e);
        }
    }

    @Tool(description = "根据用户套餐需求描述，语义检索匹配的多个套餐，返回结构化套餐列表")
    public List<MealsDTO> searchMealByText(@ToolParam(description = "用户套餐需求描述，如：50元以内校园流量卡、带宽带家庭套餐") String queryText) {
        // 向量相似度检索，取Top3最匹配套餐
        SearchRequest searchRequest = SearchRequest.builder()
                .query(queryText)
                .topK(3)
                .filterExpression("filename == 'meals.txt'")
                .build();
        List<String> matchTextList = vectorStore.similaritySearch(searchRequest)
                .stream()
                .map(Document::getText)
                .collect(Collectors.toList());

        // 批量解析文本为DTO，自动去重
        return matchTextList.stream()
                .map(line -> {
                    String name = line.split(":",2)[0].trim();
                    return getMealDetail(name);
                })
                .filter(dto -> dto != null)
                .distinct()
                .collect(Collectors.toList());
    }

    @Tool(description = "根据套餐完整精确名称，查询结构化套餐详情信息")
    public MealsDTO getMealDetail(@ToolParam(description = "套餐完整名称，如：经济卡、畅享全家享") String mealsName) {
        // 从内存缓存快速获取，不再重复读取文件
        String targetLine = mealCache.get(mealsName.trim());
        if (targetLine == null) {
            return null;
        }

        // 拆分套餐字段：套餐名:月租,流量,通话时长,附加服务,合约期,优惠活动
        String[] nameAndInfo = targetLine.split(":", 2);
        String[] infoArr = nameAndInfo[1].split(",");

        return MealsDTO.builder()
                .mealsName(nameAndInfo[0].trim())
                .monthlyRent(infoArr[0].trim())     // 月租费用
                .dataFlow(infoArr[1].trim())         // 套餐流量
                .callDuration(infoArr[2].trim())     // 通话时长
                .extraService(infoArr[3].trim())     // 附加服务
                .contractPeriod(infoArr[4].trim())   // 合约期限
                .discountActivity(infoArr[5].trim()) // 优惠活动
                .build();
    }
}