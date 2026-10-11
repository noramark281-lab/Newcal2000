package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.ClinicAccount
import com.example.data.model.ClinicSettings
import com.example.data.model.LabRecord
import com.example.data.model.MaterialRecord
import com.example.data.model.PatientRecord
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    // Sync Patient to Firestore
    suspend fun syncPatient(patient: PatientRecord) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
            .collection("patients").document(patient.id.toString())
        val data = hashMapOf(
            "id" to patient.id.toString(),
            "patientName" to patient.patientName,
            "caseType" to patient.caseType,
            "totalAmount" to patient.totalAmount,
            "paidAmount" to patient.paidAmount,
            "remainingAmount" to patient.remainingAmount,
            "dateAndDay" to patient.dateAndDay,
            "diagnosisAndTreatment" to patient.diagnosisAndTreatment,
            "phoneNumber" to patient.phoneNumber,
            "userId" to uid,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(data, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "Error syncing patient: ${e.message}")
        }
    }

    suspend fun deletePatientFromCloud(patientId: Long) {
        val uid = requireUserId()
        try {
            db.collection("users").document(uid)
                .collection("patients").document(patientId.toString()).delete().await()
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "Error deleting patient from cloud: ${e.message}")
        }
    }

    // Sync Lab to Firestore
    suspend fun syncLab(lab: LabRecord) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
            .collection("labs").document(lab.id.toString())
        val data = hashMapOf(
            "id" to lab.id.toString(),
            "labName" to lab.labName,
            "workType" to lab.workType,
            "teethCount" to lab.teethCount,
            "shade" to lab.shade,
            "sentDate" to lab.sentDate,
            "receivedDate" to lab.receivedDate,
            "totalAmount" to lab.totalAmount,
            "paidAmount" to lab.paidAmount,
            "remainingAmount" to lab.remainingAmount,
            "phoneNumber" to lab.phoneNumber,
            "userId" to uid,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(data, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "Error syncing lab: ${e.message}")
        }
    }

    suspend fun deleteLabFromCloud(labId: Long) {
        val uid = requireUserId()
        try {
            db.collection("users").document(uid)
                .collection("labs").document(labId.toString()).delete().await()
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "Error deleting lab from cloud: ${e.message}")
        }
    }

    // Sync Material to Firestore
    suspend fun syncMaterial(material: MaterialRecord) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
            .collection("materials").document(material.id.toString())
        val data = hashMapOf(
            "id" to material.id.toString(),
            "storeName" to material.storeName,
            "invoiceNumber" to material.invoiceNumber,
            "materialType" to material.materialType,
            "totalAmount" to material.totalAmount,
            "paidAmount" to material.paidAmount,
            "remainingAmount" to material.remainingAmount,
            "phoneNumber" to material.phoneNumber,
            "userId" to uid,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        try {
            docRef.set(data, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "Error syncing material: ${e.message}")
        }
    }

    suspend fun deleteMaterialFromCloud(materialId: Long) {
        val uid = requireUserId()
        try {
            db.collection("users").document(uid)
                .collection("materials").document(materialId.toString()).delete().await()
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "Error deleting material from cloud: ${e.message}")
        }
    }

    // Sync Account
    suspend fun syncAccount(account: ClinicAccount) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
            .collection("accounts").document(account.id.toString())
        val data = hashMapOf(
            "id" to account.id.toString(),
            "accountName" to account.accountName,
            "accountType" to account.accountType,
            "balanceLimit" to account.balanceLimit,
            "currentBalance" to account.currentBalance,
            "userId" to uid
        )
        try {
            docRef.set(data, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "Error syncing account: ${e.message}")
        }
    }

    // Sync Profile/Settings
    suspend fun syncProfile(settings: ClinicSettings) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
            .collection("profile").document("settings")
        val data = hashMapOf(
            "clinicName" to settings.clinicName,
            "doctorName" to settings.doctorName,
            "doctorPhone" to settings.doctorPhone,
            "currency" to settings.currency,
            "gmailEmail" to settings.gmailEmail,
            "userId" to uid
        )
        try {
            docRef.set(data, SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.e("FirestoreRepo", "Error syncing profile: ${e.message}")
        }
    }
}
