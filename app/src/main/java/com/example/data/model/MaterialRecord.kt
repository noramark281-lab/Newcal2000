package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "material_records")
data class MaterialRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val storeName: String,
    val invoiceNumber: String,
    val materialType: String,
    val totalAmount: Double,
    val paidAmount: Double,
    val remainingAmount: Double = totalAmount - paidAmount,
    val phoneNumber: String,
    val createdAt: Long = System.currentTimeMillis()
)
