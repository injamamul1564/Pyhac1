package com.example.data.model

enum class UserLevel(val titleEn: String, val titleBn: String, val minXp: Int, val description: String) {
    BEGINNER("BEGINNER", "নতুন শিক্ষানবিস", 0, "Python এর সাথে প্রথম পরিচয় ও বেসিক ধারণা"),
    BASIC("BASIC", "প্রাথমিক কোডার", 200, "লুপ, কন্ডিশন ও ফাংশন ব্যবহারে সক্ষম"),
    INTERMEDIATE("INTERMEDIATE", "মধ্যবর্তী ডেভেলপার", 500, "OOP, ফাইল সিস্টেম ও মডিউল দক্ষতা"),
    ADVANCED("ADVANCED", "উন্নত প্রোগ্রামার", 1000, "অ্যালগরিদম, ডাটা স্ট্রাকচার ও অপ্টিমাইজেশন"),
    ENGINEER("ENGINEER", "সফটওয়্যার ইঞ্জিনিয়ার", 2000, "সিস্টেম প্রোগ্রামিং, মাল্টিথ্রেডিং ও ডিজাইন প্যাটার্ন"),
    SECURITY_ENGINEER("SECURITY ENGINEER", "সাইবার সিকিউরিটি স্পেশালিস্ট", 3500, "এথিক্যাল হ্যাকিং, নেটওয়ার্কিং ও সিকিউর কোডিং");

    companion object {
        fun fromXp(xp: Int): UserLevel {
            return entries.lastOrNull { xp >= it.minXp } ?: BEGINNER
        }
    }
}

enum class LearningTrack(
    val id: String,
    val title: String,
    val subtitleBn: String,
    val iconTag: String,
    val totalLessons: Int,
    val colorHex: Long
) {
    FOUNDATION(
        id = "foundation",
        title = "Track 1: Foundation",
        subtitleBn = "জিরো থেকে বেসিক পাইথন ডেভেলপার",
        iconTag = "terminal",
        totalLessons = 8,
        colorHex = 0xFF00FF66
    ),
    CORE_ENGINEERING(
        id = "core_engineering",
        title = "Track 2: Core Engineering",
        subtitleBn = "প্রফেশনাল সফটওয়্যার ইঞ্জিনিয়ারিং ও OOP",
        iconTag = "memory",
        totalLessons = 8,
        colorHex = 0xFF00E5FF
    ),
    DSA_SYSTEM(
        id = "dsa_system",
        title = "Track 3: DSA & Systems",
        subtitleBn = "ডাটা স্ট্রাকচার, অ্যালগরিদম ও সিস্টেমস",
        iconTag = "hub",
        totalLessons = 8,
        colorHex = 0xFFFFB800
    ),
    ETHICAL_HACKING(
        id = "ethical_hacking",
        title = "Track 4: Ethical Hacking & Security",
        subtitleBn = "নেটওয়ার্ক ডিফেন্স ও সিকিউরিটি স্ক্রিপ্টিং",
        iconTag = "shield",
        totalLessons = 8,
        colorHex = 0xFFFF3366
    )
}

data class TestCase(
    val input: String,
    val expectedOutput: String,
    val isHidden: Boolean = false,
    val description: String = ""
)

data class JudgeResult(
    val isPassed: Boolean,
    val syntaxErrors: List<String> = emptyList(),
    val runtimeErrors: List<String> = emptyList(),
    val logicalIssues: List<String> = emptyList(),
    val timeComplexity: String = "O(1)",
    val spaceComplexity: String = "O(1)",
    val securityWarnings: List<SecurityWarning> = emptyList(),
    val testCasePassCount: Int = 0,
    val totalTestCases: Int = 0,
    val outputLog: String = "",
    val bugReport: BugReport? = null,
    val refactoredCode: String? = null
)

data class SecurityWarning(
    val type: String,
    val severity: String, // LOW, MEDIUM, CRITICAL
    val line: Int,
    val descriptionBn: String,
    val safeAlternative: String
)

data class BugReport(
    val line: Int,
    val problemTitle: String,
    val whyItHappensBn: String,
    val fixInstructionsBn: String,
    val correctedCode: String,
    val rememberNoteBn: String
)

data class AiBattleProblem(
    val id: String,
    val title: String,
    val difficulty: String,
    val track: String,
    val storyBn: String,
    val constraints: String,
    val sampleInput: String,
    val sampleOutput: String,
    val starterCode: String,
    val testCases: List<TestCase>,
    val aiScoreTarget: Int = 95,
    val aiTimeBenchmark: String = "12ms",
    val aiSpaceBenchmark: String = "O(n)"
)

data class ProjectBlueprint(
    val id: String,
    val name: String,
    val difficulty: String, // Beginner, Intermediate, Advanced
    val skillsRequired: List<String>,
    val features: List<String>,
    val architectureBn: String,
    val fileStructure: String,
    val starterCode: String,
    val stepByStepTasksBn: List<String>,
    val securityConsiderationsBn: String
)
