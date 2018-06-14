package com.kotlin.demo.two.view.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.kotlin.demo.two.R
import com.kotlin.demo.two.bean.RoomInfo
import com.kotlin.demo.two.view.SinglePlayerAcivity
import com.kotlin.demo.two.viewmodel.RoomListViewModel
import com.kotlin.demo.two.widget.CirImageView
import com.kotlin.demo.two.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.fragment_roomlist.*
import org.jetbrains.anko.sdk23.listeners.onClick

/**
 * Created by my on 2018/06/13 0013.
 */
class RoomListFragment : Fragment() {

    private lateinit var viewModel: RoomListViewModel
    private var list: ArrayList<RoomInfo> = ArrayList<RoomInfo>()
    private var mListRoomAdapter: ListRoomAdapter? = null
    var mHandler: Handler = Handler()

    companion object {
        fun newInstance(): RoomListFragment {
            return RoomListFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var roomView = inflater!!.inflate(R.layout.fragment_roomlist, container, false)
        return roomView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    private fun initView() {
        val Manager = LinearLayoutManager(activity)
        rlv_room_list.setLayoutManager(Manager)
        rlv_room_list.addItemDecoration(SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.dimen_5)))
        // 设置 进度条的颜色变化，最多可以设置4种颜色
        srl.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark, android.R.color.holo_orange_dark)
        rlv_room_list.setAdapter(mListRoomAdapter)
        bindEnent()
    }

    private fun bindEnent() {
        viewModel.roomListStatue.observe(this, Observer {
            if (it != null && !it.isEmpty()) {
                list.addAll(it)
                mHandler.post(Runnable {
                    mListRoomAdapter?.notifyDataSetChanged()
                })
            }
            srl.setRefreshing(false)
        })

        srl.setOnRefreshListener {
            // 下拉刷新, 数据清零
            list.clear()
            viewModel.getRoomList(activity.applicationContext)
        }
        mListRoomAdapter?.setOnItemClickListener(object : ListRoomAdapter.OnItemClickListener {
            override fun OnItemClick(position: Int) {
                val roomInfo = list.get(position)
                SinglePlayerAcivity().actionStart(activity, roomInfo)
            }
        })
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(RoomListViewModel::class.java)
        mListRoomAdapter = ListRoomAdapter(activity, list)
    }

    class ListRoomAdapter(context: Context, listRoom: List<RoomInfo>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private var mCtx: Context? = null
        private var mList: List<RoomInfo>? = null
        private val mLayoutInflater: LayoutInflater
        private val mResources: Resources
        val TYPE_FOOTER = Integer.MIN_VALUE
        val TYPE_ITEM = 0
        private var mOnItemClickListener: OnItemClickListener? = null


        init {
            mCtx = context
            mList = listRoom
            mLayoutInflater = LayoutInflater.from(context)
            mResources = context.resources
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            if (holder is LiveListHolder) {
                val room = mList?.get(position)
                var roomName = room?.room_name
                if (TextUtils.isEmpty(roomName)) {
                    roomName = room?.room_id
                }
                holder.tvPulishTitle?.setText(roomName)
                holder.tvPublishTime?.setText(room?.anchor_nick_name)

                if (mOnItemClickListener != null) {
                    holder.rlytItem?.onClick {
                        mOnItemClickListener?.OnItemClick(holder.getLayoutPosition())
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == TYPE_FOOTER) {
                FooterViewHolder(mLayoutInflater.inflate(R.layout.item_view_list_footer, parent, false))
            } else {
                LiveListHolder(mLayoutInflater.inflate(R.layout.item_room, parent, false))
            }
        }

        override fun getItemCount(): Int {
            var itemCount: Int = 0;
            if (mList != null && mList is List<RoomInfo>) {
                val size = mList?.size
                if (size == 0) {
                    itemCount = 0
                } else {
                    if (size is Int) {
//                        itemCount = size + 1
                        itemCount = size
                    } else {
                        itemCount = 0
                    }
                }
            } else {
                itemCount = 0
            }
            return itemCount
        }

        class LiveListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            internal var rlytItem: RelativeLayout? = null
            internal var civAvatar: CirImageView? = null
            internal var tvPulishTitle: TextView? = null
            internal var tvPublishTime: TextView? = null
            internal var tvLiveCount: TextView? = null

            init {
                rlytItem = itemView.findViewById<RelativeLayout>(R.id.rlyt_item)
                civAvatar = itemView?.findViewById<CirImageView>(R.id.civ_avatar)
                tvPulishTitle = itemView.findViewById<TextView>(R.id.tv_publish_title)
                tvPublishTime = itemView.findViewById<TextView>(R.id.tv_publish_time)
                tvLiveCount = itemView.findViewById<TextView>(R.id.tv_live_count)
            }

        }

        class FooterViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            init {
            }

            override fun toString(): String {
                return super.toString() + " '"
            }
        }

        interface OnItemClickListener {
            fun OnItemClick(position: Int)
        }

        fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
            mOnItemClickListener = onItemClickListener
        }
    }

}