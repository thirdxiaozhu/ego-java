package org.ruoyi.chat.controller.image;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ruoyi.chat.service.image.IImageService;
import org.ruoyi.common.chat.request.ImageRequest;
import org.ruoyi.common.core.domain.R;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/image")
public class ImageController {

    private final IImageService imageService;

    @PostMapping("/sync")
    @ResponseBody
    public R<String> createSyncRequest(@RequestBody @Valid ImageRequest imageRequest, HttpServletRequest request) throws JsonProcessingException {
        return R.ok(imageService.createSyncTask(imageRequest));
    }

    @PostMapping("/async")
    @ResponseBody
    public R<String> createAsyncRequest(@RequestBody @Valid ImageRequest imageRequest, HttpServletRequest request) {
        return R.ok(imageService.createAsyncTask(imageRequest));
//        return R.ok( imageService.createAsyncTask(imageRequest));

    }

    @PostMapping("/edit")
    @ResponseBody
    public R<String> createEditTask(@RequestBody @Valid ImageRequest imageRequest, HttpServletRequest request) throws JsonProcessingException {
        return R.ok( imageService.syncEditCall(imageRequest));
    }

    @PostMapping("/sketch")
    @ResponseBody
    public R<String> createSketchTask(@RequestBody @Valid ImageRequest imageRequest, HttpServletRequest request) throws JsonProcessingException {
        return R.ok( imageService.syncSketchCall(imageRequest));
    }

}