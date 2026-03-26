package com.mgacreative.globaltrade.ui.news

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Article
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mgacreative.globaltrade.openUrl
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch

data class NewsItem(
    val title: String,
    val link: String,
    val description: String,
    val imageUrl: String?,
    val pubDate: String
)

fun parseRss(xml: String): List<NewsItem> {
    val items = mutableListOf<NewsItem>()
    val itemRegex = Regex("(?s)<item>(.*?)</item>")

    val titleRegex = Regex("(?s)<title>(?:<!\\[CDATA\\[)?(.*?)(?:\\]\\]>)?</title>")
    val linkRegex = Regex("(?s)<link>(?:<!\\[CDATA\\[)?(.*?)(?:\\]\\]>)?</link>")
    val descRegex = Regex("(?s)<description>(?:<!\\[CDATA\\[)?(.*?)(?:\\]\\]>)?</description>")
    val pubDateRegex = Regex("(?s)<pubDate>(?:<!\\[CDATA\\[)?(.*?)(?:\\]\\]>)?</pubDate>")
    val enclosureRegex = Regex("(?s)<enclosure[^>]+url=\"(.*?)\"")
    val mediaRegex = Regex("(?s)<media:content[^>]+url=\"(.*?)\"")
    val imgTagRegex = Regex("<img[^>]+src=\"(.*?)\"")

    for (match in itemRegex.findAll(xml)) {
        val block = match.groupValues[1]

        val title = titleRegex.find(block)?.groupValues?.get(1)?.trim()
            ?.replace("&amp;", "&")?.replace("&lt;", "<")?.replace("&gt;", ">")
            ?: continue

        val link = linkRegex.find(block)?.groupValues?.get(1)?.trim() ?: ""

        val rawDesc = descRegex.find(block)?.groupValues?.get(1)?.trim() ?: ""
        
        val imageUrl = enclosureRegex.find(block)?.groupValues?.get(1)?.trim()
            ?: mediaRegex.find(block)?.groupValues?.get(1)?.trim()
            ?: imgTagRegex.find(rawDesc)?.groupValues?.get(1)?.trim()

        val desc = rawDesc.replace(Regex("<[^>]+>"), "").trim()
            .replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">")

        val pubDate = pubDateRegex.find(block)?.groupValues?.get(1)?.trim()
            ?.replace("+0000", "")?.trim() ?: ""

        if (title.isNotEmpty()) {
            items.add(NewsItem(
                title = title,
                link = link,
                description = desc,
                imageUrl = imageUrl,
                pubDate = pubDate
            ))
        }
    }
    return items
}

fun String.encodeUrl(): String {
    return this.replace(":", "%3A")
        .replace("/", "%2F")
        .replace("?", "%3F")
        .replace("=", "%3D")
        .replace("&", "%26")
}

fun extractContentsFromJson(json: String): String {
    val regex = Regex("\"contents\"\\s*:\\s*\"(.*)\"", RegexOption.DOT_MATCHES_ALL)
    val match = regex.find(json)
    val content = match?.groupValues?.get(1) ?: json
    
    // Unescape JSON string
    return content.replace("\\n", "\n")
        .replace("\\t", "\t")
        .replace("\\\"", "\"")
        .replace("\\\\", "\\")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EconomicNewsScreen(
    onBack: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var newsList by remember { mutableStateOf<List<NewsItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val client = HttpClient()
                val targetUrl = "https://tr.investing.com/rss/news_285.rss"
                val proxyUrl = "https://api.allorigins.win/get?url=${targetUrl.encodeUrl()}"
                
                val response: HttpResponse = client.get(proxyUrl) {
                    headers {
                        append(HttpHeaders.UserAgent, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    }
                }
                
                val jsonResponse = response.bodyAsText()
                val xml = extractContentsFromJson(jsonResponse)
                
                newsList = parseRss(xml)
                client.close()
            } catch (e: Exception) {
                error = "Haberer yüklenemedi: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Economic News",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Geri")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF1F3F5)),
            contentAlignment = Alignment.TopCenter
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .widthIn(max = 1200.dp)
                    .fillMaxHeight()
            ) {
                val screenWidth = maxWidth
                when {
                    isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    color = Color(0xFF0F172A),
                                    strokeWidth = 3.dp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Investing.com haberleri yükleniyor...",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                    error != null -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Article,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    error ?: "",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                    newsList.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Haber bulunamadı.", color = Color.Gray)
                        }
                    }
                    else -> {
                        val columns = when {
                            screenWidth < 600.dp -> 1
                            screenWidth < 1100.dp -> 2
                            else -> 3
                        }
                        
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(columns),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(newsList) { news ->
                                NewsListCard(
                                    newsItem = news,
                                    onClick = {
                                        if (news.link.isNotEmpty()) {
                                            openUrl(news.link)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewsListCard(
    newsItem: NewsItem,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column {
            if (!newsItem.imageUrl.isNullOrBlank()) {
                KamelImage(
                    resource = asyncPainterResource(data = newsItem.imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop,
                    onLoading = { 
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { 
                            CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(24.dp)) 
                        } 
                    },
                    onFailure = { /* Resim yüklenemezse boş kalsın */ }
                )
            }
            
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF1E293B).copy(alpha = 0.05f)
                    ) {
                        Text(
                            "INVESTING",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF475569)
                        )
                    }
                    
                    Text(
                        text = newsItem.pubDate,
                        fontSize = 10.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = newsItem.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0F172A),
                    lineHeight = 22.sp
                )
                
                if (newsItem.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = newsItem.description,
                        fontSize = 13.sp,
                        color = Color(0xFF64748B),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Haberin Devamı →",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3B82F6)
                )
            }
        }
    }
}
