package com.bharatbhushan.dailyexpensetracker

import androidx.compose.runtime.compositionLocalOf

enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    HINDI("hi", "हिंदी"),
    BANGLA("bn", "বাংলা"),
    KANNADA("kn", "ಕನ್ನಡ"),
    MARATHI("mr", "मराठी"),
    GUJARATI("gu", "ગુજરાતી"),
    PUNJABI("pa", "ਪੰਜਾਬੀ")
}

val LocalAppLanguage = compositionLocalOf { AppLanguage.ENGLISH }

private val translations = mapOf(
    "app_name" to listOf("Daily Expense Tracker", "घर खर्च", "ঘর খরচ", "ಮನೆ ಖರ್ಚು", "घर खर्च", "ઘર ખર્ચ"),
    "dashboard" to listOf("Dashboard", "डैशबोर्ड", "ড্যাশবোর্ড", "ಡ್ಯಾಶ್‌ಬೋರ್ಡ್", "डॅशबोर्ड", "ડૅશબોર્ડ"),
    "change_budget" to listOf("Change Daily Expense Tracker Book", "घर खर्च खाता बदलें", "ঘর খরচ খাতা বদলান", "ಮನೆ ಖರ್ಚು ಖಾತೆ ಬದಲಿಸಿ", "घर खर्च खाते बदला", "ઘર ખર્ચ ખાતું બદલો"),
    "add_expense" to listOf("Cash Out", "Cash Out", "Cash Out", "Cash Out", "Cash Out", "Cash Out"),
    "add_income" to listOf("Cash In", "Cash In", "Cash In", "Cash In", "Cash In", "Cash In"),
    "income_history" to listOf("Cash In History", "Cash In History", "Cash In History", "Cash In History", "Cash In History", "Cash In History"),
    "expense_history" to listOf("Cash Out History", "Cash Out History", "Cash Out History", "Cash Out History", "Cash Out History", "Cash Out History"),
    "reports" to listOf("Reports", "रिपोर्ट", "রিপোর্ট", "ವರದಿಗಳು", "अहवाल", "રિપોર્ટ્સ"),
    "monthly_budget" to listOf("Monthly Budget", "मासिक बजट", "মাসিক বাজেট", "ಮಾಸಿಕ ಬಜೆಟ್", "मासिक बजेट", "માસિક બજેટ"),
    "shopping_lists" to listOf("Shopping Lists", "खरीदारी सूची", "কেনাকাটার তালিকা", "ಖರೀದಿ ಪಟ್ಟಿಗಳು", "खरेदी सूची", "ખરીદી યાદીઓ"),
    "categories" to listOf("Manage Categories", "श्रेणियाँ प्रबंधित करें", "বিভাগ পরিচালনা", "ವರ್ಗಗಳನ್ನು ನಿರ್ವಹಿಸಿ", "श्रेणी व्यवस्थापन", "શ્રેણીઓ સંચાલિત કરો"),
    "add_item" to listOf("Add New Item", "नया आइटम जोड़ें", "নতুন আইটেম যোগ করুন", "ಹೊಸ ಐಟಂ ಸೇರಿಸಿ", "नवीन वस्तू जोडा", "નવી વસ્તુ ઉમેરો"),
    "theme" to listOf("Theme Settings", "थीम सेटिंग", "থিম সেটিংস", "ಥೀಮ್ ಸೆಟ್ಟಿಂಗ್‌ಗಳು", "थीम सेटिंग्ज", "થીમ સેટિંગ્સ"),
    "language" to listOf("Language", "भाषा", "ভাষা", "ಭಾಷೆ", "भाषा", "ભાષા"),
    "savings_goals" to listOf("Savings Goals", "बचत लक्ष्य", "সঞ্চয়ের লক্ষ্য", "ಉಳಿತಾಯ ಗುರಿಗಳು", "बचत उद्दिष्टे", "બચત લક્ષ્યો"),
    "backup_restore" to listOf("Backup & Restore", "बैकअप और रिस्टोर", "ব্যাকআপ ও পুনরুদ্ধার", "ಬ್ಯಾಕಪ್ ಮತ್ತು ಮರುಸ್ಥಾಪನೆ", "बॅकअप आणि रिस्टोअर", "બૅકઅપ અને રિસ્ટોર"),
    "security" to listOf("Account & Security", "खाता और सुरक्षा", "অ্যাকাউন্ট ও নিরাপত্তা", "ಖಾತೆ ಮತ್ತು ಭದ್ರತೆ", "खाते आणि सुरक्षा", "એકાઉન્ટ અને સુરક્ષા"),
    "logout" to listOf("Logout", "लॉग आउट", "লগ আউট", "ಲಾಗ್ ಔಟ್", "लॉग आउट", "લૉગ આઉટ"),
    "home" to listOf("Home", "होम", "হোম", "ಮುಖಪುಟ", "होम", "હોમ"),
    "transactions" to listOf("Transactions", "लेन-देन", "লেনদেন", "ವಹಿವಾಟುಗಳು", "व्यवहार", "વ્યવહારો"),
    "budget" to listOf("Budget", "बजट", "বাজেট", "ಬಜೆಟ್", "बजेट", "બજેટ"),
    "select_language" to listOf("Select App Language", "ऐप की भाषा चुनें", "অ্যাপের ভাষা বেছে নিন", "ಅಪ್ಲಿಕೇಶನ್ ಭಾಷೆ ಆಯ್ಕೆಮಾಡಿ", "अॅपची भाषा निवडा", "એપની ભાષા પસંદ કરો"),
    "language_hint" to listOf("The selected language is saved on this device.", "चुनी हुई भाषा इस डिवाइस में सेव रहेगी।", "নির্বাচিত ভাষা এই ডিভাইসে সংরক্ষিত থাকবে।", "ಆಯ್ಕೆಮಾಡಿದ ಭಾಷೆ ಈ ಸಾಧನದಲ್ಲಿ ಉಳಿಯುತ್ತದೆ.", "निवडलेली भाषा या डिव्हाइसवर सेव्ह राहील.", "પસંદ કરેલી ભાષા આ ડિવાઇસમાં સેવ રહેશે."),
    "back" to listOf("Back", "वापस", "ফিরে যান", "ಹಿಂದೆ", "मागे", "પાછળ")
)

fun appText(key: String, language: AppLanguage): String {
    if (language == AppLanguage.PUNJABI) {
        return punjabiTranslations[key] ?: key
    }
    val index = AppLanguage.entries.indexOf(language)
    return translations[key]?.getOrNull(index) ?: key
}

private val punjabiTranslations = mapOf(
    "app_name" to "ਘਰ ਖਰਚ",
    "dashboard" to "ਡੈਸ਼ਬੋਰਡ",
    "change_budget" to "ਘਰ ਬਜਟ ਬਦਲੋ",
    "add_expense" to "Cash Out",
    "add_income" to "Cash In",
    "income_history" to "Cash In History",
    "expense_history" to "Cash Out History",
    "reports" to "ਰਿਪੋਰਟਾਂ",
    "monthly_budget" to "ਮਹੀਨਾਵਾਰ ਬਜਟ",
    "shopping_lists" to "ਖਰੀਦਦਾਰੀ ਸੂਚੀਆਂ",
    "categories" to "ਸ਼੍ਰੇਣੀਆਂ ਸੰਭਾਲੋ",
    "add_item" to "ਨਵੀਂ ਵਸਤੂ ਜੋੜੋ",
    "theme" to "ਥੀਮ ਸੈਟਿੰਗਾਂ",
    "language" to "ਭਾਸ਼ਾ",
    "savings_goals" to "ਬਚਤ ਟੀਚੇ",
    "backup_restore" to "ਬੈਕਅੱਪ ਅਤੇ ਰੀਸਟੋਰ",
    "security" to "ਖਾਤਾ ਅਤੇ ਸੁਰੱਖਿਆ",
    "logout" to "ਲੌਗ ਆਉਟ",
    "home" to "ਹੋਮ",
    "transactions" to "ਲੈਣ-ਦੇਣ",
    "budget" to "ਬਜਟ",
    "select_language" to "ਐਪ ਦੀ ਭਾਸ਼ਾ ਚੁਣੋ",
    "language_hint" to "ਚੁਣੀ ਹੋਈ ਭਾਸ਼ਾ ਇਸ ਡਿਵਾਈਸ ਵਿੱਚ ਸੇਵ ਰਹੇਗੀ।",
    "back" to "ਵਾਪਸ"
)
