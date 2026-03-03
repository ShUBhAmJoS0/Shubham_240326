package com.example.shubham_240326

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shubham_240326.repository.UserRepo
import com.example.shubham_240326.repository.UserRepoImpl
import com.example.shubham_240326.ui.theme.Shubham_240326Theme
import kotlinx.coroutines.launch

data class DashboardItem(
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val imageRes: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    userRepo: UserRepo = UserRepoImpl()
) {
    var userName by remember { mutableStateOf("User") }
    var userEmail by remember { mutableStateOf("") }
    var currentTab by remember { mutableStateOf("home") }
    var favorites by remember { mutableStateOf<List<DashboardItem>>(emptyList()) }
    var cartItems by remember { mutableStateOf<List<DashboardItem>>(emptyList()) }
    val currentUser = userRepo.getCurrentUser()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        currentUser?.let { user ->
            userRepo.getUserData(user.uid) { userModel ->
                userModel?.let { 
                    userName = it.name
                    userEmail = it.email
                }
            }
            userRepo.getFavorites(user.uid) { favs ->
                favorites = favs
            }
            userRepo.getCart(user.uid) { items ->
                cartItems = items
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            BottomNavigationBar(
                currentTab = currentTab,
                onTabSelected = { currentTab = it }
            )
        }
    ) { paddingValues ->
        Box(modifier = modifier.padding(paddingValues)) {
            when (currentTab) {
                "home" -> HomeScreen(
                    userName = userName,
                    favorites = favorites,
                    cartItems = cartItems,
                    onToggleFavorite = { item ->
                        currentUser?.let { user ->
                            if (favorites.any { it.name == item.name }) {
                                userRepo.removeFromFavorites(user.uid, item.name) { success ->
                                    if (success) favorites = favorites.filter { it.name != item.name }
                                }
                            } else {
                                userRepo.addToFavorites(user.uid, item) { success ->
                                    if (success) favorites = favorites + item
                                }
                            }
                        }
                    },
                    onAddToCart = { item ->
                        currentUser?.let { user ->
                            if (!cartItems.any { it.name == item.name }) {
                                userRepo.addToCart(user.uid, item) { success ->
                                    if (success) {
                                        cartItems = cartItems + item
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Added to cart")
                                        }
                                    }
                                }
                            }
                        }
                    }
                )
                "favorites" -> FavoritesScreen(
                    favorites = favorites,
                    onRemoveFavorite = { itemName ->
                        currentUser?.let { user ->
                            userRepo.removeFromFavorites(user.uid, itemName) { success ->
                                if (success) favorites = favorites.filter { it.name != itemName }
                            }
                        }
                    }
                )
                "cart" -> CartScreen(
                    cartItems = cartItems,
                    onRemoveFromCart = { itemName ->
                        currentUser?.let { user ->
                            userRepo.removeFromCart(user.uid, itemName) { success ->
                                if (success) cartItems = cartItems.filter { it.name != itemName }
                            }
                        }
                    }
                )
                "profile" -> ProfileScreen(
                    userName = userName,
                    userEmail = userEmail,
                    onUpdateName = { newName ->
                        currentUser?.let { user ->
                            userRepo.updateUserName(user.uid, newName) { success ->
                                if (success) {
                                    userName = newName
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Username updated")
                                    }
                                }
                            }
                        }
                    }
                )
                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Coming Soon: $currentTab")
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    userName: String,
    favorites: List<DashboardItem>,
    cartItems: List<DashboardItem>,
    onToggleFavorite: (DashboardItem) -> Unit,
    onAddToCart: (DashboardItem) -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Header(userName = userName)
        Spacer(modifier = Modifier.height(24.dp))
        PopularToday(
            favorites = favorites,
            cartItems = cartItems,
            onToggleFavorite = onToggleFavorite,
            onAddToCart = onAddToCart
        )
    }
}

@Composable
fun FavoritesScreen(
    favorites: List<DashboardItem>,
    onRemoveFavorite: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Your Favorites", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        if (favorites.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No favorites yet!")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(favorites) { item ->
                    FavoriteItemCard(item, onRemoveFavorite)
                }
            }
        }
    }
}

@Composable
fun CartScreen(
    cartItems: List<DashboardItem>,
    onRemoveFromCart: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Your Cart", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        if (cartItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Your cart is empty!")
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(cartItems) { item ->
                    CartItemCard(item, onRemoveFromCart)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* Handle Checkout */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Checkout")
            }
        }
    }
}

@Composable
fun ProfileScreen(
    userName: String,
    userEmail: String,
    onUpdateName: (String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(userName) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Profile", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (isEditing) {
            OutlinedTextField(
                value = editedName,
                onValueChange = { editedName = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    onUpdateName(editedName)
                    isEditing = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }
            TextButton(onClick = { 
                isEditing = false 
                editedName = userName
            }) {
                Text("Cancel")
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = userName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                IconButton(onClick = { isEditing = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Name", modifier = Modifier.size(20.dp))
                }
            }
            Text(text = userEmail, color = Color.Gray)
        }
    }
}

@Composable
fun FavoriteItemCard(item: DashboardItem, onRemove: (String) -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imagePainter = if (item.imageRes != 0) {
                painterResource(id = item.imageRes)
            } else {
                painterResource(id = R.drawable.ic_launcher_background)
            }
            
            Image(
                painter = imagePainter,
                contentDescription = item.name,
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, fontWeight = FontWeight.Bold)
                Text(text = item.price, color = Color.Gray)
            }
            IconButton(onClick = { onRemove(item.name) }) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red)
            }
        }
    }
}

@Composable
fun CartItemCard(item: DashboardItem, onRemove: (String) -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imagePainter = if (item.imageRes != 0) {
                painterResource(id = item.imageRes)
            } else {
                painterResource(id = R.drawable.ic_launcher_background)
            }

            Image(
                painter = imagePainter,
                contentDescription = item.name,
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, fontWeight = FontWeight.Bold)
                Text(text = item.price, color = Color.Gray)
            }
            IconButton(onClick = { onRemove(item.name) }) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red)
            }
        }
    }
}

@Composable
fun Header(userName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "Profile",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = "Welcome back,", fontSize = 12.sp, color = Color.Gray)
                Text(text = userName, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PopularToday(
    favorites: List<DashboardItem>,
    cartItems: List<DashboardItem>,
    onToggleFavorite: (DashboardItem) -> Unit,
    onAddToCart: (DashboardItem) -> Unit
) {
    val items = listOf(
        DashboardItem("Coffee", "Rich & Aromatic", "$4.50", R.drawable.coffee),
        DashboardItem("Cake", "Sweet & Fluffy", "$5.20", R.drawable.cake),
        DashboardItem("Donut", "Glazed & Tasty", "$3.00", R.drawable.donut),
        DashboardItem("Tea", "Fresh & Calming", "$3.50", R.drawable.tea)
    )

    Column {
        Text("Popular Today", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        
        val rows = items.chunked(2)
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowItems.forEach { item ->
                    val isFavorite = favorites.any { it.name == item.name }
                    val isInCart = cartItems.any { it.name == item.name }
                    ProductItem(
                        item = item,
                        isFavorite = isFavorite,
                        isInCart = isInCart,
                        onToggleFavorite = { onToggleFavorite(item) },
                        onAddToCart = { onAddToCart(item) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ProductItem(
    item: DashboardItem,
    isFavorite: Boolean,
    isInCart: Boolean,
    onToggleFavorite: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box {
                val imagePainter = if (item.imageRes != 0) {
                    painterResource(id = item.imageRes)
                } else {
                    painterResource(id = R.drawable.ic_launcher_background)
                }

                Image(
                    painter = imagePainter,
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(28.dp)
                        .background(Color.White.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = item.name, fontWeight = FontWeight.Bold)
            Text(text = item.description, fontSize = 12.sp, color = Color.Gray, maxLines = 1)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = item.price, fontWeight = FontWeight.Bold)
                IconButton(
                    onClick = onAddToCart,
                    modifier = Modifier
                        .size(32.dp)
                        .background(if (isInCart) Color(0xFF4CAF50) else Color.Black, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isInCart) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = if (isInCart) "Added to Cart" else "Add to Cart",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(currentTab: String, onTabSelected: (String) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentTab == "home",
            onClick = { onTabSelected("home") }
        )
        NavigationBarItem(
            icon = { Icon(if (currentTab == "favorites") Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = "Favorites") },
            label = { Text("Favorites") },
            selected = currentTab == "favorites",
            onClick = { onTabSelected("favorites") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart") },
            label = { Text("Cart") },
            selected = currentTab == "cart",
            onClick = { onTabSelected("cart") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentTab == "profile",
            onClick = { onTabSelected("profile") }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    Shubham_240326Theme {
        DashboardScreen()
    }
}
