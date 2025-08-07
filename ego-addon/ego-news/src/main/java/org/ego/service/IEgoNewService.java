package org.ego.service;

import org.ruoyi.core.page.PageQuery;
import org.ruoyi.core.page.TableDataInfo;
import org.ego.domain.bo.EgoNewBo;
import org.ego.domain.vo.EgoNewVo;

import java.util.Collection;
import java.util.List;

/**
 * 新闻Service接口
 *
 * @author jiaxv
 * @date 2025-08-08
 */
public interface IEgoNewService {

    /**
     * 查询新闻详情
     */
    EgoNewVo queryById(Long id);

    /**
     * 分页查询新闻列表
     */
    TableDataInfo<EgoNewVo> queryPageList(EgoNewBo bo, PageQuery pageQuery);

    /**
     * 查询新闻列表
     */
    List<EgoNewVo> queryList(EgoNewBo bo);

    /**
     * 新增新闻
     */
    Boolean insertByBo(EgoNewBo bo);

    /**
     * 修改新闻
     */
    Boolean updateByBo(EgoNewBo bo);

    /**
     * 校验并批量删除新闻信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
