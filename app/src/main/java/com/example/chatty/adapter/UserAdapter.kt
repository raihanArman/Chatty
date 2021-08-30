package com.example.chatty.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chatty.R
import com.example.chatty.databinding.UserRowNewMessageBinding
import com.example.chatty.model.User
import com.squareup.picasso.Picasso

class UserAdapter(val showDetail : (User) -> Unit ): RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    val userList = ArrayList<User>()
    fun setUserList(listUser: List<User>){
        this.userList.clear()
        this.userList.addAll(listUser)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: UserRowNewMessageBinding = DataBindingUtil.inflate(inflater, R.layout.user_row_new_message, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int = userList.size

    inner class ViewHolder(val binding:UserRowNewMessageBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(user: User){
            with(binding){
                tvUsername.text = user.username
                Picasso.get().load(user.profileImage)
                    .into(ivUser)

                itemView.setOnClickListener {
                    showDetail(user)
                }
            }
        }
    }
}