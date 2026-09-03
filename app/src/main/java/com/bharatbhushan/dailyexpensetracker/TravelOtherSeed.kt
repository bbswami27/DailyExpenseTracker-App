package com.bharatbhushan.dailyexpensetracker

fun travelOtherSeed(): List<ItemMaster> {

    val category = "मनोरंजन, यात्रा एवं अन्य"

    return listOf(
        SeedItem("पेट्रोल", "Petrol", "petrol fuel gadi", "Litre"),
        SeedItem("डीजल", "Diesel", "diesel fuel gadi", "Litre"),
        SeedItem("सीएनजी", "CNG", "cng gas gadi", "Kg"),
        SeedItem("ऑटो किराया", "Auto Fare", "auto kiraya", "Trip"),
        SeedItem("टैक्सी किराया", "Taxi Fare", "taxi cab kiraya", "Trip"),
        SeedItem("बस टिकट", "Bus Ticket", "bus ticket kiraya", "Ticket"),
        SeedItem("ट्रेन टिकट", "Train Ticket", "rail railway ticket", "Ticket"),
        SeedItem("फ्लाइट टिकट", "Flight Ticket", "flight hawai ticket", "Ticket"),
        SeedItem("होटल खर्च", "Hotel Expense", "hotel room kharch", "Night"),
        SeedItem("यात्रा भोजन", "Travel Food", "safar khana food", "Meal"),

        SeedItem("फिल्म टिकट", "Movie Ticket", "film cinema ticket", "Ticket"),
        SeedItem("रेस्टोरेंट भोजन", "Restaurant Meal", "restaurant khana", "Meal"),
        SeedItem("फास्ट फूड", "Fast Food", "fast food burger pizza", "Meal"),
        SeedItem("पार्टी खर्च", "Party Expense", "party function kharch", "Payment"),
        SeedItem("पिकनिक खर्च", "Picnic Expense", "picnic outing kharch", "Payment"),
        SeedItem("गेमिंग", "Gaming Expense", "gaming game kharch", "Payment"),
        SeedItem("खेल शुल्क", "Sports Fee", "sports khel fees", "Payment"),
        SeedItem("जिम फीस", "Gym Fee", "gym fitness fees", "Payment"),
        SeedItem("मनोरंजन पार्क टिकट", "Amusement Park Ticket", "park mela ticket", "Ticket"),
        SeedItem("पर्यटन टिकट", "Tourist Entry Ticket", "tourist ghumna ticket", "Ticket"),

        SeedItem("उपहार", "Gift", "gift uphar", "Piece"),
        SeedItem("दान", "Donation", "daan donation", "Payment"),
        SeedItem("धार्मिक खर्च", "Religious Expense", "puja mandir dharmik kharch", "Payment"),
        SeedItem("पालतू पशु खर्च", "Pet Expense", "pet kutta billi expense", "Payment"),
        SeedItem("अन्य खर्च", "Other Expense", "anya other miscellaneous", "Payment")
    ).map {
        it.toItemMaster(category)
    }
}