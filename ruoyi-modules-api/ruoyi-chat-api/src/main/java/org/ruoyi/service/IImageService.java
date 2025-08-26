package org.ruoyi.service;

import org.ruoyi.core.page.PageQuery;
import org.ruoyi.core.page.TableDataInfo;
import org.ruoyi.domain.bo.ImageRecordBo;
import org.ruoyi.domain.vo.ImageRecordVo;
import org.ruoyi.domain.vo.ImageRecordVo;

import java.util.Collection;
import java.util.List;

public interface IImageService {
    /**
     * 查询聊天消息
     */
    ImageRecordVo queryById(Long id);

    /**
     * 查询聊天消息列表
     */
    TableDataInfo<ImageRecordVo> queryPageList(ImageRecordBo bo, PageQuery pageQuery);

    /**
     * 查询聊天消息列表
     */
    List<ImageRecordVo> queryList(ImageRecordBo bo);

    /**
     * 新增聊天消息
     */
    Boolean insertByBo(ImageRecordBo bo);

    /**
     * 修改聊天消息
     */
    Boolean updateByBo(ImageRecordBo bo);

    /**
     * 校验并批量删除聊天消息信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
