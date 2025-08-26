package org.ruoyi.chat.service.chat;

import org.ruoyi.domain.bo.ChatMessageBo;

/**
 * 计费管理Service接口
 *
 * @author ageerle
 * @date 2025-04-08
 */
public interface IChatCostService {
    /**
     * 扣除余额并且保存记录
     *
     * @param toRecord 对话信息
     * @return 结果
     */

    void deductToken(ChatMessageBo toRecord);

    /**
     * 直接扣除用户的余额
     *
     */
    void deductUserBalance(Long userId, Double numberCost);

    /**
     * 判断用户是否余额
     *
     * @return 结果
     */
    Boolean hasBalance();

    /**
     * 判断用户是否余额
     *
     * @return 结果
     */
    Boolean hasBalance(Long userId);


    /**
     * 扣除任务费用并且保存记录
     *
     * @param type 任务类型
     * @param prompt 任务描述
     * @param cost 扣除费用
     */
    void deductTask(String type, String prompt, double cost);


    /**
     * 判断用户是否付费
     */
    void checkUserGrade();

    /**
     * 获取登录用户id
     */
    Long getUserId();
}
