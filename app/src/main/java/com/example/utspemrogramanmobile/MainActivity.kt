package com.example.utspemrogramanmobile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.utspemrogramanmobile.ui.theme.UTSPemrogramanMobileTheme

// --- Data Models ---
data class MenuItem(
    val id: Int,
    val name: String,
    val price: String,
    val description: String,
    val imageRes: Int,
    val iconRes: Int, // Added for animated vector icons
    val isBestSeller: Boolean = false,
    val isSpicy: Boolean = false
)

data class RestaurantProfile(
    val name: String,
    val address: String,
    val description: String,
    val openingHours: String
)

// --- ViewModel ---
class MainViewModel(context: Context) : ViewModel() {
    private val prefs: SharedPreferences = context.getSharedPreferences("restaurant_prefs", Context.MODE_PRIVATE)

    var profile by mutableStateOf(
        RestaurantProfile(
            prefs.getString("name", "Mie Gourmet") ?: "Mie Gourmet",
            prefs.getString("address", "Jl. Pedas Berhadiah No. 66, Malang") ?: "Jl. Pedas Berhadiah No. 66, Malang",
            prefs.getString("description", "Pelopor mie pedas no. 1 di Indonesia dengan rasa yang otentik.") ?: "Pelopor mie pedas no. 1 di Indonesia dengan rasa yang otentik.",
            prefs.getString("openingHours", "11:00 - 23:00") ?: "11:00 - 23:00"
        )
    )
        private set

    var isDarkMode by mutableStateOf(prefs.getBoolean("is_dark_mode", false))
        private set

    fun updateProfile(newName: String, newAddress: String, newDesc: String, newHours: String) {
        profile = RestaurantProfile(newName, newAddress, newDesc, newHours)
        prefs.edit().apply {
            putString("name", newName)
            putString("address", newAddress)
            putString("description", newDesc)
            putString("openingHours", newHours)
            apply()
        }
    }

    fun toggleDarkMode() {
        isDarkMode = !isDarkMode
        prefs.edit().putBoolean("is_dark_mode", isDarkMode).apply()
    }
}

// --- Hardcoded Data ---
val menuList = listOf(
    MenuItem(1, "Mie Setan", "Rp 15.000", "Mie pedas dengan bumbu pilihan dan taburan ayam cincang serta pangsit goreng.", R.drawable.mie_setan, R.drawable.ic_mie_bowl, true, true),
    MenuItem(2, "Mie Iblis", "Rp 16.000", "Mie goreng pedas manis dengan kecap premium, ayam cincang, dan siomay.", R.drawable.mie_iblis, R.drawable.ic_mie_bowl, true, true),
    MenuItem(3, "Mie Sakral", "Rp 18.000", "Varian mie terbaru dengan racikan rempah rahasia yang menggugah selera.", R.drawable.mie_sakral, R.drawable.ic_mie_bowl, isSpicy = true),
    MenuItem(4, "Mie Teduh", "Rp 12.000", "Mie original tanpa rasa pedas, cocok untuk yang ingin menikmati gurihnya mie kami.", R.drawable.mie_teduh, R.drawable.ic_mie_bowl),
    MenuItem(5, "Es Genderuwo", "Rp 10.000", "Es buah segar dengan campuran sirup merah, susu, dan aneka jeli.", R.drawable.es_genderuwo, R.drawable.ic_cold_drink),
    MenuItem(6, "Es Pocong", "Rp 10.000", "Es segar dengan perasan jeruk nipis dan kelapa muda yang melegakan.", R.drawable.es_pocong, R.drawable.ic_cold_drink)
)

// --- Custom Animated Icons ---

@Composable
fun AnimatedGourmetIcon(resId: Int, modifier: Modifier = Modifier, isSpicy: Boolean = false) {
    val infiniteTransition = rememberInfiniteTransition(label = "iconAnim")
    
    // Scale animation for pulse effect
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Rotation for wiggle effect
    val rotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Icon(
            painter = painterResource(id = resId),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .graphicsLayer(
                    scaleX = if (isSpicy) scale else 1f,
                    scaleY = if (isSpicy) scale else 1f,
                    rotationZ = if (!isSpicy) rotation else 0f
                ),
            tint = if (isSpicy) Color(0xFFFF4500) else MaterialTheme.colorScheme.primary
        )
        
        if (isSpicy) {
            val fireAlpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "fireAlpha"
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_fire_spicy),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
                    .graphicsLayer(alpha = fireAlpha),
                tint = Color.Red
            )
        }
    }
}

// --- Main Activity ---
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val viewModel: MainViewModel = viewModel { MainViewModel(context) }
            
            UTSPemrogramanMobileTheme(darkTheme = viewModel.isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RestaurantApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun RestaurantApp(viewModel: MainViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home",
        enterTransition = { fadeIn() + slideInVertically { -it } },
        exitTransition = { fadeOut() + slideOutVertically { it } },
        popEnterTransition = { fadeIn() + slideInVertically { -it } },
        popExitTransition = { fadeOut() + slideOutVertically { it } }
    ) {
        composable("home") { HomeScreen(navController, viewModel) }
        composable("menu") { MenuScreen(navController) }
        composable(
            "menu_detail/{menuId}",
            arguments = listOf(navArgument("menuId") { type = NavType.IntType })
        ) { backStackEntry ->
            val menuId = backStackEntry.arguments?.getInt("menuId")
            val menuItem = menuList.find { it.id == menuId }
            if (menuItem != null) {
                DetailMenuScreen(navController, menuItem)
            }
        }
        composable("profile") { ProfileScreen(navController, viewModel) }
        composable("edit_profile") { EditProfileScreen(navController, viewModel) }
    }
}

// --- Screens ---

@Composable
fun HomeScreen(navController: NavController, viewModel: MainViewModel) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            // Header: Address Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_bran),
                    contentDescription = "Logo",
                    modifier = Modifier.size(40.dp).clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Antar ke:", fontSize = 12.sp, color = Color.Gray)
                    Text(viewModel.profile.address, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                IconButton(onClick = { navController.navigate("profile") }) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_favicon),
                        contentDescription = "Profile",
                        modifier = Modifier.size(32.dp).clip(CircleShape)
                    )
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        placeholder = { Text("Cari mie pedas favoritmu...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        ),
                        enabled = false
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box {
                            Image(
                                painter = painterResource(id = R.drawable.tampilan_menu), 
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.85f) // Gelapkan gradient agar teks lebih kontras
                                            )
                                        )
                                    )
                            )
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(16.dp)
                                    .background(
                                        Color.Black.copy(alpha = 0.3f), // Tambahkan sedikit background gelap di belakang teks
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp) // Padding tambahan di dalam background teks
                            ) {
                                Text(
                                    "PROMO MIE GOURMET", 
                                    color = Color.White, 
                                    fontWeight = FontWeight.ExtraBold, 
                                    fontSize = 20.sp,
                                    style = androidx.compose.ui.text.TextStyle(
                                        shadow = androidx.compose.ui.graphics.Shadow(
                                            color = Color.Black,
                                            offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                                            blurRadius = 4f
                                        )
                                    )
                                )
                                Text(
                                    "Pesta Pedas Diskon 50%!", 
                                    color = Color.White, 
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        HomeCategoryItem("Menu", R.drawable.ic_mie_bowl) { navController.navigate("menu") }
                        HomeCategoryItem("Promo", R.drawable.ic_fire_spicy) {}
                        HomeCategoryItem("Minuman", R.drawable.ic_cold_drink) { navController.navigate("menu") }
                        HomeCategoryItem("Profil", R.drawable.ic_account_box) { navController.navigate("profile") }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    Text("Menu Andalan Kita", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 16.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(menuList.filter { it.isBestSeller }) { item ->
                            BestSellerCard(item) { navController.navigate("menu_detail/${item.id}") }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    Text("Minuman Segar", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 16.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(menuList.filter { it.name.startsWith("Es") }) { item ->
                            BestSellerCard(item) { navController.navigate("menu_detail/${item.id}") }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Daftar Lengkap", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        TextButton(onClick = { navController.navigate("menu") }) {
                            Text("Lihat Semua", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                items(menuList.take(3)) { item ->
                    MenuListItem(item) { navController.navigate("menu_detail/${item.id}") }
                }
                
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun HomeCategoryItem(label: String, resId: Int, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Surface(
            modifier = Modifier.size(60.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            shadowElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                AnimatedGourmetIcon(resId = resId, modifier = Modifier.size(32.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BestSellerCard(item: MenuItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(180.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Box(modifier = Modifier.height(110.dp).fillMaxWidth()) {
                Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    modifier = Modifier.padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Red
                ) {
                    Text("TERLARIS", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(item.name, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(item.price, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Menu Mie", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(menuList) { item ->
                MenuListItem(item) { navController.navigate("menu_detail/${item.id}") }
            }
        }
    }
}

@Composable
fun MenuListItem(item: MenuItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(80.dp)) {
                Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                // Animated Icon Overlay
                AnimatedGourmetIcon(
                    resId = item.iconRes,
                    modifier = Modifier.align(Alignment.BottomEnd).offset(x = 4.dp, y = 4.dp).size(24.dp).background(Color.White, CircleShape).padding(2.dp),
                    isSpicy = item.isSpicy
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = item.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = item.price, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
            }
            
            IconButton(onClick = { }) {
                Icon(Icons.Default.AddCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailMenuScreen(navController: NavController, item: MenuItem) {
    var rating by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail ${item.name}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            Box(modifier = Modifier.height(300.dp).fillMaxWidth()) {
                Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f)))))
                
                // Animated Floating Icon in Detail
                AnimatedGourmetIcon(
                    resId = item.iconRes,
                    modifier = Modifier.align(Alignment.Center).size(100.dp).background(Color.White.copy(alpha = 0.2f), CircleShape).padding(16.dp),
                    isSpicy = item.isSpicy
                )

                if (item.isBestSeller) {
                    Surface(modifier = Modifier.align(Alignment.TopEnd).padding(16.dp), shape = RoundedCornerShape(8.dp), color = Color.Yellow) {
                        Text("BEST SELLER", color = Color.Black, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                    }
                }
            }
            
            Column(modifier = Modifier.padding(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = item.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                    Text(text = item.price, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = item.description, style = MaterialTheme.typography.bodyLarge, lineHeight = 26.sp)
                Spacer(modifier = Modifier.height(32.dp))
                
                Text("Level Pedas (Beri Rating):", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Row {
                    for (i in 1..5) {
                        IconButton(onClick = { rating = i }) {
                            Icon(
                                imageVector = Icons.Default.Whatshot,
                                contentDescription = null,
                                tint = if (i <= rating) Color(0xFFFF4500) else Color.LightGray,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCC00))
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("PESAN SEKARANG", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, viewModel: MainViewModel) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profil Mie Gourmet", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize().padding(24.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    ProfileDetailItem(label = "Nama Restoran", value = viewModel.profile.name, icon = Icons.Default.Store)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.Gray.copy(alpha = 0.2f))
                    ProfileDetailItem(label = "Alamat", value = viewModel.profile.address, icon = Icons.Default.LocationOn)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.Gray.copy(alpha = 0.2f))
                    ProfileDetailItem(label = "Deskripsi", value = viewModel.profile.description, icon = Icons.Default.Info)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.Gray.copy(alpha = 0.2f))
                    ProfileDetailItem(label = "Jam Buka", value = viewModel.profile.openingHours, icon = Icons.Default.Schedule)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { navController.navigate("edit_profile") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Update Info Resto")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = { viewModel.toggleDarkMode() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(if (viewModel.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (viewModel.isDarkMode) "Mode Terang" else "Mode Gelap")
            }
        }
    }
}

@Composable
fun ProfileDetailItem(label: String, value: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController, viewModel: MainViewModel) {
    var name by remember { mutableStateOf(viewModel.profile.name) }
    var address by remember { mutableStateOf(viewModel.profile.address) }
    var description by remember { mutableStateOf(viewModel.profile.description) }
    var openingHours by remember { mutableStateOf(viewModel.profile.openingHours) }

    Scaffold(topBar = { TopAppBar(title = { Text("Edit Profil", fontWeight = FontWeight.Bold) }) }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize().padding(24.dp)) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Restoran") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Alamat") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), minLines = 3)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = openingHours, onValueChange = { openingHours = it }, label = { Text("Jam Buka") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(onClick = { navController.popBackStack() }, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(12.dp)) { Text("Batal") }
                Button(onClick = {
                    viewModel.updateProfile(name, address, description, openingHours)
                    navController.popBackStack()
                }, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(12.dp)) { Text("Simpan") }
            }
        }
    }
}
