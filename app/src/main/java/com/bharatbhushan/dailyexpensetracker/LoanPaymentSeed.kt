package com.bharatbhushan.dailyexpensetracker

fun loanPaymentSeed(): List<ItemMaster> {

    val category = "लोन, EMI और भुगतान"

    return listOf(
        SeedItem("होम लोन EMI", "Home Loan EMI", "home loan makan emi", "Payment"),
        SeedItem("कार लोन EMI", "Car Loan EMI", "car gadi loan emi", "Payment"),
        SeedItem("बाइक लोन EMI", "Bike Loan EMI", "bike motorcycle loan emi", "Payment"),
        SeedItem("पर्सनल लोन EMI", "Personal Loan EMI", "personal loan emi", "Payment"),
        SeedItem("एजुकेशन लोन EMI", "Education Loan EMI", "education padhai loan emi", "Payment"),
        SeedItem("गोल्ड लोन भुगतान", "Gold Loan Payment", "gold sona loan", "Payment"),
        SeedItem("बिजनेस लोन EMI", "Business Loan EMI", "business vyapar loan", "Payment"),
        SeedItem("कृषि लोन भुगतान", "Agriculture Loan Payment", "agriculture kheti loan", "Payment"),
        SeedItem("क्रेडिट कार्ड बिल", "Credit Card Bill", "credit card bill", "Payment"),
        SeedItem("उधार वापसी", "Debt Repayment", "udhar wapsi karz", "Payment"),

        SeedItem("बिजली बिल", "Electricity Bill", "bijli bill light", "Bill"),
        SeedItem("पानी बिल", "Water Bill", "pani bill", "Bill"),
        SeedItem("गैस बिल", "Gas Bill", "gas bill", "Bill"),
        SeedItem("मोबाइल बिल", "Mobile Bill", "mobile phone bill", "Bill"),
        SeedItem("मोबाइल रिचार्ज", "Mobile Recharge", "mobile recharge", "Recharge"),
        SeedItem("ब्रॉडबैंड बिल", "Broadband Bill", "internet wifi bill", "Bill"),
        SeedItem("डीटीएच रिचार्ज", "DTH Recharge", "dth tv recharge", "Recharge"),
        SeedItem("किराया भुगतान", "Rent Payment", "kiraya rent", "Payment"),
        SeedItem("मेंटेनेंस शुल्क", "Maintenance Charges", "maintenance society charge", "Payment"),
        SeedItem("प्रॉपर्टी टैक्स", "Property Tax", "property makan tax", "Payment"),

        SeedItem("जीवन बीमा प्रीमियम", "Life Insurance Premium", "life insurance bima premium", "Payment"),
        SeedItem("स्वास्थ्य बीमा प्रीमियम", "Health Insurance Premium", "health medical bima premium", "Payment"),
        SeedItem("वाहन बीमा प्रीमियम", "Vehicle Insurance Premium", "vehicle gadi bima premium", "Payment"),
        SeedItem("स्कूल फीस", "School Fees", "school fees baccha", "Payment"),
        SeedItem("कॉलेज फीस", "College Fees", "college fees padhai", "Payment")
    ).map {
        it.toItemMaster(category)
    }
}