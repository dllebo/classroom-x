package com.classroomx

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.launch

private val AppBlue = Color(0xFFB9DDF7)
private val AppBg = Color(0xFF101114)
private val AppPanel = Color(0xFF1C1D20)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(16, 17, 20)
        window.navigationBarColor = android.graphics.Color.rgb(16, 17, 20)
        setContent { ClassroomTheme { ClassroomRoot(this) } }
    }
}

@Composable
private fun ClassroomTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = AppBlue,
            onPrimary = Color(0xFF003354),
            background = AppBg,
            surface = AppPanel,
            onSurface = Color(0xFFE3E2E6),
            onBackground = Color(0xFFE3E2E6),
            surfaceVariant = Color(0xFF292A2D)
        ),
        content = content
    )
}

@Composable
private fun ClassroomRoot(activity: MainActivity) {
    var accessToken by remember { mutableStateOf<String?>(null) }
    var courses by remember { mutableStateOf<List<ClassroomCourse>>(emptyList()) }
    var selectedCourse by remember { mutableStateOf<ClassroomCourse?>(null) }
    var coursework by remember { mutableStateOf<List<ClassroomWork>>(emptyList()) }
    var stream by remember { mutableStateOf<List<ClassroomStreamItem>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var status by remember { mutableStateOf("Войдите через Google, чтобы загрузить ваши курсы.") }
    val scope = rememberCoroutineScope()

    val authLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            runCatching {
                Identity.getAuthorizationClient(activity)
                    .getAuthorizationResultFromIntent(result.data)
            }.onSuccess { authResult ->
                accessToken = authResult.accessToken
                if (authResult.accessToken.isNullOrBlank()) {
                    error = "Google не выдал токен доступа. Повторите вход."
                } else {
                    loadCourses(authResult.accessToken) { loaded, failure ->
                        courses = loaded
                        error = failure
                        loading = false
                        status = if (failure == null) "Загружено курсов: ${loaded.size}" else "Не удалось загрузить курсы"
                    }
                }
            }.onFailure {
                error = it.message ?: "Не удалось завершить авторизацию Google."
                loading = false
            }
        } else {
            loading = false
            status = "Вход отменён."
        }
    }

    fun signIn() {
        loading = true
        error = null
        status = "Запрашиваем разрешение Google…"
        val requestedScopes = listOf(
            "https://www.googleapis.com/auth/classroom.courses.readonly",
            "https://www.googleapis.com/auth/classroom.coursework.me.readonly",
            "https://www.googleapis.com/auth/classroom.coursework.students.readonly",
            "https://www.googleapis.com/auth/classroom.courseworkmaterials.readonly",
            "https://www.googleapis.com/auth/classroom.announcements.readonly",
            "https://www.googleapis.com/auth/classroom.rosters.readonly"
        ).map(::Scope)
        val request = AuthorizationRequest.builder()
            .setRequestedScopes(requestedScopes)
            .build()
        Identity.getAuthorizationClient(activity).authorize(request)
            .addOnSuccessListener { result ->
                val token = result.accessToken
                if (!token.isNullOrBlank()) {
                    accessToken = token
                    loadCourses(token) { loaded, failure ->
                        courses = loaded
                        error = failure
                        loading = false
                        status = if (failure == null) "Загружено курсов: ${loaded.size}" else "Не удалось загрузить курсы"
                    }
                } else if (result.hasResolution() && result.pendingIntent != null) {
                    val sender = IntentSenderRequest.Builder(result.pendingIntent!!.intentSender).build()
                    authLauncher.launch(sender)
                } else {
                    loading = false
                    error = "Не удалось получить разрешение Classroom. Проверьте настройки Google OAuth."
                }
            }
            .addOnFailureListener {
                loading = false
                error = it.message ?: "Ошибка авторизации Google."
            }
    }

    Scaffold(
        containerColor = AppBg,
        topBar = {
            TopAppBar(
                title = { Text(selectedCourse?.name ?: "Classroom X", fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    if (selectedCourse != null) {
                        TextButton(onClick = {
                            selectedCourse = null
                            coursework = emptyList()
                            stream = emptyList()
                        }) { Text("Назад") }
                    }
                },
                actions = {
                    if (accessToken != null && selectedCourse == null) {
                        TextButton(onClick = {
                            accessToken = null
                            courses = emptyList()
                            error = null
                            status = "Войдите через Google, чтобы загрузить ваши курсы."
                        }) { Text("Выйти") }
                    }
                }
            )
        }
    ) { padding ->
        if (accessToken == null) {
            Column(
                Modifier.fillMaxSize().padding(padding).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Ваши курсы и задания", fontSize = 26.sp, fontWeight = FontWeight.SemiBold)
                Text("Войдите с аккаунтом Google. Classroom X запросит доступ только после вашего согласия и загрузит данные из Google Classroom.")
                Button(onClick = { signIn() }, enabled = !loading) {
                    Text(if (loading) "Подключение…" else "Войти через Google")
                }
                Text(status, color = MaterialTheme.colorScheme.onSurfaceVariant)
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                Text(
                    "Для входа разработчик приложения должен настроить OAuth-клиент Google Cloud и добавить SHA-1 сертификата приложения.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
        } else if (selectedCourse == null) {
            LazyColumn(
                Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item { Text("Ваши курсы", fontSize = 24.sp, fontWeight = FontWeight.SemiBold) }
                item { Text(status, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                error?.let { message -> item { Text(message, color = MaterialTheme.colorScheme.error) } }
                if (loading) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
                if (!loading && courses.isEmpty() && error == null) {
                    item { Text("В аккаунте не найдено активных курсов.") }
                }
                items(courses, key = { it.id }) { course ->
                    Card(
                        Modifier.fillMaxWidth().clickable {
                            selectedCourse = course
                            loading = true
                            error = null
                            val api = ClassroomApi(accessToken!!)
                            scope.launch {
                                try {
                                    val assignments = api.coursework(course.id)
                                    val materials = api.materials(course.id)
                                    val announcements = api.announcements(course.id)
                                    coursework = assignments
                                    stream = announcements + materials
                                } catch (t: Throwable) {
                                    error = t.message ?: "Не удалось загрузить содержимое курса."
                                } finally {
                                    loading = false
                                }
                            }
                        },
                        colors = CardDefaults.cardColors(containerColor = AppPanel)
                    ) {
                        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Text(course.name, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                            course.section?.let { Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                            course.room?.let { Text("Кабинет: $it", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        }
                    }
                }
                item {
                    OutlinedButton(onClick = { signIn() }, enabled = !loading) {
                        Text("Обновить курсы")
                    }
                }
            }
        } else {
            LazyColumn(
                Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                selectedCourse?.description?.let { description ->
                    item { Text(description, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
                item { Text("Задания", fontSize = 21.sp, fontWeight = FontWeight.SemiBold) }
                if (loading) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
                if (coursework.isEmpty() && !loading) item { Text("Заданий не найдено или у аккаунта нет разрешения на их просмотр.") }
                items(coursework, key = { it.id }) { work ->
                    Card(colors = CardDefaults.cardColors(containerColor = AppPanel)) {
                        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(work.title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            work.description?.let { Text(it) }
                            work.due?.let { Text("Срок: $it", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                            work.state?.let { Text("Статус: $it", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                            work.materials.forEach { attachment ->
                                TextButton(onClick = { attachment.url?.let { url ->
                                    runCatching {
                                        activity.startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url)))
                                    }
                                } }) {
                                    Text("📎 ${attachment.title} · ${attachment.type}")
                                }
                            }
                        }
                    }
                }
                item { Text("Материалы и объявления", fontSize = 21.sp, fontWeight = FontWeight.SemiBold) }
                if (stream.isEmpty() && !loading) item { Text("Материалов и объявлений не найдено.") }
                items(stream) { item ->
                    Card(colors = CardDefaults.cardColors(containerColor = AppPanel)) {
                        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(item.type, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                            Text(item.title, fontWeight = FontWeight.SemiBold)
                            item.body?.let { Text(it) }
                            item.materials.forEach { attachment ->
                                TextButton(onClick = { attachment.url?.let { url ->
                                    runCatching {
                                        activity.startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url)))
                                    }
                                } }) { Text("📎 ${attachment.title} · ${attachment.type}") }
                            }
                        }
                    }
                }
                error?.let { message -> item { Text(message, color = MaterialTheme.colorScheme.error) } }
            }
        }
    }
}

private fun loadCourses(
    token: String,
    onComplete: (List<ClassroomCourse>, String?) -> Unit
) {
    kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
        try {
            val result = ClassroomApi(token).courses()
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) { onComplete(result, null) }
        } catch (t: Throwable) {
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                onComplete(emptyList(), t.message ?: "Ошибка загрузки курсов")
            }
        }
    }
}
