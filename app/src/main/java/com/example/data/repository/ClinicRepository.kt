package com.example.data.repository

import com.example.data.database.AppDatabase
import com.example.data.model.ClinicAccount
import com.example.data.model.ClinicSettings
import com.example.data.model.LabRecord
import com.example.data.model.MaterialRecord
import com.example.data.model.PatientRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject

class ClinicRepository(private val database: AppDatabase) {

    // Patients
    val allPatients: Flow<List<PatientRecord>> = database.patientDao().getAllPatients()
    val totalPatientsAmount: Flow<Double?> = database.patientDao().getTotalPatientsAmount()
    val totalPatientsPaid: Flow<Double?> = database.patientDao().getTotalPatientsPaid()
    val totalPatientsRemaining: Flow<Double?> = database.patientDao().getTotalPatientsRemaining()

    fun searchPatients(query: String): Flow<List<PatientRecord>> =
        database.patientDao().searchPatients(query)

    suspend fun insertPatient(patient: PatientRecord): Long =
        database.patientDao().insertPatient(patient)

    suspend fun updatePatient(patient: PatientRecord) =
        database.patientDao().updatePatient(patient)

    suspend fun deletePatient(patient: PatientRecord) =
        database.patientDao().deletePatient(patient)

    // Labs
    val allLabs: Flow<List<LabRecord>> = database.labDao().getAllLabs()
    val totalLabAmount: Flow<Double?> = database.labDao().getTotalLabAmount()
    val totalLabPaid: Flow<Double?> = database.labDao().getTotalLabPaid()
    val totalLabRemaining: Flow<Double?> = database.labDao().getTotalLabRemaining()

    fun searchLabs(query: String): Flow<List<LabRecord>> =
        database.labDao().searchLabs(query)

    suspend fun insertLab(lab: LabRecord): Long =
        database.labDao().insertLab(lab)

    suspend fun updateLab(lab: LabRecord) =
        database.labDao().updateLab(lab)

    suspend fun deleteLab(lab: LabRecord) =
        database.labDao().deleteLab(lab)

    // Materials
    val allMaterials: Flow<List<MaterialRecord>> = database.materialDao().getAllMaterials()
    val totalMaterialAmount: Flow<Double?> = database.materialDao().getTotalMaterialAmount()
    val totalMaterialPaid: Flow<Double?> = database.materialDao().getTotalMaterialPaid()
    val totalMaterialRemaining: Flow<Double?> = database.materialDao().getTotalMaterialRemaining()

    fun searchMaterials(query: String): Flow<List<MaterialRecord>> =
        database.materialDao().searchMaterials(query)

    suspend fun insertMaterial(material: MaterialRecord): Long =
        database.materialDao().insertMaterial(material)

    suspend fun updateMaterial(material: MaterialRecord) =
        database.materialDao().updateMaterial(material)

    suspend fun deleteMaterial(material: MaterialRecord) =
        database.materialDao().deleteMaterial(material)

    // Accounts
    val allAccounts: Flow<List<ClinicAccount>> = database.accountDao().getAllAccounts()

    suspend fun insertAccount(account: ClinicAccount): Long =
        database.accountDao().insertAccount(account)

    suspend fun updateAccount(account: ClinicAccount) =
        database.accountDao().updateAccount(account)

    suspend fun deleteAccount(account: ClinicAccount) =
        database.accountDao().deleteAccount(account)

    // Settings
    val settings: Flow<ClinicSettings?> = database.settingsDao().getSettings()

    suspend fun saveSettings(settings: ClinicSettings) =
        database.settingsDao().saveSettings(settings)

    // Backup & Restore
    suspend fun exportBackupJson(): String {
        val root = JSONObject()
        val patientsList = allPatients.first()
        val labsList = allLabs.first()
        val materialsList = allMaterials.first()
        val accountsList = allAccounts.first()

        val patientsArray = JSONArray()
        patientsList.forEach { p ->
            val obj = JSONObject()
            obj.put("id", p.id)
            obj.put("patientName", p.patientName)
            obj.put("caseType", p.caseType)
            obj.put("totalAmount", p.totalAmount)
            obj.put("paidAmount", p.paidAmount)
            obj.put("remainingAmount", p.remainingAmount)
            obj.put("dateAndDay", p.dateAndDay)
            obj.put("diagnosisAndTreatment", p.diagnosisAndTreatment)
            obj.put("phoneNumber", p.phoneNumber)
            patientsArray.put(obj)
        }
        root.put("patients", patientsArray)

        val labsArray = JSONArray()
        labsList.forEach { l ->
            val obj = JSONObject()
            obj.put("id", l.id)
            obj.put("labName", l.labName)
            obj.put("workType", l.workType)
            obj.put("teethCount", l.teethCount)
            obj.put("shade", l.shade)
            obj.put("sentDate", l.sentDate)
            obj.put("receivedDate", l.receivedDate)
            obj.put("totalAmount", l.totalAmount)
            obj.put("paidAmount", l.paidAmount)
            obj.put("remainingAmount", l.remainingAmount)
            obj.put("phoneNumber", l.phoneNumber)
            labsArray.put(obj)
        }
        root.put("labs", labsArray)

        val materialsArray = JSONArray()
        materialsList.forEach { m ->
            val obj = JSONObject()
            obj.put("id", m.id)
            obj.put("storeName", m.storeName)
            obj.put("invoiceNumber", m.invoiceNumber)
            obj.put("materialType", m.materialType)
            obj.put("totalAmount", m.totalAmount)
            obj.put("paidAmount", m.paidAmount)
            obj.put("remainingAmount", m.remainingAmount)
            obj.put("phoneNumber", m.phoneNumber)
            materialsArray.put(obj)
        }
        root.put("materials", materialsArray)

        val accountsArray = JSONArray()
        accountsList.forEach { a ->
            val obj = JSONObject()
            obj.put("id", a.id)
            obj.put("accountName", a.accountName)
            obj.put("accountType", a.accountType)
            obj.put("balanceLimit", a.balanceLimit)
            obj.put("currentBalance", a.currentBalance)
            accountsArray.put(obj)
        }
        root.put("accounts", accountsArray)

        return root.toString(2)
    }

    suspend fun restoreBackupJson(jsonString: String): Boolean {
        return try {
            val root = JSONObject(jsonString)

            if (root.has("patients")) {
                val array = root.getJSONArray("patients")
                val list = mutableListOf<PatientRecord>()
                for (i in 0 until array.length()) {
                    val o = array.getJSONObject(i)
                    list.add(
                        PatientRecord(
                            id = o.optLong("id", 0),
                            patientName = o.getString("patientName"),
                            caseType = o.optString("caseType", ""),
                            totalAmount = o.optDouble("totalAmount", 0.0),
                            paidAmount = o.optDouble("paidAmount", 0.0),
                            remainingAmount = o.optDouble("remainingAmount", 0.0),
                            dateAndDay = o.optString("dateAndDay", ""),
                            diagnosisAndTreatment = o.optString("diagnosisAndTreatment", ""),
                            phoneNumber = o.optString("phoneNumber", "")
                        )
                    )
                }
                database.patientDao().clearAll()
                database.patientDao().insertAll(list)
            }

            if (root.has("labs")) {
                val array = root.getJSONArray("labs")
                val list = mutableListOf<LabRecord>()
                for (i in 0 until array.length()) {
                    val o = array.getJSONObject(i)
                    list.add(
                        LabRecord(
                            id = o.optLong("id", 0),
                            labName = o.getString("labName"),
                            workType = o.optString("workType", ""),
                            teethCount = o.optInt("teethCount", 1),
                            shade = o.optString("shade", ""),
                            sentDate = o.optString("sentDate", ""),
                            receivedDate = o.optString("receivedDate", ""),
                            totalAmount = o.optDouble("totalAmount", 0.0),
                            paidAmount = o.optDouble("paidAmount", 0.0),
                            remainingAmount = o.optDouble("remainingAmount", 0.0),
                            phoneNumber = o.optString("phoneNumber", "")
                        )
                    )
                }
                database.labDao().clearAll()
                database.labDao().insertAll(list)
            }

            if (root.has("materials")) {
                val array = root.getJSONArray("materials")
                val list = mutableListOf<MaterialRecord>()
                for (i in 0 until array.length()) {
                    val o = array.getJSONObject(i)
                    list.add(
                        MaterialRecord(
                            id = o.optLong("id", 0),
                            storeName = o.getString("storeName"),
                            invoiceNumber = o.optString("invoiceNumber", ""),
                            materialType = o.optString("materialType", ""),
                            totalAmount = o.optDouble("totalAmount", 0.0),
                            paidAmount = o.optDouble("paidAmount", 0.0),
                            remainingAmount = o.optDouble("remainingAmount", 0.0),
                            phoneNumber = o.optString("phoneNumber", "")
                        )
                    )
                }
                database.materialDao().clearAll()
                database.materialDao().insertAll(list)
            }

            if (root.has("accounts")) {
                val array = root.getJSONArray("accounts")
                val list = mutableListOf<ClinicAccount>()
                for (i in 0 until array.length()) {
                    val o = array.getJSONObject(i)
                    list.add(
                        ClinicAccount(
                            id = o.optLong("id", 0),
                            accountName = o.getString("accountName"),
                            accountType = o.optString("accountType", ""),
                            balanceLimit = o.optDouble("balanceLimit", 0.0),
                            currentBalance = o.optDouble("currentBalance", 0.0)
                        )
                    )
                }
                database.accountDao().clearAll()
                database.accountDao().insertAll(list)
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
