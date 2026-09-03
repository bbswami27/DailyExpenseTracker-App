package com.bharatbhushan.dailyexpensetracker

fun loanPaymentSeedPart2(): List<ItemMaster> {

    val category = "लोन, EMI और भुगतान"

    return listOf(
        SeedItem("दुकान किराया", "Shop Rent", "dukan kiraya rent", "Payment"),
        SeedItem("ऑफिस किराया", "Office Rent", "office kiraya rent", "Payment"),
        SeedItem("गोदाम किराया", "Warehouse Rent", "godown kiraya rent", "Payment"),
        SeedItem("जमीन लीज भुगतान", "Land Lease Payment", "jamin lease kiraya", "Payment"),
        SeedItem("सिक्योरिटी डिपॉजिट", "Security Deposit", "security jama deposit", "Payment"),

        SeedItem("इनकम टैक्स", "Income Tax", "income tax aaykar", "Payment"),
        SeedItem("एडवांस टैक्स", "Advance Tax", "advance tax", "Payment"),
        SeedItem("प्रोफेशनल टैक्स", "Professional Tax", "professional tax", "Payment"),
        SeedItem("जीएसटी भुगतान", "GST Payment", "gst tax bhugtan", "Payment"),
        SeedItem("नगरपालिका शुल्क", "Municipal Charges", "nagar palika charge", "Payment"),

        SeedItem("वाहन रोड टैक्स", "Vehicle Road Tax", "road tax gadi", "Payment"),
        SeedItem("वाहन चालान", "Traffic Challan", "traffic challan jurmana", "Payment"),
        SeedItem("टोल भुगतान", "Toll Payment", "toll tax fastag", "Payment"),
        SeedItem("फास्टैग रिचार्ज", "FASTag Recharge", "fastag recharge toll", "Recharge"),
        SeedItem("पार्किंग शुल्क", "Parking Charges", "parking charge", "Payment"),

        SeedItem("म्यूचुअल फंड SIP", "Mutual Fund SIP", "mutual fund sip nivesh", "Investment"),
        SeedItem("आरडी जमा", "Recurring Deposit", "rd jama deposit", "Investment"),
        SeedItem("पीपीएफ जमा", "PPF Deposit", "ppf jama", "Investment"),
        SeedItem("एनपीएस योगदान", "NPS Contribution", "nps pension contribution", "Investment"),
        SeedItem("सोना बचत योजना", "Gold Saving Scheme", "gold sona saving", "Investment"),

        SeedItem("ओटीटी सब्सक्रिप्शन", "OTT Subscription", "ott netflix subscription", "Payment"),
        SeedItem("सॉफ्टवेयर सब्सक्रिप्शन", "Software Subscription", "software app subscription", "Payment"),
        SeedItem("क्लब सदस्यता", "Club Membership", "club membership", "Payment"),
        SeedItem("बैंक शुल्क", "Bank Charges", "bank charge fees", "Payment"),
        SeedItem("लेट फीस और जुर्माना", "Late Fee and Penalty", "late fee jurmana penalty", "Payment")
    ).map {
        it.toItemMaster(category)
    }
}