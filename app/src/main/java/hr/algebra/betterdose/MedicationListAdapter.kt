package hr.algebra.betterdose

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.algebra.betterdose.model.Drug
import hr.algebra.betterdose.model.DrugFormatted
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class MedicationListAdapter : RecyclerView.Adapter<MedicationListAdapter.MedicationListViewHolder>() {
    private var drugs = ArrayList<DrugFormatted>()

    class MedicationListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(drug: DrugFormatted) {
            itemView.findViewById<TextView>(R.id.tvDosage).text = drug.dosage_to_take.toString() + " x"
            itemView.findViewById<TextView>(R.id.tvDrugName).text = drug.additionalData.brandName?.get(0)
            itemView.findViewById<TextView>(R.id.tvTime).text = drug.timeToTakeMedicine
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicationListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.medication_list_item, parent, false)
        return MedicationListViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicationListViewHolder, position: Int) {
        holder.bind(drugs[position])
    }

    override fun getItemCount() = drugs.size

    fun setDrugs(drugs: List<Drug>) {
        var drugs = ArrayList(drugs)
        var drugsFormatted = ArrayList<DrugFormatted>()
        for (drug in drugs) {
            var parser = SimpleDateFormat("dd/MM/yyyy")
            var formatter = SimpleDateFormat("dd/MM/yyyy")
            var startDate = SimpleDateFormat("dd/MM/yyyy").parse(drug.startDate)
            var finishDate = SimpleDateFormat("dd/MM/yyyy").parse(drug.finishDate)
            val currentDate = Date()

            if (startDate <= currentDate && finishDate >= currentDate) {
                if(drug.timesPerDay!! <= 1) {
                    val drugFormatted = DrugFormatted(
                        drug.description,
                        drug.dosage,
                        drug.purpose,
                        drug.caution,
                        drug.additionalData,
                        drug.medication,
                        drug.dosage_to_take,
                        drug.startDate,
                        drug.finishDate,
                        drug.startTimeDay,
                        drug.timesPerDay,
                        drug.hourlyFrequency,
                        drug.startTimeDay
                    )
                    drugsFormatted.add(drugFormatted)
                }
                else {
                    var iterator = 0
                    var drugStartTimeDay = drug.startTimeDay
                    while (iterator < drug.timesPerDay) {
                        if (iterator == 0) {
                            val drugFormatted = DrugFormatted(
                                drug.description,
                                drug.dosage,
                                drug.purpose,
                                drug.caution,
                                drug.additionalData,
                                drug.medication,
                                drug.dosage_to_take,
                                drug.startDate,
                                drug.finishDate,
                                drug.startTimeDay,
                                drug.timesPerDay,
                                drug.hourlyFrequency,
                                drug.startTimeDay
                            )
                            drugsFormatted.add(drugFormatted)
                            Log.v("Start date", "Prvo je uspješno.")
                        } else {
                            var hoursMinutes: List<String> = drugStartTimeDay!!.split(":")
                            var hours = hoursMinutes[0].toInt()
                            var minutes = hoursMinutes[1]
                            hours += drug.hourlyFrequency!!
                            drugStartTimeDay = "$hours:$minutes"
                            val drugFormatted = DrugFormatted(
                                drug.description,
                                drug.dosage,
                                drug.purpose,
                                drug.caution,
                                drug.additionalData,
                                drug.medication,
                                drug.dosage_to_take,
                                drug.startDate,
                                drug.finishDate,
                                drug.startTimeDay,
                                drug.timesPerDay,
                                drug.hourlyFrequency,
                                drugStartTimeDay
                            )
                            drugsFormatted.add(drugFormatted)
                            Log.v("Start date", drugStartTimeDay)
                        }
                        iterator++
                    }
                }
            }
        }
        this.drugs = drugsFormatted
        notifyDataSetChanged()
    }

    fun setDrugsForDay(drugs: List<Drug>, date: String) {
        var drugs = ArrayList(drugs)
        var drugsFormatted = ArrayList<DrugFormatted>()
        for (drug in drugs) {
/*            var parser = SimpleDateFormat("dd/MM/yyyy")
            var formatter = SimpleDateFormat("dd/MM/yyyy")*/
            var formattedDate = SimpleDateFormat("dd/MM/yyyy").parse(date)

            var startDate = SimpleDateFormat("dd/MM/yyyy").parse(drug.startDate)
            var finishDate = SimpleDateFormat("dd/MM/yyyy").parse(drug.finishDate)

            if (startDate <= formattedDate && finishDate >= formattedDate) {
                if(drug.timesPerDay!! <= 1) {
                    val drugFormatted = DrugFormatted(
                        drug.description,
                        drug.dosage,
                        drug.purpose,
                        drug.caution,
                        drug.additionalData,
                        drug.medication,
                        drug.dosage_to_take,
                        drug.startDate,
                        drug.finishDate,
                        drug.startTimeDay,
                        drug.timesPerDay,
                        drug.hourlyFrequency,
                        drug.startTimeDay
                    )
                    drugsFormatted.add(drugFormatted)
                }
                else {
                    var iterator = 0
                    var drugStartTimeDay = drug.startTimeDay
                    while (iterator < drug.timesPerDay) {
                        if (iterator == 0) {
                            val drugFormatted = DrugFormatted(
                                drug.description,
                                drug.dosage,
                                drug.purpose,
                                drug.caution,
                                drug.additionalData,
                                drug.medication,
                                drug.dosage_to_take,
                                drug.startDate,
                                drug.finishDate,
                                drug.startTimeDay,
                                drug.timesPerDay,
                                drug.hourlyFrequency,
                                drug.startTimeDay
                            )
                            drugsFormatted.add(drugFormatted)
                            Log.v("Start date", "Prvo je uspješno.")
                        } else {
                            var hoursMinutes: List<String> = drugStartTimeDay!!.split(":")
                            var hours = hoursMinutes[0].toInt()
                            var minutes = hoursMinutes[1]
                            hours += drug.hourlyFrequency!!
                            drugStartTimeDay = "$hours:$minutes"
                            val drugFormatted = DrugFormatted(
                                drug.description,
                                drug.dosage,
                                drug.purpose,
                                drug.caution,
                                drug.additionalData,
                                drug.medication,
                                drug.dosage_to_take,
                                drug.startDate,
                                drug.finishDate,
                                drug.startTimeDay,
                                drug.timesPerDay,
                                drug.hourlyFrequency,
                                drugStartTimeDay
                            )
                            drugsFormatted.add(drugFormatted)
                            Log.v("Start date", drugStartTimeDay)
                        }
                        iterator++
                    }
                }
            }
        }
        this.drugs = drugsFormatted
        notifyDataSetChanged()
    }

    fun addDrug(drug: DrugFormatted) {
        drugs.add(drug)
        notifyItemInserted(drugs.indexOf(drug))
    }

    fun addDrugs(drugs: List<DrugFormatted>) {
        this.drugs.addAll(drugs)
        notifyDataSetChanged()
    }
}