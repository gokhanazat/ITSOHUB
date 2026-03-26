package com.mgacreative.globaltrade

import androidx.compose.material3.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mgacreative.globaltrade.ui.navigation.Screen
import com.mgacreative.globaltrade.ui.navigation.TradeBridgeNavGraph
import com.mgacreative.globaltrade.ui.theme.TradeBridgeTheme
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import org.jetbrains.compose.resources.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import itsohub.composeapp.generated.resources.*
import com.mgacreative.globaltrade.core.presentation.SnackbarManager
import com.mgacreative.globaltrade.core.presentation.SnackbarEvent
import com.mgacreative.globaltrade.core.presentation.AppSnackbarVisuals
import com.mgacreative.globaltrade.core.auth.PermissionManager
import com.mgacreative.globaltrade.ui.theme.DarkNavy
import com.mgacreative.globaltrade.ui.theme.Background
import com.mgacreative.globaltrade.manager.getCurrentAppLanguage
import com.mgacreative.globaltrade.manager.changeAppLanguage
import com.mgacreative.globaltrade.manager.syncPlatformLocale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(initialLanguage: String = "tr") {
    var currentLanguage by remember { mutableStateOf(initialLanguage) }
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val saved = getCurrentAppLanguage() ?: initialLanguage
        if (saved != currentLanguage) {
            currentLanguage = saved
        }
    }

    // Platform seviyesindeki locale'i (WasmJs Navigator) senkronize tut
    LaunchedEffect(currentLanguage) {
        syncPlatformLocale(currentLanguage)
    }

    // Note: Snackbar collection is moved inside the key(currentLanguage) block to ensure correct locale

    // Dynamic Status Bar & Toolbar Management
    val isHome = currentDestination?.route == Screen.Home.route
    val isLogin = currentDestination?.route == Screen.Login.route
    val isHelpCenter = currentDestination?.route == Screen.HelpCenter.route
    val isCompanyMeeting = currentDestination?.route == Screen.CompanyMeeting.route
    val isCompanyProfile = currentDestination?.route?.startsWith("company_profile/") == true
    val isCompanySettings = currentDestination?.route == Screen.CompanySettings.route
    val isProductManagement = currentDestination?.route == Screen.ProductManagement.route
    val isEditProduct = currentDestination?.route?.startsWith("edit_product/") == true
    val isShowroom = currentDestination?.route?.startsWith("showroom?") == true
    val isMainShowroom = currentDestination?.route == Screen.MainDigitalShowroom.route
    val isProductDetail = currentDestination?.route?.startsWith("product_detail/") == true
    val isEconomicNews = currentDestination?.route?.startsWith("economic_news") == true
    val userRole by PermissionManager.currentUserRole.collectAsState()
    
    // Dynamic Status Bar
    val statusBarColor = if (isHome || isLogin) Color.Transparent else DarkNavy
    val navigationBarColor = if (isHome || isLogin) Color.Transparent else Background
    
    SetStatusBarAndNavigationBarColor(
        statusBarColor = statusBarColor,
        navigationBarColor = navigationBarColor,
        darkIcons = false
    )

    val layoutDirection = if (currentLanguage == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr

    key(currentLanguage) {
        LaunchedEffect(currentLanguage) {
            println("App currentLanguage changed to: $currentLanguage (Initial: $initialLanguage)")
        }

        LaunchedEffect(currentLanguage) {
            SnackbarManager.events.collect { event ->
                val result = when(event) {
                    is SnackbarEvent.Message -> {
                        snackbarHostState.showSnackbar(
                            AppSnackbarVisuals(
                                message = getString(event.messageRes),
                                actionLabel = event.actionLabelRes?.let { getString(it) },
                                duration = event.duration,
                                isError = false
                            )
                        )
                    }
                    is SnackbarEvent.Error -> {
                        snackbarHostState.showSnackbar(
                            AppSnackbarVisuals(
                                message = getString(event.error.userMessage),
                                actionLabel = event.actionLabelRes?.let { getString(it) },
                                duration = SnackbarManager.getDurationForError(event.error),
                                isError = true
                            )
                        )
                    }
                }

                if (result == SnackbarResult.ActionPerformed) {
                    when (event) {
                        is SnackbarEvent.Message -> event.action?.invoke()
                        is SnackbarEvent.Error -> event.action?.invoke()
                    }
                }
            }
        }

        CompositionLocalProvider(
            LocalLayoutDirection provides layoutDirection
        ) {
            TradeBridgeTheme {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        val isAdminScreen = currentDestination?.route?.startsWith("admin_") == true || 
                                            currentDestination?.route == Screen.UserManagement.route || 
                                            currentDestination?.route == Screen.RegistryManagement.route || 
                                            currentDestination?.route == Screen.AuditLog.route ||
                                            currentDestination?.route == Screen.AdminDashboard.route
                        
                        val noGlobalTopBarScreens = isHome || isLogin || isCompanyMeeting || 
                                                   isCompanyProfile || isCompanySettings || 
                                                   isProductManagement || isEditProduct || 
                                                   isShowroom || isMainShowroom || 
                                                   isProductDetail || isEconomicNews || 
                                                   isAdminScreen

                        if (!noGlobalTopBarScreens) {
                            CenterAlignedTopAppBar(
                                title = { 
                                    Text(
                                        stringResource(currentDestination?.labelResId() ?: Res.string.app_name),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                navigationIcon = {
                                    if (navController.previousBackStackEntry != null) {
                                        IconButton(onClick = { navController.popBackStack() }) {
                                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.back))
                                        }
                                    }
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = DarkNavy,
                                    titleContentColor = Color.White,
                                    navigationIconContentColor = Color.White
                                )
                            )
                        }
                    },
                    bottomBar = {
                        val noBottomBarScreens = isLogin || isCompanyMeeting || 
                                               isCompanyProfile || isCompanySettings || 
                                               isProductManagement || isEditProduct || 
                                               isShowroom || isMainShowroom || 
                                               isProductDetail || isEconomicNews
                        
                        if (!noBottomBarScreens && userRole != null) {
                            NavigationBar(
                                containerColor = Color.White,
                                contentColor = MaterialTheme.colorScheme.primary,
                                tonalElevation = 8.dp
                            ) {
                                Screen.items.forEach { screen ->
                                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                                    NavigationBarItem(
                                        icon = { Icon(screen.icon, contentDescription = stringResource(screen.title)) },
                                        label = null,
                                        alwaysShowLabel = false,
                                        selected = selected,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().route ?: "") {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.primary,
                                            selectedTextColor = MaterialTheme.colorScheme.primary,
                                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                            unselectedIconColor = Color.Gray,
                                            unselectedTextColor = Color.Gray
                                        )
                                    )
                                }
                            }
                        }
                    }
                ) { paddingValues ->
                    TradeBridgeNavGraph(
                        navController = navController, 
                        paddingValues = paddingValues
                    )
                }
            }
        }
    }
}

@Composable
fun androidx.navigation.NavDestination.labelResId(): StringResource {
    return when (route) {
        Screen.Showroom.route -> Res.string.nav_showroom
        Screen.B2BMatch.route -> Res.string.nav_b2b
        Screen.Marketplace.route -> Res.string.nav_marketplace
        Screen.Education.route -> Res.string.nav_education
        Screen.Profile.route -> Res.string.nav_profile
        else -> Res.string.app_name
    }
}
