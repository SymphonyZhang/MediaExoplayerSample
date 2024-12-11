package com.symphony.mediaexoplayersample

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.symphony.mediaexoplayersample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object{
        private const val TAG = "MainActivity"
    }
    private lateinit var binding: ActivityMainBinding

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
        // 实例化player
        val player = ExoPlayer.Builder(this).build()
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
                    }
                    Player.STATE_IDLE -> {
                        Log.i(TAG, "onPlaybackStateChanged: 空闲中~~")
                    }
                }

            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                Log.d(TAG, "onPlayWhenReadyChanged: playWhenReady->$playWhenReady  ::  reasonCod -> $reason")
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                Log.w(TAG, "onIsPlayingChanged: 当前播放状态 是否在播放: $isPlaying ", )
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e(TAG, "onPlayerError: 出错了 : ${error.errorCode}    ${PlaybackException.getErrorCodeName(error.errorCode)}" )
            }
        })
        // 将player添加到playerView中
        binding.playerView.player = player
        // 视频播放地址
        val videoUrl = "https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd"//正常视频流畅播放
        //val videoUrl = "https://raw.githubusercontent.com/SymphonyZhang/Images/refs/heads/master/videos/trailer.mp4"// github上的，测试listener
        //设置mediaItem给player
        val mediaItem = MediaItem.fromUri(videoUrl)
        player.setMediaItem(mediaItem)
        //player准备好
        player.prepare()

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
        binding.playerView.player?.release()
    }

}