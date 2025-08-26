package org.ruoyi.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.ruoyi.domain.ImageRecord;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ImageRecord.class)
public class ImageRecordVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 请求id
     */
    @ExcelProperty(value = "请求id")
    private String requestId;

    /**
     * 用户id
     */
    @ExcelProperty(value = "用户id")
    private Long userId;

    /**
     * 消息内容
     */
    @ExcelProperty(value = "提示词")
    private String prompt;

    /**
     * url
     */
    @ExcelProperty(value = "url")
    private String url;

    /**
     * base64
     */
    @ExcelProperty(value = "base64")
    private String base64;


    /**
     * 扣除金额
     */
    @ExcelProperty(value = "扣除金额")
    private BigDecimal deductCost;

    /**
     * 模型名称
     */
    @ExcelProperty(value = "模型名称")
    private String modelName;

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

}
