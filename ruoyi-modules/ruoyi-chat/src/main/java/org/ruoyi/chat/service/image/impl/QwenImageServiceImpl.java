package org.ruoyi.chat.service.image.impl;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ruoyi.chat.enums.ImageModeType;
import org.ruoyi.chat.enums.qwen.SketchStyle;
import org.ruoyi.chat.service.image.IImageService;
import org.ruoyi.common.chat.request.ImageRequest;
import org.ruoyi.common.chat.response.ImageResponse;
import org.ruoyi.common.chat.response.ImageResult;
import org.ruoyi.common.satoken.utils.LoginHelper;
import org.ruoyi.domain.bo.ImageRecordBo;
import org.ruoyi.domain.vo.ChatModelVo;
import org.ruoyi.service.IChatModelService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class QwenImageServiceImpl implements IImageService {

    private final IChatModelService chatModelService;

    private final org.ruoyi.service.IImageService imageService;

    private final ObjectMapper mapper = new ObjectMapper();


    public void asyncCall(ImageRequest request) {
        ChatModelVo chatModelVo = chatModelService.selectModelByName(request.getModel());
        System.out.println("---创建任务----");
        String taskId = this.createAsyncTask(request);
        System.out.println("--等待任务结束返回图像url----");
        this.waitAsyncTask(chatModelVo.getApiKey(), taskId);
    }

    /**
     * 创建同步任务
     * @return taskId
     */
    public String createSyncTask(ImageRequest request) throws JsonProcessingException {
        request.setUserId(LoginHelper.getUserId());
        ChatModelVo chatModelVo = chatModelService.selectModelByName(request.getModel());

        ImageSynthesisParam param = ImageSynthesisParam.builder()
                .apiKey(chatModelVo.getApiKey())
                .model(request.getModel())
                .prompt(request.getPrompt())
                .n(request.getN())
                .size(request.getSize())
                .build();

        ImageSynthesis imageSynthesis = new ImageSynthesis();
        ImageSynthesisResult result = null;
        try {
            result = imageSynthesis.call(param);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }


        return mapper.writeValueAsString(handleResult(request, result));
    }

    /**
     * 创建异步任务
     * @return taskId
     */
    public String createAsyncTask(ImageRequest request) {

        request.setUserId(LoginHelper.getUserId());
        ChatModelVo chatModelVo = chatModelService.selectModelByName(request.getModel());

        ImageSynthesisParam param = ImageSynthesisParam.builder()
                .apiKey(chatModelVo.getApiKey())
                .model(request.getModel())
                .prompt(request.getPrompt())
                .n(request.getN())
                .size(request.getSize())
                .build();

        ImageSynthesis imageSynthesis = new ImageSynthesis();
        ImageSynthesisResult result = null;
        try {
            result = imageSynthesis.asyncCall(param);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
        System.out.println(JsonUtils.toJson(result));
        String taskId = result.getOutput().getTaskId();
        System.out.println("taskId=" + taskId);
        return taskId;
    }


    /**
     * 等待异步任务结束
     * @param taskId 任务id
     * */
    public void waitAsyncTask(String apiKey, String taskId) {
        ImageSynthesis imageSynthesis = new ImageSynthesis();
        ImageSynthesisResult result = null;
        try {
            //如果已经在环境变量中设置了 DASHSCOPE_API_KEY，wait()方法可将apiKey设置为null
            result = imageSynthesis.wait(taskId, apiKey);
        } catch (ApiException | NoApiKeyException e){
            throw new RuntimeException(e.getMessage());
        }
        System.out.println(JsonUtils.toJson(result));
        System.out.println(JsonUtils.toJson(result.getOutput()));
    }

    @Override
    public String syncEditCall(ImageRequest request) throws JsonProcessingException {
        // 设置parameters参数
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("prompt_extend", true);

        request.setUserId(LoginHelper.getUserId());
        ChatModelVo chatModelVo = chatModelService.selectModelByName(request.getModel());

        ImageSynthesisParam param =
                ImageSynthesisParam.builder()
                        .apiKey(chatModelVo.getApiKey())
                        .model(request.getModel())
//                        .function(ImageSynthesis.ImageEditFunction.DESCRIPTION_EDIT_WITH_MASK)
                        .function(request.getEditFunction())
                        .prompt(request.getPrompt())
                        .maskImageUrl(request.getMaskImageUrl())
                        .baseImageUrl(request.getBaseImageUrl())
                        .n(request.getN())
                        .size(request.getSize())
                        .parameters(parameters)
                        .build();

        ImageSynthesis imageSynthesis = new ImageSynthesis();
        ImageSynthesisResult result = null;
        try {
            System.out.println("---sync call, please wait a moment----");
            result = imageSynthesis.call(param);
        } catch (ApiException | NoApiKeyException e){
            throw new RuntimeException(e.getMessage());
        }
        System.out.println(JsonUtils.toJson(result));

        return mapper.writeValueAsString(handleResult(request, result));
    }

    /**
     * 草图生成图片
     * @param request
     */
    public String syncSketchCall(ImageRequest request) throws JsonProcessingException {

        request.setUserId(LoginHelper.getUserId());
        ChatModelVo chatModelVo = chatModelService.selectModelByName(request.getModel());

        ImageSynthesisParam param = ImageSynthesisParam.builder()
                .apiKey(chatModelVo.getApiKey())
                .model(chatModelVo.getModelName())
                .prompt(request.getPrompt())
                .n(request.getN())
                .size(request.getSize())
                .sketchImageUrl(request.getSketchImageUrl())
                .style(Optional.ofNullable(SketchStyle.fromCode(request.getSketchStyle()))
                        .map(SketchStyle::getValue).orElse("<auto>"))
                .build();

        String task = "image2image";
        ImageSynthesis imageSynthesis = new ImageSynthesis(task);
        ImageSynthesisResult result = null;
        try {
            System.out.println("---sync call, please wait a moment----");
            result = imageSynthesis.call(param);
        } catch (ApiException | NoApiKeyException e){
            throw new RuntimeException(e.getMessage());
        }
        return mapper.writeValueAsString(handleResult(request, result));
    }


    @Override
    public String getCategory(){
        return ImageModeType.Qwen.getCode();
    }

    private ImageResponse handleResult(ImageRequest request, ImageSynthesisResult  result){
        // 生成返回结果
        ImageResponse imageResponse = ImageResponse.builder()
                .requestID(result.getRequestId())
                .taskID(result.getOutput().getTaskId())
                .taskStatus(result.getOutput().getTaskStatus())
                .totalImages(result.getOutput().getTaskMetrics().getTotal())
                .succeedImages(result.getOutput().getTaskMetrics().getSucceeded())
                .failedImages(result.getOutput().getTaskMetrics().getFailed())
                .build();

        // 遍历多个结果
        List<ImageResult> imageResults = new ArrayList<>();
        for(Map<String, String> rawResult: result.getOutput().getResults()){
            imageResults.add(ImageResult.builder()
                    .url(rawResult.get("url"))
                    .origPrompt(rawResult.get("orig_prompt"))
                    .actualPrompt(rawResult.get("actual_prompt"))
                    .build());

            //  插入数据库
            imageService.insertByBo(ImageRecordBo.builder()
                    .prompt(rawResult.get("actual_prompt"))
                    .url(rawResult.get("url"))
                    .userId(request.getUserId())
                    .modelName(request.getModel())
                    .requestId(result.getRequestId())
                    .deductCost(1.0)
                    .build());
        }

        imageResponse.setResults(imageResults);
        return imageResponse;
    }


    // 图片文字编辑功能

}
