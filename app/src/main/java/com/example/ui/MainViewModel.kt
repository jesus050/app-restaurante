package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.AppDatabase
import com.example.data.Reservation
import com.example.data.ReservationRepository
import com.example.data.api.GeminiContent
import com.example.data.api.GeminiPart
import com.example.data.api.GeminiRequest
import com.example.data.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class AppTab {
    HOME,
    MENU,
    RESERVAS,
    CHAT,
    PROFILE
}

data class MenuItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageUrl: String,
    val rating: Float = 5.0f
)

data class ChatMessage(
    val id: String,
    val sender: String, // "user" or "assistant"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ReservationRepository
    val reservations: StateFlow<List<Reservation>>

    // Navigation and screen state
    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userName = MutableStateFlow("Juan")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow("juan.gonzalez@gmail.com")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    // Menu list
    val menuItems = listOf(
        // Fuertes
        MenuItem(
            "1",
            "Especial Del Chef",
            "Corte premium de ribeye sazonado con sal de grano, cocinado a la perfección y acompañado con tomates cherry asados y una ramita de romero fresco.",
            38.00,
            "Fuertes",
            "https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&q=80&w=600",
            5.0f
        ),
        MenuItem(
            "2",
            "Pasta Pomodoro",
            "Spaghetti italiano gourmet con salsa marinara rústica, tomates cherry asados enteros, aceite de oliva extra virgen y hojas de albahaca fresca.",
            22.00,
            "Fuertes",
            "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?auto=format&fit=crop&q=80&w=600",
            5.0f
        ),
        MenuItem(
            "3",
            "Hamburguesa Saboré",
            "Doble carne de res premium, queso cheddar derretido, tocino crujiente, tomate, lechuga y aderezo especial de la casa en pan brioche artesanal.",
            18.00,
            "Fuertes",
            "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&q=80&w=600",
            4.0f
        ),
        // Entradas
        MenuItem(
            "4",
            "Carpaccio de Res",
            "Láminas finas de filete de res con aceite de trufa, alcaparras, láminas de queso parmesano y arúgula fresca.",
            16.00,
            "Entradas",
            "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?auto=format&fit=crop&q=80&w=600",
            4.5f
        ),
        MenuItem(
            "5",
            "Croquetas de Jamón Ibérico",
            "Croquetas crujientes rellenas de bechamel suave y trozos de auténtico jamón ibérico de bellota.",
            12.00,
            "Entradas",
            "https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?auto=format&fit=crop&q=80&w=600",
            4.8f
        ),
        // Postres
        MenuItem(
            "6",
            "Lava Cake de Chocolate",
            "Bizcocho húmedo de chocolate oscuro con corazón fundido de chocolate belga caliente, servido en plato rústico con toque de frutos rojos.",
            10.00,
            "Postres",
            "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?auto=format&fit=crop&q=80&w=600",
            5.0f
        ),
        MenuItem(
            "7",
            "Tiramisú Saboré",
            "Clásico postre italiano con bizcochos soletilla bañados en café espresso de especialidad, licor de amaretto y crema batida de mascarpone.",
            9.00,
            "Postres",
            "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?auto=format&fit=crop&q=80&w=600",
            4.7f
        ),
        // Bebidas
        MenuItem(
            "8",
            "Vino Tinto Reserva",
            "Copa de vino tinto seleccionado con notas de frutos rojos, madera y excelente cuerpo.",
            12.00,
            "Bebidas",
            "https://images.unsplash.com/photo-1510812431401-41d2bd2722f3?auto=format&fit=crop&q=80&w=600",
            4.9f
        ),
        MenuItem(
            "9",
            "Mojito de Maracuyá",
            "Refrescante combinación de ron blanco, hojas de menta maceradas, jarabe puro, refresco de limón y pulpa fresca de maracuyá.",
            10.00,
            "Bebidas",
            "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?auto=format&fit=crop&q=80&w=600",
            4.6f
        )
    )

    // Chat states
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ReservationRepository(database.reservationDao())
        reservations = repository.allReservations.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Seed default mockup reservation if empty
        viewModelScope.launch(Dispatchers.IO) {
            // Wait briefly for flow to query
            repository.allReservations.collect { list ->
                if (list.isEmpty()) {
                    repository.insert(
                        Reservation(
                            date = "24 de mayo de 2024",
                            time = "7:00 PM",
                            guests = "4 personas",
                            area = "Terraza",
                            comments = "",
                            status = "Confirmada"
                        )
                    )
                }
            }
        }

        // Initialize Chat with Mock Conversation from Screen 1
        _chatMessages.value = listOf(
            ChatMessage("m1", "assistant", "¡Hola! ¿En qué puedo ayudarte con tu reserva?"),
            ChatMessage("m2", "user", "Quiero confirmar mi mesa para el viernes."),
            ChatMessage("m3", "assistant", "¡Entendido! Veo que tienes una reserva para el viernes 24 de mayo a las 7:00 PM en Terraza. ¿Es correcto?"),
            ChatMessage("m4", "user", "Sí, es correcto."),
            ChatMessage("m5", "assistant", "¡Perfecto! Tu reserva está confirmada. ¿Necesitas algo más?")
        )
    }

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun login(email: String, name: String) {
        _userEmail.value = email.ifEmpty { "juan.gonzalez@gmail.com" }
        _userName.value = name.ifEmpty { "Juan" }
        _isLoggedIn.value = true
        _currentTab.value = AppTab.HOME
    }

    fun logout() {
        _isLoggedIn.value = false
    }

    fun insertReservation(
        date: String,
        time: String,
        guests: String,
        area: String,
        comments: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val newRes = Reservation(
                date = date,
                time = time,
                guests = guests,
                area = area,
                comments = comments,
                status = "Confirmada"
            )
            repository.insert(newRes)
        }
    }

    fun deleteReservation(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteById(id)
        }
    }

    // Interactive smart chatbot with real Gemini support or elegant fallbacks
    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val userMsg = ChatMessage(System.currentTimeMillis().toString(), "user", text)
        _chatMessages.value = _chatMessages.value + userMsg

        _isChatLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val apiKey = try {
                BuildConfig.GEMINI_API_KEY
            } catch (e: Exception) {
                ""
            }

            val responseText = if (apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY") {
                callGeminiApi(text, apiKey)
            } else {
                getRestaurantFallbackResponse(text)
            }

            withContext(Dispatchers.Main) {
                _chatMessages.value = _chatMessages.value + ChatMessage(
                    id = System.currentTimeMillis().toString(),
                    sender = "assistant",
                    text = responseText
                )
                _isChatLoading.value = false
            }
        }
    }

    private suspend fun callGeminiApi(prompt: String, apiKey: String): String {
        try {
            // Build current list of reservations to give real context to Gemini
            val activeReservations = _chatMessages.value
            val recentReservationsStr = reservations.value.joinToString("\n") { r ->
                "- Reserva para ${r.guests} el día ${r.date} a las ${r.time} en zona ${r.area}. Estado: ${r.status}."
            }

            // Create context payload
            val menuSummary = menuItems.joinToString("\n") { m ->
                "- ${m.name} (${m.category}): ${m.description} - $${m.price}"
            }

            val systemInstructionText = """
                Eres el Asistente Inteligente oficial de 'Saboré Restaurante'.
                Tu objetivo es ser refinado, elegante, extremadamente amable y servicial en español de España o Latinoamérica.
                Ayudas al usuario a gestionar, consultar o cambiar sus reservas de mesa y les informas sobre el menú.
                
                Información del cliente actual:
                - Nombre: ${_userName.value}
                - Email: ${_userEmail.value}

                Reservas del usuario registradas en el sistema actualmente:
                $recentReservationsStr

                Menú del restaurante disponible:
                $menuSummary

                Por favor, responde de manera concisa y sumamente atenta. Si te preguntan si tienen una reserva o si quieren confirmarla, menciónales los detalles específicos mostrados arriba y facilítales el proceso con profesionalidad.
            """.trimIndent()

            // Map chat message history to Gemini API format
            val historyContents = _chatMessages.value.takeLast(10).map { msg ->
                val role = if (msg.sender == "user") "user" else "model"
                GeminiContent(
                    parts = listOf(GeminiPart(text = msg.text)),
                    role = role
                )
            }

            val request = GeminiRequest(
                contents = historyContents,
                systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemInstructionText)))
            )

            val apiResponse = RetrofitClient.geminiService.generateContent(apiKey, request)
            return apiResponse.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "No pude procesar la respuesta en este momento. ¿Te puedo ayudar en algo más?"
        } catch (e: Exception) {
            return getRestaurantFallbackResponse(prompt)
        }
    }

    private fun getRestaurantFallbackResponse(prompt: String): String {
        // Smart keyword replies of Saboré is fallback representation
        val lower = prompt.lowercase()
        return when {
            lower.contains("reserva") || lower.contains("mesa") || lower.contains("confirmar") -> {
                val current = reservations.value.firstOrNull()
                if (current != null) {
                    "¡Entendido! Veo que tienes una reserva de mesa para el ${current.date} a las ${current.time} en ${current.area}. Todo se encuentra perfectamente confirmado y listo para recibirte. ¿Deseas hacer algún cambio o comentario especial?"
                } else {
                    "Con gusto te puedo ayudar a agendar una mesa. Por favor, dirígete a la pestaña de 'Reservas' o dime en qué fecha, hora y para cuántas personas te gustaría venir."
                }
            }
            lower.contains("hola") || lower.contains("buenos") || lower.contains("buenas") -> {
                "¡Hola, ${_userName.value}! Qué agrado saludarte. Soy el Asistente de Saboré. Estoy a tu servicio para organizar tu reserva, contarte recomendaciones del menú o resolver cualquier duda. ¿Qué se te apetece hoy?"
            }
            lower.contains("menu") || lower.contains("menú") || lower.contains("recomienda") || lower.contains("comer") || lower.contains("plato") || lower.contains("postre") -> {
                "Te súper recomiendo probar nuestro **Especial Del Chef** ($38.00), que consiste en una selección premium de carne de ribeye sazonada al romero. O si buscas algo dulce, no te puedes perder nuestro fundente **Lava Cake de Chocolate** ($10.00). ¿Te gustaría reservar una mesa para disfrutarlos?"
            }
            lower.contains("gracias") || lower.contains("perfe") || lower.contains("listo") -> {
                "¡Es todo un honor! En Saboré nos esmeramos por darte una experiencia inolvidable. Estaremos listos para servirte. ¿Hay algo adicional en lo que te pueda colaborar?"
            }
            else -> {
                "¡Entendido, ${_userName.value}! Tomo nota de su inquietud. Si lo desea, puedo ayudarle a gestionar una reserva o sugerirle los mejores complementos de nuestra carta de vinos. ¿Desea proceder con algo de esto?"
            }
        }
    }
}
