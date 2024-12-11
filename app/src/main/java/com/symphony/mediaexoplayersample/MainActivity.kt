package com.symphony.mediaexoplayersample

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.symphony.mediaexoplayersample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
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
        // 将player添加到playerView中
        binding.playerView.player = player
        // 视频播放地址
        val videoUrl = "https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd"
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