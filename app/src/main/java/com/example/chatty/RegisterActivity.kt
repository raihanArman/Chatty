package com.example.chatty

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.example.chatty.databinding.ActivityRegisterBinding
import com.example.chatty.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_register)

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
        val email = binding.etEmail.text.toString().trim()
        val name = binding.etNama.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        Log.d("Register", "signUpProcess: ${email}")

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    uploadImageToFirebaseStorage()
                }else{
                    Log.d("Register", "signUpProcess: Gagal")
                }
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
            .addOnFailureListener {
                Log.d("Register", "uploadImageToFirebaseStorage: ${it.message}")
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
                val intent = Intent(this, LatestMessageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("Register", "saveToFirebaseDatabase: ${it.message}")
            }
    }
}