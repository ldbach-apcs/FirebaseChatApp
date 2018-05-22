package com.example.cpu02351_local.firebasechatapp.mainscreen.conversationlist

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cpu02351_local.firebasechatapp.ChatViewModel.model.Conversation
import com.example.cpu02351_local.firebasechatapp.databinding.ItemConversationListBinding
import java.util.*


class ConversationListAdapter(private val mConversations: ArrayList<Conversation>,
                              private val mRecyclerView: RecyclerView,
                              private val mViewModel: ConversationViewModel)
    : RecyclerView.Adapter<ConversationListAdapter.ConversationViewHolder>() {

    private val mItemAction: View.OnClickListener? = View.OnClickListener { v ->
        val pos = mRecyclerView.getChildAdapterPosition(v)
        if (pos != RecyclerView.NO_POSITION) {
            val context = mRecyclerView.context
            mViewModel.onConversationClicked(context, mConversations[pos].id)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemConversationListBinding.inflate(layoutInflater, parent, false)

        val v = binding.root
        v.setOnClickListener(mItemAction)

        return ConversationViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mConversations.size
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(mConversations[position])
    }

    fun updateList(newList: List<Conversation>) {
        Collections.sort(newList, kotlin.Comparator { con1, con2 ->
            return@Comparator con1.createdTime.compareTo(con2.createdTime)
        })

        val oldList = ArrayList<Conversation>(mConversations)
        val diffResult = DiffUtil.calculateDiff(ConversationDiffCallback(oldList, newList), true)

        mConversations.clear()
        mConversations.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    class ConversationViewHolder(private val binding: ItemConversationListBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(conversation: Conversation) {
            binding.conversation = conversation
            binding.executePendingBindings()
        }
    }

    class ConversationDiffCallback(private val oldList : List<Conversation>,
                                   private val newList : List<Conversation>) : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].createdTime == newList[newItemPosition].createdTime
        }

    }
}