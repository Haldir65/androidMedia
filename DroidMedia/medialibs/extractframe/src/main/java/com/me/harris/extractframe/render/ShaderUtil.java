package com.me.harris.extractframe.render;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class ShaderUtil {


    public static final String TAG = "ShaderUtils";

    public static int loadShader(int type, String source) {
        // 1. create shader
        int shader = GLES30.glCreateShader(type);
        if (shader == GLES30.GL_NONE) {
            Log.e(TAG, "create shared failed! type: " + type);
            return GLES30.GL_NONE;
        }
        // 2. load shader source
        GLES30.glShaderSource(shader, source);
        // 3. compile shared source
        GLES30.glCompileShader(shader);
        // 4. check compile status
        int[] compiled = new int[1];
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == GLES30.GL_FALSE) { // compile failed
            Log.e(TAG, "Error compiling shader. type: " + type + ":");
            Log.e(TAG, GLES30.glGetShaderInfoLog(shader));
            GLES30.glDeleteShader(shader); // delete shader
            shader = GLES30.GL_NONE;
        }
        return shader;
    }

    public static int createProgram(String vertexSource, String fragmentSource) {
        // 1. load shader
        int vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == GLES30.GL_NONE) {
            Log.e(TAG, "load vertex shader failed! ");
            return GLES30.GL_NONE;
        }
        int fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentSource);
        if (fragmentShader == GLES30.GL_NONE) {
            Log.e(TAG, "load fragment shader failed! ");
            return GLES30.GL_NONE;
        }
        // 2. create gl program
        int program = GLES30.glCreateProgram();
        if (program == GLES30.GL_NONE) {
            Log.e(TAG, "create program failed! ");
            return GLES30.GL_NONE;
        }
        // 3. attach shader
        GLES30.glAttachShader(program, vertexShader);
        GLES30.glAttachShader(program, fragmentShader);
        // we can delete shader after attach
        GLES30.glDeleteShader(vertexShader);
        GLES30.glDeleteShader(fragmentShader);
        // 4. link program
        GLES30.glLinkProgram(program);
        // 5. check link status
        int[] linkStatus = new int[1];
        GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == GLES30.GL_FALSE) { // link failed
            Log.e(TAG, "Error link program: ");
            Log.e(TAG, GLES30.glGetProgramInfoLog(program));
            GLES30.glDeleteProgram(program); // delete program
            return GLES30.GL_NONE;
        }
        return program;
    }

    public static String loadFromAssets(String fileName, Resources resources) {
        String result = null;
        try {
            InputStream is = resources.getAssets().open(fileName);
            int length = is.available();
            byte[] data = new byte[length];
            is.read(data);
            is.close();
            result = new String(data, "UTF-8");
            result.replace("\\r\\n", "\\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int loadShaderv2(int shaderType, String source)
    {
        int shader = GLES20.glCreateShader(shaderType);
        if(shader != 0)
        {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compile = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compile, 0);
            if(compile[0] != GLES20.GL_TRUE)
            {
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    public static int createProgramv2(String vertexSource, String fragmentSource)
    {
        int vertexShader = loadShaderv2(GLES20.GL_VERTEX_SHADER, vertexSource);
        if(vertexShader == 0)
        {
            return 0;
        }
        int fragmentShader = loadShaderv2(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if(fragmentShader == 0)
        {
            return 0;
        }
        int program = GLES20.glCreateProgram();
        if(program != 0)
        {
            GLES20.glAttachShader(program, vertexShader);
            GLES20.glAttachShader(program, fragmentShader);
            GLES20.glLinkProgram(program);
            int[] linsStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linsStatus, 0);
            if(linsStatus[0] != GLES20.GL_TRUE)
            {
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return  program;
    }


}
