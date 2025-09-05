package org.ruoyi.bagent.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.ruoyi.bagent.domain.EgoBagent;

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
@AutoMapper(target = EgoBagent.class)
public class EgoBagentVo implements Serializable {

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
    @ExcelProperty(value = "用户ID")
    private Long userId;

    /**
     * 智能体名字
     */
    @ExcelProperty(value = "智能体名字")
    private String name;

    /**
     *智能体提示词
     */
    @ExcelProperty(value = "智能体提示词")
    private String prompt;

    /**
     * 类型
     */
    @ExcelProperty(value = "类型")
    private int type;

    /**
     * 是否公有
     */
    @ExcelProperty(value = "是否公有")
    private Boolean isPublic;

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
