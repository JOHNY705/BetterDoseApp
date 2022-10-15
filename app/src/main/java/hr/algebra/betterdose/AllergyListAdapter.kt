package hr.algebra.betterdose

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.algebra.betterdose.model.Allergy

class AllergyListAdapter : RecyclerView.Adapter<AllergyListAdapter.AllergyListViewHolder>() {
    private var listOfAllergies = ArrayList<Allergy>()

    class AllergyListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(allergy: Allergy) {
            itemView.findViewById<TextView>(R.id.tvAllergyName).text = allergy.allergy
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllergyListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.allergy_list_item, parent, false)
        return AllergyListViewHolder(view)
    }

    override fun onBindViewHolder(holder: AllergyListViewHolder, position: Int) {
        holder.bind(listOfAllergies[position])
    }

    override fun getItemCount(): Int = listOfAllergies.size

    fun setListOfAllergies(allergies: List<Allergy>) {
        this.listOfAllergies = ArrayList(allergies)
        notifyDataSetChanged()
    }
}