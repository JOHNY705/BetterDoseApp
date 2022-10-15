package hr.algebra.betterdose.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hr.algebra.betterdose.MedicationListAdapter
import hr.algebra.betterdose.R
import hr.algebra.betterdose.api.druglabel.DrugDiary
import hr.algebra.betterdose.api.druglabel.DrugVM
import hr.algebra.betterdose.model.Drug
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MedicineDiaryFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var tvUsername: TextView
    private lateinit var tvDayAndDate: TextView
    private lateinit var rvDailyMedication: RecyclerView
    private lateinit var dailyMedicationAdapter: MedicationListAdapter

    private lateinit var calendarView: CalendarView
    private lateinit var tvSelectedDate: TextView
    private lateinit var rvSelectedDateMedication: RecyclerView
    private lateinit var selectedDateMedicationAdapter: MedicationListAdapter

    private val sdfNoYear = SimpleDateFormat("MMMM dd", Locale.US)
    private val sdfYear = SimpleDateFormat("MMMM dd',' yyyy", Locale.US)

    private lateinit var userDiary: List<Drug>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parent = inflater.inflate(R.layout.fragment_medicine_diary, container, false)

        parent.let {
            tvUsername = it.findViewById(R.id.tvUsername)
            tvDayAndDate = it.findViewById(R.id.tvDayAndDate)

            rvDailyMedication = it.findViewById(R.id.rvDailyMedication)

            dailyMedicationAdapter = MedicationListAdapter()

            rvDailyMedication.adapter = dailyMedicationAdapter

            calendarView = it.findViewById(R.id.calendarView)
            tvSelectedDate = it.findViewById(R.id.tvSelectedDate)

            rvSelectedDateMedication = it.findViewById(R.id.rvSelectedDateMedication)

            selectedDateMedicationAdapter = MedicationListAdapter()

            rvSelectedDateMedication.adapter = selectedDateMedicationAdapter
        }

        return parent
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        setText()
        initListeners()
        getData()
    }

    private fun getData() {
        db.collection("users")
            .document(auth.currentUser?.uid ?: "")
            .collection("diary")
            .get()
            .addOnCompleteListener {
                userDiary = it.result!!.toObjects(Drug::class.java)
                dailyMedicationAdapter.setDrugs(userDiary)
                var parser = SimpleDateFormat("dd/MM/yyyy")
                var formatter = SimpleDateFormat("dd/MM/yyyy")
                val currentDate = formatter.format(Date())
                selectedDateMedicationAdapter.setDrugsForDay(userDiary, currentDate)
            }
    }

    private fun setText() {
        tvUsername.text = auth.currentUser?.displayName

        val date = Date()
        val day = when(date.day) {
            1 -> "Monday"
            2 -> "Tuesday"
            3 -> "Wednesday"
            4 -> "Thursday"
            5 -> "Friday"
            6 -> "Saturday"
            0 -> "Sunday"
            else -> "Monday"
        }
        tvDayAndDate.text = "$day, ${sdfNoYear.format(date)}"
        tvSelectedDate.text = sdfYear.format(Date(calendarView.date))

        //calendarView.minDate = date.time.plus(86_400_000L)
    }

    private fun initListeners() {
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val selectedDate: Date = GregorianCalendar(year, month, dayOfMonth).time
            tvSelectedDate.text = sdfYear.format(selectedDate)
            db.collection("users")
                .document(auth.currentUser?.uid ?: "")
                .collection("diary")
                .get()
                .addOnCompleteListener {
                    userDiary = it.result!!.toObjects(Drug::class.java)
                    var formatter = SimpleDateFormat("dd/MM/yyyy")
                    val dateSelected = "$dayOfMonth/${month+1}/$year"
                    var parser = SimpleDateFormat("dd/MM/yyyy")
                    var startDate = formatter.format(parser.parse(dateSelected))
                    Log.v("Date", dateSelected)
                    selectedDateMedicationAdapter.setDrugsForDay(userDiary, startDate)
                }
        }

        db.collection("users")
            .document(auth.currentUser?.uid ?: "")
            .collection("diary")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("DIARY_FRAGMENT", "Listen failed:", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    dailyMedicationAdapter.setDrugs(snapshot.toObjects(Drug::class.java))
                }
            }
    }
}