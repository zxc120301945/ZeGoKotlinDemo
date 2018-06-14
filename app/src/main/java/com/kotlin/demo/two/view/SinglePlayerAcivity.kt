package com.kotlin.demo.two.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.kotlin.demo.two.R
import com.kotlin.demo.two.bean.RoomInfo
import com.kotlin.demo.two.constants.IntentExtra
import com.kotlin.demo.two.widget.ViewLive
import com.kotlin.demo.two.zego.ZegoApiManager
import com.zego.zegoliveroom.ZegoLiveRoom
import com.zego.zegoliveroom.callback.IZegoLivePlayerCallback
import com.zego.zegoliveroom.callback.IZegoRoomCallback
import com.zego.zegoliveroom.callback.im.IZegoIMCallback
import com.zego.zegoliveroom.constants.ZegoConstants
import com.zego.zegoliveroom.constants.ZegoIM
import com.zego.zegoliveroom.constants.ZegoVideoViewMode
import com.zego.zegoliveroom.entity.*
import kotlinx.android.synthetic.main.activity_single_player.*
import org.jetbrains.anko.sdk23.listeners.onClick
import java.util.*

/**
 * Created by my on 2018/06/13 0013.
 */
open class SinglePlayerAcivity : AppCompatActivity() {

    /**
     * 启动入口.
     *
     * @param activity 源activity
     * @param roomInfo 房间信息
     */
    fun actionStart(activity: Activity, roomInfo: RoomInfo) {
        val intent = Intent(activity, SinglePlayerAcivity::class.java)
        intent.putExtra(IntentExtra.ROOM_ID, roomInfo.room_id)

        val streamList = getStremListFromRoomInfo(roomInfo)
        intent.putStringArrayListExtra(IntentExtra.LIST_STREAM, streamList)

        activity.startActivity(intent)
    }

    protected fun getStremListFromRoomInfo(roomInfo: RoomInfo): java.util.ArrayList<String>? {
        var streamList: java.util.ArrayList<String>? = null
        if (roomInfo.stream_info != null) {
            var streamInfo = roomInfo.stream_info
            var size = streamInfo?.size
            streamList = ArrayList(if (size is Int) size else 0)
            streamInfo?.forEach {
                var streamId = it.stream_id
                if (streamId is String) {
                    streamList!!.add(streamId)
                }
            }
        }
        return streamList
    }

    private var mRoomID: String = ""
    private var mOldSavedStreamList: ArrayList<String>? = null
    private val mZegoLiveRoom: ZegoLiveRoom = ZegoApiManager.getInstance().getZegoLiveRoom()
    private var mListViewLive = LinkedList<ViewLive>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 禁止手机休眠
        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_single_player)
        initData(savedInstanceState)
        initViews()
    }

    private fun initData(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val intent = intent
            mRoomID = intent.getStringExtra(IntentExtra.ROOM_ID)
            mOldSavedStreamList = intent.getStringArrayListExtra(IntentExtra.LIST_STREAM)
        }
    }

    private fun initViews() {
        if (vl_big_view != null) {
            vl_big_view.setActivityHost(this)
            vl_big_view.setZegoLiveRoom(mZegoLiveRoom)
            mListViewLive.add(vl_big_view)
        }
        single_close.onClick {
            finish()
        }
        doBusiness()
    }

    private fun doBusiness() {
        //登陆房间
        mZegoLiveRoom.loginRoom(mRoomID, ZegoConstants.RoomRole.Audience) { errorCode, zegoStreamInfos ->
            if (errorCode == 0) {
                handleAudienceLoginRoomSuccess(zegoStreamInfos)
            } else {
                handleAudienceLoginRoomFail(errorCode)
            }
        }
        //监听直播流状态
        mZegoLiveRoom.setZegoLivePlayerCallback(object : IZegoLivePlayerCallback {
            override fun onPlayStateUpdate(stateCode: Int, streamID: String) {
                // 拉流状态更新
                if (stateCode == 0) {
//                    handlePlaySucc(streamID)
                } else {
                    handlePlayStop(stateCode, streamID)
                }
            }

            override fun onPlayQualityUpdate(streamID: String, streamQuality: ZegoStreamQuality) {
                // 拉流质量回调
//                handlePlayQualityUpdate(streamID, streamQuality.quality, streamQuality.videoFPS, streamQuality.videoBitrate)
            }

            override fun onInviteJoinLiveRequest(seq: Int, fromUserID: String, fromUserName: String, roomID: String) {

            }

            override fun onRecvEndJoinLiveCommand(fromUserId: String, fromUserName: String, roomId: String) {

            }

            override fun onVideoSizeChangedTo(streamID: String, width: Int, height: Int) {
//                handleVideoSizeChanged(streamID, width, height)
            }
        })

        //监听直播房间状态
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
//                        ZegoConstants.StreamUpdateType.Added -> handleStreamAdded(listStream, roomID)
                        ZegoConstants.StreamUpdateType.Deleted -> handleStreamDeleted(listStream, roomID)
                    }
                }
            }

            override fun onStreamExtraInfoUpdated(zegoStreamInfos: Array<ZegoStreamInfo>, s: String) {

            }

            override fun onRecvCustomCommand(userID: String, userName: String, content: String, roomID: String) {

            }
        })

        //设置 IM 回调
        mZegoLiveRoom.setZegoIMCallback(object : IZegoIMCallback {

            override fun onUserUpdate(listUser: Array<ZegoUserState>, updateType: Int) {
                handleUserUpdate(listUser, updateType)
            }

            override fun onRecvRoomMessage(roomID: String, listMsg: Array<ZegoRoomMessage>) {
                //房间聊天
//                handleRecvRoomMsg(roomID, listMsg)
            }

            override fun onRecvConversationMessage(roomID: String, conversationID: String, message: ZegoConversationMessage) {
                //会话消息.
//                handleRecvConversationMsg(roomID, conversationID, message)
            }

            override fun onUpdateOnlineCount(s: String, i: Int) {
//                recordLog("Online Count: " + i)
            }

            override fun onRecvBigRoomMessage(s: String, zegoBigRoomMessages: Array<ZegoBigRoomMessage>) {

            }
        })

        // 播放从房间列表传过来的流列表
        if (mOldSavedStreamList != null) {
            val size = mOldSavedStreamList?.size
            if (size is Int) {
                if (size > 0) {
//                    startPlay(mOldSavedStreamList?.get(0).toString())
//                    mOldSavedStreamList?.forEach {
//                        Log.e("SingleAnchorPlayA", "Quick play: " + it)
//                        startPlay(it)
//                    }
                }
            }
        }
    }

    /**
     * 观众登录房间成功.
     */
    protected fun handleAudienceLoginRoomSuccess(zegoStreamInfos: Array<ZegoStreamInfo>?) {
        // 播放房间的流
        if (zegoStreamInfos != null && zegoStreamInfos.size > 0) {
            for (i in zegoStreamInfos.indices) {
                val streamId = zegoStreamInfos[i].streamID
                if (mOldSavedStreamList != null) {
                    val contains = mOldSavedStreamList?.contains(streamId)
                    if (contains is Boolean) {
                        when (contains) {
                            true -> mOldSavedStreamList?.remove(streamId)
                        }
                    }
                } else {
                    startPlay(streamId)
                }
            }

            if (mOldSavedStreamList != null) {
                val size = mOldSavedStreamList?.size
                if (size is Int) {
                    if (size > 0) {
                        mOldSavedStreamList?.forEach {
                            stopPlay(it)
                        }
                    }
                }
                mOldSavedStreamList?.clear()
            }
        }
        // 打印log
        Log.e("SingleLoginRoomSuccess", ": onLoginRoom success(" + mRoomID + "), streamCounts:" + zegoStreamInfos?.size)
    }

    /**
     * 观众登录房间失败.
     */
    protected fun handleAudienceLoginRoomFail(errorCode: Int) {
        // 打印log
        Log.e("SingleLoginRoomFail", ": onLoginRoom fail(" + mRoomID + ") errorCode:" + errorCode)
    }

    /**
     * 开始播放流.
     */
    protected fun startPlay(streamID: String) {

        if (TextUtils.isEmpty(streamID)) {
            return
        }

        if (isStreamExisted(streamID)) {
            Toast.makeText(this, "流已存在", Toast.LENGTH_SHORT).show()
            return
        }

        // 设置流信息
        vl_big_view!!.streamID = streamID
        vl_big_view!!.isPlayView = true

        // 输出播放状态
        Log.e("playerStatus", ": start play stream(" + streamID + ")")

        // 初始化拉流参数, 外部渲染模式使用
//        initPlayConfigs(freeViewLive, streamID)

        // 播放
        mZegoLiveRoom.startPlayingStream(streamID, vl_big_view!!.textureView)
        //ScaleAspectFill
        //等比缩放填充整View，可能有部分被裁减。 SDK 默认值。
        //ScaleAspectFit
        //等比缩放，可能有黑边。
        //ScaleToFill
        //填充整个View，视频可能会变形。
        mZegoLiveRoom.setViewMode(ZegoVideoViewMode.ScaleAspectFill, streamID)
    }

    fun isStreamExisted(streamID: String): Boolean {
        if (TextUtils.isEmpty(streamID)) {
            return true
        }

        var isExisted = false

        for (viewLive in mListViewLive) {
            if (streamID == viewLive.streamID) {
                isExisted = true
                break
            }
        }

        return isExisted
    }

    /**
     * 停止拉流.
     */
    protected fun handlePlayStop(stateCode: Int, streamID: String) {
        // 释放View
        releaseLiveView(streamID)
    }

    /**
     * 释放View用于再次播放.
     *
     * @param streamID
     */
    protected fun releaseLiveView(streamID: String) {
        if (TextUtils.isEmpty(streamID)) {
            return
        }

        var i = 0
        val size = mListViewLive.size
        while (i < size) {
            var currentViewLive = mListViewLive[i]
            if (streamID == currentViewLive.streamID) {
                var j = i
                while (j < size - 1) {
                    val nextViewLive = mListViewLive[j + 1]
                    if (nextViewLive.isFree) {
                        break
                    }

                    if (nextViewLive.isPublishView) {
                        mZegoLiveRoom.setPreviewView(currentViewLive.textureView)
                    } else {
                        mZegoLiveRoom.updatePlayView(nextViewLive.streamID, currentViewLive.textureView)
                    }

                    currentViewLive.toExchangeView(nextViewLive)
                    currentViewLive = nextViewLive
                    j++
                }
                // 标记最后一个View可用
                mListViewLive[j].setFree()
                break
            }
            i++
        }
    }

    /**
     * 用户掉线.
     */
    protected fun handleDisconnect(errorCode: Int, roomID: String) {
        Log.e("userUnlogin", ": onDisconnected, roomID:" + roomID + ", errorCode:" + errorCode)
    }

    /**
     * 房间内用户删除流.
     */
    protected fun handleStreamDeleted(listStream: Array<ZegoStreamInfo>?, roomID: String) {
        if (listStream != null && listStream.size > 0) {
            for (i in listStream.indices) {
                Log.e("streamDeleted", listStream[i].userName + ": deleted stream(" + listStream[i].streamID + ")")
                stopPlay(listStream[i].streamID)
            }
        }
    }

    protected fun stopPlay(streamID: String) {
        if (!TextUtils.isEmpty(streamID)) {
            // 临时处理
            handlePlayStop(1, streamID)

            // 输出播放状态
            Log.e("stopPlay", ": stop play stream(" + streamID + ")")
            mZegoLiveRoom.stopPlayingStream(streamID)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 清空回调, 避免内存泄漏
        mZegoLiveRoom.setZegoLivePublisherCallback(null)
        mZegoLiveRoom.setZegoLivePlayerCallback(null)
        mZegoLiveRoom.setZegoRoomCallback(null)
        // 退出房间
        mZegoLiveRoom.logoutRoom()
        for (viewLive in mListViewLive) {
            viewLive.destroy()
        }
    }

    /**
     * 用户更新.
     */
    protected fun handleUserUpdate(listUser: Array<ZegoUserState>?, updateType: Int) {
        if (listUser != null) {
            if (updateType == ZegoIM.UserUpdateType.Total) {
                //全量更新。
                //需要忽略原来的所有用户，当前用户列表中包含所有用户信息
//                mListRoomUser.clear()
            }

            if (updateType == ZegoIM.UserUpdateType.Increase) {
                //增量更新。
                //基于原来的用户列表，当前用户列表仅包含需要更新的用户信息，需要根据用户上的 flag 标识添加到原列表中或者从原列表中移除
//                for (zegoUserState in listUser) {
//                    if (zegoUserState.updateFlag == ZegoIM.UserUpdateFlag.Added) {
//                        mListRoomUser.add(zegoUserState)
//                    } else if (zegoUserState.updateFlag == ZegoIM.UserUpdateFlag.Deleted) {
//                        mListRoomUser.remove(zegoUserState)
//                    }
//                }
            }

            for (zegoUserState in listUser) {
                //主播关闭直播间
                if (zegoUserState.roomRole == ZegoConstants.RoomRole.Anchor && zegoUserState.updateFlag == ZegoIM.UserUpdateFlag.Deleted) {
                    Log.e("roomClose", "关闭直播间")
                }

            }
        }

    }
}