package com.example.chatty.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var uid: String = "",
    var username: String = "",
    var email: String = "",
    var profileImage: String = "",
): Parcelable