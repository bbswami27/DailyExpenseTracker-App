package com.bharatbhushan.dailyexpensetracker

fun grocerySeedPart4(): List<ItemMaster> {

    val category = "घरेलू राशन व दैनिक सामान"

    return listOf(
        SeedItem("बादाम", "Almonds", "badam almond", "Kg"),
        SeedItem("काजू", "Cashews", "kaju cashew", "Kg"),
        SeedItem("किशमिश", "Raisins", "kishmish raisins", "Kg"),
        SeedItem("अखरोट", "Walnuts", "akhrot walnut", "Kg"),
        SeedItem("पिस्ता", "Pistachios", "pista pistachio", "Kg"),
        SeedItem("मखाना", "Fox Nuts", "makhana fox nuts", "Packet"),
        SeedItem("खजूर", "Dates", "khajur dates", "Kg"),
        SeedItem("सूखा नारियल", "Dry Coconut", "sukha nariyal", "Piece"),
        SeedItem("सूखी अंजीर", "Dried Figs", "anjeer dried fig", "Kg"),
        SeedItem("मूंगफली", "Peanuts", "moongfali peanut", "Kg"),

        SeedItem("ब्रेड", "Bread", "bread double roti", "Packet"),
        SeedItem("ब्राउन ब्रेड", "Brown Bread", "brown bread", "Packet"),
        SeedItem("बन", "Bread Buns", "bun pav", "Packet"),
        SeedItem("रस्क", "Rusk", "rusk toast", "Packet"),
        SeedItem("बिस्कुट", "Biscuits", "biscuit cookies", "Packet"),
        SeedItem("कुकीज़", "Cookies", "cookies biscuit", "Packet"),
        SeedItem("केक", "Cake", "cake pastry", "Piece"),
        SeedItem("खारी", "Khari Biscuit", "khari puff", "Packet"),
        SeedItem("पिज्जा बेस", "Pizza Base", "pizza base", "Packet"),
        SeedItem("ब्रेड क्रम्ब्स", "Bread Crumbs", "bread crumbs", "Packet"),

        SeedItem("नमकीन", "Namkeen", "namkeen mixture", "Packet"),
        SeedItem("आलू चिप्स", "Potato Chips", "aloo chips", "Packet"),
        SeedItem("केला चिप्स", "Banana Chips", "kela chips", "Packet"),
        SeedItem("भुजिया", "Bhujia", "bhujia sev", "Packet"),
        SeedItem("मठरी", "Mathri", "mathri", "Packet"),
        SeedItem("चकली", "Chakli", "chakli murukku", "Packet"),
        SeedItem("पॉपकॉर्न", "Popcorn", "popcorn makka", "Packet"),
        SeedItem("नाचोज़", "Nachos", "nachos chips", "Packet"),
        SeedItem("खाखरा", "Khakhra", "khakhra", "Packet"),
        SeedItem("रेडीमेड समोसा", "Frozen Samosa", "samosa frozen", "Packet"),

        SeedItem("नूडल्स", "Noodles", "noodles chowmein", "Packet"),
        SeedItem("इंस्टेंट नूडल्स", "Instant Noodles", "maggi instant noodles", "Packet"),
        SeedItem("पास्ता", "Pasta", "pasta macaroni", "Packet"),
        SeedItem("मैकरोनी", "Macaroni", "macaroni pasta", "Packet"),
        SeedItem("कॉर्न फ्लेक्स", "Corn Flakes", "cornflakes cereal", "Packet"),
        SeedItem("म्यूसली", "Muesli", "muesli cereal", "Packet"),
        SeedItem("इडली मिक्स", "Idli Mix", "idli ready mix", "Packet"),
        SeedItem("डोसा मिक्स", "Dosa Mix", "dosa ready mix", "Packet"),
        SeedItem("उपमा मिक्स", "Upma Mix", "upma ready mix", "Packet"),
        SeedItem("सूप पैकेट", "Instant Soup", "soup packet", "Packet"),

        SeedItem("मिनरल वाटर", "Mineral Water", "pani water bottle", "Bottle"),
        SeedItem("सोडा", "Soda Water", "soda water", "Bottle"),
        SeedItem("कोल्ड ड्रिंक", "Soft Drink", "cold drink soda", "Bottle"),
        SeedItem("फलों का जूस", "Fruit Juice", "juice ras", "Pack"),
        SeedItem("नारियल पानी", "Coconut Water", "nariyal pani", "Bottle"),
        SeedItem("एनर्जी ड्रिंक", "Energy Drink", "energy drink", "Can"),
        SeedItem("शरबत", "Sharbat", "sharbat syrup", "Bottle"),
        SeedItem("ग्लूकोज पाउडर", "Glucose Powder", "glucose powder", "Packet"),
        SeedItem("हेल्थ ड्रिंक", "Health Drink", "health drink malt", "Jar"),
        SeedItem("नींबू पानी मिक्स", "Lemon Drink Mix", "nimbu pani mix", "Packet")
    ).map {
        it.toItemMaster(category)
    }
}