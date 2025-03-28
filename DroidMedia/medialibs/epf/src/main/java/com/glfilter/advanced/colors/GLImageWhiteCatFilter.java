package com.glfilter.advanced.colors;

import android.content.Context;
import android.opengl.GLES30;

import com.glfilter.base.GLImageFilter;
import com.spx.library.util.OpenGLUtils;

/**
 * 白猫滤镜
 * Created by cain on 2017/11/15.
 */

public class GLImageWhiteCatFilter extends GLImageFilter {

    private static final String FRAGMENT_SHADER = "" +
            "precision highp float;\n" +
            "varying highp vec2 textureCoordinate;\n" +
            "uniform sampler2D inputTexture;\n" +
            "uniform sampler2D curveTexture;  //curve\n" +
            "\n" +
            "vec3 rgb2hsv(vec3 c) \n" +
            "{ \n" +
            "vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0); \n" +
            "vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));\n" +
            "vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r)); \n" +
            "\n" +
            "float d = q.x - min(q.w, q.y); \n" +
            "float e = 1.0e-10; \n" +
            "return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x); \n" +
            "} \n" +
            "\n" +
            "vec3 hsv2rgb(vec3 c) \n" +
            "{ \n" +
            "vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0); \n" +
            "vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www); \n" +
            "return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y); \n" +
            "} \n" +
            "\n" +
            "void main() \n" +
            "{ \n" +
            "    float GreyVal; \n" +
            "lowp vec4 textureColor; \n" +
            "lowp vec4 textureColorOri;\n" +
            "float xCoordinate = textureCoordinate.x;\n" +
            "float yCoordinate = textureCoordinate.y;\n" +
            "\n" +
            "highp float redCurveValue;\n" +
            "highp float greenCurveValue;\n" +
            "highp float blueCurveValue; \n" +
            "\n" +
            "textureColor = texture2D( inputTexture, vec2(xCoordinate, yCoordinate));\n" +
            "\n" +
            "// step1 20% opacity  ExclusionBlending \n" +
            "    mediump vec4 textureColor2 = textureColor; \n" +
            "textureColor2 = textureColor + textureColor2 - (2.0 * textureColor2 * textureColor); \n" +
            "\n" +
            "textureColor = (textureColor2 - textureColor) * 0.2 + textureColor; \n" +
            "\n" +
            "    // step2 curve \n" +
            "    redCurveValue = texture2D(curveTexture, vec2(textureColor.r, 0.0)).r;\n" +
            "greenCurveValue = texture2D(curveTexture, vec2(textureColor.g, 0.0)).g;\n" +
            "blueCurveValue = texture2D(curveTexture, vec2(textureColor.b, 0.0)).b;\n" +
            "\n" +
            "    redCurveValue = texture2D(curveTexture, vec2(redCurveValue, 1.0)).r;\n" +
            "greenCurveValue = texture2D(curveTexture, vec2(greenCurveValue, 1.0)).r;\n" +
            "blueCurveValue = texture2D(curveTexture, vec2(blueCurveValue, 1.0)).r;\n" +
            "\n" +
            "redCurveValue = texture2D(curveTexture, vec2(redCurveValue, 1.0)).g;\n" +
            "greenCurveValue = texture2D(curveTexture, vec2(greenCurveValue, 1.0)).g;\n" +
            "blueCurveValue = texture2D(curveTexture, vec2(blueCurveValue, 1.0)).g;\n" +
            "\n" +
            "\n" +
            "vec3 tColor = vec3(redCurveValue, greenCurveValue, blueCurveValue); \n" +
            "tColor = rgb2hsv(tColor); \n" +
            "\n" +
            "tColor.g = tColor.g * 0.65; \n" +
            "\n" +
            "tColor = hsv2rgb(tColor); \n" +
            "    tColor = clamp(tColor, 0.0, 1.0); \n" +
            "\n" +
            "    mediump vec4 base = vec4(tColor, 1.0); \n" +
            "mediump vec4 overlay = vec4(0.62, 0.6, 0.498, 1.0); \n" +
            "// step6 overlay blending \n" +
            "    mediump float ra; \n" +
            "if (base.r < 0.5) \n" +
            "{ \n" +
            "ra = overlay.r * base.r * 2.0;\n" +
            "} else \n" +
            "{ \n" +
            "ra = 1.0 - ((1.0 - base.r) * (1.0 - overlay.r) * 2.0);\n" +
            "}\n" +
            "\n" +
            "    mediump float ga; \n" +
            "if (base.g < 0.5) \n" +
            "{ \n" +
            "ga = overlay.g * base.g * 2.0; \n" +
            "} else \n" +
            "{ \n" +
            "ga = 1.0 - ((1.0 - base.g) * (1.0 - overlay.g) * 2.0); \n" +
            "} \n" +
            "\n" +
            "mediump float ba; \n" +
            "if (base.b < 0.5) \n" +
            "{ \n" +
            "ba = overlay.b * base.b * 2.0; \n" +
            "} else \n" +
            "{ \n" +
            "ba = 1.0 - ((1.0 - base.b) * (1.0 - overlay.b) * 2.0); \n" +
            "} \n" +
            "textureColor = vec4(ra, ga, ba, 1.0); \n" +
            "textureColor = (textureColor - base) * 0.1 + base; \n" +
            "\n" +
            "gl_FragColor = vec4(textureColor.r, textureColor.g, textureColor.b, 1.0); \n" +
            "} \n";

    private int mCurveTexture;
    private int mCurveTextureHandle;

    public GLImageWhiteCatFilter(Context context) {
        this(context, VERTEX_SHADER, FRAGMENT_SHADER);
    }

    public GLImageWhiteCatFilter(Context context, String vertexShader, String fragmentShader) {
        super(context, vertexShader, fragmentShader);
        mCurveTextureHandle = GLES30.glGetUniformLocation(mProgramHandle, "curveTexture");
        createTexture();
    }


    private void createTexture() {
        byte[] arrayOfByte = new byte[2048];
        int[] arrayOfInt1 = { 0, 0, 0, 0, 0, 0, 0, 1, 2, 4, 5, 6, 7, 8, 10, 11, 12, 13, 14, 16, 17, 18, 19, 20, 22, 23, 24, 25, 26, 28, 29, 30, 31, 32, 33, 35, 36, 37, 38, 39, 41, 42, 43, 44, 45, 46, 48, 49, 50, 51, 52, 54, 55, 56, 57, 58, 59, 61, 62, 63, 64, 65, 66, 67, 69, 70, 71, 72, 73, 74, 75, 77, 78, 79, 80, 81, 82, 83, 85, 86, 87, 88, 89, 90, 91, 92, 93, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 236, 237, 238, 239, 240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 249, 250, 251, 252, 253, 254, 255, 255, 255, 255, 255, 255 };
        int[] arrayOfInt2 = { 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 4, 4, 5, 6, 7, 8, 10, 11, 12, 12, 13, 14, 16, 17, 18, 19, 19, 20, 22, 23, 24, 25, 26, 26, 28, 29, 30, 31, 32, 33, 35, 35, 36, 37, 38, 39, 41, 42, 42, 43, 44, 45, 46, 48, 49, 50, 50, 51, 52, 54, 55, 56, 57, 58, 58, 59, 61, 62, 63, 64, 65, 66, 66, 67, 69, 70, 71, 72, 73, 74, 75, 75, 77, 78, 79, 80, 81, 82, 83, 85, 85, 86, 87, 88, 89, 90, 91, 92, 93, 93, 95, 96, 97, 98, 99, 100, 101, 102, 103, 103, 104, 105, 107, 108, 109, 110, 111, 112, 113, 114, 114, 115, 116, 117, 118, 119, 120, 121, 123, 124, 125, 126, 127, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 141, 142, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 182, 183, 184, 185, 186, 187, 188, 189, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 217, 218, 219, 220, 221, 222, 223, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 236, 237, 238, 239, 240, 240 };
        for (int i = 0; i < 256; i++){
            arrayOfByte[(i * 4)] = ((byte)arrayOfInt1[i]);
            arrayOfByte[(1 + i * 4)] = ((byte)arrayOfInt1[i]);
            arrayOfByte[(2 + i * 4)] = ((byte)arrayOfInt2[i]);
            arrayOfByte[(3 + i * 4)] = -1;
        }
        int[] arrayOfInt3 = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 6, 9, 14, 17, 19, 22, 25, 27, 30, 34, 36, 39, 41, 43, 45, 49, 51, 52, 54, 55, 57, 58, 61, 63, 64, 65, 67, 68, 69, 72, 73, 75, 76, 77, 78, 81, 82, 83, 84, 86, 87, 88, 90, 91, 93, 94, 95, 96, 97, 99, 100, 101, 102, 103, 105, 106, 108, 109, 110, 111, 112, 113, 115, 116, 117, 118, 119, 120, 121, 123, 124, 125, 126, 126, 127, 128, 130, 131, 132, 133, 134, 135, 136, 138, 138, 139, 140, 141, 142, 144, 145, 146, 146, 147, 148, 149, 151, 152, 153, 153, 154, 155, 156, 158, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 168, 170, 171, 172, 172, 173, 174, 175, 176, 177, 178, 179, 180, 180, 181, 183, 183, 184, 185, 186, 186, 188, 189, 190, 190, 191, 192, 193, 194, 195, 196, 196, 197, 198, 199, 200, 201, 201, 202, 203, 204, 204, 206, 207, 207, 208, 209, 209, 211, 212, 212, 213, 214, 214, 215, 217, 217, 218, 219, 219, 220, 221, 222, 223, 224, 224, 225, 226, 227, 228, 228, 229, 230, 230, 231, 233, 233, 234, 235, 235, 236, 237, 238, 239, 239, 240, 241, 241, 242, 243, 244, 245, 245, 246, 247, 248, 249, 249, 250, 250, 251, 252, 253, 254, 254, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255 };
        int[] arrayOfInt4 = { 0, 2, 4, 6, 8, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 31, 32, 34, 36, 38, 40, 42, 44, 46, 47, 49, 51, 53, 54, 56, 58, 60, 61, 63, 65, 66, 68, 70, 71, 73, 74, 76, 77, 79, 80, 82, 83, 85, 86, 88, 89, 91, 92, 93, 95, 96, 98, 99, 100, 101, 103, 104, 105, 107, 108, 109, 110, 111, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 124, 125, 126, 127, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 142, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 198, 199, 200, 201, 202, 203, 204, 205, 206, 206, 207, 208, 209, 210, 211, 212, 212, 213, 214, 215, 216, 216, 217, 218, 219, 219, 220, 221, 222, 222, 223, 224, 224, 225, 226, 226, 227, 228, 228, 229, 230, 230, 231, 232, 232, 233, 233, 234, 235, 235, 236, 236, 237, 237, 238, 238, 239, 239, 240, 240, 241, 241, 242, 242, 243, 243, 244, 244, 245, 245, 245, 246, 246, 247, 247, 247, 248, 248, 248, 249, 249, 249, 250, 250, 250, 251, 251, 251, 252, 252, 252, 252, 253, 253, 253, 253, 254, 254, 254, 254, 254, 255, 255, 255 };
        int[] arrayOfInt5 = { 0, 0, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 5, 5, 5, 6, 6, 6, 7, 7, 8, 8, 8, 9, 9, 10, 10, 10, 11, 11, 11, 12, 12, 13, 13, 13, 14, 14, 14, 15, 15, 16, 16, 16, 17, 17, 17, 18, 18, 18, 19, 19, 20, 20, 20, 21, 21, 21, 22, 22, 23, 23, 23, 24, 24, 24, 25, 25, 25, 25, 26, 26, 27, 27, 28, 28, 28, 28, 29, 29, 30, 29, 31, 31, 31, 31, 32, 32, 33, 33, 34, 34, 34, 34, 35, 35, 36, 36, 37, 37, 37, 38, 38, 39, 39, 39, 40, 40, 40, 41, 42, 42, 43, 43, 44, 44, 45, 45, 45, 46, 47, 47, 48, 48, 49, 50, 51, 51, 52, 52, 53, 53, 54, 55, 55, 56, 57, 57, 58, 59, 60, 60, 61, 62, 63, 63, 64, 65, 66, 67, 68, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 88, 89, 90, 91, 93, 94, 95, 96, 97, 98, 100, 101, 103, 104, 105, 107, 108, 110, 111, 113, 115, 116, 118, 119, 120, 122, 123, 125, 127, 128, 130, 132, 134, 135, 137, 139, 141, 143, 144, 146, 148, 150, 152, 154, 156, 158, 160, 163, 165, 167, 169, 171, 173, 175, 178, 180, 182, 185, 187, 189, 192, 194, 197, 199, 201, 204, 206, 209, 211, 214, 216, 219, 221, 224, 226, 229, 232, 234, 236, 239, 241, 245, 247, 250, 252, 255 };
        for (int j = 0; j < 256; j++){
            arrayOfByte[(1024 + j * 4)] = ((byte)arrayOfInt4[j]);
            arrayOfByte[(1 + (1024 + j * 4))] = ((byte)arrayOfInt3[j]);
            arrayOfByte[(2 + (1024 + j * 4))] = ((byte)arrayOfInt5[j]);
            arrayOfByte[(3 + (1024 + j * 4))] = -1;
        }
        mCurveTexture = OpenGLUtils.createTexture(arrayOfByte, 256, 2);
    }

    @Override
    public void onDrawFrameBegin() {
        super.onDrawFrameBegin();
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        GLES30.glBindTexture(getTextureType(), mCurveTexture);
        GLES30.glUniform1i(mCurveTextureHandle, 1);
    }

    @Override
    public void release() {
        GLES30.glDeleteTextures(1, new int[]{mCurveTexture}, 0);
        super.release();
    }

}
