package com.bharatbhushan.dailyexpensetracker

fun personalSeedPart1(): List<ItemMaster> {

    val category = "पर्सनल और डोमेस्टिक खर्च"

    return listOf(
        SeedItem("नहाने का साबुन", "Bath Soap", "nahane ka sabun soap", "Piece"),
        SeedItem("हैंडवॉश", "Hand Wash", "handwash hath dhone ka sabun", "Bottle"),
        SeedItem("फेसवॉश", "Face Wash", "facewash chehra", "Tube"),
        SeedItem("बॉडी वॉश", "Body Wash", "body wash", "Bottle"),
        SeedItem("शैम्पू", "Shampoo", "shampoo bal", "Bottle"),
        SeedItem("कंडीशनर", "Hair Conditioner", "conditioner bal", "Bottle"),
        SeedItem("हेयर ऑयल", "Hair Oil", "bal tel hair oil", "Bottle"),
        SeedItem("हेयर सीरम", "Hair Serum", "hair serum bal", "Bottle"),
        SeedItem("टूथपेस्ट", "Toothpaste", "toothpaste manjan", "Tube"),
        SeedItem("टूथब्रश", "Toothbrush", "toothbrush dant brush", "Piece"),
        SeedItem("माउथवॉश", "Mouthwash", "mouthwash kulla", "Bottle"),
        SeedItem("डेंटल फ्लॉस", "Dental Floss", "dental floss dant", "Pack"),
        SeedItem("जीभ साफ करने वाला", "Tongue Cleaner", "jeebh cleaner", "Piece"),
        SeedItem("शेविंग क्रीम", "Shaving Cream", "shaving cream dadhi", "Tube"),
        SeedItem("शेविंग रेजर", "Shaving Razor", "razor ustara", "Piece"),
        SeedItem("रेजर ब्लेड", "Razor Blades", "blade shaving", "Packet"),
        SeedItem("आफ्टर शेव", "After Shave", "after shave lotion", "Bottle"),
        SeedItem("डिओडोरेंट", "Deodorant", "deo deodorant", "Can"),
        SeedItem("परफ्यूम", "Perfume", "perfume itr", "Bottle"),
        SeedItem("टैल्कम पाउडर", "Talcum Powder", "talc powder", "Bottle"),

        SeedItem("बॉडी लोशन", "Body Lotion", "body lotion", "Bottle"),
        SeedItem("मॉइस्चराइजर", "Moisturizer", "moisturizer cream", "Tube"),
        SeedItem("कोल्ड क्रीम", "Cold Cream", "cold cream", "Jar"),
        SeedItem("सनस्क्रीन", "Sunscreen", "sunscreen sun cream", "Tube"),
        SeedItem("फेस क्रीम", "Face Cream", "face cream chehra", "Jar"),
        SeedItem("लिप बाम", "Lip Balm", "lip balm hoth", "Piece"),
        SeedItem("फेस स्क्रब", "Face Scrub", "face scrub chehra", "Tube"),
        SeedItem("फेस पैक", "Face Pack", "face pack chehra", "Packet"),
        SeedItem("एलोवेरा जेल", "Aloe Vera Gel", "aloevera gel", "Tube"),
        SeedItem("पेट्रोलियम जेली", "Petroleum Jelly", "vaseline jelly", "Jar"),
        SeedItem("कॉटन बड्स", "Cotton Buds", "cotton buds kan", "Pack"),
        SeedItem("कॉटन बॉल", "Cotton Balls", "cotton ball rui", "Pack"),
        SeedItem("टिश्यू पेपर", "Tissue Paper", "tissue paper", "Box"),
        SeedItem("वेट वाइप्स", "Wet Wipes", "wet wipes", "Packet"),
        SeedItem("रूमाल", "Handkerchief", "rumal hanky", "Piece"),

        SeedItem("कंघी", "Comb", "kanghi comb", "Piece"),
        SeedItem("हेयर ब्रश", "Hair Brush", "hair brush bal", "Piece"),
        SeedItem("हेयर ड्रायर", "Hair Dryer", "hair dryer", "Piece"),
        SeedItem("हेयर जेल", "Hair Gel", "hair gel bal", "Jar"),
        SeedItem("हेयर कलर", "Hair Colour", "hair color bal rang", "Pack"),
        SeedItem("मेहंदी", "Henna", "mehndi henna", "Packet"),
        SeedItem("नेल कटर", "Nail Cutter", "nail cutter nakhun", "Piece"),
        SeedItem("नेल पॉलिश", "Nail Polish", "nail polish nakhun", "Bottle"),
        SeedItem("नेल पॉलिश रिमूवर", "Nail Polish Remover", "nail remover", "Bottle"),
        SeedItem("कैंची", "Personal Scissors", "kainchi scissors", "Piece"),
        SeedItem("चिमटी", "Tweezers", "chimti tweezers", "Piece"),
        SeedItem("मेकअप किट", "Makeup Kit", "makeup kit", "Kit"),
        SeedItem("काजल", "Kajal", "kajal surma", "Piece"),
        SeedItem("लिपस्टिक", "Lipstick", "lipstick hoth", "Piece"),
        SeedItem("बिंदी", "Bindi", "bindi", "Packet")
    ).map {
        it.toItemMaster(category)
    }
}