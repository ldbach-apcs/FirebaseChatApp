package com.example.cpu02351_local.firebasechatapp.model

import java.util.*

class Conversation(val id: String, users: Array<User> = arrayOf()) {
    var participantIds: List<String> = ArrayList()
    var createdTime = -1L

    var lastMessage: Message? = null

    init {
        participantIds = users.joinToString("$").split("$")
    }

    companion object {
        const val ID_DELIM = "@"
        @JvmStatic
        fun uniqueId(users: Array<User>) : String {
            if (users.size > 2) {
                return UUID.randomUUID().toString()
            }
             users.sort()
            return users.joinToString(ID_DELIM).trim(ID_DELIM[0])
        }

        fun uniqueId(userIds: Array<String>) : String {
           if (userIds.size > 2) {
               return UUID.randomUUID().toString()
           }
            userIds.sort()
            return userIds.joinToString(ID_DELIM).trim(ID_DELIM[0])
        }
    }

    fun getTime() : String {
        createdTime = lastMessage?.atTime ?: createdTime
        val interval = System.currentTimeMillis() - createdTime
        val minutes = interval / (1000 * 60)
        val hours = minutes / 60
        val days= hours / 24

        return when {
            minutes == 0L -> "Just now"
            hours == 0L -> "$minutes minutes ago"
            days == 0L -> "$hours hours ago"
            else -> "$days days ago"
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is Conversation && other.id == id
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + participantIds.hashCode()
        result = 31 * result + createdTime.hashCode()
        return result
    }
}