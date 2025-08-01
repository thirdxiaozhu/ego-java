package org.ruoyi.common.oss.config;

import org.ruoyi.common.oss.config.properties.OssProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 对象存储配置类
 *
 */
@AutoConfiguration
@EnableConfigurationProperties(OssProperties.class)
public class OssConfig {
}
