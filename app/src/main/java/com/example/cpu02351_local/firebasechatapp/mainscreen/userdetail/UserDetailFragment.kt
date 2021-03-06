package com.example.cpu02351_local.firebasechatapp.mainscreen.userdetail

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.example.cpu02351_local.firebasechatapp.loginscreen.AuthenticationActivity
import com.example.cpu02351_local.firebasechatapp.model.User
import com.example.cpu02351_local.firebasechatapp.utils.GlideDataBinder
import com.example.cpu02351_local.firebasechatapp.loginscreen.LogInHelper
import com.example.cpu02351_local.firebasechatapp.R
import com.example.cpu02351_local.firebasechatapp.changeinfoscreen.ChangeInfoActivity
import com.example.cpu02351_local.firebasechatapp.databinding.FragmentUserDetailBinding
import kotlinx.android.synthetic.main.fragment_user_detail.*


class UserDetailFragment :
        UserDetailView,
        Fragment() {

    private var mUserDetailLoader: UserDetailLoader = FirebaseUserDetailLoader()
    private lateinit var userId: String
    private lateinit var mUserDetailViewModel: UserDetailViewModel
    private lateinit var mAvatar: ImageView

    private lateinit var mBinding: FragmentUserDetailBinding

    companion object {

        const val PICK_IMAGE = 113
        const val STORAGE_PERM = 114

        @JvmStatic
        fun newInstance(userId: String): UserDetailFragment {
            val temp = UserDetailFragment()
            val args = Bundle()
            args.putString("userId", userId)
            temp.arguments = args
            return temp
        }
    }

    private fun init() {
        mUserDetailViewModel = UserDetailViewModel(mUserDetailLoader, this, userId)
    }

    private fun dispose() {
        mUserDetailViewModel.dispose()
    }

    override fun onUpdateAvatarFailed() {
        Toast.makeText(context, "Failed to change profile picture", Toast.LENGTH_SHORT).show()
    }

    override fun onUserDetailLoaded(userDetail: User) {
        mBinding.currentUser = userDetail
        mBinding.executePendingBindings()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_detail, container, false)
        val v = mBinding.root
        mAvatar = v.findViewById(R.id.avatar)
        mAvatar.setOnClickListener {
            chooseNewAva()
        }

        v.findViewById<View>(R.id.log_out).setOnClickListener {
            val intent = Intent(context, AuthenticationActivity::class.java)
            LogInHelper.logOut(activity?.applicationContext!!)
            context?.startActivity(intent)
            activity?.finish()
        }

        v.findViewById<ImageView>(R.id.btn_changeInfo).setOnClickListener { _ ->
            val intent = Intent(context, ChangeInfoActivity::class.java)
            intent.putExtra("current_name", text_name.text.toString())
            intent.putExtra("current_id", userId)
            context?.startActivity(intent)
        }

        return v
    }

    private fun chooseNewAva() {
        if (shouldAskPermission()) {
            askPermission()
        } else {
            startImageActivity()
        }
    }

    private fun startImageActivity() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
    }

    private fun askPermission() {
        ActivityCompat.requestPermissions(activity!!,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_PERM)
    }

    private fun shouldAskPermission(): Boolean {
        return ContextCompat
                .checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == STORAGE_PERM) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startImageActivity()
            }
        }
        else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        userId = args?.get("userId") as String
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            val filePath = data?.data
            if (filePath != null) {
                GlideDataBinder.setImageOffline(mAvatar, filePath)
                mUserDetailViewModel.changeAvatar(filePath)
            }
        }
        else
            super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    override fun onDestroy() {
        super.onDestroy()
        dispose()
    }
}