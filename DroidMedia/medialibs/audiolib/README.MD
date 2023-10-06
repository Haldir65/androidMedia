## 参考lychee 中obselete的opensles 音频播放器，写一个音频播放器，封一个jni,走service loader交由 mediaCodec视频播放器


// opensl 是主动要数据的，开发者无法主动往里面塞数据


[prefab是官方从gradle4.0开始就把一些比较知名的c++库用ndk封装了一层，甚至早就上传到了maven上，根本不用自己去交叉编译]https://android-developers.googleblog.com/2020/02/native-dependencies-in-android-studio-40.html


[一些ndk自带的c++ so库在这里](https://www.cnblogs.com/sjjg/p/14617031.html)，具体在这个目录下面
$NDK_HOME/21.4.7075529\toolchains\llvm\prebuilt\windows-x86_64\sysroot\usr\lib\aarch64-linux-android



[mediaCodec解码音频比ffmpeg慢的多](https://github.com/google/oboe/tree/main/samples/RhythmGame)