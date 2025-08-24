package org.ruoyi.chat.controller.image;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ruoyi.chat.service.image.ImageService;
import org.ruoyi.common.chat.request.ImageRequest;
import org.ruoyi.common.core.domain.R;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/image")
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/create")
    @ResponseBody
    public R<Long> createRequest(@RequestBody @Valid ImageRequest imageRequest, HttpServletRequest request) {
        imageService.createAsyncTask(imageRequest);
        return R.ok();
    }
}
