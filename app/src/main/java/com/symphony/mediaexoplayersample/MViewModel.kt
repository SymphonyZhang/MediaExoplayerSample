package com.symphony.mediaexoplayersample

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * @Author          : Symphony
 * @Email           : 95200261@qq.com
 * @Date            : on 2024-12-13 00:36.
 * @Description     : 描述 因为横竖屏切换会Activity会重新创建，所以需要一个ViewModel来保存状态 包括播放进度和是否是全屏
 */
class MViewModel:ViewModel() {
    private val _fullScreen = MutableStateFlow(false)
    val fullScreen:StateFlow<Boolean> = _fullScreen.asStateFlow()

    fun setFullScreen(fullScreen:Boolean){
        _fullScreen.value = fullScreen
    }

    private val _videoCurrentPosition = MutableStateFlow(0L)
    val videoCurrentPosition:StateFlow<Long> = _videoCurrentPosition.asStateFlow()

    fun setVideoCurrentPosition(position:Long){
        _videoCurrentPosition.value = position
    }
}