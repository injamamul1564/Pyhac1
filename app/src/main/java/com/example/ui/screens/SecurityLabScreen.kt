package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectBlueprint
import com.example.ui.components.CyberTerminalBox
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
import java.security.MessageDigest

val sampleBlueprints = listOf(
    ProjectBlueprint(
        id = "proj_1",
        name = "1. CLI File & Malware Hash Scanner",
        difficulty = "Beginner-Intermediate",
        skillsRequired = listOf("os module", "hashlib", "argparse", "File I/O"),
        features = listOf(
            "ডাইরেক্টরির সব ফাইলের SHA-256 হ্যাশ বের করা",
            "পরিচিত ম্যালওয়্যার হ্যাশের ডাটাবেসের সাথে মেলানো",
            "সন্দেহজনক ফাইলের রিপোর্ট তৈরি করা"
        ),
        architectureBn = "Modular CLI architecture: hash_engine.py -> database.py -> scanner_cli.py",
        fileStructure = "malware_scanner/\n├── main.py\n├── scanner.py\n├── signatures.json\n└── requirements.txt",
        starterCode = """
import hashlib
import os

def scan_file_sha256(filepath):
    hasher = hashlib.sha256()
    with open(filepath, 'rb') as f:
        while chunk := f.read(4096):
            hasher.update(chunk)
    return hasher.hexdigest()

print("Scanner Engine Loaded.")
        """.trimIndent(),
        stepByStepTasksBn = listOf(
            "Task 1: hashlib ব্যবহার করে ফাইলের SHA-256 হ্যাশ ফাংশন তৈরি করো।",
            "Task 2: os.walk() দিয়ে পুরো ফোল্ডারের ফাইল স্ক্যান করো।",
            "Task 3: ম্যালওয়্যার হ্যাশের সাথে মিলে গেলে অ্যালার্ট প্রিন্ট করো।"
        ),
        securityConsiderationsBn = "সবসময় ফাইল বাইনারি মোডে ('rb') রিড করতে হবে এবং ফাইল হ্যান্ডলিংয়ে try-except ব্যবহার আবশ্যক।"
    ),
    ProjectBlueprint(
        id = "proj_2",
        name = "2. Defensive Log Threat Analyzer",
        difficulty = "Intermediate",
        skillsRequired = listOf("re (Regex)", "datetime", "Collections", "OOP"),
        features = listOf(
            "সার্ভার অ্যাকসেস লগ পার্সিং",
            "একই IP থেকে ব্রুট-ফোর্স অ্যাটাক শনাক্তকরণ",
            "SQL ইনজেকশন বা XSS প্যাটার্ন ফিল্টারিং"
        ),
        architectureBn = "Log Stream -> Regex Parser -> Anomaly Detector -> Security Alert Report",
        fileStructure = "threat_analyzer/\n├── analyzer.py\n├── log_parser.py\n└── sample_access.log",
        starterCode = """
import re

LOG_PATTERN = r'(\d+\.\d+\.\d+\.\d+) - - \[(.*?)\] "(.*?)" (\d+) (\d+)'

def parse_log_line(line):
    match = re.match(LOG_PATTERN, line)
    return match.groups() if match else None

print("Threat Log Analyzer Ready.")
        """.trimIndent(),
        stepByStepTasksBn = listOf(
            "Task 1: রেগুলার এক্সপ্রেশন (Regex) দিয়ে লগ লাইন পার্স করো।",
            "Task 2: এক মিনিটে 10 বারের বেশি ব্যর্থ লগইন শনাক্ত করো।",
            "Task 3: কুয়েরি স্ট্রিংয়ে 'OR 1=1' থাকলে ফ্ল্যাগ করো।"
        ),
        securityConsiderationsBn = "লগ ইনজেকশন প্রতিরোধে লগের টেক্সট স্যানিটাইজ করে সংরক্ষণ করুন।"
    ),
    ProjectBlueprint(
        id = "proj_3",
        name = "3. SQLite Cryptographic Password Vault",
        difficulty = "Advanced",
        skillsRequired = listOf("sqlite3", "cryptography", "PBKDF2", "PEP 8"),
        features = listOf(
            "মাস্টার পাসওয়ার্ড দিয়ে PBKDF2 কি ড্রাইভেশন",
            "ডাটাবেসে পাসওয়ার্ডগুলো AES-GCM এনক্রিপশনে সংরক্ষণ",
            "ক্লিপবোর্ডে নিরাপদ অটো-কপি সিস্টেম"
        ),
        architectureBn = "Master Key -> PBKDF2 -> SQLite Storage -> Crypto Engine",
        fileStructure = "py_vault/\n├── vault.py\n├── crypto.py\n├── db.py\n└── config.json",
        starterCode = """
import sqlite3
import hashlib

def init_vault_db():
    conn = sqlite3.connect(":memory:")
    cur = conn.cursor()
    cur.execute('''CREATE TABLE vault (id INT, service TEXT, enc_pass TEXT)''')
    conn.commit()
    return "Database Ready"

print(init_vault_db())
        """.trimIndent(),
        stepByStepTasksBn = listOf(
            "Task 1: SQLite টেবিল ও প্যারামিটারাইজড কুয়েরি তৈরি করো।",
            "Task 2: Salt সহ SHA-256 দিয়ে মাস্টার পাসওয়ার্ড ভ্যালিডেট করো।",
            "Task 3: নিরাপদ পাসওয়ার্ড রিট্রিভাল মেথড তৈরি করো।"
        ),
        securityConsiderationsBn = "কখনোই প্লেইন টেক্সট পাসওয়ার্ড সেভ করবেন না।"
    )
)

@Composable
fun SecurityLabScreen(
    viewModel: PyHackerViewModel,
    modifier: Modifier = Modifier
) {
    var activeSubTab by remember { mutableStateOf(0) } // 0: Interactive Labs, 1: Project Blueprints

    // Lab 1: Port Scanner State
    var targetHost by remember { mutableStateOf("127.0.0.1") }
    var scannedPortsLog by remember { mutableStateOf("") }

    // Lab 2: Salt Hash State
    var rawPassword by remember { mutableStateOf("MySecretP@ss") }
    var saltValue by remember { mutableStateOf("pyhacker_2026") }
    var generatedHash by remember { mutableStateOf("") }

    // Lab 3: SQLi Simulator State
    var userInputQuery by remember { mutableStateOf("admin' OR '1'='1") }
    var sqliResultLog by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        // Hero Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberRed.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = CyberRed, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CYBERSECURITY & DEFENSE LABS",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = "এথিক্যাল হ্যাকিং, নেটওয়ার্ক ডিফেন্স ও সিকিউর সফটওয়্যার ইঞ্জিনিয়ারিং",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sub Tab Selector
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
                        .background(if (activeSubTab == 0) CyberRed.copy(alpha = 0.2f) else Color.Transparent)
                        .border(1.dp, if (activeSubTab == 0) CyberRed else Color.Transparent, RoundedCornerShape(8.dp))
                        .clickable { activeSubTab = 0 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "1. ডিফেন্সিভ ল্যাবস (Safe Labs)",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (activeSubTab == 0) CyberRed else TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (activeSubTab == 1) CyberCyan.copy(alpha = 0.2f) else Color.Transparent)
                        .border(1.dp, if (activeSubTab == 1) CyberCyan else Color.Transparent, RoundedCornerShape(8.dp))
                        .clickable { activeSubTab = 1 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "2. প্রজেক্ট ব্লুপ্রিন্টস",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (activeSubTab == 1) CyberCyan else TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (activeSubTab == 0) {
            // Lab 1: Port Scanner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Terminal, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Lab 1: Safe Socket & Port Auditor (Localhost)", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = "নিজের লোকালহোস্টে কোন কোন পোর্ট (HTTP 80, HTTPS 443, SSH 22) খোলা আছে তা পরীক্ষা করুন।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            TextField(
                                value = targetHost,
                                onValueChange = { targetHost = it },
                                label = { Text("Target Host") },
                                modifier = Modifier.weight(1f),
                                colors = TextFieldDefaults.colors(focusedContainerColor = DarkBackground, unfocusedContainerColor = DarkBackground, focusedIndicatorColor = CyberCyan)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            ElevatedButton(
                                onClick = {
                                    scannedPortsLog = "[+] Initiating TCP Handshake scan on $targetHost...\n[+] Port 80 (HTTP): OPEN [HTTP/1.1 200 OK]\n[+] Port 443 (HTTPS): OPEN [TLS v1.3 Active]\n[-] Port 22 (SSH): FILTERED / CLOSED\n[-] Port 21 (FTP): CLOSED\n[✓] Audit Complete: 2 open defensive endpoints."
                                },
                                colors = ButtonDefaults.elevatedButtonColors(containerColor = CyberCyan, contentColor = Color.Black)
                            ) {
                                Text("Scan Ports")
                            }
                        }

                        if (scannedPortsLog.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            CyberTerminalBox(output = scannedPortsLog, title = "PORT AUDIT LOG")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Lab 2: Cryptographic Hashing & Salt Lab
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberAmber.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = CyberAmber, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Lab 2: Cryptographic Password & Salt Lab", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = "Salt কীভাবে রেইনবো টেবিল অ্যাটাক প্রতিরোধ করে তা পরীক্ষা করুন।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        TextField(
                            value = rawPassword,
                            onValueChange = { rawPassword = it },
                            label = { Text("Plaintext Password") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(focusedContainerColor = DarkBackground, unfocusedContainerColor = DarkBackground, focusedIndicatorColor = CyberAmber)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        TextField(
                            value = saltValue,
                            onValueChange = { saltValue = it },
                            label = { Text("Cryptographic Salt") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(focusedContainerColor = DarkBackground, unfocusedContainerColor = DarkBackground, focusedIndicatorColor = CyberAmber)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        ElevatedButton(
                            onClick = {
                                val md = MessageDigest.getInstance("SHA-256")
                                val plainDigest = md.digest(rawPassword.toByteArray()).joinToString("") { "%02x".format(it) }
                                val saltedDigest = md.digest((rawPassword + saltValue).toByteArray()).joinToString("") { "%02x".format(it) }
                                generatedHash = "Plain SHA-256:\n$plainDigest\n\nSalted SHA-256 (Rainbow Table Resistant):\n$saltedDigest"
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.elevatedButtonColors(containerColor = CyberAmber, contentColor = Color.Black)
                        ) {
                            Text("Generate Cryptographic Hashes")
                        }

                        if (generatedHash.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            CyberTerminalBox(output = generatedHash, title = "HASH COMPARISON")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Lab 3: SQL Injection vs Parameterized Defense
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberGreen.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = CyberGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Lab 3: SQLi Defense Simulator", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = "ইউজার ইনপুট দিয়ে দেখুন কীভাবে আন-স্যানিটাইজড কুয়েরি বাইপাস হয় এবং প্যারামিটারাইজড কুয়েরি তা রক্ষা করে।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        TextField(
                            value = userInputQuery,
                            onValueChange = { userInputQuery = it },
                            label = { Text("Simulated User Input") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(focusedContainerColor = DarkBackground, unfocusedContainerColor = DarkBackground, focusedIndicatorColor = CyberGreen)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        ElevatedButton(
                            onClick = {
                                val isBypass = userInputQuery.contains("'") && userInputQuery.contains("OR")
                                sqliResultLog = """
[!] VULNERABLE STRING QUERY:
"SELECT * FROM users WHERE user = '$userInputQuery'"
Result: ${if (isBypass) "❌ VULNERABLE: Authentication Bypassed! (Always True)" else "Query Executed"}

[✓] DEFENSIVE PARAMETERIZED QUERY:
cursor.execute("SELECT * FROM users WHERE user = ?", (user_input,))
Result: 🛡️ SAFE: Treated strictly as literal text. Attack neutralized!
                                """.trimIndent()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.elevatedButtonColors(containerColor = CyberGreen, contentColor = Color.Black)
                        ) {
                            Text("Test Vulnerability vs Defense")
                        }

                        if (sqliResultLog.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            CyberTerminalBox(output = sqliResultLog, title = "SQL DEFENSE REPORT")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        } else {
            // Project Blueprints
            item {
                Text(
                    text = "PROJECT BLUEPRINTS & CODE ARCHITECTURE",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(sampleBlueprints) { bp ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .border(1.dp, DarkBorder, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = bp.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text(
                                text = bp.difficulty,
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberCyan,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(DarkBackground)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Skills: ${bp.skillsRequired.joinToString(", ")}", style = MaterialTheme.typography.labelSmall, color = CyberAmber)

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Architecture: ${bp.architectureBn}", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "File Structure:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkBackground)
                                .padding(8.dp)
                        ) {
                            Text(text = bp.fileStructure, style = MaterialTheme.typography.bodySmall, color = CyberGreen, fontFamily = FontFamily.Monospace)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { viewModel.updateSandboxCode(bp.starterCode); viewModel.setActiveTab(1) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberGreen)
                        ) {
                            Icon(imageVector = Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ব্লুপ্রিন্ট কোড স্যান্ডবক্সে লোড করো")
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
