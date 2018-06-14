package com.kotlin.demo.two.widget

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.widget.RelativeLayout
import com.kotlin.demo.two.R
import com.zego.zegoliveroom.ZegoLiveRoom
import com.zego.zegoliveroom.constants.ZegoVideoViewMode
import java.util.*

/**
 * Copyright © 2017 Zego. All rights reserved.
 * des: 直播view.
 */
class ViewLive : RelativeLayout {

    /**
     * 用于渲染视频.
     */
    var textureView: TextureView? = null
        private set


    private var mResources: Resources? = null

    private var mRootView: View? = null

    private var mZegoLiveRoom: ZegoLiveRoom? = null

    private var mActivityHost: Activity? = null

    /**
     * 推拉流质量.
     */
    var liveQuality = 0
        private set

    /**
     * 视频显示模式.
     */
    var zegoVideoViewMode = ZegoVideoViewMode.ScaleAspectFill
        private set

    /**
     * 分享地址.
     */
    private var mListShareUrls: MutableList<String> = ArrayList()

    /**
     * "切换全屏" 标记.
     */
    var isNeedToSwitchFullScreen = false
        private set

    var streamID: String? = null

    var isPublishView = false

    var isPlayView = false

    private var mShareToQQCallback: IShareToQQCallback? = null

    /**
     * 返回view是否为"空闲"状态.
     */
    val isFree: Boolean
        get() = TextUtils.isEmpty(streamID)

    var listShareUrls: List<String>
        get() = mListShareUrls
        set(listShareUrls) {
            mListShareUrls.clear()
            mListShareUrls.addAll(listShareUrls)
        }


    constructor(context: Context) : super(context) {}

    @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {

        val a = context.obtainStyledAttributes(attrs, R.styleable.ViewLive, defStyleAttr, 0)
        val isBigView = a.getBoolean(R.styleable.ViewLive_isBigView, false)
        a.recycle()

        initViews(context, isBigView)
    }

    fun setZegoLiveRoom(zegoLiveRoom: ZegoLiveRoom) {
        mZegoLiveRoom = zegoLiveRoom
    }

    fun setActivityHost(activity: Activity) {
        mActivityHost = activity
    }

    fun setShareToQQCallback(shareToQQCallback: IShareToQQCallback) {
        mShareToQQCallback = shareToQQCallback
    }

    fun destroy() {
        mActivityHost = null
    }

    private fun initViews(context: Context, isBigView: Boolean) {

        mResources = context.resources



        if (isBigView) {
            mRootView = LayoutInflater.from(context).inflate(R.layout.view_live_big, this)

        } else {
            mRootView = LayoutInflater.from(context).inflate(R.layout.view_live, this)
        }

        textureView = mRootView!!.findViewById<TextureView>(R.id.textureView)
    }

    /**
     * 释放view.
     */
    fun setFree() {
        liveQuality = 0
        visibility = View.INVISIBLE

        zegoVideoViewMode = ZegoVideoViewMode.ScaleAspectFill
        isNeedToSwitchFullScreen = false


        mListShareUrls = ArrayList()

        streamID = null
        isPublishView = false
        isPlayView = false
    }


    /**
     * 交换view, 通常是跟大的View交换.
     */
    fun toExchangeView(vlBigView: ViewLive) {

    }

    /**
     * 设置mode.
     */
    fun setZegoVideoViewMode(needToSwitchFullScreen: Boolean, mode: Int) {
        isNeedToSwitchFullScreen = needToSwitchFullScreen
        zegoVideoViewMode = mode
    }

    interface IShareToQQCallback {
        val roomID: String
    }
}