package org.ruoyi.bagent.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ruoyi.bagent.domain.EgoBagent;
import org.ruoyi.common.core.validate.AddGroup;
import org.ruoyi.common.core.validate.EditGroup;
import org.ruoyi.core.domain.BaseEntity;

/**
 * Ego基础智能体 ego_bagent
 *
 * @author jiaxv
 * @date 2025-08-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = EgoBagent.class, reverseConvertGenerate = false)
public class EgoBagentBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 用户id（作者）
     */
    @NotNull(message = "作者不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 智能体名字
     */
    @NotBlank(message = "智能体名字不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 智能体提示词
     */
    @NotBlank(message = "智能体提示词不能为空", groups = { AddGroup.class, EditGroup.class })
    private String prompt;


    /**
     * 类型
     */
    @NotBlank(message = "类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String type;

    /**
     * 状态
     */
    private Boolean isPublic;

}
