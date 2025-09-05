package org.ruoyi.bagent.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.ruoyi.bagent.domain.bo.EgoBagentBo;
import org.ruoyi.bagent.domain.vo.EgoBagentVo;
import org.ruoyi.bagent.service.IEgoBagentService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Ego基础智能体
 *
 * @author jiaxv
 * @date 2025-08-31
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/ego/bagent")
public class EgoBagentController extends BaseController {

    private final IEgoBagentService EgoBagentService;

    /**
     * 查询推送消息列表
     */
    @GetMapping("/list")
    public TableDataInfo<EgoBagentVo> list(EgoBagentBo bo, PageQuery pageQuery) {
        return EgoBagentService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出推送消息列表
     */
    @Log(title = "推送消息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(EgoBagentBo bo, HttpServletResponse response) {
        List<EgoBagentVo> list = EgoBagentService.queryList(bo);
        ExcelUtil.exportExcel(list, "推送消息", EgoBagentVo.class, response);
    }

    /**
     * 获取推送消息详细信息
     *
     * @param id 主键
     */
    @GetMapping("/{id}")
    public R<EgoBagentVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(EgoBagentService.queryById(id));
    }


    /**
     * 新增推送消息
     */
    @Log(title = "推送消息", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Long> add(@Validated(AddGroup.class) @RequestBody EgoBagentBo bo) {
        EgoBagentService.insertByBo(bo);
        return R.ok(bo.getId());
    }

    /**
     * 修改推送消息
     */
    @Log(title = "推送消息", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody EgoBagentBo bo) {
        return toAjax(EgoBagentService.updateByBo(bo));
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
        return toAjax(EgoBagentService.deleteWithValidByIds(List.of(ids), true));
    }
}