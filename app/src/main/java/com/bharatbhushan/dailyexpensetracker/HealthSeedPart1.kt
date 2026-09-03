package com.bharatbhushan.dailyexpensetracker

fun healthSeedPart1(): List<ItemMaster> {

    val category = "स्वास्थ्य एवं चिकित्सा"

    return listOf(
        SeedItem("डॉक्टर फीस", "Doctor Consultation Fee", "doctor fees paramarsh", "Visit"),
        SeedItem("विशेषज्ञ डॉक्टर फीस", "Specialist Consultation", "specialist doctor fees", "Visit"),
        SeedItem("अस्पताल भर्ती खर्च", "Hospital Admission", "hospital bharti expense", "Payment"),
        SeedItem("इमरजेंसी शुल्क", "Emergency Charges", "emergency charge", "Payment"),
        SeedItem("ऑपरेशन खर्च", "Surgery Expense", "operation surgery expense", "Payment"),

        SeedItem("ब्लड टेस्ट", "Blood Test", "blood khoon test", "Test"),
        SeedItem("यूरिन टेस्ट", "Urine Test", "urine peshab test", "Test"),
        SeedItem("एक्स-रे", "X-Ray", "xray test", "Test"),
        SeedItem("अल्ट्रासाउंड", "Ultrasound", "ultrasound sonography", "Test"),
        SeedItem("एमआरआई स्कैन", "MRI Scan", "mri scan", "Test"),
        SeedItem("सीटी स्कैन", "CT Scan", "ct scan", "Test"),
        SeedItem("ईसीजी", "ECG", "ecg heart test", "Test"),
        SeedItem("आँखों की जाँच", "Eye Checkup", "aankh eye checkup", "Test"),
        SeedItem("दाँतों की जाँच", "Dental Checkup", "dant dental checkup", "Visit"),
        SeedItem("फिजियोथेरेपी", "Physiotherapy", "physio therapy", "Session"),

        SeedItem("प्रिस्क्रिप्शन दवाई", "Prescription Medicine", "doctor dawa medicine", "Packet"),
        SeedItem("बुखार की दवाई", "Fever Medicine", "bukhar dawa", "Packet"),
        SeedItem("सर्दी-खाँसी की दवाई", "Cold and Cough Medicine", "sardi khansi dawa", "Bottle"),
        SeedItem("दर्द निवारक दवाई", "Pain Relief Medicine", "dard pain killer dawa", "Packet"),
        SeedItem("एंटीबायोटिक", "Antibiotic", "antibiotic dawa", "Packet"),
        SeedItem("विटामिन सप्लीमेंट", "Vitamin Supplement", "vitamin supplement", "Bottle"),
        SeedItem("कैल्शियम सप्लीमेंट", "Calcium Supplement", "calcium haddi supplement", "Bottle"),
        SeedItem("आयुर्वेदिक दवाई", "Ayurvedic Medicine", "ayurvedic dawa", "Packet"),
        SeedItem("होम्योपैथिक दवाई", "Homeopathic Medicine", "homeopathy dawa", "Bottle"),
        SeedItem("मेडिकल उपकरण", "Medical Equipment", "medical upkaran device", "Piece")
    ).map {
        it.toItemMaster(category)
    }
}