package org.ruoyi.bagent.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.ruoyi.bagent.domain.EgoBagent;
import org.ruoyi.bagent.domain.vo.EgoBagentVo;
import org.ruoyi.core.mapper.BaseMapperPlus;

/**
 * Ego推送Mapper接口
 *
 * @author ageerle
 * @date 2025-04-08
 */
@Mapper
public interface EgoBagentMapper extends BaseMapperPlus<EgoBagent, EgoBagentVo> {

}