package com.example.agarkovlab1

import androidx.room.*

@Dao
interface SaleDao {
    @Query("SELECT * FROM sales")
    suspend fun getAllSales(): List<SaleEntity>

    @Insert
    suspend fun insertSale(sale: SaleEntity)

    @Update
    suspend fun updateSale(sale: SaleEntity)

    @Delete
    suspend fun deleteSale(sale: SaleEntity)
}
