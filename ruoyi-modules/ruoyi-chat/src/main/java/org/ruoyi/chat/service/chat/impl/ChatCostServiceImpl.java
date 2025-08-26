package org.ruoyi.chat.service.chat.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ruoyi.chat.enums.BillingType;
import org.ruoyi.chat.enums.UserGradeType;
import org.ruoyi.chat.service.chat.IChatCostService;
import org.ruoyi.common.chat.request.ChatRequest;
import org.ruoyi.common.chat.utils.TikTokensUtil;
import org.ruoyi.common.core.domain.model.LoginUser;
import org.ruoyi.common.core.exception.ServiceException;
import org.ruoyi.common.core.exception.base.BaseException;
import org.ruoyi.common.satoken.utils.LoginHelper;
import org.ruoyi.domain.ChatUsageToken;
import org.ruoyi.domain.bo.ChatMessageBo;
import org.ruoyi.domain.vo.ChatModelVo;
import org.ruoyi.service.IChatMessageService;
import org.ruoyi.service.IChatModelService;
import org.ruoyi.service.IChatTokenService;
import org.ruoyi.system.domain.SysUser;
import org.ruoyi.system.mapper.SysUserMapper;
import org.springframework.stereotype.Service;


/**
 * 计费管理Service实现
 *
 * @author ageerle
 * @date 2025-04-08
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatCostServiceImpl implements IChatCostService {

    private final SysUserMapper sysUserMapper;

    private final IChatMessageService chatMessageService;

    private final IChatTokenService chatTokenService;

    private final IChatModelService chatModelService;

    @Override
    public void deductToken(ChatMessageBo toRecord) {
        if(toRecord.getUserId()==null || toRecord.getSessionId()==null){
            return;
        }

        log.info("deductToken->本次提交token数 :{} ", toRecord.getTotalTokens());
        String modelName = toRecord.getModelName();

        // 获得记录的累计token数
        ChatUsageToken chatToken = chatTokenService.queryByUserId(toRecord.getUserId(), modelName);
        if (chatToken == null) {
            chatToken = new ChatUsageToken();
            chatToken.setToken(0);
        }

        // 计算总token数
        int totalTokens = chatToken.getToken() + toRecord.getTotalTokens();

        //当前未付费token
        int token = chatToken.getToken();

        log.warn("deductToken->未付费的token数       {}: ", token);
        log.warn("deductToken->本次提交+未付费token数 {}: ", totalTokens);

        //扣费核心逻辑（总token大于100就要对未结清的token进行扣费）
        if (totalTokens >= 100) {// 如果总token数大于等于100,进行费用扣除
            ChatModelVo chatModelVo = chatModelService.selectModelByName(modelName);
            double cost = chatModelVo.getModelPrice();
            if (BillingType.TIMES.getCode().equals(chatModelVo.getModelType())) {
                // 按次数扣费
                deductUserBalance(toRecord.getUserId(), cost);
                toRecord.setDeductCost(cost);
            }else {
                // 按token扣费
                Double numberCost = totalTokens * cost;
                log.warn("deductToken->按token扣费 计算token数量: {}", totalTokens);
                log.warn("deductToken->按token扣费 每token的价格: {}", cost);

                deductUserBalance(toRecord.getUserId(), numberCost);
                toRecord.setDeductCost(numberCost);

                // 保存剩余tokens
                chatToken.setModelName(modelName);
                chatToken.setUserId(toRecord.getUserId());
                chatToken.setToken(0);//因为判断大于100token直接全部计算扣除了所以这里直接=0就可以了
                chatTokenService.editToken(chatToken);
            }
        } else {
            //不满100Token,不需要进行扣费
            //deductUserBalance(chatMessageBo.getUserId(), 0.0);
            toRecord.setDeductCost(0d);
            toRecord.setRemark("不满100Token,计入下一次!");
            log.warn("deductToken->不满100Token,计入下一次!");
            chatToken.setToken(totalTokens);
            chatToken.setModelName(toRecord.getModelName());
            chatToken.setUserId(toRecord.getUserId());
            chatTokenService.editToken(chatToken);
        }

    }

    @Override
    public Boolean hasBalance() {
        SysUser sysUser = sysUserMapper.selectById(this.getUserId());
        if (sysUser == null || sysUser.getUserBalance() == 0) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean hasBalance(Long userId) {
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser == null || sysUser.getUserBalance() == 0) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 从用户余额中扣除费用
     *
     * @param userId     用户ID
     * @param numberCost 要扣除的费用
     */
    @Override
    public void deductUserBalance(Long userId, Double numberCost) {

        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser == null) {
            return;
        }

        Double userBalance = sysUser.getUserBalance();
        log.warn("deductUserBalance->准备扣除：numberCost: {}", numberCost);
        log.warn("deductUserBalance->剩余金额：userBalance:{}", userBalance);

        double newBalance = Math.max(userBalance - numberCost, 0);

        sysUserMapper.update(null,
                new LambdaUpdateWrapper<SysUser>()
                        .set(SysUser::getUserBalance, newBalance)
                        .eq(SysUser::getUserId, userId));

        if (userBalance == 0 || userBalance < numberCost) {
            throw new ServiceException("余额不足, 请充值");
        }

    }

    /**
     * 扣除任务费用
     */
    @Override
    public void deductTask(String type, String prompt, double cost) {
        // 判断用户是否付费
        checkUserGrade();
        // 扣除费用
        deductUserBalance(getUserId(), cost);
        // 保存消息记录
        ChatMessageBo chatMessageBo = ChatMessageBo.builder()
                .userId(getUserId())
                .modelName(type)
                .content(prompt)
                .deductCost(cost)
                .totalTokens(0)
                .build();
        chatMessageService.insertByBo(chatMessageBo);
    }

    /**
     * 判断用户是否付费
     */
    @Override
    public void checkUserGrade() {
        SysUser sysUser = sysUserMapper.selectById(getUserId());
        if(UserGradeType.UNPAID.getCode().equals(sysUser.getUserGrade())){
            throw new BaseException("该模型仅限付费用户使用。请升级套餐，开启高效体验之旅！");
        }
    }

    /**
     * 获取用户Id
     */
    @Override
    public Long getUserId() {
        LoginUser loginUser = LoginHelper.getLoginUser();
        if (loginUser == null) {
            throw new BaseException("用户未登录！");
        }
        return loginUser.getUserId();
    }
}
