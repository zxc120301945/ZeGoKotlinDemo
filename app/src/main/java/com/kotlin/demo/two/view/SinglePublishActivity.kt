package com.kotlin.demo.two.view

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.kotlin.demo.two.R
import com.kotlin.demo.two.constants.IntentExtra
import com.kotlin.demo.two.viewmodel.SinglePublishViewModel
import kotlinx.android.synthetic.main.activity_single_publish.*
import org.jetbrains.anko.sdk23.listeners.onClick

/**
 * Created by my on 2018/06/14 0014.
 */
open class SinglePublishActivity : AppCompatActivity() {

    private lateinit var viewModel: SinglePublishViewModel

    /**
     * 启动入口.
     *
     * @param activity     源activity
     * @param publishTitle 视频标题
     */
    open fun actionStart(activity: Activity, publishTitle: String) {
        val intent = Intent(activity, SinglePublishActivity::class.java)
        intent.putExtra(IntentExtra.PUBLISH_TITLE, publishTitle)
        activity.startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 禁止手机休眠
        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_single_publish)
        initViewModel()
        initData()
        initViews()
        doBusiness()
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(SinglePublishViewModel::class.java)
    }

    private fun initData() {
        viewModel.initData(true,this,intent)
        vl_big_view.streamID = viewModel.mRoomID
    }

    private fun initViews() {
        viewModel.initView(vl_big_view)
        viewModel.mZegoLiveRoom.setPreviewView(vl_big_view)
        viewModel.mZegoLiveRoom.startPreview()
        single_close.onClick {
            finish()
        }
    }

    private fun doBusiness() {
        viewModel.doBusiness()
    }

    override fun onDestroy() {
        viewModel.destory()
        super.onDestroy()
    }

}