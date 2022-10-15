package hr.algebra.betterdose.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hr.algebra.betterdose.AllergyListAdapter
import hr.algebra.betterdose.LoginActivity
import hr.algebra.betterdose.MedicationListAdapter
import hr.algebra.betterdose.R
import hr.algebra.betterdose.framework.startActivity
import hr.algebra.betterdose.model.Allergy
import hr.algebra.betterdose.model.Drug
import kotlinx.android.synthetic.main.fragment_profile.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var allergiesAdapter: AllergyListAdapter
    private lateinit var rvAllergies: RecyclerView
    private lateinit var allergies : List<Allergy>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parent = inflater.inflate(R.layout.fragment_profile, container, false)
        parent.let {
            rvAllergies = it.findViewById(R.id.rvAlergies)
            allergiesAdapter = AllergyListAdapter()
            rvAllergies.adapter = allergiesAdapter
        }
        return parent
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSignOut()
        initAccountDelete()
        initLabels()
        initListeners()
        getAllergies()
    }

    private fun getAllergies() {
        /*db.collection("allergies")
            .whereEqualTo(auth.currentUser?.uid)
            .collection("allergies")
            .get()
            .addOnCompleteListener { document ->
                allergies = document.result!!.toObjects(Allergy::class.java)
                allergiesAdapter.setListOfAllergies(allergies)
                Log.v("Allergies", allergies.toString())
            }*/

        db.collection("users")
            .document(auth.currentUser?.uid ?: "")
            .collection("allergies")
            .get()
            .addOnCompleteListener { document ->
                allergies = document.result!!.toObjects(Allergy::class.java)
                allergiesAdapter.setListOfAllergies(allergies)
                Log.v("Allergies", allergies.toString())
            }
    }

    private fun initListeners() {
        btn_add_alergie.setOnClickListener {
            val dialog = Dialog(requireActivity(), R.style.CustomDialogDiary)
            dialog.setContentView(R.layout.dialog_add_alergie)
            dialog.setCancelable(false)

            val etAddAllergy = dialog.findViewById<EditText>(R.id.et_add_alergie)
            val btnCancelAddAllergy = dialog.findViewById<Button>(R.id.btn_add_alergie_cancel)
            btnCancelAddAllergy.setOnClickListener{
                dialog.dismiss()
            }
            val btnConfirmAddAllergy = dialog.findViewById<Button>(R.id.btn_add_alergie_confirm)
            btnConfirmAddAllergy.setOnClickListener{
                if (etAddAllergy.text.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter valid information.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), etAddAllergy.text, Toast.LENGTH_SHORT).show()
                    val users = db.collection("users")

                    val allergy = Allergy(etAddAllergy.text.toString(), auth.currentUser!!.uid)
                    users.document(auth.currentUser!!.uid)
                        .collection("allergies")
                        .document().set(allergy)
                    dialog.dismiss()
                    getAllergies()
                }
            }

            dialog.show()
        }
    }

    private fun initSignOut() {
        btn_sign_out.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Sign out?")
                .setMessage("Are you sure?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes") { _, _ ->
                    auth.signOut()
                    requireActivity().run {
                        startActivity<LoginActivity>()
                        finish()
                    }
                }
                .show()
        }
    }

    private fun initAccountDelete() {
        btn_delete_account.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete account?")
                .setMessage("Are you sure you want to delete your account? All of your data will be permanently deleted from our database.")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes") { _, _, ->
                    db.collection("users").document(auth.currentUser?.uid ?: "").delete()
                    auth.currentUser?.delete()
                    requireActivity().run {
                        startActivity<LoginActivity>()
                        finish()
                    }
                }
                .show()
        }
    }

    private fun initLabels() {
        tv_username.text = auth.currentUser?.displayName
        tv_email.text = auth.currentUser?.email

        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val date = auth.currentUser?.metadata?.creationTimestamp?.let { Date(it) }
        tv_date_joined.text = sdf.format(date!!)
    }
}