package com.classroomx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(11, 13, 20)
        window.navigationBarColor = android.graphics.Color.rgb(11, 13, 20)
        setContent { ClassroomXTheme { DashboardScreen() } }
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
private fun DashboardScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = Ink,
        bottomBar = {
            NavigationBar(containerColor = Panel, contentColor = Violet) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Rounded.Home, contentDescription = "Главная") },
                    label = { Text("Главная") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Rounded.MenuBook, contentDescription = "Курсы") },
                    label = { Text("Курсы") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Rounded.AutoAwesome, contentDescription = "AI") },
                    label = { Text("X AI") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Rounded.CalendarMonth, contentDescription = "Планер") },
                    label = { Text("Планер") }
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Ink)
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { Header() }
            item { WelcomeBlock() }
            item { SummaryRow() }
            item { SectionHeading("Ближайшие задания", "Все задания") }
            item {
                AssignmentCard(
                    subject = "Математика",
                    title = "Квадратные уравнения",
                    due = "Сегодня · 18:00",
                    tag = "Скоро срок",
                    accent = Violet
                )
            }
            item {
                AssignmentCard(
                    subject = "Английский",
                    title = "Essay: My future plans",
                    due = "Завтра · 12:00",
                    tag = "В процессе",
                    accent = Mint
                )
            }
            item { SectionHeading("Мои курсы", "Открыть") }
            item { CourseCard("Математика", "12 заданий · 3 новых", "М", Violet) }
            item { CourseCard("Английский язык", "8 заданий · 1 новое", "EN", Mint) }
        }
    }
}

@Composable
private fun Header() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("CLASSROOM", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Text("X", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
        }
        Surface(
            shape = CircleShape,
            color = PanelSoft,
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.NotificationsNone, contentDescription = "Уведомления", tint = Color.White)
            }
        }
    }
}

@Composable
private fun WelcomeBlock() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Понедельник, 23 ноября", color = Muted, fontSize = 13.sp)
        Text(
            "Привет! Готов
к продуктивному дню?",
            color = Color.White,
            fontSize = 29.sp,
            lineHeight = 35.sp,
            fontWeight = FontWeight.Bold
        )
        Text("Собрали всё важное по твоей учёбе в одном месте.", color = Muted, fontSize = 14.sp)
    }
}

@Composable
private fun SummaryRow() {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        SummaryCard("На сегодня", "2", "задания", Violet, Modifier.weight(1f))
        SummaryCard("Выполнено", "7", "за неделю", Mint, Modifier.weight(1f))
    }
}

@Composable
private fun SummaryCard(
    label: String,
    value: String,
    detail: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Panel),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Text(label, color = Muted, fontSize = 12.sp)
            Text(value, color = accent, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Text(detail, color = Muted, fontSize = 12.sp)
        }
    }
}

@Composable
private fun SectionHeading(title: String, action: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(action, color = Violet, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun AssignmentCard(
    subject: String,
    title: String,
    due: String,
    tag: String,
    accent: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Panel),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            Box(
                modifier = Modifier.size(4.dp, 54.dp).clip(CircleShape).background(accent)
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(subject.uppercase(), color = accent, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Text(due, color = Muted, fontSize = 12.sp)
            }
            Icon(Icons.Rounded.TaskAlt, contentDescription = null, tint = accent)
        }
    }
}

@Composable
private fun CourseCard(
    name: String,
    detail: String,
    initials: String,
    accent: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PanelSoft),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            Surface(color = accent.copy(alpha = 0.16f), shape = RoundedCornerShape(14.dp)) {
                Box(modifier = Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                    Text(initials, color = accent, fontWeight = FontWeight.Bold)
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(name, color = Color.White, fontWeight = FontWeight.SemiBold)
                Text(detail, color = Muted, fontSize = 12.sp)
            }
            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = accent.copy(alpha = 0.8f))
        }
    }
}
