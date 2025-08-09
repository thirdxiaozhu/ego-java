package org.ruoyi.push.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ruoyi.common.core.validate.AddGroup;
import org.ruoyi.common.core.validate.EditGroup;
import org.ruoyi.core.domain.BaseEntity;
import org.ruoyi.push.domain.EgoPush;

/**
 * Ego推送业务对象 ego_push
 *
 * @author ageerle
 * @date 2025-04-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = EgoPush.class, reverseConvertGenerate = false)
public class EgoPushBo extends BaseEntity {

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
     * 推送内容（富文本）
     */
    @NotBlank(message = "推送内容不能为空", groups = { AddGroup.class, EditGroup.class })
    private String content;

    /**
     * 推送标题
     */
    @NotBlank(message = "推送标题不能为空", groups = { AddGroup.class, EditGroup.class })
    private String title;

    /**
     * 推送类型
     */
    @NotBlank(message = "推送类型不能为空", groups = { AddGroup.class, EditGroup.class })
    private String type;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;


}
