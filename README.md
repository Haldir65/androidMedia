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

- #### Camera 2 API use case(Not done yet)
- #### ExoPlayer 2 for Android 16 and above 
- #### MusicPlayer imitating (timber)[https://github.com/naman14/Timber]
- #### Best Practice in MediaPlayback - Ian Lake





### Reference
- [CameraView](https://github.com/google/cameraview)
- [camera2 basics](https://github.com/googlesamples/android-Camera2Basic)
- [camera2 video](https://github.com/googlesamples/android-Camera2Video)
- [camera2 raw](https://github.com/googlesamples/android-Camera2Raw)
- [ExoPlayer Guide](https://google.github.io/ExoPlayer/guide.html)





