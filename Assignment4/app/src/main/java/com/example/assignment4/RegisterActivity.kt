package com.example.assignment4

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    lateinit var editTextFirstName: EditText
    lateinit var editTextLastName: EditText
    lateinit var editTextSignUpEmail: EditText
    lateinit var editTextSignUpPassword: EditText
    lateinit var editTextUsername: EditText
    lateinit var editTextConfirmPass: EditText
    lateinit var btnSignUp: Button
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        editTextFirstName = findViewById(R.id.editTextFirstname)
        editTextLastName = findViewById(R.id.editTextLastname)
        editTextSignUpEmail = findViewById(R.id.editTextEmail)
        editTextUsername = findViewById(R.id.editTextUsername)
        editTextSignUpPassword = findViewById(R.id.editTextPassword)
        editTextConfirmPass = findViewById(R.id.editTextConfirmPassword)
        btnSignUp = findViewById(R.id.btnRegister)
        firebaseAuth = FirebaseAuth.getInstance()
        btnSignUp.setOnClickListener {
            val firstname = editTextFirstName.text.toString()
            val lastname = editTextLastName.text.toString()
            val email = editTextSignUpEmail.text.toString()
            val pass = editTextSignUpPassword.text.toString()
            if (email.isNotEmpty() && pass.isNotEmpty() && firstname.isNotEmpty() && lastname.isNotEmpty()) {
                firebaseAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d(ContentValues.TAG, "createUserWithEmail:success")
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
            }
        }
    }
}