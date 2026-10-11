package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.data.model.ClinicSettings
import com.example.ui.ClinicScreen
import com.example.ui.ClinicViewModel
import com.example.ui.theme.*
import com.example.utils.CommunicationUtil
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ClinicViewModel,
    onNavigate: (ClinicScreen) -> Unit,
    onSignedOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val settings by viewModel.settings.collectAsState()

    var doctorName by remember(settings) { mutableStateOf(settings.doctorName) }
    var clinicName by remember(settings) { mutableStateOf(settings.clinicName) }
    var doctorPhone by remember(settings) { mutableStateOf(settings.doctorPhone) }
    var currency by remember(settings) { mutableStateOf(settings.currency) }
    var gmailEmail by remember(settings) { mutableStateOf(settings.gmailEmail) }
    var whatsappTemplate by remember(settings) { mutableStateOf(settings.whatsappDefaultTemplate) }

    var showRestoreDialog by remember { mutableStateOf(false) }
    var jsonRestoreInput by remember { mutableStateOf("") }

    BackHandler {
        onNavigate(ClinicScreen.HOME)
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text("الإعدادات والمزامنة", fontWeight = FontWeight.Bold, color = ClinicDarkTeal)
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigate(ClinicScreen.HOME) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = ClinicDarkTeal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ClinicSkyBlueMid)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ClinicSkyBlueBottom)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Google Account Status
            val currentUser = getFirebaseAuthOrNull(context)?.currentUser
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xF5FFFFFF),
                border = BorderStroke(1.dp, Color(0x66FFFFFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("حساب المزامنة السحابية (Firebase / Google):", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = ClinicDarkTeal)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentUser?.email ?: "تم تسجيل الدخول",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimaryDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            val credentialManager = CredentialManager.create(context)
                            signOutGoogle(context, credentialManager, onSignedOut, coroutineScope)
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DebtRed),
                        border = BorderStroke(1.dp, DebtRed)
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تسجيل الخروج من الحساب", fontSize = 12.sp)
                    }
                }
            }

            // Clinic Profile
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xF5FFFFFF),
                border = BorderStroke(1.dp, Color(0x66FFFFFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("بيانات العيادة والدكتور:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ClinicDarkTeal)

                    OutlinedTextField(
                        value = doctorName,
                        onValueChange = { doctorName = it },
                        label = { Text("اسم الدكتور") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = clinicName,
                        onValueChange = { clinicName = it },
                        label = { Text("اسم العيادة (بالإنجليزية أو العربية)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = doctorPhone,
                        onValueChange = { doctorPhone = it },
                        label = { Text("رقم هاتف العيادة") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = currency,
                        onValueChange = { currency = it },
                        label = { Text("العملة المستخدمة") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = gmailEmail,
                        onValueChange = { gmailEmail = it },
                        label = { Text("البريد الإلكتروني للعيادة (Gmail)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = whatsappTemplate,
                        onValueChange = { whatsappTemplate = it },
                        label = { Text("نص الترويسة في رسائل الواتساب") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            val updated = settings.copy(
                                doctorName = doctorName.trim(),
                                clinicName = clinicName.trim(),
                                doctorPhone = doctorPhone.trim(),
                                currency = currency.trim(),
                                gmailEmail = gmailEmail.trim(),
                                whatsappDefaultTemplate = whatsappTemplate.trim()
                            )
                            viewModel.updateSettings(updated)
                            Toast.makeText(context, "تم حفظ الإعدادات بنجاح", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ClinicDarkTeal),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("حفظ التغييرات", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Backup & Restore
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xF5FFFFFF),
                border = BorderStroke(1.dp, Color(0x66FFFFFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("النسخ الاحتياطي واسترجاع البيانات:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ClinicDarkTeal)
                    Text("يمكنك تصدير قاعدة بيانات العيادة بالكامل إلى ملف JSON لمشاركتها عبر جوجل درايف أو البريد، أو استرجاعها في أي وقت.", fontSize = 12.sp, color = Color.Gray)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    val json = viewModel.exportBackupJson()
                                    CommunicationUtil.sendEmail(context, gmailEmail, "نسخة احتياطية - عيادة الرحمن", json)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ClinicDarkTeal),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("تصدير النسخة", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                showRestoreDialog = true
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("استرجاع نسخة", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Google Drive Information
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xF5FFFFFF),
                border = BorderStroke(1.dp, Color(0x66FFFFFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("جوجل درايف والمزامنة السحابية:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ClinicDarkTeal)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "بيانات تطبيقك مربوطة بمزامنة سحابية آمنة على خوادم Google Firebase Firestore. لحفظ نسخة على Google Drive، قم بتصدير ملف النسخة الاحتياطية واختيار Google Drive من خيارات المشاركة.",
                        fontSize = 12.sp,
                        color = TextSecondaryDark,
                        lineHeight = 18.sp
                    )
                }
            }

            // Footer
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Text("برمجة وتصميم الدكتور : مالك الرميمة", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ClinicDarkTeal)
                Text("هاتف : 771134103", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = TextPrimaryDark)
            }
        }
    }

    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("استرجاع نسخة احتياطية", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("الصق محتوى ملف النسخة الاحتياطية (JSON) هنا:", fontSize = 12.sp, color = Color.Gray)
                    OutlinedTextField(
                        value = jsonRestoreInput,
                        onValueChange = { jsonRestoreInput = it },
                        minLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val success = viewModel.restoreBackupJson(jsonRestoreInput)
                            if (success) {
                                Toast.makeText(context, "تم استرجاع النسخة الاحتياطية بنجاح!", Toast.LENGTH_LONG).show()
                                showRestoreDialog = false
                            } else {
                                Toast.makeText(context, "فشل الاسترجاع، تأكد من صحة ملف JSON", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ClinicDarkTeal)
                ) {
                    Text("استرجاع الآن")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
