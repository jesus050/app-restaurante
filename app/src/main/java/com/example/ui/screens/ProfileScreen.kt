package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val userName by viewModel.userName.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val reservations by viewModel.reservations.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editNameInput by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B0C))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Title Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp)
            ) {
                Text(
                    text = "Mi Perfil",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Profile Badge Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0xFF1E1F22), CircleShape)
                    .border(2.dp, Color(0xFFD36113), CircleShape)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.take(1).uppercase(),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD36113)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Name + Email Text
            Text(
                text = userName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = userEmail,
                fontSize = 14.sp,
                color = Color(0xFF8E8E93),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Metrics Summary Info Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MetricItem(
                    label = "Reservas",
                    value = reservations.size.toString(),
                    icon = Icons.Default.CalendarToday,
                    modifier = Modifier.weight(1f)
                )
                MetricItem(
                    label = "Estado",
                    value = "Elite VIP",
                    icon = Icons.Default.WorkspacePremium,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Menu Quick Settings Card Panel
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131315))
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    ProfileOptionRow(
                        title = "Editar Perfil",
                        icon = Icons.Default.Edit,
                        onClick = {
                            editNameInput = userName
                            showEditProfileDialog = true
                        },
                        modifier = Modifier.testTag("option_edit_profile")
                    )

                    HorizontalDivider(color = Color(0xFF1F1F22), thickness = 1.dp, modifier = Modifier.padding(horizontal = 12.dp))

                    ProfileOptionRow(
                        title = "Centro de ayuda",
                        icon = Icons.Default.HelpOutline,
                        onClick = { /* No-Op */ }
                    )

                    HorizontalDivider(color = Color(0xFF1F1F22), thickness = 1.dp, modifier = Modifier.padding(horizontal = 12.dp))

                    ProfileOptionRow(
                        title = "Configuraciones",
                        icon = Icons.Default.Settings,
                        onClick = { /* No-Op */ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Logout row button
            Button(
                onClick = { viewModel.logout() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF221111)
                ),
                border = BorderStroke(1.dp, Color(0xFF721C24)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(48.dp)
                    .testTag("logout_button")
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Cerrar Sesión",
                        tint = Color(0xFFE05C5C)
                    )
                    Text(
                        text = "Cerrar Sesión",
                        color = Color(0xFFE05C5C),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // EDIT PROFILE CUSTOM DIALOG POPUP
        if (showEditProfileDialog) {
            AlertDialog(
                onDismissRequest = { showEditProfileDialog = false },
                containerColor = Color(0xFF1E1F22),
                title = {
                    Text(
                        text = "Editar Perfil",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Por favor, ingresa tu nuevo nombre completo:",
                            color = Color(0xFFECEBE9),
                            fontSize = 14.sp
                        )
                        OutlinedTextField(
                            value = editNameInput,
                            onValueChange = { editNameInput = it },
                            placeholder = { Text("Nombre...") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFD36113),
                                unfocusedBorderColor = Color(0xFF2C2C2E),
                                focusedContainerColor = Color(0xFF131315),
                                unfocusedContainerColor = Color(0xFF131315),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("edit_profile_name_input")
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.login(userEmail, editNameInput) // update model username
                            showEditProfileDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD36113))
                    ) {
                        Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showEditProfileDialog = false }
                    ) {
                        Text("Cancelar", color = Color(0xFF8E8E93))
                    }
                }
            )
        }
    }
}

@Composable
fun MetricItem(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(84.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131315)),
        border = BorderStroke(1.dp, Color(0xFF1E1F22))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color(0xFFD36113),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = label,
                    fontSize = 12.sp,
                    color = Color(0xFF8E8E93),
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ProfileOptionRow(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF8E8E93),
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Ir",
            tint = Color(0xFF48484A),
            modifier = Modifier.size(16.dp)
        )
    }
}
