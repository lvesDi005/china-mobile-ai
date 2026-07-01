package cn.it.chinamobileai.dto;

/**
 * @Description ChatDTO
 * @Author kight-tom
 * @Date 2026-07-01  11:38
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatDTO {

    /**
     * 用户的问题
     */
    private String question;

    /**
     * 会话id
     */
    private String sessionId;

}