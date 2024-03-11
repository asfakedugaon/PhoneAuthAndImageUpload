package com.example.phoneauthandimageupload

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import com.squareup.picasso.Picasso
import java.util.UUID
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    lateinit var enterPhoneNo: EditText
//    lateinit var enterOtp: EditText
    lateinit var sentOtpBtn: AppCompatButton
//    lateinit var verifyOtpBtn: AppCompatButton
    lateinit var addImage: ShapeableImageView

    val db = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private var verificationId = ""
    val child = "image/${UUID.randomUUID()}.png"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        enterPhoneNo = findViewById(R.id.enterPhoneNo_editText)
//        enterOtp = findViewById(R.id.enterOtp_editText)
        sentOtpBtn = findViewById(R.id.sentOtp_button)
//        verifyOtpBtn = findViewById(R.id.verify_button)
        addImage = findViewById(R.id.addImage_imageView)

        auth = FirebaseAuth.getInstance()

        sentOtpBtn.setOnClickListener {
            otpSent()
        }
//        verifyOtpBtn.setOnClickListener {
//            verifyOtp()
//        }

        addImage.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_PICK
            intent.type = "image/*"
            imageLauncher.launch(intent)
        }
    }

    fun otpSent() {
        val phoneAuth = PhoneAuthOptions.newBuilder()
            .setPhoneNumber("+91${enterPhoneNo.text}")
            .setActivity(this)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    Toast.makeText(
                        this@MainActivity,
                        "Verification Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    Toast.makeText(this@MainActivity, "Verification failed", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onCodeSent(
                    verificationId: String,
                    p1: PhoneAuthProvider.ForceResendingToken
                ) {
                    super.onCodeSent(verificationId, p1)
                    this@MainActivity.verificationId = verificationId
                    Toast.makeText(this@MainActivity, "Otp is sent", Toast.LENGTH_SHORT).show()

                }
            }).build()
        PhoneAuthProvider.verifyPhoneNumber(phoneAuth)
        startActivity(Intent(this,VerifyOtp::class.java))
    }

//    fun verifyOtp() {
//        val verifyOtp = enterOtp.text.toString()
//        val userPhoneCredential = PhoneAuthProvider.getCredential(verificationId, verifyOtp)
//
//        auth.signInWithCredential(userPhoneCredential)
//            .addOnSuccessListener {
//                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
//
//            }.addOnFailureListener {
//                Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
//            }
//        finish()
//    }

    val imageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (it.data != null) {
                    val ref = Firebase.storage.reference.child(child)

                    ref.putFile(it.data!!.data!!).addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener { uri ->

                            val imageUrl = uri.toString()
                            Toast.makeText(this@MainActivity, "Photo uploaded", Toast.LENGTH_LONG)
                                .show()
                            Picasso.get().load(imageUrl).into(addImage).apply {
                                intent.putExtra("imageUri", imageUrl)
                            }
                        }
                    }
                }
            }
        }
}