package org.ruoyi.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ruoyi.common.core.validate.AddGroup;
import org.ruoyi.common.core.validate.EditGroup;
import org.ruoyi.core.domain.BaseEntity;
import org.ruoyi.domain.ImageRecord;


@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ImageRecord.class, reverseConvertGenerate = false)
@Builder
public class ImageRecordBo extends BaseEntity {
    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 请求id
     */
    @NotNull(message = "请求id不能为空", groups = { AddGroup.class, EditGroup.class })
    private String requestId;

    /**
     * 用户id
     */
    @NotNull(message = "用户id不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 提示词
     */
    @NotBlank(message = "提示词不能为空", groups = { AddGroup.class, EditGroup.class })
    private String prompt;

   /**
     * url
     */
    @NotBlank(message = "url不能为空", groups = { AddGroup.class, EditGroup.class })
    private String url;

    /**
     * url
     */
    @NotBlank(message = "base64不能为空", groups = { AddGroup.class, EditGroup.class })
    private String base64;


    /**
     * 扣除次数
     */
    @NotNull(message = "扣除次数不能为空", groups = { AddGroup.class, EditGroup.class })
    private Double deductCost;

    /**
     * 模型名称
     */
    @NotBlank(message = "模型名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String modelName;

    /**
     * 备注
     */
    @NotBlank(message = "备注不能为空", groups = { AddGroup.class, EditGroup.class })
    private String remark;

}
