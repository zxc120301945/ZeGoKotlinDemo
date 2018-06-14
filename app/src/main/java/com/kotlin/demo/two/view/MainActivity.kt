package com.kotlin.demo.two.view

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.kotlin.demo.two.R
import com.kotlin.demo.two.view.fragment.PublishFragment
import com.kotlin.demo.two.view.fragment.RoomListFragment
import com.kotlin.demo.two.view.fragment.SettingFragment
import com.kotlin.demo.two.viewmodel.MainViewModel
import com.kotlin.demo.two.widget.NavigationBar
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.support.v4.onPageChangeListener

class MainActivity : AppCompatActivity() {

    private var mFragments: ArrayList<Fragment> = ArrayList<Fragment>()
    private lateinit var viewModel: MainViewModel
    private var mTabSelected: Int = -1


    private val mRoomListFragment: RoomListFragment by lazy { RoomListFragment.newInstance() }
    private val mPublishFragment: PublishFragment by lazy { PublishFragment.newInstance() }
    private val mSettingFragment: SettingFragment by lazy { SettingFragment.newInstance() }
    private val mPagerAdapter: PagerAdapter by lazy { PagerAdapter.newInstance(supportFragmentManager, mFragments) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initModel()
    }

    private fun initView() {
        mFragments?.add(mRoomListFragment)
        mFragments?.add(mPublishFragment)
        mFragments?.add(mSettingFragment)

        nb.selectTab(0)
        vp.setAdapter(mPagerAdapter)
        bindEvent()
    }

    private fun bindEvent() {
        vp.onPageChangeListener {
            onPageSelected {
                mTabSelected = it
                nb.selectTab(it)
            }
        }
        nb.setNavigationBarListener {
            mTabSelected = it
            vp.setCurrentItem(it, true)
        }
    }

    private fun initModel() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    class PagerAdapter(fm: FragmentManager, fragments: ArrayList<Fragment>) : FragmentPagerAdapter(fm) {

        private var mFragments: ArrayList<Fragment> = ArrayList<Fragment>()

        companion object {
            fun newInstance(fm: FragmentManager, fragments: ArrayList<Fragment>): PagerAdapter {
                return PagerAdapter(fm, fragments)
            }
        }

        init {
            mFragments = fragments
        }

        override fun getItem(position: Int): Fragment {
            return mFragments?.get(position)
        }

        override fun getCount(): Int {
            return mFragments?.size
        }

    }
}
