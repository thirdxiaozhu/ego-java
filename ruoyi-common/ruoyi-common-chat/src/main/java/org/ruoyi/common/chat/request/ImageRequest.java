package org.ruoyi.common.chat.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ImageRequest {

    @NotEmpty(message = "传入的模型不能为空")

    private String model;
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 会话id
     */
    private Long sessionId;

    /**
     * 提示词
     */
    private String prompt;

    /**
     * 反向提示词
     */
    private String negativePrompt;

    /**
     * 生成数量
     */
    private int n;

    /**
     * 图片大小
     */
    private String size;

    /**
     * 随机种子
     */
    private int seed;

}
