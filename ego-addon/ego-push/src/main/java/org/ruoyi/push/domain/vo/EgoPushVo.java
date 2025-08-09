package org.ruoyi.push.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.ruoyi.push.domain.EgoPush;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;


/**
 * Ego推送消息视图对象 ego_push
 *
 * @author ageerle
 * @date 2025-04-08
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = EgoPush.class)
public class EgoPushVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 用户id（作者）
     */
    @ExcelProperty(value = "作者ID")
    private Long userId;

    /**
     * 推送标题
     */
    @ExcelProperty(value = "推送标题")
    private String title;

    /**
     * 推送内容（富文本）
     */
    @ExcelProperty(value = "推送内容")
    private String content;

    /**
     * 推送类型
     */
    @ExcelProperty(value = "推送类型")
    private String type;

    /**
     * 状态（0正常 1停用）
     */
    @ExcelProperty(value = "状态")
    private String status;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @ExcelProperty(value = "更新时间")
    private Date updateTime;

}
