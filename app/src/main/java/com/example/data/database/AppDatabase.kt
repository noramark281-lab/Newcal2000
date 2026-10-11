package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AccountDao
import com.example.data.dao.LabDao
import com.example.data.dao.MaterialDao
import com.example.data.dao.PatientDao
import com.example.data.dao.SettingsDao
import com.example.data.model.ClinicAccount
import com.example.data.model.ClinicSettings
import com.example.data.model.LabRecord
import com.example.data.model.MaterialRecord
import com.example.data.model.PatientRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        PatientRecord::class,
        LabRecord::class,
        MaterialRecord::class,
        ClinicAccount::class,
        ClinicSettings::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun labDao(): LabDao
    abstract fun materialDao(): MaterialDao
    abstract fun accountDao(): AccountDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "alrahman_clinic_database"
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Prepopulate initial settings and default accounts
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getDatabase(context)
                            database.settingsDao().saveSettings(ClinicSettings())
                            database.accountDao().insertAccount(
                                ClinicAccount(
                                    accountName = "الصندوق الرئيسي للعيادة",
                                    accountType = "نقدية / كاش",
                                    balanceLimit = 500000.0,
                                    currentBalance = 0.0
                                )
                            )
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
