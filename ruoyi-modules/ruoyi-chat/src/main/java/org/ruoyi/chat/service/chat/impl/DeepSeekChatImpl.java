package org.ruoyi.chat.service.chat.impl;


import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ruoyi.chat.config.ChatConfig;
import org.ruoyi.chat.enums.ChatModeType;
import org.ruoyi.chat.listener.SSEEventSourceListener;
import org.ruoyi.chat.service.chat.IChatService;
import org.ruoyi.common.chat.entity.chat.ChatCompletion;
import org.ruoyi.common.chat.entity.chat.Message;
import org.ruoyi.common.chat.openai.OpenAiStreamClient;
import org.ruoyi.common.chat.request.ChatRequest;
import org.ruoyi.domain.bo.ChatMessageBo;
import org.ruoyi.domain.vo.ChatModelVo;
import org.ruoyi.service.IChatMessageService;
import org.ruoyi.service.IChatModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * deepseek
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DeepSeekChatImpl  implements IChatService {

    private final IChatModelService chatModelService;

    private final IChatMessageService chatMessageService;

    @Override
    public SseEmitter chat(ChatRequest chatRequest, SseEmitter emitter) {

        ChatModelVo chatModelVo = chatModelService.selectModelByName(chatRequest.getModel());
        List<Message> messages = chatRequest.getMessages();

        chatMessageService.insertByBo(ChatMessageBo.builder()
                .userId(chatRequest.getUserId())
                .sessionId(chatRequest.getSessionId())
                .role(chatRequest.getRole())
                .content(chatRequest.getMessages().get(chatRequest.getMessages().size()-1).getContent().toString())
                .modelName(chatRequest.getModel()).build());


        OpenAiStreamClient openAiStreamClient = ChatConfig.createOpenAiStreamClient(chatModelVo.getApiHost(), chatModelVo.getApiKey());
        String token = StpUtil.getTokenValue();
        SSEEventSourceListener listener = new SSEEventSourceListener(emitter,chatRequest.getUserId(),chatRequest.getSessionId(), token);
        ChatCompletion completion = ChatCompletion
                .builder()
                .messages(messages)
                .model(chatRequest.getModel())
                .stream(true)
                .build();
        openAiStreamClient.streamChatCompletion(completion, listener);
        return emitter;

    }

    @Override
    public String getCategory() {
        return ChatModeType.DEEPSEEK.getCode();
    }
}
