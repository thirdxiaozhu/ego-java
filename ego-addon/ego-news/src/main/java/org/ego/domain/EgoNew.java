package org.ego.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.EqualsAndHashCode;
import org.ruoyi.core.domain.BaseEntity;

import java.io.Serial;
import java.util.Date;

/**
 * 新闻对象
 *
 * @author jiaxv
 * @date 2025-8-8
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ego_new")
public class EgoNew extends BaseEntity{
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 发布者id
     */
    private Long publisherId;

    /**
     * 新闻id
     */
    private Long sessionId;

    /**
     * 发布内容
     */
    private String content;

    /**
     * 发布时间
     */
    private Date publishTime;
}