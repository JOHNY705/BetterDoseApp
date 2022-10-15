package hr.algebra.betterdose.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import  android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hr.algebra.betterdose.DrugListAdapter
import hr.algebra.betterdose.InteractionListAdapter
import hr.algebra.betterdose.R
import hr.algebra.betterdose.SearchListAdapter
import hr.algebra.betterdose.api.druginteraction.DrugInteraction
import hr.algebra.betterdose.api.druginteraction.InteractionFetcher
import hr.algebra.betterdose.api.druglabel.DrugDiary
import hr.algebra.betterdose.api.druglabel.DrugFetcher
import hr.algebra.betterdose.api.druglabel.DrugVM
import hr.algebra.betterdose.model.DrugInteractionVM
import hr.algebra.betterdose.model.SearchDrug
import kotlinx.android.synthetic.main.fragment_medicine_search.*
import kotlinx.android.synthetic.main.medication_list_item.*
import kotlinx.coroutines.*
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList

private const val FIRESTORE_SEARCH_TAG: String = "FIRESTORE_SEARCH_LOG"

class MedicineSearchFragment : Fragment() {

    private var selectedDrug: DrugVM? = null

    private var interactionDrugs = arrayListOf<DrugVM>()

    private var searchList = listOf<SearchDrug>()
    private var searchListAdapter = SearchListAdapter(searchList) {
        onDrugClickedHandler(it)
    }

    private var drugInteractions = ArrayList<DrugInteractionVM>()

    private lateinit var rvInteractions: RecyclerView
    private lateinit var interactionListAdapter: InteractionListAdapter

    private lateinit var rvInteractionDrugs: RecyclerView
    private lateinit var drugListAdapter: DrugListAdapter

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_medicine_search, container, false)

        rvInteractionDrugs = rootView.findViewById(R.id.rvInteractionDrugs)
        rvInteractionDrugs.layoutManager = LinearLayoutManager(rootView.context)

        drugListAdapter = DrugListAdapter(interactionDrugs) {
            removeDrugFromInteractions(it)
        }

        rvInteractionDrugs.adapter = drugListAdapter

        rvInteractions = rootView.findViewById(R.id.rvInteractions)
        rvInteractions.layoutManager = LinearLayoutManager(rootView.context)

        interactionListAdapter = InteractionListAdapter()
        interactionListAdapter.setItems(drugInteractions)

        rvInteractions.adapter = interactionListAdapter

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        search_list.hasFixedSize()
        search_list.adapter = searchListAdapter
        search_list.layoutManager = LinearLayoutManager(view.context)

        initEventListeners()
    }

    private fun searchInFirestore(searchText: String) {
        db.collection("drugs").orderBy("GenericName").startAt(searchText).endAt("$searchText\uf8ff")
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    search_list.isVisible = true
                    search_list.bringToFront()
                    drug_card.isVisible = false
                    interaction_card.isVisible = false

                    if (it.result!!.isEmpty) {
                        searchList = listOf(SearchDrug("No results"))
                        searchListAdapter.searchList = searchList
                        searchListAdapter.notifyDataSetChanged()
                    } else {
                        searchList = it.result!!.toObjects(SearchDrug::class.java)
                        searchListAdapter.searchList = searchList
                        searchListAdapter.notifyDataSetChanged()

                    }
                } else {
                    Log.e(FIRESTORE_SEARCH_TAG, "Error: ${it.exception!!.message}")
                }
            }

    }

    private fun initEventListeners() {
        search_field.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = search_field.text.toString()
                if (searchText.isEmpty()) {
                    Handler().postDelayed({
                        search_list?.isVisible = false
                        drug_card.isVisible = true
                        interaction_card.isVisible = true
                    }, 700)
                } else {
                    searchInFirestore(searchText.uppercase())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        btn_diary_add.setOnClickListener {
            if (selectedDrug == null)
                return@setOnClickListener

            addToDiary()

            /*GlobalScope.launch {
                addToDiary()
            }*/
        }

        btn_interactions_add.setOnClickListener {
            GlobalScope.launch {
                addToInteractions()
            }
            if (selectedDrug != null && !interactionDrugs.contains(selectedDrug))
                drugListAdapter.addDrug(selectedDrug!!)
        }
    }

    private suspend fun addToInteractions() {
        if (selectedDrug == null)
            return

        if (!interactionDrugs.contains(selectedDrug)) {
            interactionDrugs.add(selectedDrug!!)
        }

        if (interactionDrugs.size > 1) {
            withContext(Dispatchers.Default) {
                fetchInteractions()
            }
        }
    }

    private fun fetchInteractions() {
        val rxcuis = StringBuilder()

        interactionDrugs.forEach {
            rxcuis.append(it.additionalData?.rxcui?.get(0) + "+")
        }

        InteractionFetcher(requireContext())
            .fetchInteractions(rxcuis.toString()) {
                handleInteractions(it)
            }
    }

    private fun handleInteractions(interactions: DrugInteraction) {
        if (interactions.interactions == null) {
            interactionListAdapter.apply {
                setItems(ArrayList())
                notifyDataSetChanged()
            }
            return
        }

        drugInteractions.clear()

        interactions.interactions[0].interactionType?.forEach { interaction ->
            drugInteractions.add(DrugInteractionVM(
                interaction.interactionPair[0].interactionConcept[0].conceptItem.name,
                interaction.interactionPair[0].interactionConcept[1].conceptItem.name,
                interaction.interactionPair[0].description
            ))
        }

        interactionListAdapter.apply {
            setItems(drugInteractions)
            notifyDataSetChanged()
        }
    }

    private fun addToDiary() {

        val dialog = Dialog(requireActivity(), R.style.CustomDialogDiary)
        dialog.setContentView(R.layout.dialog_add_to_diary)
        dialog.setCancelable(false)
        val etAddToDiaryStartTimeDay = dialog.findViewById(R.id.et_add_to_diary_start_time_day) as EditText

        val timePickerDialog = Dialog(requireActivity(), R.style.CustomDialogDiary)
        timePickerDialog.setContentView(R.layout.dialog_time_picker)
        timePickerDialog.window?.setLayout((resources.displayMetrics.widthPixels * 0.85).toInt(), (resources.displayMetrics.heightPixels * 0.85).toInt())
        val timePicker = timePickerDialog.findViewById(R.id.time_picker) as TimePicker
        timePicker.setIs24HourView(true)
        val timePickerCancelButton = timePickerDialog.findViewById(R.id.btn_time_picker_cancel) as Button
        timePickerCancelButton.setOnClickListener {
            timePickerDialog.dismiss()
        }
        val timePickerConfirmButton = timePickerDialog.findViewById(R.id.btn_time_picker_confirm) as Button
        timePickerConfirmButton.setOnClickListener {
            if (timePicker.minute < 10) {
                etAddToDiaryStartTimeDay.setText("${timePicker.hour}:0${timePicker.minute}")
            } else {
                etAddToDiaryStartTimeDay.setText("${timePicker.hour}:${timePicker.minute}")
            }
            timePickerDialog.dismiss()
        }

        val etAddToDiaryDosage = dialog.findViewById(R.id.et_add_to_diary_dosage) as EditText

        val addToDiaryTVBrandName = dialog.findViewById(R.id.add_to_diary_tv_brand_name) as TextView
        addToDiaryTVBrandName.setText(drug_title.text)

        val addToDiaryTVDescription = dialog.findViewById(R.id.add_to_diary_tv_description) as TextView
        if (drug_description.text.isEmpty() || drug_description.text == null) {
            addToDiaryTVDescription.setText("No data available.")
        } else {
            addToDiaryTVDescription.setText(drug_description.text)
        }

        val addToDiaryTVDosage = dialog.findViewById(R.id.add_to_diary_tv_dosage) as TextView
        if (drug_dosage.text.isEmpty() || drug_dosage.text == null) {
            addToDiaryTVDosage.setText("No data available.")
        } else {
            addToDiaryTVDosage.setText(drug_dosage.text)
        }

        val addToDiaryTVPurpose = dialog.findViewById(R.id.add_to_diary_tv_purpose) as TextView
        if (drug_purpose.text.isEmpty() || drug_purpose.text == null) {
            addToDiaryTVPurpose.setText("No data available.")
        } else {
            addToDiaryTVPurpose.setText(drug_purpose.text)
        }

        val addToDiaryTVCaution = dialog.findViewById(R.id.add_to_diary_tv_caution) as TextView
        if (drug_caution.text.isEmpty() || drug_caution.text == null) {
            addToDiaryTVCaution.setText("No data available.")
        } else {
            addToDiaryTVCaution.setText(drug_caution.text)
        }

        val etStartDate = dialog.findViewById(R.id.et_add_to_diary_start_date) as EditText
        val etFinishDate = dialog.findViewById(R.id.et_add_to_diary_finish_date) as EditText

        val addStartDateButton = dialog.findViewById(R.id.btn_add_to_diary_start_date) as Button
        addStartDateButton.setOnClickListener {

            val myCalendar = Calendar.getInstance()
            val year = myCalendar.get(Calendar.YEAR)
            val month = myCalendar.get(Calendar.MONTH)
            val day = myCalendar.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener {view, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedDate = "$selectedDayOfMonth/${selectedMonth+1}/$selectedYear"
                etStartDate.setText(selectedDate)
            }
                ,year
                ,month
                ,day).show()
        }

        val addFinishDateButton = dialog.findViewById(R.id.btn_add_to_diary_finish_date) as Button
        addFinishDateButton.setOnClickListener {

            val myCalendar = Calendar.getInstance()
            val year = myCalendar.get(Calendar.YEAR)
            val month = myCalendar.get(Calendar.MONTH)
            val day = myCalendar.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener {view, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedDate = "$selectedDayOfMonth/${selectedMonth+1}/$selectedYear"
                etFinishDate.setText(selectedDate)
            }
                ,year
                ,month
                ,day).show()
        }

        val cancelButton = dialog.findViewById(R.id.btn_add_to_diary_cancel) as Button
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        val selectStartTimeDayButton = dialog.findViewById(R.id.btn_add_to_diary_start_time_day) as Button
        selectStartTimeDayButton.setOnClickListener {
            timePickerDialog.show()
        }

        val etTimesPerDay = dialog.findViewById(R.id.et_times_per_day) as EditText
        val etHourlyFrequency = dialog.findViewById(R.id.et_hourly_frequency) as EditText

        val addToDiaryButton = dialog.findViewById(R.id.btn_add_to_diary_add_to_diary) as Button
        addToDiaryButton.setOnClickListener {
            when {
                etAddToDiaryDosage.text.isEmpty() -> {
                    Toast.makeText(requireContext(), "Please enter dosage.", Toast.LENGTH_SHORT).show()
                }
                etStartDate.text.isEmpty() -> {
                    Toast.makeText(requireContext(), "Please enter start date.", Toast.LENGTH_SHORT).show()
                }
                etFinishDate.text.isEmpty() -> {
                    Toast.makeText(requireContext(), "Please enter finish date.", Toast.LENGTH_SHORT).show()
                }
                etAddToDiaryStartTimeDay.text.isEmpty() -> {
                    Toast.makeText(requireContext(), "Please enter start time in the day.", Toast.LENGTH_SHORT).show()
                }
                etTimesPerDay.text.isEmpty() -> {
                    Toast.makeText(requireContext(), "Please enter number of times per day.", Toast.LENGTH_SHORT).show()
                }
                etHourlyFrequency.text.isEmpty() -> {
                    Toast.makeText(requireContext(), "Please enter hourly frequency you will be taking the medicine.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val  drugToDiary = DrugDiary(
                        addToDiaryTVDescription.text.toString(),
                        addToDiaryTVDosage.text.toString(),
                        addToDiaryTVPurpose.text.toString(),
                        addToDiaryTVPurpose.text.toString(),
                        selectedDrug!!.additionalData,
                        addToDiaryTVBrandName.text.toString(),
                        etAddToDiaryDosage.text.toString().toInt(),
                        etStartDate.text.toString(),
                        etFinishDate.text.toString(),
                        etAddToDiaryStartTimeDay.text.toString(),
                        etTimesPerDay.text.toString().toInt(),
                        etHourlyFrequency.text.toString().toInt()
                    )
                        val users = db.collection("users")

                        drugToDiary.additionalData.rxcui?.get(0)?.let {
                            users
                                .document(auth.currentUser!!.uid)
                                .collection("diary")
                                .document(it).set(drugToDiary!!)
                        }
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    //TODO BACKUP
    /*private suspend fun addToDiary() {
        withContext(Dispatchers.Default) {
            val users = db.collection("users")

            selectedDrug?.additionalData?.rxcui?.get(0)?.let {
                users
                    .document(auth.currentUser!!.uid)
                    .collection("diary")
                    .document(it).set(selectedDrug!!)
            }
        }
    }*/

    private fun onDrugClickedHandler(searchDrug: SearchDrug) {
        DrugFetcher(requireContext())
            .fetchDrugData("rxcui=${searchDrug.Rxcui}"){
                handleDrugData(it)
            }

        search_field.text.clear()
    }

    private fun removeDrugFromInteractions(drug: DrugVM) {
        interactionDrugs.remove(drug)
        fetchInteractions()

        interactionListAdapter.apply {
            setItems(drugInteractions)
            notifyDataSetChanged()
        }
    }

    private fun handleDrugData(drugData: DrugVM) {
        selectedDrug = drugData
        fillMedicineCard(drugData)
    }

    private fun fillMedicineCard(drugData: DrugVM) {
        drugData.additionalData.let { drug_title.text = "${it?.brandName} ${it?.manufacturerName}" }
        drugData.description?.let { drug_description.text = it[0] }
        drugData.dosage?.let { drug_dosage.text = it[0] }
        drugData.purpose?.let { drug_purpose.text = it[0] }
        drugData.caution?.let { drug_caution.text = it[0] }
    }
}