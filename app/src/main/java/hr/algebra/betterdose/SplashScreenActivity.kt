package hr.algebra.betterdose

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import hr.algebra.betterdose.framework.startActivity


class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        checkForLoginAndRedirect()
    }

    private fun checkForLoginAndRedirect() {
        val auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            Handler(Looper.getMainLooper()).postDelayed(
                {startActivity<MainActivity>()},
                3000
            )
        } else {
            Handler(Looper.getMainLooper()).postDelayed(
                {startActivity<LoginActivity>()},
                3000
            )
        }
    }
}