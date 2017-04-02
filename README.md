## AndroidMedia
 A series of small Demo for media related api on android platform , including ExoPlayer, camera2 , mediaPlayerBack, etc.... All of which rely on android's hardware layer support. Just a small piece of draft.

Note! quote from [Google's guide](https://google.github.io/ExoPlayer/faqs.html) :

> ### Does ExoPlayer support emulators?
>
> If you’re seeing ExoPlayer fail when using an emulator,
> this is usually because the emulator does not properly implement components of Android’s media stack.
> This is an issue with the emulator, not with ExoPlayer. Android’s official emulator (“Virtual Devices” in Android Studio)
> supports ExoPlayer provided the system image has an API level of at least 23. System images with earlier API levels do not support ExoPlayer.
> The level of support provided by third party emulators varies. If you find a third party emulator on which ExoPlayer fails,
> you should report this to the developer of the emulator rather than to the ExoPlayer team. Where possible,
> we recommend testing media applications on physical devices rather than emulators.

Also

> SurfaceView are more performant than textureView ,the latter is shall only be used as media output when scrolling or animation are required.

- Camera 2 API use case(Not done yet)

> [Camera2Raw](https://github.com/googlesamples/android-Camera2Raw) ShowCase for heavyLifting coding for concurrency code</br>
> [camera2 basics](https://github.com/googlesamples/android-Camera2Basic) </br>
> [camera2 Video](https://github.com/googlesamples/android-Camera2Video)</br>
> [Android相机开发(六): 高效实时处理预览帧数据](https://www.polarxiong.com/archives/Android%E7%9B%B8%E6%9C%BA%E5%BC%80%E5%8F%91-%E5%85%AD-%E9%AB%98%E6%95%88%E5%AE%9E%E6%97%B6%E5%A4%84%E7%90%86%E9%A2%84%E8%A7%88%E5%B8%A7%E6%95%B0%E6%8D%AE.html)</br>
> [Using concurrency to improve speed and performance in Android](https://medium.com/@ali.muzaffar/using-concurrency-and-speed-and-performance-on-android-d00ab4c5c8e3)


-  ExoPlayer 2 for Android 16 and above .By the way, ExoPlayer is the default videoPlayer for the youtube Player
-  MusicPlayer imitating [timber](https://github.com/naman14/Timber)
-  [Best Practice in MediaPlayback](https://www.youtube.com/watch?v=iIKxyDRjecU) - Ian Lake on Google I/O 2016
-  [MediaBrowserCompat](https://medium.com/google-developers/mediabrowserservicecompat-and-the-modern-media-playback-app-7959a5196d90#.iamgrv1w6)
-  [siwtching-exoplayer-better-video](https://realm.io/news/360andev-effie-barak-switching-exoplayer-better-video-android/)

![Music](https://github.com/Haldir65/androidMedia/blob/master/art/snapshot20170304225245.jpg)
![Movie](https://github.com/Haldir65/androidMedia/blob/master/art/snapshot20170304225305.jpg)

## todo RemoteControlClient onLockScreen , or MediaSession on L



### Reference
- [CameraView](https://github.com/google/cameraview)
- [camera2 basics](https://github.com/googlesamples/android-Camera2Basic)
- [camera2 video](https://github.com/googlesamples/android-Camera2Video)
- [camera2 raw](https://github.com/googlesamples/android-Camera2Raw)
- [ExoPlayer Guide](https://google.github.io/ExoPlayer/guide.html)
- [Streaming media with ExoPlayer Google IO 2016](https://www.youtube.com/watch?v=vOzOZ7hRr00)





