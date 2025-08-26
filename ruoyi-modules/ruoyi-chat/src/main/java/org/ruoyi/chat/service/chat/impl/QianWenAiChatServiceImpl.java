package org.ruoyi.chat.service.chat.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.exception.UploadFileException;
import com.coze.openapi.client.chat.model.Chat;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.reactivex.Flowable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ruoyi.chat.enums.ChatModeType;
import org.ruoyi.chat.service.chat.IChatCostService;
import org.ruoyi.chat.service.chat.IChatService;
//import org.ruoyi.common.chat.entity.chat.Message as RuoyiMessage;
import org.ruoyi.common.chat.entity.chat.Content;
import org.ruoyi.common.chat.entity.chat.MessageResponse;
import org.ruoyi.common.chat.request.ChatRequest;
import org.ruoyi.common.core.exception.base.BaseException;
import org.ruoyi.common.core.service.BaseContext;
import org.ruoyi.domain.bo.ChatMessageBo;
import org.ruoyi.domain.vo.ChatModelVo;
import org.ruoyi.service.IChatMessageService;
import org.ruoyi.service.IChatModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.IOException;
import java.util.*;


/**
 * 阿里通义千问
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class QianWenAiChatServiceImpl  implements IChatService {

    private final IChatModelService chatModelService;
    private final IChatCostService chatCostService; // 添加扣费服务
    private final IChatMessageService chatMessageService;

    private void handleGenerationResult(GenerationResult message, ChatRequest request, SseEmitter emitter, String token, StringBuilder stringBuffer) throws IOException {

        try {
            if(!message.getOutput().getChoices().get(0).getFinishReason().equals("null")){
//                ChatRequest chatRequest = new ChatRequest();
//                chatRequest.setRole(org.ruoyi.common.chat.entity.chat.Message.Role.ASSISTANT.getName());
//                chatRequest.setModel(request.getModel());
//                chatRequest.setUserId(request.getUserId());
//                chatRequest.setSessionId(request.getSessionId());
//                chatRequest.setPrompt(stringBuffer.toString());
                // 记录会话token
                BaseContext.setCurrentToken(token);
                // 要减去输入的token

                ChatMessageBo toRecordMessage = ChatMessageBo.builder()
                        .userId(request.getUserId())
                        .sessionId(request.getSessionId())
                        .role(org.ruoyi.common.chat.entity.chat.Message.Role.ASSISTANT.getName())
                        .content(stringBuffer.toString())
                        .totalTokens(message.getUsage().getTotalTokens())
                        .modelName(request.getModel()).build();
                chatCostService.deductToken(toRecordMessage);

                // 保存消息记录
                chatMessageService.insertByBo(toRecordMessage);

                emitter.send("[DONE]");
                emitter.complete();
                return;
            }

            String content = message.getOutput().getChoices().get(0).getMessage().getContent();
            String reasoningContent = message.getOutput().getChoices().get(0).getMessage().getReasoningContent();

            if(content != null){
                stringBuffer.append(content);
            }
            ObjectMapper mapper = new ObjectMapper();
            MessageResponse resp = MessageResponse.builder().sessionID(request.getSessionId()).userID(request.getUserId()).model(request.getModel()).content(message).build();

            String jsonString = mapper.writeValueAsString(resp);
            emitter.send(Objects.requireNonNull(jsonString));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleMultiModalResult(MultiModalConversationResult message, ChatRequest request, SseEmitter emitter, String token, StringBuilder stringBuffer) throws IOException {
        try {
            if(!message.getOutput().getChoices().get(0).getFinishReason().equals("null")){
//                ChatRequest chatRequest = new ChatRequest();
//                chatRequest.setRole(org.ruoyi.common.chat.entity.chat.Message.Role.ASSISTANT.getName());
//                chatRequest.setModel(request.getModel());
//                chatRequest.setUserId(request.getUserId());
//                chatRequest.setSessionId(request.getSessionId());
//                chatRequest.setPrompt(stringBuffer.toString());
                // 记录会话token
                BaseContext.setCurrentToken(token);

                ChatMessageBo toRecordMessage = ChatMessageBo.builder()
                        .userId(request.getUserId())
                        .sessionId(request.getSessionId())
                        .role(org.ruoyi.common.chat.entity.chat.Message.Role.ASSISTANT.getName())
                        .content((String) message.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text"))
                        .totalTokens(message.getUsage().getTotalTokens())
                        .modelName(request.getModel()).build();
                chatCostService.deductToken(toRecordMessage);

                // 保存消息记录
                chatMessageService.insertByBo(toRecordMessage);

                emitter.send("[DONE]");
                emitter.complete();
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            MessageResponse resp = MessageResponse.builder().sessionID(request.getSessionId()).userID(request.getUserId()).model(request.getModel()).content(message).build();
            String jsonString = mapper.writeValueAsString(resp);
            emitter.send(Objects.requireNonNull(jsonString));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void streamCallText(ChatRequest chatRequest, SseEmitter emitter)
            throws NoApiKeyException, ApiException, InputRequiredException {

        Generation gen = new Generation();
        GenerationParam param = buildGenerationParam(chatRequest);
        Flowable<GenerationResult> result = gen.streamCall(param);
        StringBuilder stringBuffer = new StringBuilder();
        String token = StpUtil.getTokenValue();

        ChatMessageBo toRecord = ChatMessageBo.builder()
                .userId(chatRequest.getUserId())
                .sessionId(chatRequest.getSessionId())
                .role(chatRequest.getRole())
                .content(chatRequest.getMessages().get(chatRequest.getMessages().size()-1).getContent().toString())
                .modelName(chatRequest.getModel()).build();

        chatMessageService.insertByBo(toRecord);

        result.subscribe(
                message -> {
                    try {
                        handleGenerationResult(message, chatRequest, emitter, token, stringBuffer);
                    } catch (IOException e) {
                        log.error("处理生成结果时发生错误: {}", e.getMessage(), e);
                        emitter.completeWithError(e);
                    }
                },
                error -> {
                    log.error("流调用发生错误: {}", error.getMessage(), error);
                    emitter.completeWithError(error);
                }
        );
    }

    private GenerationParam buildGenerationParam(ChatRequest request) {
        ChatModelVo chatModelVo = chatModelService.selectModelByName(request.getModel());
        List<Message> messages = new ArrayList<>();
        for(int i = 0; i < request.getMessages().size(); i++){
//            log.warn("Message {} , Content: {}",i , request.getMessages().get(i).getContent());
            messages.add(MessageConverter(request.getMessages().get(i)));
        }

        return GenerationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey(chatModelVo.getApiKey())
                // 此处以qwen-plus为例，可按需更换模型名称。模型列表：https://help.aliyun.com/zh/model-studio/getting-started/models
                .model(request.getModel())
                .messages(messages)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .incrementalOutput(true)
                .enableThinking(true)
                .build();
    }

    private Message MessageConverter(org.ruoyi.common.chat.entity.chat.Message message){
        return Message.builder()
                .role(message.getRole())
                .content((String) message.getContent())
                .build();
    }

    private void streamCallMultiModal(ChatRequest chatRequest, SseEmitter emitter)
            throws ApiException, NoApiKeyException, UploadFileException, JsonProcessingException {
        ChatModelVo chatModelVo = chatModelService.selectModelByName(chatRequest.getModel());

        Object content =  chatRequest.getMessages().get(chatRequest.getMessages().size()-1).getContent();

        ArrayList<Map<String, Object>> modals = new ArrayList<>();

        if(content instanceof ArrayList<?> listContent){
            for(Object obj: listContent){
                ObjectMapper mapper = new ObjectMapper();
                //先转换为标准json字符串
                String jsonString = mapper.writeValueAsString(obj);
                //再转换为对象
                Content multiModal = mapper.readValue(jsonString, Content.class);

                switch (multiModal.getType()){
                    case "text":
                        ChatMessageBo toRecord = ChatMessageBo.builder()
                                .userId(chatRequest.getUserId())
                                .sessionId(chatRequest.getSessionId())
                                .role(chatRequest.getRole())
                                .content(multiModal.getText())
                                .modelName(chatRequest.getModel()).build();

                        chatMessageService.insertByBo(toRecord);
                        modals.add(Collections.singletonMap("text", multiModal.getText()));
                        break;
                    case "image_url":
                        modals.add(Collections.singletonMap("image", multiModal.getImageUrl().getUrl()));
                        break;
                    case "video_url":
                        modals.add(Collections.singletonMap("video", multiModal.getVideoUrl().getUrl()));
                        break;
                    default:
                        throw new BaseException("请检查你的输入参数");
                }
            }
        }else {
            throw new BaseException("请检查你的输入参数");
        }

        log.warn("!!!!!!!!!!!! {}", modals);

        MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                .content(modals).build();

        MultiModalConversation conv = new MultiModalConversation();
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                .apiKey(chatModelVo.getApiKey())
                // 此处以qwen-vl-plus为例，可按需更换模型名称。模型列表：https://help.aliyun.com/zh/model-studio/getting-started/models
                .model(chatRequest.getModel())
                .message(userMessage)
                .build();

        StringBuilder stringBuffer = new StringBuilder();
        Flowable<MultiModalConversationResult> result = conv.streamCall(param);

        String token = StpUtil.getTokenValue();
        result.subscribe(
                message -> {
                    try {
                        handleMultiModalResult(message, chatRequest, emitter, token, stringBuffer);
                    } catch (IOException e) {
                        log.error("处理生成结果时发生错误: {}", e.getMessage(), e);
                        emitter.completeWithError(e);
                    }
                },
                error -> {
                    log.error("流调用发生错误: {}", error.getMessage(), error);
                    emitter.completeWithError(error);
                }
        );
    }


    @Override
    public SseEmitter chat(ChatRequest chatRequest, SseEmitter emitter) {
        // 统一设置事件回调
        emitter.onCompletion(() -> {
            log.info("SSE 连接已完成，正在关闭.");
        });

        emitter.onError(throwable -> {
            log.error("SSE 连接发生错误: {}", throwable.getMessage(), throwable);
        });

        try {
            // 获取最后一条消息,判断是否为多模态
            org.ruoyi.common.chat.entity.chat.Message msg = chatRequest.getMessages().get(chatRequest.getMessages().size()-1);
            if(msg.getContent().getClass() == String.class){
                streamCallText(chatRequest, emitter);
            }else {
                streamCallMultiModal(chatRequest, emitter);
            }
        } catch (ApiException | NoApiKeyException | InputRequiredException | JsonProcessingException e) {
            log.error("An exception occurred: {}", e.getMessage());
        } catch (UploadFileException e) {
            throw new RuntimeException(e);
        }
        return emitter;
    }

    @Override
    public String getCategory() {
        return ChatModeType.QIANWEN.getCode();
    }



}
