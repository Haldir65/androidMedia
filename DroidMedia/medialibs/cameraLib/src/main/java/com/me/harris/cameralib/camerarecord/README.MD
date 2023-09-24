把数据传入给 MediaCodec 之后，通过 dequeueOutputBuffer 方法取出编解码后的数据，除了指定超时时间外，还需要传入 MediaCodec.BufferInfo 对象，这个对象里面有着编码后数据的长度、偏移量以及标识符。

取出 MediaCodec.BufferInfo 内的数据之后，根据不同的标识符进行不同的操作：

BUFFER_FLAG_CODEC_CONFIG
表示当前数据是一些配置数据，在 H264 编码中就是 SPS 和 PPS 数据，也就是 00 00 00 01 67 和 00 00 00 01 68 开头的数据，这个数据是必须要有的，它里面有着视频的宽、高信息。
BUFFER_FLAG_KEY_FRAME
关键帧数据，对于 I 帧数据，也就是开头是 00 00 00 01 65 的数据，
BUFFER_FLAG_END_OF_STREAM
表示结束，MediaCodec 工作结束

对于返回的 flags ，不符合预定义的标识，则可以直接写入，那些数据可能代表的是 H264 中的 P 帧 或者 B 帧。


[注意]以上是输出h264文件，不是mp4文件，前者没有容器信息
