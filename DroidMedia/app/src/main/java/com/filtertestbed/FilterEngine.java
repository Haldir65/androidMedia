package com.filtertestbed;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;



import com.me.harris.droidmedia.R;

/**
 * Created by lb6905 on 2017/6/12.
 */

public class FilterEngine {

    private static FilterEngine filterEngine = null;

    private Context mContext;
    private FloatBuffer mBuffer;
    private int mOESTextureId = -1;
    private int vertexShader = -1;
    private int fragmentShader = -1;

    private int mShaderProgram = -1;

    private int aPositionLocation = -1;
    private int aTextureCoordLocation = -1;
    private int uTextureMatrixLocation = -1;
    private int uTextureSamplerLocation = -1;

    public FilterEngine(int OESTextureId, Context context) {
        mContext = context;
        mOESTextureId = OESTextureId;
        mBuffer = createBuffer(vertexData);
        vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, Utils.readShaderFromResource(mContext, R.raw.base_vertex_shader));
        fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, Utils.readShaderFromResource(mContext, R.raw.base_fragment_shader));
        mShaderProgram = linkProgram(vertexShader, fragmentShader);
    }

    /*public static FilterEngine getInstance() {
        if (filterEngine == null) {
            synchronized (FilterEngine.class) {
                if (filterEngine == null)
                    filterEngine = new FilterEngine();
            }
        }
        return filterEngine;
    }*/

    private static final float[] vertexData = {
            1f, 1f, 1f, 1f,
            -1f, 1f, 0f, 1f,
            -1f, -1f, 0f, 0f,
            1f, 1f, 1f, 1f,
            -1f, -1f, 0f, 0f,
            1f, -1f, 1f, 0f
    };

    public static final String POSITION_ATTRIBUTE = "aPosition";
    public static final String TEXTURE_COORD_ATTRIBUTE = "aTextureCoordinate";
    public static final String TEXTURE_MATRIX_UNIFORM = "uTextureMatrix";
    public static final String TEXTURE_SAMPLER_UNIFORM = "uTextureSampler";

    public FloatBuffer createBuffer(float[] vertexData) {
        FloatBuffer buffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        buffer.put(vertexData, 0, vertexData.length).position(0);
        return buffer;
    }

    public int loadShader(int type, String shaderSource) {
        int shader = GLES20.glCreateShader(type);
        if (shader == 0) {
            throw new RuntimeException("Create Shader Failed!" + GLES20.glGetError());
        }
        GLES20.glShaderSource(shader, shaderSource);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public int linkProgram(int verShader, int fragShader) {
        int program = GLES20.glCreateProgram();
        if (program == 0) {
            throw new RuntimeException("Create Program Failed!" + GLES20.glGetError());
        }
        GLES20.glAttachShader(program, verShader);
        GLES20. glAttachShader(program, fragShader);
        GLES20.glLinkProgram(program);

        GLES20.glUseProgram(program);
        return program;
    }

    public void drawTexture(float[] transformMatrix) {
        aPositionLocation = GLES20.glGetAttribLocation(mShaderProgram, FilterEngine.POSITION_ATTRIBUTE);
        aTextureCoordLocation = GLES20.glGetAttribLocation(mShaderProgram, FilterEngine.TEXTURE_COORD_ATTRIBUTE);
        uTextureMatrixLocation = GLES20.glGetUniformLocation(mShaderProgram, FilterEngine.TEXTURE_MATRIX_UNIFORM);
        uTextureSamplerLocation = GLES20.glGetUniformLocation(mShaderProgram, FilterEngine.TEXTURE_SAMPLER_UNIFORM);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mOESTextureId);
        GLES20.glUniform1i(uTextureSamplerLocation, 0);
        GLES20.glUniformMatrix4fv(uTextureMatrixLocation, 1, false, transformMatrix, 0);

        if (mBuffer != null) {
            mBuffer.position(0);
            GLES20.glEnableVertexAttribArray(aPositionLocation);
            GLES20.glVertexAttribPointer(aPositionLocation, 2, GLES20.GL_FLOAT, false, 16, mBuffer);

            mBuffer.position(2);
            GLES20.glEnableVertexAttribArray(aTextureCoordLocation);
            GLES20. glVertexAttribPointer(aTextureCoordLocation, 2, GLES20.GL_FLOAT, false, 16, mBuffer);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
        }
    }

    public int getShaderProgram() {
        return mShaderProgram;
    }

    public FloatBuffer getBuffer() {
        return mBuffer;
    }

    public int getOESTextureId() {
        return mOESTextureId;
    }

    public void setOESTextureId(int OESTextureId) {
        mOESTextureId = OESTextureId;
    }
}

