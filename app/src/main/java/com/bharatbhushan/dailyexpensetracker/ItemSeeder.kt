package com.bharatbhushan.dailyexpensetracker

suspend fun seedInitialCategories(
    categoryMasterDao: CategoryMasterDao
) {
    val categories = listOf(
        CategoryMaster(
            nameHindi = "घरेलू राशन व दैनिक सामान",
            nameEnglish = "Grocery and Daily Needs",
            searchAliases = "grocery rashan daily needs",
            isCustom = false,
            sortOrder = 1
        ),
        CategoryMaster(
            nameHindi = "पर्सनल और डोमेस्टिक खर्च",
            nameEnglish = "Personal and Domestic",
            searchAliases = "personal domestic ghar",
            isCustom = false,
            sortOrder = 2
        ),
        CategoryMaster(
            nameHindi = "लोन, EMI और भुगतान",
            nameEnglish = "Loans EMI and Payments",
            searchAliases = "loan emi payment bhugtan",
            isCustom = false,
            sortOrder = 3
        ),
        CategoryMaster(
            nameHindi = "स्वास्थ्य एवं चिकित्सा",
            nameEnglish = "Health and Medicine",
            searchAliases = "health medical swasthya dawa",
            isCustom = false,
            sortOrder = 4
        ),
        CategoryMaster(
            nameHindi = "मनोरंजन, यात्रा एवं अन्य",
            nameEnglish = "Travel Entertainment and Others",
            searchAliases = "travel entertainment yatra other",
            isCustom = false,
            sortOrder = 5
        ),
        CategoryMaster(
            nameHindi = "कपड़े एवं फैशन",
            nameEnglish = "Clothing and Fashion",
            searchAliases = "clothing clothes fashion kapde",
            isCustom = false,
            isActive = true,
            sortOrder = 6
        ),
        CategoryMaster(
            nameHindi = "मनोरंजन",
            nameEnglish = "Entertainment",
            searchAliases = "entertainment movie games manoranjan",
            isCustom = false,
            isActive = true,
            sortOrder = 7
        ),
        CategoryMaster(
            nameHindi = "होटल एवं आवास",
            nameEnglish = "Hotel and Accommodation",
            searchAliases = "hotel accommodation stay room",
            isCustom = false,
            isActive = true,
            sortOrder = 8
        ),
        CategoryMaster(
            nameHindi = "यात्रा एवं परिवहन",
            nameEnglish = "Travel and Transport",
            searchAliases = "travel transport taxi bus train flight yatra",
            isCustom = false,
            isActive = true,
            sortOrder = 9
        ),
        CategoryMaster(
            nameHindi = "शिक्षा",
            nameEnglish = "Education",
            searchAliases = "education school college fees books",
            isCustom = false,
            isActive = true,
            sortOrder = 10
        ),
        CategoryMaster(
            nameHindi = "बिजली, पानी एवं Utilities",
            nameEnglish = "Utilities",
            searchAliases = "utilities electricity water gas internet",
            isCustom = false,
            isActive = true,
            sortOrder = 11
        ),
        CategoryMaster(
            nameHindi = "पर्सनल केयर",
            nameEnglish = "Personal Care",
            searchAliases = "personal care salon beauty grooming",
            isCustom = false,
            isActive = true,
            sortOrder = 12
        ),
        CategoryMaster(
            nameHindi = "घर एवं रखरखाव",
            nameEnglish = "Home and Maintenance",
            searchAliases = "home maintenance repair furniture",
            isCustom = false,
            isActive = true,
            sortOrder = 13
        ),
        CategoryMaster(
            nameHindi = "अन्य",
            nameEnglish = "Other",
            searchAliases = "other miscellaneous anya",
            isCustom = false,
            isActive = true,
            sortOrder = 99
        )
    )

    categories.forEach { category ->
        categoryMasterDao.insertCategory(category)
    }
}

suspend fun seedInitialItems(
    itemMasterDao: ItemMasterDao
) {

    val existingItemCount =
        itemMasterDao.getItemCount()

    if (existingItemCount > 0) {
        return
    }

    val allItems = buildList {

        addAll(grocerySeedPart1())
        addAll(grocerySeedPart2())
        addAll(grocerySeedPart3())
        addAll(grocerySeedPart4())
        addAll(grocerySeedPart5())

        addAll(personalSeedPart1())
        addAll(personalSeedPart2())
        addAll(personalSeedPart3())

        addAll(loanPaymentSeed())
        addAll(loanPaymentSeedPart2())

        addAll(healthSeedPart1())

        addAll(travelOtherSeed())
    }

    itemMasterDao.insertItems(allItems)
}
