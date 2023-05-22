[MeidaCodec抽帧抽出来的是yuv](https://blog.csdn.net/afei__/article/details/114886960) 视频解码解出来的好像都是YUV
[yuv转argb 除了 java 还有renderScript](https://juejin.cn/post/6844903749811634190)
[jni yuv to rgba ](https://github.com/xvolica/Camera2-Yuv2Rgb)


[opengl3.0 渲染yuv数据到GLSurfaceView上](https://blog.csdn.net/afei__/article/details/109031907)
速度排名:
libyuv(汇编了都) > ndk > renderScript > java


opengl应该也可以
https://github.com/dingjikerbo/Android-Camera  yuv to rgb


[yuv转rgba在opengl这边一般有两种方式](https://cloud.tencent.com/developer/article/1035613)
对图像数据的处理，为了达到实时性的要求，一般情况下还是需要用OpenGL在GPU上完成。所以在拿到相机YUV数据以后，我们需要把YUV数据转换成GPU可用的普通RGBA纹理才方便对数据进行再处理。从相机拿到的YUV数据格式是NV21或NV12，这种格式下，Y数据在一个平面（planar）上，UV数据在一个平面上。这种格式的YUV字节流转换成RGBA纹理一般有两种方式：
UV所在的一个平面拆成U和V数据分别在一个平面上，然后将Y、U、V三个平面作为三个GL_LUMINANCE的纹理作为输入，然后用YUV到RGB的转换矩阵在着色器程序中实现。
将YUV数据转换成类似RGBA的每个像素点包含YUVA格式的字节流，然后用YUV到RGB的转换矩阵在着色器程序中实现。
两种方式都需要先在CPU上对相机YUV格式字节流做一些预处理，然后上载到GPU上用着色器程序完成转换。这个过程涉及的预处理和着色器程序可以单独再拿一篇文章来写，篇幅有限，本文中就不详细介绍了。