package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ClinicScreen
import com.example.ui.theme.ClinicDarkTeal
import com.example.ui.theme.DentalGold
import com.example.ui.theme.DentalRedBadge
import com.example.ui.theme.TextPrimaryDark

@Composable
fun ClinicNavigationDrawer(
    currentScreen: ClinicScreen,
    onNavigate: (ClinicScreen) -> Unit,
    onCloseDrawer: () -> Unit,
    onExportBackup: () -> Unit,
    onRestoreBackup: () -> Unit,
    onOpenGmail: () -> Unit,
    onOpenWhatsApp: () -> Unit,
    onGoogleDriveInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .width(320.dp),
        color = Color(0xF7FFFFFF),
        shape = RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 24.dp, bottomEnd = 24.dp),
        shadowElevation = 16.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                ClinicDarkTeal,
                                Color(0xFF093937)
                            )
                        )
                    )
                    .statusBarsPadding()
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x33FFFFFF))
                        ) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = DentalGold,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        IconButton(onClick = onCloseDrawer) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "إغلاق القائمة",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "ALRAHMAN CLINICAL",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "عيادة الرحمن لطب وجراحة الفم والاسنان",
                        color = Color(0xCCFFFFFF),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "الدكتور : مالك الرميمة  |  771134103",
                        color = DentalGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Navigation items according to PDF page 3
            DrawerItemRow(
                icon = Icons.Default.Email,
                title = "جيميل (البريد الإلكتروني)",
                subtitle = "مراسلة وتصدير الكشوفات",
                onClick = {
                    onCloseDrawer()
                    onOpenGmail()
                }
            )

            DrawerItemRow(
                icon = Icons.Default.AccountBalanceWallet,
                title = "اضافة حساب",
                subtitle = "الصناديق المالية والحسابات",
                isSelected = currentScreen == ClinicScreen.ACCOUNTS,
                onClick = {
                    onCloseDrawer()
                    onNavigate(ClinicScreen.ACCOUNTS)
                }
            )

            DrawerItemRow(
                icon = Icons.Default.TrendingUp,
                title = "سقف الحساب",
                subtitle = "تحديد سقوف الائتمان والمصروفات",
                isSelected = currentScreen == ClinicScreen.ACCOUNTS,
                onClick = {
                    onCloseDrawer()
                    onNavigate(ClinicScreen.ACCOUNTS)
                }
            )

            DrawerItemRow(
                icon = Icons.Default.NotificationsActive,
                title = "التذكيرات",
                subtitle = "مواعيد المرضى وطلبيات المعمل المعلقة",
                isSelected = currentScreen == ClinicScreen.REMINDERS,
                onClick = {
                    onCloseDrawer()
                    onNavigate(ClinicScreen.REMINDERS)
                }
            )

            DrawerItemRow(
                icon = Icons.AutoMirrored.Filled.Chat,
                title = "ربط واتس اب",
                subtitle = "إرسال التذكيرات ومطابقة الفواتير",
                onClick = {
                    onCloseDrawer()
                    onOpenWhatsApp()
                }
            )

            DrawerItemRow(
                icon = Icons.Default.Balance,
                title = "مصادقة الرصيد",
                subtitle = "التدقيق المالي ومطابقة الحسابات",
                isSelected = currentScreen == ClinicScreen.RECONCILIATION,
                onClick = {
                    onCloseDrawer()
                    onNavigate(ClinicScreen.RECONCILIATION)
                }
            )

            DrawerItemRow(
                icon = Icons.Default.Assessment,
                title = "التقارير",
                subtitle = "الإيرادات والمصروفات والأرباح",
                isSelected = currentScreen == ClinicScreen.REPORTS,
                onClick = {
                    onCloseDrawer()
                    onNavigate(ClinicScreen.REPORTS)
                }
            )

            HorizontalDivider(
                color = Color(0x1F000000),
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            DrawerItemRow(
                icon = Icons.Default.Save,
                title = "حفظ نسخة احتياطية",
                subtitle = "تصدير جميع السجلات إلى ملف محلي",
                onClick = {
                    onCloseDrawer()
                    onExportBackup()
                }
            )

            DrawerItemRow(
                icon = Icons.Default.Restore,
                title = "استرجاع نسخة احتياطية",
                subtitle = "استيراد البيانات واسترجاعها",
                onClick = {
                    onCloseDrawer()
                    onRestoreBackup()
                }
            )

            DrawerItemRow(
                icon = Icons.Default.CloudSync,
                title = "جوجل درايف",
                subtitle = "المزامنة والحفظ السحابي",
                onClick = {
                    onCloseDrawer()
                    onGoogleDriveInfo()
                }
            )

            DrawerItemRow(
                icon = Icons.Default.Settings,
                title = "الاعدادات",
                subtitle = "بيانات العيادة والعملة وقوالب الرسائل",
                isSelected = currentScreen == ClinicScreen.SETTINGS,
                onClick = {
                    onCloseDrawer()
                    onNavigate(ClinicScreen.SETTINGS)
                }
            )

            Spacer(modifier = Modifier.weight(1f, fill = false))
            Spacer(modifier = Modifier.height(20.dp))

            // Footer per PDF page 3
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color(0x0F0F4D4A), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "تصميم وبرمجة الدكتور مالك الرميمة",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = ClinicDarkTeal
                )
                Text(
                    text = "هاتف : 771134103",
                    fontSize = 12.sp,
                    color = TextPrimaryDark,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DrawerItemRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) Color(0x1F0F4D4A) else Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 2.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) ClinicDarkTeal else Color(0x120F4D4A))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else ClinicDarkTeal,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = if (isSelected) ClinicDarkTeal else TextPrimaryDark
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
