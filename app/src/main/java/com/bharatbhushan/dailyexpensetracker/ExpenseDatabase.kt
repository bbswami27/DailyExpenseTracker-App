package com.bharatbhushan.dailyexpensetracker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        Expense::class,
        Income::class,
        Budget::class,
        ItemMaster::class,
        ExpenseLineItem::class,
        ShoppingList::class,
        ShoppingListItem::class,
        CategoryMaster::class,
        SavingsGoal::class
    ],
    version = 10,
    exportSchema = false
)
abstract class ExpenseDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao

    abstract fun incomeDao(): IncomeDao

    abstract fun budgetDao(): BudgetDao

    abstract fun itemMasterDao(): ItemMasterDao

    abstract fun expenseLineItemDao(): ExpenseLineItemDao

    abstract fun shoppingListDao(): ShoppingListDao

    abstract fun categoryMasterDao(): CategoryMasterDao
    abstract fun savingsGoalDao(): SavingsGoalDao

    companion object {

        @Volatile
        private var INSTANCE: ExpenseDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS income (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        amount REAL NOT NULL,
                        source TEXT NOT NULL,
                        paymentMode TEXT NOT NULL,
                        description TEXT NOT NULL,
                        receivedAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS budgets (
                        monthKey TEXT NOT NULL,
                        category TEXT NOT NULL,
                        amount REAL NOT NULL,
                        PRIMARY KEY (monthKey, category)
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS item_master (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nameHindi TEXT NOT NULL,
                        nameEnglish TEXT NOT NULL,
                        searchAliases TEXT NOT NULL,
                        category TEXT NOT NULL,
                        defaultUnit TEXT NOT NULL,
                        isCustom INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS
                    index_item_master_category
                    ON item_master(category)
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS
                    index_item_master_nameHindi
                    ON item_master(nameHindi)
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS
                    index_item_master_nameEnglish
                    ON item_master(nameEnglish)
                    """.trimIndent()
                )
            }
        }
        private val MIGRATION_3_4 = object : Migration(3, 4) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS expense_line_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                expenseId INTEGER NOT NULL,
                itemMasterId INTEGER,
                itemNameHindi TEXT NOT NULL,
                itemNameEnglish TEXT NOT NULL,
                quantity REAL NOT NULL,
                unit TEXT NOT NULL,
                rate REAL NOT NULL,
                amount REAL NOT NULL,
                FOREIGN KEY (expenseId)
                    REFERENCES expenses(id)
                    ON UPDATE NO ACTION
                    ON DELETE CASCADE
            )
            """.trimIndent()
                )

                database.execSQL(
                    """
            CREATE INDEX IF NOT EXISTS
            index_expense_line_items_expenseId
            ON expense_line_items(expenseId)
            """.trimIndent()
                )

                database.execSQL(
                    """
            CREATE INDEX IF NOT EXISTS
            index_expense_line_items_itemMasterId
            ON expense_line_items(itemMasterId)
            """.trimIndent()
                )
            }
        }
        private val MIGRATION_4_5 = object : Migration(4, 5) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS shopping_lists (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                createdAt INTEGER NOT NULL,
                isCompleted INTEGER NOT NULL
            )
            """.trimIndent()
                )

                database.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS shopping_list_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                shoppingListId INTEGER NOT NULL,
                itemMasterId INTEGER,
                itemNameHindi TEXT NOT NULL,
                itemNameEnglish TEXT NOT NULL,
                quantity REAL NOT NULL,
                unit TEXT NOT NULL,
                estimatedRate REAL NOT NULL,
                isPurchased INTEGER NOT NULL,
                FOREIGN KEY (shoppingListId)
                    REFERENCES shopping_lists(id)
                    ON UPDATE NO ACTION
                    ON DELETE CASCADE
            )
            """.trimIndent()
                )

                database.execSQL(
                    """
            CREATE INDEX IF NOT EXISTS
            index_shopping_list_items_shoppingListId
            ON shopping_list_items(shoppingListId)
            """.trimIndent()
                )

                database.execSQL(
                    """
            CREATE INDEX IF NOT EXISTS
            index_shopping_list_items_itemMasterId
            ON shopping_list_items(itemMasterId)
            """.trimIndent()
                )
            }
        }
        private val MIGRATION_5_6 = object : Migration(5, 6) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS category_master (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                nameHindi TEXT NOT NULL,
                nameEnglish TEXT NOT NULL,
                searchAliases TEXT NOT NULL,
                isCustom INTEGER NOT NULL,
                isActive INTEGER NOT NULL,
                sortOrder INTEGER NOT NULL
            )
            """.trimIndent()
                )

                database.execSQL(
                    """
            CREATE UNIQUE INDEX IF NOT EXISTS
            index_category_master_nameHindi
            ON category_master(nameHindi)
            """.trimIndent()
                )

                database.execSQL(
                    """
            INSERT OR IGNORE INTO category_master
            (
                nameHindi,
                nameEnglish,
                searchAliases,
                isCustom,
                isActive,
                sortOrder
            )
            VALUES
            (
                'घरेलू राशन व दैनिक सामान',
                'Grocery and Daily Needs',
                'grocery rashan daily needs',
                0,
                1,
                1
            )
            """.trimIndent()
                )

                database.execSQL(
                    """
            INSERT OR IGNORE INTO category_master
            (
                nameHindi,
                nameEnglish,
                searchAliases,
                isCustom,
                isActive,
                sortOrder
            )
            VALUES
            (
                'पर्सनल और डोमेस्टिक खर्च',
                'Personal and Domestic',
                'personal domestic ghar',
                0,
                1,
                2
            )
            """.trimIndent()
                )

                database.execSQL(
                    """
            INSERT OR IGNORE INTO category_master
            (
                nameHindi,
                nameEnglish,
                searchAliases,
                isCustom,
                isActive,
                sortOrder
            )
            VALUES
            (
                'लोन, EMI और भुगतान',
                'Loans EMI and Payments',
                'loan emi payment bhugtan',
                0,
                1,
                3
            )
            """.trimIndent()
                )

                database.execSQL(
                    """
            INSERT OR IGNORE INTO category_master
            (
                nameHindi,
                nameEnglish,
                searchAliases,
                isCustom,
                isActive,
                sortOrder
            )
            VALUES
            (
                'स्वास्थ्य एवं चिकित्सा',
                'Health and Medicine',
                'health medical swasthya dawa',
                0,
                1,
                4
            )
            """.trimIndent()
                )

                database.execSQL(
                    """
            INSERT OR IGNORE INTO category_master
            (
                nameHindi,
                nameEnglish,
                searchAliases,
                isCustom,
                isActive,
                sortOrder
            )
            VALUES
            (
                'मनोरंजन, यात्रा एवं अन्य',
                'Travel Entertainment and Others',
                'travel entertainment yatra other',
                0,
                1,
                5
            )
            """.trimIndent()
                )
            }
        }
        private val MIGRATION_6_7 =
            object : Migration(6, 7) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {
                    database.execSQL(
                        """
                CREATE TABLE IF NOT EXISTS savings_goals (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    targetAmount REAL NOT NULL,
                    savedAmount REAL NOT NULL,
                    targetDate INTEGER,
                    createdAt INTEGER NOT NULL,
                    isCompleted INTEGER NOT NULL
                )
                """.trimIndent()
                    )
                }
            }

        private val MIGRATION_7_8 =
            object : Migration(7, 8) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {
                    database.execSQL(
                        """
                        ALTER TABLE expenses
                        ADD COLUMN shopName TEXT NOT NULL DEFAULT ''
                        """.trimIndent()
                    )

                    database.execSQL(
                        """
                        ALTER TABLE expenses
                        ADD COLUMN billAttachmentUri TEXT NOT NULL DEFAULT ''
                        """.trimIndent()
                    )
                }
            }

        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE shopping_lists ADD COLUMN attachmentUri TEXT NOT NULL DEFAULT ''"
                )
                database.execSQL(
                    "ALTER TABLE shopping_list_items ADD COLUMN brand TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        private val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE expenses ADD COLUMN billNumber TEXT NOT NULL DEFAULT ''"
                )
            }
        }
        fun closeDatabase() {

            synchronized(this) {

                INSTANCE?.close()
                INSTANCE = null
            }
        }
        fun getDatabase(
            context: Context,
            userId: String,
            bookId: String
        ): ExpenseDatabase {

            return INSTANCE ?: synchronized(this) {

                val databaseName = userDatabaseName(userId, bookId)
                copyLegacyDatabaseForFirstUser(
                    context = context,
                    targetName = databaseName,
                    userId = userId,
                    bookId = bookId
                )

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExpenseDatabase::class.java,
                    databaseName
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_5,
                        MIGRATION_5_6,
                        MIGRATION_6_7,
                        MIGRATION_7_8,
                        MIGRATION_8_9,
                        MIGRATION_9_10
                    )

                    .build()

                INSTANCE = instance
                instance
            }
        }

        fun userDatabaseName(
            userId: String,
            bookId: String
        ): String {
            val safeUserId = userId
                .replace(Regex("[^A-Za-z0-9_-]"), "_")
                .take(50)
            val safeBookId = bookId
                .replace(Regex("[^A-Za-z0-9_-]"), "_")
                .take(32)
            return "ghar_budget_${safeUserId}_${safeBookId}.db"
        }

        private fun copyLegacyDatabaseForFirstUser(
            context: Context,
            targetName: String,
            userId: String,
            bookId: String
        ) {
            val target = context.getDatabasePath(targetName)
            if (target.exists()) return

            if (bookId != "default") return

            val preferences = context.getSharedPreferences(
                "ghar_budget_database_owner",
                Context.MODE_PRIVATE
            )
            val claimedBy = preferences.getString("legacy_claimed_by", null)
            val previousUserDatabase = context.getDatabasePath(
                "ghar_budget_${userId.replace(Regex("[^A-Za-z0-9_-]"), "_").take(80)}.db"
            )
            val legacy = if (previousUserDatabase.exists()) {
                previousUserDatabase
            } else {
                context.getDatabasePath("ghar_kharch_database")
            }

            if (claimedBy == null && legacy.exists()) {
                target.parentFile?.mkdirs()
                legacy.copyTo(target, overwrite = false)
                listOf("-wal", "-shm").forEach { suffix ->
                    val sourceSidecar = java.io.File(legacy.path + suffix)
                    if (sourceSidecar.exists()) {
                        sourceSidecar.copyTo(
                            java.io.File(target.path + suffix),
                            overwrite = false
                        )
                    }
                }
                preferences.edit()
                    .putString("legacy_claimed_by", userId)
                    .apply()
            }
        }
    }
}
