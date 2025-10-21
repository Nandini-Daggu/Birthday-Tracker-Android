package com.example.birthday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.birthday.ui.theme.BirthdayTheme
import java.time.LocalDate
import java.time.temporal.ChronoUnit

// Data class to hold birthday information
data class Birthday(val id: Int = (0..100000).random(), val name: String, val day: Int, val month: Int)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BirthdayTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showSplashScreen by remember { mutableStateOf(true) }

                    LaunchedEffect(key1 = true) {
                        kotlinx.coroutines.delay(2000) // Show splash for 2 seconds
                        showSplashScreen = false
                    }

                    if (showSplashScreen) {
                        SplashScreen()
                    } else {
                        BirthdayTrackerApp()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthdayTrackerApp() {
    var birthdays by remember { mutableStateOf(listOf<Birthday>()) }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Birthday", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF6200EE),
                                Color(0xFF3700B3)
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Text(
                    text = "🎂 Birthday Tracker",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Birthday List
            if (birthdays.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🎈",
                            fontSize = 64.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No birthdays added yet",
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "Tap + to add your first birthday",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(birthdays.sortedBy { getDaysUntilBirthday(it) }) { birthday ->
                        BirthdayCard(
                            birthday = birthday,
                            onDelete = { birthdays = birthdays.filter { it.id != birthday.id } }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddBirthdayDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { name, day, month ->
                    birthdays = birthdays + Birthday(name = name, day = day, month = month)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF6200EE),
                        Color(0xFF3700B3)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "🎂",
                fontSize = 80.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Birthday Tracker",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Never miss a celebration",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun BirthdayCard(birthday: Birthday, onDelete: () -> Unit) {
    val daysUntil = getDaysUntilBirthday(birthday)
    val monthName = getMonthName(birthday.month)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF6200EE),
                                    Color(0xFF03DAC6)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = birthday.name.firstOrNull()?.uppercase() ?: "?",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = birthday.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "$monthName ${birthday.day}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = when (daysUntil) {
                            0 -> "🎉 Today!"
                            1 -> "Tomorrow"
                            else -> "in $daysUntil days"
                        },
                        fontSize = 14.sp,
                        color = if (daysUntil == 0) Color(0xFFFFD700) else Color(0xFF03DAC6),
                        fontWeight = if (daysUntil == 0) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBirthdayDialog(onDismiss: () -> Unit, onAdd: (String, Int, Int) -> Unit) {
    var name by remember { mutableStateOf("") }
    var selectedMonth by remember { mutableIntStateOf(1) }
    var selectedDay by remember { mutableIntStateOf(1) }
    var expandedMonth by remember { mutableStateOf(false) }
    var expandedDay by remember { mutableStateOf(false) }

    val months = listOf(
        "January" to 31, "February" to 29, "March" to 31,
        "April" to 30, "May" to 31, "June" to 30,
        "July" to 31, "August" to 31, "September" to 30,
        "October" to 31, "November" to 30, "December" to 31
    )

    val daysInMonth = months[selectedMonth - 1].second

    // Define custom colors for text fields to look good on a dark background
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = Color.Gray,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = Color.Gray,
        focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
        unfocusedTrailingIconColor = Color.Gray
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Add Birthday",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = textFieldColors
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Month Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedMonth,
                    onExpandedChange = { expandedMonth = it }
                ) {
                    OutlinedTextField(
                        value = months[selectedMonth - 1].first,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Month") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMonth) },
                        colors = textFieldColors
                    )

                    ExposedDropdownMenu(
                        expanded = expandedMonth,
                        onDismissRequest = { expandedMonth = false }
                    ) {
                        months.forEachIndexed { index, month ->
                            DropdownMenuItem(
                                text = { Text(month.first) },
                                onClick = {
                                    selectedMonth = index + 1
                                    if (selectedDay > month.second) {
                                        selectedDay = month.second
                                    }
                                    expandedMonth = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Day Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedDay,
                    onExpandedChange = { expandedDay = it }
                ) {
                    OutlinedTextField(
                        value = selectedDay.toString(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Day") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDay) },
                        colors = textFieldColors
                    )

                    ExposedDropdownMenu(
                        expanded = expandedDay,
                        onDismissRequest = { expandedDay = false }
                    ) {
                        (1..daysInMonth).forEach { day ->
                            DropdownMenuItem(
                                text = { Text(day.toString()) },
                                onClick = {
                                    selectedDay = day
                                    expandedDay = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onAdd(name, selectedDay, selectedMonth)
                            }
                        },
                        enabled = name.isNotBlank()
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

fun getDaysUntilBirthday(birthday: Birthday): Int {
    val today = LocalDate.now()
    var nextBirthday = LocalDate.of(today.year, birthday.month, birthday.day)

    if (nextBirthday.isBefore(today)) {
        nextBirthday = nextBirthday.plusYears(1)
    } else if (nextBirthday.isEqual(today)) {
        return 0
    }

    return ChronoUnit.DAYS.between(today, nextBirthday).toInt() + 1
}


fun getMonthName(month: Int): String {
    return listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )[month - 1]
}