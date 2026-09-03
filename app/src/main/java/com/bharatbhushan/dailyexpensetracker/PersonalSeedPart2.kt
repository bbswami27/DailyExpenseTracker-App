package com.bharatbhushan.dailyexpensetracker

fun personalSeedPart2(): List<ItemMaster> {

    val category = "पर्सनल और डोमेस्टिक खर्च"

    return listOf(
        SeedItem("कपड़े धोने का पाउडर", "Laundry Detergent Powder", "washing powder detergent", "Kg"),
        SeedItem("लिक्विड डिटर्जेंट", "Liquid Detergent", "liquid detergent kapde", "Bottle"),
        SeedItem("कपड़े धोने का साबुन", "Laundry Soap", "kapde dhone ka sabun", "Piece"),
        SeedItem("फैब्रिक सॉफ्टनर", "Fabric Softener", "fabric softener kapde", "Bottle"),
        SeedItem("स्टेन रिमूवर", "Stain Remover", "daag remover kapde", "Bottle"),
        SeedItem("ब्लीच", "Bleach", "bleach safedi", "Bottle"),
        SeedItem("नील", "Fabric Whitener", "neel whitener", "Bottle"),
        SeedItem("बर्तन धोने का साबुन", "Dishwash Bar", "bartan sabun", "Piece"),
        SeedItem("डिशवॉश लिक्विड", "Dishwash Liquid", "bartan liquid", "Bottle"),
        SeedItem("फ्लोर क्लीनर", "Floor Cleaner", "farsh cleaner phenyl", "Bottle"),
        SeedItem("फिनाइल", "Phenyl", "phenyl floor cleaner", "Bottle"),
        SeedItem("टॉयलेट क्लीनर", "Toilet Cleaner", "toilet cleaner", "Bottle"),
        SeedItem("बाथरूम क्लीनर", "Bathroom Cleaner", "bathroom cleaner", "Bottle"),
        SeedItem("ग्लास क्लीनर", "Glass Cleaner", "sheesha cleaner", "Bottle"),
        SeedItem("किचन क्लीनर", "Kitchen Cleaner", "rasoi cleaner", "Bottle"),
        SeedItem("डिसइन्फेक्टेंट", "Disinfectant", "disinfectant kitanu", "Bottle"),
        SeedItem("सैनिटाइजर", "Sanitizer", "sanitizer hath", "Bottle"),
        SeedItem("नाली साफ करने वाला", "Drain Cleaner", "nali cleaner", "Packet"),
        SeedItem("चिमनी क्लीनर", "Chimney Cleaner", "chimney cleaner", "Bottle"),
        SeedItem("फर्नीचर पॉलिश", "Furniture Polish", "furniture polish", "Bottle"),

        SeedItem("झाड़ू", "Broom", "jhadu broom", "Piece"),
        SeedItem("पोछा", "Floor Mop", "pocha mop", "Piece"),
        SeedItem("मोप रिफिल", "Mop Refill", "mop refill pocha", "Piece"),
        SeedItem("डस्टर", "Cleaning Duster", "duster kapda", "Piece"),
        SeedItem("स्क्रब पैड", "Scrub Pad", "scrub pad bartan", "Piece"),
        SeedItem("स्टील स्क्रबर", "Steel Scrubber", "steel scrubber juna", "Piece"),
        SeedItem("टॉयलेट ब्रश", "Toilet Brush", "toilet brush", "Piece"),
        SeedItem("बोतल साफ करने का ब्रश", "Bottle Cleaning Brush", "bottle brush", "Piece"),
        SeedItem("कपड़े साफ करने का ब्रश", "Laundry Brush", "kapda brush", "Piece"),
        SeedItem("डस्टपैन", "Dustpan", "dustpan kachra", "Piece"),
        SeedItem("कूड़ेदान", "Dustbin", "dustbin kachra dabba", "Piece"),
        SeedItem("कचरा बैग", "Garbage Bags", "kachra bag dustbin bag", "Roll"),
        SeedItem("रबर के दस्ताने", "Rubber Gloves", "rubber gloves safai", "Pair"),
        SeedItem("वाइपर", "Floor Wiper", "wiper floor", "Piece"),
        SeedItem("सफाई स्पंज", "Cleaning Sponge", "sponge safai", "Piece"),

        SeedItem("कपड़े सुखाने की रस्सी", "Clothesline", "kapde rassi", "Piece"),
        SeedItem("कपड़े की क्लिप", "Cloth Clips", "kapde clip", "Packet"),
        SeedItem("हैंगर", "Clothes Hanger", "hanger kapde", "Piece"),
        SeedItem("इस्त्री", "Electric Iron", "istri press", "Piece"),
        SeedItem("इस्त्री कवर", "Ironing Board Cover", "istri cover", "Piece"),
        SeedItem("लॉन्ड्री बैग", "Laundry Bag", "laundry bag kapde", "Piece"),
        SeedItem("जूता पॉलिश", "Shoe Polish", "juta polish", "Tin"),
        SeedItem("जूता ब्रश", "Shoe Brush", "juta brush", "Piece"),
        SeedItem("एयर फ्रेशनर", "Air Freshener", "room freshener khushboo", "Can"),
        SeedItem("मच्छर स्प्रे", "Mosquito Spray", "machhar spray", "Can"),
        SeedItem("मच्छर कॉइल", "Mosquito Coil", "machhar coil", "Packet"),
        SeedItem("मच्छर मशीन रिफिल", "Mosquito Repellent Refill", "machhar refill", "Piece"),
        SeedItem("कॉकरोच स्प्रे", "Cockroach Spray", "cockroach spray", "Can"),
        SeedItem("चूहा मार दवा", "Rat Control", "chuha mar dawa", "Packet"),
        SeedItem("कीटनाशक चॉक", "Insect Control Chalk", "kitnashak chalk", "Piece")
    ).map {
        it.toItemMaster(category)
    }
}