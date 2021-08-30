package com.example.chatty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatty.adapter.UserAdapter
import com.example.chatty.databinding.ActivityNewMessageBinding
import com.example.chatty.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NewMessageActivity : AppCompatActivity() {

    companion object{
        const val USER_KEY = "user"
    }

    private lateinit var binding: ActivityNewMessageBinding
    private val adapter: UserAdapter by lazy {
        UserAdapter{
            showDetail(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_message)
        supportActionBar?.title = "Select User"

        binding.rvNewMessage.layoutManager = LinearLayoutManager(this)
        binding.rvNewMessage.adapter = adapter

        fetchUsers()

    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val listUser = mutableListOf<User>()
                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    if(user != null) {
                        listUser.add(user)
                    }
                }
                adapter.setUserList(listUser)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun showDetail(user: User){
        val intent = Intent(this, ChatLogActivity::class.java)
        intent.putExtra(USER_KEY, user)
        startActivity(intent)
    }
}
