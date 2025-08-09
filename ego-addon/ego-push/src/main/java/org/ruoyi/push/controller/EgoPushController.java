package org.ruoyi.push.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.ruoyi.common.core.domain.R;
import org.ruoyi.common.core.validate.AddGroup;
import org.ruoyi.common.core.validate.EditGroup;
import org.ruoyi.common.excel.utils.ExcelUtil;
import org.ruoyi.common.idempotent.annotation.RepeatSubmit;
import org.ruoyi.common.log.annotation.Log;
import org.ruoyi.common.log.enums.BusinessType;
import org.ruoyi.common.web.core.BaseController;
import org.ruoyi.core.page.PageQuery;
import org.ruoyi.core.page.TableDataInfo;
import org.ruoyi.push.domain.bo.EgoPushBo;
import org.ruoyi.push.domain.vo.EgoPushVo;
import org.ruoyi.push.service.IEgoPushService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Ego推送消息
 *
 * @author ageerle
 * @date 2025-04-08
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/ego/push")
public class EgoPushController extends BaseController {

    private final IEgoPushService egoPushService;

    /**
     * 查询推送消息列表
     */
    @GetMapping("/list")
    public TableDataInfo<EgoPushVo> list(EgoPushBo bo, PageQuery pageQuery) {
        return egoPushService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出推送消息列表
     */
    @Log(title = "推送消息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(EgoPushBo bo, HttpServletResponse response) {
        List<EgoPushVo> list = egoPushService.queryList(bo);
        ExcelUtil.exportExcel(list, "推送消息", EgoPushVo.class, response);
    }

    /**
     * 获取推送消息详细信息
     *
     * @param id 主键
     */
    @GetMapping("/{id}")
    public R<EgoPushVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(egoPushService.queryById(id));
    }


    /**
     * 新增推送消息
     */
    @Log(title = "推送消息", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Long> add(@Validated(AddGroup.class) @RequestBody EgoPushBo bo) {
        egoPushService.insertByBo(bo);
        return R.ok(bo.getId());
    }

    /**
     * 修改推送消息
     */
    @Log(title = "推送消息", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody EgoPushBo bo) {
        return toAjax(egoPushService.updateByBo(bo));
    }

    /**
     * 删除推送消息
     *
     * @param ids 主键串
     */
    @Log(title = "推送消息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(egoPushService.deleteWithValidByIds(List.of(ids), true));
    }
}