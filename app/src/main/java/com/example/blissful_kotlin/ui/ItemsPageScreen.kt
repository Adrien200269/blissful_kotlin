package com.blissfulcakes.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blissfulcakes.theme.Pink80
import com.blissfulcakes.theme.Purple80

// Sample data class for items
data class CakeItem(val name: String, val description: String, val imageRes: Int)

val cakeItems = listOf(
    CakeItem("Chocolate Cake", "Rich and creamy chocolate cake", R.drawable.cake),
    CakeItem("Vanilla Cupcake", "Classic vanilla cupcake with sprinkles", R.drawable.cupcake),
    CakeItem("Strawberry Delight", "Fresh strawberry layered cake", R.drawable.cake),
    CakeItem("Red Velvet", "Smooth red velvet cake", R.drawable.cake)
)

@Composable
fun ItemsPageScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Our Cakes", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Pink80)
            )
        },
        containerColor = Purple80
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Purple80),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(cakeItems) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { /* TODO: Show details */ },
                    colors = CardDefaults.cardColors(containerColor = Pink80),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = item.imageRes),
                            contentDescription = item.name,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
                            Text(item.description, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                }
            }
        }
    }
}
