package org.ruoyi.chat.service.chat.impl;

import ch.qos.logback.classic.pattern.MessageConverter;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.dashscope.aigc.conversation.ConversationResult;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.exception.UploadFileException;
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
import org.ruoyi.common.chat.entity.chat.Content;
import org.ruoyi.common.chat.openai.OpenAiStreamClient;
import org.ruoyi.common.chat.request.ChatRequest;
import org.ruoyi.common.core.exception.base.BaseException;
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
import java.util.*;


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

            if(content != null ){
                stringBuffer.append(content);
            }
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(message);
            emitter.send(Objects.requireNonNull(jsonString));
            log.info(Objects.requireNonNull(jsonString));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleMultiModalResult(MultiModalConversationResult message) throws IOException {
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

//            String content = message.getOutput().getChoices().get(0).getMessage().getContent();
//            String reasoningContent = message.getOutput().getChoices().get(0).getMessage().getReasoningContent();
//
//            if(content != null ){
//                stringBuffer.append(content);
//            }
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(message);
            emitter.send(Objects.requireNonNull(jsonString));
            log.info(Objects.requireNonNull(jsonString));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
    private void streamCallWithMessage()
            throws NoApiKeyException, ApiException, InputRequiredException {

        Generation gen = new Generation();
        GenerationParam param = buildGenerationParam(request);
        Flowable<GenerationResult> result = gen.streamCall(param);
//        result.blockingForEach(message -> handleGenerationResult(message));
        result.subscribe(message -> handleGenerationResult(message));
        // 当完成时关闭 SseEmitter
        emitter.onCompletion(() -> {
            System.out.println("SSE 连接已完成，正在关闭.");
        });
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

    public  void simpleMultiModalConversationCall()
            throws ApiException, NoApiKeyException, UploadFileException {
        ChatModelVo chatModelVo = chatModelService.selectModelByName(request.getModel());

        Object content =  request.getMessages().get(request.getMessages().size()-1).getContent();

        if(content instanceof ArrayList<?> listContent){
            for(Object obj: listContent){
                Content
                log.warn("+++++++++++++++++++++++++++++ {}", obj.getClass().toString());
            }
        }else {
            throw new BaseException("请检查你的输入参数");
        }



        MultiModalConversation conv = new MultiModalConversation();
        MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                .content(Arrays.asList(
                        Collections.singletonMap("image", "https://dashscope.oss-cn-beijing.aliyuncs.com/images/dog_and_girl.jpeg"),
                        Collections.singletonMap("image", "https://dashscope.oss-cn-beijing.aliyuncs.com/images/tiger.png"),
                        Collections.singletonMap("image", "https://dashscope.oss-cn-beijing.aliyuncs.com/images/rabbit.png"),
                        Collections.singletonMap("text", "这些是什么?"))).build();
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey(chatModelVo.getApiKey())
                // 此处以qwen-vl-plus为例，可按需更换模型名称。模型列表：https://help.aliyun.com/zh/model-studio/getting-started/models
                .model("qwen-vl-plus")
                .message(userMessage)
                .build();

        Flowable<MultiModalConversationResult> result = conv.streamCall(param);
        result.subscribe(message -> handleMultiModalResult(message));
        // 当完成时关闭 SseEmitter
        emitter.onCompletion(() -> {
            System.out.println("SSE 连接已完成，正在关闭.");
        });
    }


    @Override
    public SseEmitter chat(ChatRequest chatRequest, SseEmitter emitter) {
        this.emitter = emitter;
        this.request = chatRequest;
        try {
            // 获取最后一条消息,判断是否为多模态
            org.ruoyi.common.chat.entity.chat.Message msg = chatRequest.getMessages().get(chatRequest.getMessages().size()-1);
            if(msg.getContent().getClass() == String.class){
                streamCallWithMessage();
            }else {
                simpleMultiModalConversationCall();
            }
        } catch (ApiException | NoApiKeyException | InputRequiredException  e) {
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
