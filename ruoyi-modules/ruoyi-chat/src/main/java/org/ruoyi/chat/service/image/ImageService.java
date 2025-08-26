package org.ruoyi.chat.service.image;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.ruoyi.common.chat.request.ImageRequest;

public interface ImageService {

    public void asyncCall(ImageRequest request);

    public String createSyncTask(ImageRequest request) throws JsonProcessingException;

    public String createAsyncTask(ImageRequest request);
}
