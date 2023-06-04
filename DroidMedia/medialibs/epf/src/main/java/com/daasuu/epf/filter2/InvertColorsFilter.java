package com.daasuu.epf.filter2;

import android.opengl.GLSurfaceView;

import com.daasuu.epf.filter.FilterType;
import com.daasuu.epf.filter.GlFilter;

public class InvertColorsFilter extends GlFilter {

    @Override
    public FilterType getFilterType() {
        return FilterType.INVERT_COLORS;
    }

    private static final String FRAGMENT_SHADER =
            "#extension GL_OES_EGL_image_external : require\n"
                    + "precision mediump float;\n"
                    + "varying vec2 vTextureCoord;\n"
                    + "uniform samplerExternalOES sTexture;\n" + "void main() {\n"
                    + "  vec4 color = texture2D(sTexture, vTextureCoord);\n"
                    + "  float colorR = (1.0 - color.r) / 1.0;\n"
                    + "  float colorG = (1.0 - color.g) / 1.0;\n"
                    + "  float colorB = (1.0 - color.b) / 1.0;\n"
                    + "  gl_FragColor = vec4(colorR, colorG, colorB, color.a);\n"
                    + "}\n";


    public InvertColorsFilter() {
        super(DEFAULT_VERTEX_SHADER, FRAGMENT_SHADER);
    }



}
