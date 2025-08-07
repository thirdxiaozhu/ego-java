package org.ego.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.ego.domain.EgoNew;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 新闻视图对象 ego-new
 *
 * @author jiaxv
 * @date 2025-08-08
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = EgoNew.class)
public class EgoNewVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 发布者id
     */
    @ExcelProperty(value = "发布者id")
    private Long publisherId;

    /**
     * 新闻id
     */
    private Long sessionId;

    /**
     * 发布内容
     */
    @ExcelProperty(value = "发布内容")
    private String content;

    /**
     * 发布时间
     */
    @ExcelProperty(value = "发布时间")
    private Date publishTime;
}
