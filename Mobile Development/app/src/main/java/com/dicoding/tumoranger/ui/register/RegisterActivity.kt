package com.dicoding.tumoranger.ui.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.dicoding.tumoranger.databinding.ActivityRegisterBinding
import com.dicoding.tumoranger.ui.login.LoginActivity
import com.dicoding.tumoranger.ui.login.afterTextChanged

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        registerViewModel.registerFormState.observe(this, Observer {
            val registerState = it ?: return@Observer

            binding.register?.isEnabled = registerState.isDataValid

            if (registerState.usernameError != null) {
                binding.username.error = getString(registerState.usernameError)
            }
            if (registerState.emailError != null) {
                binding.email?.error = getString(registerState.emailError)
            }
            if (registerState.passwordError != null) {
                binding.password.error = getString(registerState.passwordError)
            }
        })

        registerViewModel.registerResult.observe(this) { result ->
            Log.d("RegisterActivity", "Register result: $result")
            binding.loading.visibility = View.GONE
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
            if (result == "Register successful") {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.username.afterTextChanged {
            registerViewModel.registerDataChanged(
                binding.username.text.toString(),
                binding.email?.text.toString(),
                binding.password.text.toString()
            )
        }

        binding.email?.afterTextChanged {
            registerViewModel.registerDataChanged(
                binding.username.text.toString(),
                binding.email?.text.toString(),
                binding.password.text.toString()
            )
        }

        binding.password.afterTextChanged {
            registerViewModel.registerDataChanged(
                binding.username.text.toString(),
                binding.email?.text.toString(),
                binding.password.text.toString()
            )
        }

        binding.register?.setOnClickListener {
            val name = binding.username.text.toString()
            val email = binding.email?.text.toString()
            val password = binding.password.text.toString()
            binding.loading.visibility = View.VISIBLE
            registerViewModel.registerUser(name, email, password)
        }
    }
}