package com.example.cpu02351_local.firebasechatapp.ChatCore.boundary

import com.example.cpu02351_local.firebasechatapp.ChatCore.model.Conversation
import com.example.cpu02351_local.firebasechatapp.ChatCore.model.Message
import com.example.cpu02351_local.firebasechatapp.ChatCore.model.User

interface NetworkDataSource {
    fun loadUserDetail(userId: String) : User
    fun loadConversationList(displayUnit: ListConversationDisplayUnit, userId: String)
    fun loadConversation(conversationId: String) : Conversation
    fun loadMessage(messageId : String): Message
    fun addConversation(displayUnit: ListConversationDisplayUnit, participants: List<String>)
}