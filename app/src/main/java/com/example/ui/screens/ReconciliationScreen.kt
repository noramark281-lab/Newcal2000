package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
fun ReconciliationScreen(
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
                    Text("مصادقة الرصيد والتدقيق المالي", fontWeight = FontWeight.Bold, color = ClinicDarkTeal)
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
                                CommunicationUtil.shareFile(context, uri, "application/pdf", "تقرير مصادقة الرصيد")
                            } else {
                                Toast.makeText(context, "فشل إنشاء التقرير", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "طباعة تقرير", tint = ClinicDarkTeal)
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
            // Net Profit Hero Card
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = ClinicDarkTeal,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "صافي السيولة النقدية المتوفرة بالعيادة",
                        color = Color(0xCCFFFFFF),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${String.format(Locale.US, "%,.0f", summary.netClinicProfit)} ريال",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "(المقبوض نقداً من المرضى - المدفوع للمعمل والمواد)",
                        color = DentalGold,
                        fontSize = 11.5.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Text("تفاصيل التدقيق والمطابقة", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimaryDark)

            // Section 1: General / Patients (إيرادات العيادة)
            AuditCard(
                icon = Icons.Default.People,
                title = "قسم عام (المرضى)",
                totalLabel = "إجمالي قيمة الحالات",
                totalValue = summary.totalPatientsIncome,
                receivedLabel = "المبلغ المحصل فعلياً (نقدية)",
                receivedValue = summary.totalPatientsReceived,
                remainingLabel = "ديون المرضى المعلقة (لنا)",
                remainingValue = summary.totalPatientsRemaining,
                remainingColor = DebtRed
            )

            // Section 2: Dental Labs (المعمل)
            AuditCard(
                icon = Icons.Default.Architecture,
                title = "قسم المعمل (التركيبات)",
                totalLabel = "إجمالي فواتير المعامل",
                totalValue = summary.totalLabExpense,
                receivedLabel = "المسدد للمعمل",
                receivedValue = summary.totalLabPaid,
                remainingLabel = "مستحقات المعامل المتبقية (علينا)",
                remainingValue = summary.totalLabRemaining,
                remainingColor = WarningOrange
            )

            // Section 3: Dental Materials (المواد)
            AuditCard(
                icon = Icons.Default.Inventory2,
                title = "قسم المواد (المستلزمات)",
                totalLabel = "إجمالي فواتير المشتريات",
                totalValue = summary.totalMaterialExpense,
                receivedLabel = "المسدد للموردين",
                receivedValue = summary.totalMaterialPaid,
                remainingLabel = "ديون الموردين المتبقية (علينا)",
                remainingValue = summary.totalMaterialRemaining,
                remainingColor = WarningOrange
            )

            // Reconciliation Status Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xF2FFFFFF),
                border = BorderStroke(1.dp, Color(0x66FFFFFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(SuccessGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = SuccessGreen)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("حالة مصادقة السجلات", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimaryDark)
                        Text(
                            "جميع العمليات مطابقة للسجلات المحلية وقاعدة البيانات السحابية.",
                            fontSize = 11.5.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun AuditCard(
    icon: ImageVector,
    title: String,
    totalLabel: String,
    totalValue: Double,
    receivedLabel: String,
    receivedValue: Double,
    remainingLabel: String,
    remainingValue: Double,
    remainingColor: Color
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0xF5FFFFFF),
        border = BorderStroke(1.dp, Color(0x66FFFFFF)),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = ClinicDarkTeal, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ClinicDarkTeal)
            }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0x1F000000))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(totalLabel, fontSize = 12.5.sp, color = Color.Gray)
                Text("${String.format(Locale.US, "%,.0f", totalValue)} ريال", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(receivedLabel, fontSize = 12.5.sp, color = SuccessGreen)
                Text("${String.format(Locale.US, "%,.0f", receivedValue)} ريال", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SuccessGreen)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(remainingLabel, fontSize = 12.5.sp, color = remainingColor)
                Text("${String.format(Locale.US, "%,.0f", remainingValue)} ريال", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = remainingColor)
            }
        }
    }
}
