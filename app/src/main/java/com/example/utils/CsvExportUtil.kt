package com.example.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.model.LabRecord
import com.example.data.model.MaterialRecord
import com.example.data.model.PatientRecord
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

object CsvExportUtil {

    private fun escapeCsv(value: String): String {
        var str = value.replace("\"", "\"\"")
        if (str.contains(",") || str.contains("\n") || str.contains("\"")) {
            str = "\"$str\""
        }
        return str
    }

    fun exportPatientsToCsv(context: Context, patients: List<PatientRecord>): Uri? {
        return try {
            val file = File(context.cacheDir, "alrahman_patients_${System.currentTimeMillis()}.csv")
            FileOutputStream(file).use { fos ->
                // Write UTF-8 BOM so Excel opens Arabic correctly
                fos.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
                OutputStreamWriter(fos, StandardCharsets.UTF_8).use { writer ->
                    writer.write("م,اسم المريض,نوع الحالة,إجمالي المبلغ,الواصل,الباقي,التاريخ واليوم,التشخيص والمعالجة,رقم الهاتف\n")
                    patients.forEach { p ->
                        writer.write(
                            "${p.id},${escapeCsv(p.patientName)},${escapeCsv(p.caseType)},${p.totalAmount},${p.paidAmount},${p.remainingAmount},${escapeCsv(p.dateAndDay)},${escapeCsv(p.diagnosisAndTreatment)},${escapeCsv(p.phoneNumber)}\n"
                        )
                    }
                }
            }
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun exportLabsToCsv(context: Context, labs: List<LabRecord>): Uri? {
        return try {
            val file = File(context.cacheDir, "alrahman_labs_${System.currentTimeMillis()}.csv")
            FileOutputStream(file).use { fos ->
                fos.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
                OutputStreamWriter(fos, StandardCharsets.UTF_8).use { writer ->
                    writer.write("اسم المعمل,نوع العمل,عدد الاسنان,اللون,تاريخ الارسال,تاريخ الاستلام,إجمالي المبلغ,الواصل,الباقي,رقم الهاتف\n")
                    labs.forEach { l ->
                        writer.write(
                            "${escapeCsv(l.labName)},${escapeCsv(l.workType)},${l.teethCount},${escapeCsv(l.shade)},${escapeCsv(l.sentDate)},${escapeCsv(l.receivedDate)},${l.totalAmount},${l.paidAmount},${l.remainingAmount},${escapeCsv(l.phoneNumber)}\n"
                        )
                    }
                }
            }
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun exportMaterialsToCsv(context: Context, materials: List<MaterialRecord>): Uri? {
        return try {
            val file = File(context.cacheDir, "alrahman_materials_${System.currentTimeMillis()}.csv")
            FileOutputStream(file).use { fos ->
                fos.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
                OutputStreamWriter(fos, StandardCharsets.UTF_8).use { writer ->
                    writer.write("اسم المحل,رقم الفاتورة,نوع المواد,إجمالي المبلغ,الواصل,الباقي,رقم الهاتف\n")
                    materials.forEach { m ->
                        writer.write(
                            "${escapeCsv(m.storeName)},${escapeCsv(m.invoiceNumber)},${escapeCsv(m.materialType)},${m.totalAmount},${m.paidAmount},${m.remainingAmount},${escapeCsv(m.phoneNumber)}\n"
                        )
                    }
                }
            }
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
