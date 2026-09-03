package com.bharatbhushan.dailyexpensetracker

fun personalSeedPart3(): List<ItemMaster> {

    val category = "पर्सनल और डोमेस्टिक खर्च"

    return listOf(
        SeedItem("सैनिटरी पैड", "Sanitary Pads", "sanitary pad mahavari", "Packet"),
        SeedItem("टैम्पोन", "Tampons", "tampon periods", "Packet"),
        SeedItem("मेंस्ट्रुअल कप", "Menstrual Cup", "menstrual cup periods", "Piece"),
        SeedItem("पैंटी लाइनर", "Panty Liners", "panty liner", "Packet"),
        SeedItem("इंटिमेट वॉश", "Intimate Wash", "intimate wash", "Bottle"),

        SeedItem("बेबी डायपर", "Baby Diapers", "baby diaper baccha", "Packet"),
        SeedItem("बेबी वाइप्स", "Baby Wipes", "baby wipes baccha", "Packet"),
        SeedItem("बेबी पाउडर", "Baby Powder", "baby powder baccha", "Bottle"),
        SeedItem("बेबी लोशन", "Baby Lotion", "baby lotion baccha", "Bottle"),
        SeedItem("बेबी ऑयल", "Baby Oil", "baby tel baccha", "Bottle"),
        SeedItem("बेबी साबुन", "Baby Soap", "baby sabun baccha", "Piece"),
        SeedItem("बेबी शैम्पू", "Baby Shampoo", "baby shampoo baccha", "Bottle"),
        SeedItem("बेबी क्रीम", "Baby Cream", "baby cream baccha", "Tube"),
        SeedItem("डायपर रैश क्रीम", "Diaper Rash Cream", "rash cream baby", "Tube"),
        SeedItem("बेबी फीडिंग बोतल", "Baby Feeding Bottle", "feeding bottle baccha", "Piece"),

        SeedItem("एल्युमिनियम फॉयल", "Aluminium Foil", "foil kitchen", "Roll"),
        SeedItem("क्लिंग फिल्म", "Cling Film", "cling wrap kitchen", "Roll"),
        SeedItem("बटर पेपर", "Butter Paper", "butter paper baking", "Roll"),
        SeedItem("पेपर नैपकिन", "Paper Napkins", "napkin tissue", "Packet"),
        SeedItem("पेपर प्लेट", "Paper Plates", "paper plate pattal", "Packet"),
        SeedItem("पेपर कप", "Paper Cups", "paper cup glass", "Packet"),
        SeedItem("डिस्पोजेबल चम्मच", "Disposable Spoons", "disposable spoon chamach", "Packet"),
        SeedItem("माचिस", "Matchbox", "machis matchbox", "Box"),
        SeedItem("मोमबत्ती", "Candles", "mombatti candle", "Packet"),
        SeedItem("सिलाई किट", "Sewing Kit", "silai kit sui dhaga", "Kit")
    ).map {
        it.toItemMaster(category)
    }
}