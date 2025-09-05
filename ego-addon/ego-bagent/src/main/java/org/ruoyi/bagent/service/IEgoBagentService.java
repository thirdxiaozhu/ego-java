package org.ruoyi.bagent.service;

import org.ruoyi.bagent.domain.bo.EgoBagentBo;
import org.ruoyi.bagent.domain.vo.EgoBagentVo;
import org.ruoyi.core.page.PageQuery;
import org.ruoyi.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * Ego推送接口
 *
 * @author ageerle
 * @date 2025-04-08
 */
public interface IEgoBagentService {

    /**
     * 查询聊天消息
     */
    EgoBagentVo queryById(Long id);

    /**
     * 查询聊天消息列表
     */
    TableDataInfo<EgoBagentVo> queryPageList(EgoBagentBo bo, PageQuery pageQuery);

    /**
     * 查询聊天消息列表
     */
    List<EgoBagentVo> queryList(EgoBagentBo bo);

    /**
     * 新增聊天消息
     */
    Boolean insertByBo(EgoBagentBo bo);

    /**
     * 修改聊天消息
     */
    Boolean updateByBo(EgoBagentBo bo);

    /**
     * 校验并批量删除聊天消息信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
