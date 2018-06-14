package com.kotlin.demo.two.viewmodel

import android.text.TextUtils
import android.util.Log
import com.kotlin.demo.two.viewmodel.base.BaseViewModel
import com.kotlin.demo.two.widget.ViewLive
import com.zego.zegoliveroom.callback.IZegoLivePlayerCallback
import com.zego.zegoliveroom.callback.IZegoRoomCallback
import com.zego.zegoliveroom.callback.im.IZegoIMCallback
import com.zego.zegoliveroom.constants.ZegoConstants
import com.zego.zegoliveroom.constants.ZegoIM
import com.zego.zegoliveroom.constants.ZegoVideoViewMode
import com.zego.zegoliveroom.entity.*
import java.util.*

/**
 * Created by my on 2018/06/14 0014.
 */
class SinglePlayerViewModel : BaseViewModel() {

    private var mListViewLive = LinkedList<ViewLive>()

    override fun initView(viewLive: ViewLive) {
        super.initView(viewLive)
        viewLive.setZegoLiveRoom(mZegoLiveRoom)
        mListViewLive.add(viewLive)
    }

    fun doBusiness() {
        //登陆房间
        mZegoLiveRoom.loginRoom(mRoomID, ZegoConstants.RoomRole.Audience) { errorCode, zegoStreamInfos ->
            if (errorCode == 0) {
                handleAudienceLoginRoomSuccess(zegoStreamInfos)
            } else {
                loginRoomFail(errorCode)
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
                    mOldSavedStreamList?.forEach {
                        Log.e("SingleAnchorPlayA", "Quick play: " + it)
                        startPlay(it)
                    }
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

    /**
     * 开始播放流.
     */
    protected fun startPlay(streamID: String) {

        if (TextUtils.isEmpty(streamID)) {
            return
        }

        // 设置流信息
        mViewLive!!.streamID = streamID
        mViewLive!!.isPlayView = true

        // 输出播放状态
        Log.e("playerStatus", ": start play stream(" + streamID + ")")

        // 初始化拉流参数, 外部渲染模式使用
//        initPlayConfigs(freeViewLive, streamID)

        // 播放
        mZegoLiveRoom.startPlayingStream(streamID, mViewLive!!.textureView)
        //ScaleAspectFill
        //等比缩放填充整View，可能有部分被裁减。 SDK 默认值。
        //ScaleAspectFit
        //等比缩放，可能有黑边。
        //ScaleToFill
        //填充整个View，视频可能会变形。
        mZegoLiveRoom.setViewMode(ZegoVideoViewMode.ScaleAspectFill, streamID)
    }

    override fun destory() {
        super.destory()
        if(mListViewLive != null && !mListViewLive.isEmpty()){
            mListViewLive.forEach{
                it?.destroy()
            }
        }
    }
}