package com.example.cpu02351_local.firebasechatapp.mainscreen.conversationlist

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cpu02351_local.firebasechatapp.model.Conversation
import com.example.cpu02351_local.firebasechatapp.R
import com.example.cpu02351_local.firebasechatapp.localdatabase.DaggerRoomLocalUserDatabaseComponent
import com.example.cpu02351_local.firebasechatapp.localdatabase.RoomLocalUserDatabase
import com.example.cpu02351_local.firebasechatapp.model.User
import com.example.cpu02351_local.firebasechatapp.utils.ContextModule
import com.example.cpu02351_local.firebasechatapp.utils.ConversationListDivider
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class ConversationListFragment :
        ConversationView,
        Fragment() {

    companion object {
        @JvmStatic
        fun newInstance(userId: String): ConversationListFragment {
            val temp = ConversationListFragment()
            val args = Bundle()
            args.putString("userId", userId)
            temp.arguments = args
            return temp
        }
    }

    private lateinit var userId: String
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: ConversationListAdapter
    private var mConversationLoader: ConversationLoader = FirebaseConversationLoader()
    private lateinit var mConversationViewModel: ConversationViewModel

    @Inject lateinit var localUserDatabase: RoomLocalUserDatabase

    private fun init() {
        mConversationViewModel = ConversationViewModel(mConversationLoader, this, userId)
        mAdapter = ConversationListAdapter(ArrayList(), mRecyclerView, mConversationViewModel)
        mRecyclerView.adapter = mAdapter

        DaggerRoomLocalUserDatabaseComponent
                .builder()
                .contextModule(ContextModule(context!!))
                .build()
                .injectInto(this)
        loadUsers()
    }

    private fun loadUsers() {
        localUserDatabase.loadAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { res ->
                    val userMap = HashMap<String, User>()
                    res.forEach {
                        userMap[it.id] = it
                    }
                    mAdapter.updateUserInfo(userMap)
                }
    }

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        userId = args?.get("userId") as String
    }

    private fun dispose() {
        mConversationViewModel.dispose()
    }

    override fun onConversationsLoaded(result: List<Conversation>) {
        mAdapter.updateList(result)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_conversation_list, container, false)
        mRecyclerView = root.findViewById(R.id.conversationListContainer)
        mRecyclerView.addItemDecoration(ConversationListDivider(context!!))
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        return root
    }

    override fun onStart() {
        super.onStart()
        init()
    }

    override fun onStop() {
        super.onStop()
        dispose()
    }
}