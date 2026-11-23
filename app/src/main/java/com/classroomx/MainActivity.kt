package com.classroomx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Class
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Ink = Color(0xFF0B0D14)
private val Panel = Color(0xFF151927)
private val PanelSoft = Color(0xFF1B2132)
private val Violet = Color(0xFF9B8CFF)
private val Mint = Color(0xFF8BE6C1)
private val Muted = Color(0xFF9BA3B8)
private val DemoAmber = Color(0xFFFFD18A)

private data class Course(
    val name: String,
    val section: String,
    val teacher: String,
    val room: String,
    val accent: Color
)

private data class Assignment(
    val course: String,
    val title: String,
    val due: String,
    val status: String,
    val accent: Color
)

private val demoCourses = listOf(
    Course("Математика", "Алгебра · 10А", "Елена Сергеевна", "Кабинет 214", Violet),
    Course("Английский язык", "English · 10А", "Mr. Johnson", "Кабинет 108", Mint),
    Course("История", "История · 10А", "Андрей Викторович", "Кабинет 302", Color(0xFFFFB86B)),
    Course("Физика", "Физика · 10А", "Мария Олеговна", "Кабинет 216", Color(0xFF8FC7FF))
)

private val demoAssignments = listOf(
    Assignment("Математика", "Квадратные уравнения", "Сегодня · 18:00", "Назначено", Violet),
    Assignment("Английский язык", "Essay: My future plans", "Завтра · 12:00", "В работе", Mint),
    Assignment("История", "Причины реформ XIX века", "Пятница · 23:59", "Назначено", Color(0xFFFFB86B)),
    Assignment("Физика", "Лабораторная работа №4", "Сдано", "На проверке", Color(0xFF8FC7FF))
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(11, 13, 20)
        window.navigationBarColor = android.graphics.Color.rgb(11, 13, 20)
        setContent { ClassroomXTheme { ClassroomXApp() } }
    }
}

@Composable
private fun ClassroomXTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Violet,
            secondary = Mint,
            background = Ink,
            surface = Panel,
            onPrimary = Ink,
            onBackground = Color(0xFFF4F5FA),
            onSurface = Color(0xFFF4F5FA)
        ),
        content = content
    )
}

@Composable
private fun ClassroomXApp() {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedCourse by remember { mutableStateOf<Course?>(null) }

    Scaffold(
        containerColor = Ink,
        bottomBar = {
            NavigationBar(containerColor = Panel, contentColor = Violet) {
                val tabs = listOf("Главная", "Курсы", "Задания", "Планер", "Ещё")
                val icons = listOf(
                    Icons.Rounded.Home,
                    Icons.Rounded.MenuBook,
                    Icons.Rounded.TaskAlt,
                    Icons.Rounded.CalendarMonth,
                    Icons.Rounded.Settings
                )
                tabs.forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = selectedTab == index && selectedCourse == null,
                        onClick = { selectedTab = index; selectedCourse = null },
                        icon = { Icon(icons[index], contentDescription = label) },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        if (selectedCourse != null) {
            CourseDetailScreen(
                course = selectedCourse!!,
                modifier = Modifier.padding(innerPadding),
                onBack = { selectedCourse = null }
            )
        } else {
            when (selectedTab) {
                0 -> HomeScreen(Modifier.padding(innerPadding), onOpenCourse = { selectedCourse = it })
                1 -> CoursesScreen(Modifier.padding(innerPadding), onOpenCourse = { selectedCourse = it })
                2 -> AssignmentsScreen(Modifier.padding(innerPadding))
                3 -> PlannerScreen(Modifier.padding(innerPadding))
                else -> MoreScreen(Modifier.padding(innerPadding))
            }
        }
    }
}

@Composable
private fun Page(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize().background(Ink).padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) { content() }
}

@Composable
private fun DemoNotice() {
    Surface(color = DemoAmber.copy(alpha = 0.12f), shape = RoundedCornerShape(12.dp)) {
        Row(Modifier.fillMaxWidth().padding(11.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("DEMO", color = DemoAmber, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
            Text("  Пример данных — Google Classroom ещё не подключён", color = DemoAmber, fontSize = 11.sp)
        }
    }
}

@Composable
private fun Header(title: String, subtitle: String? = null) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text("CLASSROOM X", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Text(title, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            if (subtitle != null) Text(subtitle, color = Muted, fontSize = 13.sp)
        }
        Surface(shape = CircleShape, color = PanelSoft, modifier = Modifier.size(44.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.NotificationsNone, contentDescription = "Уведомления", tint = Color.White)
            }
        }
    }
}

@Composable
private fun HomeScreen(modifier: Modifier, onOpenCourse: (Course) -> Unit) {
    LazyColumn(
        modifier = modifier.fillMaxSize().background(Ink),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item { Header("Главная", "Твоя учебная неделя — в одном месте") }
        item { DemoNotice() }
        item { WelcomeCard() }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryCard("На сегодня", "2", "задания", Violet, Modifier.weight(1f))
                SummaryCard("На проверке", "1", "работа", Mint, Modifier.weight(1f))
            }
        }
        item { SectionHeading("Ближайшие задания", "Все задания") }
        items(demoAssignments.take(2)) { AssignmentCard(it) }
        item { SectionHeading("Мои курсы", "Все курсы") }
        items(demoCourses.take(3)) { CourseCard(it, onClick = { onOpenCourse(it) }) }
    }
}

@Composable
private fun WelcomeCard() {
    Card(colors = CardDefaults.cardColors(containerColor = PanelSoft), shape = RoundedCornerShape(24.dp)) {
        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("ПОНЕДЕЛЬНИК, 23 НОЯБРЯ", color = Violet, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.3.sp)
            Text("Привет! Готов к продуктивному дню?", color = Color.White, fontSize = 23.sp, lineHeight = 29.sp, fontWeight = FontWeight.Bold)
            Text("Проверь сроки, новые материалы и объявления от преподавателей.", color = Muted, fontSize = 13.sp)
        }
    }
}

@Composable
private fun CoursesScreen(modifier: Modifier, onOpenCourse: (Course) -> Unit) {
    LazyColumn(
        modifier = modifier.fillMaxSize().background(Ink),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Header("Мои курсы", "Все учебные пространства") }
        item { DemoNotice() }
        items(demoCourses) { CourseCard(it, onClick = { onOpenCourse(it) }) }
    }
}

@Composable
private fun CourseDetailScreen(course: Course, modifier: Modifier, onBack: () -> Unit) {
    var section by remember { mutableIntStateOf(0) }
    Column(modifier.fillMaxSize().background(Ink).padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Spacer(Modifier.height(6.dp))
        Text("‹  Все курсы", color = Violet, modifier = Modifier.clickable { onBack() })
        Text(course.name, color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Bold)
        Text("${course.section} · ${course.teacher} · ${course.room}", color = Muted, fontSize = 13.sp)
        DemoNotice()
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Лента", "Задания", "Люди").forEachIndexed { index, label ->
                Surface(
                    color = if (section == index) Violet.copy(alpha = 0.2f) else Panel,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.clickable { section = index }
                ) {
                    Text(label, color = if (section == index) Violet else Muted, modifier = Modifier.padding(horizontal = 15.dp, vertical = 9.dp), fontSize = 13.sp)
                }
            }
        }
        when (section) {
            0 -> InfoCard("Объявление", "Добро пожаловать на курс! Здесь будут отображаться публикации преподавателя и материалы.")
            1 -> {
                val tasks = demoAssignments.filter { it.course == course.name }
                if (tasks.isEmpty()) InfoCard("Пока нет заданий", "Новые задания появятся здесь после синхронизации.")
                else tasks.forEach { AssignmentCard(it) }
            }
            else -> InfoCard("Участники курса", "После подключения Classroom API здесь появятся преподаватели и одноклассники, доступные твоему аккаунту.")
        }
    }
}

@Composable
private fun AssignmentsScreen(modifier: Modifier) {
    var filter by remember { mutableIntStateOf(0) }
    val labels = listOf("Все", "Назначено", "Сдано")
    val filtered = when (filter) {
        1 -> demoAssignments.filter { it.status == "Назначено" }
        2 -> demoAssignments.filter { it.status != "Назначено" }
        else -> demoAssignments
    }
    LazyColumn(
        modifier = modifier.fillMaxSize().background(Ink),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Header("Задания", "Сроки и состояние работ") }
        item { DemoNotice() }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                labels.forEachIndexed { index, label ->
                    Surface(color = if (filter == index) Violet.copy(alpha = 0.2f) else Panel, shape = RoundedCornerShape(18.dp), modifier = Modifier.clickable { filter = index }) {
                        Text(label, color = if (filter == index) Violet else Muted, modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp), fontSize = 12.sp)
                    }
                }
            }
        }
        items(filtered) { AssignmentCard(it) }
    }
}

@Composable
private fun PlannerScreen(modifier: Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize().background(Ink),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Header("Планер", "Твой календарь учебных дел") }
        item { DemoNotice() }
        item { InfoCard("Сегодня · 23 ноября", "18:00  ·  Математика — Квадратные уравнения") }
        item { InfoCard("Завтра · 24 ноября", "12:00  ·  Английский — Essay: My future plans") }
        item { InfoCard("Пятница · 27 ноября", "23:59  ·  История — Причины реформ XIX века") }
        item { Text("Полная синхронизация календаря появится после подключения Classroom API и, при необходимости, Google Calendar API.", color = Muted, fontSize = 12.sp) }
    }
}

@Composable
private fun MoreScreen(modifier: Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize().background(Ink),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Header("Ещё", "Настройки и инструменты") }
        item { DemoNotice() }
        item { InfoCard("X AI", "Помощник для объяснения тем, подготовки конспектов и учебных планов. Интеграция ещё не подключена.") }
        item { InfoCard("Уведомления", "Напоминания о сроках и новых публикациях. Настройки появятся после реализации уведомлений.") }
        item { InfoCard("Внешний вид", "Material 3 · Тёмная тема. Светлая тема и Liquid Glass запланированы.") }
        item { InfoCard("X Smoothness", "Автоматический запрос поддерживаемого режима обновления экрана будет добавлен отдельно.") }
        item { InfoCard("Аккаунт и безопасность", "Google OAuth, управление разрешениями и защита данных ещё не подключены.") }
        item { InfoCard("О Classroom X", "Независимый неофициальный клиент. Не связан с Google.") }
    }
}

@Composable
private fun SummaryCard(label: String, value: String, detail: String, accent: Color, modifier: Modifier = Modifier) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(label, color = Muted, fontSize = 11.sp)
            Text(value, color = accent, fontSize = 29.sp, fontWeight = FontWeight.Bold)
            Text(detail, color = Muted, fontSize = 11.sp)
        }
    }
}

@Composable
private fun SectionHeading(title: String, action: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(action, color = Violet, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun AssignmentCard(assignment: Assignment) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(19.dp)) {
        Row(Modifier.fillMaxWidth().padding(15.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(4.dp, 48.dp).clip(CircleShape).background(assignment.accent))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(assignment.course.uppercase(), color = assignment.accent, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text(assignment.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(assignment.due, color = Muted, fontSize = 11.sp)
            }
            Text(assignment.status, color = assignment.accent, fontSize = 10.sp)
        }
    }
}

@Composable
private fun CourseCard(course: Course, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = PanelSoft),
        shape = RoundedCornerShape(19.dp)
    ) {
        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(13.dp)) {
            Surface(color = course.accent.copy(alpha = 0.16f), shape = RoundedCornerShape(14.dp)) {
                Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Class, contentDescription = null, tint = course.accent)
                }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(course.name, color = Color.White, fontWeight = FontWeight.SemiBold)
                Text(course.section + " · " + course.teacher, color = Muted, fontSize = 11.sp)
            }
            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = course.accent.copy(alpha = 0.8f))
        }
    }
}

@Composable
private fun InfoCard(title: String, body: String) {
    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, color = Color.White, fontWeight = FontWeight.SemiBold)
            Text(body, color = Muted, fontSize = 13.sp, lineHeight = 19.sp)
        }
    }
}
