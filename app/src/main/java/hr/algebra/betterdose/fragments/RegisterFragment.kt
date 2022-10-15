package hr.algebra.betterdose.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import hr.algebra.betterdose.MainActivity
import hr.algebra.betterdose.R
import hr.algebra.betterdose.framework.startActivity
import hr.algebra.betterdose.model.User
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var currentUser: User
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tv_terms_and_condition.setOnClickListener {
            showTermsAndConditionsDialog()
        }
        super.onViewCreated(view, savedInstanceState)
        register()
        initActionListener()
    }

    private fun showTermsAndConditionsDialog() {
        val dialog = Dialog(requireActivity(), R.style.TermsAndConditionsDialog)
        dialog.setContentView(R.layout.terms_and_conditions_dialog)
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.80).toInt()
        dialog.window?.setLayout(width, height)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        val btnCloseDialog =
            dialog.findViewById(R.id.btn_close_terms_and_conditions_dialog) as Button
        btnCloseDialog.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

        private fun init() {
            requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            auth = FirebaseAuth.getInstance()
            db = FirebaseFirestore.getInstance()
        }

        private fun register() {
            btnRegister.setOnClickListener {
                if (validateForm()) {
                    auth.createUserWithEmailAndPassword(
                        currentUser.email,
                        password
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val documentReference =
                                db.collection("users").document(auth.currentUser!!.uid)
                            val user = mapOf<String, Any>(
                                "fullName" to currentUser.fullName,
                                "email" to currentUser.email
                            )
                            documentReference.set(user)

                            if (auth.currentUser != null)
                                auth.currentUser?.updateProfile(
                                    UserProfileChangeRequest.Builder()
                                        .setDisplayName(etUsername.text?.trim().toString())
                                        .build()
                                )

                            requireActivity().run {
                                startActivity<MainActivity>()
                                finish()
                            }

                        }
                    }
                } else {
                    Toast.makeText(
                        view?.context,
                        "Registration failed, please try again! ",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        private fun initActionListener() {
            etPasswordRepetition.setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> btnRegister.performClick()
                    else -> false
                }
            }
        }

        private fun validateForm(): Boolean {
            var valid = true;
            if (etUsername.text.toString().isEmpty()) {
                etUsername.error = "Please enter username "
                etUsername.requestFocus()
                valid = false
            } else if (etPassword.text.toString().isEmpty()) {
                etPassword.error = "Please enter password "
                etPassword.requestFocus()
                valid = false

            } else if (etEmail.text.toString()
                    .isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString())
                    .matches()
            ) {
                etEmail.error = "Please enter valid email "
                etEmail.requestFocus()
                valid = false
            } else if (etPasswordRepetition.text.toString().isEmpty()) {
                etPasswordRepetition.error = "Please confirm password "
                etPasswordRepetition.requestFocus()
                valid = false
            } else if (etPassword.text.toString() != etPasswordRepetition.text.toString()) {
                etPasswordRepetition.error = "Password does not match "
                etPasswordRepetition.requestFocus()
                valid = false
            } else if (etPassword.text.toString().length < 6) {
                etPassword.error = "Password must be at least 6 characters long!"
                etPassword.requestFocus()
                valid = false
            } else if (!cb_terms_and_condition.isChecked) {
                val snackbar: Snackbar = Snackbar.make(
                    requireView(),
                    "Please agree to Terms & Conditions.",
                    Snackbar.LENGTH_SHORT
                )
                snackbar.view.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red
                    )
                )
                snackbar.show()
                valid = false
            }
            if (valid) {
                currentUser =
                    User(etUsername.text.toString(), etEmail.text.toString())
                password = etPassword.text.toString()
            }
            return valid
        }

        private fun setupTermsAndConditionsDialog() {

        }
    }