package org.ruoyi.chat.enums.qwen;

import lombok.Getter;
import org.ruoyi.chat.enums.DisplayType;

@Getter
public enum SketchStyle {
    AUTO(0, "<auto>"),
    CARTOON3D(1, "<3d cartoon>"),
    ANIME(2, "<anime>"),
    OILPAINTING(3, "<oil painting>"),
    WATERCOLOR(4, "<watercolor>"),
    SKETCH(5, "<sketch>"),
    CHINESEPAINTING(6, "<chinese painting>"),
    FLATILLUSTRATION(7, "<flat illustration>");

    private final int key;
    private final String value;

    SketchStyle(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public static SketchStyle fromCode(int key) {
        for (SketchStyle style : values()) {
            if (style.getKey()== key) {
                return style;
            }
        }
        return null;
    }
}
