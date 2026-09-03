package com.bharatbhushan.dailyexpensetracker

fun grocerySeedPart3(): List<ItemMaster> {

    val category = "घरेलू राशन व दैनिक सामान"

    return listOf(
        SeedItem("दूध", "Milk", "doodh milk", "Litre"),
        SeedItem("दही", "Curd", "dahi curd yogurt", "Kg"),
        SeedItem("पनीर", "Paneer", "paneer cottage cheese", "Kg"),
        SeedItem("मक्खन", "Butter", "makhan butter", "Packet"),
        SeedItem("छाछ", "Buttermilk", "chhach lassi buttermilk", "Litre"),
        SeedItem("क्रीम", "Fresh Cream", "cream malai", "Packet"),
        SeedItem("चीज", "Cheese", "cheese chiz", "Packet"),
        SeedItem("खोया", "Khoya", "khoya mawa", "Kg"),
        SeedItem("अंडे", "Eggs", "anda ande eggs", "Dozen"),
        SeedItem("टोफू", "Tofu", "tofu soya paneer", "Packet"),

        SeedItem("आलू", "Potato", "aloo potato", "Kg"),
        SeedItem("प्याज", "Onion", "pyaj onion", "Kg"),
        SeedItem("टमाटर", "Tomato", "tamatar tomato", "Kg"),
        SeedItem("लहसुन", "Garlic", "lahsun garlic", "Kg"),
        SeedItem("अदरक", "Ginger", "adrak ginger", "Kg"),
        SeedItem("हरी मिर्च", "Green Chilli", "hari mirch chilli", "Kg"),
        SeedItem("धनिया पत्ती", "Fresh Coriander", "hara dhania", "Bunch"),
        SeedItem("पालक", "Spinach", "palak spinach", "Bunch"),
        SeedItem("मेथी पत्ती", "Fenugreek Leaves", "methi patta", "Bunch"),
        SeedItem("फूलगोभी", "Cauliflower", "phool gobhi", "Piece"),
        SeedItem("पत्तागोभी", "Cabbage", "patta gobhi", "Piece"),
        SeedItem("भिंडी", "Lady Finger", "bhindi okra", "Kg"),
        SeedItem("बैंगन", "Brinjal", "baingan eggplant", "Kg"),
        SeedItem("लौकी", "Bottle Gourd", "lauki ghiya", "Piece"),
        SeedItem("तोरई", "Ridge Gourd", "torai tori", "Kg"),
        SeedItem("करेला", "Bitter Gourd", "karela", "Kg"),
        SeedItem("कद्दू", "Pumpkin", "kaddu pumpkin", "Kg"),
        SeedItem("खीरा", "Cucumber", "kheera cucumber", "Kg"),
        SeedItem("गाजर", "Carrot", "gajar carrot", "Kg"),
        SeedItem("मूली", "Radish", "mooli radish", "Kg"),
        SeedItem("शलजम", "Turnip", "shaljam turnip", "Kg"),
        SeedItem("हरी मटर", "Green Peas", "hari matar peas", "Kg"),
        SeedItem("शिमला मिर्च", "Capsicum", "shimla mirch capsicum", "Kg"),
        SeedItem("फ्रेंच बीन्स", "French Beans", "beans sem", "Kg"),
        SeedItem("शकरकंद", "Sweet Potato", "shakarkand", "Kg"),
        SeedItem("अरबी", "Colocasia", "arbi ghuiya", "Kg"),
        SeedItem("नींबू", "Lemon", "nimbu lemon", "Piece"),
        SeedItem("मशरूम", "Mushroom", "mushroom khumbi", "Packet"),
        SeedItem("ब्रोकली", "Broccoli", "broccoli hari gobhi", "Piece"),
        SeedItem("कच्चा केला", "Raw Banana", "kacha kela", "Piece"),
        SeedItem("परवल", "Pointed Gourd", "parwal", "Kg"),
        SeedItem("सहजन", "Drumstick", "sahjan drumstick", "Kg"),
        SeedItem("कमल ककड़ी", "Lotus Stem", "kamal kakdi", "Kg"),
        SeedItem("चुकंदर", "Beetroot", "chukandar beetroot", "Kg"),
        SeedItem("हरा प्याज", "Spring Onion", "hara pyaj", "Bunch"),

        SeedItem("सेब", "Apple", "seb apple", "Kg"),
        SeedItem("केला", "Banana", "kela banana", "Dozen"),
        SeedItem("संतरा", "Orange", "santra orange", "Kg"),
        SeedItem("अंगूर", "Grapes", "angoor grapes", "Kg"),
        SeedItem("अमरूद", "Guava", "amrood guava", "Kg"),
        SeedItem("पपीता", "Papaya", "papita papaya", "Piece"),
        SeedItem("अनार", "Pomegranate", "anar pomegranate", "Kg"),
        SeedItem("आम", "Mango", "aam mango", "Kg"),
        SeedItem("तरबूज", "Watermelon", "tarbooj watermelon", "Piece"),
        SeedItem("खरबूजा", "Muskmelon", "kharbooja muskmelon", "Piece"),
        SeedItem("अनानास", "Pineapple", "ananas pineapple", "Piece"),
        SeedItem("नाशपाती", "Pear", "nashpati pear", "Kg"),
        SeedItem("कीवी", "Kiwi", "kiwi", "Piece"),
        SeedItem("नारियल", "Coconut", "nariyal coconut", "Piece"),
        SeedItem("चीकू", "Sapota", "chikoo sapota", "Kg")
    ).map {
        it.toItemMaster(category)
    }
}