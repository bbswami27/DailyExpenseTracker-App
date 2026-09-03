package com.bharatbhushan.dailyexpensetracker

fun grocerySeedPart2(): List<ItemMaster> {

    val category = "घरेलू राशन व दैनिक सामान"

    return listOf(
        SeedItem("हल्दी पाउडर", "Turmeric Powder", "haldi turmeric", "Packet"),
        SeedItem("लाल मिर्च पाउडर", "Red Chilli Powder", "lal mirch chilli", "Packet"),
        SeedItem("धनिया पाउडर", "Coriander Powder", "dhania coriander", "Packet"),
        SeedItem("गरम मसाला", "Garam Masala", "garam masala", "Packet"),
        SeedItem("चाट मसाला", "Chaat Masala", "chat masala", "Packet"),
        SeedItem("सब्जी मसाला", "Vegetable Masala", "sabji masala", "Packet"),
        SeedItem("चना मसाला", "Chana Masala", "chhole chana masala", "Packet"),
        SeedItem("राजमा मसाला", "Rajma Masala", "rajma masala", "Packet"),
        SeedItem("सांभर मसाला", "Sambar Masala", "sambhar masala", "Packet"),
        SeedItem("पाव भाजी मसाला", "Pav Bhaji Masala", "pav bhaji masala", "Packet"),
        SeedItem("बिरयानी मसाला", "Biryani Masala", "biryani masala", "Packet"),
        SeedItem("चिकन मसाला", "Chicken Masala", "chicken murga masala", "Packet"),
        SeedItem("मीट मसाला", "Meat Masala", "meat mutton masala", "Packet"),
        SeedItem("काली मिर्च", "Black Pepper", "kali mirch pepper", "Packet"),
        SeedItem("सफेद मिर्च", "White Pepper", "safed mirch pepper", "Packet"),
        SeedItem("जीरा", "Cumin Seeds", "jeera cumin", "Packet"),
        SeedItem("शाही जीरा", "Caraway Seeds", "shahi jeera", "Packet"),
        SeedItem("धनिया साबुत", "Coriander Seeds", "sabut dhania", "Packet"),
        SeedItem("राई", "Mustard Seeds", "rai sarson seeds", "Packet"),
        SeedItem("मेथी दाना", "Fenugreek Seeds", "methi dana", "Packet"),
        SeedItem("अजवाइन", "Carom Seeds", "ajwain", "Packet"),
        SeedItem("सौंफ", "Fennel Seeds", "saunf fennel", "Packet"),
        SeedItem("कलौंजी", "Nigella Seeds", "kalonji mangrail", "Packet"),
        SeedItem("हींग", "Asafoetida", "hing heeng", "Packet"),
        SeedItem("तेज पत्ता", "Bay Leaf", "tej patta", "Packet"),
        SeedItem("दालचीनी", "Cinnamon", "dalchini cinnamon", "Packet"),
        SeedItem("लौंग", "Cloves", "laung long cloves", "Packet"),
        SeedItem("हरी इलायची", "Green Cardamom", "hari elaichi", "Packet"),
        SeedItem("काली इलायची", "Black Cardamom", "kali elaichi", "Packet"),
        SeedItem("जायफल", "Nutmeg", "jaiphal nutmeg", "Piece"),

        SeedItem("सरसों तेल", "Mustard Oil", "sarson tel oil", "Litre"),
        SeedItem("रिफाइंड तेल", "Refined Oil", "refined tel oil", "Litre"),
        SeedItem("सूरजमुखी तेल", "Sunflower Oil", "surajmukhi tel", "Litre"),
        SeedItem("सोयाबीन तेल", "Soybean Oil", "soyabean tel", "Litre"),
        SeedItem("मूंगफली तेल", "Groundnut Oil", "moongfali tel", "Litre"),
        SeedItem("तिल का तेल", "Sesame Oil", "til tel sesame", "Litre"),
        SeedItem("नारियल तेल", "Coconut Oil", "nariyal tel coconut", "Litre"),
        SeedItem("जैतून तेल", "Olive Oil", "jaitun olive tel", "Litre"),
        SeedItem("देशी घी", "Desi Ghee", "desi ghee", "Kg"),
        SeedItem("वनस्पति घी", "Vanaspati Ghee", "vanaspati dalda", "Kg"),

        SeedItem("टमाटर सॉस", "Tomato Ketchup", "tomato sauce ketchup", "Bottle"),
        SeedItem("चिली सॉस", "Chilli Sauce", "chilli sauce mirch", "Bottle"),
        SeedItem("सोया सॉस", "Soy Sauce", "soya sauce", "Bottle"),
        SeedItem("सिरका", "Vinegar", "sirka vinegar", "Bottle"),
        SeedItem("अचार", "Pickle", "achar pickle", "Jar"),
        SeedItem("पापड़", "Papad", "papad", "Packet"),
        SeedItem("इमली", "Tamarind", "imli tamarind", "Packet"),
        SeedItem("बेकिंग सोडा", "Baking Soda", "baking soda meetha soda", "Packet"),
        SeedItem("बेकिंग पाउडर", "Baking Powder", "baking powder", "Packet"),
        SeedItem("कॉर्न फ्लोर", "Corn Flour", "corn flour makai starch", "Packet")
    ).map {
        it.toItemMaster(category)
    }
}