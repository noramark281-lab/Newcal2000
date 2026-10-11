package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lab_records")
data class LabRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val labName: String,
    val workType: String,
    val teethCount: Int,
    val shade: String,
    val sentDate: String,
    val receivedDate: String,
    val totalAmount: Double,
    val paidAmount: Double,
    val remainingAmount: Double = totalAmount - paidAmount,
    val phoneNumber: String,
    val createdAt: Long = System.currentTimeMillis()
)
