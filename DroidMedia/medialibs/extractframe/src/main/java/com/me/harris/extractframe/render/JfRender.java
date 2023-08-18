package com.me.harris.extractframe.render;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;


import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class JfRender implements GLSurfaceView.Renderer {
    private Context context;

    private final float[] vertexData ={//顶点坐标

            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f

    };

    private final float[] textureData ={//纹理坐标
            0f,1f,
            1f, 1f,
            0f, 0f,
            1f, 0f
    };

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;
    private int program_yuv;
    private int avPosition_yuv;
    private int afPosition_yuv;

    private int sampler_y;
    private int sampler_u;
    private int sampler_v;
    private int[] textureId_yuv;

    //渲染用
    private int width_yuv;
    private int height_yuv;
    private ByteBuffer y;
    private ByteBuffer u;
    private ByteBuffer v;

    public JfRender(Context context){
        this.context = context;
        //存储顶点坐标数据
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        //存储纹理坐标
        textureBuffer = ByteBuffer.allocateDirect(textureData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureData);
        textureBuffer.position(0);
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initRenderYUV();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //用黑色清屏
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        renderYUV();
    }


    /**
     * 初始化
     */
    private void initRenderYUV(){
        String fragmentSource = ShaderUtil.loadFromAssets("fragment_v2_yuv.glsl",context.getResources());
        String vertexSource = ShaderUtil.loadFromAssets("vertext_v2_yuv.glsl",context.getResources());

        //创建一个渲染程序
        program_yuv = ShaderUtil.createProgramv2(vertexSource,fragmentSource);

        //得到着色器中的属性
        avPosition_yuv = GLES20.glGetAttribLocation(program_yuv,"av_Position");
        afPosition_yuv = GLES20.glGetAttribLocation(program_yuv,"af_Position");



        sampler_y = GLES20.glGetUniformLocation(program_yuv, "sampler_y");
        sampler_u = GLES20.glGetUniformLocation(program_yuv, "sampler_u");
        sampler_v = GLES20.glGetUniformLocation(program_yuv, "sampler_v");

        //创建纹理
        textureId_yuv = new int[3];
        GLES20.glGenTextures(3, textureId_yuv, 0);

        for(int i = 0; i < 3; i++)
        {
            //绑定纹理  //它告诉OpenGL下面对纹理的任何操作都是对它所绑定的纹理对象的
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId_yuv[i]);
            ////设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        }
    }



    /**
     * 渲染
     */
    private void renderYUV(){
        if(width_yuv > 0 && height_yuv > 0 && y != null && u != null && v != null){
            GLES20.glUseProgram(program_yuv);//使用源程序

            GLES20.glEnableVertexAttribArray(avPosition_yuv);//使顶点属性数组有效
            GLES20.glVertexAttribPointer(avPosition_yuv, 2, GLES20.GL_FLOAT, false, 8, vertexBuffer);//为顶点属性赋值

            GLES20.glEnableVertexAttribArray(afPosition_yuv);
            GLES20.glVertexAttribPointer(afPosition_yuv, 2, GLES20.GL_FLOAT, false, 8, textureBuffer);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);//激活纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId_yuv[0]);//绑定纹理
            long now = System.nanoTime();
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, width_yuv, height_yuv, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, y);//
            Log.w("=A=","glTexImage2D y data with size " + (y.limit() - y.position())  + " cost " + (System.nanoTime() - now)+ " nano seconds");


            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId_yuv[1]);
            now = System.nanoTime();
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, width_yuv / 2, height_yuv / 2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, u);
            Log.w("=A=","glTexImage2D u data with size " + (u.limit() - u.position())  + " cost " + (System.nanoTime() - now)+ " nano seconds");


            GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId_yuv[2]);
            now = System.nanoTime();
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, width_yuv / 2, height_yuv / 2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, v);
            Log.w("=A=","glTexImage2D v data with size " + (v.limit() - v.position())  + " cost " + (System.nanoTime() - now) + " nano seconds");

            GLES20.glUniform1i(sampler_y, 0);
            GLES20.glUniform1i(sampler_u, 1);
            GLES20.glUniform1i(sampler_v, 2);

            y.clear();
            u.clear();
            v.clear();
            y = null;
            u = null;
            v = null;

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        }
    }

    private void setYUVRenderData(int width, int height, byte[] y, byte[] u, byte[] v)
    {
        this.width_yuv = width;
        this.height_yuv = height;
        this.y = ByteBuffer.wrap(y);
        this.u = ByteBuffer.wrap(u);
        this.v = ByteBuffer.wrap(v);
    }

    // https://www.jianshu.com/p/741466b8f67c
    // https://www.codenong.com/cs109031907/
    public void setYuvData(@Nullable byte[] i420, int width, int height) {
        if (i420!=null){
            this.width_yuv = width;
            this.height_yuv = height;
//            int total = width * height;
//            byte[] copyYuv = Arrays.copyOf(yuv,yuv.length);
//            this.y = ByteBuffer.wrap(copyYuv,0,total);
//            this.u = ByteBuffer.wrap(copyYuv,total,total/4);
//            this.v = ByteBuffer.wrap(copyYuv,total+total/4,total/4);
//            this.y.put(copyYuv,0,total);
//            for(int i = total;i<copyYuv.length;i+=2){
//                u.put(copyYuv[i]);
//                v.put(copyYuv[i+1]);
//            }

            if (y != null) y.clear();
            if (u != null) u.clear();
            if (v != null) v.clear();

            // 该函数多次被调用的时，不要每次都new，可以设置为全局变量缓存起来
            byte[] y = new byte[width * height];
            byte[] u = new byte[width * height / 4];
            byte[] v = new byte[width * height / 4];
            System.arraycopy(i420, 0, y, 0, y.length);
            System.arraycopy(i420, y.length, u, 0, u.length);
            System.arraycopy(i420, y.length + u.length, v, 0, v.length);
            this.y  = ByteBuffer.wrap(y);
            this.u  = ByteBuffer.wrap(u);
            this.v  = ByteBuffer.wrap(v);
        }
    }
}
