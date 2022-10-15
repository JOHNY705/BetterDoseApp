package hr.algebra.betterdose

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.algebra.betterdose.api.druglabel.DrugVM

class DrugListAdapter(
        private val drugs: ArrayList<DrugVM>,
        private val drugRemovedListener: (DrugVM) -> Unit
    )
    : RecyclerView.Adapter<DrugListAdapter.DrugListViewHolder>() {

    class DrugListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(drug: DrugVM) {
            itemView.findViewById<TextView>(R.id.tvDrug).text =
                drug.additionalData?.brandName?.get(0) ?: ""
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DrugListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.drug_list_item, parent, false)
        return DrugListViewHolder(view)
    }

    override fun onBindViewHolder(holder: DrugListViewHolder, position: Int) {
        val drug = drugs[position]
        holder.itemView.findViewById<ImageView>(R.id.ivRemove).setOnClickListener {
            removeDrug(drug)
            drugRemovedListener.invoke(drug)
        }
        return holder.bind(drug)
    }

    override fun getItemCount() = drugs.size

    fun addDrug(drug: DrugVM) {
        drugs.add(drug)
        notifyItemInserted(drugs.indexOf(drug))
    }

    private fun removeDrug(drug: DrugVM) {
        try {
            val index = drugs.indexOf(drug)
            drugs.remove(drug)
            notifyItemRemoved(index)
        } catch (e: Exception) {
            Log.d("DRUG_LIST_ADAPTER", "An error occured:", e)
        }
    }
}