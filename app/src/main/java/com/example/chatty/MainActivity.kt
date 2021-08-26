package com.example.chatty

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.example.chatty.databinding.ActivityMainBinding
import com.example.chatty.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        binding.tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignUp.setOnClickListener {
            signUpProcess()
        }

        val getImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                selectedPhotoUri = it
                binding.imageView.setImageURI(it)
            }
        )
        binding.imageView.setOnClickListener {
            getImage.launch("image/*")
        }

    }

    private fun signUpProcess() {
        val email = binding.etEmail.text.toString()
        val name = binding.etNama.text.toString()
        val password = binding.etPassword.text.toString()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(it.isSuccessful) return@addOnCompleteListener

                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener {
                Log.d("Register", "signUpProcess: ${it.message}")
            }
    }

    private fun uploadImageToFirebaseStorage() {
        if(selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("Register", "uploadImageToFirebaseStorage: ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    saveToFirebaseDatabase(it.toString())
                }
            }
    }

    private fun saveToFirebaseDatabase(profileImage: String) {
        val email = binding.etEmail.text.toString()
        val name = binding.etNama.text.toString()
        val password = binding.etPassword.text.toString()
        val uid =FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(
            uid!!,
            name,
            email,
            profileImage
        )
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("Register", "saveToFirebaseDatabase: Success")
            }
    }
}