package com.company.avishkar

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.company.avishkar.databinding.ActivityOtpBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class OTP : AppCompatActivity() {

    private var KEY: String = "OTP"
    lateinit var binding: ActivityOtpBinding
    lateinit var getMobileNumber: String
    lateinit var getOtpBackend: String
    lateinit var auth: FirebaseAuth
    lateinit var storedVerification: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getMobileNumber = intent.getStringExtra(KEY).toString()
        getOtpBackend = intent.getStringExtra(KEY).toString()

        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        binding.otpEdittextOne.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0?.length == 1) {
                    binding.otpEdittextTwo.requestFocus()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
        binding.otpEdittextTwo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0?.length == 1) {
                    binding.otpEdittextThree.requestFocus()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
        binding.otpEdittextThree.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0?.length == 1) {
                    binding.otpEdittextFour.requestFocus()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
        binding.otpEdittextFour.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0?.length == 1) {
                    binding.otpEdittextFive.requestFocus()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
        binding.otpEdittextFive.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0?.length == 1) {
                    binding.otpEdittextSix.requestFocus()
                }
            }
        })

        binding.otpSubmit.setOnClickListener {
            if (binding.otpEdittextOne.text.toString()
                    .isEmpty() && binding.otpEdittextTwo.text.toString()
                    .isEmpty() && binding.otpEdittextThree.text.toString()
                    .isEmpty() && binding.otpEdittextFour.text.toString()
                    .isEmpty() && binding.otpEdittextFive.text.toString()
                    .isEmpty() && binding.otpEdittextSix.text.toString().isEmpty()
            ) {
                Toast.makeText(this, R.string.otp_toast_fillall, Toast.LENGTH_SHORT).show()
            } else {
                var enteredOtp = binding.otpEdittextOne.text.toString() +
                        binding.otpEdittextTwo.text.toString() +
                        binding.otpEdittextThree.text.toString() +
                        binding.otpEdittextFour.text.toString() +
                        binding.otpEdittextFive.text.toString() +
                        binding.otpEdittextSix.text.toString()

                if (getOtpBackend != null) {
                    binding.otpProgbar.visibility = View.VISIBLE
                    binding.otpSubmit.visibility = View.INVISIBLE

                    var phoneAuthCredential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                        getOtpBackend, enteredOtp
                    )
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                        .addOnCompleteListener(OnCompleteListener {
                            binding.otpProgbar.visibility = View.GONE
                            binding.otpSubmit.visibility = View.VISIBLE
                            if (it.isSuccessful) {
                                val intent = Intent(this, HomePage::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this,
                                    R.string.otp_toast_incorrect,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                } else {
                    Toast.makeText(this, R.string.otp_toast_error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.otpResendTextView.setOnClickListener {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber("+91" + getMobileNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)

            callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {

                }

                override fun onVerificationFailed(p0: FirebaseException) {

                    Toast.makeText(this@OTP, p0.message, Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(
                    newBackendOTP : String,
                    p1: PhoneAuthProvider.ForceResendingToken
                ) {
                    getOtpBackend = newBackendOTP
                    Toast.makeText(this@OTP , R.string.otp_toast_resend_succ , Toast.LENGTH_SHORT).show()
                }

            }
        }

    }
}