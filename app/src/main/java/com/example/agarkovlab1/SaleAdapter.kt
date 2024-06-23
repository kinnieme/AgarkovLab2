package com.example.agarkovlab1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SaleAdapter(
    private var sales: MutableList<SaleEntity>,
    private val onEditClick: (SaleEntity) -> Unit,
    private val onDeleteClick: (SaleEntity) -> Unit
) : RecyclerView.Adapter<SaleAdapter.SaleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sale, parent, false)
        return SaleViewHolder(view)
    }

    override fun onBindViewHolder(holder: SaleViewHolder, position: Int) {
        val sale = sales[position]
        holder.bind(sale, onEditClick, onDeleteClick)
    }

    override fun getItemCount(): Int = sales.size

    fun updateSales(newSales: List<SaleEntity>) {
        sales = newSales.toMutableList()
        notifyDataSetChanged()
    }

    fun addSale(sale: SaleEntity) {
        sales.add(sale)
        notifyItemInserted(sales.size - 1)
    }

    fun updateSale(sale: SaleEntity) {
        val index = sales.indexOfFirst { it.id == sale.id }
        if (index != -1) {
            sales[index] = sale
            notifyItemChanged(index)
        }
    }

    fun removeSale(sale: SaleEntity) {
        val index = sales.indexOfFirst { it.id == sale.id }
        if (index != -1) {
            sales.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    class SaleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sellerTextView: TextView = itemView.findViewById(R.id.sellerTextView)
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val quantityTextView: TextView = itemView.findViewById(R.id.quantityTextView)
        private val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)

        fun bind(sale: SaleEntity, onEditClick: (SaleEntity) -> Unit, onDeleteClick: (SaleEntity) -> Unit) {
            sellerTextView.text = sale.seller
            nameTextView.text = sale.name
            quantityTextView.text = sale.quantity.toString()
            priceTextView.text = sale.price.toString()
            dateTextView.text = sale.date

            itemView.findViewById<View>(R.id.editButton).setOnClickListener {
                onEditClick(sale)
            }

            itemView.findViewById<View>(R.id.deleteButton).setOnClickListener {
                onDeleteClick(sale)
            }
        }
    }
}
