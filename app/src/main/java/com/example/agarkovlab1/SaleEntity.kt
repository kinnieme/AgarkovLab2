package com.example.agarkovlab1

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class SaleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var seller: String,
    var name: String,
    var quantity: Int,
    var price: Double,
    var date: String
)