package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.ui.ClinicScreen
import com.example.ui.ClinicViewModel
import com.example.ui.components.ClinicNavigationDrawer
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.utils.CommunicationUtil
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val clinicViewModel: ClinicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                // Ensure RTL layout for Arabic Dental Clinic
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    AppRoot(viewModel = clinicViewModel)
                }
            }
        }
    }
}

@Composable
fun AppRoot(viewModel: ClinicViewModel) {
    // Open the clinic app directly without showing the Google sign-in screen.
    // Local Room data remains available. Cloud sync runs only when Firebase has
    // an authenticated user; bypassing sign-in does not create a cloud session.
    val currentUser = Firebase.auth.currentUser

    LaunchedEffect(currentUser?.uid) {
        if (currentUser != null) {
            viewModel.syncAllLocalToCloud()
        }
    }

    ClinicAppScaffold(
        viewModel = viewModel,
        onSignedOut = {
            // Keep the app open locally if the user signs out of Firebase.
        }
    )
}

@Composable
fun ClinicAppScaffold(
    viewModel: ClinicViewModel,
    onSignedOut: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val currentScreen by viewModel.currentScreen.collectAsState()
    val settings by viewModel.settings.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ClinicNavigationDrawer(
                currentScreen = currentScreen,
                onNavigate = { screen ->
                    viewModel.navigateTo(screen)
                },
                onCloseDrawer = {
                    coroutineScope.launch { drawerState.close() }
                },
                onExportBackup = {
                    coroutineScope.launch {
                        val json = viewModel.exportBackupJson()
                        CommunicationUtil.sendEmail(context, settings.gmailEmail, "نسخة احتياطية - عيادة الرحمن", json)
                    }
                },
                onRestoreBackup = {
                    viewModel.navigateTo(ClinicScreen.SETTINGS)
                },
                onOpenGmail = {
                    CommunicationUtil.sendEmail(
                        context,
                        settings.gmailEmail,
                        "استفسار من تطبيق عيادة الرحمن",
                        "السلام عليكم ورحمة الله وبركاته، د. مالك الرميمة"
                    )
                },
                onOpenWhatsApp = {
                    CommunicationUtil.sendWhatsAppMessage(
                        context,
                        "771134103",
                        "السلام عليكم دكتور مالك الرميمة، أود التواصل بشأن تطبيق عيادة الرحمن لطب وجراحة الفم والاسنان."
                    )
                },
                onGoogleDriveInfo = {
                    viewModel.navigateTo(ClinicScreen.SETTINGS)
                    Toast.makeText(context, "البيانات متزامنة سحابياً مع Firebase Firestore وجوجل درايف", Toast.LENGTH_LONG).show()
                }
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = currentScreen,
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    ClinicScreen.HOME -> {
                        HomeScreen(
                            onNavigate = { viewModel.navigateTo(it) },
                            onOpenDrawer = {
                                coroutineScope.launch { drawerState.open() }
                            },
                            onQuickAddPatient = { viewModel.navigateTo(ClinicScreen.GENERAL) },
                            onQuickAddLab = { viewModel.navigateTo(ClinicScreen.LAB) },
                            onQuickAddMaterial = { viewModel.navigateTo(ClinicScreen.MATERIALS) }
                        )
                    }
                    ClinicScreen.GENERAL -> {
                        GeneralScreen(
                            viewModel = viewModel,
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                    }
                    ClinicScreen.LAB -> {
                        LabScreen(
                            viewModel = viewModel,
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                    }
                    ClinicScreen.MATERIALS -> {
                        MaterialsScreen(
                            viewModel = viewModel,
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                    }
                    ClinicScreen.RECONCILIATION -> {
                        ReconciliationScreen(
                            viewModel = viewModel,
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                    }
                    ClinicScreen.REPORTS -> {
                        ReportsScreen(
                            viewModel = viewModel,
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                    }
                    ClinicScreen.REMINDERS -> {
                        RemindersScreen(
                            viewModel = viewModel,
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                    }
                    ClinicScreen.ACCOUNTS -> {
                        AccountsScreen(
                            viewModel = viewModel,
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                    }
                    ClinicScreen.SETTINGS -> {
                        SettingsScreen(
                            viewModel = viewModel,
                            onNavigate = { viewModel.navigateTo(it) },
                            onSignedOut = onSignedOut
                        )
                    }
                }
            }
        }
    }
}
