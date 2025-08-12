package org.ruoyi.chat.service.chat.impl;

import ch.qos.logback.classic.pattern.MessageConverter;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.dashscope.aigc.conversation.ConversationResult;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import dev.langchain4j.community.model.dashscope.QwenChatRequestParameters;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.PartialThinking;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import io.reactivex.Flowable;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.ruoyi.chat.config.ChatConfig;
import org.ruoyi.chat.enums.ChatModeType;
import org.ruoyi.chat.listener.SSEEventSourceListener;
import org.ruoyi.chat.service.chat.IChatCostService;
import org.ruoyi.chat.service.chat.IChatService;
import org.ruoyi.common.chat.entity.chat.ChatCompletion;
//import org.ruoyi.common.chat.entity.chat.Message as RuoyiMessage;
import org.ruoyi.common.chat.entity.chat.ChatCompletionResponse;
import org.ruoyi.common.chat.openai.OpenAiStreamClient;
import org.ruoyi.common.chat.request.ChatRequest;
import org.ruoyi.common.core.service.BaseContext;
import org.ruoyi.common.core.utils.JsonUtils;
import org.ruoyi.common.core.utils.StringUtils;
import org.ruoyi.domain.vo.ChatModelVo;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * 阿里通义千问
 */
@Service
@Slf4j
public class QianWenAiChatServiceImpl  implements IChatService {

    @Autowired
    private IChatModelService chatModelService;
    @Autowired
    private IChatCostService chatCostService; // 添加扣费服务

    private SseEmitter emitter;
    private ChatRequest request;

    private StringBuilder stringBuffer = new StringBuilder();
    private void handleGenerationResult(GenerationResult message) throws IOException {
//        System.out.println(JsonUtils.toJson(message));
        try {
            if(!message.getOutput().getChoices().get(0).getFinishReason().equals("null")){
                emitter.complete();
                ChatRequest chatRequest = new ChatRequest();
                chatRequest.setRole(org.ruoyi.common.chat.entity.chat.Message.Role.ASSISTANT.getName());
                chatRequest.setModel(request.getModel());
                chatRequest.setUserId(request.getUserId());
                chatRequest.setSessionId(request.getSessionId());
                chatRequest.setPrompt(stringBuffer.toString());
                // 记录会话token
                BaseContext.setCurrentToken(StpUtil.getTokenValue());
                chatCostService.deductToken(chatRequest);
                return;
            }

            String content = message.getOutput().getChoices().get(0).getMessage().getContent();
            String reasoningContent = message.getOutput().getChoices().get(0).getMessage().getReasoningContent();

            log.info("Content: {}", content);
            log.info("ReasioningContent: {}", reasoningContent);

            if(content != null ){
                stringBuffer.append(content);
            }
            emitter.send(Objects.requireNonNull(JsonUtils.toJson(message)));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
    private void streamCallWithMessage(Generation gen)
            throws NoApiKeyException, ApiException, InputRequiredException {
        GenerationParam param = buildGenerationParam(request);
        Flowable<GenerationResult> result = gen.streamCall(param);
        result.blockingForEach(message -> handleGenerationResult(message));
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

    @Override
    public SseEmitter chat(ChatRequest chatRequest, SseEmitter emitter) {

        this.emitter = emitter;
        this.request = chatRequest;
        try {
            Generation gen = new Generation();
            streamCallWithMessage(gen);
        } catch (ApiException | NoApiKeyException | InputRequiredException  e) {
            log.error("An exception occurred: {}", e.getMessage());
        }
        return emitter;
    }

//    @Override
//    public SseEmitter chat(ChatRequest chatRequest, SseEmitter emitter) {
//        ChatModelVo chatModelVo = chatModelService.selectModelByName(chatRequest.getModel());
//        OpenAiStreamClient openAiStreamClient = ChatConfig.createOpenAiStreamClient(chatModelVo.getApiHost(), chatModelVo.getApiKey());
//        List<Message> messages = chatRequest.getMessages();
//        String token = StpUtil.getTokenValue();
//        SSEEventSourceListener listener = new SSEEventSourceListener(emitter,chatRequest.getUserId(),chatRequest.getSessionId(), token);
//        ChatCompletion completion = ChatCompletion
//                .builder()
//                .messages(messages)
//                .model(chatRequest.getModel())
//                .stream(true)
//                .enableThinking(true)
//                .build();
//        openAiStreamClient.streamChatCompletion(completion, listener);
//        return emitter;
//
//    }

    @Override
    public String getCategory() {
        return ChatModeType.QIANWEN.getCode();
    }



}
