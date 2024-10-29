package com.example.campusmarketplace.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusmarketplace.R
import com.example.campusmarketplace.data.Product
import com.example.campusmarketplace.fragments.shopping.SearchFragmentDirections

class SearchAdapter(
    private val context: android.content.Context,
    private var dataList: List<Product>
): RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    inner class SearchViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var recImage: ImageView = itemView.findViewById(R.id.recImage)
        var recTitle: TextView = itemView.findViewById(R.id.recTitle)
        var recPrice: TextView = itemView.findViewById(R.id.recPrice)
        var recDesc: TextView = itemView.findViewById(R.id.recDesc)
        var recPriority: TextView = itemView.findViewById(R.id.recPriority)
        var recCard: CardView = itemView.findViewById(R.id.recCard)

        init {
            recCard.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val product = dataList[position]
                    val action = SearchFragmentDirections.actionSearchFragmentToProductDetailsFragment(product)
                    itemView.findNavController().navigate(action)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.search_recycler_item, parent, false)
        return SearchViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val product = dataList[position]

        Glide.with(context).load(product.images[0]).into(holder.recImage)
        holder.recTitle.text = product.name
        holder.recDesc.text = product.description
        holder.recPriority.text = product.category
        holder.recPrice.text = product.price.toString()
    }

    fun searchDataList(searchList: List<Product>) {
        dataList = searchList
        notifyDataSetChanged()
    }
}
