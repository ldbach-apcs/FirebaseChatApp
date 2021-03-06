package com.example.cpu02351_local.firebasechatapp.localdatabase

import com.example.cpu02351_local.firebasechatapp.model.AbstractMessage
import com.example.cpu02351_local.firebasechatapp.model.Conversation
import com.example.cpu02351_local.firebasechatapp.model.User
import io.reactivex.Completable
import io.reactivex.Single

interface LocalDatabase {
    fun loadUserByIds(ids : List<String>): Single<List<User>>
    fun loadUserAll(): Single<List<User>>
    fun saveUserAll(users: List<User>): Completable

    fun loadConversationAll(): Single<List<Conversation>>
    fun saveConversationAll(conversations: List<Conversation>): Completable

    fun loadMessages(conversationId: String): Single<List<AbstractMessage>>
    fun saveMessageAll(messages: List<AbstractMessage>, conversationId: String): Completable
    fun updateConversationLastMessage(conversationId: String, id: String): Completable
}