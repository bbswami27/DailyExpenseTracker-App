package com.bharatbhushan.dailyexpensetracker

fun grocerySeedPart5(): List<ItemMaster> {

    val category = "घरेलू राशन व दैनिक सामान"

    return listOf(
        SeedItem("चिकन", "Chicken", "chicken murga", "Kg"),
        SeedItem("मटन", "Mutton", "mutton bakra meat", "Kg"),
        SeedItem("मछली", "Fish", "machhli fish", "Kg"),
        SeedItem("झींगा", "Prawns", "jhinga prawns shrimp", "Kg"),
        SeedItem("चिकन कीमा", "Chicken Mince", "chicken keema", "Kg"),
        SeedItem("मटन कीमा", "Mutton Mince", "mutton keema", "Kg"),
        SeedItem("चिकन सॉसेज", "Chicken Sausage", "chicken sausage", "Packet"),
        SeedItem("सलामी", "Salami", "salami", "Packet"),
        SeedItem("फिश फिलेट", "Fish Fillet", "fish fillet machhli", "Packet"),
        SeedItem("फ्रोजन चिकन", "Frozen Chicken", "frozen chicken", "Packet"),

        SeedItem("फ्रोजन मटर", "Frozen Peas", "frozen matar", "Packet"),
        SeedItem("फ्रोजन स्वीट कॉर्न", "Frozen Sweet Corn", "frozen corn makka", "Packet"),
        SeedItem("फ्रोजन मिक्स सब्जी", "Frozen Mixed Vegetables", "frozen mix sabji", "Packet"),
        SeedItem("फ्रोजन पराठा", "Frozen Paratha", "frozen parantha", "Packet"),
        SeedItem("फ्रोजन फ्रेंच फ्राइज", "Frozen French Fries", "french fries aloo", "Packet"),
        SeedItem("फ्रोजन मोमोज़", "Frozen Momos", "frozen momos", "Packet"),
        SeedItem("फ्रोजन टिक्की", "Frozen Tikki", "aloo tikki frozen", "Packet"),
        SeedItem("फ्रोजन नगेट्स", "Frozen Nuggets", "nuggets frozen", "Packet"),
        SeedItem("फ्रोजन कबाब", "Frozen Kebab", "kabab kebab frozen", "Packet"),
        SeedItem("फ्रोजन पनीर", "Frozen Paneer", "frozen paneer", "Packet"),

        SeedItem("रसगुल्ला", "Rasgulla", "rasgulla sweet mithai", "Box"),
        SeedItem("गुलाब जामुन", "Gulab Jamun", "gulab jamun mithai", "Box"),
        SeedItem("लड्डू", "Laddu", "laddu ladoo mithai", "Box"),
        SeedItem("बर्फी", "Barfi", "barfi burfi mithai", "Box"),
        SeedItem("जलेबी", "Jalebi", "jalebi mithai", "Kg"),
        SeedItem("पेड़ा", "Peda", "peda mithai", "Box"),
        SeedItem("खीर मिक्स", "Kheer Mix", "kheer mix", "Packet"),
        SeedItem("कस्टर्ड पाउडर", "Custard Powder", "custard powder", "Packet"),
        SeedItem("जेली क्रिस्टल", "Jelly Crystals", "jelly crystals", "Packet"),
        SeedItem("आइसक्रीम", "Ice Cream", "icecream kulfi", "Pack"),

        SeedItem("मेयोनीज़", "Mayonnaise", "mayonnaise mayo", "Bottle"),
        SeedItem("पीनट बटर", "Peanut Butter", "peanut butter moongfali", "Jar"),
        SeedItem("चॉकलेट स्प्रेड", "Chocolate Spread", "chocolate spread", "Jar"),
        SeedItem("जैम", "Jam", "jam murabba", "Jar"),
        SeedItem("शहद", "Honey", "shahad honey", "Bottle"),
        SeedItem("मुरब्बा", "Murabba", "murabba preserve", "Jar"),
        SeedItem("हक्का नूडल्स", "Hakka Noodles", "hakka noodles chowmein", "Packet"),
        SeedItem("चिली फ्लेक्स", "Chilli Flakes", "chilli flakes mirch", "Packet"),
        SeedItem("ओरिगैनो", "Oregano", "oregano herb", "Packet"),
        SeedItem("पास्ता सॉस", "Pasta Sauce", "pasta sauce", "Bottle"),

        SeedItem("ताजा नारियल दूध", "Coconut Milk", "nariyal doodh coconut milk", "Can"),
        SeedItem("कंडेंस्ड मिल्क", "Condensed Milk", "condensed milk meetha doodh", "Can"),
        SeedItem("फूड कलर", "Food Colour", "food colour rang", "Bottle"),
        SeedItem("वनीला एसेंस", "Vanilla Essence", "vanilla essence", "Bottle"),
        SeedItem("यीस्ट", "Yeast", "yeast khamir", "Packet"),
        SeedItem("जिलेटिन", "Gelatin", "gelatin", "Packet"),
        SeedItem("कोको पाउडर", "Cocoa Powder", "cocoa chocolate powder", "Packet"),
        SeedItem("चॉकलेट", "Chocolate", "chocolate", "Piece"),
        SeedItem("स्प्रिंकल्स", "Cake Sprinkles", "sprinkles cake decoration", "Packet"),
        SeedItem("केक मिक्स", "Cake Mix", "cake ready mix", "Packet")
    ).map {
        it.toItemMaster(category)
    }
}