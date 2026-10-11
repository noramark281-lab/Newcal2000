package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ClinicScreen
import com.example.ui.ClinicViewModel
import com.example.ui.theme.*
import com.example.utils.CommunicationUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    viewModel: ClinicViewModel,
    onNavigate: (ClinicScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val patients by viewModel.patients.collectAsState()
    val labs by viewModel.labs.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: All, 1: Patients with debt, 2: Lab pending

    BackHandler {
        onNavigate(ClinicScreen.HOME)
    }

    val indebtedPatients = remember(patients) {
        patients.filter { it.remainingAmount > 0 }
    }

    val pendingLabs = remember(labs) {
        labs.filter { it.remainingAmount > 0 || it.receivedDate.isNotBlank() }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text("التذكيرات والمتابعة", fontWeight = FontWeight.Bold, color = ClinicDarkTeal)
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
                .padding(14.dp)
        ) {
            // Filter Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("الكل (${indebtedPatients.size + pendingLabs.size})") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("ديون المرضى (${indebtedPatients.size})") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    label = { Text("المعمل (${pendingLabs.size})") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Indebted Patients section
                if (selectedTab == 0 || selectedTab == 1) {
                    if (indebtedPatients.isNotEmpty()) {
                        item {
                            Text("مرضى لديهم مبالغ متبقية (ديون عيادة):", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ClinicDarkTeal)
                        }
                        items(indebtedPatients, key = { "pat_${it.id}" }) { p ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xF5FFFFFF),
                                border = BorderStroke(1.dp, Color(0x66FFFFFF)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(p.patientName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                            Text(p.caseType, fontSize = 12.sp, color = Color.Gray)
                                        }
                                        Text("${p.remainingAmount} ريال", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DebtRed)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                val msg = "تحية طيبة مريضنا العزيز (${p.patientName}) من عيادة الرحمن لطب وجراحة الفم والاسنان - د. مالك الرميمة.\nنود تذكيركم بالرصيد المتبقي لحالة (${p.caseType}) وقدره: ${p.remainingAmount} ريال.\nشاكرين تعاونكم (هاتف: 771134103)"
                                                CommunicationUtil.sendWhatsAppMessage(context, p.phoneNumber, msg)
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(1.dp, SuccessGreen),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("واتساب", fontSize = 12.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                                        }

                                        if (p.phoneNumber.isNotBlank()) {
                                            FilledTonalButton(
                                                onClick = {
                                                    CommunicationUtil.dialPhoneNumber(context, p.phoneNumber)
                                                },
                                                shape = RoundedCornerShape(10.dp),
                                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = ClinicDarkTeal, contentColor = Color.White)
                                            ) {
                                                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("اتصال", fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Pending Labs section
                if (selectedTab == 0 || selectedTab == 2) {
                    if (pendingLabs.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("طلبيات ومستحقات المعامل:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ClinicDarkTeal)
                        }
                        items(pendingLabs, key = { "lab_${it.id}" }) { l ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xF5FFFFFF),
                                border = BorderStroke(1.dp, Color(0x66FFFFFF)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(l.labName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                            Text("${l.workType} (${l.teethCount} أسنان)", fontSize = 12.sp, color = ClinicAccentTeal)
                                        }
                                        Text("${l.remainingAmount} ريال", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = WarningOrange)
                                    }

                                    if (l.receivedDate.isNotBlank()) {
                                        Text("تاريخ الاستلام: ${l.receivedDate}", fontSize = 11.5.sp, color = Color.Gray)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                val msg = "تحية طيبة للأخوة في (${l.labName}) من د. مالك الرميمة - عيادة الرحمن.\nنود الاستفسار عن موعد جاهزية طلبية (${l.workType}).\nتاريخ الاستلام: ${l.receivedDate}.\nشاكرين تعاونكم (771134103)"
                                                CommunicationUtil.sendWhatsAppMessage(context, l.phoneNumber, msg)
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(1.dp, SuccessGreen),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("واتساب المعمل", fontSize = 12.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                                        }

                                        if (l.phoneNumber.isNotBlank()) {
                                            FilledTonalButton(
                                                onClick = {
                                                    CommunicationUtil.dialPhoneNumber(context, l.phoneNumber)
                                                },
                                                shape = RoundedCornerShape(10.dp),
                                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = ClinicDarkTeal, contentColor = Color.White)
                                            ) {
                                                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("اتصال", fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
