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
import com.example.chatty.model.ChatMessage
import com.example.chatty.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.groupiex.plusAssign

class ChatLogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatLogBinding
    val adapter = GroupieAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_log)


        val user = intent.getParcelableExtra<User>(USER_KEY)
        supportActionBar?.title = user!!.username

        binding.rvMessage.layoutManager = LinearLayoutManager(this)
        binding.rvMessage.adapter = adapter
        listenForMessage()

        binding.btnSend.setOnClickListener {
            val text = binding.etMessage.text.toString()
            val fromId = FirebaseAuth.getInstance().uid
            val user = intent.getParcelableExtra<User>(USER_KEY)
            val toId = user!!.uid

            val ref = FirebaseDatabase.getInstance().getReference("/messages").push()
            val chatMessage = ChatMessage(
                ref.key!!,
                text,
                fromId!!,
                toId,
                System.currentTimeMillis()/1000
            )
            ref.setValue(chatMessage)
                .addOnSuccessListener {

                }
        }
    }

    private fun listenForMessage() {
        val ref = FirebaseDatabase.getInstance().getReference("/messages")
        ref.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if(chatMessage != null) {
                    if(chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        adapter.add(ChatFrom(chatMessage))
                    }else {
                        adapter.add(ChatTo(chatMessage))
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setupDummyData() {

        val adapter = GroupieAdapter()
//        adapter += ChatFrom()
//        adapter += ChatTo()
//        adapter += ChatFrom()
//        adapter += ChatTo()

        binding.rvMessage.adapter = adapter

    }
}

class ChatFrom(val chatMessage: ChatMessage): BindableItem<ChatFromRowBinding>(){
    override fun bind(binding: ChatFromRowBinding, position: Int) {
        binding.tvMessage.text = chatMessage.text
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

}

class ChatTo(val chatMessage: ChatMessage): BindableItem<ChatToRowBinding>(){
    override fun bind(binding: ChatToRowBinding, position: Int) {
        binding.tvMessage.text = chatMessage.text
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

}
