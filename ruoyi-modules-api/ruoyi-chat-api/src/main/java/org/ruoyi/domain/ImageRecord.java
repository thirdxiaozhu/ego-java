package org.ruoyi.domain;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ruoyi.core.domain.BaseEntity;

import java.io.Serial;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("image_record")
public class ImageRecord extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 请求id
     */
    private String requestId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 提示词
     */
    private String prompt;

    /**
     * url
     */
    private String url;

    /**
     * base64
     */
    private String base64;

    /**
     * 扣除次数
     */
    private BigDecimal deductCost;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 备注
     */
    private String remark;

}
