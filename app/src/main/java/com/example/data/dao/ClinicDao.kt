package com.example.data.dao

import androidx.room.*
import com.example.data.model.ClinicAccount
import com.example.data.model.ClinicSettings
import com.example.data.model.LabRecord
import com.example.data.model.MaterialRecord
import com.example.data.model.PatientRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Query("SELECT * FROM patient_records ORDER BY id DESC")
    fun getAllPatients(): Flow<List<PatientRecord>>

    @Query("SELECT * FROM patient_records WHERE id = :id")
    suspend fun getPatientById(id: Long): PatientRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: PatientRecord): Long

    @Update
    suspend fun updatePatient(patient: PatientRecord)

    @Delete
    suspend fun deletePatient(patient: PatientRecord)

    @Query("SELECT * FROM patient_records WHERE patientName LIKE '%' || :query || '%' OR caseType LIKE '%' || :query || '%' ORDER BY id DESC")
    fun searchPatients(query: String): Flow<List<PatientRecord>>

    @Query("SELECT SUM(totalAmount) FROM patient_records")
    fun getTotalPatientsAmount(): Flow<Double?>

    @Query("SELECT SUM(paidAmount) FROM patient_records")
    fun getTotalPatientsPaid(): Flow<Double?>

    @Query("SELECT SUM(remainingAmount) FROM patient_records")
    fun getTotalPatientsRemaining(): Flow<Double?>

    @Query("DELETE FROM patient_records")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(patients: List<PatientRecord>)
}

@Dao
interface LabDao {
    @Query("SELECT * FROM lab_records ORDER BY id DESC")
    fun getAllLabs(): Flow<List<LabRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLab(lab: LabRecord): Long

    @Update
    suspend fun updateLab(lab: LabRecord)

    @Delete
    suspend fun deleteLab(lab: LabRecord)

    @Query("SELECT * FROM lab_records WHERE labName LIKE '%' || :query || '%' OR workType LIKE '%' || :query || '%' ORDER BY id DESC")
    fun searchLabs(query: String): Flow<List<LabRecord>>

    @Query("SELECT SUM(totalAmount) FROM lab_records")
    fun getTotalLabAmount(): Flow<Double?>

    @Query("SELECT SUM(paidAmount) FROM lab_records")
    fun getTotalLabPaid(): Flow<Double?>

    @Query("SELECT SUM(remainingAmount) FROM lab_records")
    fun getTotalLabRemaining(): Flow<Double?>

    @Query("DELETE FROM lab_records")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(labs: List<LabRecord>)
}

@Dao
interface MaterialDao {
    @Query("SELECT * FROM material_records ORDER BY id DESC")
    fun getAllMaterials(): Flow<List<MaterialRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: MaterialRecord): Long

    @Update
    suspend fun updateMaterial(material: MaterialRecord)

    @Delete
    suspend fun deleteMaterial(material: MaterialRecord)

    @Query("SELECT * FROM material_records WHERE storeName LIKE '%' || :query || '%' OR materialType LIKE '%' || :query || '%' ORDER BY id DESC")
    fun searchMaterials(query: String): Flow<List<MaterialRecord>>

    @Query("SELECT SUM(totalAmount) FROM material_records")
    fun getTotalMaterialAmount(): Flow<Double?>

    @Query("SELECT SUM(paidAmount) FROM material_records")
    fun getTotalMaterialPaid(): Flow<Double?>

    @Query("SELECT SUM(remainingAmount) FROM material_records")
    fun getTotalMaterialRemaining(): Flow<Double?>

    @Query("DELETE FROM material_records")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(materials: List<MaterialRecord>)
}

@Dao
interface AccountDao {
    @Query("SELECT * FROM clinic_accounts ORDER BY id ASC")
    fun getAllAccounts(): Flow<List<ClinicAccount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: ClinicAccount): Long

    @Update
    suspend fun updateAccount(account: ClinicAccount)

    @Delete
    suspend fun deleteAccount(account: ClinicAccount)

    @Query("DELETE FROM clinic_accounts")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(accounts: List<ClinicAccount>)
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM clinic_settings WHERE id = 1")
    fun getSettings(): Flow<ClinicSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: ClinicSettings)
}
