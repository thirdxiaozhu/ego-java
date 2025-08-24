package org.ruoyi.chat.service.image;

import org.ruoyi.common.chat.request.ImageRequest;

public interface ImageService {

    public void asyncCall(ImageRequest request);

    public String createAsyncTask(ImageRequest request);
}
