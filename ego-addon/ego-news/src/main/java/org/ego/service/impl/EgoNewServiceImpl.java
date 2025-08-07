package org.ego.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.ruoyi.common.core.utils.MapstructUtils;
import org.ruoyi.common.core.utils.StringUtils;
import org.ruoyi.core.page.PageQuery;
import org.ruoyi.core.page.TableDataInfo;
import org.ego.domain.EgoNew;
import org.ego.domain.bo.EgoNewBo;
import org.ego.domain.vo.EgoNewVo;
import org.ego.mapper.EgoNewMapper;
import org.ego.service.IEgoNewService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 新闻Service业务层处理
 *
 * @author jiaxv
 * @date 2025-08-08
 */
@RequiredArgsConstructor
@Service
public class EgoNewServiceImpl implements IEgoNewService {

    private final EgoNewMapper baseMapper;

    /**
     * 查询新闻详情
     */
    @Override
    public EgoNewVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询新闻列表
     */
    @Override
    public TableDataInfo<EgoNewVo> queryPageList(EgoNewBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<EgoNew> lqw = buildQueryWrapper(bo);
        Page<EgoNewVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询新闻列表
     */
    @Override
    public List<EgoNewVo> queryList(EgoNewBo bo) {
        LambdaQueryWrapper<EgoNew> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<EgoNew> buildQueryWrapper(EgoNewBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<EgoNew> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getPublisherId() != null, EgoNew::getPublisherId, bo.getPublisherId());
        lqw.eq(bo.getSessionId() != null, EgoNew::getSessionId, bo.getSessionId());
        lqw.like(StringUtils.isNotBlank(bo.getContent()), EgoNew::getContent, bo.getContent());
        lqw.eq(bo.getPublishTime() != null, EgoNew::getPublishTime, bo.getPublishTime());
        return lqw;
    }

    /**
     * 新增新闻
     */
    @Override
    public Boolean insertByBo(EgoNewBo bo) {
        EgoNew add = MapstructUtils.convert(bo, EgoNew.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改新闻
     */
    @Override
    public Boolean updateByBo(EgoNewBo bo) {
        EgoNew update = MapstructUtils.convert(bo, EgoNew.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(EgoNew entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除新闻
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
