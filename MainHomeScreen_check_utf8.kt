package com.mgacreative.globaltrade.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mgacreative.globaltrade.manager.changeAppLanguage
import com.mgacreative.globaltrade.manager.getCurrentAppLanguage
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import itsohub.composeapp.generated.resources.*
import com.mgacreative.globaltrade.core.domain.showroom.ProductService
import com.mgacreative.globaltrade.core.domain.showroom.ShowroomProduct
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import com.mgacreative.globaltrade.core.domain.announcement.Announcement
import com.mgacreative.globaltrade.core.domain.announcement.AnnouncementService
import com.mgacreative.globaltrade.core.domain.b2b.B2BCompany
import com.mgacreative.globaltrade.core.domain.b2b.CompanyService
import com.mgacreative.globaltrade.core.domain.sector.Sector
import com.mgacreative.globaltrade.core.domain.sector.SectorService
import androidx.compose.foundation.text.BasicTextField
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import com.mgacreative.globaltrade.core.network.ApiConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainHomeScreen(
    onModuleClick: (String, String?, String?) -> Unit = { _, _, _ -> },
    onProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {}
) {
    val productService = remember { ProductService() }
    val announcementService = remember { AnnouncementService() }
    val sectorService = remember { SectorService() }
    val companyService = remember { CompanyService() }

    var products by remember { mutableStateOf<List<ShowroomProduct>>(emptyList()) }
    var announcements by remember { mutableStateOf<List<Announcement>>(emptyList()) }
    var allCompanies by remember { mutableStateOf<List<B2BCompany>>(emptyList()) }
    var allSectors by remember { mutableStateOf<List<Sector>>(emptyList()) }
    
    val scope = rememberCoroutineScope()
    
    var isLoading by remember { mutableStateOf(true) }
    var selectedProductForDetail by remember { mutableStateOf<ShowroomProduct?>(null) }
    
    var sectorQuery by remember { mutableStateOf("") }
    var companyQuery by remember { mutableStateOf("") }
    var sectorSearchSuggestions by remember { mutableStateOf<List<Sector>>(emptyList()) }
    var companySearchSuggestions by remember { mutableStateOf<List<B2BCompany>>(emptyList()) }

    val announcementsTitle = stringResource(Res.string.platform_announcements)
    val noAnnouncementsText = stringResource(Res.string.no_active_announcements)

    LaunchedEffect(Unit) {
        try {
            coroutineScope {
                val productsDef = async { productService.getAllProducts() }
                val announcementsDef = async { announcementService.getActiveAnnouncements() }
                val companiesDef = async { companyService.getAllCompanies() }
                val sectorsDef = async { sectorService.getSectors() }

                products = productsDef.await().getOrNull() ?: emptyList()
                announcements = announcementsDef.await().getOrNull() ?: emptyList()
                allCompanies = companiesDef.await().getOrNull() ?: emptyList()
                allSectors = sectorsDef.await().getOrNull() ?: emptyList()
            }
        } catch (e: Exception) {
            println("MainHomeScreen Data Load Error: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidth = maxWidth
        val isWeb = screenWidth > 800.dp
        val horizontalPadding = if (isWeb) (screenWidth - 800.dp) / 2 else 0.dp
        
        // Background
        Box(modifier = Modifier.fillMaxSize().background(if (isWeb) Color(0xFF0F172A) else Color(0xFFF8FAFC)))

        // Hero Background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isWeb) 260.dp else 200.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B)),
                        startY = 0f,
                        endY = 600f
                    )
                )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = if (isWeb) 48.dp else 40.dp, bottom = 24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "GLOBAL TRADE",
                            color = Color.White,
                            style = if (isWeb) MaterialTheme.typography.displaySmall else MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "MGA Creative Works",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding)
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 24.dp)
                    ) {
                        // Top Bar: Languages & Profile
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LanguageFlagBar(
                                onLanguageClick = { code -> 
                                    scope.launch { changeAppLanguage(code) }
                                }
                            )

                            Surface(
                                onClick = onProfileClick,
                                shape = CircleShape,
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "GiriÅŸ Yap",
                                        tint = Color(0xFF1E293B),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp),
                            color = Color(0xFFF1F5F9)
                        )

                        // Showroom
                        Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                            FeaturedModuleCard(
                                title = stringResource(Res.string.nav_showroom),
                                imageRes = Res.drawable.products,
                                onClick = { onModuleClick("Showroom", null, null) }
                            )
                        }

                        // Search
                        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    SearchInput(
                                        value = sectorQuery,
                                        placeholder = stringResource(Res.string.search_sector),
                                        icon = Icons.Default.Category,
                                        onValueChange = { newVal ->
                                            sectorQuery = newVal
                                            val q = newVal.trim()
                                            sectorSearchSuggestions = if (q.isEmpty()) emptyList() 
                                            else allSectors.filter { it.name.contains(q, true) || it.groupNo.contains(q, true) }.take(5)
                                        }
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    SearchInput(
                                        value = companyQuery,
                                        placeholder = stringResource(Res.string.search_company),
                                        icon = Icons.Default.Business,
                                        onValueChange = { newVal ->
                                            companyQuery = newVal
                                            val q = newVal.trim()
                                            companySearchSuggestions = if (q.isEmpty()) emptyList() 
                                            else allCompanies.filter { it.name.contains(q, true) }.take(5)
                                        }
                                    )
                                }
                            }
                            
                            if (sectorSearchSuggestions.isNotEmpty() || companySearchSuggestions.isNotEmpty()) {
                                SuggestionsOverlay(
                                    sectorSuggestions = sectorSearchSuggestions,
                                    companySuggestions = companySearchSuggestions,
                                    onSectorClick = { s -> 
                                        onModuleClick("Showroom", s.name, null)
                                        sectorQuery = ""
                                        sectorSearchSuggestions = emptyList()
                                    },
                                    onCompanyClick = { c ->
                                        onModuleClick("Showroom", null, c.id)
                                        companyQuery = ""
                                        companySearchSuggestions = emptyList()
                                    }
                                )
                            }
                        }

                        // Announcements
                        Text(
                            text = announcementsTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 24.dp),
                            color = Color.DarkGray
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (announcements.isNotEmpty()) {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(announcements) { announcement ->
                                    AnnouncementCard(
                                        announcement.title,
                                        announcement.description,
                                        Color(colorStringToInt(announcement.colorHex)),
                                        isWeb = isWeb
                                    )
                                }
                            }
                        } else if (!isLoading) {
                            Text(
                                noAnnouncementsText,
                                modifier = Modifier.padding(horizontal = 24.dp),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Features List
                        Column(
                            modifier = Modifier.padding(horizontal = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            WideFeatureCard(
                                title = stringResource(Res.string.nav_b2b),
                                subtitle = "KÃ¼resel aÄŸÄ±nÄ±zÄ± geniÅŸletin ve yeni iÅŸ ortaklarÄ± bulun",
                                imageRes = Res.drawable.companymeeting,
                                onClick = { onModuleClick("CompanyMeeting", null, null) }
                            )

                            WideFeatureCard(
                                title = stringResource(Res.string.economic_news),
                                subtitle = "DÃ¼nyadan gÃ¼ncel ekonomi ve ticaret haberleri",
                                imageRes = Res.drawable.news,
                                onClick = { onModuleClick("EconomicNews", null, null) }
                            )
                        }
                    }
                }
            }

            item {
                FooterSection(isWeb = isWeb, horizontalPadding = horizontalPadding)
            }
        }
    } // Closes BoxWithConstraints

    selectedProductForDetail?.let { product ->
        SimpleProductDetailDialog(
            product = product,
            onDismissRequest = { selectedProductForDetail = null }
        )
    }
    
    if (isLoading) {
        Box(Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun FeaturedModuleCard(
    title: String,
    imageRes: org.jetbrains.compose.resources.DrawableResource,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp) // Height increased
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // Shadow increased
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = org.jetbrains.compose.resources.painterResource(imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 100f
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(Res.string.showroom_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ArrowForward, null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@Composable
fun SimpleProductDetailDialog(
    product: ShowroomProduct,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        val scrollState = rememberScrollState()
        Card(
            modifier = Modifier.fillMaxWidth().heightIn(max = 500.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(Color(0xFFF1F3F5))) {
                    if (!product.imageUrl.isNullOrBlank()) {
                        val rawUrl = product.imageUrl ?: ""
                        val model: Any? = if (rawUrl.startsWith("data:image/")) {
                            try {
                                val base64String = rawUrl.substringAfter("base64,").trim()
                                @OptIn(ExperimentalEncodingApi::class)
                                Base64.Default.decode(base64String)
                            } catch (e: Exception) { null }
                        } else {
                            val cleaned = rawUrl.trim().filter { it.code in 33..1000 }
                            if (cleaned.isNotBlank() && cleaned != "null") {
                                if (cleaned.startsWith("http")) cleaned else ApiConfig.getImageUrl(cleaned)
                            } else null
                        }

                        if (model != null) {
                            coil3.compose.AsyncImage(
                                model = model,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.BrokenImage, null, tint = Color.Gray, modifier = Modifier.align(Alignment.Center))
                        }
                    }
                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).background(Color.Black.copy(alpha=0.3f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, null, tint = Color.White)
                    }
                }
                
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(product.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(product.companyName, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(product.country, color = Color.Gray)
                        Spacer(modifier = Modifier.weight(1f))
                        if (product.price.isNotBlank()) {
                            Text("$${product.price}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("ÃœrÃ¼n AÃ§Ä±klamasÄ±", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(product.description.ifEmpty { "AÃ§Ä±klama bulunmuyor." }, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = onDismissRequest,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Kapat", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AnnouncementCard(title: String, desc: String, accentColor: Color, isWeb: Boolean = false) {
    Card(
        modifier = Modifier.width(if (isWeb) 350.dp else 260.dp).height(120.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxHeight().width(6.dp).background(accentColor))
            Column(modifier = Modifier.padding(16.dp)) {
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(4.dp))
                Text(desc, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 3)
            }
        }
    }
}

@Composable
fun SearchInput(
    value: String,
    placeholder: String,
    icon: ImageVector,
    onValueChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF1F5F9), // Subtle light gray background
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = Color(0xFF1E293B), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = Color(0xFF94A3B8),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF1E293B)),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    cursorBrush = SolidColor(Color(0xFF3B82F6))
                )
            }
        }
    }
}

private fun colorStringToInt(hex: String): Int {
    val cleanHex = hex.removePrefix("#")
    val longVal = try { cleanHex.toLong(16) } catch(e: Exception) { 0xFF3B82F6L }
    return if (cleanHex.length == 6) {
        (0xFF000000 or longVal).toInt()
    } else {
        longVal.toInt()
    }
}

@Composable
fun WideFeatureCard(
    title: String,
    subtitle: String,
    imageRes: DrawableResource,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(130.dp)
                    .fillMaxHeight()
            ) {
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 2
                )
            }
            
            Icon(
                Icons.Default.ChevronRight,
                null,
                tint = Color(0xFF3B82F6),
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    }
}

@Composable
fun InfoSquareCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    imageRes: DrawableResource? = null,
    backgroundColor: Color,
    titleColor: Color = Color.DarkGray,
    subtitleColor: Color = Color.Gray,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.aspectRatio(1f).clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (imageRes != null) {
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                                startY = 100f
                            )
                        )
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
                if (subtitle.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = subtitleColor
                    )
                }
            }
        }
    }
}
@Composable
fun SuggestionsOverlay(
    sectorSuggestions: List<Sector>,
    companySuggestions: List<B2BCompany>,
    onSectorClick: (Sector) -> Unit,
    onCompanyClick: (B2BCompany) -> Unit
) {
    if (sectorSuggestions.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column {
                sectorSuggestions.forEach { sector ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onSectorClick(sector) }.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Category, null, tint = Color(0xFF3B82F6), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("${sector.groupNo} - ${sector.name}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }

    if (companySuggestions.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column {
                companySuggestions.forEach { company ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onCompanyClick(company) }.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Business, null, tint = Color(0xFF3B82F6), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(company.name, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun FooterSection(isWeb: Boolean, horizontalPadding: androidx.compose.ui.unit.Dp) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
            .padding(bottom = 48.dp, top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(Res.string.footer_title),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.footer_info),
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.5f),
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
    }
}

@Composable
fun LanguageFlagBar(
    modifier: Modifier = Modifier,
    onLanguageClick: (String) -> Unit
) {
    val languages = listOf(
        "en" to "ğŸ‡ºğŸ‡¸ EN",
        "ru" to "ğŸ‡·ğŸ‡º RU",
        "ar" to "ğŸ‡¸ğŸ‡¦ AR",
        "de" to "ğŸ‡©ğŸ‡ª DE",
        "zh-rCN" to "ğŸ‡¨ğŸ‡³ ZH"
    )

    var currentLang by remember { mutableStateOf("tr") }

    LaunchedEffect(Unit) {
        currentLang = getCurrentAppLanguage() ?: "tr"
    }

    Row(
        modifier = modifier
            .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        languages.forEach { (code, label) ->
            val isSelected = currentLang == code
            Surface(
                onClick = { onLanguageClick(code) },
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) Color.White else Color.Transparent,
                shadowElevation = if (isSelected) 2.dp else 0.dp,
                modifier = Modifier
            ) {
                Text(
                    text = label,
                    color = if (isSelected) Color(0xFF3B82F6) else Color(0xFF1E293B),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )
            }
        }
    }
}
