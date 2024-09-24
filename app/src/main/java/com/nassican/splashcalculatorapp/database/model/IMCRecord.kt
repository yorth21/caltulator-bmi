package com.nassican.splashcalculatorapp.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "imc_records",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class IMCRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val weight: Float,
    val height: Float,
    val bmi: Float,
    val date: String,
    val time: String
)
