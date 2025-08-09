package org.ruoyi.push.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.ruoyi.common.core.utils.MapstructUtils;
import org.ruoyi.common.core.utils.StringUtils;
import org.ruoyi.common.satoken.utils.LoginHelper;
import org.ruoyi.core.page.PageQuery;
import org.ruoyi.core.page.TableDataInfo;
import org.ruoyi.push.domain.EgoPush;
import org.ruoyi.push.domain.bo.EgoPushBo;
import org.ruoyi.push.domain.vo.EgoPushVo;
import org.ruoyi.push.mapper.EgoPushMapper;
import org.ruoyi.push.service.IEgoPushService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 聊天消息Service业务层处理
 *
 * @author ageerle
 * @date 2025-04-08
 */
@RequiredArgsConstructor
@Service
public class EgoPushServiceImpl implements IEgoPushService {

    private final EgoPushMapper baseMapper;

    /**
     * 查询聊天消息
     */
    @Override
    public EgoPushVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询聊天消息列表
     */
    @Override
    public TableDataInfo<EgoPushVo> queryPageList(EgoPushBo bo, PageQuery pageQuery) {
        if(!LoginHelper.isLogin()){
            return TableDataInfo.build();
        }
        // 只有非管理员才自动设置为自己的 ID
        if (!LoginHelper.isSuperAdmin()) {
            bo.setUserId(LoginHelper.getUserId());
        }
        LambdaQueryWrapper<EgoPush> lqw = buildQueryWrapper(bo);
        Page<EgoPushVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询聊天消息列表
     */
    @Override
    public List<EgoPushVo> queryList(EgoPushBo bo) {
        LambdaQueryWrapper<EgoPush> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<EgoPush> buildQueryWrapper(EgoPushBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<EgoPush> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getUserId() != null, EgoPush::getUserId, bo.getUserId());
        lqw.eq(StringUtils.isNotBlank(bo.getContent()), EgoPush::getContent, bo.getContent());
        lqw.eq(bo.getSessionId() != null, EgoPush::getSessionId, bo.getSessionId());
        lqw.like(StringUtils.isNotBlank(bo.getRole()), EgoPush::getRole, bo.getRole());
        lqw.like(StringUtils.isNotBlank(bo.getModelName()), EgoPush::getModelName, bo.getModelName());
        return lqw;
    }

    /**
     * 新增聊天消息
     */
    @Override
    public Boolean insertByBo(EgoPushBo bo) {
        EgoPush add = MapstructUtils.convert(bo, EgoPush.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改聊天消息
     */
    @Override
    public Boolean updateByBo(EgoPushBo bo) {
        EgoPush update = MapstructUtils.convert(bo, EgoPush.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(EgoPush entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除聊天消息
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
