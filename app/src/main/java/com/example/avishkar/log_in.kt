package com.example.avishkar

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

import java.util.*
import java.util.concurrent.TimeUnit


class log_in : AppCompatActivity() {
    private var editTextMobile: EditText? = null
    private var editTextPassword: EditText? = null
    private var signInButton: Button = null
    private var mCallBacks: OnVerificationStateChangedCallbacks? = null
    private var mVerificationId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)
        FirebaseApp.initializeApp(this)
        editTextMobile = findViewById<EditText>(R.id.editTextMobile)
        //editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        signInButton = findViewById<Button>(R.id.SignInButton)
        signInButton.setOnClickListener(View.OnClickListener {
            if (mVerificationId != null) {
                verifyPhoneNumberWithCode()
            } else {
                startPhoneNumberVerification()
                Toast.makeText(
                    this@RegisterActivity,
                    "Hold on tight!\nSending OTP",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        mCallBacks = object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                signInButton.setEnabled(true)
                Toast.makeText(this@RegisterActivity, "verification failed", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                Toast.makeText(this@RegisterActivity, "Code sent", Toast.LENGTH_SHORT).show()
                mVerificationId = s
                signInButton.setEnabled(true)
                signInButton.setText("Verify Code")
            }
        }
    }
    private fun verifyPhoneNumberWithCode() {
        if (editTextPassword!!.text == null) return
        if (editTextPassword!!.text.toString().trim { it <= ' ' } == "") return
        signInButton.setEnabled(false)
        val credential =
            PhoneAuthProvider.getCredential(mVerificationId!!, editTextPassword!!.text.toString())
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(phoneAuthCredential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(
            this
        ) { task ->
            if (task.isSuccessful) userIsLoggedIn() else Toast.makeText(
                this@RegisterActivity,
                "Oops! Something went wrong",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun userIsLoggedIn() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            storeDataInDatabase()
        } else {
            Toast.makeText(this@RegisterActivity, "Oops! Something went wrong", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun storeDataInDatabase() {
        saveMessagingToken()
        val intent = intent
        val name = intent.getStringExtra("NAME")
        val age = intent.getStringExtra("AGE")
        val gender = intent.getStringExtra("GENDER")
        val sport = intent.getStringExtra("SPORT")
        //String recovered = intent.getStringExtra("RECOVERED");
        val user: User
        user = User(name, age, gender, sport /*, recovered*/)
        user.setUserId(FirebaseAuth.getInstance().uid)
        store(user)
    }

    private fun store(user: User) {
        val sharedPreferences = getSharedPreferences("MY_INFO", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("SPORT", user.getUserSport())
        editor.apply()
        val map: MutableMap<String, Any> = HashMap()
        map["userAge"] = user.getUserAge()
        map["userSport"] = user.getUserSport()
        //map.put("userRecovered", user.getUserRecovered());
        map["userName"] = user.getUserName()
        map["userId"] = user.getUserId()
        map["userGender"] = user.getUserGender()
        FirebaseDatabase.getInstance()
            .reference
            .child("sports")
            .child(user.getUserSport())
            .setValue(true)
        /*FirebaseDatabase.getInstance()
                .getReference()
                .child("diseases")
                .child(user.getUserRecovered())
                .setValue(true);*/FirebaseDatabase.getInstance()
            .reference
            .child("user")
            .child(Objects.requireNonNull(FirebaseAuth.getInstance().uid))
            .updateChildren(map)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this@RegisterActivity, HomeActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Oops! Something went wrong",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
    }
    private fun saveMessagingToken() {
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(OnCompleteListener<String?> { task ->
                if (task.isSuccessful) {
                    PushNotification.saveToken(task.result)
                }
            })
    }
    private fun startPhoneNumberVerification() {
        if (editTextMobile!!.text == null) return
        if (editTextMobile!!.text.toString().trim { it <= ' ' } == "") return
       signInButton.setEnabled(false)
        val options = PhoneAuthOptions.newBuilder()
            .setPhoneNumber(editTextMobile!!.text.toString().trim { it <= ' ' })
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallBacks!!)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    fun onLoginClick(view: View?) {
        startActivity(Intent(this, InfoActivity::class.java))
        finish()
        //overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right)
    }

}