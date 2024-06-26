package com.daasuu.epf.filter2;

import com.daasuu.epf.filter.FilterType;
import com.daasuu.epf.filter.GlFilter;

public class BlackAndWhiteEffectFilter extends GlFilter {

    public BlackAndWhiteEffectFilter(){
        super(DEFAULT_VERTEX_SHADER,FRAGMENT_SHADER);
    }

    @Override
    public FilterType getFilterType() {
        return FilterType.BLACK_AND_WHITE_FILTER;
    }


    private static final String FRAGMENT_SHADER =
            "#extension GL_OES_EGL_image_external : require\n"
                    + "precision mediump float;\n"
                    + "varying vec2 vTextureCoord;\n"
                    + "uniform samplerExternalOES sTexture;\n" + "void main() {\n"
                    + "  vec4 color = texture2D(sTexture, vTextureCoord);\n"
                    + "  float colorR = (color.r + color.g + color.b) / 3.0;\n"
                    + "  float colorG = (color.r + color.g + color.b) / 3.0;\n"
                    + "  float colorB = (color.r + color.g + color.b) / 3.0;\n"
                    + "  gl_FragColor = vec4(colorR, colorG, colorB, color.a);\n"
                    + "}\n";

}
