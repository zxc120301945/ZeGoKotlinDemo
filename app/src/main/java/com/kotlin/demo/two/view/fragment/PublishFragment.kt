package com.kotlin.demo.two.view.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kotlin.demo.two.R
import com.kotlin.demo.two.view.SinglePlayerAcivity
import com.kotlin.demo.two.view.SinglePublishActivity
import kotlinx.android.synthetic.main.fragment_publish.*
import org.jetbrains.anko.sdk23.listeners.onClick

/**
 * Created by my on 2018/06/13 0013.
 */
class PublishFragment : Fragment() {

    companion object {
        fun newInstance(): PublishFragment {
            return PublishFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_publish, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btn_start_publish.onClick {
            var title = et_publish_title.text.toString()
            if(title == null || title.isEmpty()){
                title = System.currentTimeMillis().toString()
            }
            SinglePublishActivity().actionStart(activity, title)
        }
    }
}