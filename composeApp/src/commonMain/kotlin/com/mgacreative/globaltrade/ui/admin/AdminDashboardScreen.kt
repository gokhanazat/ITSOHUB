package com.mgacreative.globaltrade.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mgacreative.globaltrade.core.domain.auth.SettingsService
import com.mgacreative.globaltrade.core.domain.auth.RegistryService
import com.mgacreative.globaltrade.core.domain.b2b.CompanyService
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    paddingValues: PaddingValues = PaddingValues(),
    onNavigateToUsers: () -> Unit,
    onNavigateToRegistry: () -> Unit,
    onNavigateToAuditLog: () -> Unit,
    onNavigateToEducations: () -> Unit,
    onNavigateToSectors: () -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToConsultancy: () -> Unit,
    onNavigateToHelpCenter: () -> Unit,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val settingsService = remember { SettingsService() }
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showSettingsDialog by remember { mutableStateOf(false) }
    var contactEmail by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    
    val companyService = remember { CompanyService() }
    val registryService = remember { RegistryService() }
    
    var totalCompanies by remember { mutableStateOf(0) }
    var activeRegistries by remember { mutableStateOf(0) }
    var isLoadingStats by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            coroutineScope {
                val companiesDef = async { companyService.getAllCompanies() }
                val registryDef = async { registryService.getAllRegistryEntries() }
                
                totalCompanies = companiesDef.await().getOrNull()?.size ?: 0
                val registryRes = registryDef.await()
                if (registryRes is com.mgacreative.globaltrade.core.error.AppResult.Success) {
                    activeRegistries = registryRes.data.count { it.active }
                }
            }
        } catch (e: Exception) {
            println("Admin Dashboard Stats Error: ${e.message}")
        } finally {
            isLoadingStats = false
        }
    }

    Scaffold(
        modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Yönetim Paneli", fontWeight = FontWeight.Bold, color = Color.White) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Çıkış", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF5F7FA))
                .padding(20.dp)
        ) {
            Text(
                text = "Hoş Geldiniz, Sistem Yöneticisi",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Platform yetkilerini ve kayıtlı üyeleri buradan yönetebilirsiniz.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            val adminModules = listOf(
                AdminModule("Sicil Yönetimi", "Yeni üye sicil no ekle.", Icons.Default.AppRegistration, Color(0xFF4361EE), onNavigateToRegistry),
                AdminModule("Kullanıcı Yetki", "Rolleri düzenle.", Icons.Default.People, Color(0xFF3A0CA3), onNavigateToUsers),
                AdminModule("Denetim Kaydı", "Logları incele.", Icons.Default.History, Color(0xFF4CC9F0), onNavigateToAuditLog),
                AdminModule("Eğitimler", "Modülleri tanımla.", Icons.Default.School, Color(0xFFF72585), onNavigateToEducations),
                AdminModule("Meslek Grupları", "Sektörleri yönet.", Icons.Default.Category, Color(0xFF7209B7), onNavigateToSectors),
                AdminModule("Duyuru Yönetimi", "Duyuruları yönet.", Icons.Default.Campaign, Color(0xFFF72585), onNavigateToAnnouncements),
                AdminModule("Danışmanlıklar", "Uzmanları yönet.", Icons.Default.SupportAgent, Color(0xFF4361EE), onNavigateToConsultancy),
                AdminModule("Yardım Merkezi", "SSS içeriklerini düzenle.", Icons.Default.LiveHelp, Color(0xFF4CC9F0), onNavigateToHelpCenter)
            )

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                items(adminModules) { module ->
                    AdminModuleCard(module)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                if (isLoadingStats) {
                    Box(Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                } else {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatItem("Tanımlanan İşletme Sayısı", "$totalCompanies")
                        HorizontalDivider(modifier = Modifier.height(30.dp).width(1.dp))
                        StatItem("Aktif Sicil Kaydı", "$activeRegistries")
                    }
                }
            }
        }
    }
}

@Composable
fun AdminModuleCard(module: AdminModule) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { module.onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = module.color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(module.icon, contentDescription = null, tint = module.color, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = module.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.DarkGray)
            Text(text = module.description, fontSize = 10.sp, color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
        Text(text = label, fontSize = 11.sp, color = Color.Gray)
    }
}

data class AdminModule(val title: String, val description: String, val icon: ImageVector, val color: Color, val onClick: () -> Unit)
