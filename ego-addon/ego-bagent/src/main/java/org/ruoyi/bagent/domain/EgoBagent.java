package org.ruoyi.bagent.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ruoyi.core.domain.BaseEntity;

import java.io.Serial;

/**
 * Ego推送 ego_push
 *
 * @author ageerle
 * @date 2025-04-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ego_bagent")
public class EgoBagent extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 用户id（作者）
     */
    private Long userId;

    /**
     * 智能体名字
     */
    private String name;

    /**
     * 智能体prompt
     */
    private String prompt;

    /**
     * 类型
     */
    private int type;

    /**
     * 是否公开
     */
    private Boolean isPublic;

}