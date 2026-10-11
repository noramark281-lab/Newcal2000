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
import com.example.data.model.MaterialRecord
import com.example.ui.ClinicScreen
import com.example.ui.ClinicViewModel
import com.example.ui.components.SpeechBubbleTab
import com.example.ui.theme.*
import com.example.utils.CommunicationUtil
import com.example.utils.CsvExportUtil
import com.example.utils.PdfExportUtil
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialsScreen(
    viewModel: ClinicViewModel,
    onNavigate: (ClinicScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val materials by viewModel.materials.collectAsState()
    val searchQuery by viewModel.materialSearchQuery.collectAsState()

    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingMaterial by remember { mutableStateOf<MaterialRecord?>(null) }
    var materialToDelete by remember { mutableStateOf<MaterialRecord?>(null) }

    BackHandler {
        onNavigate(ClinicScreen.HOME)
    }

    val totalSum = materials.sumOf { it.totalAmount }
    val paidSum = materials.sumOf { it.paidAmount }
    val remainingSum = materials.sumOf { it.remainingAmount }

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
                            isSelected = true,
                            onClick = {}
                        )
                        SpeechBubbleTab(
                            title = "المعمل",
                            isSelected = false,
                            onClick = { onNavigate(ClinicScreen.LAB) }
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
                        onValueChange = { viewModel.materialSearchQuery.value = it },
                        placeholder = { Text("بحث باسم المحل أو نوع المواد...", fontSize = 13.sp) },
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
                            val uri = PdfExportUtil.exportMaterialsPdf(context, materials)
                            if (uri != null) {
                                CommunicationUtil.shareFile(context, uri, "application/pdf", "مشاركة تقرير المواد PDF")
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
                            val uri = CsvExportUtil.exportMaterialsToCsv(context, materials)
                            if (uri != null) {
                                CommunicationUtil.shareFile(context, uri, "text/csv", "مشاركة تقرير المواد Excel")
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
                        title = "إجمالي الفواتير",
                        amount = totalSum,
                        color = ClinicDarkTeal,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryBadge(
                        title = "الواصل للمحل",
                        amount = paidSum,
                        color = SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryBadge(
                        title = "متبقي للمورد",
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
                    editingMaterial = null
                    showAddEditDialog = true
                },
                containerColor = ClinicDarkTeal,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("add_material_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "إضافة فاتورة مواد")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("فاتورة مواد جديدة", fontWeight = FontWeight.Bold)
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
            if (materials.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = ClinicDarkTeal.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "لا توجد فواتير مواد ومستلزمات حالياً",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = TextPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "اضغط على زر (فاتورة مواد جديدة) لتسجيل فواتير المحلات والموردين",
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
                    itemsIndexed(materials, key = { _, m -> m.id }) { index, mat ->
                        MaterialItemCard(
                            index = index + 1,
                            material = mat,
                            onEdit = {
                                editingMaterial = mat
                                showAddEditDialog = true
                            },
                            onDelete = {
                                materialToDelete = mat
                            },
                            onSendReminder = {
                                val message = "تحية طيبة للأخوة في (${mat.storeName}) من عيادة الرحمن لطب وجراحة الفم والاسنان - د. مالك الرميمة.\nنود مطابقة فاتورة رقم (${mat.invoiceNumber}) الخاصة بمواد (${mat.materialType}).\nالمبلغ الإجمالي: ${mat.totalAmount} | الواصل: ${mat.paidAmount} | المتبقي: ${mat.remainingAmount} ريال.\nشاكرين تعاونكم (هاتف: 771134103)"
                                CommunicationUtil.sendWhatsAppMessage(context, mat.phoneNumber, message)
                            },
                            onCall = {
                                CommunicationUtil.dialPhoneNumber(context, mat.phoneNumber)
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

    // Add / Edit Material Dialog
    if (showAddEditDialog) {
        MaterialFormDialog(
            material = editingMaterial,
            onDismiss = { showAddEditDialog = false },
            onSave = { updated ->
                if (editingMaterial == null) {
                    viewModel.addMaterial(updated)
                } else {
                    viewModel.updateMaterial(updated)
                }
                showAddEditDialog = false
            }
        )
    }

    // Delete confirmation
    if (materialToDelete != null) {
        AlertDialog(
            onDismissRequest = { materialToDelete = null },
            title = { Text("تأكيد الحذف", fontWeight = FontWeight.Bold) },
            text = { Text("هل أنت متأكد من حذف فاتورة (${materialToDelete?.storeName} - ${materialToDelete?.materialType})؟") },
            confirmButton = {
                TextButton(
                    onClick = {
                        materialToDelete?.let { viewModel.deleteMaterial(it) }
                        materialToDelete = null
                    }
                ) {
                    Text("حذف", color = DebtRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { materialToDelete = null }) {
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
private fun MaterialItemCard(
    index: Int,
    material: MaterialRecord,
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
            // Header Row: ID, Store Name, Invoice Number
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
                            text = material.storeName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimaryDark
                        )
                        Text(
                            text = "فاتورة: ${material.invoiceNumber}  |  المواد: ${material.materialType}",
                            fontSize = 12.sp,
                            color = ClinicAccentTeal,
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
                        Text("${material.totalAmount}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("الواصل", fontSize = 10.5.sp, color = Color.Gray)
                        Text("${material.paidAmount}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("الباقي", fontSize = 10.5.sp, color = Color.Gray)
                        Text(
                            "${material.remainingAmount}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (material.remainingAmount > 0) WarningOrange else TextPrimaryDark
                        )
                    }
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

                if (material.phoneNumber.isNotBlank()) {
                    FilledTonalButton(
                        onClick = onCall,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = ClinicDarkTeal, contentColor = Color.White),
                        contentPadding = PaddingValues(vertical = 4.dp, horizontal = 10.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "اتصال", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(material.phoneNumber, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MaterialFormDialog(
    material: MaterialRecord?,
    onDismiss: () -> Unit,
    onSave: (MaterialRecord) -> Unit
) {
    var storeName by remember { mutableStateOf(material?.storeName ?: "") }
    var invoiceNumber by remember { mutableStateOf(material?.invoiceNumber ?: "") }
    var materialType by remember { mutableStateOf(material?.materialType ?: "كمبوزيت ومواد حشو") }
    var totalAmount by remember { mutableStateOf(material?.totalAmount?.toString() ?: "") }
    var paidAmount by remember { mutableStateOf(material?.paidAmount?.toString() ?: "") }
    var phone by remember { mutableStateOf(material?.phoneNumber ?: "") }

    val commonSupplies = listOf("كمبوزيت ومواد حشو", "تخدير موضعي وإبر", "مبارد روتاري وإندو", "مطاط وطبعات الجينات", "معقمات ومطهرات", "أدوات جراحية ومشارط")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (material == null) "تسجيل فاتورة مواد جديدة" else "تعديل فاتورة المواد",
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
                    value = storeName,
                    onValueChange = { storeName = it },
                    label = { Text("اسم المحل / المورد *") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("store_name_input")
                )

                OutlinedTextField(
                    value = invoiceNumber,
                    onValueChange = { invoiceNumber = it },
                    label = { Text("رقم الفاتورة") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("invoice_number_input")
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("رقم هاتف المحل") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("store_phone_input")
                )

                Text("نوع المواد الشائعة:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    commonSupplies.forEach { s ->
                        FilterChip(
                            selected = materialType == s,
                            onClick = { materialType = s },
                            label = { Text(s, fontSize = 11.5.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = materialType,
                    onValueChange = { materialType = it },
                    label = { Text("بيان المواد والمشتريات") },
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
                    if (storeName.isNotBlank()) {
                        val record = MaterialRecord(
                            id = material?.id ?: 0,
                            storeName = storeName.trim(),
                            invoiceNumber = invoiceNumber.trim(),
                            materialType = materialType.trim(),
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
