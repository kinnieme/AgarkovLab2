package com.example.agarkovlab1

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SaleEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun saleDao(): SaleDao
}