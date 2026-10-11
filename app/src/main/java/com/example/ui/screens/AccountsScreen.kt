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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClinicAccount
import com.example.ui.ClinicScreen
import com.example.ui.ClinicViewModel
import com.example.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    viewModel: ClinicViewModel,
    onNavigate: (ClinicScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val accounts by viewModel.accounts.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingAccount by remember { mutableStateOf<ClinicAccount?>(null) }

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
                    Text("الحسابات وسقف الحساب", fontWeight = FontWeight.Bold, color = ClinicDarkTeal)
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigate(ClinicScreen.HOME) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = ClinicDarkTeal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ClinicSkyBlueMid)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingAccount = null
                    showDialog = true
                },
                containerColor = ClinicDarkTeal,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("add_account_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("إضافة حساب", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ClinicSkyBlueBottom)
                .padding(16.dp)
        ) {
            Text(
                "إدارة الصناديق المالية وسقوف الحسابات الخاصة بالعيادة:",
                fontSize = 13.sp,
                color = TextSecondaryDark,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(accounts, key = { it.id }) { acc ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xF5FFFFFF),
                        border = BorderStroke(1.dp, Color(0x66FFFFFF)),
                        shadowElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(acc.accountName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ClinicDarkTeal)
                                    Text("النوع: ${acc.accountType}", fontSize = 12.sp, color = Color.Gray)
                                }
                                Row {
                                    IconButton(
                                        onClick = {
                                            editingAccount = acc
                                            showDialog = true
                                        }
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = ClinicDarkTeal)
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteAccount(acc) }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = DebtRed)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Color(0x1F000000))
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("سقف الحساب / الحد الائتماني", fontSize = 11.sp, color = Color.Gray)
                                    Text("${String.format(Locale.US, "%,.0f", acc.balanceLimit)} ريال", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = TextPrimaryDark)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("الرصيد الحالي", fontSize = 11.sp, color = Color.Gray)
                                    Text("${String.format(Locale.US, "%,.0f", acc.currentBalance)} ريال", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = SuccessGreen)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        var name by remember { mutableStateOf(editingAccount?.accountName ?: "") }
        var type by remember { mutableStateOf(editingAccount?.accountType ?: "نقدية / كاش") }
        var limit by remember { mutableStateOf(editingAccount?.balanceLimit?.toString() ?: "500000") }
        var balance by remember { mutableStateOf(editingAccount?.currentBalance?.toString() ?: "0") }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(if (editingAccount == null) "إضافة حساب جديد" else "تعديل الحساب وسقف الرصيد", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("اسم الحساب (مثال: صندوق العيادة)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = type,
                        onValueChange = { type = it },
                        label = { Text("نوع الحساب") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = limit,
                        onValueChange = { limit = it },
                        label = { Text("سقف الحساب (الحد الأقصى)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = balance,
                        onValueChange = { balance = it },
                        label = { Text("الرصيد الافتتاحي") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val lim = limit.toDoubleOrNull() ?: 0.0
                        val bal = balance.toDoubleOrNull() ?: 0.0
                        if (name.isNotBlank()) {
                            val acc = ClinicAccount(
                                id = editingAccount?.id ?: 0,
                                accountName = name.trim(),
                                accountType = type.trim(),
                                balanceLimit = lim,
                                currentBalance = bal
                            )
                            if (editingAccount == null) {
                                viewModel.addAccount(acc)
                            } else {
                                viewModel.updateAccount(acc)
                            }
                            showDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ClinicDarkTeal)
                ) {
                    Text("حفظ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
