package org.ruoyi.push.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ruoyi.core.mapper.BaseMapperPlus;
import org.ruoyi.push.domain.EgoPush;
import org.ruoyi.push.domain.vo.EgoPushVo;

/**
 * Ego推送Mapper接口
 *
 * @author ageerle
 * @date 2025-04-08
 */
@Mapper
public interface EgoPushMapper extends BaseMapperPlus<EgoPush, EgoPushVo> {

}