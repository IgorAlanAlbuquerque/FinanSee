package com.igor.finansee.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.igor.finansee.data.daos.BankAccountDao
import com.igor.finansee.data.daos.CategoryDao
import com.igor.finansee.data.daos.CreditCardDao
import com.igor.finansee.data.daos.ExpenseDao
import com.igor.finansee.data.daos.FaturaCreditCardDao
import com.igor.finansee.data.daos.MonthPlanningDao
import com.igor.finansee.data.daos.TransactionDao
import com.igor.finansee.data.daos.UserDao
import com.igor.finansee.data.models.BankAccount
import com.igor.finansee.data.models.Category
import com.igor.finansee.data.models.CreditCard
import com.igor.finansee.data.models.Expense
import com.igor.finansee.data.models.FaturaCreditCard
import com.igor.finansee.data.models.MonthPlanning
import com.igor.finansee.data.models.Transaction
import com.igor.finansee.data.models.User
import com.igor.finansee.data.utils.Converters

// 1. ANOTAÇÃO DE CONFIGURAÇÃO
@Database(
    entities = [
        Expense::class,
        MonthPlanning::class,
        BankAccount::class,
        Category::class,
        CreditCard::class,
        FaturaCreditCard::class,
        Transaction::class,
        User::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun bankAccountDao(): BankAccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun creditCardDao(): CreditCardDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun faturaCreditCardDao(): FaturaCreditCardDao
    abstract fun monthPlanningDao(): MonthPlanningDao
    abstract fun userDao(): UserDao

    // ---------------------------------

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finansee_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}