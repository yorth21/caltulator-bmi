package com.nassican.splashcalculatorapp


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nassican.splashcalculatorapp.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var editTextUser: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonAboutMe: Button
    private lateinit var tvGoRegister: TextView

    private lateinit var database: AppDatabase

    private val userDefault = "jesus"
    private val passDefault = "12345"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instalar la pantalla de splash
        installSplashScreen()

        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        database = AppDatabase.getDatabase(this)

        editTextUser = findViewById(R.id.et_user)
        editTextPassword = findViewById(R.id.et_password)
        buttonLogin = findViewById(R.id.btn_login)
        buttonAboutMe = findViewById(R.id.btn_about_me)

        buttonLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            initLogin(intent)
        }

        buttonAboutMe.setOnClickListener {
            val intent = Intent(this, Aboutme::class.java)
            startActivity(intent)
        }

        tvGoRegister = findViewById(R.id.tv_register)
        tvGoRegister.setOnClickListener {
            // Navega a RegisterActivity
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        val mainView = findViewById<View>(R.id.main)
        mainView.viewTreeObserver.addOnGlobalLayoutListener {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }

    private fun initLogin(intent: Intent) {
        val username = findViewById<EditText>(R.id.et_user).text.toString()
        val password = findViewById<EditText>(R.id.et_password).text.toString()

        CoroutineScope(Dispatchers.Main).launch {
            val user = withContext(Dispatchers.IO) {
                database.userDao().login(username, password)
            }
            if (user != null) {
                Toast.makeText(this@LoginActivity, getString(R.string.login_successful), Toast.LENGTH_SHORT).show()
                intent.putExtra("USER_ID", user.id)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@LoginActivity, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }
}