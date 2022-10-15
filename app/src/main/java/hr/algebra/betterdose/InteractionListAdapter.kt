package hr.algebra.betterdose

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hr.algebra.betterdose.api.druginteraction.DrugInteraction
import hr.algebra.betterdose.model.DrugInteractionVM
import kotlinx.android.synthetic.main.interaction_list_item.view.*

class InteractionListAdapter : RecyclerView.Adapter<InteractionListAdapter.InteractionListViewHolder>() {

    private var items: ArrayList<DrugInteractionVM> = ArrayList()

    class InteractionListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(drugInteraction: DrugInteractionVM) {
            itemView.tvFirstDrug.text = drugInteraction.firstDrugName
            itemView.tvSecondDrug.text = drugInteraction.secondDrugName
            itemView.tvInteraction.text = drugInteraction.interactionDescription
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InteractionListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.interaction_list_item, parent, false)
        return InteractionListViewHolder(view)
    }

    override fun onBindViewHolder(holder: InteractionListViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount() = items.size

    fun addItem(drugInteraction: DrugInteractionVM) {
        items.add(drugInteraction)
    }

    fun addItems(drugInteractions: List<DrugInteractionVM>) {
        items.addAll(drugInteractions)
    }

    fun setItems(drugInteractions: ArrayList<DrugInteractionVM>) {
        items = drugInteractions
    }
}