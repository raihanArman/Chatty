package com.example.chatty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatty.databinding.ActivityLatestMessageBinding
import com.example.chatty.databinding.ChatToRowBinding
import com.example.chatty.databinding.LatestMessageRowBinding
import com.example.chatty.model.ChatMessage
import com.example.chatty.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.databinding.BindableItem

class LatestMessageActivity : AppCompatActivity() {
    companion object{
        var currentUser: User?= null
    }

    val adapter = GroupieAdapter()
    val latestMessageMap = HashMap<String, ChatMessage>()

    private lateinit var binding: ActivityLatestMessageBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_latest_message)

        verifyUserIsLoggedIn()
        fetchUser()

        binding.rvMessageLatest.layoutManager = LinearLayoutManager(this)
        binding.rvMessageLatest.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        binding.rvMessageLatest.adapter = adapter
        listenMessage()

        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this, ChatLogActivity::class.java)

            val row = item as LatestMessageViewHolder

            intent.putExtra(NewMessageActivity.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }

    }

    private fun listenMessage() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                latestMessageMap[snapshot.key!!] = chatMessage!!
                refreshRecyclerViewMessages()

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                latestMessageMap[snapshot.key!!] = chatMessage!!
                refreshRecyclerViewMessages()
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

    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        latestMessageMap.values.forEach {
            adapter.add(LatestMessageViewHolder(it))
        }
    }

    private fun fetchUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/${uid}")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if(uid == null){
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_sign_out -> {
                signOutProcess()
            }
            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun signOutProcess() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, RegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}

class LatestMessageViewHolder(val chatMessage: ChatMessage): BindableItem<LatestMessageRowBinding>(){

    var chatPartnerUser: User? = null
    override fun bind(viewBinding: LatestMessageRowBinding, position: Int) {
        viewBinding.tvMessage.text = chatMessage.text

        val chatPartnerId: String
        if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
            chatPartnerId = chatMessage.toId
        }else{
            chatPartnerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnerUser = snapshot.getValue(User::class.java)
                viewBinding.tvUsername.text = chatPartnerUser!!.username
                Picasso.get().load(chatPartnerUser!!.profileImage).into(viewBinding.ivUser)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }

}