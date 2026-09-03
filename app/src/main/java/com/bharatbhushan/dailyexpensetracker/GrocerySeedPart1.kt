package com.bharatbhushan.dailyexpensetracker

fun grocerySeedPart1(): List<ItemMaster> {

    val category = "घरेलू राशन व दैनिक सामान"

    return listOf(
        SeedItem("चावल", "Rice", "chawal rice", "Kg"),
        SeedItem("बासमती चावल", "Basmati Rice", "basmati chawal", "Kg"),
        SeedItem("ब्राउन चावल", "Brown Rice", "brown chawal", "Kg"),
        SeedItem("टूटा चावल", "Broken Rice", "tukda chawal", "Kg"),
        SeedItem("गेहूँ", "Wheat", "gehun gehu wheat", "Kg"),
        SeedItem("गेहूँ का आटा", "Wheat Flour", "atta gehun flour", "Kg"),
        SeedItem("मैदा", "Refined Flour", "maida refined flour", "Kg"),
        SeedItem("सूजी", "Semolina", "suji rawa rava", "Kg"),
        SeedItem("बेसन", "Gram Flour", "besan chana flour", "Kg"),
        SeedItem("मक्के का आटा", "Maize Flour", "makki atta corn flour", "Kg"),
        SeedItem("बाजरे का आटा", "Pearl Millet Flour", "bajra atta", "Kg"),
        SeedItem("ज्वार का आटा", "Sorghum Flour", "jowar atta", "Kg"),
        SeedItem("रागी का आटा", "Ragi Flour", "ragi mandua atta", "Kg"),
        SeedItem("जौ", "Barley", "jau barley", "Kg"),
        SeedItem("ओट्स", "Oats", "oats jai", "Packet"),
        SeedItem("पोहा", "Flattened Rice", "poha chiwda", "Kg"),
        SeedItem("मुरमुरा", "Puffed Rice", "murmura kurmura", "Packet"),
        SeedItem("साबूदाना", "Tapioca Pearls", "sabudana sago", "Kg"),
        SeedItem("दलिया", "Broken Wheat", "daliya broken wheat", "Kg"),
        SeedItem("सेवई", "Vermicelli", "sevai semiya", "Packet"),

        SeedItem("अरहर दाल", "Pigeon Pea Lentil", "arhar toor dal", "Kg"),
        SeedItem("मूंग दाल", "Moong Dal", "moong mung dal", "Kg"),
        SeedItem("मूंग साबुत", "Whole Green Gram", "sabut moong", "Kg"),
        SeedItem("उड़द दाल", "Urad Dal", "urad dal", "Kg"),
        SeedItem("उड़द साबुत", "Whole Black Gram", "sabut urad", "Kg"),
        SeedItem("मसूर दाल", "Red Lentil", "masoor dal", "Kg"),
        SeedItem("मसूर साबुत", "Whole Masoor", "sabut masoor", "Kg"),
        SeedItem("चना दाल", "Chana Dal", "chana dal", "Kg"),
        SeedItem("काबुली चना", "Chickpeas", "kabuli chana chhole", "Kg"),
        SeedItem("काला चना", "Black Chickpeas", "kala chana", "Kg"),
        SeedItem("राजमा", "Kidney Beans", "rajma kidney beans", "Kg"),
        SeedItem("लोबिया", "Black Eyed Peas", "lobia rongi", "Kg"),
        SeedItem("सूखी मटर", "Dried Peas", "sukhi matar", "Kg"),
        SeedItem("मोठ दाल", "Moth Beans", "moth dal matki", "Kg"),
        SeedItem("कुल्थी दाल", "Horse Gram", "kulthi dal", "Kg"),
        SeedItem("सोयाबीन", "Soybean", "soyabean soya", "Kg"),
        SeedItem("सोया बड़ी", "Soya Chunks", "soya badi chunks", "Packet"),
        SeedItem("मिक्स दाल", "Mixed Lentils", "mix dal", "Kg"),
        SeedItem("भुना चना", "Roasted Gram", "bhuna chana", "Kg"),
        SeedItem("सत्तू", "Roasted Gram Flour", "sattu", "Kg"),

        SeedItem("नमक", "Salt", "namak salt", "Kg"),
        SeedItem("सेंधा नमक", "Rock Salt", "sendha namak", "Packet"),
        SeedItem("काला नमक", "Black Salt", "kala namak", "Packet"),
        SeedItem("चीनी", "Sugar", "chini shakkar sugar", "Kg"),
        SeedItem("गुड़", "Jaggery", "gud gur jaggery", "Kg"),
        SeedItem("मिश्री", "Rock Sugar", "mishri", "Packet"),
        SeedItem("चाय पत्ती", "Tea Leaves", "chai patti tea", "Packet"),
        SeedItem("कॉफी", "Coffee", "coffee kafi", "Packet"),
        SeedItem("ग्रीन टी", "Green Tea", "green tea hari chai", "Box"),
        SeedItem("दूध पाउडर", "Milk Powder", "milk powder doodh", "Packet")
    ).map {
        it.toItemMaster(category)
    }
}