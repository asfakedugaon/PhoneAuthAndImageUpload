package com.example.phoneauthandimageupload

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider

class VerifyOtp : AppCompatActivity() {

    lateinit var enterOtp: EditText
    lateinit var verifyOtpBtn: AppCompatButton

    private lateinit var auth: FirebaseAuth
    private var verificationId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_otp)

        enterOtp = findViewById(R.id.enterOtp_editText)
        verifyOtpBtn = findViewById(R.id.verify_button)

        verifyOtpBtn.setOnClickListener {
            verifyOtp()
        }
    }

    fun verifyOtp() {
        val verifyOtp = enterOtp.text.toString()
        val userPhoneCredential = PhoneAuthProvider.getCredential(verificationId, verifyOtp)

        auth.signInWithCredential(userPhoneCredential)
            .addOnSuccessListener {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

            }.addOnFailureListener {
                Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
            }
    }
}