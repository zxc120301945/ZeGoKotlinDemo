package com.kotlin.demo.two.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Surface
import android.view.WindowManager
import com.kotlin.demo.two.R
import com.kotlin.demo.two.constants.IntentExtra

/**
 * Created by my on 2018/06/14 0014.
 */
open class SinglePublishActivity : AppCompatActivity() {

    private var mPublishTitle: String = ""
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
    }

    private fun initData(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val intent = intent
            mPublishTitle = intent.getStringExtra(IntentExtra.PUBLISH_TITLE)
        }
    }


}