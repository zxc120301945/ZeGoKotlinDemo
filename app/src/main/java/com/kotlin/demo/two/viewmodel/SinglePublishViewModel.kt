package com.kotlin.demo.two.viewmodel

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.kotlin.demo.two.constants.Constants
import com.kotlin.demo.two.util.PreferenceUtil
import com.kotlin.demo.two.viewmodel.base.BaseViewModel
import com.kotlin.demo.two.widget.ViewLive
import com.zego.zegoliveroom.callback.IZegoLivePublisherCallback
import com.zego.zegoliveroom.callback.IZegoRoomCallback
import com.zego.zegoliveroom.constants.ZegoConstants
import com.zego.zegoliveroom.constants.ZegoVideoViewMode
import com.zego.zegoliveroom.entity.AuxData
import com.zego.zegoliveroom.entity.ZegoStreamInfo
import com.zego.zegoliveroom.entity.ZegoStreamQuality
import java.util.*

/**
 * Created by my on 2018/06/14 0014.
 */
class SinglePublishViewModel : BaseViewModel() {

    fun doBusiness() {

        mZegoLiveRoom.loginRoom(mRoomID, mPublishTitle, ZegoConstants.RoomRole.Anchor) { errorCode, zegoStreamInfos ->
            if (errorCode == 0) {
                handleAnchorLoginRoomSuccess(zegoStreamInfos)
            } else {
                loginRoomFail(errorCode)
            }
        }

        mZegoLiveRoom.setZegoLivePublisherCallback(object : IZegoLivePublisherCallback {
            override fun onPublishStateUpdate(stateCode: Int, streamID: String, streamInfo: HashMap<String, Any>) {
                //推流状态更新
                if (stateCode == 0) {
//                    handlePublishSucc(streamID, streamInfo)
                } else {
                    handlePublishStop(stateCode, streamID)
                }
            }

            override fun onJoinLiveRequest(seq: Int, fromUserID: String, fromUserName: String, roomID: String) {}

            override fun onPublishQualityUpdate(streamID: String, streamQuality: ZegoStreamQuality) {
                // 推流质量回调
//                handlePublishQualityUpdate(streamID, streamQuality.quality, streamQuality.videoFPS, streamQuality.videoBitrate)
            }

            override fun onAuxCallback(dataLen: Int): AuxData {
//                return handleAuxCallback(dataLen)
                return AuxData()
            }

            override fun onCaptureVideoSizeChangedTo(width: Int, height: Int) {

            }

            override fun onMixStreamConfigUpdate(errorCode: Int, streamID: String, streamInfo: HashMap<String, Any>) {

            }
        })

        mZegoLiveRoom.setZegoRoomCallback(object : IZegoRoomCallback {
            override fun onKickOut(reason: Int, roomID: String) {

            }

            override fun onDisconnect(errorCode: Int, roomID: String) {
                handleDisconnect(errorCode, roomID)
            }

            override fun onReconnect(i: Int, s: String) {

            }

            override fun onTempBroken(i: Int, s: String) {

            }

            override fun onStreamUpdated(type: Int, listStream: Array<ZegoStreamInfo>?, roomID: String) {
                if (listStream != null && listStream.size > 0) {
                    when (type) {
                        ZegoConstants.StreamUpdateType.Deleted -> handleStreamDeleted(listStream, roomID)
                    }
                }
            }

            override fun onStreamExtraInfoUpdated(zegoStreamInfos: Array<ZegoStreamInfo>, s: String) {

            }

            override fun onRecvCustomCommand(userID: String, userName: String, content: String, roomID: String) {

            }
        })
    }


    private fun publishStream() {

        if (TextUtils.isEmpty(mRoomID)) {
            return
        }

        // 设置流信息
        mViewLive!!.streamID = mRoomID
        mViewLive!!.isPublishView = true

        // 单主播模式, 直推CDN
        var publishFlag = ZegoConstants.PublishFlag.SingleAnchor

        // 输出发布状态
        Log.e("publishStream", ": start publishing($mRoomID)")

        // 设置水印
//        ZegoLiveRoom.setWaterMarkImagePath("asset:watermark.png")
//        val rect = Rect()
//        rect.left = 50
//        rect.top = 20
//        rect.right = 200
//        rect.bottom = 170
//        ZegoLiveRoom.setPreviewWaterMarkRect(rect)
//        ZegoLiveRoom.setPublishWaterMarkRect(rect)

        // 开启流量自动控制
        val properties = ZegoConstants.ZegoTrafficControlProperty.ZEGOAPI_TRAFFIC_FPS or ZegoConstants.ZegoTrafficControlProperty.ZEGOAPI_TRAFFIC_RESOLUTION
        mZegoLiveRoom.enableTrafficControl(properties, true)

        //开启双声道
//        mZegoLiveRoom.setAudioChannelCount(2)

        // 开始播放
        mZegoLiveRoom.setPreviewView(mViewLive!!.textureView)
        mZegoLiveRoom.startPreview()
        mZegoLiveRoom.enableMic(true)
        mZegoLiveRoom.enableCamera(true)

        mZegoLiveRoom.startPublishing(mRoomID, mPublishTitle, publishFlag)
        mZegoLiveRoom.setPreviewViewMode(ZegoVideoViewMode.ScaleAspectFill)
        //开启抑制噪音开关
        mZegoLiveRoom.enableNoiseSuppress(true)
        //回声消除开关
        mZegoLiveRoom.enableAEC(true)
        //	setAuxVolume  设置混音音量

    }

    /**
     * 主播登录房间成功.
     */
    private fun handleAnchorLoginRoomSuccess(zegoStreamInfos: Array<ZegoStreamInfo>) {
        mPublishTitle = PreferenceUtil.getInstance().getUserName() + " is coming"

        // 开始推流
        startPublish()

        // 打印log
        Log.e("publishLoginRoomSuccess", ": onLoginRoom success(" + mRoomID + "), streamCounts:" + zegoStreamInfos.size)
    }

    /**
     * 开始发布.
     */
    private fun startPublish() {

        // 6.0及以上的系统需要在运行时申请CAMERA RECORD_AUDIO权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(mActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity, arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO), 101)
            } else {
                publishStream()
            }
        } else {
            publishStream()
        }
    }

    /**
     * 停止推流.
     */
    fun handlePublishStop(stateCode: Int, streamID: String) {
        Log.e("publishStop", ": onPublishStop(" + streamID + ") --stateCode:" + stateCode)
    }

}