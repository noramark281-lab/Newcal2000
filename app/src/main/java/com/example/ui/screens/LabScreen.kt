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
import com.example.data.model.LabRecord
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
fun LabScreen(
    viewModel: ClinicViewModel,
    onNavigate: (ClinicScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val labs by viewModel.labs.collectAsState()
    val searchQuery by viewModel.labSearchQuery.collectAsState()

    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingLab by remember { mutableStateOf<LabRecord?>(null) }
    var labToDelete by remember { mutableStateOf<LabRecord?>(null) }

    BackHandler {
        onNavigate(ClinicScreen.HOME)
    }

    val totalSum = labs.sumOf { it.totalAmount }
    val paidSum = labs.sumOf { it.paidAmount }
    val remainingSum = labs.sumOf { it.remainingAmount }

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
                            isSelected = true,
                            onClick = {}
                        )
                        SpeechBubbleTab(
                            title = "عام",
                            isSelected = false,
                            onClick = { onNavigate(ClinicScreen.GENERAL) }
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
                        onValueChange = { viewModel.labSearchQuery.value = it },
                        placeholder = { Text("بحث باسم المعمل أو نوع العمل...", fontSize = 13.sp) },
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
                            val uri = PdfExportUtil.exportLabsPdf(context, labs)
                            if (uri != null) {
                                CommunicationUtil.shareFile(context, uri, "application/pdf", "مشاركة تقرير المعمل PDF")
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
                            val uri = CsvExportUtil.exportLabsToCsv(context, labs)
                            if (uri != null) {
                                CommunicationUtil.shareFile(context, uri, "text/csv", "مشاركة تقرير المعمل Excel")
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
                        title = "إجمالي التكلفة",
                        amount = totalSum,
                        color = ClinicDarkTeal,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryBadge(
                        title = "الواصل للمعمل",
                        amount = paidSum,
                        color = SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryBadge(
                        title = "متبقي للمعمل",
                        amount = remainingSum,
                        color = if (remainingSum > 0) WarningOrange else TextPrimaryDark,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingLab = null
                    showAddEditDialog = true
                },
                containerColor = ClinicDarkTeal,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("add_lab_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "إضافة طلبية معمل")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("طلبية معمل جديدة", fontWeight = FontWeight.Bold)
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
            if (labs.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Architecture,
                        contentDescription = null,
                        tint = ClinicDarkTeal.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "لا توجد طلبيات معامل حالياً",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = TextPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "اضغط على زر (طلبية معمل جديدة) لتسجيل تركيبات المعمل",
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
                    itemsIndexed(labs, key = { _, l -> l.id }) { index, lab ->
                        LabItemCard(
                            index = index + 1,
                            lab = lab,
                            onEdit = {
                                editingLab = lab
                                showAddEditDialog = true
                            },
                            onDelete = {
                                labToDelete = lab
                            },
                            onSendReminder = {
                                val message = "تحية طيبة للأخوة في (${lab.labName}) من عيادة الرحمن لطب وجراحة الفم والاسنان - د. مالك الرميمة.\nنود الاستفسار ومتابعة طلبية (${lab.workType}) - عدد الأسنان: ${lab.teethCount} - اللون: ${lab.shade}.\nتاريخ الاستلام المحدد: ${lab.receivedDate}.\nالمتبقي المالي: ${lab.remainingAmount} ريال.\nشاكرين تعاونكم (هاتف: 771134103)"
                                CommunicationUtil.sendWhatsAppMessage(context, lab.phoneNumber, message)
                            },
                            onCall = {
                                CommunicationUtil.dialPhoneNumber(context, lab.phoneNumber)
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

    // Add / Edit Lab Dialog
    if (showAddEditDialog) {
        LabFormDialog(
            lab = editingLab,
            onDismiss = { showAddEditDialog = false },
            onSave = { updated ->
                if (editingLab == null) {
                    viewModel.addLab(updated)
                } else {
                    viewModel.updateLab(updated)
                }
                showAddEditDialog = false
            }
        )
    }

    // Delete confirmation
    if (labToDelete != null) {
        AlertDialog(
            onDismissRequest = { labToDelete = null },
            title = { Text("تأكيد الحذف", fontWeight = FontWeight.Bold) },
            text = { Text("هل أنت متأكد من حذف طلبية المعمل (${labToDelete?.labName} - ${labToDelete?.workType})؟") },
            confirmButton = {
                TextButton(
                    onClick = {
                        labToDelete?.let { viewModel.deleteLab(it) }
                        labToDelete = null
                    }
                ) {
                    Text("حذف", color = DebtRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { labToDelete = null }) {
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
private fun LabItemCard(
    index: Int,
    lab: LabRecord,
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
            // Header Row: ID, Lab Name, Work Type
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
                            .background(ClinicAccentTeal.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$index",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = ClinicAccentTeal
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = lab.labName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimaryDark
                        )
                        Text(
                            text = "${lab.workType}  |  ${lab.teethCount} أسنان  |  اللون: ${lab.shade}",
                            fontSize = 12.sp,
                            color = ClinicDarkTeal,
                            fontWeight = FontWeight.SemiBold
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
                        Text("${lab.totalAmount}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("الواصل", fontSize = 10.5.sp, color = Color.Gray)
                        Text("${lab.paidAmount}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("الباقي", fontSize = 10.5.sp, color = Color.Gray)
                        Text(
                            "${lab.remainingAmount}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (lab.remainingAmount > 0) WarningOrange else TextPrimaryDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sent & Delivery Dates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (lab.sentDate.isNotBlank()) {
                    Text("تاريخ الإرسال: ${lab.sentDate}", fontSize = 11.sp, color = Color.Gray)
                }
                if (lab.receivedDate.isNotBlank()) {
                    Text("تاريخ الاستلام: ${lab.receivedDate}", fontSize = 11.sp, color = ClinicDarkTeal, fontWeight = FontWeight.Bold)
                }
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

                if (lab.phoneNumber.isNotBlank()) {
                    FilledTonalButton(
                        onClick = onCall,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = ClinicDarkTeal, contentColor = Color.White),
                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 10.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "اتصال", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(lab.phoneNumber, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun LabFormDialog(
    lab: LabRecord?,
    onDismiss: () -> Unit,
    onSave: (LabRecord) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val today = remember { dateFormat.format(Date()) }

    var labName by remember { mutableStateOf(lab?.labName ?: "") }
    var workType by remember { mutableStateOf(lab?.workType ?: "زركونيا Zirconia") }
    var teethCount by remember { mutableStateOf(lab?.teethCount?.toString() ?: "1") }
    var shade by remember { mutableStateOf(lab?.shade ?: "A2") }
    var sentDate by remember { mutableStateOf(lab?.sentDate ?: today) }
    var receivedDate by remember { mutableStateOf(lab?.receivedDate ?: "") }
    var totalAmount by remember { mutableStateOf(lab?.totalAmount?.toString() ?: "") }
    var paidAmount by remember { mutableStateOf(lab?.paidAmount?.toString() ?: "") }
    var phone by remember { mutableStateOf(lab?.phoneNumber ?: "") }

    val commonWorks = listOf("زركونيا Zirconia", "إيماكس E-Max", "بي اف ام PFM", "طقم أكريليك", "طقم كاست", "فينير تجميلي", "جسر خزف")
    val commonShades = listOf("A1", "A2", "A3", "A3.5", "B1", "BL2", "BL3")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (lab == null) "تسجيل طلبية معمل جديدة" else "تعديل طلبية المعمل",
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
                    value = labName,
                    onValueChange = { labName = it },
                    label = { Text("اسم المعمل *") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("lab_name_input")
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("رقم هاتف المعمل") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("lab_phone_input")
                )

                Text("نوع العمل:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    commonWorks.forEach { w ->
                        FilterChip(
                            selected = workType == w,
                            onClick = { workType = w },
                            label = { Text(w, fontSize = 11.5.sp) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = teethCount,
                        onValueChange = { teethCount = it },
                        label = { Text("عدد الأسنان") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = shade,
                        onValueChange = { shade = it },
                        label = { Text("اللون / Shade") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    commonShades.forEach { sh ->
                        FilterChip(
                            selected = shade == sh,
                            onClick = { shade = sh },
                            label = { Text(sh, fontSize = 11.sp) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = sentDate,
                        onValueChange = { sentDate = it },
                        label = { Text("تاريخ الإرسال") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = receivedDate,
                        onValueChange = { receivedDate = it },
                        label = { Text("تاريخ الاستلام") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

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
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = paidAmount,
                        onValueChange = { paidAmount = it },
                        label = { Text("الواصل") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val tot = totalAmount.toDoubleOrNull() ?: 0.0
                    val pd = paidAmount.toDoubleOrNull() ?: 0.0
                    val cnt = teethCount.toIntOrNull() ?: 1
                    if (labName.isNotBlank()) {
                        val record = LabRecord(
                            id = lab?.id ?: 0,
                            labName = labName.trim(),
                            workType = workType.trim(),
                            teethCount = cnt,
                            shade = shade.trim(),
                            sentDate = sentDate.trim(),
                            receivedDate = receivedDate.trim(),
                            totalAmount = tot,
                            paidAmount = pd,
                            remainingAmount = tot - pd,
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
