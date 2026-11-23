package com.classroomx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private val Blue = Color(0xFFB9DDF7)
private val Bg = Color(0xFF101114)
private val Panel = Color(0xFF1C1D20)
private val PanelHigh = Color(0xFF292A2D)
private val Muted = Color(0xFFBFC1C6)

private data class Course(val name: String, val teacher: String, val color: Color, val initial: String)
private data class Post(val title: String, val date: String)
private data class Notice(val title: String, val detail: String, val time: String, val initial: String)

private val courses = listOf(
    Course("Erdkunde 8D", "Herr Jirzik", Color(0xFF1755A5), "E"),
    Course("Biologie live", "Frau Mai-Gebhardt", Color(0xFF55575A), "B"),
    Course("Kunst 8d", "Herr Rychlik", Color(0xFF55575A), "K"),
    Course("WiPo 8D - 26/27", "Herr Lange", Color(0xFF1755A5), "W"),
    Course("AL 8d", "", Color(0xFF55575A), "A"),
    Course("Englisch 8d", "Frau Siebigteroth", Color(0xFF1755A5), "E"),
    Course("Deutsch 8d", "", Color(0xFF1755A5), "D"),
    Course("Biologie 8D", "", Color(0xFF55575A), "B"),
    Course("Kunst 7d", "", Color(0xFF55575A), "K"),
    Course("25/26 - 7d - GL", "", Color(0xFFB65B00), "2"),
    Course("6D GL", "", Color(0xFF008A9A), "6")
)
private val posts = listOf(
    Post("3. Das Gradnetz der Erde – Zeitverschiebung und Zeitzonen", "4 нояб."),
    Post("2. Der Mensch hebt ab", "23 сент."),
    Post("1. Die Erforschung der Tiefsee", "16 сент.")
)
private val notices = listOf(
    Notice("Новые объявления от пользователя Frau De...", "AL 8d · Подробнее", "19:58", "F"),
    Notice("Преподаватель Frau Stöcker вернул вашу работу", "2_Meine Bearbeitung zum Layout von Hornbach", "19:36", "F"),
    Notice("Преподаватель Frau Stöcker вернул вашу работу", "1.6_(LB) Werbung kritisch hinterfragen", "19:20", "F"),
    Notice("Преподаватель Frau Siebigteroth: новое задание", "2.3 Grammar: The past progressive", "16:07", "S"),
    Notice("Преподаватель Frau Siebigteroth: новое задание", "2.2 Kisha’s experience - racism", "16:05", "S")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(16, 17, 20)
        window.navigationBarColor = android.graphics.Color.rgb(16, 17, 20)
        setContent { ClassroomTheme { ClassroomApp() } }
    }
}

@Composable
private fun ClassroomTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Blue, onPrimary = Color(0xFF003354),
            secondary = Color(0xFFB5C9E8), background = Bg,
            surface = Panel, surfaceVariant = PanelHigh,
            onBackground = Color(0xFFE3E2E6), onSurface = Color(0xFFE3E2E6),
            onSurfaceVariant = Muted
        ),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClassroomApp() {
    var page by remember { mutableIntStateOf(0) }
    var course by remember { mutableStateOf<Course?>(null) }
    var courseTab by remember { mutableIntStateOf(0) }
    var settings by remember { mutableStateOf(false) }
    var help by remember { mutableStateOf(false) }
    var mobileSync by remember { mutableStateOf(false) }
    var theme by remember { mutableStateOf("Системная") }
    var themeDialog by remember { mutableStateOf(false) }
    val drawer = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawer,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = Panel) {
                Column(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 20.dp)) {
                    Text("Google Класс", fontSize = 25.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(12.dp))
                    DrawerEntry("Курсы", Icons.Rounded.Home, page == 0 && !settings) {
                        page = 0; course = null; settings = false; scope.launch { drawer.close() }
                    }
                    DrawerEntry("Календарь", Icons.Rounded.CalendarMonth, page == 3) {
                        page = 3; course = null; settings = false; scope.launch { drawer.close() }
                    }
                    DrawerEntry("Уведомления", Icons.Rounded.Notifications, page == 2) {
                        page = 2; course = null; settings = false; scope.launch { drawer.close() }
                    }
                    Spacer(Modifier.height(18.dp))
                    Text("Аккаунт зарегистрирован", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(12.dp))
                    DrawerEntry("Список заданий", Icons.Rounded.Checklist, page == 1) {
                        page = 1; course = null; settings = false; scope.launch { drawer.close() }
                    }
                    courses.forEach { item ->
                        Row(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp))
                                .clickable { course = item; courseTab = 0; settings = false; scope.launch { drawer.close() } }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Avatar(item.initial, item.color, 36)
                            Text(item.name, fontSize = 14.sp, maxLines = 1)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = Muted.copy(alpha = 0.2f))
                    DrawerEntry("Офлайн-файлы", Icons.Rounded.OfflinePin, false) { page = 4; course = null; scope.launch { drawer.close() } }
                    DrawerEntry("Архив курсов", Icons.Rounded.Archive, false) { page = 5; course = null; scope.launch { drawer.close() } }
                    DrawerEntry("Папки Класса", Icons.Rounded.Folder, false) { page = 6; course = null; scope.launch { drawer.close() } }
                    DrawerEntry("Настройки", Icons.Rounded.Settings, settings) {
                        settings = true; course = null; scope.launch { drawer.close() }
                    }
                    DrawerEntry("Справка", Icons.Rounded.HelpOutline, false) { help = true; scope.launch { drawer.close() } }
                }
            }
        }
    ) {
        Scaffold(
            containerColor = Bg,
            topBar = {
                TopAppBar(
                    title = {
                        Text(when {
                            settings -> "Настройки"
                            course != null -> course!!.name
                            page == 0 -> "Google Класс"
                            page == 1 -> "Список заданий"
                            page == 2 -> "Уведомления"
                            page == 3 -> "Календарь"
                            else -> "Classroom X"
                        }, fontWeight = FontWeight.Medium)
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (course != null) course = null
                            else if (settings) settings = false
                            else scope.launch { drawer.open() }
                        }) {
                            Icon(if (course != null || settings) Icons.AutoMirrored.Rounded.ArrowBack else Icons.Rounded.Menu, contentDescription = "Навигация")
                        }
                    },
                    actions = {
                        if (!settings) IconButton(onClick = { page = 2; course = null }) {
                            Icon(Icons.Rounded.NotificationsNone, contentDescription = "Уведомления")
                        } else IconButton(onClick = { help = true }) {
                            Icon(Icons.Rounded.MoreVert, contentDescription = "О приложении")
                        }
                    }
                )
            },
            bottomBar = {
                if (course != null) {
                    NavigationBar(containerColor = Panel) {
                        listOf("Лента", "Задания", "Пользователи").forEachIndexed { i, label ->
                            NavigationBarItem(
                                selected = courseTab == i, onClick = { courseTab = i },
                                icon = { Icon(if (i == 0) Icons.Rounded.Forum else if (i == 1) Icons.Rounded.Checklist else Icons.Rounded.People, contentDescription = label) },
                                label = { Text(label) }
                            )
                        }
                    }
                } else if (!settings) {
                    NavigationBar(containerColor = Panel) {
                        val labels = listOf("Курсы", "Задания", "Уведомления")
                        val icons = listOf(Icons.Rounded.Class, Icons.Rounded.Assignment, Icons.Rounded.Notifications)
                        val current = if (page in 0..2) page else 0
                        labels.forEachIndexed { i, label ->
                            NavigationBarItem(selected = current == i, onClick = { page = i; course = null },
                                icon = { Icon(icons[i], contentDescription = label) }, label = { Text(label) })
                        }
                    }
                }
            },
            floatingActionButton = {
                if (page == 0 && course == null && !settings) {
                    FloatingActionButton(onClick = { help = true }, containerColor = Blue, contentColor = Color(0xFF003354)) {
                        Icon(Icons.Rounded.Add, contentDescription = "Создать")
                    }
                }
            }
        ) { padding ->
            when {
                settings -> SettingsScreen(Modifier.padding(padding), mobileSync, { mobileSync = it }, theme, { themeDialog = true })
                course != null -> CourseScreen(course!!, courseTab, Modifier.padding(padding))
                page == 0 -> CoursesScreen(Modifier.padding(padding)) { course = it; courseTab = 0 }
                page == 1 -> TasksScreen(Modifier.padding(padding))
                page == 2 -> NotificationsScreen(Modifier.padding(padding))
                page == 3 -> CalendarScreen(Modifier.padding(padding))
                else -> UtilityScreen(Modifier.padding(padding), page)
            }
        }
    }

    if (themeDialog) {
        AlertDialog(
            onDismissRequest = { themeDialog = false },
            title = { Text("Выбрать тему") },
            text = {
                Column {
                    listOf("Системная", "Светлая", "Тёмная").forEach { choice ->
                        NavigationDrawerItem(
                            label = { Text(choice) }, selected = theme == choice,
                            onClick = { theme = choice; themeDialog = false }
                        )
                    }
                }
            },
            confirmButton = {}
        )
    }
    if (help) {
        AlertDialog(
            onDismissRequest = { help = false },
            title = { Text("Classroom X") },
            text = { Text("Интерфейсный прототип на Material 3. Вход в Google и синхронизация с Classroom API ещё не подключены.") },
            confirmButton = { TextButton(onClick = { help = false }) { Text("Понятно") } }
        )
    }
}

@Composable
private fun DrawerEntry(label: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(label, maxLines = 1) }, icon = { Icon(icon, contentDescription = null) },
        selected = selected, onClick = onClick, modifier = Modifier.padding(vertical = 2.dp)
    )
}

@Composable
private fun Avatar(initial: String, color: Color, size: Int = 42) {
    Surface(color = color, shape = CircleShape, modifier = Modifier.size(size.dp)) {
        Box(contentAlignment = Alignment.Center) { Text(initial, color = Color.White, fontSize = 15.sp) }
    }
}

@Composable
private fun CoursesScreen(modifier: Modifier, onOpen: (Course) -> Unit) {
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) {
                Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Подходит срок", fontSize = 21.sp)
                        Text("Открыть список задач", color = Blue, fontSize = 13.sp)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(3) { Surface(Modifier.size(70.dp, 34.dp), color = PanelHigh, shape = RoundedCornerShape(12.dp)) {} }
                    }
                }
            }
        }
        items(courses) { item ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onOpen(item) },
                colors = CardDefaults.cardColors(containerColor = Panel),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column {
                    Box(Modifier.fillMaxWidth().height(156.dp).background(item.color).padding(18.dp)) {
                        Column(Modifier.align(Alignment.TopStart), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                            Text(item.name, color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Medium)
                            if (item.teacher.isNotBlank()) Text(item.teacher, color = Color.White, fontSize = 14.sp)
                        }
                        IconButton(onClick = { onOpen(item) }, modifier = Modifier.align(Alignment.TopEnd)) {
                            Icon(Icons.Rounded.MoreVert, contentDescription = "Действия курса", tint = Color.White)
                        }
                    }
                    Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(item.teacher.ifBlank { "Курс" }, color = Muted, fontSize = 13.sp)
                        Text("Открыть курс", color = Blue, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseScreen(course: Course, tab: Int, modifier: Modifier) {
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Box(Modifier.fillMaxWidth().height(176.dp).background(course.color).padding(22.dp)) {
                Column(Modifier.align(Alignment.CenterStart), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(course.name, color = Color.White, fontSize = 26.sp)
                    Text(course.teacher, color = Color.White, fontSize = 15.sp)
                }
            }
        }
        when (tab) {
            0 -> {
                item {
                    Surface(color = Blue, shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                        Text("✎   Новое объявление", color = Color(0xFF003354), modifier = Modifier.padding(16.dp), fontWeight = FontWeight.SemiBold)
                    }
                }
                if (course.name == "Erdkunde 8D") {
                    items(posts) { post ->
                        Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp), colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp)) {
                            Column {
                                Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Avatar("▣", Color(0xFF1755A5), 42)
                                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                        Text("Новый материал: " + post.title, fontSize = 15.sp, lineHeight = 21.sp)
                                        Text(post.date, color = Muted, fontSize = 12.sp)
                                    }
                                }
                                HorizontalDivider(color = Muted.copy(alpha = 0.18f))
                                Text("Добавить публичный комментарий", modifier = Modifier.fillMaxWidth().clickable {}.padding(16.dp), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                } else item { InfoCard("Лента пока пуста", "Публикации и материалы этого курса появятся здесь после синхронизации.") }
            }
            1 -> item { InfoCard("Задания курса", "Список заданий и сроки будут загружены из Google Classroom после подключения API.") }
            else -> item { InfoCard("Пользователи", "Преподаватели и ученики курса появятся здесь после синхронизации.") }
        }
    }
}

@Composable
private fun TasksScreen(modifier: Modifier) {
    var filter by remember { mutableIntStateOf(0) }
    val labels = listOf("Все", "Назначено", "Сдано")
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Список заданий", fontSize = 25.sp, fontWeight = FontWeight.Medium) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                labels.forEachIndexed { i, label -> FilterChip(selected = filter == i, onClick = { filter = i }, label = { Text(label) }) }
            }
        }
        item { InfoCard(if (filter == 2) "Сданные задания" else "Erdkunde 8D", "3. Das Gradnetz der Erde – Zeitverschiebung und Zeitzonen\nСроки и статусы загрузятся после подключения Classroom API.") }
        if (filter != 2) item { InfoCard("Englisch 8d", "2.3 Grammar: The past progressive\nНовое задание · Сегодня") }
    }
}

@Composable
private fun NotificationsScreen(modifier: Modifier) {
    var filter by remember { mutableIntStateOf(0) }
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                FilterChip(selected = filter == 0, onClick = { filter = 0 }, label = { Text("Все") })
                FilterChip(selected = filter == 1, onClick = { filter = 1 }, label = { Text("Непрочитанные") })
                Spacer(Modifier.weight(1f))
                IconButton(onClick = {}) { Icon(Icons.Rounded.Settings, contentDescription = "Настройки уведомлений") }
            }
        }
        items(notices) { notice ->
            Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Avatar(notice.initial, Color(0xFF765C57), 48)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(notice.title, fontSize = 14.sp, lineHeight = 19.sp, maxLines = 2)
                    Text(notice.detail, color = Muted, fontSize = 12.sp, maxLines = 2)
                }
                Text(notice.time, color = Muted, fontSize = 11.sp)
            }
            HorizontalDivider(color = Muted.copy(alpha = 0.12f))
        }
    }
}

@Composable
private fun CalendarScreen(modifier: Modifier) {
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("Календарь", fontSize = 25.sp, fontWeight = FontWeight.Medium) }
        item { InfoCard("Сегодня", "Сроки заданий и события из курсов будут отображаться здесь.") }
        item { InfoCard("Предстоящие события", "Для синхронизации потребуется подключение Classroom API и, при необходимости, Google Calendar API.") }
    }
}

@Composable
private fun SettingsScreen(modifier: Modifier, mobileSync: Boolean, onSync: (Boolean) -> Unit, theme: String, onTheme: () -> Unit) {
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Настройки аккаунта", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text("Измените фото, пароль, язык и другие параметры.", color = Muted, fontSize = 14.sp)
            }
        }
        item { SettingRow("Изменить имя", "Если нужно изменить имя, обратитесь к администратору.") }
        item { SettingRow("Уведомления", "Настройте уведомления для получения по электронной почте и на этом устройстве.") }
        item { SettingRow("Выбрать тему", theme, onTheme) }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Синхронизация по мобильной сети", fontSize = 15.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                Switch(checked = mobileSync, onCheckedChange = onSync)
            }
        }
        item { Text("О приложении", fontSize = 16.sp, fontWeight = FontWeight.SemiBold) }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp)) {
                Row(Modifier.fillMaxWidth().padding(18.dp), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = Color(0xFFE6EEFF), shape = CircleShape, modifier = Modifier.size(48.dp)) {
                        Box(contentAlignment = Alignment.Center) { Icon(Icons.Rounded.Science, contentDescription = null, tint = Color(0xFF164F9C)) }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text("Classroom X", fontWeight = FontWeight.SemiBold)
                        Text("Независимый клиент. Вход и синхронизация с Google Classroom пока не подключены.", color = Muted, fontSize = 12.sp, lineHeight = 17.sp)
                        Text("Версия 0.1.0 · Material 3", color = Muted, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingRow(title: String, subtitle: String, onClick: () -> Unit = {}) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable(onClick = onClick).padding(vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        Text(subtitle, color = Muted, fontSize = 14.sp, lineHeight = 20.sp)
    }
}

@Composable
private fun UtilityScreen(modifier: Modifier, page: Int) {
    val title = when (page) { 4 -> "Офлайн-файлы"; 5 -> "Архив курсов"; else -> "Папки Класса" }
    Column(modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, fontSize = 24.sp)
        InfoCard(title, "Раздел подготовлен в интерфейсе. Работа с файлами и архивом будет подключена вместе с Classroom API.")
    }
}

@Composable
private fun InfoCard(title: String, body: String) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(body, color = Muted, fontSize = 13.sp, lineHeight = 19.sp)
        }
    }
}
