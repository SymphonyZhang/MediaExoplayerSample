package com.symphony.mediaexoplayersample

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.symphony.mediaexoplayersample.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    companion object{
        private const val TAG = "MainActivity"
    }
    private lateinit var binding: ActivityMainBinding
    // 当前播放进度
    private var currentPosition = 0L
    // 是否是全屏
    private var fullScreen = false

    private lateinit var viewModel: MViewModel

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel = ViewModelProvider(this)[MViewModel::class.java]
        // 实例化player
        val player = ExoPlayer.Builder(this).build()
        // 将player添加到playerView中
        binding.playerView.player = player

        // 视频播放地址
        //val videoUrl = "https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd"//正常视频流畅播放
        //val videoUrl1 = "https://storage.googleapis.com/exoplayer-test-media-1/mp4/dizzy-with-tx3g.mp4"
        val videoUrl2 = "https://storage.googleapis.com/exoplayer-test-media-1/mp4/frame-counter-one-hour.mp4"
        //val videoUrl = "https://raw.githubusercontent.com/SymphonyZhang/Images/refs/heads/master/videos/trailer.mp4"// github上的，测试listener
        //设置mediaItem给player
        //val mediaItem = MediaItem.fromUri(videoUrl)
        //val mediaItem1 = MediaItem.fromUri(videoUrl1)
        val mediaItem2 = MediaItem.fromUri(videoUrl2)
        player.setMediaItem(mediaItem2)
        //player准备好
        player.prepare()

        lifecycleScope.launch {
            viewModel.fullScreen.collect{
                fullScreen = it
                binding.playerView.setFullscreenButtonState(fullScreen)
            }
        }

        lifecycleScope.launch {
            viewModel.videoCurrentPosition.collect {
                currentPosition = it
                if(currentPosition != 0L && !player.isPlaying){ //如果播放进度不为0，就跳转到播放进度，并进行播放
                    Log.d(TAG, "onPlaybackStateChanged: ==> currentPosition: $currentPosition")
                    player.seekTo(currentPosition)
                    player.prepare()
                    player.play()
                }
            }


        }

        player.addListener(object:Player.Listener{
            override fun onPlaybackStateChanged(playbackState: Int) {
                when(playbackState){
                    Player.STATE_BUFFERING -> {
                        Log.i(TAG, "onPlaybackStateChanged: 加载中...")
                    }
                    Player.STATE_READY -> {
                        Log.i(TAG, "onPlaybackStateChanged: 准备!!!")
                    }
                    Player.STATE_ENDED -> {
                        Log.i(TAG, "onPlaybackStateChanged: 完毕!!!")
                        //播放完毕需要清除播放进度
                        viewModel.setVideoCurrentPosition(0L)
                    }
                    Player.STATE_IDLE -> {
                        //初始状态，即播放器 以及停止播放的时间玩家只会拥有有限的资源 状态
                        Log.i(TAG, "onPlaybackStateChanged: 初始状态中~~")
                    }
                }

            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                Log.d(TAG, "onPlayWhenReadyChanged: playWhenReady->$playWhenReady  ::  reasonCod -> $reason")
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                Log.w(TAG, "onIsPlayingChanged: 当前播放状态 是否在播放: $isPlaying ")
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e(TAG, "onPlayerError: 出错了 : ${error.errorCode}    ${PlaybackException.getErrorCodeName(error.errorCode)}" )
            }


        })

        // 监听全屏按钮点击事件
        // 只要添加了这个监听，全屏按钮就会出来，并不需要自己去重新弄布局控件
        // 当然也可以自己自定义布局，复用系统的exo_player_control_view.xml里的 控件id 把自定义的控件 在PlayerView控件中通过 app:controller_layout_id="" 来添加到playerView
        binding.playerView.setFullscreenButtonClickListener {
            viewModel.setVideoCurrentPosition(player.currentPosition)
            viewModel.setFullScreen(it)
            this.requestedOrientation = if(it) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun onStart() {
        super.onStart()
        binding.playerView.onResume()
    }


    override fun onStop() {
        super.onStop()
        binding.playerView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.launch {
            viewModel.setVideoCurrentPosition(0L)
        }
        binding.playerView.player?.release()
    }

}