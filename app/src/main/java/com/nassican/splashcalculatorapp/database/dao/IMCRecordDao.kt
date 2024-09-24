package com.nassican.splashcalculatorapp.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nassican.splashcalculatorapp.database.model.IMCRecord

@Dao
interface IMCRecordDao {
    @Insert
    suspend fun insertRecord(record: IMCRecord)

    @Query("SELECT * FROM imc_records WHERE userId = :userId ORDER BY date DESC, time DESC")
    suspend fun getRecordsForUser(userId: Int): List<IMCRecord>

    @Query("SELECT * FROM imc_records ORDER BY date DESC, time DESC")
    suspend fun getAllRecords(): List<IMCRecord>
}