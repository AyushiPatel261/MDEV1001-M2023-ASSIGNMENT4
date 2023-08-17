package com.example.assignment4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var editTextEmail: EditText
    lateinit var editTextPassword: EditText
    lateinit var btnLogin: Button
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editTextEmail = findViewById(R.id.usernameEditText)
        editTextPassword = findViewById(R.id.passwordEditText)
        btnLogin = findViewById(R.id.loginButton)
        btnRegister = findViewById(R.id.registerButton)
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        firebaseAuth = FirebaseAuth.getInstance()
        auth = Firebase.auth
        btnLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val pass = editTextPassword.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, MovieListActivity::class.java)
                        startActivity(intent)

                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null) {
            val intent = Intent(this, MovieListActivity::class.java)
            startActivity(intent)
        }
    }
}