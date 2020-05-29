package com.harris.androidMedia.exoPlayer.customize

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Pair
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.drm.*
import com.google.android.exoplayer2.offline.FilteringManifestParser
import com.google.android.exoplayer2.offline.StreamKey
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.manifest.DashManifest
import com.google.android.exoplayer2.source.dash.manifest.DashManifestParser
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.hls.playlist.DefaultHlsPlaylistParserFactory
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifestParser
import com.google.android.exoplayer2.trackselection.*
import com.google.android.exoplayer2.ui.DebugTextViewHelper
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.util.ErrorMessageProvider
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.util.Util
import com.harris.androidMedia.App
import com.harris.androidMedia.R
import com.harris.androidMedia.exoPlayer.SurfaceViewPlayerActivity.REQUEST_READ_EXTERNAL_STORAGE
import com.harris.androidMedia.hideSystemUI
import com.harris.androidMedia.showToast
import com.harris.androidMedia.util.ToastUtil
import com.harris.androidMedia.util.Utils
import kotlinx.android.synthetic.main.exoplayer_activity.*
import java.io.File
import java.util.*


class PlayerActivity : AppCompatActivity(), View.OnClickListener, CustomPlaybackControlView.VisibilityListener, PlaybackPreparer {


    private var player: SimpleExoPlayer? = null
    private var trackSelector: DefaultTrackSelector? = null
    private var mediaSource: MediaSource? = null
    private var dataSourceFactory: DataSource.Factory? = null
    private var mediaDrm: FrameworkMediaDrm? = null
    private var trackSelectorParameters: DefaultTrackSelector.Parameters? = null
    private var lastSeenTrackGroupArray: TrackGroupArray? = null


    companion object {


        fun go(ctx: Context, localFileAbsPath: String) {
            val uri = Uri.fromFile(File(localFileAbsPath))
            ctx.startActivity(Intent().setAction(ACTION_VIEW).setClass(ctx, PlayerActivity::class.java).setData(uri))
        }


        private val DRM_SCHEME_EXTRA = "drm_scheme"
        private val DRM_LICENSE_URL_EXTRA = "drm_license_url"
        private val DRM_KEY_REQUEST_PROPERTIES_EXTRA = "drm_key_request_properties"
        private val DRM_MULTI_SESSION_EXTRA = "drm_multi_session"
        private val PREFER_EXTENSION_DECODERS_EXTRA = "prefer_extension_decoders"

        // track selectors related
        private val ABR_ALGORITHM_EXTRA = "abr_algorithm"
        private val ABR_ALGORITHM_DEFAULT = "default"
        private val ABR_ALGORITHM_RANDOM = "random"

        // Saved instance state keys.
        private val KEY_TRACK_SELECTOR_PARAMETERS = "track_selector_parameters"
        private val KEY_WINDOW = "window"
        private val KEY_POSITION = "position"
        private val KEY_AUTO_PLAY = "auto_play"

        val ACTION_VIEW = "com.me.harris.androidMedia.exoplayer.action.VIEW"
        val ACTION_VIEW_LIST = "com.me.harris.androidMedia.exoplayer.action.VIEW_LIST"
    }


    //记录当前的状态
    private var startAutoPlay: Boolean = false
    private var startWindow: Int = 0
    private var startPosition: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataSourceFactory = buildDataSourceFactory()
        setContentView(R.layout.exoplayer_activity)
        playerView.setControllerVisibilityListener(this)
//        playerView.setErrorMessageProvider(PlayerErrorMessageProvider())
        playerView.requestFocus()

        if (savedInstanceState != null) {
            trackSelectorParameters = savedInstanceState.getParcelable(KEY_TRACK_SELECTOR_PARAMETERS)
            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY)
            startWindow = savedInstanceState.getInt(KEY_WINDOW)
            startPosition = savedInstanceState.getLong(KEY_POSITION)
        } else {
            trackSelectorParameters = DefaultTrackSelector.ParametersBuilder().build()
            clearStartPosition()
        }
        checkPermissions()
        hideSystemUI()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        releasePlayer()
        clearStartPosition()
        setIntent(intent)
    }


    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT > 23) {
            initializePlayer()
            if (playerView != null) {
                playerView.onResume()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT <= 23 || player == null) {
            initializePlayer()
            if (playerView != null) {
                playerView.onResume()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT <= 23) {
            playerView?.onPause()
            releasePlayer()
        }
    }


    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT > 23) {
            playerView?.onPause()
            releasePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    @TargetApi(23)
    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
                show_EXTERNAL_STORAGE_PermissionRequestRationale()
            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(READ_EXTERNAL_STORAGE), REQUEST_READ_EXTERNAL_STORAGE)
            }
        } else {
            //已经拥有permission

        }
    }

    private fun show_EXTERNAL_STORAGE_PermissionRequestRationale() {
        ToastUtil.showTextLong(this, "Please grant permission")
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty()) {
            // Empty results are triggered if a permission is requested while another request was already
            // pending and can be safely ignored in this case.
            return
        }
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializePlayer()
        } else {
            showToast("permission denied!")
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        updateTrackSelectorParameters()
        updateStartPosition()
        outState?.putParcelable(KEY_TRACK_SELECTOR_PARAMETERS, trackSelectorParameters)
        outState?.putBoolean(KEY_AUTO_PLAY, startAutoPlay)
        outState?.putInt(KEY_WINDOW, startWindow)
        outState?.putLong(KEY_POSITION, startPosition)
    }


    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        // See whether the player view wants to handle media or DPAD keys events.
        return playerView.dispatchKeyEvent(event) || super.dispatchKeyEvent(event)
    }


    override fun onClick(v: View?) {

    }

    // PlaybackControlView.PlaybackPreparer implementation

    override fun preparePlayback() {
        initializePlayer()
    }

    // PlaybackControlView.VisibilityListener implementation

    override fun onVisibilityChange(visibility: Int) {
        debugRootView.visibility = visibility
        if (visibility==View.GONE){
            hideSystemUI()
        }
    }

    private fun initializePlayer() {
        if (player == null) {
            val intent = intent
            val action = intent.action
            val uris: Array<Uri>
            val extensions: Array<String>?
            when (action) {
                ACTION_VIEW -> {
                    uris = arrayOf(intent.data).filterNotNull().toTypedArray()
                }
                ACTION_VIEW_LIST -> {
                    showToast("not implemented!")
                    finish()
                    return
                }
                else -> {
                    showToast("not implemented!")
                    finish()
                    return
                }
            }
            // drm Digital Rights Management

            ///... skip
            ///

            val trackSelectionFactory: TrackSelection.Factory
            val abrAlgorithm = intent.getStringExtra(ABR_ALGORITHM_EXTRA)
            if (abrAlgorithm == null || ABR_ALGORITHM_DEFAULT == abrAlgorithm) {
                trackSelectionFactory = AdaptiveTrackSelection.Factory()
            } else if (ABR_ALGORITHM_RANDOM == abrAlgorithm) {
                trackSelectionFactory = RandomTrackSelection.Factory()
            } else {
                showToast("Unrecognized ABR algorithm")
                finish()
                return
            }

            val preferExtensionDecoders = intent.getBooleanExtra(PREFER_EXTENSION_DECODERS_EXTRA, false)
            @DefaultRenderersFactory.ExtensionRendererMode val extensionRendererMode = if ((application as App).useExtensionRenderers())
                if (preferExtensionDecoders)
                    DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
                else
                    DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON
            else
                DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF
            val renderersFactory = DefaultRenderersFactory(this, extensionRendererMode)


            trackSelector = DefaultTrackSelector(trackSelectionFactory)
            trackSelector!!.setParameters(trackSelectorParameters)
            lastSeenTrackGroupArray = null


            // initialize the player
            player = ExoPlayerFactory.newSimpleInstance(
                    /* context= */ this, renderersFactory, trackSelector)
            player!!.addListener(PlayerEventListener())
            player!!.playWhenReady = startAutoPlay
            player!!.addAnalyticsListener(EventLogger(trackSelector))
            playerView.player = player
            playerView.setPlaybackPreparer(this)


            // prepare the mediaResources
            val mediaSources = arrayOfNulls<MediaSource>(uris.size)
            for (i in uris.indices) {
                mediaSources[i] = buildMediaSource(uris[i])
            }
            mediaSource = if (mediaSources.size == 1) mediaSources[0] else ConcatenatingMediaSource(*mediaSources)

            // ads unrelated
        }
        val haveStartPosition = startWindow != C.INDEX_UNSET
        if (haveStartPosition) {
            player?.seekTo(startWindow, startPosition)
        }
        player?.prepare(mediaSource, !haveStartPosition, false)
        updateButtonVisibilities()
    }


    private fun buildMediaSource(uri: Uri): MediaSource {
        return buildMediaSource(uri, null)
    }

    private fun buildMediaSource(uri: Uri, overrideExtension: String?): MediaSource {
//        @C.ContentType val type = Util.inferContentType(uri, overrideExtension)
//        when (type) {
//            C.TYPE_DASH -> return DashMediaSource.Factory(dataSourceFactory)
//                    .setManifestParser(
//                            FilteringManifestParser<DashManifest>(DashManifestParser(), getOfflineStreamKeys(uri)))
//                    .createMediaSource(uri)
//            C.TYPE_SS -> return SsMediaSource.Factory(dataSourceFactory)
//                    .setManifestParser(
//                            FilteringManifestParser<SsManifest>(SsManifestParser(), getOfflineStreamKeys(uri)))
//                    .createMediaSource(uri)
//            C.TYPE_HLS -> return HlsMediaSource.Factory(dataSourceFactory)
//                    .setPlaylistParserFactory(
//                            DefaultHlsPlaylistParserFactory(getOfflineStreamKeys(uri)))
//                    .createMediaSource(uri)
//            C.TYPE_OTHER -> return ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
//            else -> {
//                throw IllegalStateException("Unsupported type: $type")
//            }
//        }
        return ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
    }

    private fun getOfflineStreamKeys(uri: Uri): List<StreamKey> {
        return emptyList()
    }

    /** Returns a new DataSource factory.  */
    private fun buildDataSourceFactory(): DataSource.Factory {
        return (application as App).buildLocalFileDataSourceFactory()
    }

    @Throws(UnsupportedDrmException::class)
    private fun buildDrmSessionManagerV18(
            uuid: UUID, licenseUrl: String, keyRequestPropertiesArray: Array<String>?, multiSession: Boolean): DefaultDrmSessionManager<FrameworkMediaCrypto> {
        val licenseDataSourceFactory = (application as App).buildHttpDataSourceFactory()
        val drmCallback = HttpMediaDrmCallback(licenseUrl, licenseDataSourceFactory)
        if (keyRequestPropertiesArray != null) {
            var i = 0
            while (i < keyRequestPropertiesArray.size - 1) {
                drmCallback.setKeyRequestProperty(keyRequestPropertiesArray[i],
                        keyRequestPropertiesArray[i + 1])
                i += 2
            }
        }
        releaseMediaDrm()
        mediaDrm = FrameworkMediaDrm.newInstance(uuid)
        return DefaultDrmSessionManager(uuid, mediaDrm, drmCallback, null, multiSession)
    }


    private fun releasePlayer() {
        player?.run {
            updateTrackSelectorParameters()
            updateStartPosition()
            player!!.release()
            player = null
            mediaSource = null
            trackSelector = null
        }
        releaseMediaDrm()
    }

    private fun releaseMediaDrm() {
        if (mediaDrm != null) {
            mediaDrm!!.release()
            mediaDrm = null
        }
    }


    private fun updateTrackSelectorParameters() {
        if (trackSelector != null) {
            trackSelectorParameters = trackSelector!!.getParameters()
        }
    }

    private fun updateStartPosition() {
        if (player != null) {
            startAutoPlay = player!!.getPlayWhenReady()
            startWindow = player!!.getCurrentWindowIndex()
            startPosition = Math.max(0, player!!.getContentPosition())
        }
    }

    private fun clearStartPosition() {
        startAutoPlay = true
        startWindow = C.INDEX_UNSET
        startPosition = C.TIME_UNSET
    }

    private fun isBehindLiveWindow(e: ExoPlaybackException): Boolean {
        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
            return false
        }
        var cause: Throwable? = e.sourceException
        while (cause != null) {
            if (cause is BehindLiveWindowException) {
                return true
            }
            cause = cause.cause
        }
        return false
    }

    private fun updateButtonVisibilities() {
        debugRootView.removeAllViews()
        if (player == null) {
            return
        }

        val mappedTrackInfo = trackSelector?.getCurrentMappedTrackInfo() ?: return

        loop@ for (i in 0 until mappedTrackInfo.rendererCount) {
            val trackGroups = mappedTrackInfo.getTrackGroups(i)
            if (trackGroups.length != 0) {
                val button = Button(this)
                val label: Int
                when (player?.getRendererType(i)) {
                    C.TRACK_TYPE_AUDIO -> label = R.string.exo_track_selection_title_audio
                    C.TRACK_TYPE_VIDEO -> label = R.string.exo_track_selection_title_video
                    C.TRACK_TYPE_TEXT -> label = R.string.exo_track_selection_title_text
                    else -> continue@loop
                }
                button.setText(label)
                button.tag = i
                button.setOnClickListener(this)
                debugRootView.addView(button)
            }
        }
    }

    private fun showControls() {
        debugRootView.visibility = View.VISIBLE
    }


    private inner class PlayerEventListener : Player.EventListener {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            if (playbackState == Player.STATE_ENDED) {
                showControls()
            }
            updateButtonVisibilities()
        }

        override fun onPositionDiscontinuity(@Player.DiscontinuityReason reason: Int) {
            if (player?.playbackError != null) {
                // The user has performed a seek whilst in the error state. Update the resume position so
                // that if the user then retries, playback resumes from the position to which they seeked.
                updateStartPosition()
            }
        }

        override fun onPlayerError(e: ExoPlaybackException?) {
            if (isBehindLiveWindow(e!!)) {
                clearStartPosition()
                initializePlayer()
            } else {
                updateStartPosition()
                updateButtonVisibilities()
                showControls()
            }
        }

        override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
            updateButtonVisibilities()
            if (trackGroups !== lastSeenTrackGroupArray) {
                val mappedTrackInfo = trackSelector?.currentMappedTrackInfo
                if (mappedTrackInfo != null) {
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO) == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                        showToast("error_unsupported_video")
                    }
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_AUDIO) == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                        showToast("error_unsupported_audio")
                    }
                }
                lastSeenTrackGroupArray = trackGroups
            }
        }
    }

    inner class PlayerErrorMessageProvider : ErrorMessageProvider<ExoPlaybackException> {
        override fun getErrorMessage(throwable: ExoPlaybackException?): Pair<Int, String> {
            return Pair.create<Int, String>(0, "")
        }
    }
}