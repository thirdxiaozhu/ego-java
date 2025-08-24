package org.ruoyi.chat.service.image.impl;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ruoyi.chat.service.image.ImageService;
import org.ruoyi.common.chat.request.ImageRequest;
import org.ruoyi.common.satoken.utils.LoginHelper;
import org.ruoyi.domain.vo.ChatModelVo;
import org.ruoyi.service.IChatModelService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class QwenImageServiceImpl implements ImageService {

    private final IChatModelService chatModelService;


    public void asyncCall(ImageRequest request) {
        System.out.println("---创建任务----");
        String taskId = this.createAsyncTask(request);
        System.out.println("--等待任务结束返回图像url----");
        this.waitAsyncTask(taskId);
    }


    /**
     * 创建异步任务
     * @return taskId
     */
    public String createAsyncTask(ImageRequest imageRequest) {

        imageRequest.setUserId(LoginHelper.getUserId());
        ChatModelVo chatModelVo = chatModelService.selectModelByName(imageRequest.getModel());


        String prompt = "一副典雅庄重的对联悬挂于厅堂之中，房间是个安静古典的中式布置，桌子上放着一些青花瓷，对联上左书“义本生知人机同道善思新”，右书“通云赋智乾坤启数高志远”， 横批“智启通义”，字体飘逸，中间挂在一着一副中国风的画作，内容是岳阳楼。";
        ImageSynthesisParam param;
        param = ImageSynthesisParam.builder()
                .apiKey(chatModelVo.getApiKey())
                .model(imageRequest.getModel())
                .prompt(prompt)
                .n(1)
                .size("1328*1328")
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
    public void waitAsyncTask(String taskId) {
        ImageSynthesis imageSynthesis = new ImageSynthesis();
        ImageSynthesisResult result = null;
        try {
            //如果已经在环境变量中设置了 DASHSCOPE_API_KEY，wait()方法可将apiKey设置为null
            result = imageSynthesis.wait(taskId, null);
        } catch (ApiException | NoApiKeyException e){
            throw new RuntimeException(e.getMessage());
        }
        System.out.println(JsonUtils.toJson(result));
        System.out.println(JsonUtils.toJson(result.getOutput()));
    }

}
