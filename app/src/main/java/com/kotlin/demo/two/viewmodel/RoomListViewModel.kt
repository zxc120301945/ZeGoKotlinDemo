package com.kotlin.demo.two.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.kotlin.demo.two.KotlinZegoApplication
import com.kotlin.demo.two.bean.RoomInfo
import com.kotlin.demo.two.bean.RoomInfoEx
import com.kotlin.demo.two.util.ZegoAppUtil
import com.kotlin.demo.two.util.ZegoRoomUtil
import com.kotlin.demo.two.zego.ZegoApiManager
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by my on 2018/06/13 0013.
 */
class RoomListViewModel : ViewModel() {

    val roomListStatue: MutableLiveData<List<RoomInfo>> by lazy { MutableLiveData<List<RoomInfo>>() }

    /**
     * 线程池.
     */
    private val mExecutorService: ExecutorService = Executors.newFixedThreadPool(4)

    /**
     * 获取房间列表.
     */
    fun getRoomList(context: Context) {
        mExecutorService.execute(Runnable {
            kotlin.run {
                val mQueue = Volley.newRequestQueue(context)
                var appID = ZegoApiManager.getInstance().appID
                // 区分国内环境与国际环境
                var domain = if (ZegoAppUtil.isInternationalProduct(appID)) "zegocloud.com" else "zego.im"
                var url = String.format("https://liveroom%d-api.%s/demo/roomlist?appid=%s", appID, domain, appID);
                //  测试环境, 使用不同的url获取房间列表
                if (ZegoApiManager.getInstance().isUseTestEvn()) {
                    var testBase = "https://test2-liveroom-api.zego.im"
                    if (ZegoAppUtil.isInternationalProduct(appID)) {
                        testBase = "https://test2-liveroom-api.zegocloud.com"
                    }
                    url = String.format("%s/demo/roomlist?appid=%s", testBase, appID)
                }
                val request = StringRequest(url, Response.Listener<String> {
                    val gson = Gson()
                    val roomInfoEx = gson.fromJson<RoomInfoEx>(it, RoomInfoEx::class.java)
                    var list: ArrayList<RoomInfo> = ArrayList<RoomInfo>()
                    if (roomInfoEx != null && roomInfoEx.data != null) {
                        var roomInfo = roomInfoEx?.data?.room_list
                        roomInfo?.forEach {
                            if (it.stream_info != null) {
                                when (it?.stream_info?.size) {
                                    0 -> {
                                    }
                                    else -> {
                                        list.add(it)
                                    }
                                }
                            }
                        }
                        roomListStatue.value = list
                    }
                }, Response.ErrorListener {

                })
                mQueue.add(request)
            }
        })
    }

}