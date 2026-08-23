package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
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
import com.example.data.model.AiBattleProblem
import com.example.data.model.TestCase
import com.example.ui.components.BugReportCard
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

val sampleBattleProblems = listOf(
    AiBattleProblem(
        id = "battle_1",
        title = "Battle 1: Reverse String in O(1) Memory",
        difficulty = "Medium",
        track = "DSA",
        storyBn = "অ্যালগরিদম চ্যালেঞ্জ: একটি স্ট্রিংকে মেমোরি অতিরিক্ত না বাড়িয়ে রিভার্স করতে হবে। টু পয়েন্টার (Two Pointer) টেকনিক ব্যবহার করে এটি সমাধান করো।",
        constraints = "1 <= len(s) <= 10^5, Space Complexity must be O(1)",
        sampleInput = "'cyber'",
        sampleOutput = "'rebyc'",
        starterCode = "def reverse_string(s):\n    # Two pointers logic\n    chars = list(s)\n    left, right = 0, len(chars) - 1\n    while left < right:\n        chars[left], chars[right] = chars[right], chars[left]\n        left += 1\n        right -= 1\n    return ''.join(chars)\n\nprint(reverse_string(\"cyber\"))\n",
        testCases = listOf(TestCase("cyber", "rebyc"))
    ),
    AiBattleProblem(
        id = "battle_2",
        title = "Battle 2: Balanced Parentheses Scanner",
        difficulty = "Hard",
        track = "System",
        storyBn = "কোড পার্সার চ্যালেঞ্জ: একটি স্ট্রিংয়ে '()', '[]', '{}' সঠিকভাবে ব্যালান্সড আছে কিনা Stack ব্যবহার করে O(N) সময়ে চেক করো।",
        constraints = "len(s) <= 10^4, Space: O(N)",
        sampleInput = "'{[()]}'",
        sampleOutput = "True",
        starterCode = "def is_balanced(s):\n    stack = []\n    mapping = {')': '(', ']': '[', '}': '{'}\n    for ch in s:\n        if ch in mapping:\n            if not stack or stack.pop() != mapping[ch]:\n                return False\n        else:\n            stack.append(ch)\n    return len(stack) == 0\n\nprint(is_balanced(\"{[()]}\"))\n",
        testCases = listOf(TestCase("{[()]}", "True"))
    ),
    AiBattleProblem(
        id = "battle_3",
        title = "Battle 3: Two Sum Linear Scan",
        difficulty = "Medium",
        track = "DSA",
        storyBn = "একটি লিস্ট ও টার্গেট যোগফল দেওয়া আছে। Hash Table / Dictionary দিয়ে O(N) সময়ে সেই দুটি ইনডেক্স খুঁজে বের করো।",
        constraints = "2 <= len(nums) <= 10^4, Time: O(N)",
        sampleInput = "nums=[2, 7, 11, 15], target=9",
        sampleOutput = "[0, 1]",
        starterCode = "def two_sum(nums, target):\n    seen = {}\n    for i, num in enumerate(nums):\n        diff = target - num\n        if diff in seen:\n            return [seen[diff], i]\n        seen[num] = i\n    return []\n\nprint(two_sum([2, 7, 11, 15], 9))\n",
        testCases = listOf(TestCase("[2, 7, 11, 15], 9", "[0, 1]"))
    ),
    AiBattleProblem(
        id = "battle_4",
        title = "Battle 4: SQL Injection Input Sanitizer",
        difficulty = "Hard",
        track = "Security",
        storyBn = "ডিফেন্সিভ সিকিউরিটি চ্যালেঞ্জ: ক্ষতিকর SQL ইনজেকশন প্যাটার্ন যেমন \"'\", \";\", \"--\", \"OR 1=1\" নিরপেক্ষ করার জন্য পিউরিফায়ার অ্যালগরিদম তৈরি করো।",
        constraints = "Remove single quotes and semicolon triggers safely",
        sampleInput = "\"admin' OR '1'='1\"",
        sampleOutput = "\"admin OR 1=1\"",
        starterCode = "def sanitize_input(user_input):\n    dangerous = [\"'\", \";\", \"--\"]\n    cleaned = user_input\n    for item in dangerous:\n        cleaned = cleaned.replace(item, \"\")\n    return cleaned\n\nprint(sanitize_input(\"admin' OR '1'='1\"))\n",
        testCases = listOf(TestCase("admin' OR '1'='1", "admin OR 1=1"))
    )
)

@Composable
fun AiBattleScreen(
    viewModel: PyHackerViewModel,
    modifier: Modifier = Modifier
) {
    val selectedProblem by viewModel.selectedBattleProblem.collectAsState()
    val battleResult by viewModel.battleResult.collectAsState()
    val isJudging by viewModel.isBattleJudging.collectAsState()
    val battleRecords by viewModel.battleRecords.collectAsState()

    var userSolutionCode by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        item {
            // Header Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberAmber.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
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
                            Icon(imageVector = Icons.Default.SportsEsports, contentDescription = null, tint = CyberAmber, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AI BATTLE ARENA",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = "AI ইঞ্জিনের সাথে অ্যালগরিদমিক দক্ষতা ও স্পিড টেস্ট করো",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(CyberAmber.copy(alpha = 0.2f))
                            .border(1.dp, CyberAmber, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = CyberAmber, modifier = Modifier.size(24.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (selectedProblem == null) {
            // List of Battle Arenas
            item {
                Text(
                    text = "SELECT BATTLE CHALLENGE",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(sampleBattleProblems) { prob ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            viewModel.selectBattleProblem(prob)
                            userSolutionCode = prob.starterCode
                        },
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = prob.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = prob.storyBn, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, fontSize = 12.sp, maxLines = 2)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row {
                                Text(
                                    text = "AI Benchmark: ${prob.aiTimeBenchmark} • ${prob.aiSpaceBenchmark}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CyberCyan,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        ElevatedButton(
                            onClick = {
                                viewModel.selectBattleProblem(prob)
                                userSolutionCode = prob.starterCode
                            },
                            colors = ButtonDefaults.elevatedButtonColors(containerColor = CyberAmber, contentColor = Color.Black)
                        ) {
                            Text("Battle", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Past Battle Records
            if (battleRecords.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "PAST BATTLE RECORDS",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
                items(battleRecords.take(5)) { record ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = record.problemTitle, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                                Text(
                                    text = "Your Score: ${record.userScore} vs AI: ${record.aiScore} • Time: ${record.executionTimeMs}ms",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted
                                )
                            }
                            Text(
                                text = if (record.winner == "USER") "🏆 WON" else "LOST",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (record.winner == "USER") CyberGreen else CyberRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            val currentProb = selectedProblem!!

            // Problem Details & Code Editor
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { viewModel.selectBattleProblem(currentProb.copy(id = "")) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                    ) {
                        Text("← All Battles")
                    }
                    Text(
                        text = "Target Score: ${currentProb.aiScoreTarget} pts",
                        style = MaterialTheme.typography.labelMedium,
                        color = CyberAmber,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberAmber.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = currentProb.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = currentProb.storyBn, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Constraints: ${currentProb.constraints}",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberCyan,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Solution Editor
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DarkBorder, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "YOUR SOLUTION (PYTHON)", style = MaterialTheme.typography.labelSmall, color = CyberGreen, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        TextField(
                            value = userSolutionCode,
                            onValueChange = { userSolutionCode = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp, color = CyberGreen),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = DarkBackground,
                                unfocusedContainerColor = DarkBackground,
                                focusedIndicatorColor = CyberAmber
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ElevatedButton(
                            onClick = { viewModel.submitBattleSolution(userSolutionCode) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.elevatedButtonColors(containerColor = CyberAmber, contentColor = Color.Black)
                        ) {
                            Icon(imageVector = Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Submit Solution vs AI Judge", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Battle Result Comparison Card
            battleResult?.let { res ->
                item {
                    val isWon = res.isPassed && !res.outputLog.contains("Error")
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, if (isWon) CyberGreen.copy(alpha = 0.5f) else CyberRed.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isWon) Icons.Default.EmojiEvents else Icons.Default.Bolt,
                                        contentDescription = null,
                                        tint = if (isWon) CyberGreen else CyberRed,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isWon) "YOU WON THE BATTLE! 🏆" else "AI OUTPERFORMED THIS ROUND",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isWon) CyberGreen else CyberRed
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Your Time: ${res.timeComplexity} | Space: ${res.spaceComplexity} | Output: ${res.outputLog.take(50)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isWon) "+50 XP Earned! তোমার কোড অপ্টিমাইজড ও সঠিক।" else "টিপস: সলিউশনের টাইম ও স্পেস কমপ্লেক্সিটি চেক করো।",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
