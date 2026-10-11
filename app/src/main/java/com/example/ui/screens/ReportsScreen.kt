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
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.utils.PdfExportUtil
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ClinicViewModel,
    onNavigate: (ClinicScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val summary by viewModel.financialSummary.collectAsState()
    val patients by viewModel.patients.collectAsState()
    val labs by viewModel.labs.collectAsState()
    val materials by viewModel.materials.collectAsState()

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
                    Text("تقارير وإحصائيات العيادة", fontWeight = FontWeight.Bold, color = ClinicDarkTeal)
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigate(ClinicScreen.HOME) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = ClinicDarkTeal)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val uri = PdfExportUtil.exportPatientsPdf(context, patients)
                            if (uri != null) {
                                CommunicationUtil.shareFile(context, uri, "application/pdf", "مشاركة تقرير العيادة")
                            } else {
                                Toast.makeText(context, "فشل إنشاء التقرير", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "تصدير PDF", tint = ClinicDarkTeal)
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
            // General Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "عدد المرضى",
                    value = "${patients.size}",
                    color = ClinicDarkTeal,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "طلبيات المعمل",
                    value = "${labs.size}",
                    color = ClinicAccentTeal,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "فواتير المواد",
                    value = "${materials.size}",
                    color = DentalGold,
                    modifier = Modifier.weight(1f)
                )
            }

            Text("المؤشرات المالية الرئيسية", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimaryDark)

            FinancialIndicatorRow(
                label = "إجمالي إيرادات المرضى المسجلة",
                amount = summary.totalPatientsIncome,
                color = ClinicDarkTeal
            )

            FinancialIndicatorRow(
                label = "المبالغ المحصلة كاش (الواصل)",
                amount = summary.totalPatientsReceived,
                color = SuccessGreen
            )

            FinancialIndicatorRow(
                label = "إجمالي ديون المرضى المعلقة",
                amount = summary.totalPatientsRemaining,
                color = DebtRed
            )

            FinancialIndicatorRow(
                label = "تكاليف ومصروفات المعامل والتركيبات",
                amount = summary.totalLabExpense,
                color = WarningOrange
            )

            FinancialIndicatorRow(
                label = "تكاليف ومصروفات المواد والمستلزمات",
                amount = summary.totalMaterialExpense,
                color = WarningOrange
            )

            FinancialIndicatorRow(
                label = "صافي الأرباح النقدية للعيادة",
                amount = summary.netClinicProfit,
                color = if (summary.netClinicProfit >= 0) SuccessGreen else DebtRed
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Percentage Bars / Simple Visual Meter
            val totalExpense = summary.totalLabExpense + summary.totalMaterialExpense
            val income = if (summary.totalPatientsIncome > 0) summary.totalPatientsIncome else 1.0
            val expenseRatio = (totalExpense / income).coerceIn(0.0, 1.0).toFloat()

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xF5FFFFFF),
                border = BorderStroke(1.dp, Color(0x66FFFFFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("نسبة المصروفات من إجمالي الدخل", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { expenseRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        color = WarningOrange,
                        trackColor = Color(0x200F4D4A)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "${String.format(Locale.US, "%.1f", expenseRatio * 100)}% من الإيرادات للمعمل والمواد",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xF5FFFFFF),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(title, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun FinancialIndicatorRow(
    label: String,
    amount: Double,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xF5FFFFFF),
        border = BorderStroke(1.dp, Color(0x40FFFFFF)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 13.sp, color = TextPrimaryDark, fontWeight = FontWeight.Medium)
            Text(
                "${String.format(Locale.US, "%,.0f", amount)} ريال",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
