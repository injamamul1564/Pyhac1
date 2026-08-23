package com.example.ui.screens

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AiMessageEntity
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberGreenLight
import com.example.ui.theme.CyberIndigo
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
fun AiTutorChatScreen(
    viewModel: PyHackerViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.aiMessages.collectAsState()
    val isThinking by viewModel.isAiThinking.collectAsState()

    var textInput by remember { mutableStateOf("") }
    var attachSandboxCode by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // Chat Header Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(CyberGreen.copy(alpha = 0.2f))
                        .border(1.dp, CyberGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = CyberGreen, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = "PyHacker AI Tutor & Debugger", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text(text = "Natural Bangla + English Coding Engine", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                }
            }

            IconButton(onClick = { viewModel.clearChat() }, modifier = Modifier.size(32.dp)) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Clear Chat", tint = TextMuted, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Suggestion Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                "Python শুরু করতে চাই" to "আমি একদম নতুন, পাইথন শুরু করতে চাই। Lesson 1 বুঝিয়ে দাও।",
                "আমার কোড ডিবাগ করো" to "আমার বর্তমান কোডটি চেক করো এবং কোনো বাগ বা সিকিউরিটি ঝুঁকি আছে কিনা বলো।",
                "OOP সহজ বাংলায়" to "OOP (Object-Oriented Programming) সহজ বাংলায় উপমাসহ বুঝিয়ে দাও।",
                "Time Complexity" to "Time Complexity O(1), O(N), O(log N) কী এবং কেন গুরুত্বপূর্ণ?",
                "SQL Injection ডিফেন্স" to "SQL Injection কীভাবে ঘটে এবং Python-এ কীভাবে নিরাপদ প্যারামিটারাইজড কোড লিখব?",
                "Project Ideas" to "আমার জন্য একটি প্র্যাকটিক্যাল পাইথন প্রজেক্ট সাজেস্ট করো।"
            ).forEach { (chipLabel, promptText) ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkSurface)
                        .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                        .clickable { viewModel.sendChatMessage(promptText, attachSandboxCode) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = chipLabel, style = MaterialTheme.typography.labelSmall, color = CyberGreenLight, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(messages) { message ->
                ChatMessageBubble(message = message, onTransferCode = { viewModel.updateSandboxCode(it) })
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (isThinking) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = CyberGreen, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "PyHacker AI লিখছে এবং কোড বিশ্লেষণ করছে...", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Attach Sandbox Toggle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { attachSandboxCode = !attachSandboxCode }
                .padding(vertical = 2.dp)
        ) {
            Checkbox(
                checked = attachSandboxCode,
                onCheckedChange = { attachSandboxCode = it },
                colors = CheckboxDefaults.colors(checkedColor = CyberGreen, checkmarkColor = Color.Black)
            )
            Text(
                text = "Include Sandbox Code with prompt",
                style = MaterialTheme.typography.labelSmall,
                color = if (attachSandboxCode) CyberGreen else TextMuted
            )
        }

        // Input Field & Send Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = textInput,
                onValueChange = { textInput = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("যেকোনো প্রশ্ন লিখুন বা কোড পেস্ট করুন...", color = TextMuted, fontSize = 13.sp) },
                textStyle = TextStyle(color = TextPrimary, fontSize = 14.sp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface,
                    focusedIndicatorColor = CyberGreen,
                    unfocusedIndicatorColor = DarkBorder
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (textInput.isNotBlank()) {
                        viewModel.sendChatMessage(textInput, attachSandboxCode)
                        textInput = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberGreen)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.Black)
            }
        }
    }
}

@Composable
fun ChatMessageBubble(
    message: AiMessageEntity,
    onTransferCode: (String) -> Unit
) {
    val isUser = message.sender == "USER"
    val clipboardManager = LocalClipboardManager.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(CyberGreen.copy(alpha = 0.2f))
                    .border(1.dp, CyberGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Terminal, contentDescription = null, tint = CyberGreen, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .border(
                    1.dp,
                    if (isUser) CyberIndigo.copy(alpha = 0.4f) else DarkBorder,
                    RoundedCornerShape(16.dp)
                ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) DarkSurfaceVariant else DarkSurface
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = if (isUser) "You" else "PyHacker AI",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isUser) CyberCyan else CyberGreen,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = message.messageText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    lineHeight = 20.sp
                )

                // Code snippet if present
                if (!message.codeSnippet.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkBackground)
                            .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(
                                text = message.codeSnippet,
                                style = MaterialTheme.typography.bodySmall,
                                color = CyberGreen,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                OutlinedButton(
                                    onClick = { onTransferCode(message.codeSnippet) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan)
                                ) {
                                    Text("Load into Sandbox", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
