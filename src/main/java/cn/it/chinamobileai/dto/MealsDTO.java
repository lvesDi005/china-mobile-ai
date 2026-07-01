package cn.it.chinamobileai.dto;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description MealsDTO
 * @Author kight-tom
 * @Date 2026-07-01  16:56
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealsDTO {

    @JsonPropertyDescription("套餐名称")
    private String mealsName;

    @JsonPropertyDescription("月租费用")
    private String monthlyRent;

    @JsonPropertyDescription("套餐流量")
    private String dataFlow;

    @JsonPropertyDescription("通话时长")
    private String callDuration;

    @JsonPropertyDescription("附加服务内容")
    private String extraService;

    @JsonPropertyDescription("合约期限")
    private String contractPeriod;

    @JsonPropertyDescription("对应优惠活动")
    private String discountActivity;
}