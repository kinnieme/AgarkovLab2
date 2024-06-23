package com.example.agarkovlab1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.appcompat.app.AlertDialog
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    private lateinit var saleAdapter: SaleAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "sales.db"
        ).build()

        saleAdapter = SaleAdapter(mutableListOf(), { editSale(it) }, { deleteSale(it) })
        findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = saleAdapter
        }

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            addSale()
        }

        findViewById<Button>(R.id.filterButton).setOnClickListener {
            showFilteredSales()
        }

        // Load all sales from the database
        loadSales()
    }

    private fun loadSales() {
        GlobalScope.launch(Dispatchers.Main) {
            val sales = withContext(Dispatchers.IO) {
                db.saleDao().getAllSales()
            }
            saleAdapter.updateSales(sales)
        }
    }

    private fun addSale() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_sale, null)
        AlertDialog.Builder(this)
            .setTitle("Add Sale")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val seller = dialogView.findViewById<EditText>(R.id.sellerEditText).text.toString()
                val name = dialogView.findViewById<EditText>(R.id.nameEditText).text.toString()
                val quantity = dialogView.findViewById<EditText>(R.id.quantityEditText).text.toString().toInt()
                val price = dialogView.findViewById<EditText>(R.id.priceEditText).text.toString().toDouble()
                val date = dialogView.findViewById<EditText>(R.id.dateEditText).text.toString()
                val sale = SaleEntity(seller = seller, name = name, quantity = quantity, price = price, date = date)
                GlobalScope.launch(Dispatchers.IO) {
                    db.saleDao().insertSale(sale)
                    withContext(Dispatchers.Main) {
                        saleAdapter.addSale(sale)
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun editSale(sale: SaleEntity) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_sale, null)
        val sellerEditText = dialogView.findViewById<EditText>(R.id.sellerEditText)
        val nameEditText = dialogView.findViewById<EditText>(R.id.nameEditText)
        val quantityEditText = dialogView.findViewById<EditText>(R.id.quantityEditText)
        val priceEditText = dialogView.findViewById<EditText>(R.id.priceEditText)
        val dateEditText = dialogView.findViewById<EditText>(R.id.dateEditText)

        sellerEditText.setText(sale.seller)
        nameEditText.setText(sale.name)
        quantityEditText.setText(sale.quantity.toString())
        priceEditText.setText(sale.price.toString())
        dateEditText.setText(sale.date)

        AlertDialog.Builder(this)
            .setTitle("Edit Sale")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                sale.seller = sellerEditText.text.toString()
                sale.name = nameEditText.text.toString()
                sale.quantity = quantityEditText.text.toString().toInt()
                sale.price = priceEditText.text.toString().toDouble()
                sale.date = dateEditText.text.toString()

                GlobalScope.launch(Dispatchers.IO) {
                    db.saleDao().updateSale(sale)
                    withContext(Dispatchers.Main) {
                        saleAdapter.updateSale(sale)
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun deleteSale(sale: SaleEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            db.saleDao().deleteSale(sale)
            withContext(Dispatchers.Main) {
                saleAdapter.removeSale(sale)
            }
        }
    }

    private fun showFilteredSales() {
        GlobalScope.launch(Dispatchers.Main) {
            val sales = withContext(Dispatchers.IO) {
                db.saleDao().getAllSales()
            }
            val ivanovSales = sales.filter { it.seller == "Ivanov" }
            val maxPriceSale = ivanovSales.maxByOrNull { it.price }

            val message = StringBuilder()
            message.append("Количество товаров проданных Ивановым: ${ivanovSales.size}\n\n")
            ivanovSales.forEach { sale ->
                message.append("Продавец: ${sale.seller}\n")
                message.append("Наименование: ${sale.name}\n")
                message.append("Количество: ${sale.quantity}\n")
                message.append("Цена: ${sale.price}\n")
                message.append("Дата продажи: ${sale.date}\n\n")
            }
            maxPriceSale?.let {
                message.append("Товар с максимальной стоимостью:\n")
                message.append("Продавец: ${it.seller}\n")
                message.append("Наименование: ${it.name}\n")
                message.append("Количество: ${it.quantity}\n")
                message.append("Цена: ${it.price}\n")
                message.append("Дата продажи: ${it.date}\n")
            }

            AlertDialog.Builder(this@MainActivity)
                .setTitle("Иванов's Sales")
                .setMessage(message.toString())
                .setPositiveButton("OK", null)
                .create()
                .show()
        }
    }
}
