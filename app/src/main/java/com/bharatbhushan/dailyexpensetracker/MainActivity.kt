package com.bharatbhushan.dailyexpensetracker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import java.util.Calendar
import java.util.Locale
import com.google.firebase.auth.FirebaseAuth

class MainActivity : FragmentActivity() {

    private var quickAction by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quickAction = intent.getStringExtra(QuickAddNotification.EXTRA_ACTION)

        setContent {

            val context = LocalContext.current

            val firebaseAuth = remember {
                FirebaseAuth.getInstance()
            }

            var currentUser by remember {
                mutableStateOf(
                    firebaseAuth.currentUser
                )
            }
            var guestMode by remember {
                mutableStateOf(GuestModeManager.isEnabled(context))
            }

            var isPinAccepted by remember {
                mutableStateOf(false)
            }
            val appLockEnabled =
                currentUser?.let { user ->
                    PinManager.isAppLockEnabled(
                        context = context,
                        userId = user.uid
                    )
                } ?: false

            when {

                currentUser == null && !guestMode -> {

                    GharKharchTheme {

                        FirebaseAuthScreen(
                            onAuthSuccess = {
                                GuestModeManager.disable(context)
                                guestMode = false
                                currentUser =
                                    firebaseAuth.currentUser

                                isPinAccepted = false
                            },
                            onContinueAsGuest = {
                                GuestModeManager.enable(context)
                                guestMode = true
                                isPinAccepted = true
                            }
                        )
                    }
                }

                appLockEnabled && !isPinAccepted -> {

                    GharKharchTheme {

                        PinLockScreen(
                            userId = currentUser!!.uid,

                            onPinAccepted = {
                                isPinAccepted = true
                            },

                            onLogout = {
                                firebaseAuth.signOut()
                                currentUser = null
                                isPinAccepted = false
                            }
                        )
                    }
                }

                else -> {

                    val userId = currentUser?.uid ?: "guest_local"
                    var selectedBook by remember(userId) {
                        mutableStateOf<BudgetBook?>(
                            BudgetBookManager.selectedBook(context, userId)
                        )
                    }

                    if (selectedBook == null) {
                        BudgetBookSelectionScreen(
                            userId = userId,
                            onBookSelected = { selectedBook = it }
                        )
                    } else {
                        val appContent: @Composable () -> Unit = {
                            GharKharchApp(
                                userId = userId,
                                book = selectedBook!!,
                                isGuest = guestMode,
                                quickAction = quickAction,
                                onQuickActionConsumed = { quickAction = null },
                                onChangeBook = {
                                    ExpenseDatabase.closeDatabase()
                                    selectedBook = null
                                },
                                onLogout = {
                                    ExpenseDatabase.closeDatabase()
                                    GuestModeManager.disable(context)
                                    guestMode = false
                                    firebaseAuth.signOut()
                                    currentUser = null
                                    isPinAccepted = false
                                }
                            )
                        }
                        if (guestMode) {
                            appContent()
                        } else {
                            UserDataGate(userId = userId, bookId = selectedBook!!.id) {
                                appContent()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        quickAction = intent.getStringExtra(QuickAddNotification.EXTRA_ACTION)
    }
}

@Composable
private fun UserDataGate(
    userId: String,
    bookId: String,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    var ready by remember(userId, bookId) { mutableStateOf(false) }

    LaunchedEffect(userId, bookId) {
        CloudDatabaseBackupManager.restoreIfLocalMissing(
            context = context,
            userId = userId,
            bookId = bookId
        )
        ready = true
    }

    if (ready) {
        content()
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                AppText(
                    "आपका data तैयार किया जा रहा है…",
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GharKharchApp(
    userId: String,
    book: BudgetBook,
    isGuest: Boolean,
    quickAction: String?,
    onQuickActionConsumed: () -> Unit,
    onChangeBook: () -> Unit,
    onLogout: () -> Unit
) {

    val context = LocalContext.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) QuickAddNotification.show(context)
    }

    LaunchedEffect(Unit) {
        if (
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            QuickAddNotification.show(context)
        } else {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    var selectedTheme by remember {
        mutableStateOf(
            loadSavedTheme(context)
        )
    }

    var selectedLanguage by remember {
        mutableStateOf(AppLanguageManager.load(context))
    }

    var selectedCurrency by remember {
        mutableStateOf(AppCurrencyManager.load(context))
    }

    val database = remember(userId, book.id) {
        ExpenseDatabase.getDatabase(
            context = context,
            userId = userId,
            bookId = book.id
        )
    }

    val incomeDao = remember {
        database.incomeDao()
    }
    val budgetDao = remember {
        database.budgetDao()
    }

    val itemMasterDao = remember {
        database.itemMasterDao()
    }
    val shoppingListDao = remember {
        database.shoppingListDao()
    }

    val expenseRepository = remember {
        ExpenseRepository(database)
    }

    val expenseDao = remember {
        database.expenseDao()
    }
    val categoryMasterDao = remember {
        database.categoryMasterDao()

    }
    val savingsGoalDao = remember {
        database.savingsGoalDao()
    }

    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

    LaunchedEffect(database) {
        seedInitialCategories(categoryMasterDao)
        seedInitialItems(itemMasterDao)
    }

    LaunchedEffect(database, userId, book.id, isGuest) {
        if (isGuest) return@LaunchedEffect
        delay(5000)
        CloudDatabaseBackupManager.upload(
            context = context,
            userId = userId,
            bookId = book.id,
            database = database
        )
    }

    LaunchedEffect(database, userId, book.id, isGuest) {
        if (isGuest) return@LaunchedEffect
        database.invalidationTracker.createFlow(
            "expenses",
            "income",
            "budgets",
            "item_master",
            "expense_line_items",
            "shopping_lists",
            "shopping_list_items",
            "category_master",
            "savings_goals",
            emitInitialState = false
        ).collectLatest {
            delay(2000)
            CloudDatabaseBackupManager.upload(
                context = context,
                userId = userId,
                bookId = book.id,
                database = database
            )
        }
    }

    DisposableEffect(lifecycleOwner, database, userId, book.id, isGuest) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP && !isGuest) {
                coroutineScope.launch {
                    CloudDatabaseBackupManager.upload(
                        context = context,
                        userId = userId,
                        bookId = book.id,
                        database = database
                    )
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val todayStart = remember { getTodayStart() }
    val tomorrowStart = remember { getTomorrowStart() }
    val monthStart = remember { getMonthStart() }
    val nextMonthStart = remember { getNextMonthStart() }
    val financialYearStart = remember {
        getFinancialYearStart()
    }

    val nextFinancialYearStart = remember {
        getNextFinancialYearStart()
    }
    val currentMonthKey = remember {
        getCurrentMonthKey()
    }
    val todayTotal by expenseDao
        .getTotalBetween(todayStart, tomorrowStart)
        .collectAsState(initial = 0.0)

    val monthTotal by expenseDao
        .getTotalBetween(monthStart, nextMonthStart)
        .collectAsState(initial = 0.0)

    val financialYearTotal by expenseDao
        .getTotalBetween(
            financialYearStart,
            nextFinancialYearStart
        )
        .collectAsState(initial = 0.0)

    val monthIncome by incomeDao
        .getIncomeTotalBetween(monthStart, nextMonthStart)
        .collectAsState(initial = 0.0)

    val categoryTotals by expenseDao
        .getCategoryTotals(monthStart, nextMonthStart)
        .collectAsState(initial = emptyList())

    val budgets by budgetDao
        .getBudgetsForMonth(currentMonthKey)
        .collectAsState(initial = emptyList())

    val allExpenses by expenseDao
        .getAllExpenses()
        .collectAsState(initial = emptyList())

    val allExpenseBills by expenseDao
        .getAllExpensesWithItems()
        .collectAsState(initial = emptyList())

    val monthlyExpenseTotals by expenseDao
        .getMonthlyExpenseTotals()
        .collectAsState(initial = emptyList())

    val monthlyIncomeTotals by incomeDao
        .getMonthlyIncomeTotals()
        .collectAsState(initial = emptyList())

    val savingsGoals by savingsGoalDao
        .getAllGoals()
        .collectAsState(initial = emptyList())

    var showAddExpense by remember {
        mutableStateOf(false)
    }
    var showAddIncome by remember {
        mutableStateOf(false)
    }

    var showHistory by remember {
        mutableStateOf(false)
    }
    var showReports by remember {
        mutableStateOf(false)
    }
    var showBudget by remember {
        mutableStateOf(false)
    }
    var showShoppingLists by remember {
        mutableStateOf(false)
    }

    var showBackupRestore by remember {
        mutableStateOf(false)
    }
    var backupOpenedFromSecurity by remember { mutableStateOf(false) }

    var selectedShoppingList by remember {
        mutableStateOf<ShoppingList?>(null)
    }
    var editingExpense by remember {
        mutableStateOf<ExpenseWithItems?>(null)
    }

    var selectedExpenseBill by remember {
        mutableStateOf<ExpenseWithItems?>(null)
    }
    var showManageCategories by remember {
        mutableStateOf(false)
    }
    var showAddCustomItem by remember {
        mutableStateOf(false)
    }

    var showThemeSettings by remember {
        mutableStateOf(false)
    }

    var showSavingsGoals by remember {
        mutableStateOf(false)
    }
    var showIncomeHistory by remember {
        mutableStateOf(false)
    }
    var showAccountSecurity by remember {
        mutableStateOf(false)
    }
    var showLanguageSettings by remember {
        mutableStateOf(false)
    }
    var showCurrencySettings by remember {
        mutableStateOf(false)
    }
    var showItemCategoryLinks by remember { mutableStateOf(false) }
    var reportSection by remember { mutableStateOf<String?>(null) }

    fun goHome() {
        showAddExpense = false
        showAddIncome = false
        showHistory = false
        showReports = false
        showBudget = false
        showShoppingLists = false
        showBackupRestore = false
        selectedShoppingList = null
        editingExpense = null
        selectedExpenseBill = null
        showManageCategories = false
        showAddCustomItem = false
        showThemeSettings = false
        showSavingsGoals = false
        showIncomeHistory = false
        showAccountSecurity = false
        showLanguageSettings = false
        showCurrencySettings = false
        showItemCategoryLinks = false
        reportSection = null
    }

    LaunchedEffect(quickAction) {
        when (quickAction) {
            QuickAddNotification.ACTION_EXPENSE -> {
                goHome()
                showAddExpense = true
                onQuickActionConsumed()
            }
            QuickAddNotification.ACTION_INCOME -> {
                goHome()
                showAddIncome = true
                onQuickActionConsumed()
            }
        }
    }

    val isAwayFromHome = showAddExpense || showAddIncome || showHistory ||
            showReports || showBudget || showShoppingLists || showBackupRestore ||
            selectedShoppingList != null || editingExpense != null ||
            selectedExpenseBill != null || showManageCategories ||
            showAddCustomItem || showThemeSettings || showSavingsGoals ||
            showIncomeHistory || showAccountSecurity || showLanguageSettings ||
            showCurrencySettings || showItemCategoryLinks

    BackHandler(
        enabled = drawerState.isOpen || isAwayFromHome
    ) {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else when {
            editingExpense != null -> { editingExpense = null; showHistory = true }
            selectedExpenseBill != null -> { selectedExpenseBill = null; showHistory = true }
            selectedShoppingList != null -> { selectedShoppingList = null; showShoppingLists = true }
            else -> goHome()
        }
    }
    CompositionLocalProvider(LocalAppLanguage provides selectedLanguage) {
    GharKharchTheme(themeMode = selectedTheme) {
        when {
            showItemCategoryLinks -> {
                ItemCategoryLinkScreen(
                    itemMasterDao = itemMasterDao,
                    categoryMasterDao = categoryMasterDao,
                    onBack = { goHome() }
                )
            }
            showCurrencySettings -> {
                CurrencySettingsScreen(
                    selectedCurrency = selectedCurrency,
                    onCurrencySelected = { currency ->
                        selectedCurrency = currency
                        AppCurrencyManager.save(context, currency)
                    },
                    onBack = { goHome() }
                )
            }
            showLanguageSettings -> {
                LanguageSettingsScreen(
                    selectedLanguage = selectedLanguage,
                    onLanguageSelected = { language ->
                        selectedLanguage = language
                        AppLanguageManager.save(context, language)
                    },
                    onBack = { goHome() }
                )
            }
            showAccountSecurity -> {

                val firebaseUser =
                    FirebaseAuth.getInstance().currentUser

                AccountSecurityScreen(
                    userId = firebaseUser?.uid ?: "",
                    userEmail = firebaseUser?.email ?: "No email",
                    onBackupRestore = {
                        backupOpenedFromSecurity = true
                        showAccountSecurity = false
                        showBackupRestore = true
                    },

                    onBack = { goHome() }
                )
            }

            selectedShoppingList != null -> {

                ShoppingListDetailScreen(
                    shoppingList = selectedShoppingList!!,
                    shoppingListDao = shoppingListDao,
                    itemMasterDao = itemMasterDao,
                    categoryMasterDao = categoryMasterDao,

                    onBack = {
                        selectedShoppingList = null
                        showShoppingLists = true
                    }
                )
            }
            showBackupRestore -> {

                BackupRestoreScreen(
                    database = database,
                    userId = userId,
                    bookId = book.id,

                    onBack = {
                        if (backupOpenedFromSecurity) {
                            backupOpenedFromSecurity = false
                            showBackupRestore = false
                            showAccountSecurity = true
                        } else {
                            goHome()
                        }
                    }
                )
            }

            showShoppingLists -> {

                ShoppingListsScreen(
                    shoppingListDao = shoppingListDao,

                    onBack = { goHome() },

                    onOpenList = { shoppingList ->
                        selectedShoppingList = shoppingList
                        showShoppingLists = false
                    }
                )
            }
            showIncomeHistory -> {

                IncomeHistoryScreen(
                    incomeDao = incomeDao,

                    onBack = { goHome() }
                )
            }
            showSavingsGoals -> {

                SavingsGoalsScreen(
                    savingsGoalDao = savingsGoalDao,

                    onBack = { goHome() }
                )
            }
            showThemeSettings -> {

                ThemeSettingsScreen(
                    selectedTheme = selectedTheme,

                    onThemeSelected = { theme ->

                        selectedTheme = theme

                        saveSelectedTheme(
                            context = context,
                            themeMode = theme
                        )
                    },

                    onBack = { goHome() }
                )
            }

            showAddCustomItem -> {
                AddCustomItemScreen(
                    itemMasterDao = itemMasterDao,
                    categoryMasterDao = categoryMasterDao,
                    onBack = { goHome() }
                )
            }
            showManageCategories -> {
                ManageCategoriesScreen(
                    categoryMasterDao = categoryMasterDao,
                    onBack = { goHome() }
                )
            }

            selectedExpenseBill != null -> {

                ExpenseDetailScreen(
                    bill = selectedExpenseBill!!,
                    expenseLineItemDao =
                        database.expenseLineItemDao(),
                    onBack = {
                        selectedExpenseBill = null
                        showHistory = true
                    }
                )
            }

            editingExpense != null -> {

                EditExpenseScreen(
                    bill = editingExpense!!,

                    onBack = {
                        editingExpense = null
                        showHistory = true
                    },

                    onSave = { updatedExpense, updatedItems ->

                        coroutineScope.launch {
                            expenseDao.updateExpense(updatedExpense)
                            database.expenseLineItemDao()
                                .deleteItemsForExpense(updatedExpense.id)
                            if (updatedItems.isNotEmpty()) {
                                database.expenseLineItemDao()
                                    .insertLineItems(updatedItems)
                            }
                            editingExpense = null
                            showHistory = true
                        }
                    }
                )
            }
            showBudget -> {

                BudgetScreen(
                    monthKey = currentMonthKey,
                    existingBudgets = budgets,
                    categoryMasterDao = categoryMasterDao,

                    onBack = { goHome() },

                    onSave = { updatedBudgets ->

                        coroutineScope.launch {

                            updatedBudgets.forEach { budget ->
                                budgetDao.saveBudget(budget)
                            }

                            showBudget = false
                        }
                    }
                )
            }
            showReports -> {
                when (reportSection) {
                    "overview" -> ReportsScreen(
                        expenseDao = expenseDao,
                        expenseLineItemDao = database.expenseLineItemDao(),
                        categoryMasterDao = categoryMasterDao,
                        onBack = { reportSection = null }
                    )
                    "cash_out" -> ExpenseHistoryScreen(
                        expenses = allExpenseBills,
                        onBack = { reportSection = null },
                        onOpenDetail = { selectedExpenseBill = it },
                        onEdit = { editingExpense = it },
                        onDelete = { expense ->
                            coroutineScope.launch { expenseDao.deleteExpense(expense) }
                        }
                    )
                    "cash_in" -> IncomeHistoryScreen(
                        incomeDao = incomeDao,
                        onBack = { reportSection = null }
                    )
                    else -> ReportsHubScreen(
                        onOpenOverview = { reportSection = "overview" },
                        onOpenCashOutHistory = { reportSection = "cash_out" },
                        onOpenCashInHistory = { reportSection = "cash_in" },
                        onBack = { goHome() }
                    )
                }
            }

            showHistory -> {

                ExpenseHistoryScreen(
                    expenses = allExpenseBills,

                    onBack = { goHome() },

                    onOpenDetail = { bill ->
                        selectedExpenseBill = bill
                        showHistory = false
                    },

                    onEdit = { expense ->
                        editingExpense = expense
                        showHistory = false
                    },

                    onDelete = { expense ->

                        coroutineScope.launch {
                            expenseDao.deleteExpense(expense)
                        }
                    }
                )
            }
            showAddIncome -> {

                AddIncomeScreen(
                    onBack = { goHome() },

                    onSave = {
                            amount,
                            source,
                            paymentMode,
                            description,
                            receivedAt ->

                        coroutineScope.launch {

                            incomeDao.insertIncome(
                                Income(
                                    amount = amount,
                                    source = source,
                                    paymentMode = paymentMode,
                                    description = description,
                                    receivedAt = receivedAt
                                )
                            )

                            showAddIncome = false
                        }
                    }
                )
            }

            showAddExpense -> {

                MultiItemExpenseScreen(
                    itemMasterDao = itemMasterDao,
                    categoryMasterDao = categoryMasterDao,
                    onBack = { goHome() },

                    onSave = {
                            category,
                            paymentMode,
                            description,
                            shopName,
                            billAttachmentUri,
                            billNumber,
                            expenseDate,
                            items ->

                        coroutineScope.launch {

                            val savedBillUri = if (isGuest) {
                                billAttachmentUri
                            } else {
                                CloudDatabaseBackupManager.uploadBillAttachment(
                                        userId = userId,
                                        bookId = book.id,
                                        localUri = billAttachmentUri
                                    )
                            }

                            expenseRepository.saveExpenseWithItems(
                                category = category,
                                paymentMode = paymentMode,
                                description = description,
                                shopName = shopName,
                                billAttachmentUri = savedBillUri,
                                billNumber = billNumber,
                                expenseDate = expenseDate,
                                items = items
                            )

                            showAddExpense = false
                        }
                    }
                )
            }

            else -> {

                ModalNavigationDrawer(
                    drawerState = drawerState,

                    drawerContent = {

                        ModalDrawerSheet(
                            modifier = Modifier.verticalScroll(
                                rememberScrollState()
                            )
                        ) {

                            Column(
                                modifier = Modifier.padding(20.dp)
                            ) {
                                AppText(
                                    text = "Daily Expense Tracker",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                AppText(
                                    text = if (isGuest) "Guest • Local device only" else FirebaseAuth.getInstance()
                                        .currentUser
                                        ?.email
                                        ?: "No email",

                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                AppText(
                                    text = "Budget: ${book.name}",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            HorizontalDivider()

                            NavigationDrawerItem(
                                label = {
                                    AppText(appText("dashboard", selectedLanguage))
                                },
                                selected = true,
                                onClick = {
                                    coroutineScope.launch {
                                        drawerState.close()
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Home,
                                        contentDescription = "Dashboard"
                                    )
                                }
                            )

                            NavigationDrawerItem(
                                label = { AppText(appText("savings_goals", selectedLanguage)) },
                                selected = false,
                                onClick = {
                                    showSavingsGoals = true
                                    coroutineScope.launch { drawerState.close() }
                                },
                                icon = {
                                    Icon(Icons.Default.Star, contentDescription = "Savings Goals")
                                }
                            )

                            NavigationDrawerItem(
                                label = { AppText(appText("change_budget", selectedLanguage)) },
                                selected = false,
                                onClick = {
                                    coroutineScope.launch {
                                        drawerState.close()
                                        onChangeBook()
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.SwapHoriz,
                                        contentDescription = "Change budget book"
                                    )
                                }
                            )
                            NavigationDrawerItem(
                                label = {
                                    AppText(appText("add_expense", selectedLanguage))
                                },
                                selected = false,
                                onClick = {
                                    showAddExpense = true

                                    coroutineScope.launch {
                                        drawerState.close()
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Cash Out"
                                    )
                                }
                            )

                            NavigationDrawerItem(
                                label = {
                                    AppText(appText("add_income", selectedLanguage))
                                },
                                selected = false,
                                onClick = {
                                    showAddIncome = true

                                    coroutineScope.launch {
                                        drawerState.close()
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.AddCircle,
                                        contentDescription = "Cash In"
                                    )
                                }
                            )

                            NavigationDrawerItem(
                                label = {
                                    AppText(appText("reports", selectedLanguage))
                                },
                                selected = false,
                                onClick = {
                                    showReports = true
                                    reportSection = null

                                    coroutineScope.launch {
                                        drawerState.close()
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Assessment,
                                        contentDescription = "Reports"
                                    )
                                }
                            )

                            NavigationDrawerItem(
                                label = {
                                    AppText(appText("monthly_budget", selectedLanguage))
                                },
                                selected = false,
                                onClick = {
                                    showBudget = true

                                    coroutineScope.launch {
                                        drawerState.close()
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalanceWallet,
                                        contentDescription = "Budget"
                                    )
                                }
                            )

                            NavigationDrawerItem(
                                label = {
                                    AppText(appText("shopping_lists", selectedLanguage))
                                },
                                selected = false,
                                onClick = {
                                    showShoppingLists = true

                                    coroutineScope.launch {
                                        drawerState.close()
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = "Shopping Lists"
                                    )
                                }
                            )

                            NavigationDrawerItem(
                                label = { AppText("Items & Categories") },
                                selected = false,
                                onClick = {
                                    showItemCategoryLinks = true
                                    coroutineScope.launch { drawerState.close() }
                                },
                                icon = {
                                    Icon(Icons.Default.Link, contentDescription = "Link Items and Categories")
                                }
                            )

                            NavigationDrawerItem(
                                label = {
                                    AppText(appText("theme", selectedLanguage))
                                },
                                selected = false,

                                onClick = {
                                    showThemeSettings = true

                                    coroutineScope.launch {
                                        drawerState.close()
                                    }
                                },

                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Palette,
                                        contentDescription = "Theme Settings"
                                    )
                                }
                            )
                            NavigationDrawerItem(
                                label = { AppText(appText("language", selectedLanguage)) },
                                selected = false,
                                onClick = {
                                    showLanguageSettings = true
                                    coroutineScope.launch { drawerState.close() }
                                },
                                icon = {
                                    Icon(Icons.Default.Language, contentDescription = "Language")
                                }
                            )
                            NavigationDrawerItem(
                                label = { AppText("Currency (${selectedCurrency.code})") },
                                selected = false,
                                onClick = {
                                    showCurrencySettings = true
                                    coroutineScope.launch { drawerState.close() }
                                },
                                icon = {
                                    Icon(Icons.Default.CurrencyExchange, contentDescription = "Currency")
                                }
                            )
                            HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp)
                            )

                            NavigationDrawerItem(
                                label = {
                                    AppText(appText("security", selectedLanguage))
                                },
                                selected = false,

                                onClick = {
                                    showAccountSecurity = true

                                    coroutineScope.launch {
                                        drawerState.close()
                                    }
                                },

                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription =
                                            "Account and Security"
                                    )
                                }
                            )

                NavigationDrawerItem(
                    label = {
                        AppText(appText("logout", selectedLanguage))
                    },
                    selected = false,

                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                            onLogout()
                        }
                    },

                    icon = {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout"
                        )
                    }
                )
                        }
                    }
                ) {

                    Scaffold(
                        topBar = {

                            TopAppBar(
                                navigationIcon = {

                                    IconButton(
                                        onClick = {
                                            coroutineScope.launch {
                                                drawerState.open()
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = "Open Menu"
                                        )
                                    }
                                },

                                title = {
                                    Column {

                                        AppText(
                                            text = appText("app_name", selectedLanguage),
                                            fontWeight = FontWeight.Bold
                                        )

                                        AppText(
                                            text = book.name,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            )
                        },
                        bottomBar = {

                            NavigationBar {

                                NavigationBarItem(
                                    selected = true,
                                    onClick = {},
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Home,
                                            contentDescription = "Dashboard"
                                        )
                                    },
                                    label = {
                                        AppText(appText("home", selectedLanguage))
                                    }
                                )

                                NavigationBarItem(
                                    selected = false,
                                    onClick = {
                                        showReports = true
                                        reportSection = null
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = "Transactions"
                                        )
                                    },
                                    label = {
                                        AppText(appText("transactions", selectedLanguage))
                                    }
                                )

                                NavigationBarItem(
                                    selected = false,
                                    onClick = {
                                        showBudget = true
                                    },
                                    icon = {
                                        Icon(
                                            imageVector =
                                                Icons.Default.AccountBalanceWallet,
                                            contentDescription = "Budget"
                                        )
                                    },
                                    label = {
                                        AppText(appText("budget", selectedLanguage))
                                    }
                                )

                                NavigationBarItem(
                                    selected = false,

                                    onClick = {
                                        showSavingsGoals = true
                                    },

                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Goals"
                                        )
                                    },

                                    label = {
                                        AppText(appText("savings_goals", selectedLanguage))
                                    }
                                )

                                NavigationBarItem(
                                    selected = false,
                                    onClick = {
                                        showReports = true
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Assessment,
                                            contentDescription = "Reports"
                                        )
                                    },
                                    label = {
                                        AppText(appText("reports", selectedLanguage))
                                    }
                                )
                            }
                        },

                        floatingActionButton = {

                            FloatingActionButton(
                                onClick = {
                                    showAddExpense = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Cash Out"
                                )
                            }
                        }
                    ) { padding ->

                        PremiumDashboard(
                            todayTotal = todayTotal,
                            monthTotal = monthTotal,
                            financialYearTotal = financialYearTotal,
                            monthIncome = monthIncome,
                            categoryTotals = categoryTotals,
                            budgets = budgets,
                            recentExpenses = allExpenses,
                            monthlyIncome = monthlyIncomeTotals,
                            monthlyExpenses = monthlyExpenseTotals,
                            savingsGoals = savingsGoals,
                            onCashInClick = { showAddIncome = true },
                            onCashOutClick = { showAddExpense = true },
                            onRecentTransactionsClick = {
                                showReports = true
                                reportSection = "cash_out"
                            },
                            modifier = Modifier.padding(padding)
                        )
                    }
                }
            }

        }
    }
    }
}

@Composable
fun Dashboard(
    todayTotal: Double,
    monthTotal: Double,
    financialYearTotal: Double,
    onOpenManageCategories: () -> Unit,
    monthIncome: Double,
    categoryTotals: List<CategoryTotal>,
    budgets: List<Budget>,
    onOpenHistory: () -> Unit,
    onOpenReports: () -> Unit,
    onAddIncome: () -> Unit,
    onOpenBudget: () -> Unit,
    onOpenShoppingLists: () -> Unit,
    onOpenBackupRestore: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        AppText(
            text = "आज का खर्च",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(6.dp))

        AppText(
            text = formatAmount(todayTotal),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            SummaryCard(
                title = "आज",
                amount = formatAmount(todayTotal),
                modifier = Modifier.weight(1f)
            )

            SummaryCard(
                title = "इस माह",
                amount = formatAmount(monthTotal),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            SummaryCard(
                title = "इस माह आय",
                amount = formatAmount(monthIncome),
                modifier = Modifier.weight(1f)
            )

            SummaryCard(
                title = "शेष Balance",
                amount = formatAmount(monthIncome - monthTotal),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        SummaryCard(
            title = "इस Financial Year का खर्च",
            amount = formatAmount(financialYearTotal),
            modifier = Modifier.fillMaxWidth()
        )

        BudgetProgressSection(
            budgets = budgets,
            categoryTotals = categoryTotals
        )

        Spacer(modifier = Modifier.height(18.dp))

        CategoryTotalsSection(
            categoryTotals = categoryTotals
        )

        AppText(
            text = "खर्च की श्रेणियाँ",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        GroupCard(
            icon = Icons.Default.ShoppingCart,
            title = "घरेलू राशन व दैनिक सामान",
            subtitle = "Grocery & Daily Needs"
        )

        GroupCard(
            icon = Icons.Default.Home,
            title = "पर्सनल और डोमेस्टिक खर्च",
            subtitle = "Personal & Domestic"
        )

        GroupCard(
            icon = Icons.Default.CreditCard,
            title = "लोन, EMI और भुगतान",
            subtitle = "Loans & Payments"
        )

        GroupCard(
            icon = Icons.Default.LocalHospital,
            title = "स्वास्थ्य एवं चिकित्सा",
            subtitle = "Health & Medicine"
        )

        GroupCard(
            icon = Icons.Default.AccountBalanceWallet,
            title = "मनोरंजन, यात्रा एवं अन्य",
            subtitle = "Travel & Others"
        )
    }
}
@Composable
fun SummaryCard(
    title: String,
    amount: String,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            AppText(
                text = title,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            AppText(
                text = amount,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun GroupCard(
    icon: ImageVector,
    title: String,
    subtitle: String
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),

        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = title
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column {

                AppText(
                    text = title,
                    fontWeight = FontWeight.Bold
                )

                AppText(
                    text = subtitle,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    onBack: () -> Unit,
    onSave: (
        amount: Double,
        category: String,
        paymentMode: String,
        description: String
    ) -> Unit
) {

    var amount by remember {
        mutableStateOf("")
    }

    var note by remember {
        mutableStateOf("")
    }

    var selectedCategory by remember {
        mutableStateOf("घरेलू राशन व दैनिक सामान")
    }

    var selectedPayment by remember {
        mutableStateOf("Cash")
    }

    var amountError by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },

                title = {
                    AppText(
                        text = "Cash Out",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),

            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            AppText(
                text = "नया खर्च",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            AppText(
                text = "Add New Expense",
                fontSize = 13.sp
            )

            OutlinedTextField(
                value = amount,

                onValueChange = {
                    amount = it
                    amountError = false
                },

                modifier = Modifier.fillMaxWidth(),

                label = {
                    AppText("राशि / Amount")
                },

                prefix = {
                    AppText("${currentCurrencySymbol()} ")
                },

                singleLine = true,

                isError = amountError,

                supportingText = {
                    if (amountError) {
                        AppText("सही राशि दर्ज करें")
                    }
                },

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                )
            )

            CategoryDropdown(
                selectedCategory = selectedCategory,

                onCategorySelected = {
                    selectedCategory = it
                }
            )

            PaymentDropdown(
                selectedPayment = selectedPayment,

                onPaymentSelected = {
                    selectedPayment = it
                }
            )

            OutlinedTextField(
                value = note,

                onValueChange = {
                    note = it
                },

                modifier = Modifier.fillMaxWidth(),

                label = {
                    AppText("नोट / Description")
                },

                minLines = 3
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {

                    val enteredAmount = amount.toDoubleOrNull()

                    if (enteredAmount != null && enteredAmount > 0) {

                        onSave(
                            enteredAmount,
                            selectedCategory,
                            selectedPayment,
                            note.trim()
                        )

                    } else {
                        amountError = true
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),

                shape = RoundedCornerShape(14.dp)
            ) {

                AppText(
                    text = "खर्च सेव करें",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    val categories = listOf(
        "घरेलू राशन व दैनिक सामान",
        "पर्सनल और डोमेस्टिक खर्च",
        "लोन, EMI और भुगतान",
        "स्वास्थ्य एवं चिकित्सा",
        "मनोरंजन, यात्रा एवं अन्य"
    )

    ExposedDropdownMenuBox(
        expanded = expanded,

        onExpandedChange = {
            expanded = !expanded
        }
    ) {

        OutlinedTextField(
            value = selectedCategory,
            onValueChange = {},
            readOnly = true,

            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),

            label = {
                AppText("श्रेणी / Category")
            },

            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            }
        )

        DropdownMenu(
            expanded = expanded,

            onDismissRequest = {
                expanded = false
            }
        ) {

            categories.forEach { category ->

                DropdownMenuItem(
                    text = {
                        AppText(category)
                    },

                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentDropdown(
    selectedPayment: String,
    onPaymentSelected: (String) -> Unit
) {

    val context = LocalContext.current

    var expanded by remember {
        mutableStateOf(false)
    }

    val builtInPayments = listOf(
        "Cash",
        "UPI",
        "Debit Card",
        "Credit Card",
        "Bank Transfer"
    )

    var customPayments by remember {
        mutableStateOf(
            context.getSharedPreferences("ghar_budget_payment_modes", 0)
                .getStringSet("custom_modes", emptySet())
                .orEmpty()
                .sorted()
        )
    }

    val payments = builtInPayments + customPayments
    val canSaveCustom = selectedPayment.isNotBlank() &&
            payments.none { it.equals(selectedPayment.trim(), true) }

    ExposedDropdownMenuBox(
        expanded = expanded,

        onExpandedChange = {
            expanded = !expanded
        }
    ) {

        OutlinedTextField(
            value = selectedPayment,
            onValueChange = onPaymentSelected,

            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),

            label = {
                AppText("Payment Mode / Bank Name")
            },

            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            }
        )

        DropdownMenu(
            expanded = expanded,

            onDismissRequest = {
                expanded = false
            }
        ) {

            if (canSaveCustom) {
                DropdownMenuItem(
                    text = { AppText("\"${selectedPayment.trim()}\" जोड़ें") },
                    onClick = {
                        val value = selectedPayment.trim()
                        val updated = (customPayments + value).distinct().sorted()
                        context.getSharedPreferences("ghar_budget_payment_modes", 0)
                            .edit().putStringSet("custom_modes", updated.toSet()).apply()
                        customPayments = updated
                        onPaymentSelected(value)
                        expanded = false
                    }
                )
            }

            payments.forEach { payment ->

                DropdownMenuItem(
                    text = {
                        AppText(payment)
                    },

                    onClick = {
                        onPaymentSelected(payment)
                        expanded = false
                    }
                )
            }
        }
    }
}

fun formatAmount(amount: Double): String {
    return formatCurrencyAmount(amount)
}

fun getTodayStart(): Long {

    return Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

fun getTomorrowStart(): Long {

    return Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.DAY_OF_MONTH, 1)
    }.timeInMillis
}

fun getMonthStart(): Long {

    return Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

fun getNextMonthStart(): Long {

    return Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.MONTH, 1)
    }.timeInMillis
}
fun getCurrentMonthKey(): String {

    val calendar = Calendar.getInstance()

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1

    return String.format(
        Locale.US,
        "%04d-%02d",
        year,
        month
    )
}
fun getFinancialYearStart(): Long {

    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)

    val financialYear =
        if (calendar.get(Calendar.MONTH) < Calendar.APRIL) {
            currentYear - 1
        } else {
            currentYear
        }

    calendar.set(
        financialYear,
        Calendar.APRIL,
        1,
        0,
        0,
        0
    )

    calendar.set(Calendar.MILLISECOND, 0)

    return calendar.timeInMillis
}

fun getNextFinancialYearStart(): Long {

    val calendar = Calendar.getInstance()
    calendar.timeInMillis = getFinancialYearStart()
    calendar.add(Calendar.YEAR, 1)

    return calendar.timeInMillis
}
