package com.kotlin.demo.two.view

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.kotlin.demo.two.R
import com.kotlin.demo.two.bean.RoomInfo
import com.kotlin.demo.two.constants.IntentExtra
import com.kotlin.demo.two.viewmodel.SinglePlayerViewModel
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

    private lateinit var viewModel: SinglePlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 禁止手机休眠
        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_single_player)
        initViewModel()
        viewModel.initData(false, this, intent)
        initViews()
        viewModel.doBusiness()
    }

    fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(SinglePlayerViewModel::class.java)
    }

    private fun initViews() {
        viewModel.initView(vl_big_view)
        single_close.onClick {
            finish()
        }
    }

    override fun onDestroy() {
        viewModel.destory()
        super.onDestroy()
    }
}