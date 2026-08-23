package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BugReportCard
import com.example.ui.components.CyberTerminalBox
import com.example.ui.components.SecurityWarningsCard
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGreen
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
fun SandboxScreen(
    viewModel: PyHackerViewModel,
    modifier: Modifier = Modifier
) {
    val code by viewModel.sandboxCode.collectAsState()
    val output by viewModel.sandboxOutput.collectAsState()
    val judgeResult by viewModel.judgeResult.collectAsState()
    val isExecuting by viewModel.isExecuting.collectAsState()
    val savedSnippets by viewModel.savedSnippets.collectAsState()

    var showSaveDialog by remember { mutableStateOf(false) }
    var snippetTitleInput by remember { mutableStateOf("") }
    var showSnippetsSheet by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        // Quick Code Insert Chips
        item {
            Text(
                text = "PYTHON SANDBOX & CODE JUDGE",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "print()" to "print(\"Hello PyHacker\")",
                    "for loop" to "for i in range(5):\n    print(i)",
                    "if-else" to "if x > 10:\n    print(\"High\")\nelse:\n    print(\"Low\")",
                    "def function" to "def calculate(a, b):\n    return a + b\n\nprint(calculate(10, 20))",
                    "try-except" to "try:\n    res = 10 / 0\nexcept ZeroDivisionError:\n    print(\"Division by zero!\")",
                    "class OOP" to "class Agent:\n    def __init__(self, name):\n        self.name = name\n\na = Agent(\"Neo\")\nprint(a.name)",
                    "hashlib SHA256" to "import hashlib\nprint(hashlib.sha256(b\"pyhacker\").hexdigest()[:16])",
                    "safe socket" to "import socket\ns = socket.socket(socket.AF_INET, socket.SOCK_STREAM)\nprint(\"Socket created:\", s.__class__.__name__)"
                ).forEach { (label, template) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurfaceVariant)
                            .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                            .clickable { viewModel.updateSandboxCode(template) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "+ $label",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberCyan,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Code Editor Box
        item {
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "main.py",
                                style = MaterialTheme.typography.labelMedium,
                                color = CyberGreen,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { showSaveDialog = true },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkBorder,
                                    contentDescription = "Save Snippet",
                                    tint = CyberAmber,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            if (savedSnippets.isNotEmpty()) {
                                TextButton(onClick = { showSnippetsSheet = !showSnippetsSheet }) {
                                    Text("Snippets (${savedSnippets.size})", color = CyberCyan, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // TextField Editor
                    TextField(
                        value = code,
                        onValueChange = { viewModel.updateSandboxCode(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            color = CyberGreen,
                            lineHeight = 19.sp
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = DarkBackground,
                            unfocusedContainerColor = DarkBackground,
                            focusedIndicatorColor = CyberGreen,
                            unfocusedIndicatorColor = DarkBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Character Insertion Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("(", ")", ":", "\"", "'", "[", "]", "{", "}", "=", "==", "+", "-", "*", "/", "f\"\"", "def ", "return ").forEach { symbol ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(DarkBackground)
                                    .border(1.dp, DarkBorder, RoundedCornerShape(4.dp))
                                    .clickable { viewModel.updateSandboxCode(code + symbol) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = symbol,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action Buttons: Run Code & Judge / Security Audit
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
                            onClick = { viewModel.runSandboxCode() },
                            modifier = Modifier.weight(1.2f),
                            colors = ButtonDefaults.elevatedButtonColors(containerColor = CyberGreen, contentColor = Color(0xFF072111)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Judge & Security Audit", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Judge Metrics Banner (Time & Space Complexity)
        judgeResult?.let { res ->
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkSurface)
                        .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Speed, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Time: ${res.timeComplexity}",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberCyan,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Space: ${res.spaceComplexity}",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberAmber,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (res.isPassed) "STATUS: PASS" else "STATUS: AUDIT REQUIRED",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (res.isPassed) CyberGreen else CyberRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Terminal Box
        item {
            CyberTerminalBox(
                output = output,
                isLoading = isExecuting,
                title = "SANDBOX TERMINAL LOG"
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Bug Report Card
        judgeResult?.bugReport?.let { bug ->
            item {
                BugReportCard(
                    bugReport = bug,
                    onApplyFix = { viewModel.applyFixToSandbox(it) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // Security Warnings
        judgeResult?.securityWarnings?.let { warnings ->
            if (warnings.isNotEmpty()) {
                item {
                    SecurityWarningsCard(warnings = warnings)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        // Saved Snippets List Section
        if (showSnippetsSheet && savedSnippets.isNotEmpty()) {
            item {
                Text(
                    text = "SAVED PYTHON SNIPPETS",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberCyan,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }
            items(savedSnippets) { snippet ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .border(1.dp, DarkBorder, RoundedCornerShape(8.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = snippet.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                            Text(
                                text = snippet.code.take(60).replace("\n", " ") + "...",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Row {
                            OutlinedButton(
                                onClick = { viewModel.loadSnippetToSandbox(snippet) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan)
                            ) {
                                Text("Load", fontSize = 11.sp)
                            }
                            IconButton(onClick = { viewModel.deleteSnippet(snippet.id) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = CyberRed, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Save Snippet Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Python Snippet", color = TextPrimary) },
            text = {
                Column {
                    Text("Enter a name for this script:", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = snippetTitleInput,
                        onValueChange = { snippetTitleInput = it },
                        placeholder = { Text("e.g. My Port Checker") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = DarkBackground,
                            unfocusedContainerColor = DarkBackground,
                            focusedIndicatorColor = CyberGreen
                        )
                    )
                }
            },
            confirmButton = {
                ElevatedButton(
                    onClick = {
                        viewModel.saveCurrentSnippet(snippetTitleInput)
                        showSaveDialog = false
                        snippetTitleInput = ""
                    },
                    colors = ButtonDefaults.elevatedButtonColors(containerColor = CyberGreen, contentColor = Color.Black)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurface
        )
    }
}
