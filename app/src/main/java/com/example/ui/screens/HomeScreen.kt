package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.ClinicScreen
import com.example.ui.components.SpeechBubbleTab
import com.example.ui.theme.*
import com.example.utils.CommunicationUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigate: (ClinicScreen) -> Unit,
    onOpenDrawer: () -> Unit,
    onQuickAddPatient: () -> Unit,
    onQuickAddLab: () -> Unit,
    onQuickAddMaterial: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showQuickActionSheet by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        ClinicSkyBlueTop,
                        ClinicSkyBlueMid,
                        ClinicSkyBlueBottom
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar matching the uploaded mockup
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Menu Button (Dark teal rounded square with red badge)
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .testTag("menu_button")
                        .shadow(6.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(ClinicDarkTeal)
                        .clickable(onClick = onOpenDrawer),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(modifier = Modifier.size(width = 24.dp, height = 3.5.dp).background(Color.White, RoundedCornerShape(2.dp)))
                        Box(modifier = Modifier.size(width = 24.dp, height = 3.5.dp).background(Color.White, RoundedCornerShape(2.dp)))
                        Box(modifier = Modifier.size(width = 24.dp, height = 3.5.dp).background(Color.White, RoundedCornerShape(2.dp)))
                    }

                    // Red accent badge at top right
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(DentalRedBadge)
                    )
                }

                // 3 Speech Bubble Tabs: عام , المعمل , المواد
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
                        onClick = { onNavigate(ClinicScreen.GENERAL) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Center Medical Dental Illustration & Floating Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                // Background Dental Tooth Graphic Banner
                AsyncImage(
                    model = R.drawable.dental_tooth_banner,
                    contentDescription = "Dental Tooth Endodontic Illustration",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .shadow(4.dp, RoundedCornerShape(24.dp))
                )

                // Foreground Glassmorphic Clinic Badge Card
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xDEFFFFFF),
                    border = BorderStroke(1.5.dp, Color.White),
                    shadowElevation = 10.dp,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(horizontal = 12.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 16.dp, horizontal = 14.dp)
                    ) {
                        Text(
                            text = "ALRAHMAN CLINICAL",
                            fontWeight = FontWeight.Black,
                            fontSize = 19.sp,
                            color = ClinicDarkTeal,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "الدكتور : مالك الرميمة",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimaryDark
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    CommunicationUtil.dialPhoneNumber(context, "771134103")
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "اتصال",
                                tint = ClinicAccentTeal,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "771134103 : هاتف",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimaryDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Large 3D Silver Chevron / Plus Button
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xF0EDF4F7),
                border = BorderStroke(2.dp, Color.White),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .size(width = 90.dp, height = 54.dp)
                    .testTag("center_action_button")
                    .clickable { showQuickActionSheet = true }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "إضافة سريعة",
                            tint = ClinicDarkTeal,
                            modifier = Modifier.size(26.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = ClinicDarkTeal,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom Frosted Glass Card matching the mockup
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = Color(0xCCFFFFFF),
                border = BorderStroke(1.5.dp, Color(0xB3FFFFFF)),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🧮 برنامج حسابات عيادة الرحمن لطب وجراحة الفم والاسنان",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.5.sp,
                            color = TextPrimaryDark,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "💻🛠️ برمجة الدكتور : مالك الرميمة",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = ClinicDarkTeal
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                CommunicationUtil.dialPhoneNumber(context, "771134103")
                            }
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneInTalk,
                            contentDescription = "اتصال",
                            tint = ClinicDarkTeal,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "771134103 : هاتف",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimaryDark
                        )
                    }
                }
            }
        }
    }

    // Quick Action Bottom Sheet
    if (showQuickActionSheet) {
        ModalBottomSheet(
            onDismissRequest = { showQuickActionSheet = false },
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "إضافة سريعة / اختيار القسم",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = ClinicDarkTeal
                )
                Spacer(modifier = Modifier.height(14.dp))

                QuickActionButton(
                    icon = Icons.Default.PersonAdd,
                    title = "إضافة مريض / معالجة جديدة (عام)",
                    subtitle = "تسجيل بيانات المريض والمبالغ والتشخيص",
                    onClick = {
                        showQuickActionSheet = false
                        onQuickAddPatient()
                    }
                )

                QuickActionButton(
                    icon = Icons.Default.Architecture,
                    title = "إضافة طلبية للمعمل (المعمل)",
                    subtitle = "تسجيل تفاصيل التركيبة والأسنان واللون وتواريخ الإرسال",
                    onClick = {
                        showQuickActionSheet = false
                        onQuickAddLab()
                    }
                )

                QuickActionButton(
                    icon = Icons.Default.Inventory,
                    title = "إضافة فاتورة مواد ومستلزمات (المواد)",
                    subtitle = "تسجيل فاتورة الموردين ومشتريات العيادة",
                    onClick = {
                        showQuickActionSheet = false
                        onQuickAddMaterial()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        color = Color(0x0C0F4D4A),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(ClinicDarkTeal)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.5.sp,
                    color = TextPrimaryDark
                )
                Text(
                    text = subtitle,
                    fontSize = 11.5.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
