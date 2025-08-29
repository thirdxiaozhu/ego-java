package org.ruoyi.chat.factory;

import lombok.extern.slf4j.Slf4j;
import org.ruoyi.chat.service.chat.IChatService;
import org.ruoyi.chat.service.image.IImageService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class ImageServiceFactory implements ApplicationContextAware {
    private final Map<String, IImageService> imageServiceMap = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 初始化时收集所有IImageService的实现
        Map<String, IImageService> serviceMap = applicationContext.getBeansOfType(IImageService.class);
        for (IImageService service : serviceMap.values()) {
            if (service != null) {
                imageServiceMap.put(service.getCategory(), service);
            }
        }
    }


    /**
     * 根据模型类别获取对应的聊天服务实现
     */
    public IImageService getImageService(String category) {
        IImageService service = imageServiceMap.get(category);
        if (service == null) {
            throw new IllegalArgumentException("不支持的模型类别: " + category);
        }
        return service;
    }
}
