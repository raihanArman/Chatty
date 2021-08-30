package com.example.chatty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatty.NewMessageActivity.Companion.USER_KEY
import com.example.chatty.databinding.ActivityChatLogBinding
import com.example.chatty.databinding.ChatFromRowBinding
import com.example.chatty.databinding.ChatToRowBinding
import com.example.chatty.databinding.UserRowNewMessageBinding
import com.example.chatty.model.User
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.groupiex.plusAssign

class ChatLogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatLogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_log)

        val user = intent.getParcelableExtra<User>(USER_KEY)
        supportActionBar?.title = user!!.username

        val adapter = GroupieAdapter()
        adapter += ChatFrom()
        adapter += ChatTo()
        adapter += ChatFrom()
        adapter += ChatTo()

        binding.rvMessage.layoutManager = LinearLayoutManager(this)
        binding.rvMessage.adapter = adapter

    }
}

class ChatFrom: BindableItem<ChatFromRowBinding>(){
    override fun bind(binding: ChatFromRowBinding, position: Int) {

    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

}

class ChatTo: BindableItem<ChatToRowBinding>(){
    override fun bind(binding: ChatToRowBinding, position: Int) {

    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

}
