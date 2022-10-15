package hr.algebra.betterdose

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import hr.algebra.betterdose.model.SearchDrug
import kotlinx.android.synthetic.main.search_item.view.*

class SearchListAdapter(var searchList: List<SearchDrug>,
                        private val listener: (SearchDrug) -> Unit) :
    RecyclerView.Adapter<SearchListAdapter.SearchListViewHolder>() {

    class SearchListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(searchDrug: SearchDrug) {
            if (searchDrug.BrandName == "No results"){
                itemView.search_item_title.text = "No results found"
            }
            else {
                itemView.search_item_title.text =
                    searchDrug.BrandName + " [" + searchDrug.GenericName + "]"
            }
            itemView.isVisible = true
            itemView.bringToFront()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)
        return SearchListViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchListViewHolder, position: Int) {
        val item = searchList[position]
        holder.itemView.setOnClickListener { listener(item) }
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

}