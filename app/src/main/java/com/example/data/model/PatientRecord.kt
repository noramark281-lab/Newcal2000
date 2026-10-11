package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patient_records")
data class PatientRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientName: String,
    val caseType: String,
    val totalAmount: Double,
    val paidAmount: Double,
    val remainingAmount: Double = totalAmount - paidAmount,
    val dateAndDay: String,
    val diagnosisAndTreatment: String,
    val phoneNumber: String,
    val createdAt: Long = System.currentTimeMillis()
)
