package org.ruoyi.chat.listener;


import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.ruoyi.chat.service.chat.IChatCostService;
import org.ruoyi.common.chat.entity.chat.ChatCompletionResponse;
import org.ruoyi.common.chat.entity.chat.Message;
import org.ruoyi.common.chat.request.ChatRequest;
import org.ruoyi.common.core.service.BaseContext;
import org.ruoyi.common.core.utils.SpringUtils;
import org.ruoyi.common.core.utils.StringUtils;
import org.ruoyi.domain.bo.ChatMessageBo;
import org.ruoyi.service.IChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Objects;

/**
 *  OpenAIEventSourceListener
 *
 * @author https:www.unfbx.com
 * @date 2023-02-22
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class SSEEventSourceListener extends EventSourceListener {

    @Autowired
    private IChatMessageService chatMessageService;


    private SseEmitter emitter;

    private Long userId;

    private Long sessionId;

    private String token;

//    private long totalTokens;

    ObjectMapper mapper;

    @Autowired(required = false)
    public SSEEventSourceListener(SseEmitter emitter,Long userId,Long sessionId, String token) {
        this.emitter = emitter;
        this.userId = userId;
        this.sessionId = sessionId;
        this.token = token;
        this.mapper = new ObjectMapper();
    }


    private StringBuilder stringBuffer = new StringBuilder();

    private String modelName;

    private static final IChatCostService chatCostService = SpringUtils.getBean(IChatCostService.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void onOpen(EventSource eventSource, Response response) {
        if(!chatCostService.hasBalance(userId)){
            log.info("用户无余额...");
            emitter.complete();
            eventSource.cancel();
            return;
        }
        log.info("OpenAI建立sse连接...");
    }

    /**
     * {@inheritDoc}
     */
    @SneakyThrows
    @Override
    public void onEvent(@NotNull EventSource eventSource, String id, String type, String data) {
        try {

            ChatCompletionResponse completionResponse = mapper.readValue(data, ChatCompletionResponse.class);
            if(completionResponse == null || CollectionUtil.isEmpty(completionResponse.getChoices())){
                return;
            }

            if(completionResponse.getChoices().get(0).getFinishReason() != null){
                //成功响应
                emitter.complete();
                BaseContext.setCurrentToken(token);

                ChatMessageBo toRecordMessage = ChatMessageBo.builder()
                        .userId(userId)
                        .sessionId(sessionId)
                        .role(Message.Role.ASSISTANT.getName())
                        .content(stringBuffer.toString())
                        .totalTokens((int) completionResponse.getUsage().getTotalTokens())
                        .modelName(modelName).build();
                chatCostService.deductToken(toRecordMessage);

                // 保存消息记录
                chatMessageService.insertByBo(toRecordMessage);
                return;
            }

            Object content = completionResponse.getChoices().get(0).getDelta().getContent();
            Object reasoningContent = completionResponse.getChoices().get(0).getDelta().getReasoningContent();
//            this.totalTokens = completionResponse.getUsage().getTotalTokens();

            if(content != null ){
                if(StringUtils.isEmpty(modelName)){
                    modelName = completionResponse.getModel();
                }
                stringBuffer.append(content);
            }
            // for both reasoning or normal chat
            emitter.send(data);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void onClosed(EventSource eventSource) {
        log.info("OpenAI关闭sse连接...");
    }

    @SneakyThrows
    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        if (Objects.isNull(response)) {
            return;
        }
        ResponseBody body = response.body();
        if (Objects.nonNull(body)) {
            log.error("OpenAI  sse连接异常data：{}，异常：{}", body.string(), t);
        } else {
            log.error("OpenAI  sse连接异常data：{}，异常：{}", response, t);
        }
        eventSource.cancel();
    }

}
