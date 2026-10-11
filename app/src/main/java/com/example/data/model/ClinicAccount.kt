package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clinic_accounts")
data class ClinicAccount(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val accountName: String,
    val accountType: String,
    val balanceLimit: Double,
    val currentBalance: Double
)

@Entity(tableName = "clinic_settings")
data class ClinicSettings(
    @PrimaryKey
    val id: Int = 1,
    val clinicName: String = "ALRAHMAN CLINICAL",
    val doctorName: String = "الدكتور : مالك الرميمة",
    val doctorPhone: String = "771134103",
    val currency: String = "ريال",
    val gmailEmail: String = "aboanasanam42@gmail.com",
    val whatsappDefaultTemplate: String = "تحية طيبة من عيادة الرحمن لطب وجراحة الفم والاسنان - د. مالك الرميمة (771134103)."
)
