package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateFlowOf("") }
    var password by remember { mutableStateFlowOf("") }
    var name by remember { mutableStateFlowOf("") }
    var isRegistering by remember { mutableStateOf(false) }

    // Luxury background with ambient wood-textured dark vignette
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F0E0D),
                        Color(0xFF070707)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // White rounded card
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Drawing Logo Saboré Cloche
                SaboréLogoDrawing(modifier = Modifier.size(110.dp))

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isRegistering) "Crea tu Cuenta" else "Bienvenido",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF161618),
                    fontFamily = FontFamily.SansSerif
                )

                Text(
                    text = if (isRegistering) "Regístrate para recibir recomendaciones" else "Inicia sesión para continuar",
                    fontSize = 14.sp,
                    color = Color(0xFF6E6E73),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (isRegistering) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("Nombre Completo", color = Color(0xFF9EA0A5)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD36113),
                            unfocusedBorderColor = Color(0xFFD4D4D9),
                            focusedContainerColor = Color(0xFFF5F5F7),
                            unfocusedContainerColor = Color(0xFFF5F5F7),
                            focusedTextColor = Color(0xFF1C1C1E),
                            unfocusedTextColor = Color(0xFF1C1C1E)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("name_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                // Email Input
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Email", color = Color(0xFF9EA0A5)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            tint = Color(0xFF8E8E93)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFD36113),
                        unfocusedBorderColor = Color(0xFFD4D4D9),
                        focusedContainerColor = Color(0xFFF5F5F7),
                        unfocusedContainerColor = Color(0xFFF5F5F7),
                        focusedTextColor = Color(0xFF1C1C1E),
                        unfocusedTextColor = Color(0xFF1C1C1E)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("email_input"),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )

                // Password Input
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Password", color = Color(0xFF9EA0A5)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password",
                            tint = Color(0xFF8E8E93)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFD36113),
                        unfocusedBorderColor = Color(0xFFD4D4D9),
                        focusedContainerColor = Color(0xFFF5F5F7),
                        unfocusedContainerColor = Color(0xFFF5F5F7),
                        focusedTextColor = Color(0xFF1C1C1E),
                        unfocusedTextColor = Color(0xFF1C1C1E)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("password_input"),
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Iniciar Sesión / Registrar Button
                Button(
                    onClick = {
                        viewModel.login(email, if (isRegistering) name else "Juan")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("login_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD36113)
                    ),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(
                        text = if (isRegistering) "Registrarse" else "Iniciar Sesión",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Crear Cuenta Button
                OutlinedButton(
                    onClick = {
                        isRegistering = !isRegistering
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("toggle_register_button"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFD36113)
                    ),
                    border = BorderStroke(1.5.dp, Color(0xFFD36113)),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(
                        text = if (isRegistering) "Volver al Login" else "Crear cuenta",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Guest option
                Text(
                    text = "Continuar como invitado",
                    fontSize = 14.sp,
                    color = Color(0xFF1C1C1E),
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clickable {
                            viewModel.login("invitado@sabore.com", "Invitado")
                        }
                        .testTag("guest_button")
                        .padding(8.dp)
                )
            }
        }
    }
}

// Custom function to create mutableState from Flow or standard delegate
private fun mutableStateFlowOf(init: String): MutableState<String> {
    return mutableStateOf(init)
}

@Composable
fun SaboréLogoDrawing(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Canvas(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(bottom = 6.dp)) {
            val w = size.width
            val h = size.height

            // Colors
            val logoColor = Color(0xFFD36113)

            // Draw tray line at bottom
            val trayY = h * 0.85f
            drawLine(
                color = logoColor,
                start = androidx.compose.ui.geometry.Offset(w * 0.15f, trayY),
                end = androidx.compose.ui.geometry.Offset(w * 0.85f, trayY),
                strokeWidth = 6.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )

            // Draw tray base handles or corners (tiny rounded lines under the main tray line)
            drawLine(
                color = logoColor,
                start = androidx.compose.ui.geometry.Offset(w * 0.25f, trayY + 4.dp.toPx()),
                end = androidx.compose.ui.geometry.Offset(w * 0.35f, trayY + 4.dp.toPx()),
                strokeWidth = 4.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            drawLine(
                color = logoColor,
                start = androidx.compose.ui.geometry.Offset(w * 0.65f, trayY + 4.dp.toPx()),
                end = androidx.compose.ui.geometry.Offset(w * 0.75f, trayY + 4.dp.toPx()),
                strokeWidth = 4.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )

            // Draw cloche dome
            val arcLeft = w * 0.22f
            val arcTop = h * 0.25f
            val arcRight = w * 0.78f
            val arcBottom = h * 0.85f
            val arcWidth = arcRight - arcLeft
            val arcHeight = arcBottom - arcTop

            drawArc(
                color = logoColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                size = androidx.compose.ui.geometry.Size(arcWidth, arcHeight * 2),
                topLeft = androidx.compose.ui.geometry.Offset(arcLeft, arcTop)
            )

            // Draw dome knob at top center
            drawCircle(
                color = logoColor,
                radius = 7.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(w * 0.5f, arcTop)
            )

            // Knob loop
            drawArc(
                color = logoColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                size = androidx.compose.ui.geometry.Size(12.dp.toPx(), 12.dp.toPx()),
                topLeft = androidx.compose.ui.geometry.Offset(w * 0.5f - 6.dp.toPx(), arcTop - 11.dp.toPx()),
                style = Stroke(width = 3.dp.toPx())
            )
        }

        // TEXTS
        Text(
            text = "SABORÉ",
            color = Color(0xFF161618),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            letterSpacing = 4.sp,
            lineHeight = 22.sp
        )
        Text(
            text = "RESTAURANTE",
            color = Color(0xFFD36113),
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 2.sp,
            lineHeight = 10.sp
        )
    }
}
