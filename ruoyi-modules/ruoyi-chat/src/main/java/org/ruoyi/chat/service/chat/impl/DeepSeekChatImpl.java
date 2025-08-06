package org.ruoyi.chat.service.chat.impl;


import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.ruoyi.chat.config.ChatConfig;
import org.ruoyi.chat.enums.ChatModeType;
import org.ruoyi.chat.listener.SSEEventSourceListener;
import org.ruoyi.chat.service.chat.IChatService;
import org.ruoyi.common.chat.entity.chat.ChatCompletion;
import org.ruoyi.common.chat.entity.chat.Message;
import org.ruoyi.common.chat.openai.OpenAiStreamClient;
import org.ruoyi.common.chat.request.ChatRequest;
import org.ruoyi.domain.vo.ChatModelVo;
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
public class DeepSeekChatImpl  implements IChatService {

    @Autowired
    private IChatModelService chatModelService;

    @Override
    public SseEmitter chat(ChatRequest chatRequest, SseEmitter emitter) {
//        ChatModelVo chatModelVo = chatModelService.selectModelByName(chatRequest.getModel());
//        StreamingChatModel chatModel = OpenAiStreamingChatModel.builder()
//                .baseUrl(chatModelVo.getApiHost())
//                .apiKey(chatModelVo.getApiKey())
//                .modelName(chatModelVo.getModelName())
//                .logRequests(true)
//                .logResponses(true)
//                .temperature(0.8)
//                .build();
//
//        // 发送流式消息
//        try {
//            chatModel.chat(chatRequest.getPrompt(), new StreamingChatResponseHandler() {
//                @SneakyThrows
//                @Override
//                public void onPartialResponse(String partialResponse) {
//                    emitter.send(partialResponse);
//                    log.info("收到消息片段: {}", partialResponse);
////                    System.out.print(partialResponse);
//                }
//
//                @Override
//                public void onCompleteResponse(ChatResponse completeResponse) {
//                    emitter.complete();
//                    log.info("消息结束，完整消息ID: {}", completeResponse);
//                }
//
//                @Override
//                public void onError(Throwable error) {
//                    System.err.println("错误: " + error.getMessage());
//                }
//            });
//
//        } catch (Exception e) {
//            log.error("deepseek请求失败：{}", e.getMessage());
//        }

        ChatModelVo chatModelVo = chatModelService.selectModelByName(chatRequest.getModel());
        OpenAiStreamClient openAiStreamClient = ChatConfig.createOpenAiStreamClient(chatModelVo.getApiHost(), chatModelVo.getApiKey());
        List<Message> messages = chatRequest.getMessages();
//        for(Message msg : messages){
//            log.warn("}}}}}} {}", msg.getRole());
//        }
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
