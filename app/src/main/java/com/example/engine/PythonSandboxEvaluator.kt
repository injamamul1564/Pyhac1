package com.example.engine

import com.example.data.model.BugReport
import com.example.data.model.JudgeResult
import com.example.data.model.SecurityWarning
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.util.regex.Pattern

object PythonSandboxEvaluator {

    fun executeAndJudge(code: String, customInput: String = ""): JudgeResult {
        val trimmed = code.trim()
        if (trimmed.isEmpty()) {
            return JudgeResult(
                isPassed = false,
                outputLog = "কোড খালি! দয়া করে Python কোড লিখুন।",
                timeComplexity = "O(1)",
                spaceComplexity = "O(1)",
                bugReport = BugReport(
                    line = 1,
                    problemTitle = "Empty Code Submission",
                    whyItHappensBn = "কোনো কোড লেখা হয়নি।",
                    fixInstructionsBn = "কমপক্ষে একটি print() অথবা স্টেটমেন্ট লিখুন।",
                    correctedCode = "print(\"Hello World\")",
                    rememberNoteBn = "Python কোড এক্সিকিউট করতে স্টেটমেন্ট প্রয়োজন।"
                )
            )
        }

        // 1. Static Security Scan
        val securityWarnings = scanSecurity(code)

        // 2. Syntax & Indentation Validation
        val syntaxBug = checkSyntaxAndIndentation(code)
        if (syntaxBug != null) {
            return JudgeResult(
                isPassed = false,
                syntaxErrors = listOf("${syntaxBug.problemTitle} at line ${syntaxBug.line}"),
                outputLog = "Traceback (most recent call last):\n  File \"<pyhacker_sandbox>\", line ${syntaxBug.line}\n${syntaxBug.problemTitle}",
                bugReport = syntaxBug,
                securityWarnings = securityWarnings,
                timeComplexity = "O(1)",
                spaceComplexity = "O(1)"
            )
        }

        // 3. Simulated Execution
        val execResult = runSimulatedPython(code, customInput)

        // 4. Calculate Complexity
        val timeComplexity = analyzeTimeComplexity(code)
        val spaceComplexity = analyzeSpaceComplexity(code)

        return JudgeResult(
            isPassed = execResult.isSuccess,
            runtimeErrors = if (!execResult.isSuccess) listOf(execResult.errorMsg) else emptyList(),
            timeComplexity = timeComplexity,
            spaceComplexity = spaceComplexity,
            securityWarnings = securityWarnings,
            testCasePassCount = if (execResult.isSuccess) 1 else 0,
            totalTestCases = 1,
            outputLog = execResult.output,
            bugReport = execResult.bugReport,
            refactoredCode = generateCleanRefactor(code)
        )
    }

    private fun scanSecurity(code: String): List<SecurityWarning> {
        val warnings = mutableListOf<SecurityWarning>()
        val lines = code.lines()

        lines.forEachIndexed { index, line ->
            val lineNum = index + 1
            // 1. SQL Injection Risk
            if (line.contains("SELECT", ignoreCase = true) && (line.contains("+") || line.contains("%") || line.contains("f\"") || line.contains("format("))) {
                warnings.add(
                    SecurityWarning(
                        type = "SQL Injection (SQLi) Vulnerability",
                        severity = "CRITICAL",
                        line = lineNum,
                        descriptionBn = "স্ট্রিং কনক্যাটিনেশন বা f-string দিয়ে সরাসরি SQL কুয়েরি তৈরি করা মারাত্মক অনিরাপদ। এতে অ্যাটাকার ' OR 1=1 -- ইনজেক্ট করতে পারে।",
                        safeAlternative = "cursor.execute(\"SELECT * FROM users WHERE user = ?\", (username,)) # Use Parameterized Queries"
                    )
                )
            }
            // 2. Command Injection Risk
            if (line.contains("os.system") || (line.contains("subprocess.Popen") && line.contains("shell=True")) || (line.contains("subprocess.run") && line.contains("shell=True"))) {
                warnings.add(
                    SecurityWarning(
                        type = "Command Injection Risk",
                        severity = "CRITICAL",
                        line = lineNum,
                        descriptionBn = "os.system() বা shell=True সরাসরি শেল কমান্ড রান করায়। ইউজার ইনপুটের মাধ্যমে সিস্টেমে ক্ষতিকর কমান্ড এক্সিকিউট হতে পারে।",
                        safeAlternative = "subprocess.run([\"command\", arg], shell=False)"
                    )
                )
            }
            // 3. Dangerous eval() / exec()
            if (line.contains("eval(") || line.contains("exec(")) {
                warnings.add(
                    SecurityWarning(
                        type = "Arbitrary Code Execution (eval/exec)",
                        severity = "CRITICAL",
                        line = lineNum,
                        descriptionBn = "eval() বা exec() দিয়ে ডায়নামিক কোড এক্সিকিউট করা অনিরাপদ। এর মাধ্যমে আর্বিট্রেটারি কোড রান হতে পারে।",
                        safeAlternative = "ast.literal_eval() বা নিরাপদ পার্সার ব্যবহার করুন।"
                    )
                )
            }
            // 4. Hardcoded Secrets / Keys
            if (Pattern.compile("(api_key|password|secret|token)\\s*=\\s*[\"'][a-zA-Z0-9_-]{8,}[\"']", Pattern.CASE_INSENSITIVE).matcher(line).find()) {
                warnings.add(
                    SecurityWarning(
                        type = "Hardcoded Secret / API Key",
                        severity = "MEDIUM",
                        line = lineNum,
                        descriptionBn = "সোর্স কোডে সরাসরি পাসওয়ার্ড বা সিক্রেট কি লিখে রাখা অনিরাপদ। APK ডিকম্পাইল বা গিটহাবে পুশ করলে কি লিক হয়ে যাবে।",
                        safeAlternative = "os.getenv(\"API_KEY\") বা এনভায়রনমেন্ট ভ্যারিয়েবল ব্যবহার করুন।"
                    )
                )
            }
            // 5. Weak Crypto (MD5)
            if (line.contains("hashlib.md5", ignoreCase = true)) {
                warnings.add(
                    SecurityWarning(
                        type = "Weak Cryptographic Hash (MD5)",
                        severity = "MEDIUM",
                        line = lineNum,
                        descriptionBn = "MD5 ক্রিপ্টোগ্রাফিক্যালি ব্রেক হয়ে গেছে এবং কলিশন অ্যাটাকের জন্য ঝুঁকিপূর্ণ।",
                        safeAlternative = "hashlib.sha256() বা argon2 / bcrypt ব্যবহার করুন।"
                    )
                )
            }
        }
        return warnings
    }

    private fun checkSyntaxAndIndentation(code: String): BugReport? {
        val lines = code.lines()
        var openParens = 0
        var openBrackets = 0
        var openBraces = 0

        lines.forEachIndexed { idx, line ->
            val lineNum = idx + 1
            val trimmed = line.trim()

            // Check colon on if/for/while/def/class/try/except/elif/else
            if (trimmed.startsWith("if ") || trimmed.startsWith("elif ") || trimmed.startsWith("while ") ||
                trimmed.startsWith("def ") || trimmed.startsWith("class ") || trimmed == "else" ||
                trimmed == "try" || trimmed.startsWith("except") || trimmed.startsWith("for ")) {
                if (!trimmed.endsWith(":") && !trimmed.contains("#")) {
                    return BugReport(
                        line = lineNum,
                        problemTitle = "SyntaxError: expected ':'",
                        whyItHappensBn = "Python-এ শর্ত (if), লুপ (for/while), বা ফাংশন (def) ডিক্লেয়ারেশনের লাইনের শেষে অবশ্যই কোলন (:) দিতে হয়।",
                        fixInstructionsBn = "লাইনের শেষে ':' যোগ করুন।",
                        correctedCode = line + ":",
                        rememberNoteBn = "কোলন (:) দিয়ে কোডের নতুন ব্লক নির্দেশ করা হয়।"
                    )
                }
            }

            // Check print without parens (Python 2 syntax)
            if (trimmed.startsWith("print ") && !trimmed.startsWith("print(")) {
                return BugReport(
                    line = lineNum,
                    problemTitle = "SyntaxError: Missing parentheses in call to 'print'",
                    whyItHappensBn = "Python 3-তে print একটি ফাংশন, তাই প্যারেন্থেসিস (ব্র্যাকেট) ছাড়া print কাজ করে না।",
                    fixInstructionsBn = "print-এর পর প্রথম বন্ধনী () দিন।",
                    correctedCode = "print(" + trimmed.removePrefix("print ") + ")",
                    rememberNoteBn = "Python 3-এ print(\"...\") ব্র্যাকেট সহ লিখতে হয়।"
                )
            }

            // Count delimiters
            openParens += trimmed.count { it == '(' } - trimmed.count { it == ')' }
            openBrackets += trimmed.count { it == '[' } - trimmed.count { it == ']' }
            openBraces += trimmed.count { it == '{' } - trimmed.count { it == '}' }

            if (openParens < 0) {
                return BugReport(
                    line = lineNum,
                    problemTitle = "SyntaxError: unmatched ')'",
                    whyItHappensBn = "অতিরিক্ত ক্লোজিং ব্র্যাকেট ')' দেওয়া হয়েছে যা আগের ওপেনিং ব্র্যাকেটের সাথে মিলছে না।",
                    fixInstructionsBn = "অতিরিক্ত ')' মুছে ফেলুন।",
                    correctedCode = line.replace(")", ""),
                    rememberNoteBn = "সবসময় ব্র্যাকেটের সংখ্যা সমান রাখুন।"
                )
            }
        }

        if (openParens > 0) {
            return BugReport(
                line = lines.size,
                problemTitle = "SyntaxError: '(' was never closed",
                whyItHappensBn = "ওপেনিং ব্র্যাকেট '(' দেওয়া হয়েছে কিন্তু শেষে ক্লোজিং ')' দেওয়া হয়নি।",
                fixInstructionsBn = "লাইনের শেষে ')' দিয়ে বন্ধ করুন।",
                correctedCode = lines.last() + ")",
                rememberNoteBn = "যেকোনো ওপেনিং ব্র্যাকেট অবশ্যই ক্লোজ করতে হবে।"
            )
        }

        return null
    }

    private data class ExecutionResult(
        val isSuccess: Boolean,
        val output: String,
        val errorMsg: String = "",
        val bugReport: BugReport? = null
    )

    private fun runSimulatedPython(code: String, customInput: String): ExecutionResult {
        val outputBuilder = StringBuilder()
        val variables = mutableMapOf<String, Any>()
        val lines = code.lines()

        try {
            var i = 0
            while (i < lines.size) {
                val rawLine = lines[i]
                val line = rawLine.trim()
                i++

                if (line.isEmpty() || line.startsWith("#")) continue

                // Check print statement
                if (line.startsWith("print(") && line.endsWith(")")) {
                    val inner = line.substring(6, line.length - 1).trim()
                    val evalOutput = evaluatePrintExpression(inner, variables)
                    outputBuilder.appendLine(evalOutput)
                    continue
                }

                // Check basic variable assignments: x = 10 or name = "abc"
                if (line.contains("=") && !line.startsWith("if") && !line.startsWith("while") && !line.startsWith("def") && !line.contains("==")) {
                    val parts = line.split("=", limit = 2)
                    if (parts.size == 2) {
                        val varName = parts[0].trim()
                        val valExpr = parts[1].trim()
                        val evaluated = evaluateBasicValue(valExpr, variables)
                        variables[varName] = evaluated
                    }
                    continue
                }

                // Handle basic for loop with range
                if (line.startsWith("for ") && line.contains(" in range(") && line.endsWith(":")) {
                    val varName = line.substringAfter("for ").substringBefore(" in").trim()
                    val rangeContent = line.substringAfter("range(").substringBefore(")").trim()
                    val rangeParams = rangeContent.split(",").map { it.trim().toIntOrNull() ?: 0 }
                    val start = if (rangeParams.size == 1) 0 else rangeParams.getOrNull(0) ?: 0
                    val end = if (rangeParams.size == 1) rangeParams[0] else rangeParams.getOrNull(1) ?: 0
                    val step = if (rangeParams.size == 3) rangeParams[2] else 1

                    // Gather body
                    val bodyLines = mutableListOf<String>()
                    while (i < lines.size && (lines[i].startsWith("    ") || lines[i].startsWith("\t") || lines[i].trim().isEmpty())) {
                        bodyLines.add(lines[i].trim())
                        i++
                    }

                    var curr = start
                    var iterations = 0
                    while ((if (step > 0) curr < end else curr > end) && iterations < 100) {
                        variables[varName] = curr
                        for (bodyLine in bodyLines) {
                            if (bodyLine.startsWith("print(") && bodyLine.endsWith(")")) {
                                val inner = bodyLine.substring(6, bodyLine.length - 1).trim()
                                outputBuilder.appendLine(evaluatePrintExpression(inner, variables))
                            }
                        }
                        curr += step
                        iterations++
                    }
                    continue
                }

                // Handle try-except simulation
                if (line.startsWith("try:")) {
                    var hasError = false
                    val tryBody = mutableListOf<String>()
                    while (i < lines.size && !lines[i].trim().startsWith("except")) {
                        tryBody.add(lines[i].trim())
                        i++
                    }
                    val exceptLine = if (i < lines.size) lines[i].trim() else ""
                    if (exceptLine.startsWith("except")) i++
                    val exceptBody = mutableListOf<String>()
                    while (i < lines.size && (lines[i].startsWith("    ") || lines[i].startsWith("\t") || lines[i].trim().isEmpty())) {
                        exceptBody.add(lines[i].trim())
                        i++
                    }

                    for (tl in tryBody) {
                        if (tl.contains("/ 0") || tl.contains("/0")) {
                            hasError = true
                            break
                        } else if (tl.startsWith("print(")) {
                            val inner = tl.substring(6, tl.length - 1).trim()
                            outputBuilder.appendLine(evaluatePrintExpression(inner, variables))
                        }
                    }

                    if (hasError) {
                        for (el in exceptBody) {
                            if (el.startsWith("print(")) {
                                val inner = el.substring(6, el.length - 1).trim()
                                outputBuilder.appendLine(evaluatePrintExpression(inner, variables))
                            }
                        }
                    }
                    continue
                }

                // Handle basic if-else
                if (line.startsWith("if ") && line.endsWith(":")) {
                    val condExpr = line.substring(3, line.length - 1).trim()
                    val condTrue = evaluateCondition(condExpr, variables)
                    
                    val ifBody = mutableListOf<String>()
                    while (i < lines.size && (lines[i].startsWith("    ") || lines[i].startsWith("\t") || lines[i].trim().isEmpty()) && !lines[i].trim().startsWith("else")) {
                        ifBody.add(lines[i].trim())
                        i++
                    }
                    
                    val elseBody = mutableListOf<String>()
                    if (i < lines.size && lines[i].trim().startsWith("else:")) {
                        i++
                        while (i < lines.size && (lines[i].startsWith("    ") || lines[i].startsWith("\t") || lines[i].trim().isEmpty())) {
                            elseBody.add(lines[i].trim())
                            i++
                        }
                    }

                    val targetBody = if (condTrue) ifBody else elseBody
                    for (b in targetBody) {
                        if (b.startsWith("print(") && b.endsWith(")")) {
                            val inner = b.substring(6, b.length - 1).trim()
                            outputBuilder.appendLine(evaluatePrintExpression(inner, variables))
                        }
                    }
                    continue
                }
            }

            val finalOutput = outputBuilder.toString().trim()
            return ExecutionResult(
                isSuccess = true,
                output = if (finalOutput.isEmpty()) ">>> Program executed successfully (no output printed)." else finalOutput
            )
        } catch (e: Exception) {
            return ExecutionResult(
                isSuccess = false,
                output = "Runtime Error: ${e.message}",
                errorMsg = e.message ?: "Unknown runtime error",
                bugReport = BugReport(
                    line = 1,
                    problemTitle = "Runtime Execution Error",
                    whyItHappensBn = "কোড রান করার সময় আনহ্যান্ডেল্ড এক্সেপশন ঘটেছে: ${e.message}",
                    fixInstructionsBn = "কোডের ভ্যারিয়েবল এবং টাইপ ভ্যালিডেশন চেক করুন।",
                    correctedCode = code,
                    rememberNoteBn = "যেকোনো সম্ভাব্য এরর try-except দিয়ে হ্যান্ডল করুন।"
                )
            )
        }
    }

    private fun evaluatePrintExpression(expr: String, vars: Map<String, Any>): String {
        // Check f-string
        if (expr.startsWith("f\"") && expr.endsWith("\"")) {
            var content = expr.substring(2, expr.length - 1)
            val matcher = Pattern.compile("\\{([^}]+)\\}").matcher(content)
            val sb = StringBuffer()
            while (matcher.find()) {
                val key = matcher.group(1)?.trim() ?: ""
                val rep = when {
                    vars.containsKey(key) -> vars[key].toString()
                    key.contains("+") -> {
                        val p = key.split("+").map { it.trim() }
                        val v1 = vars[p[0]]?.toString()?.toIntOrNull() ?: p[0].toIntOrNull() ?: 0
                        val v2 = vars[p[1]]?.toString()?.toIntOrNull() ?: p[1].toIntOrNull() ?: 0
                        (v1 + v2).toString()
                    }
                    key.contains("*") -> {
                        val p = key.split("*").map { it.trim() }
                        val v1 = vars[p[0]]?.toString()?.toIntOrNull() ?: p[0].toIntOrNull() ?: 0
                        val v2 = vars[p[1]]?.toString()?.toIntOrNull() ?: p[1].toIntOrNull() ?: 0
                        (v1 * v2).toString()
                    }
                    key.contains("/") -> {
                        val p = key.split("/").map { it.trim() }
                        val v1 = vars[p[0]]?.toString()?.toDoubleOrNull() ?: p[0].toDoubleOrNull() ?: 1.0
                        val v2 = vars[p[1]]?.toString()?.toDoubleOrNull() ?: p[1].toDoubleOrNull() ?: 1.0
                        (v1 / v2).toString()
                    }
                    else -> key
                }
                matcher.appendReplacement(sb, MatcherQuote(rep))
            }
            matcher.appendTail(sb)
            return sb.toString()
        }

        // Check string literal "..." or '...'
        if ((expr.startsWith("\"") && expr.endsWith("\"")) || (expr.startsWith("'") && expr.endsWith("'"))) {
            return expr.substring(1, expr.length - 1)
        }

        // Check if variable
        if (vars.containsKey(expr)) {
            return vars[expr].toString()
        }

        // Check comma-separated values in print
        if (expr.contains(",")) {
            val parts = expr.split(",")
            return parts.joinToString(" ") { evaluatePrintExpression(it.trim(), vars) }
        }

        return expr
    }

    private fun MatcherQuote(str: String): String {
        return java.util.regex.Matcher.quoteReplacement(str)
    }

    private fun evaluateBasicValue(expr: String, vars: Map<String, Any>): Any {
        if ((expr.startsWith("\"") && expr.endsWith("\"")) || (expr.startsWith("'") && expr.endsWith("'"))) {
            return expr.substring(1, expr.length - 1)
        }
        if (expr == "True") return true
        if (expr == "False") return false
        expr.toIntOrNull()?.let { return it }
        expr.toDoubleOrNull()?.let { return it }
        if (expr.startsWith("[") && expr.endsWith("]")) {
            val items = expr.substring(1, expr.length - 1).split(",").map { it.trim() }.filter { it.isNotEmpty() }
            return items.map { evaluateBasicValue(it, vars) }.toMutableList()
        }
        return expr
    }

    private fun evaluateCondition(cond: String, vars: Map<String, Any>): Boolean {
        if (cond.contains(">=")) {
            val (l, r) = cond.split(">=").map { it.trim() }
            val lv = vars[l]?.toString()?.toDoubleOrNull() ?: l.toDoubleOrNull() ?: 0.0
            val rv = vars[r]?.toString()?.toDoubleOrNull() ?: r.toDoubleOrNull() ?: 0.0
            return lv >= rv
        }
        if (cond.contains("<=")) {
            val (l, r) = cond.split("<=").map { it.trim() }
            val lv = vars[l]?.toString()?.toDoubleOrNull() ?: l.toDoubleOrNull() ?: 0.0
            val rv = vars[r]?.toString()?.toDoubleOrNull() ?: r.toDoubleOrNull() ?: 0.0
            return lv <= rv
        }
        if (cond.contains("==")) {
            val (l, r) = cond.split("==").map { it.trim().trim('\"', '\'') }
            val lv = vars[l]?.toString() ?: l
            val rv = vars[r]?.toString() ?: r
            return lv == rv
        }
        if (cond.contains("!=")) {
            val (l, r) = cond.split("!=").map { it.trim().trim('\"', '\'') }
            val lv = vars[l]?.toString() ?: l
            val rv = vars[r]?.toString() ?: r
            return lv != rv
        }
        if (cond.contains(">")) {
            val (l, r) = cond.split(">").map { it.trim() }
            val lv = vars[l]?.toString()?.toDoubleOrNull() ?: l.toDoubleOrNull() ?: 0.0
            val rv = vars[r]?.toString()?.toDoubleOrNull() ?: r.toDoubleOrNull() ?: 0.0
            return lv > rv
        }
        if (cond.contains("<")) {
            val (l, r) = cond.split("<").map { it.trim() }
            val lv = vars[l]?.toString()?.toDoubleOrNull() ?: l.toDoubleOrNull() ?: 0.0
            val rv = vars[r]?.toString()?.toDoubleOrNull() ?: r.toDoubleOrNull() ?: 0.0
            return lv < rv
        }
        return vars[cond] == true || cond.equals("True", ignoreCase = true)
    }

    private fun analyzeTimeComplexity(code: String): String {
        val forCount = code.lines().count { it.trim().startsWith("for ") || it.trim().startsWith("while ") }
        val nested = hasNestedLoops(code)
        return when {
            nested -> "O(N²)"
            forCount > 0 -> "O(N)"
            code.contains("binary_search") || code.contains("mid =") -> "O(log N)"
            else -> "O(1)"
        }
    }

    private fun analyzeSpaceComplexity(code: String): String {
        return when {
            code.contains("append(") || code.contains("[") || code.contains("dict(") || code.contains("{") -> "O(N)"
            code.contains("dp = [") && hasNestedLoops(code) -> "O(N²)"
            else -> "O(1)"
        }
    }

    private fun hasNestedLoops(code: String): Boolean {
        var outerFound = false
        var outerIndent = 0
        for (line in code.lines()) {
            val trimmed = line.trim()
            val indent = line.length - line.trimStart().length
            if (trimmed.startsWith("for ") || trimmed.startsWith("while ")) {
                if (outerFound && indent > outerIndent) {
                    return true
                }
                outerFound = true
                outerIndent = indent
            }
        }
        return false
    }

    private fun generateCleanRefactor(code: String): String {
        return code.lines().joinToString("\n") { line ->
            // Add PEP8 spacing after commas
            line.replace(",", ", ")
                .replace("  ", " ")
        }
    }
}
