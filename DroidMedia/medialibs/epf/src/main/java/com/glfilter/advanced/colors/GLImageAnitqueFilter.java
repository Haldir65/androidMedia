package com.glfilter.advanced.colors;

import android.content.Context;
import android.opengl.GLES30;

import com.glfilter.base.GLImageFilter;
import com.spx.library.util.OpenGLUtils;

/**
 * 古董特效
 * Created by cain on 2017/11/15.
 */

public class GLImageAnitqueFilter extends GLImageFilter {

    private static final String FRAGMENT_SHADER = "" +
            "precision highp float;\n" +
            "varying highp vec2 textureCoordinate;\n" +
            "\n" +
            "uniform sampler2D inputTexture;\n" +
            "uniform sampler2D curveTexture;//curve\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "highp vec4 textureColor;\n" +
            "highp vec4 textureColorRes;\n" +
            "highp float satVal = 65.0 / 100.0;\n" +
            "\n" +
            "float xCoordinate = textureCoordinate.x;\n" +
            "float yCoordinate = textureCoordinate.y;\n" +
            "\n" +
            "highp float redCurveValue;\n" +
            "highp float greenCurveValue;\n" +
            "highp float blueCurveValue;\n" +
            "\n" +
            "textureColor = texture2D( inputTexture, vec2(xCoordinate, yCoordinate));\n" +
            "textureColorRes = textureColor;\n" +
            "\n" +
            "redCurveValue = texture2D(curveTexture, vec2(textureColor.r, 0.0)).r;\n" +
            "greenCurveValue = texture2D(curveTexture, vec2(textureColor.g, 0.0)).g;\n" +
            "blueCurveValue = texture2D(curveTexture, vec2(textureColor.b, 0.0)).b;\n" +
            "\n" +
            "highp float G = (redCurveValue + greenCurveValue + blueCurveValue);\n" +
            "G = G / 3.0;\n" +
            "\n" +
            "redCurveValue = ((1.0 - satVal) * G + satVal * redCurveValue);\n" +
            "greenCurveValue = ((1.0 - satVal) * G + satVal * greenCurveValue);\n" +
            "blueCurveValue = ((1.0 - satVal) * G + satVal * blueCurveValue);\n" +
            "redCurveValue = (((redCurveValue) > (1.0)) ? (1.0) : (((redCurveValue) < (0.0)) ? (0.0) : (redCurveValue)));\n" +
            "greenCurveValue = (((greenCurveValue) > (1.0)) ? (1.0) : (((greenCurveValue) < (0.0)) ? (0.0) : (greenCurveValue)));\n" +
            "blueCurveValue = (((blueCurveValue) > (1.0)) ? (1.0) : (((blueCurveValue) < (0.0)) ? (0.0) : (blueCurveValue)));\n" +
            "\n" +
            "redCurveValue = texture2D(curveTexture, vec2(redCurveValue, 0.0)).a;\n" +
            "greenCurveValue = texture2D(curveTexture, vec2(greenCurveValue, 0.0)).a;\n" +
            "blueCurveValue = texture2D(curveTexture, vec2(blueCurveValue, 0.0)).a;\n" +
            "\n" +
            "highp vec4 base = vec4(redCurveValue, greenCurveValue, blueCurveValue, 1.0);\n" +
            "highp vec4 overlayer = vec4(250.0/255.0, 227.0/255.0, 193.0/255.0, 1.0);\n" +
            "\n" +
            "textureColor = overlayer * base;\n" +
            "base = (textureColor - base) * 0.850980 + base;\n" +
            "textureColor = base; \n" +
            "\n" +
            "gl_FragColor = vec4(textureColor.r, textureColor.g, textureColor.b, 1.0);\n" +
            "}\n";

    private int mCurveTexture;
    private int mCurveTextureHandle;

    public GLImageAnitqueFilter(Context context) {
        this(context, VERTEX_SHADER, FRAGMENT_SHADER);
    }

    public GLImageAnitqueFilter(Context context, String vertexShader, String fragmentShader) {
        super(context, vertexShader, fragmentShader);
    }

    @Override
    public void initProgramHandle() {
        super.initProgramHandle();
        mCurveTextureHandle = GLES30.glGetUniformLocation(mProgramHandle, "curveTexture");
        createTexture();
    }

    /**
     * 创建纹理
     */
    private void createTexture() {
        byte[] arrayOfByte = new byte[2048];
        int[] arrayOfInt1 = { 0, 1, 1, 2, 3, 3, 4, 5, 6, 6, 7, 8, 8, 9, 10, 11, 11, 12, 13, 14, 15, 15, 16, 17, 18, 19, 20, 21, 22, 23, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 48, 49, 50, 51, 52, 53, 55, 56, 57, 58, 59, 61, 62, 63, 64, 66, 67, 68, 69, 71, 72, 73, 74, 76, 77, 78, 79, 81, 82, 83, 85, 86, 87, 89, 90, 91, 93, 94, 95, 96, 98, 99, 100, 102, 103, 104, 106, 107, 108, 110, 111, 112, 114, 115, 116, 118, 119, 120, 122, 123, 124, 126, 127, 128, 130, 131, 132, 134, 135, 136, 137, 139, 140, 141, 143, 144, 145, 146, 148, 149, 150, 152, 153, 154, 155, 157, 158, 159, 160, 161, 163, 164, 165, 166, 168, 169, 170, 171, 172, 173, 175, 176, 177, 178, 179, 180, 181, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 211, 212, 213, 214, 215, 216, 216, 217, 218, 219, 220, 220, 221, 222, 223, 223, 224, 225, 226, 226, 227, 228, 228, 229, 230, 230, 231, 232, 232, 233, 234, 234, 235, 236, 236, 237, 238, 238, 239, 239, 240, 241, 241, 242, 242, 243, 244, 244, 245, 245, 246, 247, 247, 248, 248, 249, 249, 250, 251, 251, 252, 252, 253, 253, 254, 254, 255 };
        int[] arrayOfInt2 = { 15, 15, 16, 17, 18, 19, 20, 20, 21, 22, 23, 23, 24, 25, 26, 27, 28, 29, 30, 31, 31, 32, 33, 34, 35, 37, 38, 39, 40, 41, 42, 43, 43, 44, 45, 46, 48, 49, 50, 51, 52, 53, 55, 56, 57, 57, 58, 59, 61, 62, 63, 64, 66, 67, 68, 69, 71, 72, 72, 73, 74, 76, 77, 78, 79, 81, 82, 83, 85, 86, 87, 87, 89, 90, 91, 93, 94, 95, 96, 98, 99, 100, 102, 102, 103, 104, 106, 107, 108, 110, 111, 112, 114, 115, 116, 118, 118, 119, 120, 122, 123, 124, 126, 127, 128, 130, 131, 132, 134, 134, 135, 136, 137, 139, 140, 141, 143, 144, 145, 146, 148, 149, 149, 150, 152, 153, 154, 155, 157, 158, 159, 160, 161, 163, 163, 164, 165, 166, 168, 169, 170, 171, 172, 173, 175, 176, 177, 177, 178, 179, 180, 181, 183, 184, 185, 186, 187, 188, 189, 190, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 211, 212, 212, 213, 214, 215, 216, 216, 217, 218, 219, 220, 220, 221, 222, 222, 223, 223, 224, 225, 226, 226, 227, 228, 228, 229, 230, 230, 230, 231, 232, 232, 233, 234, 234, 235, 236, 236, 237, 238, 238, 238, 239, 239, 240, 241, 241, 242, 242, 243, 244, 244, 245, 245, 245, 246, 247, 247, 248, 248, 249, 249, 250, 251, 251, 252, 252, 252, 253, 253, 254, 254, 255 };
        int[] arrayOfInt3 = { 87, 89, 89, 90, 90, 91, 91, 93, 93, 94, 95, 95, 96, 96, 98, 98, 99, 100, 100, 102, 102, 103, 103, 104, 104, 106, 107, 107, 108, 108, 110, 110, 111, 112, 112, 114, 114, 115, 115, 116, 118, 118, 119, 119, 120, 120, 122, 123, 123, 124, 124, 126, 126, 127, 128, 128, 130, 130, 131, 131, 132, 134, 134, 135, 135, 136, 136, 137, 139, 139, 140, 140, 141, 143, 143, 144, 144, 145, 146, 146, 148, 148, 149, 150, 150, 152, 152, 153, 154, 154, 155, 155, 157, 158, 158, 159, 159, 160, 161, 161, 163, 163, 164, 165, 165, 166, 168, 168, 169, 169, 170, 171, 171, 172, 173, 173, 175, 175, 176, 177, 177, 178, 179, 179, 180, 181, 181, 183, 183, 184, 185, 185, 186, 187, 187, 188, 189, 189, 190, 191, 191, 192, 193, 193, 194, 195, 195, 196, 197, 197, 198, 199, 199, 200, 201, 201, 202, 203, 204, 204, 205, 206, 206, 207, 208, 208, 209, 210, 211, 211, 211, 212, 212, 213, 214, 215, 215, 216, 216, 217, 217, 218, 219, 219, 220, 220, 221, 221, 222, 223, 223, 223, 224, 225, 226, 226, 226, 227, 228, 228, 228, 229, 230, 230, 230, 231, 232, 232, 232, 233, 234, 234, 235, 235, 236, 236, 237, 238, 238, 238, 239, 239, 240, 240, 241, 241, 242, 242, 242, 243, 244, 244, 244, 245, 245, 246, 247, 247, 247, 248, 248, 249, 249, 249, 250, 251, 251, 252, 252, 252, 253, 253, 254, 254, 254, 255 };
        int[] arrayOfInt4 = { 0, 1, 1, 2, 3, 4, 4, 5, 6, 7, 7, 8, 9, 10, 10, 11, 12, 13, 13, 14, 15, 16, 17, 17, 18, 19, 20, 20, 21, 22, 23, 24, 24, 25, 26, 27, 28, 29, 29, 30, 31, 32, 33, 34, 35, 36, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 64, 65, 66, 67, 68, 69, 70, 72, 73, 74, 75, 76, 78, 79, 80, 81, 83, 84, 85, 86, 88, 89, 90, 91, 93, 94, 95, 97, 98, 99, 101, 102, 103, 105, 106, 107, 109, 110, 111, 113, 114, 115, 117, 118, 119, 121, 122, 123, 125, 126, 127, 129, 130, 131, 133, 134, 136, 137, 138, 140, 141, 142, 144, 145, 146, 148, 149, 150, 152, 153, 154, 156, 157, 158, 159, 161, 162, 163, 165, 166, 167, 168, 170, 171, 172, 173, 175, 176, 177, 178, 180, 181, 182, 183, 184, 186, 187, 188, 189, 190, 191, 192, 193, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 213, 214, 215, 216, 217, 218, 219, 219, 220, 221, 222, 223, 223, 224, 225, 226, 227, 227, 228, 229, 230, 230, 231, 232, 232, 233, 234, 234, 235, 236, 236, 237, 238, 238, 239, 240, 240, 241, 242, 242, 243, 244, 244, 245, 245, 246, 247, 247, 248, 248, 249, 250, 250, 251, 251, 252, 253, 253, 254, 254, 255 };
        for (int i = 0; i < 256; i++){
            arrayOfByte[(i * 4)] = ((byte)arrayOfInt1[i]);
            arrayOfByte[(1 + i * 4)] = ((byte)arrayOfInt2[i]);
            arrayOfByte[(2 + i * 4)] = ((byte)arrayOfInt3[i]);
            arrayOfByte[(3 + i * 4)] = ((byte)arrayOfInt4[i]);
        }
        int[] arrayOfInt5 = { 0, 0, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 5, 5, 5, 6, 6, 6, 7, 7, 8, 8, 8, 9, 9, 10, 10, 10, 11, 11, 11, 12, 12, 13, 13, 13, 14, 14, 14, 15, 15, 16, 16, 16, 17, 17, 17, 18, 18, 18, 19, 19, 20, 20, 20, 21, 21, 21, 22, 22, 23, 23, 23, 24, 24, 24, 25, 25, 25, 25, 26, 26, 27, 27, 28, 28, 28, 28, 29, 29, 30, 29, 31, 31, 31, 31, 32, 32, 33, 33, 34, 34, 34, 34, 35, 35, 36, 36, 37, 37, 37, 38, 38, 39, 39, 39, 40, 40, 40, 41, 42, 42, 43, 43, 44, 44, 45, 45, 45, 46, 47, 47, 48, 48, 49, 50, 51, 51, 52, 52, 53, 53, 54, 55, 55, 56, 57, 57, 58, 59, 60, 60, 61, 62, 63, 63, 64, 65, 66, 67, 68, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 88, 89, 90, 91, 93, 94, 95, 96, 97, 98, 100, 101, 103, 104, 105, 107, 108, 110, 111, 113, 115, 116, 118, 119, 120, 122, 123, 125, 127, 128, 130, 132, 134, 135, 137, 139, 141, 143, 144, 146, 148, 150, 152, 154, 156, 158, 160, 163, 165, 167, 169, 171, 173, 175, 178, 180, 182, 185, 187, 189, 192, 194, 197, 199, 201, 204, 206, 209, 211, 214, 216, 219, 221, 224, 226, 229, 232, 234, 236, 239, 241, 245, 247, 250, 252, 255 };
        for (int j = 0; j < 256; j++){
            arrayOfByte[(1024 + j * 4)] = ((byte)arrayOfInt5[j]);
            arrayOfByte[(1 + (1024 + j * 4))] = ((byte)arrayOfInt5[j]);
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
