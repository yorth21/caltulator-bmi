package com.nassican.splashcalculatorapp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nassican.splashcalculatorapp.database.AppDatabase
import com.nassican.splashcalculatorapp.ui.IMCHistoryAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IMCHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var imcHistoryAdapter: IMCHistoryAdapter
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imc_history)

        recyclerView = findViewById(R.id.imcHistoryRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        database = AppDatabase.getDatabase(this)

        val userId = intent.getIntExtra("USER_ID", -1)
        setupRecyclerView()
        setupBackButton()
        if (userId != -1) {
            loadIMCHistory(userId)
        } else {
            // Handle error - no user ID provided
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        imcHistoryAdapter = IMCHistoryAdapter(this, mutableListOf())
        recyclerView.adapter = imcHistoryAdapter
    }

    private fun loadIMCHistory(userId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val records = database.imcRecordDao().getRecordsForUser(userId)
            withContext(Dispatchers.Main) {
                imcHistoryAdapter.updateRecords(records)
            }
        }
    }

    private fun setupBackButton() {
        findViewById<Button>(R.id.btn_back).setOnClickListener {
            finish()
        }
    }
}