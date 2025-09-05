package org.ruoyi.bagent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.ruoyi.bagent.domain.EgoBagent;
import org.ruoyi.bagent.domain.bo.EgoBagentBo;
import org.ruoyi.bagent.domain.vo.EgoBagentVo;
import org.ruoyi.bagent.mapper.EgoBagentMapper;
import org.ruoyi.bagent.service.IEgoBagentService;
import org.ruoyi.common.core.utils.MapstructUtils;
import org.ruoyi.common.core.utils.StringUtils;
import org.ruoyi.common.satoken.utils.LoginHelper;
import org.ruoyi.core.page.PageQuery;
import org.ruoyi.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Ego推送Service业务层处理
 *
 * @author ageerle
 * @date 2025-04-08
 */
@RequiredArgsConstructor
@Service
public class EgoBagentServiceImpl implements IEgoBagentService {

    private final EgoBagentMapper baseMapper;

    /**
     * 查询推送消息
     */
    @Override
    public EgoBagentVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询推送消息列表
     */
    @Override
    public TableDataInfo<EgoBagentVo> queryPageList(EgoBagentBo bo, PageQuery pageQuery) {
        if(!LoginHelper.isLogin()){
            return TableDataInfo.build();
        }
        // 只有非管理员才自动设置为自己的 ID
        if (!LoginHelper.isSuperAdmin()) {
            bo.setUserId(LoginHelper.getUserId());
        }
        LambdaQueryWrapper<EgoBagent> lqw = buildQueryWrapper(bo);
        Page<EgoBagentVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询推送消息列表
     */
    @Override
    public List<EgoBagentVo> queryList(EgoBagentBo bo) {
        LambdaQueryWrapper<EgoBagent> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<EgoBagent> buildQueryWrapper(EgoBagentBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<EgoBagent> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getUserId() != null, EgoBagent::getUserId, bo.getUserId());
//        lqw.like(StringUtils.isNotBlank(bo.getTitle()), EgoBagent::getTitle, bo.getTitle());
//        lqw.like(StringUtils.isNotBlank(bo.getContent()), EgoBagent::getContent, bo.getContent());
//        lqw.eq(StringUtils.isNotBlank(bo.getType()), EgoBagent::getType, bo.getType());
//        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), EgoBagent::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增推送消息
     */
    @Override
    public Boolean insertByBo(EgoBagentBo bo) {
        EgoBagent add = MapstructUtils.convert(bo, EgoBagent.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改推送消息
     */
    @Override
    public Boolean updateByBo(EgoBagentBo bo) {
        EgoBagent update = MapstructUtils.convert(bo, EgoBagent.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(EgoBagent entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除推送消息
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}