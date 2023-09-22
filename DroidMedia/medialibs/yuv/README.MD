https://github.com/theeasiestway/android-yuv-utils
https://mp.weixin.qq.com/s?__biz=MzA4MjU1MDk3Ng==&mid=2451526625&idx=1&sn=117a22b577d0638de92f18149e3602a3&chksm=886ffa4ebf187358264758fe249224a01087977335ffe48b9f7575df076385f2443e05d28a83&scene=21#wechat_redirect


YUV 几种格式

yuv 是一种color space （色彩空间）
yuv比rgb省数据，只有y就是黑白，所以yuv可以同时兼容老的黑白电视和彩色电视


1.yuv420p(yu12和yv12)格式,yuv420p又叫做plane平面模式，y,u,v分别在不同的平面
- 1.1 yu12格式，在android平台下也叫做i420格式，先是所有的y值（这里的所有指的是【这一帧】），紧接着是所有的u值，最后是所有的v值
  亮度(行 X 列) + U（行 X 列/4）+ V（行 X 列/4）
  yyyyyyyy  uuvv
- 1.2 yu21格式，在android平台下也叫做i420格式，先是所有的y值，紧接着是所有的V值，最后是所有的U值
  亮度(行 X 列) + V（行 X 列/4）+ U（行 X 列/4）
  yyyyyyyy  vvuu

2. YUV420SP(nv21和nv12)
- 2.1 nv21格式（android平台下使用相机默认图像格式），先是所有的y值，紧接着是 vu交替存储
  yyyyyy vuvuvu（以4X4图片为例，占用内存为 4 X 4 X 3 /2 =24个字节）

- 2.2 nv12格式，先是所有的y值，紧接着是 uv交替存储
  yyyyyy uvuvuv（以4X4图片为例，占用内存为 4 X 4 X 3 /2 =24个字节）

3. yuv采样
   yuv 4:4:4 采样 每一个Y对应一组UV分量，一个YUV占用 8+8+8 = 24bit 3个字节
   yuv 4:2:2 采样 每两个Y公用一组UV分量，一个YUV占用 8+4+4 = 16bit 2个字节
   yuv 4:2:0 采样 每四个Y对应一组UV分量，一个YUV占用 8+2+2 = 24bit 1.5个字节 （我们最常见的yuv420p和yuv420sp都是基于4:2:0采样的，
   所以如果图片的宽为width,高为height，在内存中占用的空间就是width * height *3/2 。其中 width * height 的空间存放Y分量，接着width * height /4 存放u分量，最后width * height /4 存放v分量，
   这个指的应该是yu12格式
   ）


4
4.1yuv422p
yuv44p基于yuv4:2:2 采样。两个Y公用一组uv分量，一个yuv占用 8+4+4 = 16bit 个字节

4.2 yuv444p
yuv44p基于yuv4:4:4 采样。每一个Y对应一组uv分量，一个yuv占用 8+8+8 = 24bit 个字节

5. yuv和rgb转换，这个很固定的数学公式

6. yuv420p在AVFrame中的存储
   struct AVFrame里面
   data[0] -Y 分量
   data[1] -U 分量
   data[2] -v 分量

linesize[]数组保存的是对应通道的数据宽度
linesize[0] - y分量的数据宽度 (linesize[0]并不一定等于图片宽度，内存对齐？)
linesize[1] - u分量的数据宽度
linesize[2] - v分量的数据宽度

所以AVFrame -> yuv420P buffer应该这么写

所以想要得到这个frame对应的yuv420 buffer
data[0] + data[1] + data[2] 就可以了



https://github.com/Denislyl/MyTechBlog2/issues/3
https://juejin.cn/post/6996678928018440223

todo:
- yuvJava allocateDirect老是oom?
https://mp.weixin.qq.com/s?__biz=MzA4MjU1MDk3Ng==&mid=2451526770&idx=1&sn=25cd744ea471ede99bbbece6f1150237&chksm=886ffdddbf1874cbdd15bd5285b65f2572314a16980c3a9050690261c006db76e6bffdb289e5&cur_album_id=1337187910394920960&scene=189#wechat_redirect
- [一直烂尾]https://github.com/glumes/AndroidOpenGLTutorial/blob/master/opengl_tutorial/src/main/java/com/glumes/openglbasicshape/transition/TransitionRender.java#L39
- compose 搞搞
- 整理一下项目经历
- 算法先hold住，需要脑子清醒


done
- 高版本不是有一个ImageDecoder吗？纯粹兴趣 测下来速度比BitmapFactory慢多了
- ImageReader和ImageWriter要会用，用ImageReader抽帧？ getLatestesImage感觉是不会有好处的
- 相机输出的nv21 yuv通过mediacodec存储为h264视频？（不过还是mediaCodec那一套，注意pps 和 一些config信息）
- yuv数据的来源有相机，mediaCodec，播放器。。。。 yuv420sp == semiplanar yyyyuvuv 好像是
