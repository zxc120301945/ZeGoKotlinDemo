package com.kotlin.demo.two.viewmodel.base

import android.app.Activity
import android.arch.lifecycle.ViewModel
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.kotlin.demo.two.constants.IntentExtra
import com.kotlin.demo.two.util.ZegoRoomUtil
import com.kotlin.demo.two.widget.ViewLive
import com.kotlin.demo.two.zego.ZegoApiManager
import com.zego.zegoliveroom.ZegoLiveRoom
import com.zego.zegoliveroom.entity.ZegoStreamInfo
import java.util.*

/**
 * Created by my on 2018/06/14 0014.
 */
open class BaseViewModel : ViewModel() {

    val mZegoLiveRoom: ZegoLiveRoom = ZegoApiManager.getInstance().getZegoLiveRoom()
    var mOldSavedStreamList: ArrayList<String>? = null
    var mPublishTitle: String = ""
    var mRoomID: String = ""
    var mIsAnchor: Boolean = false
    lateinit var mActivity: Activity
    lateinit var mViewLive: ViewLive

    open fun initData(isAnchor: Boolean, activity: Activity, intent: Intent) {
        if (intent == null) {
            return
        }
        val intent = intent
        mPublishTitle = intent.getStringExtra(IntentExtra.PUBLISH_TITLE)
        when (isAnchor) {
            false -> {
                mRoomID = intent.getStringExtra(IntentExtra.ROOM_ID)
                mOldSavedStreamList = intent.getStringArrayListExtra(IntentExtra.LIST_STREAM)
            }
            else -> mRoomID = ZegoRoomUtil.getRoomID(ZegoRoomUtil.ROOM_TYPE_SINGLE)
        }
        mIsAnchor = isAnchor
        mActivity = activity
    }

    open fun initView(viewLive: ViewLive) {
        mViewLive = viewLive
    }

    /**
     * 登录房间失败.
     */
    protected fun loginRoomFail(errorCode: Int) {
        // 打印log
        if (mIsAnchor) {
            Log.e("SingleLoginRoomFail", ": onLoginRoom fail(" + mRoomID + ") errorCode:" + errorCode)
        } else {
            Log.e("publishLoginRoomFail", ": onLoginRoom fail(" + mRoomID + "), errorCode:" + errorCode)
        }
    }

    /**
     * 用户掉线.
     */
    protected fun handleDisconnect(errorCode: Int, roomID: String) {
        if (mIsAnchor) {
            Log.e("publishdisconnect", ": onDisconnected, roomID:" + roomID + ", errorCode:" + errorCode)
        } else {
            Log.e("userUnlogin", ": onDisconnected, roomID:" + roomID + ", errorCode:" + errorCode)
        }
    }

    /**
     * 房间内用户删除流.
     */
    protected fun handleStreamDeleted(listStream: Array<ZegoStreamInfo>?, roomID: String) {
        if (listStream != null && listStream.size > 0) {
            for (i in listStream.indices) {
                Log.e("publishstreamDeleted", listStream[i].userName + ": deleted stream(" + listStream[i].streamID + ")")
                stopPlay(listStream[i].streamID)
            }
        }
    }

    protected fun stopPlay(streamID: String) {
        if (!TextUtils.isEmpty(streamID)) {

            // 输出播放状态
            Log.e("stopPlay", ": stop play stream(" + streamID + ")")
            mZegoLiveRoom.stopPlayingStream(streamID)
        }
    }

    open fun destory() {
        // 清空回调, 避免内存泄漏
        mZegoLiveRoom.setZegoLivePublisherCallback(null)
        mZegoLiveRoom.setZegoLivePlayerCallback(null)
        mZegoLiveRoom.setZegoRoomCallback(null)
        // 退出房间
        mZegoLiveRoom.logoutRoom()
        mViewLive?.destroy()
    }

}
