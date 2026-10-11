package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.ClinicAccount
import com.example.data.model.ClinicSettings
import com.example.data.model.LabRecord
import com.example.data.model.MaterialRecord
import com.example.data.model.PatientRecord
import com.example.data.repository.ClinicRepository
import com.example.data.repository.FirestoreRepository
import com.google.firebase.FirebaseApp
import com.example.ui.screens.getFirebaseAuthOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ClinicScreen {
    HOME,
    GENERAL,       // عام (المرضى)
    LAB,           // المعمل
    MATERIALS,     // المواد
    RECONCILIATION,// مصادقة الرصيد
    REPORTS,       // التقارير
    REMINDERS,     // التذكيرات
    ACCOUNTS,      // اضافة حساب وسقف الحساب
    SETTINGS       // الاعدادات واللغة وربط جوجل
}

data class FinancialSummary(
    val totalPatientsIncome: Double = 0.0,
    val totalPatientsReceived: Double = 0.0,
    val totalPatientsRemaining: Double = 0.0,
    val totalLabExpense: Double = 0.0,
    val totalLabPaid: Double = 0.0,
    val totalLabRemaining: Double = 0.0,
    val totalMaterialExpense: Double = 0.0,
    val totalMaterialPaid: Double = 0.0,
    val totalMaterialRemaining: Double = 0.0,
    val netClinicProfit: Double = 0.0
)

class ClinicViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ClinicRepository
    private val firestoreRepo: FirestoreRepository?

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ClinicRepository(db)
        firestoreRepo = if (FirebaseApp.getApps(application).isNotEmpty()) {
            try { FirestoreRepository(application) } catch (_: Exception) { null }
        } else null
    }

    // Active Screen & Navigation
    private val _currentScreen = MutableStateFlow(ClinicScreen.HOME)
    val currentScreen: StateFlow<ClinicScreen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: ClinicScreen) {
        _currentScreen.value = screen
    }

    // Search queries
    val patientSearchQuery = MutableStateFlow("")
    val labSearchQuery = MutableStateFlow("")
    val materialSearchQuery = MutableStateFlow("")

    // Data streams
    val patients: StateFlow<List<PatientRecord>> = patientSearchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) repository.allPatients else repository.searchPatients(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val labs: StateFlow<List<LabRecord>> = labSearchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) repository.allLabs else repository.searchLabs(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val materials: StateFlow<List<MaterialRecord>> = materialSearchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) repository.allMaterials else repository.searchMaterials(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val accounts: StateFlow<List<ClinicAccount>> = repository.allAccounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<ClinicSettings> = repository.settings
        .flatMapLatest { s ->
            val resolved = s?.let {
                if (it.gmailEmail.isBlank()) it.copy(gmailEmail = "aboanasanam42@gmail.com") else it
            } ?: ClinicSettings(gmailEmail = "aboanasanam42@gmail.com")
            MutableStateFlow(resolved)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ClinicSettings(gmailEmail = "aboanasanam42@gmail.com"))

    // Financial Summary Flow
    val financialSummary: StateFlow<FinancialSummary> = combine(
        patients,
        labs,
        materials
    ) { pList, lList, mList ->
        val patTotal = pList.sumOf { it.totalAmount }
        val patPaid = pList.sumOf { it.paidAmount }
        val patRem = pList.sumOf { it.remainingAmount }

        val labTotal = lList.sumOf { it.totalAmount }
        val labPaid = lList.sumOf { it.paidAmount }
        val labRem = lList.sumOf { it.remainingAmount }

        val matTotal = mList.sumOf { it.totalAmount }
        val matPaid = mList.sumOf { it.paidAmount }
        val matRem = mList.sumOf { it.remainingAmount }

        // Net Profit = (Received Cash from Patients) - (Paid Expenses to Labs + Materials)
        val profit = patPaid - (labPaid + matPaid)

        FinancialSummary(
            totalPatientsIncome = patTotal,
            totalPatientsReceived = patPaid,
            totalPatientsRemaining = patRem,
            totalLabExpense = labTotal,
            totalLabPaid = labPaid,
            totalLabRemaining = labRem,
            totalMaterialExpense = matTotal,
            totalMaterialPaid = matPaid,
            totalMaterialRemaining = matRem,
            netClinicProfit = profit
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FinancialSummary())

    // Patient Actions (Room + Firestore)
    fun addPatient(patient: PatientRecord) {
        viewModelScope.launch {
            val generatedId = repository.insertPatient(patient)
            if (getFirebaseAuthOrNull(getApplication())?.currentUser != null) {
                firestoreRepo?.syncPatient(patient.copy(id = generatedId))
            }
        }
    }

    fun updatePatient(patient: PatientRecord) {
        viewModelScope.launch {
            repository.updatePatient(patient)
            if (getFirebaseAuthOrNull(getApplication())?.currentUser != null) {
                firestoreRepo?.syncPatient(patient)
            }
        }
    }

    fun deletePatient(patient: PatientRecord) {
        viewModelScope.launch {
            repository.deletePatient(patient)
            if (getFirebaseAuthOrNull(getApplication())?.currentUser != null) {
                firestoreRepo?.deletePatientFromCloud(patient.id)
            }
        }
    }

    // Lab Actions (Room + Firestore)
    fun addLab(lab: LabRecord) {
        viewModelScope.launch {
            val generatedId = repository.insertLab(lab)
            if (getFirebaseAuthOrNull(getApplication())?.currentUser != null) {
                firestoreRepo?.syncLab(lab.copy(id = generatedId))
            }
        }
    }

    fun updateLab(lab: LabRecord) {
        viewModelScope.launch {
            repository.updateLab(lab)
            if (getFirebaseAuthOrNull(getApplication())?.currentUser != null) {
                firestoreRepo?.syncLab(lab)
            }
        }
    }

    fun deleteLab(lab: LabRecord) {
        viewModelScope.launch {
            repository.deleteLab(lab)
            if (getFirebaseAuthOrNull(getApplication())?.currentUser != null) {
                firestoreRepo?.deleteLabFromCloud(lab.id)
            }
        }
    }

    // Material Actions (Room + Firestore)
    fun addMaterial(material: MaterialRecord) {
        viewModelScope.launch {
            val generatedId = repository.insertMaterial(material)
            if (getFirebaseAuthOrNull(getApplication())?.currentUser != null) {
                firestoreRepo?.syncMaterial(material.copy(id = generatedId))
            }
        }
    }

    fun updateMaterial(material: MaterialRecord) {
        viewModelScope.launch {
            repository.updateMaterial(material)
            if (getFirebaseAuthOrNull(getApplication())?.currentUser != null) {
                firestoreRepo?.syncMaterial(material)
            }
        }
    }

    fun deleteMaterial(material: MaterialRecord) {
        viewModelScope.launch {
            repository.deleteMaterial(material)
            if (getFirebaseAuthOrNull(getApplication())?.currentUser != null) {
                firestoreRepo?.deleteMaterialFromCloud(material.id)
            }
        }
    }

    // Account Actions (Room + Firestore)
    fun addAccount(account: ClinicAccount) {
        viewModelScope.launch {
            val generatedId = repository.insertAccount(account)
            if (getFirebaseAuthOrNull(getApplication())?.currentUser != null) {
                firestoreRepo?.syncAccount(account.copy(id = generatedId))
            }
        }
    }

    fun updateAccount(account: ClinicAccount) {
        viewModelScope.launch {
            repository.updateAccount(account)
            if (getFirebaseAuthOrNull(getApplication())?.currentUser != null) {
                firestoreRepo?.syncAccount(account)
            }
        }
    }

    fun deleteAccount(account: ClinicAccount) {
        viewModelScope.launch {
            repository.deleteAccount(account)
        }
    }

    // Settings (Room + Firestore)
    fun updateSettings(newSettings: ClinicSettings) {
        viewModelScope.launch {
            repository.saveSettings(newSettings)
            if (getFirebaseAuthOrNull(getApplication())?.currentUser != null) {
                firestoreRepo?.syncProfile(newSettings)
            }
        }
    }

    // Sync all existing local records to Firestore
    fun syncAllLocalToCloud() {
        viewModelScope.launch {
            if (getFirebaseAuthOrNull(getApplication())?.currentUser == null || firestoreRepo == null) return@launch
            try {
                val currentPatients = repository.allPatients.first()
                currentPatients.forEach { firestoreRepo?.syncPatient(it) }

                val currentLabs = repository.allLabs.first()
                currentLabs.forEach { firestoreRepo?.syncLab(it) }

                val currentMaterials = repository.allMaterials.first()
                currentMaterials.forEach { firestoreRepo?.syncMaterial(it) }

                val currentAccounts = repository.allAccounts.first()
                currentAccounts.forEach { firestoreRepo?.syncAccount(it) }

                repository.settings.first()?.let { firestoreRepo?.syncProfile(it) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Backup & Restore
    suspend fun exportBackupJson(): String {
        return repository.exportBackupJson()
    }

    suspend fun restoreBackupJson(jsonString: String): Boolean {
        val result = repository.restoreBackupJson(jsonString)
        if (result && getFirebaseAuthOrNull(getApplication())?.currentUser != null) {
            syncAllLocalToCloud()
        }
        return result
    }
}
