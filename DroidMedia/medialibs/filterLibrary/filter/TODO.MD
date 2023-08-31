[加滤镜的项目](https://github.com/DiskyZhs/PinFilter)
[Android音视频编码那点破事](https://www.jianshu.com/p/aac2f8699735) 推荐了https://github.com/imalimin/HardwareVideoCodec
[设置模糊图片主要是通过修改某一个像素点的颜色来实现的，就是将某一个像素点上下左右四个像素点的颜色算一下平均值作为当前像素的最终颜色。所有的颜色都这样处理就会出现模糊的效果了。](https://www.jianshu.com/p/5d69d93f95de)

[OpenGL ES滤镜](https://github.com/CharonChui/AndroidNote/blob/master/VideoDevelopment/OpenGL/11.OpenGL%20ES%E6%BB%A4%E9%95%9C.md)
给图像添加滤镜本质就是图片处理，也就是对图片的像素进行计算，简单来说，图像处理的方法可以分为三类:
点算:当前像素的处理只和自身的像素值有关，和其他像素无关，比如灰度处理。
领域算:当前像素的处理需要和相邻的一定范围内的像素有关，比如高斯模糊。
全局算:在全局上对所有像素进行统一变换，比如几何变换。


- 滤镜都是通过SurfaceTexture（先生成一个GLES20.glGenTextures的纹理，然后用这个纹理new一个SurfaceTexture，
再用这个new出来的SurfaceTexture创建一个Surface，交给播放器）来做的，
让视频（或者相机）源输入数据到SurfaceTexture中，在onFrameAvailable中去requestRender，
在Render的onDrawFrame里面updateTexImage(更新SurfaceTexture对应的GL纹理对象)

创建OES 纹理的套路
```java
public static int createOESTextureObject() {
        int[] tex = new int[1];
        //生成一个纹理
        GLES20.glGenTextures(1, tex, 0);
        //将此纹理绑定到外部纹理上
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0]);
        //设置纹理过滤参数
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        return tex[0];
    }
```

- 思路就是camera预览不采用直接到View和数据回调的方式，而是采用setPreviewTexture方式，
将camera数据预览到surfaceTexture，surfaceTexture由于opengl 中texture关联，
数据准备好后surfaceTexture中onFrameAvailable回调通知Render开始处理，
通过updateImage将surfaceTexture数据更新到texture，然后进行处理，
最后显示到glSurfaceView上。

GLSurfaceView rendermode为RENDERMODE_WHEN_DITY，靠onFrameAvailable中调用requestRender驱动，
texture绑定需要GLES11Ext.GL_TEXTURE_EXTERNAL_OES类型，
Fragement中需要声明extension GL_OES_EGL_image_external : require。


- 播放器这边的api
    ExoPlayer.setVideoSurface(Surface surface);
    MediaPlayer.setSurface(Surface surface);
    Camera.setPreviewTexture(SurfaceTexture surfaceTexture); // Surface可以用SurfaceTexture new出来
    Camera.setPreviewDisplay(holder); //内部还是调用了setPreviewSurface
    Camera2.CaptureRequest.addTarget(Surface surface) //

- 凡是涉及到滤镜的，无一例外的都是
GLES20.glGenTextures -> new SurfaceTexture -> new Surface
渲染是渲染在GLSurfaceView上的（所以到现在没有TextureView什么事？）

- 为什么不关TextureView什么屁事？
EGL是OpenGL ES与设备的系统屏幕进行通信的桥梁，因为TextureView是没有任何OpenGL ES相关的环境的，而上篇文章讲的GLSurfaceView是封装好了OpenGL ES相关的环境，包括EGL环境。当OpenGL ES需要绘制图像时，会找到EGL的EGLSurface，通过此对象请求SurfaceFlinger返回系统屏幕的图形访问接口，这个接口也就是屏幕的帧缓冲区，这样OpenGL就可以将图像渲染到屏幕的帧缓冲区中。
EGL主要需要四个对象，一个EGLDisplay描述EGL显示屏，一个EGLConfig描述帧缓冲区配置参数，一个EGLContext描述EGL上下文环境，一个EGLSurface描述EGL绘图表面。整个EGL初始化见下述代码。
————————————————
原文链接：https://blog.csdn.net/lb377463323/article/details/77096652


.鹅厂彭于晏 - https://blog.nowcoder.net/n/8b0aca3f71d84e1cb66e5f758030b1e5
经过前面图片添加滤镜效果的实践，相机预览无非也就是将每一帧的bitmap放到OpenGL中去渲染。
所以很容易想到如下方案：
方案A 使用TextureView用于相机预览，TextureView表面再盖上一层GLSurfaceView用与OpenGL ES的渲染输出，从TextureView中获取每一帧的Bitmap交给OpenGL ES渲染到GLSurfaceView上。
很容易看出，方案A的性能并不好，甚至可以说很差，不免反思我们是不是可以去掉一层，因此有了方案B。
方案B 从OpenGL ES中生成一个纹理对象，以纹理ID new一个SurfaceTexture回调给外部的相机作为预览的输出，作为OpenGL ES的纹理输入渲染到GLSurfaceView上。

说到GLSurfaceView就一定要提一下SurfaceTexture。
和SurfaceView功能类似，区别是，SurfaceTexure可以不显示在界面中。使用OpenGl对图片流进行美化，添加水印，滤镜这些操作的时候我们都是通过SurfaceTexre去处理，处理完之后再通过GlSurfaceView显示。缺点，可能会导致个别帧的延迟。本身管理着BufferQueue,所以内存消耗会多一点。 SurfaceTexture从图像流（来自Camera预览，视频解码，GL绘制场景等）中获得帧数据，当调用updateTexImage()时，根据内容流中最近的图像更新SurfaceTexture对应的GL纹理对象，接下来，就可以像操作普通GL纹理一样操作它了。 SurfaceTexture 可以将 Surface 中最近的图像数据更新到 GL Texture 中。通过 GL Texture 我们就可以拿到视频帧，然后直接渲染到 GLSurfaceView 中。


他山之石
- [参考]
[iOS上的滤镜实现]https://www.jianshu.com/p/1b5c90df5829


1.GLSurfaceView介绍
GLSurfaceView从Android 1.5(API level 3)开始加入，继承自SurfaceView，实现了SurfaceHolder.Callback2接口，拥有SurfaceView的全部特性，也有view所有的功能和属性，特别是处理事件的能力，它主要是在SurfaceView的基础上它加入了EGL的管理，并自带了一个GLThread绘制线程（EGLContext创建GL环境所在线程即为GL线程），绘制的工作直接通过OpenGL在绘制线程进行，不会阻塞主线程，绘制的结果输出到SurfaceView所提供的Surface上，这使得GLSurfaceView也拥有了OpenGlES所提供的图形处理能力，通过它定义的Render接口，使更改具体的Render的行为非常灵活性，只需要将实现了渲染函数的Renderer的实现类设置给GLSurfaceView即可。

GLSurfaceView提供了下列特性：

1.提供并且管理一个独立的Surface。
2.提供并且管理一个EGL display，它能让opengl把内容渲染到上述的Surface上。
3.支持用户自定义渲染器(Render)，通过setRenderer设置一个自定义的Renderer。
4.让渲染器在独立的GLThread线程里运作，和UI线程分离。
5.支持按需渲染(on-demand)和连续渲染(continuous)两种模式。
6.GPU加速：GLSurfaceView的效率是SurfaceView的30倍以上，SurfaceView使用画布进行绘制，GLSurfaceView利用GPU加速提高了绘制效率
7.View的绘制onDraw(Canvas canvas)使用Skia渲染引擎渲染，而GLSurfaceView的渲染器Renderer的onDrawFrame(GL10 gl)使用opengl绘制引擎进行渲染。



https://www.codenong.com/jsb36b6e17e818/


