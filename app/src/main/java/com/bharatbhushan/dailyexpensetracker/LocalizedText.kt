package com.bharatbhushan.dailyexpensetracker

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text as MaterialText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

@Composable
fun AppText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    MaterialText(
        text = translateUiText(text, LocalAppLanguage.current),
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style
    )
}

private data class UiPhrase(
    val sources: List<String>,
    val values: Map<AppLanguage, String>
)

private fun phrase(
    english: String,
    hindi: String,
    bangla: String,
    kannada: String,
    marathi: String,
    gujarati: String,
    punjabi: String,
    vararg aliases: String
) = UiPhrase(
    sources = listOf(english, hindi, *aliases),
    values = mapOf(
        AppLanguage.ENGLISH to english,
        AppLanguage.HINDI to hindi,
        AppLanguage.BANGLA to bangla,
        AppLanguage.KANNADA to kannada,
        AppLanguage.MARATHI to marathi,
        AppLanguage.GUJARATI to gujarati,
        AppLanguage.PUNJABI to punjabi
    )
)

private val uiPhrases = listOf(
    phrase("Cash Out", "Cash Out", "Cash Out", "Cash Out", "Cash Out", "Cash Out", "Cash Out", "Cash Out", "Cash Out"),
    phrase("Cash In", "Cash In", "Cash In", "Cash In", "Cash In", "Cash In", "Cash In", "Cash In", "Cash In"),
    phrase("Cash Out History", "Cash Out History", "Cash Out History", "Cash Out History", "Cash Out History", "Cash Out History", "Cash Out History", "Cash Out History"),
    phrase("Cash In History", "Cash In History", "Cash In History", "Cash In History", "Cash In History", "Cash In History", "Cash In History"),
    phrase("Reports", "रिपोर्ट", "রিপোর্ট", "ವರದಿಗಳು", "अहवाल", "રિપોર્ટ્સ", "ਰਿਪੋਰਟਾਂ", "खर्च रिपोर्ट / Reports"),
    phrase("Monthly Budget", "मासिक बजट", "মাসিক বাজেট", "ಮಾಸಿಕ ಬಜೆಟ್", "मासिक बजेट", "માસિક બજેટ", "ਮਹੀਨਾਵਾਰ ਬਜਟ", "मासिक बजट / Monthly Budget"),
    phrase("Shopping Lists", "खरीदारी सूची", "কেনাকাটার তালিকা", "ಖರೀದಿ ಪಟ್ಟಿಗಳು", "खरेदी सूची", "ખરીદી યાદીઓ", "ਖਰੀਦਦਾਰੀ ਸੂਚੀਆਂ"),
    phrase("Savings Goals", "बचत लक्ष्य", "সঞ্চয়ের লক্ষ্য", "ಉಳಿತಾಯ ಗುರಿಗಳು", "बचत उद्दिष्टे", "બચત લક્ષ્યો", "ਬਚਤ ਟੀਚੇ"),
    phrase("Backup & Restore", "बैकअप और रिस्टोर", "ব্যাকআপ ও পুনরুদ্ধার", "ಬ್ಯಾಕಪ್ ಮತ್ತು ಮರುಸ್ಥಾಪನೆ", "बॅकअप आणि रिस्टोअर", "બૅકઅપ અને રિસ્ટોર", "ਬੈਕਅੱਪ ਅਤੇ ਰੀਸਟੋਰ", "Backup और Restore"),
    phrase("Account & Security", "खाता और सुरक्षा", "অ্যাকাউন্ট ও নিরাপত্তা", "ಖಾತೆ ಮತ್ತು ಭದ್ರತೆ", "खाते आणि सुरक्षा", "એકાઉન્ટ અને સુરક્ષા", "ਖਾਤਾ ਅਤੇ ਸੁਰੱਖਿਆ"),
    phrase("Save", "सेव करें", "সংরক্ষণ করুন", "ಉಳಿಸಿ", "सेव्ह करा", "સેવ કરો", "ਸੇਵ ਕਰੋ", "बदलाव सेव करें", "खर्च सेव करें", "आय सेव करें", "बजट सेव करें", "Goal सेव करें", "Item सेव करें"),
    phrase("Cancel", "रद्द करें", "বাতিল করুন", "ರದ್ದುಮಾಡಿ", "रद्द करा", "રદ કરો", "ਰੱਦ ਕਰੋ"),
    phrase("Delete", "हटाएँ", "মুছুন", "ಅಳಿಸಿ", "हटवा", "દૂર કરો", "ਹਟਾਓ"),
    phrase("Close", "बंद करें", "বন্ধ করুন", "ಮುಚ್ಚಿ", "बंद करा", "બંધ કરો", "ਬੰਦ ਕਰੋ"),
    phrase("Add", "जोड़ें", "যোগ করুন", "ಸೇರಿಸಿ", "जोडा", "ઉમેરો", "ਜੋੜੋ"),
    phrase("Amount", "राशि", "পরিমাণ", "ಮೊತ್ತ", "रक्कम", "રકમ", "ਰਕਮ", "राशि / Amount"),
    phrase("Category", "श्रेणी", "বিভাগ", "ವರ್ಗ", "श्रेणी", "શ્રેણી", "ਸ਼੍ਰੇਣੀ", "श्रेणी / Category"),
    phrase("Payment Mode", "भुगतान माध्यम", "পেমেন্ট মাধ্যম", "ಪಾವತಿ ವಿಧಾನ", "पेमेंट पद्धत", "ચુકવણી માધ્યમ", "ਭੁਗਤਾਨ ਢੰਗ", "Payment Mode / Bank Name"),
    phrase("Description", "विवरण", "বিবরণ", "ವಿವರಣೆ", "वर्णन", "વર્ણન", "ਵੇਰਵਾ", "नोट / Description"),
    phrase("Date", "तारीख", "তারিখ", "ದಿನಾಂಕ", "तारीख", "તારીખ", "ਤਾਰੀਖ", "तारीख चुनें"),
    phrase("Total Expense", "कुल खर्च", "মোট খরচ", "ಒಟ್ಟು ಖರ್ಚು", "एकूण खर्च", "કુલ ખર્ચ", "ਕੁੱਲ ਖਰਚਾ"),
    phrase("Total Income", "कुल आय", "মোট আয়", "ಒಟ್ಟು ಆದಾಯ", "एकूण उत्पन्न", "કુલ આવક", "ਕੁੱਲ ਆਮਦਨ", "कुल आय / Total Income"),
    phrase("Bill Details", "बिल विवरण", "বিলের বিবরণ", "ಬಿಲ್ ವಿವರಗಳು", "बिल तपशील", "બિલની વિગતો", "ਬਿੱਲ ਵੇਰਵਾ", "Bill Details / खर्च विवरण"),
    phrase("Open Bill", "बिल खोलें", "বিল খুলুন", "ಬಿಲ್ ತೆರೆಯಿರಿ", "बिल उघडा", "બિલ ખોલો", "ਬਿੱਲ ਖੋਲ੍ਹੋ", "Bill खोलें", "Bill Photo/PDF खोलें"),
    phrase("Attach Bill", "बिल जोड़ें", "বিল সংযুক্ত করুন", "ಬಿಲ್ ಲಗತ್ತಿಸಿ", "बिल जोडा", "બિલ જોડો", "ਬਿੱਲ ਜੋੜੋ", "Bill Photo/PDF जोड़ें"),
    phrase("Camera", "कैमरा", "ক্যামেরা", "ಕ್ಯಾಮೆರಾ", "कॅमेरा", "કૅમેરા", "ਕੈਮਰਾ"),
    phrase("Gallery / PDF", "गैलरी / PDF", "গ্যালারি / PDF", "ಗ್ಯಾಲರಿ / PDF", "गॅलरी / PDF", "ગૅલેરી / PDF", "ਗੈਲਰੀ / PDF"),
    phrase("Item", "आइटम", "আইটেম", "ಐಟಂ", "वस्तू", "વસ્તુ", "ਵਸਤੂ"),
    phrase("All Items", "सभी आइटम", "সব আইটেম", "ಎಲ್ಲಾ ಐಟಂಗಳು", "सर्व वस्तू", "બધી વસ્તુઓ", "ਸਾਰੀਆਂ ਵਸਤੂਆਂ", "सभी Items"),
    phrase("Search Item", "आइटम खोजें", "আইটেম খুঁজুন", "ಐಟಂ ಹುಡುಕಿ", "वस्तू शोधा", "વસ્તુ શોધો", "ਵਸਤੂ ਲੱਭੋ", "Item खोजें / Search Item"),
    phrase("Quantity", "मात्रा", "পরিমাণ", "ಪ್ರಮಾಣ", "प्रमाण", "જથ્થો", "ਮਾਤਰਾ", "Quantity / मात्रा"),
    phrase("Rate", "दर", "দর", "ದರ", "दर", "દર", "ਦਰ", "Rate / दर"),
    phrase("Unit", "इकाई", "একক", "ಘಟಕ", "एकक", "એકમ", "ਇਕਾਈ"),
    phrase("Budget Overview", "बजट सारांश", "বাজেট সারাংশ", "ಬಜೆಟ್ ಅವಲೋಕನ", "बजेट आढावा", "બજેટ સારાંશ", "ਬਜਟ ਸੰਖੇਪ"),
    phrase("Spending Overview", "खर्च सारांश", "খরচের সারাংশ", "ಖರ್ಚಿನ ಅವಲೋಕನ", "खर्च आढावा", "ખર્ચ સારાંશ", "ਖਰਚਾ ਸੰਖੇਪ"),
    phrase("Monthly Trend", "मासिक रुझान", "মাসিক প্রবণতা", "ಮಾಸಿಕ ಪ್ರವೃತ್ತಿ", "मासिक कल", "માસિક વલણ", "ਮਹੀਨਾਵਾਰ ਰੁਝਾਨ"),
    phrase("Recent Transactions", "हाल के लेन-देन", "সাম্প্রতিক লেনদেন", "ಇತ್ತೀಚಿನ ವಹಿವಾಟುಗಳು", "अलीकडील व्यवहार", "તાજેતરના વ્યવહારો", "ਹਾਲੀਆ ਲੈਣ-ਦੇਣ", "हाल के खर्च / Recent Transactions"),
    phrase("No records found", "कोई रिकॉर्ड नहीं मिला", "কোনো রেকর্ড পাওয়া যায়নি", "ಯಾವುದೇ ದಾಖಲೆ ಸಿಗಲಿಲ್ಲ", "कोणतीही नोंद सापडली नाही", "કોઈ રેકોર્ડ મળ્યો નથી", "ਕੋਈ ਰਿਕਾਰਡ ਨਹੀਂ ਮਿਲਿਆ", "कोई item नहीं मिला"),
    phrase("Password", "पासवर्ड", "পাসওয়ার্ড", "ಪಾಸ್‌ವರ್ಡ್", "पासवर्ड", "પાસવર્ડ", "ਪਾਸਵਰਡ"),
    phrase("Email", "ईमेल", "ইমেল", "ಇಮೇಲ್", "ईमेल", "ઈમેલ", "ਈਮੇਲ"),
    phrase("Logout", "लॉग आउट", "লগ আউট", "ಲಾಗ್ ಔಟ್", "लॉग आउट", "લૉગ આઉટ", "ਲੌਗ ਆਉਟ"),
    phrase("Language", "भाषा", "ভাষা", "ಭಾಷೆ", "भाषा", "ભાષા", "ਭਾਸ਼ਾ"),
    phrase("Theme Settings", "थीम सेटिंग", "থিম সেটিংস", "ಥೀಮ್ ಸೆಟ್ಟಿಂಗ್‌ಗಳು", "थीम सेटिंग्ज", "થીમ સેટિંગ્સ", "ਥੀਮ ਸੈਟਿੰਗਾਂ"),
    phrase("Fingerprint Unlock", "फिंगरप्रिंट से खोलें", "ফিঙ্গারপ্রিন্ট দিয়ে খুলুন", "ಬೆರಳಚ್ಚಿನಿಂದ ತೆರೆಯಿರಿ", "फिंगरप्रिंटने उघडा", "ફિંગરપ્રિન્ટથી ખોલો", "ਫਿੰਗਰਪ੍ਰਿੰਟ ਨਾਲ ਖੋਲ੍ਹੋ"),
    phrase("App Lock", "ऐप लॉक", "অ্যাপ লক", "ಆಪ್ ಲಾಕ್", "अॅप लॉक", "એપ લૉક", "ਐਪ ਲੌਕ"),
    phrase("This Month", "इस माह", "এই মাস", "ಈ ತಿಂಗಳು", "या महिन्यात", "આ મહિને", "ਇਸ ਮਹੀਨੇ"),
    phrase("Today’s Expense", "आज का खर्च", "আজকের খরচ", "ಇಂದಿನ ಖರ್ಚು", "आजचा खर्च", "આજનો ખર્ચ", "ਅੱਜ ਦਾ ਖਰਚਾ"),
    phrase("Balance", "शेष", "ব্যালেন্স", "ಬಾಕಿ", "शिल्लक", "બેલેન્સ", "ਬਕਾਇਆ", "इस माह Balance"),
    phrase("Shop / Vendor", "दुकान / विक्रेता", "দোকান / বিক্রেতা", "ಅಂಗಡಿ / ಮಾರಾಟಗಾರ", "दुकान / विक्रेता", "દુકાન / વિક્રેતા", "ਦੁਕਾਨ / ਵਿਕਰੇਤਾ", "दुकान / Shop or Vendor (Optional)"),
    phrase("Party / Shop / Vendor Search", "पार्टी / दुकान / विक्रेता खोजें", "পার্টি / দোকান / বিক্রেতা খুঁজুন", "ಪಾರ್ಟಿ / ಅಂಗಡಿ / ಮಾರಾಟಗಾರ ಹುಡುಕಿ", "पार्टी / दुकान / विक्रेता शोधा", "પાર્ટી / દુકાન / વિક્રેતા શોધો", "ਪਾਰਟੀ / ਦੁਕਾਨ / ਵਿਕਰੇਤਾ ਲੱਭੋ", "Party / Shop / Vendor खोजें"),
    phrase("Brand", "ब्रांड", "ব্র্যান্ড", "ಬ್ರ್ಯಾಂಡ್", "ब्रँड", "બ્રાન્ડ", "ਬ੍ਰਾਂਡ"),
    phrase("Import Photo/PDF", "Photo/PDF Import करें", "Photo/PDF ইমপোর্ট করুন", "Photo/PDF ಆಮದು ಮಾಡಿ", "Photo/PDF इम्पोर्ट करा", "Photo/PDF ઇમ્પોર્ટ કરો", "Photo/PDF ਇੰਪੋਰਟ ਕਰੋ"),
    phrase("View Attachment", "Attachment देखें", "সংযুক্তি দেখুন", "ಲಗತ್ತನ್ನು ನೋಡಿ", "अटॅचमेंट पहा", "અટૅચમેન્ટ જુઓ", "ਅਟੈਚਮੈਂਟ ਵੇਖੋ"),
    phrase("Review Imported Items", "Import किए गए Items जाँचें", "ইমপোর্ট করা আইটেম যাচাই করুন", "ಆಮದು ಮಾಡಿದ ಐಟಂಗಳನ್ನು ಪರಿಶೀಲಿಸಿ", "इम्पोर्ट केलेल्या वस्तू तपासा", "ઇમ્પોર્ટ કરેલી વસ્તુઓ તપાસો", "ਇੰਪੋਰਟ ਕੀਤੀਆਂ ਵਸਤੂਆਂ ਜਾਂਚੋ"),
    phrase("Add to List", "List में जोड़ें", "তালিকায় যোগ করুন", "ಪಟ್ಟಿಗೆ ಸೇರಿಸಿ", "सूचीत जोडा", "યાદીમાં ઉમેરો", "ਸੂਚੀ ਵਿੱਚ ਜੋੜੋ"),
    phrase("Bill / Invoice Number", "बिल / इनवॉइस नंबर", "বিল / ইনভয়েস নম্বর", "ಬಿಲ್ / ಇನ್‌ವಾಯ್ಸ್ ಸಂಖ್ಯೆ", "बिल / इनव्हॉइस क्रमांक", "બિલ / ઇન્વૉઇસ નંબર", "ਬਿੱਲ / ਇਨਵੌਇਸ ਨੰਬਰ"),
    phrase("Scan Bill and Fill Expense", "Bill Scan करके Expense भरें", "বিল স্ক্যান করে খরচ পূরণ করুন", "ಬಿಲ್ ಸ್ಕ್ಯಾನ್ ಮಾಡಿ ಖರ್ಚು ಭರ್ತಿ ಮಾಡಿ", "बिल स्कॅन करून खर्च भरा", "બિલ સ્કૅન કરીને ખર્ચ ભરો", "ਬਿੱਲ ਸਕੈਨ ਕਰਕੇ ਖਰਚਾ ਭਰੋ"),
    phrase("Review Scanned Bill", "Scanned Bill जाँचें", "স্ক্যান করা বিল যাচাই করুন", "ಸ್ಕ್ಯಾನ್ ಮಾಡಿದ ಬಿಲ್ ಪರಿಶೀಲಿಸಿ", "स्कॅन केलेले बिल तपासा", "સ્કૅન કરેલું બિલ તપાસો", "ਸਕੈਨ ਕੀਤਾ ਬਿੱਲ ਜਾਂਚੋ"),
    phrase("Fill Expense", "Expense में भरें", "খরচে পূরণ করুন", "ಖರ್ಚಿನಲ್ಲಿ ಭರ್ತಿ ಮಾಡಿ", "खर्चात भरा", "ખર્ચમાં ભરો", "ਖਰਚੇ ਵਿੱਚ ਭਰੋ")
)

private val uiWords = listOf(
    phrase("New", "नया", "নতুন", "ಹೊಸ", "नवीन", "નવું", "ਨਵਾਂ", "नई"),
    phrase("All", "सभी", "সব", "ಎಲ್ಲಾ", "सर्व", "બધા", "ਸਾਰੇ"),
    phrase("Today", "आज", "আজ", "ಇಂದು", "आज", "આજે", "ਅੱਜ"),
    phrase("This month", "इस माह", "এই মাস", "ಈ ತಿಂಗಳು", "या महिन्यात", "આ મહિને", "ਇਸ ਮਹੀਨੇ", "इस महीने"),
    phrase("Last 6 months", "पिछले 6 महीने", "গত ৬ মাস", "ಕಳೆದ 6 ತಿಂಗಳು", "मागील 6 महिने", "છેલ્લા 6 મહિના", "ਪਿਛਲੇ 6 ਮਹੀਨੇ", "पिछले 6 महीनों"),
    phrase("Expense", "खर्च", "খরচ", "ಖರ್ಚು", "खर्च", "ખર્ચ", "ਖਰਚਾ"),
    phrase("Income", "आय", "আয়", "ಆದಾಯ", "उत्पन्न", "આવક", "ਆਮਦਨ"),
    phrase("Budget", "बजट", "বাজেট", "ಬಜೆಟ್", "बजेट", "બજેટ", "ਬਜਟ"),
    phrase("Report", "रिपोर्ट", "রিপোর্ট", "ವರದಿ", "अहवाल", "રિપોર્ટ", "ਰਿਪੋਰਟ"),
    phrase("History", "इतिहास", "ইতিহাস", "ಇತಿಹಾಸ", "इतिहास", "ઇતિહાસ", "ਇਤਿਹਾਸ"),
    phrase("Item", "आइटम", "আইটেম", "ಐಟಂ", "वस्तू", "વસ્તુ", "ਵਸਤੂ", "item", "Items"),
    phrase("Category", "श्रेणी", "বিভাগ", "ವರ್ಗ", "श्रेणी", "શ્રેણી", "ਸ਼੍ਰੇਣੀ", "Categories"),
    phrase("List", "सूची", "তালিকা", "ಪಟ್ಟಿ", "सूची", "યાદી", "ਸੂਚੀ", "list"),
    phrase("Bill", "बिल", "বিল", "ಬಿಲ್", "बिल", "બિલ", "ਬਿੱਲ"),
    phrase("Goal", "लक्ष्य", "লক্ষ্য", "ಗುರಿ", "उद्दिष्ट", "લક્ષ્ય", "ਟੀਚਾ", "Goals"),
    phrase("Name", "नाम", "নাম", "ಹೆಸರು", "नाव", "નામ", "ਨਾਮ"),
    phrase("Source", "स्रोत", "উৎস", "ಮೂಲ", "स्रोत", "સ્ત્રોત", "ਸਰੋਤ"),
    phrase("Total", "कुल", "মোট", "ಒಟ್ಟು", "एकूण", "કુલ", "ਕੁੱਲ"),
    phrase("Remaining", "शेष", "অবশিষ্ট", "ಉಳಿದ", "उर्वरित", "બાકી", "ਬਾਕੀ", "बचा"),
    phrase("Selected", "चुना गया", "নির্বাচিত", "ಆಯ್ಕೆಮಾಡಲಾಗಿದೆ", "निवडले", "પસંદ કરેલ", "ਚੁਣਿਆ", "चुनी हुई", "चुने गए"),
    phrase("Select", "चुनें", "বেছে নিন", "ಆಯ್ಕೆಮಾಡಿ", "निवडा", "પસંદ કરો", "ਚੁਣੋ"),
    phrase("Search", "खोजें", "খুঁজুন", "ಹುಡುಕಿ", "शोधा", "શોધો", "ਲੱਭੋ"),
    phrase("Open", "खोलें", "খুলুন", "ತೆರೆಯಿರಿ", "उघडा", "ખોલો", "ਖੋਲ੍ਹੋ"),
    phrase("Add", "जोड़ें", "যোগ করুন", "ಸೇರಿಸಿ", "जोडा", "ઉમેરો", "ਜੋੜੋ", "जोड़ा गया"),
    phrase("Save", "सेव करें", "সংরক্ষণ করুন", "ಉಳಿಸಿ", "सेव्ह करा", "સેવ કરો", "ਸੇਵ ਕਰੋ"),
    phrase("Delete", "हटाएँ", "মুছুন", "ಅಳಿಸಿ", "हटवा", "દૂર કરો", "ਹਟਾਓ", "हट जाएगा", "हट जाएँगे"),
    phrase("Cancel", "रद्द करें", "বাতিল করুন", "ರದ್ದುಮಾಡಿ", "रद्द करा", "રદ કરો", "ਰੱਦ ਕਰੋ"),
    phrase("Available", "उपलब्ध", "উপলব্ধ", "ಲಭ್ಯ", "उपलब्ध", "ઉપલબ્ધ", "ਉਪਲਬਧ"),
    phrase("Not available", "उपलब्ध नहीं", "উপলব্ধ নয়", "ಲಭ್ಯವಿಲ್ಲ", "उपलब्ध नाही", "ઉપલબ્ધ નથી", "ਉਪਲਬਧ ਨਹੀਂ"),
    phrase("No record", "कोई रिकॉर्ड नहीं", "কোনো রেকর্ড নেই", "ಯಾವುದೇ ದಾಖಲೆ ಇಲ್ಲ", "कोणतीही नोंद नाही", "કોઈ રેકોર્ડ નથી", "ਕੋਈ ਰਿਕਾਰਡ ਨਹੀਂ", "कोई खर्च नहीं", "कोई Income दर्ज नहीं"),
    phrase("Recorded", "दर्ज", "নথিভুক্ত", "ದಾಖಲಿಸಲಾಗಿದೆ", "नोंदवले", "નોંધાયેલ", "ਦਰਜ"),
    phrase("Month", "माह", "মাস", "ತಿಂಗಳು", "महिना", "મહિનો", "ਮਹੀਨਾ", "महीने"),
    phrase("Purchase", "खरीद", "কেনাকাটা", "ಖರೀದಿ", "खरेदी", "ખરીદી", "ਖਰੀਦ"),
    phrase("Shop", "दुकान", "দোকান", "ಅಂಗಡಿ", "दुकान", "દુકાન", "ਦੁਕਾਨ"),
    phrase("Vendor", "विक्रेता", "বিক্রেতা", "ಮಾರಾಟಗಾರ", "विक्रेता", "વિક્રેતા", "ਵਿਕਰੇਤਾ"),
    phrase("Note", "नोट", "নোট", "ಟಿಪ್ಪಣಿ", "नोंद", "નોંધ", "ਨੋਟ"),
    phrase("Details", "विवरण", "বিবরণ", "ವಿವರಗಳು", "तपशील", "વિગતો", "ਵੇਰਵਾ", "details"),
    phrase("Previous", "पुराना", "পুরোনো", "ಹಳೆಯ", "जुना", "જૂનું", "ਪੁਰਾਣਾ", "पुराने"),
    phrase("Current", "वर्तमान", "বর্তমান", "ಪ್ರಸ್ತುತ", "सध्याचा", "વર્તમાન", "ਮੌਜੂਦਾ"),
    phrase("Settings", "सेटिंग", "সেটিংস", "ಸೆಟ್ಟಿಂಗ್‌ಗಳು", "सेटिंग्ज", "સેટિંગ્સ", "ਸੈਟਿੰਗਾਂ")
)

fun translateUiText(text: String, language: AppLanguage): String {
    if (text.isBlank()) return text
    uiPhrases.firstOrNull { phrase ->
        phrase.sources.any { source -> text.trim().equals(source.trim(), ignoreCase = true) }
    }?.let { return it.values[language] ?: text }

    var result = text
    (uiPhrases + uiWords).sortedByDescending { it.sources.maxOfOrNull(String::length) ?: 0 }
        .forEach { phrase ->
            val target = phrase.values[language] ?: return@forEach
            phrase.sources.sortedByDescending(String::length).forEach { source ->
                if (source.length >= 5) result = result.replace(source, target, ignoreCase = true)
            }
        }
    return result
}
