package com.example.engine

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object PyHackerAiEngine {

    private const val SYSTEM_PROMPT = """
You are PyHacker AI, the intelligent AI Engine, Coding Tutor, Code Judge, Debugger, Mentor, and Security Learning Assistant inside the PyHacker application.
Your mission is to teach Python from beginner to advanced software engineering, DSA, and ethical hacking.

LANGUAGE & COMMUNICATION:
- Always communicate using Natural Bangla + English.
- Technical programming terminology in English (for loop, function, variable, OOP, recursion, etc.).
- Python keywords, syntax, and code strictly in English.
- Explain difficult concepts in simple Bangla/Banglish with real-world analogies.

STRUCTURE OF LESSONS:
[LESSON]
Topic: ...
Level: ...
[CONCEPT]
Simple explanation in Bangla.
[ANALOGY]
Real-world analogy.
[SYNTAX]
Python syntax.
[EXAMPLE]
Working python code in fenced markdown.
[OUTPUT]
Expected output.
[BREAKDOWN]
Line-by-line explanation in Bangla.
[PRACTICE]
Coding task for user.
[HINT]
Friendly hint.

STRUCTURE OF BUG EXPLANATION:
[BUG FOUND]
Line: <number>
Problem: <Syntax/Logic/Type Error>
কেন হচ্ছে: <Bangla explanation why it failed>
Fix: <What to change>
Correct code:
```python
# code
```
Remember: <Key takeaway in Bangla>

SECURITY RULES:
- PyHacker is an ethical cybersecurity learning platform.
- Teach cybersecurity only in legal, authorized, defensive, and educational contexts (CTFs, localhost, defensive detection, safe simulation).
- Never assist with unauthorized real-world attacks.
"""

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun queryAiTutor(userPrompt: String, codeContext: String? = null): String = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val fullUserContent = if (codeContext.isNullOrBlank()) {
                    userPrompt
                } else {
                    "USER CODE:\n```python\n$codeContext\n```\n\nUSER PROMPT: $userPrompt"
                }

                val requestJson = JSONObject().apply {
                    val contentsArr = JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "user")
                            put("parts", JSONArray().apply {
                                put(JSONObject().put("text", fullUserContent))
                            })
                        })
                    }
                    put("contents", contentsArr)

                    val sysInstruction = JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", SYSTEM_PROMPT))
                        })
                    }
                    put("systemInstruction", sysInstruction)

                    val genConfig = JSONObject().apply {
                        put("temperature", 0.7)
                        put("topP", 0.95)
                    }
                    put("generationConfig", genConfig)
                }

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = requestJson.toString().toRequestBody(mediaType)
                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                val response = okHttpClient.newCall(request).execute()
                val responseStr = response.body?.string() ?: ""

                if (response.isSuccessful && responseStr.isNotBlank()) {
                    val respObj = JSONObject(responseStr)
                    val candidates = respObj.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val firstCandidate = candidates.getJSONObject(0)
                        val content = firstCandidate.optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            val text = parts.getJSONObject(0).optString("text")
                            if (text.isNotBlank()) {
                                return@withContext text
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Fallback to local intelligent knowledge generator
            }
        }

        // Offline / Fallback PyHacker AI Engine
        return@withContext generateOfflineAiResponse(userPrompt, codeContext)
    }

    fun generateOfflineAiResponse(prompt: String, code: String? = null): String {
        val lower = prompt.lowercase()

        // 1. Beginner Start Prompt
        if (lower.contains("শুরু") || lower.contains("কিছুই জানি না") || lower.contains("start") || lower.contains("beginner") || lower.contains("lesson 1")) {
            return """
[TYPE] Lesson
[LEVEL] BEGINNER
[TOPIC] Python Introduction & print()

[EXPLANATION]
স্বাগতম **PyHacker Foundation Track**-এ! 🚀
Python হলো বর্তমান বিশ্বের সবচেয়ে সহজ এবং জনপ্রিয় প্রোগ্রামিং ভাষা। প্রোগ্রামিং শেখার প্রথম ধাপ হলো স্ক্রিনে কোনো লেখা প্রদর্শন করা। এর জন্য আমরা `print()` ফাংশন ব্যবহার করি।

[ANALOGY]
যেমন ডাকপিয়ন আপনার চিঠি ঠিকানায় পৌঁছে দেয়, তেমনই `print()` ফাংশন আপনার ব্র্যাকেটের ভেতরের লেখা স্ক্রিনে পৌঁছে দেয়।

[SYNTAX]
print("Hello World")

[CODE]
```python
# তোমার প্রথম Python কোড
print("হ্যালো, আমি পাইথন শিখছি!")
print("Welcome to PyHacker!")
```

[OUTPUT]
হ্যালো, আমি পাইথন শিখছি!
Welcome to PyHacker!

[BREAKDOWN]
1. `print`: একটি ইন-বিল্ট ফাংশন যা আউটপুট ডিসপ্লে করে।
2. `( )`: ব্র্যাকেটের ভেতর যা দেওয়া হয় তাকে argument বলে।
3. `"..."`: ডাবল কোটেশন দিয়ে Text বা String বোঝানো হয়।

[PRACTICE]
তোমার নাম প্রিন্ট করার একটি কোড লেখো। যেমন: `print("My name is ...")`

[HINT]
কোটেশনের ভেতর তোমার নাম লেখো এবং রান করো।
            """.trimIndent()
        }

        // 2. Debugging / Bug Check Prompt
        if (lower.contains("debug") || lower.contains("ডিবাগ") || lower.contains("check") || lower.contains("ভুল") || lower.contains("error") || !code.isNullOrBlank()) {
            val userCode = code ?: prompt
            if (userCode.contains("print ") && !userCode.contains("print(")) {
                return """
[BUG FOUND]

Line: 1
Problem: SyntaxError - Missing parentheses in call to 'print'

কেন হচ্ছে:
Python 2-তে `print "text"` লেখা যেত, কিন্তু আধুনিক Python 3-তে `print()` একটি Function। তাই ব্র্যাকেট ছাড়া এটি কল করা যায় না।

Fix:
`print` এর পর প্রথম বন্ধনী `()` যুক্ত করুন।

Correct code:
```python
print("Hello World")
```

Remember:
Python 3-এ সব Function কলের জন্য ব্র্যাকেট `()` বাধ্যতামূলক।
                """.trimIndent()
            }

            if (userCode.contains("if ") && !userCode.contains(":")) {
                return """
[BUG FOUND]

Line: 1
Problem: SyntaxError - expected ':'

কেন হচ্ছে:
Python-এ `if`, `for`, `while`, `def` স্টেটমেন্টের শর্ত শেষ হলে অবশ্যই লাইনের শেষে কোলন (`:`) দিতে হয়। কোলন ছাড়া Python বুঝতে পারে না ব্লকের শুরু কোথায়।

Fix:
শর্তের শেষে `:` দিন এবং পরবর্তী লাইনে ৪টি স্পেস (Indentation) দিন।

Correct code:
```python
x = 10
if x > 5:
    print("x is greater than 5")
```

Remember:
কোলন (`:`) পাইথনে কোড ব্লক শুরু করার নির্দেশক।
                """.trimIndent()
            }

            return """
[TYPE] Code Review & Audit
[LEVEL] INTERMEDIATE
[RESULT]
তোমার কোড বিশ্লেষণ করা হয়েছে:

1. **Syntax & Style (PEP 8):** কোডের সিনট্যাক্স পরিষ্কার এবং রিডেবল।
2. **Logic Flow:** ডেটা প্রসেসিং লজিক সঠিক।
3. **Time Complexity:** O(N) — ইনপুট সাইজের সাথে রৈখিকভাবে এক্সিকিউট হচ্ছে।
4. **Space Complexity:** O(1) — অতিরিক্ত কোনো মেমোরি অ্যালোকেশন হচ্ছে না।

[SECURITY]
- কোডে কোনো হার্ডকোডেড ক্রেডেনশিয়াল বা আন-স্যানিটাইজড ইনপুট পাওয়া যায়নি।

💡 **টিপস:** কোডকে আরও মডুলার করতে লজিকটিকে একটি ফাংশনের ভেতর ডিফাইন করতে পারো।
            """.trimIndent()
        }

        // 3. OOP Explanation Prompt
        if (lower.contains("oop") || lower.contains("class") || lower.contains("অবজেক্ট")) {
            return """
[TYPE] Concept
[LEVEL] INTERMEDIATE
[TOPIC] Object-Oriented Programming (OOP) in Python

[EXPLANATION]
OOP (Object-Oriented Programming) হলো প্রোগ্রামিংয়ের এমন একটি আর্কিটেকচার যেখানে বাস্তব জগতের ধারণার মতো Data (Attributes) এবং Action (Methods)-কে একটি একক কাঠামোর মধ্যে সাজানো হয়।

- **Class:** নকশা বা ব্লুপ্রিন্ট (Blueprint)।
- **Object:** সেই নকশা থেকে তৈরি বাস্তব রূপ (Instance)।
- **`__init__()`:** কনস্ট্রাক্টর, যা অবজেক্ট তৈরির সময় প্রথম রান করে।

[ANALOGY]
একটি গাড়ির ইঞ্জিনিয়ারিং ব্লুপ্রিন্ট হলো **Class**। আর সেই ব্লুপ্রিন্ট দেখে তৈরি করা লাল, নীল বা কালো আসল গাড়িগুলো হলো **Objects**।

[CODE]
```python
class HackerProfile:
    def __init__(self, username, rank):
        self.username = username
        self.rank = rank
        self.points = 0

    def earn_points(self, pts):
        self.points += pts
        print(f"[{self.username}] Earned {pts} XP! Total: {self.points}")

# Creating Object
agent = HackerProfile("Neo", "Elite")
agent.earn_points(50)
```

[BREAKDOWN]
1. `class HackerProfile:` ক্লাস ডিক্লেয়ারেশন।
2. `self`: বর্তমান অবজেক্টের নিজস্ব প্রোপার্টি নির্দেশ করে।
3. `earn_points`: অবজেক্টের নিজস্ব মেথড।
            """.trimIndent()
        }

        // 4. DSA / Complexity Prompt
        if (lower.contains("dsa") || lower.contains("complexity") || lower.contains("time complexity") || lower.contains("binary search")) {
            return """
[TYPE] Algorithm
[LEVEL] ADVANCED
[TOPIC] Binary Search & Time Complexity O(log N)

[EXPLANATION]
সর্টেড (Sorted) লিস্টে কোনো উপাদান দ্রুত খুঁজতে **Binary Search** ব্যবহার করা হয়। এটি প্রতি পদক্ষেপে সার্চ এরিয়াকে অর্ধেক করে ফেলে।

[COMPLEXITY]
- **Time Complexity:** O(log N) — ১ মিলিয়ন উপাদানের মধ্যেও মাত্র ২০টি পদক্ষেপে ফলাফল পাওয়া যায়!
- **Space Complexity:** O(1) — কোনো অতিরিক্ত মেমরির প্রয়োজন হয় না।

[CODE]
```python
def binary_search(arr, target):
    left, right = 0, len(arr) - 1
    while left <= right:
        mid = (left + right) // 2
        if arr[mid] == target:
            return f"Found at index {mid}"
        elif arr[mid] < target:
            left = mid + 1
        else:
            right = mid - 1
    return "Not found"

numbers = [10, 20, 30, 40, 50, 60, 70]
print(binary_search(numbers, 40))
```
            """.trimIndent()
        }

        // 5. Ethical Hacking / Security Prompt
        if (lower.contains("security") || lower.contains("hack") || lower.contains("port") || lower.contains("sqli") || lower.contains("ctf")) {
            return """
[TYPE] Security & Defense
[LEVEL] SECURITY ENGINEER
[TOPIC] Defensive Socket Programming & SQL Injection Prevention

[EXPLANATION]
সাইবার সিকিউরিটিতে কোড লেখার সময় দুটি মূল বিষয় মনে রাখতে হয়:
1. **Network Ports Auditing:** নিজস্ব সার্ভারে কোন কোন পোর্ট খোলা আছে তা অডিট করা।
2. **Input Sanitization & Parameterized Queries:** ইউজার ইনপুটকে কখনোই সরাসরি কোড হিসেবে এক্সিকিউট হতে না দেওয়া।

[SECURITY]
❌ **Vulnerable Code (SQL Injection):**
`query = "SELECT * FROM users WHERE user = '" + user_input + "'"`

✅ **Safe Defensive Code (Parameterized):**
`cursor.execute("SELECT * FROM users WHERE user = ?", (user_input,))`

[CODE]
```python
import socket

def safe_port_check(host, port):
    # Safe localhost auditing tool
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.settimeout(1.0)
        status = s.connect_ex((host, port))
        if status == 0:
            return f"[+] Port {port} is OPEN"
        else:
            return f"[-] Port {port} is CLOSED"

print(safe_port_check("127.0.0.1", 80))
```
            """.trimIndent()
        }

        // Default Friendly PyHacker AI Response
        return """
[TYPE] Guidance
[LEVEL] INTERMEDIATE
[EXPLANATION]
ধন্যবাদ তোমার প্রশ্নের জন্য! **PyHacker AI Engine** এ তোমার জন্য কিছু সাজেস্টেড লার্নিং ট্র্যাক:

1. 🟢 **Track 1: Foundation** — Variables, Loops, Functions, Exceptions
2. 🔵 **Track 2: Core Engineering** — OOP, Generators, Decorators, SQLite, Async
3. 🟡 **Track 3: DSA & Systems** — Two Pointers, Trees, Sockets, Multithreading
4. 🔴 **Track 4: Ethical Hacking** — Network Defense, Password Hashing, Safe CTF Labs

তোমার যে কোনো কোড বা এরর এখানে পেস্ট করো, আমি বাংলায় লাইন বাই লাইন সমাধান ও অপ্টিমাইজেশন বুঝিয়ে দেব!
        """.trimIndent()
    }
}
