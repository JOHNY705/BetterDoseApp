package hr.algebra.betterdose.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import hr.algebra.betterdose.MainActivity
import hr.algebra.betterdose.R
import hr.algebra.betterdose.framework.startActivity
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.etEmail
import kotlinx.android.synthetic.main.fragment_login.etPassword
import kotlinx.android.synthetic.main.fragment_register.*

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        login()
        initActionListener()
        openWeb()
    }

    private fun openWeb() {
        tv_visit_our_website.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://betterdose-f75af.web.app/")))
        }
    }

    private fun initActionListener() {
        etPassword.setOnEditorActionListener{ _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> btnLogin.performClick()
                else -> false
            }
        }
    }

    private fun login() {
        btnLogin.setOnClickListener {
            if (validateForm()) {
               auth.signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            requireActivity().run {
                                startActivity<MainActivity>()
                                finish()
                            }
                        } else {
                            Toast.makeText(
                                view?.context,
                                getString(R.string.invalid_email_or_password),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }
    }

    private fun validateForm(): Boolean {
        var valid = true
        if (etEmail.text.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString())
                .matches()
        ) {
            etEmail.error = "Please enter valid email "
            etEmail.requestFocus()
            valid = false
        } else if (etPassword.text.isNullOrEmpty()) {
            etPassword.error = "Please enter password "
            etPassword.requestFocus()
            valid = false
        }
        return valid
    }

}