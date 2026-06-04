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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Reservation
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val reservations by viewModel.reservations.collectAsState()

    // Form inputs state
    var selectedDay by remember { mutableIntStateOf(24) }
    var selectedMonthName by remember { mutableStateOf("mayo 2024") }
    var selectedTime by remember { mutableStateOf("7:00 PM") }
    var selectedGuests by remember { mutableStateOf("4 personas") }
    var selectedArea by remember { mutableStateOf("Terraza") }
    var comments by remember { mutableStateOf("") }
    
    // Dropdowns open state
    var isGuestsDropdownExpanded by remember { mutableStateOf(false) }
    var isAreaDropdownExpanded by remember { mutableStateOf(false) }

    // Dialog state
    var showConfirmationDialog by remember { mutableStateOf(false) }

    val guestsOptions = listOf("1 persona", "2 personas", "3 personas", "4 personas", "6 personas", "8 personas", "10 personas")
    val areaOptions = listOf("Terraza", "Salón Principal", "Zona Vip", "Jardín", "Bar")
    val timesList = listOf("6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM", "8:00 PM", "8:30 PM")

    val reservationDateString = "$selectedDay de $selectedMonthName"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B0C))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 90.dp) // Leave screen space for Confirm button
        ) {
            // Header of Calendar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Reservar mesa",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Selecciona fecha, hora e indicaciones",
                    fontSize = 13.sp,
                    color = Color(0xFF8E8E93)
                )
            }

            // Calendar Container Card exactly resembling Screenshot 3
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131315))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    // Month title
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mayo 2024",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Mes anterior",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Mes siguiente",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Calendar Week Header
                    val weekDays = listOf("Lu", "Ma", "Mi", "Ju", "Vi", "Sá", "Do")
                    Row(modifier = Modifier.fillMaxWidth()) {
                        weekDays.forEach { dayName ->
                            Text(
                                text = dayName,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                color = Color(0xFF6E6E73),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Calendar numbers grid resembling May 2024
                    // In Screen 3: Row 1 has [29, 30, 1, 2, 3, 4, 5]
                    // Row 2: [6, 7, 8, 9, 10, 11, 12]
                    // Row 3: [13, 14, 15, 16, 17, 18, 19]
                    // Row 4: [20, 21, 22, 23, 24, 25, 26]
                    // Row 5: [27, 28, 29, 30, 31, 1, 2]
                    val calendarGrid = listOf(
                        listOf(29, 30, 1, 2, 3, 4, 5),
                        listOf(6, 7, 8, 9, 10, 11, 12),
                        listOf(13, 14, 15, 16, 17, 18, 19),
                        listOf(20, 21, 22, 23, 24, 25, 26),
                        listOf(27, 28, 29, 30, 31, 1, 2)
                    )

                    calendarGrid.forEachIndexed { rowIndex, row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            row.forEach { dayNumber ->
                                // Decide if the day belongs to current month or previous/subsequent
                                val isCurrentMonthDay = when {
                                    rowIndex == 0 && dayNumber > 20 -> false
                                    rowIndex == 4 && dayNumber < 10 -> false
                                    else -> true
                                }

                                val isSelected = isCurrentMonthDay && dayNumber == selectedDay

                                val textColor = when {
                                    isSelected -> Color.White
                                    isCurrentMonthDay -> Color.White
                                    else -> Color(0xFF48484A) // Muted gray
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) Color(0xFFD36113) else Color.Transparent
                                        )
                                        .clickable {
                                            if (isCurrentMonthDay) {
                                                selectedDay = dayNumber
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dayNumber.toString(),
                                        color = textColor,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Time Selector (Hora)
            Text(
                text = "Hora",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 8.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("time_row")
            ) {
                items(timesList) { time ->
                    val isSelected = time == selectedTime
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) Color(0xFFD36113) else Color(0xFF131315)
                            )
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFFD36113) else Color(0xFF2C2C2E),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedTime = time }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = time,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dropdowns (Personas and Area)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Guests Dropdown
                Text(
                    text = "Personas",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF131315), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFF2C2C2E), RoundedCornerShape(12.dp))
                            .clickable { isGuestsDropdownExpanded = true }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = selectedGuests, color = Color.White, fontSize = 14.sp)
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Expand Guest Dropdown",
                            tint = Color.White
                        )
                    }

                    DropdownMenu(
                        expanded = isGuestsDropdownExpanded,
                        onDismissRequest = { isGuestsDropdownExpanded = false },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .background(Color(0xFF1E1F22))
                    ) {
                        guestsOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(text = option, color = Color.White) },
                                onClick = {
                                    selectedGuests = option
                                    isGuestsDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Area Dropdown
                Text(
                    text = "Área",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF131315), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFF2C2C2E), RoundedCornerShape(12.dp))
                            .clickable { isAreaDropdownExpanded = true }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = selectedArea, color = Color.White, fontSize = 14.sp)
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Expand Area Dropdown",
                            tint = Color.White
                        )
                    }

                    DropdownMenu(
                        expanded = isAreaDropdownExpanded,
                        onDismissRequest = { isAreaDropdownExpanded = false },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .background(Color(0xFF1E1F22))
                    ) {
                        areaOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(text = option, color = Color.White) },
                                onClick = {
                                    selectedArea = option
                                    isAreaDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Comments (Comentarios)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "Comentarios (opcional)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = comments,
                    onValueChange = { comments = it },
                    placeholder = { Text("Alguna solicitud especial...", color = Color(0xFF6E6E73)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFD36113),
                        unfocusedBorderColor = Color(0xFF2C2C2E),
                        focusedContainerColor = Color(0xFF131315),
                        unfocusedContainerColor = Color(0xFF131315),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("comments_input")
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tu Reserva Pre-Confirmation Summary Card exactly as in Screenshot 3
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "Lista de reservas",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Render each reservation (preloaded standard and any new ones saved to Room database represent high fidelity!)
                reservations.forEach { res ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(Color(0xFF131315), RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0xFF2C2C2E), RoundedCornerShape(16.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Rounded brown calendar icon container
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(Color(0xFF22150C), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Calendario",
                                    tint = Color(0xFFD36113),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Texts
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "Tu reserva",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "${res.date} • ${res.time} • ${res.guests}",
                                    color = Color(0xFF8E8E93),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = res.area,
                                    color = Color(0xFF8E8E93),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Green Pill Status Badge
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF0D2818), RoundedCornerShape(6.dp))
                                    .border(1.dp, Color(0xFF1DB954), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Confirmada",
                                    fontSize = 11.sp,
                                    color = Color(0xFF1DB954),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }

        // ABSOLUTE BOTTOM BAR "Confirmar reserva" fixed action button matching height safe zone
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0xFF0B0B0C))
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .navigationBarsPadding() // Notch and Bottom Safe Area rule
        ) {
            Button(
                onClick = {
                    viewModel.insertReservation(
                        date = reservationDateString,
                        time = selectedTime,
                        guests = selectedGuests,
                        area = selectedArea,
                        comments = comments
                    )
                    // Reset comments
                    comments = ""
                    showConfirmationDialog = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD36113)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("confirm_booking_button")
            ) {
                Text(
                    text = "Confirmar reserva",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // GORGEOUS POPUP DIALOG ON SUCCESS
        if (showConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmationDialog = false },
                containerColor = Color(0xFF1E1F22),
                icon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Confirmado",
                        tint = Color(0xFF1DB954),
                        modifier = Modifier.size(50.dp)
                    )
                },
                title = {
                    Text(
                        text = "¡Mesa Reservada!",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Text(
                        text = "Tu reserva para el $reservationDateString a las $selectedTime ha sido agendada con éxito.\n¡Te esperamos en Saboré!",
                        color = Color(0xFFECEBE9),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { showConfirmationDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD36113))
                    ) {
                        Text("Excelente", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}
