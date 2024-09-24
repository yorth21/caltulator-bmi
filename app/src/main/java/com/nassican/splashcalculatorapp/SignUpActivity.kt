package com.nassican.splashcalculatorapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.nassican.splashcalculatorapp.database.AppDatabase
import com.nassican.splashcalculatorapp.database.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_sign_up)

        database = AppDatabase.getDatabase(this)

        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)

        setupBackButton()
        setupRegisterButton()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupBackButton() {
        findViewById<TextView>(R.id.tv_back_login).setOnClickListener {
            finish()
        }
    }

    private fun setupRegisterButton() {
        findViewById<Button>(R.id.btn_register).setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateInput(username, password)) {
                registerUser(username, password)
            }
        }
    }

    private fun validateInput(username: String, password: String): Boolean {
        if (username.isEmpty()) {
            showToast(getString(R.string.username_form))
            return false
        }
        if (password.isEmpty()) {
            showToast(getString(R.string.password_form))
            return false
        }
        if (password.length < 6) {
            showToast(getString(R.string.password_limit))
            return false
        }
        return true
    }

    private fun registerUser(username: String, password: String) {
        lifecycleScope.launch {
            try {
                val newUser = User(username = username, password = password)
                withContext(Dispatchers.IO) {
                    database.userDao().insertUser(newUser)
                }
                showToast(getString(R.string.register_succesfull))
                finish()
            } catch (e: Exception) {
                showToast(getString(R.string.register_error))
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}