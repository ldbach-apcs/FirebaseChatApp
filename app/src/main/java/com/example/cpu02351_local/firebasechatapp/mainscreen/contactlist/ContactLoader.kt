package com.example.cpu02351_local.firebasechatapp.mainscreen.contactlist

import com.example.cpu02351_local.firebasechatapp.model.User
import io.reactivex.Single

interface ContactLoader {
    fun loadContacts(userId: String): Single<List<User>>
}