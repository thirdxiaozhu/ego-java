package org.ego.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ego.domain.EgoNew;
import org.ruoyi.common.core.validate.AddGroup;
import org.ruoyi.common.core.validate.EditGroup;
import org.ruoyi.core.domain.BaseEntity;

import java.util.Date;

/**
 * 新闻业务对象 ego-new
 *
 * @author jiaxv
 * @date 2025-08-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = EgoNew.class, reverseConvertGenerate = false)
public class EgoNewBo extends BaseEntity {
    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 发布者id
     */
    @NotNull(message = "发布者id不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long publisherId;

    /**
     * 新闻id
     */
    @NotNull(message = "新闻id不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long sessionId;

    /**
     * 内容
     */
    private String content;

    /**
     * 发布时间
     */
    @NotNull(message = "发布时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Date publishTime;


}
