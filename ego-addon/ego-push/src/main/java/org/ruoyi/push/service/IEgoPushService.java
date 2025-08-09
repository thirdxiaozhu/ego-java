package org.ruoyi.push.service;

import org.ruoyi.core.page.PageQuery;
import org.ruoyi.core.page.TableDataInfo;
import org.ruoyi.push.domain.bo.EgoPushBo;
import org.ruoyi.push.domain.vo.EgoPushVo;

import java.util.Collection;
import java.util.List;

/**
 * Ego推送接口
 *
 * @author ageerle
 * @date 2025-04-08
 */
public interface IEgoPushService {

    /**
     * 查询聊天消息
     */
    EgoPushVo queryById(Long id);

    /**
     * 查询聊天消息列表
     */
    TableDataInfo<EgoPushVo> queryPageList(EgoPushBo bo, PageQuery pageQuery);

    /**
     * 查询聊天消息列表
     */
    List<EgoPushVo> queryList(EgoPushBo bo);

    /**
     * 新增聊天消息
     */
    Boolean insertByBo(EgoPushBo bo);

    /**
     * 修改聊天消息
     */
    Boolean updateByBo(EgoPushBo bo);

    /**
     * 校验并批量删除聊天消息信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
