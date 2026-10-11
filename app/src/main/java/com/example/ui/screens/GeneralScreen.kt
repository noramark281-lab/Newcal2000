package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PatientRecord
import com.example.ui.ClinicScreen
import com.example.ui.ClinicViewModel
import com.example.ui.components.SpeechBubbleTab
import com.example.ui.theme.*
import com.example.utils.CommunicationUtil
import com.example.utils.CsvExportUtil
import com.example.utils.PdfExportUtil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralScreen(
    viewModel: ClinicViewModel,
    onNavigate: (ClinicScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val patients by viewModel.patients.collectAsState()
    val searchQuery by viewModel.patientSearchQuery.collectAsState()

    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingPatient by remember { mutableStateOf<PatientRecord?>(null) }
    var patientToDelete by remember { mutableStateOf<PatientRecord?>(null) }

    BackHandler {
        onNavigate(ClinicScreen.HOME)
    }

    val totalSum = patients.sumOf { it.totalAmount }
    val paidSum = patients.sumOf { it.paidAmount }
    val remainingSum = patients.sumOf { it.remainingAmount }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(ClinicSkyBlueTop, ClinicSkyBlueMid)
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { onNavigate(ClinicScreen.HOME) },
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color(0x33FFFFFF), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "الرجوع للرئيسية",
                            tint = ClinicDarkTeal
                        )
                    }

                    // Tabs
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SpeechBubbleTab(
                            title = "المواد",
                            isSelected = false,
                            onClick = { onNavigate(ClinicScreen.MATERIALS) }
                        )
                        SpeechBubbleTab(
                            title = "المعمل",
                            isSelected = false,
                            onClick = { onNavigate(ClinicScreen.LAB) }
                        )
                        SpeechBubbleTab(
                            title = "عام",
                            isSelected = true,
                            onClick = {}
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar & Export Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.patientSearchQuery.value = it },
                        placeholder = { Text("بحث باسم المريض أو نوع الحالة...", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = ClinicDarkTeal)
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color(0xF2FFFFFF),
                            focusedBorderColor = ClinicDarkTeal,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    // Export PDF Button
                    FilledTonalButton(
                        onClick = {
                            val uri = PdfExportUtil.exportPatientsPdf(context, patients)
                            if (uri != null) {
                                CommunicationUtil.shareFile(context, uri, "application/pdf", "مشاركة تقرير المرضى PDF")
                            } else {
                                Toast.makeText(context, "فشل إنشاء تقرير PDF", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ClinicDarkTeal,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Export Excel Button
                    FilledTonalButton(
                        onClick = {
                            val uri = CsvExportUtil.exportPatientsToCsv(context, patients)
                            if (uri != null) {
                                CommunicationUtil.shareFile(context, uri, "text/csv", "مشاركة تقرير المرضى Excel")
                            } else {
                                Toast.makeText(context, "فشل تصدير Excel", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = SuccessGreen,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إكسل", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Summary Financial Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SummaryBadge(
                        title = "الإجمالي",
                        amount = totalSum,
                        color = ClinicDarkTeal,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryBadge(
                        title = "الواصل",
                        amount = paidSum,
                        color = SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryBadge(
                        title = "المتبقي (ديون)",
                        amount = remainingSum,
                        color = if (remainingSum > 0) DebtRed else TextPrimaryDark,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingPatient = null
                    showAddEditDialog = true
                },
                containerColor = ClinicDarkTeal,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("add_patient_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "إضافة مريض")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("إضافة مريض", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ClinicSkyBlueBottom)
        ) {
            if (patients.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MedicalInformation,
                        contentDescription = null,
                        tint = ClinicDarkTeal.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "لا توجد سجلات مرضى حالياً",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = TextPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "اضغط على زر (إضافة مريض) لتسجيل أول حالة وعلاج",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(patients, key = { _, p -> p.id }) { index, patient ->
                        PatientItemCard(
                            index = index + 1,
                            patient = patient,
                            onEdit = {
                                editingPatient = patient
                                showAddEditDialog = true
                            },
                            onDelete = {
                                patientToDelete = patient
                            },
                            onSendReminder = {
                                val message = "تحية طيبة مريضنا العزيز (${patient.patientName}) من عيادة الرحمن لطب وجراحة الفم والاسنان - د. مالك الرميمة.\nنود تذكيركم بموعدكم القادم لمتابعة حالة (${patient.caseType}).\nالمبلغ المتبقي: ${patient.remainingAmount} ريال.\nللتواصل والاستفسار: 771134103"
                                CommunicationUtil.sendWhatsAppMessage(context, patient.phoneNumber, message)
                            },
                            onCall = {
                                CommunicationUtil.dialPhoneNumber(context, patient.phoneNumber)
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Add / Edit Patient Dialog
    if (showAddEditDialog) {
        PatientFormDialog(
            patient = editingPatient,
            onDismiss = { showAddEditDialog = false },
            onSave = { updated ->
                if (editingPatient == null) {
                    viewModel.addPatient(updated)
                } else {
                    viewModel.updatePatient(updated)
                }
                showAddEditDialog = false
            }
        )
    }

    // Delete confirmation
    if (patientToDelete != null) {
        AlertDialog(
            onDismissRequest = { patientToDelete = null },
            title = { Text("تأكيد الحذف", fontWeight = FontWeight.Bold) },
            text = { Text("هل أنت متأكد من حذف سجل المريض (${patientToDelete?.patientName})؟") },
            confirmButton = {
                TextButton(
                    onClick = {
                        patientToDelete?.let { viewModel.deletePatient(it) }
                        patientToDelete = null
                    }
                ) {
                    Text("حذف", color = DebtRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { patientToDelete = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
private fun SummaryBadge(
    title: String,
    amount: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xF2FFFFFF),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
            Text(
                String.format(Locale.US, "%.0f", amount),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun PatientItemCard(
    index: Int,
    patient: PatientRecord,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSendReminder: () -> Unit,
    onCall: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0xF5FFFFFF),
        border = BorderStroke(1.dp, Color(0x66FFFFFF)),
        shadowElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: ID, Patient Name, Case Type, Edit/Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(ClinicDarkTeal.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$index",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = ClinicDarkTeal
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = patient.patientName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimaryDark
                        )
                        Text(
                            text = patient.caseType,
                            fontSize = 12.sp,
                            color = ClinicAccentTeal,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = ClinicDarkTeal, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = DebtRed, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Financial Table Details (إجمالي المبلغ, الواصل, الباقي)
            Surface(
                color = Color(0xFFF3F8FA),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("إجمالي المبلغ", fontSize = 10.5.sp, color = Color.Gray)
                        Text("${patient.totalAmount}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("الواصل", fontSize = 10.5.sp, color = Color.Gray)
                        Text("${patient.paidAmount}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("الباقي", fontSize = 10.5.sp, color = Color.Gray)
                        Text(
                            "${patient.remainingAmount}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (patient.remainingAmount > 0) DebtRed else TextPrimaryDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Date & Treatment Notes
            if (patient.diagnosisAndTreatment.isNotBlank()) {
                Text(
                    text = "التشخيص والمعالجة: ${patient.diagnosisAndTreatment}",
                    fontSize = 12.sp,
                    color = TextSecondaryDark,
                    lineHeight = 16.sp
                )
            }
            if (patient.dateAndDay.isNotBlank()) {
                Text(
                    text = "التاريخ واليوم: ${patient.dateAndDay}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: Send Reminder (WhatsApp/SMS) and Call Phone
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onSendReminder,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, SuccessGreen),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 4.dp, horizontal = 6.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ارسال رسالة تذكيرية", fontSize = 11.5.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                }

                if (patient.phoneNumber.isNotBlank()) {
                    FilledTonalButton(
                        onClick = onCall,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = ClinicDarkTeal, contentColor = Color.White),
                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 10.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "اتصال", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(patient.phoneNumber, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PatientFormDialog(
    patient: PatientRecord?,
    onDismiss: () -> Unit,
    onSave: (PatientRecord) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd (EEEE)", Locale("ar")) }
    val today = remember { dateFormat.format(Date()) }

    var name by remember { mutableStateOf(patient?.patientName ?: "") }
    var caseType by remember { mutableStateOf(patient?.caseType ?: "حشو عصب") }
    var totalAmount by remember { mutableStateOf(patient?.totalAmount?.toString() ?: "") }
    var paidAmount by remember { mutableStateOf(patient?.paidAmount?.toString() ?: "") }
    var dateAndDay by remember { mutableStateOf(patient?.dateAndDay ?: today) }
    var diagnosis by remember { mutableStateOf(patient?.diagnosisAndTreatment ?: "") }
    var phone by remember { mutableStateOf(patient?.phoneNumber ?: "") }

    val commonCases = listOf("حشو عصب", "حشوة ضوئية تجميلية", "قلع جراحي", "تنظيف وتبييض", "زراعة أسنان", "تركيبة ثابتة", "علاج لثة", "تقويم")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (patient == null) "إضافة مريض وحالة جديدة" else "تعديل بيانات المريض",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = ClinicDarkTeal
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم المريض *") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("patient_name_input")
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("رقم هاتف المريض") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("patient_phone_input")
                )

                // Common Case Type Chips
                Text("نوع الحالة / العلاج:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    commonCases.forEach { type ->
                        FilterChip(
                            selected = caseType == type,
                            onClick = { caseType = type },
                            label = { Text(type, fontSize = 11.5.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = caseType,
                    onValueChange = { caseType = it },
                    label = { Text("نوع الحالة مخصص") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = totalAmount,
                        onValueChange = { totalAmount = it },
                        label = { Text("إجمالي المبلغ *") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("patient_total_amount_input")
                    )

                    OutlinedTextField(
                        value = paidAmount,
                        onValueChange = { paidAmount = it },
                        label = { Text("الواصل") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("patient_paid_amount_input")
                    )
                }

                OutlinedTextField(
                    value = dateAndDay,
                    onValueChange = { dateAndDay = it },
                    label = { Text("التاريخ واليوم") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = diagnosis,
                    onValueChange = { diagnosis = it },
                    label = { Text("التشخيص والمعالجة") },
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val tot = totalAmount.toDoubleOrNull() ?: 0.0
                    val pd = paidAmount.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank()) {
                        val record = PatientRecord(
                            id = patient?.id ?: 0,
                            patientName = name.trim(),
                            caseType = caseType.trim(),
                            totalAmount = tot,
                            paidAmount = pd,
                            remainingAmount = tot - pd,
                            dateAndDay = dateAndDay.trim(),
                            diagnosisAndTreatment = diagnosis.trim(),
                            phoneNumber = phone.trim()
                        )
                        onSave(record)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ClinicDarkTeal),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("حفظ", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
