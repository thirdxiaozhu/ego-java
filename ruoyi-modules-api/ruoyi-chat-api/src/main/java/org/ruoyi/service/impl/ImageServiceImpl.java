package org.ruoyi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.ruoyi.common.core.utils.MapstructUtils;
import org.ruoyi.common.core.utils.StringUtils;
import org.ruoyi.common.satoken.utils.LoginHelper;
import org.ruoyi.core.page.PageQuery;
import org.ruoyi.core.page.TableDataInfo;
import org.ruoyi.domain.ImageRecord;
import org.ruoyi.domain.bo.ImageRecordBo;
import org.ruoyi.domain.vo.ImageRecordVo;
import org.ruoyi.mapper.ImageRecordMapper;
import org.ruoyi.service.IImageService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ImageServiceImpl implements IImageService {
    private final ImageRecordMapper baseMapper;

    /**
     * 查询聊天消息
     */
    @Override
    public ImageRecordVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询聊天消息列表
     */
    @Override
    public TableDataInfo<ImageRecordVo> queryPageList(ImageRecordBo bo, PageQuery pageQuery) {
        if(!LoginHelper.isLogin()){
            return TableDataInfo.build();
        }
        // 只有非管理员才自动设置为自己的 ID
        if (!LoginHelper.isSuperAdmin()) {
            bo.setUserId(LoginHelper.getUserId());
        }
        LambdaQueryWrapper<ImageRecord> lqw = buildQueryWrapper(bo);
        Page<ImageRecordVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询聊天消息列表
     */
    @Override
    public List<ImageRecordVo> queryList(ImageRecordBo bo) {
        LambdaQueryWrapper<ImageRecord> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ImageRecord> buildQueryWrapper(ImageRecordBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ImageRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getUserId() != null, ImageRecord::getUserId, bo.getUserId());
        lqw.like(StringUtils.isNotBlank(bo.getModelName()), ImageRecord::getModelName, bo.getModelName());
        return lqw;
    }

    /**
     * 新增聊天消息
     */
    @Override
    public Boolean insertByBo(ImageRecordBo bo) {
        ImageRecord add = MapstructUtils.convert(bo, ImageRecord.class);
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
    public Boolean updateByBo(ImageRecordBo bo) {
        ImageRecord update = MapstructUtils.convert(bo, ImageRecord.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ImageRecord entity){
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
