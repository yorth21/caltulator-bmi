package com.nassican.splashcalculatorapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nassican.splashcalculatorapp.database.AppDatabase
import com.nassican.splashcalculatorapp.database.model.IMCRecord
import com.nassican.splashcalculatorapp.ui.IMCCircle
import com.nassican.splashcalculatorapp.ui.IMCHistoryAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.pow

class MainActivity : AppCompatActivity() {

    private lateinit var editTextWeight: EditText
    private lateinit var editTextHeight: EditText
    private lateinit var buttonCalculate: Button
    private lateinit var textViewResult: TextView
    private lateinit var imgResult: ImageView
    private lateinit var database: AppDatabase
    private lateinit var imcHistoryAdapter: IMCHistoryAdapter
    private var userId: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        userId = intent.getIntExtra("USER_ID", -1)
        if (userId == -1) {
            // Manejar el error: No se proporcionó un ID de usuario válido
            Toast.makeText(this, "Error: No valid user ID", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        database = AppDatabase.getDatabase(this)

        initViews()
        setListeners()
        setupBackButton()
        if (userId != -1) {
            setupRecyclerView()
            loadIMCHistory()
        } else {
            // Handle error - no user ID provided
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.buttonViewHistory).setOnClickListener {
            val intent = Intent(this, IMCHistoryActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }
    }

    private fun initViews() {
        editTextWeight = findViewById(R.id.editTextWeight)
        editTextHeight = findViewById(R.id.editTextHeight)
        buttonCalculate = findViewById(R.id.buttonCalculate)
        textViewResult = findViewById(R.id.textViewResult)
        imgResult = findViewById(R.id.imgResult)
    }

    private fun setupRecyclerView() {
        imcHistoryAdapter = IMCHistoryAdapter(this, mutableListOf())
    }

    private fun loadIMCHistory() {
        CoroutineScope(Dispatchers.IO).launch {
            val records = database.imcRecordDao().getRecordsForUser(userId)
            withContext(Dispatchers.Main) {
                imcHistoryAdapter.updateRecords(records)
            }
        }
    }

    private fun setListeners() {
        buttonCalculate.setOnClickListener {
            calculateBMI()
        }
    }

    private fun setupBackButton() {
        findViewById<Button>(R.id.btn_back).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun calculateBMI() {
        val weightStr = editTextWeight.text.toString()
        val heightStr = editTextHeight.text.toString()

        if (weightStr.isEmpty() || heightStr.isEmpty()) {
            showResult(getString(R.string.error_empty_fields), R.color.unknown)
            return
        }

        try {
            val weight = weightStr.toFloat()
            val height = heightStr.toFloat()

            if (weight <= 0 || height <= 0) {
                showResult(getString(R.string.error_invalid_values), R.color.unknown)
                return
            }

            val bmi = weight / (height.pow(2))
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

            val imcRecord = IMCRecord(
                userId = userId,
                weight = weight,
                height = height,
                bmi = bmi,
                date = currentDate,
                time = currentTime
            )

            CoroutineScope(Dispatchers.IO).launch {
                database.imcRecordDao().insertRecord(imcRecord)
                // Recargar el historial después de insertar el nuevo registro
                loadIMCHistory()
            }

            val bmiCategories = mapOf(
                (0.0..18.4) to Pair(getString(R.string.bmi_underweight), R.drawable.moderate_thinness),
                (18.5..24.99) to Pair(getString(R.string.bmi_normal), R.drawable.normal),
                (25.0..29.99) to Pair(getString(R.string.bmi_overweight), R.drawable.overweight),
                (30.0..34.99) to Pair(getString(R.string.bmi_obesity_1), R.drawable.obese_1),
                (35.0..39.99) to Pair(getString(R.string.bmi_obesity_2), R.drawable.obese_2),
                (40.0..Double.MAX_VALUE) to Pair(getString(R.string.bmi_obesity_3), R.drawable.obese_3)
            )

            val category = bmiCategories.entries.find { bmi in it.key }?.value
                ?: Pair(getString(R.string.unknown_category), R.drawable.normal)

            val result = String.format(getString(R.string.bmi_result), bmi)
            showResult(result, category.second)

        } catch (e: NumberFormatException) {
            showResult(getString(R.string.error_numeric_values), R.color.normal)
        }
    }

    private fun showResult(message: String, imgResource: Int) {
        textViewResult.text = message
        imgResult.setImageResource(imgResource)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}