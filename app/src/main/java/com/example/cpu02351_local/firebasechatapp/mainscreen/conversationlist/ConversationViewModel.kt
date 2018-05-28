package com.example.cpu02351_local.firebasechatapp.mainscreen.conversationlist

import android.content.Context
import android.util.Log
import com.example.cpu02351_local.firebasechatapp.localdatabase.LocalDatabase
import com.example.cpu02351_local.firebasechatapp.messagelist.MessageListActivity
import com.example.cpu02351_local.firebasechatapp.model.Conversation
import io.reactivex.Observer
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

class ConversationViewModel(private val conversationLoader: ConversationLoader,
                            private val conversationView: ConversationView,
                            val userId: String) {
    private var mDisposable: Disposable? = null
    private var localDatabase: LocalDatabase? = null

    init {
        loadConversations()
    }

    fun dispose() {
        if (mDisposable != null && !mDisposable!!.isDisposed) {
            mDisposable!!.dispose()
        }
    }

    fun resume() {
        loadConversations()
    }

    private fun loadConversations() {
        val obs = conversationLoader.loadConversations(userId)
        dispose()
        obs.subscribe(object : Observer<List<Conversation>> {
            override fun onComplete() {
                // Do nothing
            }

            override fun onNext(t: List<Conversation>) {
                if (mLocalDisposable != null && !mLocalDisposable!!.isDisposed) {
                    mLocalDisposable!!.dispose()
                }

                conversationView.onConversationsLoaded(t)
                localDatabase?.saveConversationAll(t)
                        ?.subscribe { Log.d("DEBUGGING", "Conversation saved") }
            }

            override fun onSubscribe(d: Disposable) {
                mDisposable = d
            }

            override fun onError(e: Throwable) {
                // Do nothing for now
            }
        })
    }

    fun onConversationClicked(context: Context, conversation: Conversation) {
        startConversation(context, conversation)
    }

    private fun startConversation(context: Context, conversation: Conversation) {
        val intent = MessageListActivity.newInstance(
                context, conversation.id, conversation.participantIds.joinToString(Conversation.ID_DELIM))
        context.startActivity(intent)
    }

    fun setLocalDatabase(localDatabase: LocalDatabase) {
        Log.d("LOCAL_LOAD", "LocalDatabaseEstablished")
        this.localDatabase = localDatabase
        loadLocalConversation()
    }

    private var mLocalDisposable: Disposable ?=null
    private fun loadLocalConversation() {
         localDatabase!!.loadConversationAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<List<Conversation>> {
                    override fun onSuccess(t: List<Conversation>) {
                        conversationView.onLocalConversationsLoaded(
                                t.filter { it.participantIds.contains(userId) })
                    }

                    override fun onSubscribe(d: Disposable) {
                        mLocalDisposable = d
                    }

                    override fun onError(e: Throwable) {
                        // Do nothing for now
                    }

                })
    }
}
