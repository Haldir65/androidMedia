package com.cgfay.filterlibrary.filter;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES30;
import android.opengl.Matrix;

import com.cgfay.filterlibrary.util.TextureRotationUtils;


/**
 * Created by cain on 2017/7/9.
 */

public class OESInputFilter extends BaseImageFilter {
    private static final String VERTEX_SHADER =
            "uniform mat4 uMVPMatrix;                               \n" +
            "uniform mat4 uTexMatrix;                               \n" +
            "attribute vec4 aPosition;                              \n" +
            "attribute vec4 aTextureCoord;                          \n" +
            "varying vec2 textureCoordinate;                            \n" +
            "void main() {                                          \n" +
            "    gl_Position = uMVPMatrix * aPosition;              \n" +
            "    textureCoordinate = (uTexMatrix * aTextureCoord).xy;   \n" +
            "}                                                      \n";

    // 备注： samplerExternalOES 格式是相机流的数据，跟渲染后的texture格式不一样
    // 相机流的数据经过渲染后得到的texture格式是sampler2D的
    private static final String FRAGMENT_SHADER_OES =
            "#extension GL_OES_EGL_image_external : require         \n" +
            "precision mediump float;                               \n" +
            "varying vec2 textureCoordinate;                            \n" +
            "uniform samplerExternalOES inputTexture;                   \n" +
            "void main() {                                          \n" +
            "    gl_FragColor = texture2D(inputTexture, textureCoordinate); \n" +
            "}                                                      \n";


    private int muTexMatrixLoc;
    private float[] mTextureMatrix;

    public OESInputFilter(Context context) {
        this(context, VERTEX_SHADER, FRAGMENT_SHADER_OES);
    }

    public OESInputFilter(Context context, String vertexShader, String fragmentShader) {
        super(context, vertexShader, fragmentShader);
        muTexMatrixLoc = GLES30.glGetUniformLocation(mProgramHandle, "uTexMatrix");
        // 视图矩阵
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -1, 0f, 0f, 0f, 0f, 1f, 0f);
    }

    @Override
    public void onInputSizeChanged(int width, int height) {
        super.onInputSizeChanged(width, height);
        float aspect = (float) width / height; // 计算宽高比
        Matrix.perspectiveM(mProjectionMatrix, 0, 60, aspect, 2, 10);
    }

    @Override
    public int getTextureType() {
        return GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
    }

    @Override
    public void onDrawArraysBegin() {
        GLES30.glUniformMatrix4fv(muTexMatrixLoc, 1, false, mTextureMatrix, 0);
    }

    public void updateTextureBuffer() {
        mTextureBuffer = TextureRotationUtils.getTextureBuffer();
    }

    /**
     * 设置SurfaceTexture的变换矩阵
     * @param texMatrix
     */
    public void setTextureTransformMatirx(float[] texMatrix) {
        mTextureMatrix = texMatrix;
    }

    /**
     * 镜像翻转
     * @param coords
     * @param matrix
     * @return
     */
    private float[] transformTextureCoordinates(float[] coords, float[] matrix) {
        float[] result = new float[coords.length];
        float[] vt = new float[4];

        for (int i = 0; i < coords.length; i += 2) {
            float[] v = { coords[i], coords[i + 1], 0, 1 };
            Matrix.multiplyMV(vt, 0, matrix, 0, v, 0);
            result[i] = vt[0];// x轴镜像
            // result[i + 1] = vt[1];y轴镜像
            result[i + 1] = coords[i + 1];
        }
        return result;
    }
}
