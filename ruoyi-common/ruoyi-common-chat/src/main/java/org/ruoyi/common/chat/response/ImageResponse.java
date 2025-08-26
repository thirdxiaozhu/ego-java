package org.ruoyi.common.chat.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@Builder
public class ImageResponse implements Serializable {
    private String requestID;

    private String taskID;

    private String taskStatus;

    private List<ImageResult> results;

    private int totalImages;

    private int succeedImages;

    private int failedImages;
}
