package com.kotlin.demo.two.util

import android.text.TextUtils
import com.kotlin.demo.two.widget.ViewLive
import com.zego.zegoliveroom.constants.ZegoBeauty
import java.util.*

/**
 * Created by my on 2018/06/13 0013.
 */
object ZegoRoomUtil {

    val ROOM_TYPE_SINGLE = 1

    val ROOM_TYPE_MORE = 2

    val ROOM_TYPE_MIX = 3

    val ROOM_TYPE_GAME = 4

    val ROOM_TYPE_WOLF = 5

    val ROOM_PREFIX_SINGLE_ANCHOR = "#d-"

    val ROOM_PREFIX_MORE_ANCHORS = "#m-"

    val ROOM_PREFIX_MIX_STREAM = "#s-"

    val ROOM_PREFIX_GAME_LIVING = "#g-"

    val ROOM_PREFIX_WERE_WOLVES = "#i-"

    val publishStreamID: String
        get() = "s-" + PreferenceUtil.getInstance().userID + "-" + System.currentTimeMillis()


    fun getRoomID(roomType: Int): String {
        var roomID: String? = null
        when (roomType) {
            ROOM_TYPE_SINGLE -> roomID = ROOM_PREFIX_SINGLE_ANCHOR
            ROOM_TYPE_MORE -> roomID = ROOM_PREFIX_MORE_ANCHORS
            ROOM_TYPE_MIX -> roomID = ROOM_PREFIX_MIX_STREAM
            ROOM_TYPE_GAME -> roomID = ROOM_PREFIX_GAME_LIVING
            ROOM_TYPE_WOLF -> roomID = ROOM_PREFIX_WERE_WOLVES
        }

        return roomID!! + PreferenceUtil.getInstance().userID
    }

    fun getZegoBeauty(index: Int): Int {

        var beauty = 0

        when (index) {
            0 -> beauty = ZegoBeauty.NONE
            1 -> beauty = ZegoBeauty.POLISH
            2 -> beauty = ZegoBeauty.WHITEN
            3 -> beauty = ZegoBeauty.POLISH or ZegoBeauty.WHITEN
            4 -> beauty = ZegoBeauty.POLISH or ZegoBeauty.SKIN_WHITEN
        }

        return beauty
    }

}