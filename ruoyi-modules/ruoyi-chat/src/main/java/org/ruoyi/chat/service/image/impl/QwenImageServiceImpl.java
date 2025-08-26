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
import org.ruoyi.chat.service.image.ImageService;
import org.ruoyi.common.chat.request.ImageRequest;
import org.ruoyi.common.chat.response.ImageResponse;
import org.ruoyi.common.chat.response.ImageResult;
import org.ruoyi.common.satoken.utils.LoginHelper;
import org.ruoyi.domain.bo.ImageRecordBo;
import org.ruoyi.domain.vo.ChatModelVo;
import org.ruoyi.domain.vo.ImageRecordVo;
import org.ruoyi.service.IChatModelService;
import org.ruoyi.service.IImageService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class QwenImageServiceImpl implements ImageService {

    private final IChatModelService chatModelService;

    private final IImageService imageService;


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

        ImageResponse imageResponse = ImageResponse.builder()
                .requestID(result.getRequestId())
                .taskID(result.getOutput().getTaskId())
                .taskStatus(result.getOutput().getTaskStatus())
                .totalImages(result.getOutput().getTaskMetrics().getTotal())
                .succeedImages(result.getOutput().getTaskMetrics().getSucceeded())
                .failedImages(result.getOutput().getTaskMetrics().getFailed())
                .build();

        List<ImageResult> imageResults = new ArrayList<>();
        for(Map<String, String> rawResult: result.getOutput().getResults()){
            imageResults.add(ImageResult.builder()
                    .url(rawResult.get("url"))
                    .origPrompt(rawResult.get("orig_prompt"))
                    .actualPrompt(rawResult.get("actual_prompt"))
                    .build());

            imageService.insertByBo(ImageRecordBo.builder()
                            .prompt(rawResult.get("actual_prompt"))
                            .url(rawResult.get("url"))
                            .userId(request.getUserId())
                            .modelName(request.getModel())
                            .requestId(result.getRequestId())
                            .deductCost(1.0)
                    .build());
        }

        ObjectMapper mapper = new ObjectMapper();
        imageResponse.setResults(imageResults);

        String jsonString = mapper.writeValueAsString(imageResponse);

        return jsonString;
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

}
