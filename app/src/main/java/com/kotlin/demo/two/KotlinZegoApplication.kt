package com.kotlin.demo.two

import android.app.Application
import android.content.Context
import com.kotlin.demo.two.zego.ZegoApiManager

/**
 * Created by my on 2018/06/13 0013.
 */
open class KotlinZegoApplication : Application() {

    companion object {

        private lateinit var application: Application

        fun getApp(): Application {
            return application
        }

    }

    override fun onCreate() {
        super.onCreate()
        KotlinZegoApplication.application = this
        // 初始化sdk
        ZegoApiManager.getInstance().initSDK()
    }
}