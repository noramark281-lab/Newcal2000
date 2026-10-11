package com.example.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.model.LabRecord
import com.example.data.model.MaterialRecord
import com.example.data.model.PatientRecord
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExportUtil {

    private fun drawHeader(
        canvas: Canvas,
        title: String,
        doctorName: String,
        phone: String,
        width: Int
    ): Float {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        
        // Background banner
        paint.color = Color.rgb(15, 77, 74) // Clinic Dark Teal
        canvas.drawRect(0f, 0f, width.toFloat(), 110f, paint)

        // Title
        paint.color = Color.WHITE
        paint.textSize = 22f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("ALRAHMAN CLINICAL - عيادة الرحمن", width / 2f, 40f, paint)

        paint.textSize = 15f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("$doctorName  |  هاتف: $phone", width / 2f, 70f, paint)

        paint.textSize = 13f
        canvas.drawText("برنامج حسابات عيادة طب وجراحة الفم والاسنان", width / 2f, 95f, paint)

        // Section Title & Date
        paint.color = Color.rgb(30, 41, 59)
        paint.textSize = 18f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(title, width - 40f, 150f, paint)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        paint.textSize = 13f
        paint.typeface = Typeface.DEFAULT
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("التاريخ: ${dateFormat.format(Date())}", 40f, 150f, paint)

        // Separator line
        paint.color = Color.LTGRAY
        paint.strokeWidth = 1.5f
        canvas.drawLine(40f, 165f, width - 40f, 165f, paint)

        return 185f
    }

    fun exportPatientsPdf(
        context: Context,
        patients: List<PatientRecord>,
        doctorName: String = "الدكتور : مالك الرميمة",
        phone: String = "771134103"
    ): Uri? {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        var y = drawHeader(canvas, "تقرير المرضى والحسابات (عام)", doctorName, phone, 595)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Summary Bar
        val total = patients.sumOf { it.totalAmount }
        val paid = patients.sumOf { it.paidAmount }
        val remaining = patients.sumOf { it.remainingAmount }

        paint.color = Color.rgb(235, 245, 250)
        canvas.drawRoundRect(40f, y, 555f, y + 40f, 8f, 8f, paint)

        paint.color = Color.rgb(15, 77, 74)
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.RIGHT

        canvas.drawText("العدد: ${patients.size}  |  الإجمالي: $total  |  الواصل: $paid  |  الباقي: $remaining", 540f, y + 25f, paint)
        y += 60f

        // Table Header
        paint.color = Color.rgb(240, 240, 240)
        canvas.drawRect(40f, y, 555f, y + 25f, paint)
        paint.color = Color.BLACK
        paint.textSize = 11f
        paint.textAlign = Paint.Align.RIGHT

        canvas.drawText("م", 545f, y + 17f, paint)
        canvas.drawText("اسم المريض", 515f, y + 17f, paint)
        canvas.drawText("نوع الحالة", 380f, y + 17f, paint)
        canvas.drawText("الإجمالي", 280f, y + 17f, paint)
        canvas.drawText("الواصل", 220f, y + 17f, paint)
        canvas.drawText("الباقي", 160f, y + 17f, paint)
        canvas.drawText("الهاتف", 100f, y + 17f, paint)
        y += 30f

        paint.typeface = Typeface.DEFAULT
        patients.take(25).forEachIndexed { index, p ->
            if (index % 2 == 1) {
                paint.color = Color.rgb(250, 250, 250)
                canvas.drawRect(40f, y - 12f, 555f, y + 10f, paint)
            }
            paint.color = Color.rgb(30, 30, 30)
            paint.textSize = 10f
            canvas.drawText("${index + 1}", 545f, y, paint)
            val nameDisplay = if (p.patientName.length > 20) p.patientName.take(18) + ".." else p.patientName
            canvas.drawText(nameDisplay, 515f, y, paint)
            val caseDisplay = if (p.caseType.length > 15) p.caseType.take(13) + ".." else p.caseType
            canvas.drawText(caseDisplay, 380f, y, paint)
            canvas.drawText("${p.totalAmount}", 280f, y, paint)
            canvas.drawText("${p.paidAmount}", 220f, y, paint)
            canvas.drawText("${p.remainingAmount}", 160f, y, paint)
            canvas.drawText(p.phoneNumber, 100f, y, paint)
            y += 22f
        }

        // Footer
        paint.textSize = 10f
        paint.color = Color.GRAY
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("برمجة وتصميم الدكتور : مالك الرميمة  -  هاتف : 771134103", 595 / 2f, 820f, paint)

        document.finishPage(page)

        return savePdfToFile(context, document, "patients_report")
    }

    fun exportLabsPdf(
        context: Context,
        labs: List<LabRecord>,
        doctorName: String = "الدكتور : مالك الرميمة",
        phone: String = "771134103"
    ): Uri? {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        var y = drawHeader(canvas, "تقرير طلبيات المعمل (المعمل)", doctorName, phone, 595)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        val total = labs.sumOf { it.totalAmount }
        val paid = labs.sumOf { it.paidAmount }
        val remaining = labs.sumOf { it.remainingAmount }

        paint.color = Color.rgb(235, 245, 250)
        canvas.drawRoundRect(40f, y, 555f, y + 40f, 8f, 8f, paint)

        paint.color = Color.rgb(15, 77, 74)
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("العدد: ${labs.size}  |  الإجمالي: $total  |  الواصل: $paid  |  الباقي: $remaining", 540f, y + 25f, paint)
        y += 60f

        paint.color = Color.rgb(240, 240, 240)
        canvas.drawRect(40f, y, 555f, y + 25f, paint)
        paint.color = Color.BLACK
        paint.textSize = 11f
        paint.textAlign = Paint.Align.RIGHT

        canvas.drawText("المعمل", 545f, y + 17f, paint)
        canvas.drawText("العمل", 430f, y + 17f, paint)
        canvas.drawText("الأسنان", 340f, y + 17f, paint)
        canvas.drawText("اللون", 280f, y + 17f, paint)
        canvas.drawText("الإجمالي", 220f, y + 17f, paint)
        canvas.drawText("الواصل", 160f, y + 17f, paint)
        canvas.drawText("الباقي", 100f, y + 17f, paint)
        y += 30f

        paint.typeface = Typeface.DEFAULT
        labs.take(25).forEachIndexed { index, l ->
            if (index % 2 == 1) {
                paint.color = Color.rgb(250, 250, 250)
                canvas.drawRect(40f, y - 12f, 555f, y + 10f, paint)
            }
            paint.color = Color.rgb(30, 30, 30)
            paint.textSize = 10f
            canvas.drawText(l.labName, 545f, y, paint)
            canvas.drawText(l.workType, 430f, y, paint)
            canvas.drawText("${l.teethCount}", 340f, y, paint)
            canvas.drawText(l.shade, 280f, y, paint)
            canvas.drawText("${l.totalAmount}", 220f, y, paint)
            canvas.drawText("${l.paidAmount}", 160f, y, paint)
            canvas.drawText("${l.remainingAmount}", 100f, y, paint)
            y += 22f
        }

        paint.textSize = 10f
        paint.color = Color.GRAY
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("برمجة وتصميم الدكتور : مالك الرميمة  -  هاتف : 771134103", 595 / 2f, 820f, paint)

        document.finishPage(page)
        return savePdfToFile(context, document, "labs_report")
    }

    fun exportMaterialsPdf(
        context: Context,
        materials: List<MaterialRecord>,
        doctorName: String = "الدكتور : مالك الرميمة",
        phone: String = "771134103"
    ): Uri? {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        var y = drawHeader(canvas, "تقرير فواتير ومشتريات المواد (المواد)", doctorName, phone, 595)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        val total = materials.sumOf { it.totalAmount }
        val paid = materials.sumOf { it.paidAmount }
        val remaining = materials.sumOf { it.remainingAmount }

        paint.color = Color.rgb(235, 245, 250)
        canvas.drawRoundRect(40f, y, 555f, y + 40f, 8f, 8f, paint)

        paint.color = Color.rgb(15, 77, 74)
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("العدد: ${materials.size}  |  الإجمالي: $total  |  الواصل: $paid  |  الباقي: $remaining", 540f, y + 25f, paint)
        y += 60f

        paint.color = Color.rgb(240, 240, 240)
        canvas.drawRect(40f, y, 555f, y + 25f, paint)
        paint.color = Color.BLACK
        paint.textSize = 11f
        paint.textAlign = Paint.Align.RIGHT

        canvas.drawText("اسم المحل", 545f, y + 17f, paint)
        canvas.drawText("الفاتورة", 420f, y + 17f, paint)
        canvas.drawText("نوع المواد", 340f, y + 17f, paint)
        canvas.drawText("الإجمالي", 220f, y + 17f, paint)
        canvas.drawText("الواصل", 160f, y + 17f, paint)
        canvas.drawText("الباقي", 100f, y + 17f, paint)
        y += 30f

        paint.typeface = Typeface.DEFAULT
        materials.take(25).forEachIndexed { index, m ->
            if (index % 2 == 1) {
                paint.color = Color.rgb(250, 250, 250)
                canvas.drawRect(40f, y - 12f, 555f, y + 10f, paint)
            }
            paint.color = Color.rgb(30, 30, 30)
            paint.textSize = 10f
            canvas.drawText(m.storeName, 545f, y, paint)
            canvas.drawText(m.invoiceNumber, 420f, y, paint)
            val matDisplay = if (m.materialType.length > 15) m.materialType.take(13) + ".." else m.materialType
            canvas.drawText(matDisplay, 340f, y, paint)
            canvas.drawText("${m.totalAmount}", 220f, y, paint)
            canvas.drawText("${m.paidAmount}", 160f, y, paint)
            canvas.drawText("${m.remainingAmount}", 100f, y, paint)
            y += 22f
        }

        paint.textSize = 10f
        paint.color = Color.GRAY
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("برمجة وتصميم الدكتور : مالك الرميمة  -  هاتف : 771134103", 595 / 2f, 820f, paint)

        document.finishPage(page)
        return savePdfToFile(context, document, "materials_report")
    }

    private fun savePdfToFile(context: Context, document: PdfDocument, prefix: String): Uri? {
        return try {
            val file = File(context.cacheDir, "alrahman_${prefix}_${System.currentTimeMillis()}.pdf")
            FileOutputStream(file).use { out ->
                document.writeTo(out)
            }
            document.close()
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            try { document.close() } catch (_: Exception) {}
            null
        }
    }
}
