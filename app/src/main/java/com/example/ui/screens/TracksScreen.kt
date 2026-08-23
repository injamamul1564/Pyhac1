package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.LessonEntity
import com.example.data.model.LearningTrack
import com.example.ui.components.BugReportCard
import com.example.ui.components.CyberTerminalBox
import com.example.ui.components.SecurityWarningsCard
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberRed
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceHighlight
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.PyHackerViewModel

@Composable
fun TracksScreen(
    viewModel: PyHackerViewModel,
    modifier: Modifier = Modifier
) {
    val activeTrackId by viewModel.activeTrackId.collectAsState()
    val lessons by viewModel.activeTrackLessons.collectAsState()
    val userProgress by viewModel.userProgress.collectAsState()
    val selectedLesson by viewModel.selectedLesson.collectAsState()

    val completedIds = remember(userProgress?.completedLessonsCsv) {
        userProgress?.completedLessonsCsv?.split(",")?.filter { it.isNotBlank() }?.toSet() ?: emptySet()
    }

    if (selectedLesson != null) {
        LessonDetailView(
            lesson = selectedLesson!!,
            viewModel = viewModel,
            isCompleted = completedIds.contains(selectedLesson!!.id),
            onBack = { viewModel.selectLesson(null) }
        )
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Track Selector Chips
            item {
                Text(
                    text = "SELECT LEARNING TRACK",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LearningTrack.entries.forEach { track ->
                        val isSelected = track.id == activeTrackId
                        val trackColor = Color(track.colorHex)
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) trackColor.copy(alpha = 0.15f) else DarkSurface)
                                .border(
                                    1.dp,
                                    if (isSelected) trackColor else DarkBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.setActiveTrack(track.id) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = when (track.iconTag) {
                                        "terminal" -> Icons.Default.Terminal
                                        "memory" -> Icons.Default.Memory
                                        "hub" -> Icons.Default.Hub
                                        else -> Icons.Default.Shield
                                    },
                                    contentDescription = track.title,
                                    tint = if (isSelected) trackColor else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = track.title.substringBefore(":"),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Active Track Hero Banner
                val currentTrack = LearningTrack.entries.firstOrNull { it.id == activeTrackId } ?: LearningTrack.FOUNDATION
                val trackColor = Color(currentTrack.colorHex)
                val trackCompletedCount = lessons.count { completedIds.contains(it.id) }
                val progressFraction = if (lessons.isNotEmpty()) trackCompletedCount.toFloat() / lessons.size else 0f

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, trackColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentTrack.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = currentTrack.subtitleBn,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(trackColor.copy(alpha = 0.15f))
                                    .border(1.dp, trackColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (currentTrack.iconTag) {
                                        "terminal" -> Icons.Default.Terminal
                                        "memory" -> Icons.Default.Memory
                                        "hub" -> Icons.Default.Hub
                                        else -> Icons.Default.Security
                                    },
                                    contentDescription = null,
                                    tint = trackColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Progress Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Track Progress: $trackCompletedCount/${lessons.size} Modules",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                            Text(
                                text = "${(progressFraction * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = trackColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = trackColor,
                            trackColor = DarkSurfaceHighlight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "CURRICULUM LESSONS",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Lessons List
            items(lessons) { lesson ->
                val isCompleted = completedIds.contains(lesson.id)
                LessonItemCard(
                    lesson = lesson,
                    isCompleted = isCompleted,
                    onClick = { viewModel.selectLesson(lesson) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun LessonItemCard(
    lesson: LessonEntity,
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isCompleted) CyberGreen.copy(alpha = 0.5f) else DarkBorder,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) Color(0xFF0F1E19) else DarkSurface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = lesson.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = lesson.conceptBn,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 2,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Level badge
                    Text(
                        text = lesson.level,
                        style = MaterialTheme.typography.labelSmall,
                        color = when (lesson.level) {
                            "BEGINNER" -> CyberGreen
                            "BASIC" -> CyberCyan
                            "INTERMEDIATE" -> CyberPurple
                            "ADVANCED" -> CyberAmber
                            else -> CyberRed
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(DarkBackground)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Diff: ${lesson.difficulty}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Action / Status Icon
            if (isCompleted) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CyberGreen.copy(alpha = 0.2f))
                        .border(1.dp, CyberGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = CyberGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant)
                        .border(1.dp, DarkBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start Lesson",
                        tint = CyberCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LessonDetailView(
    lesson: LessonEntity,
    viewModel: PyHackerViewModel,
    isCompleted: Boolean,
    onBack: () -> Unit
) {
    val sandboxCode by viewModel.sandboxCode.collectAsState()
    val sandboxOutput by viewModel.sandboxOutput.collectAsState()
    val judgeResult by viewModel.judgeResult.collectAsState()
    val isExecuting by viewModel.isExecuting.collectAsState()

    var activeSubTab by remember { mutableStateOf(0) } // 0: Concept & Breakdown, 1: Practice Sandbox

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        // Back Button & Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onBack,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = BorderStroke(1.dp, DarkBorder),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("← Back to Tracks")
                }

                if (isCompleted) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(CyberGreen.copy(alpha = 0.15f))
                            .border(1.dp, CyberGreen, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = CyberGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("COMPLETED (+40 XP)", style = MaterialTheme.typography.labelSmall, color = CyberGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = lesson.title,
                style = MaterialTheme.typography.displayLarge,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sub Tabs: [CONCEPT & BREAKDOWN] vs [PRACTICE & RUN]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (activeSubTab == 0) CyberCyan.copy(alpha = 0.2f) else Color.Transparent)
                        .border(1.dp, if (activeSubTab == 0) CyberCyan else Color.Transparent, RoundedCornerShape(8.dp))
                        .clickable { activeSubTab = 0 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "1. কনসেপ্ট ও ব্যাখ্যা",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (activeSubTab == 0) CyberCyan else TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (activeSubTab == 1) CyberGreen.copy(alpha = 0.2f) else Color.Transparent)
                        .border(1.dp, if (activeSubTab == 1) CyberGreen else Color.Transparent, RoundedCornerShape(8.dp))
                        .clickable { activeSubTab = 1 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "2. প্র্যাকটিস ও জাজ",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (activeSubTab == 1) CyberGreen else TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (activeSubTab == 0) {
            // [CONCEPT]
            item {
                LessonSectionCard(title = "[CONCEPT] মূল ধারণা (বাংলায়)", color = CyberCyan) {
                    Text(
                        text = lesson.conceptBn,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "English: ${lesson.englishExplanation}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // [ANALOGY]
            item {
                LessonSectionCard(title = "[ANALOGY] বাস্তব উদাহরণ / উপমা", color = CyberAmber) {
                    Text(
                        text = lesson.analogyBn,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // [SYNTAX] & [EXAMPLE]
            item {
                LessonSectionCard(title = "[SYNTAX & CODE] সিনট্যাক্স ও কোড", color = CyberGreen) {
                    Text(
                        text = "Syntax: ${lesson.syntaxCode}",
                        style = MaterialTheme.typography.labelMedium,
                        color = CyberAmber,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkBackground)
                            .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = lesson.exampleCode,
                            style = MaterialTheme.typography.bodyMedium,
                            color = CyberGreen,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Expected Output:\n${lesson.expectedOutput}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.5.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // [BREAKDOWN]
            item {
                LessonSectionCard(title = "[BREAKDOWN] লাইন বাই লাইন বিশ্লেষণ", color = CyberPurple) {
                    Text(
                        text = lesson.breakdownBn,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary,
                        lineHeight = 22.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                ElevatedButton(
                    onClick = { activeSubTab = 1 },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.elevatedButtonColors(containerColor = CyberGreen, contentColor = Color(0xFF0B190F))
                ) {
                    Icon(imageVector = Icons.Default.Code, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("এবার প্র্যাকটিস কোডিং শুরু করো →", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        } else {
            // [PRACTICE & JUDGE]
            item {
                LessonSectionCard(title = "[PRACTICE] কোডিং টাস্ক", color = CyberGreen) {
                    Text(
                        text = lesson.practiceTaskBn,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    var showHint by remember { mutableStateOf(false) }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { showHint = !showHint }
                            .padding(vertical = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Lightbulb, contentDescription = "Hint", tint = CyberAmber, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (showHint) "হিন্ট লুকান" else "হিন্ট দেখুন (Hint)",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberAmber,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (showHint) {
                        Text(
                            text = lesson.hintBn,
                            style = MaterialTheme.typography.bodyMedium,
                            color = CyberAmber,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkBackground)
                                .padding(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Interactive Code Editor
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DarkBorder, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PYTHON EDITOR",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                fontWeight = FontWeight.Bold
                            )
                            Row {
                                OutlinedButton(
                                    onClick = { viewModel.updateSandboxCode(lesson.starterCode) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                                ) {
                                    Text("Reset Code", fontSize = 11.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        androidx.compose.material3.TextField(
                            value = sandboxCode,
                            onValueChange = { viewModel.updateSandboxCode(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                color = CyberGreen
                            ),
                            colors = androidx.compose.material3.TextFieldDefaults.colors(
                                focusedContainerColor = DarkBackground,
                                unfocusedContainerColor = DarkBackground,
                                focusedIndicatorColor = CyberGreen,
                                unfocusedIndicatorColor = DarkBorder
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.runSandboxCode() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                                border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.6f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Run Code")
                            }

                            ElevatedButton(
                                onClick = { viewModel.submitJudgeForCurrentLesson() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.elevatedButtonColors(containerColor = CyberGreen, contentColor = Color(0xFF041F0E))
                            ) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Submit Judge")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Terminal Output Box
                CyberTerminalBox(
                    output = sandboxOutput,
                    isLoading = isExecuting,
                    title = "TERMINAL EXECUTION LOG"
                )

                // Bug Report if any
                judgeResult?.bugReport?.let { bug ->
                    Spacer(modifier = Modifier.height(12.dp))
                    BugReportCard(
                        bugReport = bug,
                        onApplyFix = { viewModel.applyFixToSandbox(it) }
                    )
                }

                // Security Warnings
                judgeResult?.securityWarnings?.let { warnings ->
                    Spacer(modifier = Modifier.height(12.dp))
                    SecurityWarningsCard(warnings = warnings)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun LessonSectionCard(
    title: String,
    color: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}
