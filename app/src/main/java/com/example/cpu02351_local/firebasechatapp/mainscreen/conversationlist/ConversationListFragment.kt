package com.example.cpu02351_local.firebasechatapp.mainscreen.conversationlist

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cpu02351_local.firebasechatapp.R
import com.example.cpu02351_local.firebasechatapp.localdatabase.DaggerRoomLocalDatabaseComponent
import com.example.cpu02351_local.firebasechatapp.localdatabase.RoomLocalDatabase
import com.example.cpu02351_local.firebasechatapp.mainscreen.contactlist.ContactItem
import com.example.cpu02351_local.firebasechatapp.messagelist.MessageListActivity
import com.example.cpu02351_local.firebasechatapp.model.Conversation
import com.example.cpu02351_local.firebasechatapp.model.User
import com.example.cpu02351_local.firebasechatapp.utils.ContactConsumerView
import com.example.cpu02351_local.firebasechatapp.utils.ContactItemEvent
import com.example.cpu02351_local.firebasechatapp.utils.ContextModule
import com.example.cpu02351_local.firebasechatapp.utils.ConversationListDivider
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class ConversationListFragment :
        ContactConsumerView,
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
    private var mAdapter: ConversationItemAdapter? = null
    private var mConversationLoader: ConversationLoader = FirebaseConversationLoader()
    private lateinit var mConversationViewModel: ConversationViewModel

    @Inject lateinit var localDatabase: RoomLocalDatabase

    private fun init() {
        mConversationViewModel = ConversationViewModel(mConversationLoader, this, userId)

        DaggerRoomLocalDatabaseComponent
                .builder()
                .contextModule(ContextModule(context!!))
                .build()
                .injectInto(this)
        mConversationViewModel.setLocalDatabase(localDatabase)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onContactListLoaded(itemEvent: ContactItemEvent) {
        if (itemEvent.isFromNetwork) {
            onNetworkContactsLoaded(itemEvent.contactItems)
        } else {
            onLocalContactsLoaded(itemEvent.contactItems)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun updateUserInfo(contactItems: List<ContactItem>) {
        val userMap = HashMap<String, User>()
        contactItems.map {
            userMap[it.contactId] = User(it.contactId, it.contactName, "", it.avaUrl)
        }
        mAdapter?.updateUserInfo(userMap)
    }


    override fun onNetworkContactsLoaded(res: List<ContactItem>) {
        updateUserInfo(res)
    }

    override fun onLocalContactsLoaded(res: List<ContactItem>) {
        updateUserInfo(res)
    }

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        userId = args?.get("userId") as String
    }

    private fun dispose() {
        mConversationViewModel.dispose()
    }

    private fun updateConversations(result: List<ConversationItem>, fromNetwork: Boolean) {
        val manager = mRecyclerView.layoutManager as LinearLayoutManager
        val firstVisible = manager.findFirstCompletelyVisibleItemPosition()
        mAdapter?.setItems(result, fromNetwork)
        if (firstVisible == 0) {
            manager.scrollToPosition(0)
        }
    }

    override fun onLocalConversationsLoaded(result: List<ConversationItem>) {
        updateConversations(result, false)
    }

    override fun onConversationsLoaded(result: List<ConversationItem>) {
        updateConversations(result, true)
    }

    override fun navigate(where: ConversationItem) {
        val conversation = where.getConversation()
        val intent = MessageListActivity.newInstance(
                context!!, conversation.id, conversation.participantIds.joinToString(Conversation.ID_DELIM))
        context!!.startActivity(intent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_conversation_list, container, false)
        mRecyclerView = root.findViewById(R.id.conversationListContainer)
        mRecyclerView.addItemDecoration(ConversationListDivider(context!!))
        mRecyclerView.layoutManager = LinearLayoutManager(context)

        if (mAdapter == null) {
            mAdapter = ConversationItemAdapter(mRecyclerView, mConversationViewModel)
            mRecyclerView.adapter = mAdapter
        }
        return root
    }

    override fun onStart() {
        super.onStart()
        mConversationViewModel.resume()
        mAdapter?.resetState()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        dispose()
        super.onStop()
    }
}