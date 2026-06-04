package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.ui.MenuItem

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val userName by viewModel.userName.collectAsState()
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B0C))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 80.dp) // Avoid overlap with bottom navigation
        ) {
            // Saboré Header
            HeaderRow(
                onProfileClick = { viewModel.selectTab(AppTab.PROFILE) },
                onNotificationClick = { /* No-op or notification view */ }
            )

            // Welcome Text
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Hola, $userName",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = FontFamily.SansSerif
                )
            }

            // Hero banner image card
            ChefSpecialHeroBanner(
                onReserveClick = { viewModel.selectTab(AppTab.RESERVAS) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 2x2 Quick Actions
            QuickActionsGrid(
                onReservasClick = { viewModel.selectTab(AppTab.RESERVAS) },
                onMenuClick = { viewModel.selectTab(AppTab.MENU) },
                onMisReservasClick = { viewModel.selectTab(AppTab.RESERVAS) },
                onPromotClick = { viewModel.selectTab(AppTab.RESERVAS) } // Jumps to booking where current promo reservation is highlighted!
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Recomendaciones title
            Text(
                text = "Recomendaciones",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp)
            )

            // Horizontal Recommendation Row
            val recommendations = viewModel.menuItems.take(3)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .testTag("recommendations_row")
            ) {
                items(recommendations) { item ->
                    RecommendationCard(
                        item = item,
                        onClick = { viewModel.selectTab(AppTab.MENU) }
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderRow(
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo + App Name Left
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Drawn cloche circle logo
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF1E1F22), CircleShape)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                SaboréLogoIconOnly(modifier = Modifier.fillMaxSize())
            }

            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "SABORÉ",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 2.sp,
                    lineHeight = 16.sp
                )
                Text(
                    text = "RESTAURANTE",
                    color = Color(0xFFD36113),
                    fontSize = 7.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    lineHeight = 8.sp
                )
            }
        }

        // Action Buttons Right
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onProfileClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF1E1F22), CircleShape)
                    .testTag("header_profile_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Perfil",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Box {
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF1E1F22), CircleShape)
                        .testTag("header_notif_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notificaciones",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Orange Dot
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .background(Color(0xFFD36113), CircleShape)
                        .align(Alignment.TopEnd)
                        .offset(x = (-2).dp, y = 2.dp)
                )
            }
        }
    }
}

@Composable
fun SaboréLogoIconOnly(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val logoColor = Color(0xFFD36113)

        // Bottom tray line
        val trayY = h * 0.82f
        drawLine(
            color = logoColor,
            start = androidx.compose.ui.geometry.Offset(w * 0.2f, trayY),
            end = androidx.compose.ui.geometry.Offset(w * 0.8f, trayY),
            strokeWidth = 2.dp.toPx(),
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )

        // Arc Dome
        drawArc(
            color = logoColor,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            size = androidx.compose.ui.geometry.Size(w * 0.5f, h * 0.5f * 2),
            topLeft = androidx.compose.ui.geometry.Offset(w * 0.25f, h * 0.32f)
        )

        // Dome knob top
        drawCircle(
            color = logoColor,
            radius = 2.5.dp.toPx(),
            center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.32f)
        )
    }
}

@Composable
fun ChefSpecialHeroBanner(
    onReserveClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 20.dp)
            .testTag("chef_special_banner"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1F22)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&q=80&w=600")
                    .crossfade(true)
                    .build(),
                contentDescription = "Especial del Chef Steak",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Linear Gradient overlay for darkness and text contrast
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.Black,
                                Color.Black.copy(alpha = 0.8f),
                                Color.Transparent
                            ),
                            start = androidx.compose.ui.geometry.Offset(0f, 0f),
                            end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        )
                    )
            )

            // Content Overlay
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.55f)
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Especial\nDel Chef",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                }

                Button(
                    onClick = onReserveClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD36113)
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("banner_reserve_button")
                ) {
                    Text(
                        text = "Reservar ahora",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionsGrid(
    onReservasClick: () -> Unit,
    onMenuClick: () -> Unit,
    onMisReservasClick: () -> Unit,
    onPromotClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionItem(
                title = "Reservar mesa",
                icon = Icons.Default.DateRange,
                onClick = onReservasClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag("action_reserve")
            )
            QuickActionItem(
                title = "Ver Menú",
                icon = Icons.Default.List,
                onClick = onMenuClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag("action_menu")
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionItem(
                title = "Mis reservas",
                icon = Icons.Default.ConfirmationNumber,
                onClick = onMisReservasClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag("action_my_bookings")
            )
            QuickActionItem(
                title = "Promociones",
                icon = Icons.Default.LocalOffer,
                onClick = onPromotClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag("action_promos")
            )
        }
    }
}

@Composable
fun QuickActionItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(72.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131315))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF1E1F22), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFFD36113),
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Composable
fun RecommendationCard(
    item: MenuItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .fillMaxHeight()
            .clickable { onClick() }
            .testTag("rec_card_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131315))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Food Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.2f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )

            // Rating & Title info details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = item.name,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.category,
                        color = Color(0xFF8E8E93),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Stars rating bar and price tag
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(5) { index ->
                            val isFilled = index < item.rating.toInt()
                            Icon(
                                imageVector = if (isFilled) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Star",
                                tint = if (isFilled) Color(0xFFF1C40F) else Color(0xFF48484A),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Text(
                        text = "$${String.format("%.2f", item.price)}",
                        color = Color(0xFFD36113),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
