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
