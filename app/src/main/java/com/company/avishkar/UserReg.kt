package com.company.avishkar

import com.company.avishkar.User
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.company.avishkar.databinding.ActivityUserRegBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.company.avishkar.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class UserReg : AppCompatActivity() {

    private val KEY : String = "OTP"
    lateinit var binding: ActivityUserRegBinding
    private lateinit var database : DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var storedVerification: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserRegBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        binding.userRegOtpBtn.setOnClickListener {
            if (binding.userRegFirstName.text.isEmpty()) {
                Toast.makeText(this, R.string.reg_user_toast_fname, Toast.LENGTH_SHORT).show()
            } else if (binding.userRegLastName.text.isEmpty()) {
                Toast.makeText(this, R.string.reg_user_toast_lname, Toast.LENGTH_SHORT).show()
            } else if (binding.userRegPhoneNumber.length() != 10) {
                Toast.makeText(this, R.string.reg_user_toast_phone, Toast.LENGTH_SHORT).show()
            } else {
                binding.userRegProgbar.visibility = View.VISIBLE
                binding.userRegOtpBtn.visibility = View.INVISIBLE

                val firstName = binding.userRegFirstName.text.toString()
                val lastName = binding.userRegLastName.text.toString()
                //val age = binding.age.text.toString()
                val userPhone = binding.userRegPhoneNumber.text.toString()

                database = FirebaseDatabase.getInstance().getReference("Users")
                val User = User(firstName,lastName,userPhone)
                database.child(userPhone).setValue(User).addOnSuccessListener {

                    binding.userRegFirstName.text.clear()
                    binding.userRegLastName.text.clear()
                    //binding.age.text.clear()
                    binding.userRegPhoneNumber.text.clear()

                    Toast.makeText(this,"Successfully Saved",Toast.LENGTH_SHORT).show()

                }.addOnFailureListener{

                    Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
                }


                callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                        binding.userRegProgbar.visibility = View.GONE
                        binding.userRegOtpBtn.visibility = View.VISIBLE
                    }

                    override fun onVerificationFailed(p0: FirebaseException) {
                        binding.userRegProgbar.visibility = View.GONE
                        binding.userRegOtpBtn.visibility = View.VISIBLE
                        Toast.makeText(this@UserReg, p0.message, Toast.LENGTH_SHORT).show()
                    }

                    override fun onCodeSent(backendOtp: String, p1: PhoneAuthProvider.ForceResendingToken) {
                        binding.userRegProgbar.visibility = View.GONE
                        binding.userRegOtpBtn.visibility = View.VISIBLE
                        val intent = Intent(this@UserReg, OTP::class.java)
                        intent.putExtra(KEY , binding.userRegPhoneNumber.toString())
                        intent.putExtra(KEY,backendOtp)
                        startActivity(intent)
                    }

                }

                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber("+91" + binding.userRegPhoneNumber.text.toString())       // Phone number to verify
                    .setTimeout(60, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(this)                 // Activity (for callback binding)
                    .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)

            }
        }
    }
}