package hr.algebra.betterdose

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class LoginActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2

    private lateinit var tvPageTitle: TextView
    private lateinit var tvAccountAction: TextView
    private lateinit var tvActionPrompt: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initTextViews()
        initViewPager()
        initLoginAdapter()
        initOnClickListeners()
    }

    private fun initOnClickListeners() {
        tvAccountAction.setOnClickListener {
            when (viewPager.currentItem) {
                0 -> viewPager.setCurrentItem(1, true)
                1 -> viewPager.setCurrentItem(0, true)
            }
        }
    }

    private fun initTextViews() {
        tvPageTitle = findViewById(R.id.tvPageTitle)
        tvActionPrompt = findViewById(R.id.tvAccountPrompt)
        tvAccountAction = findViewById(R.id.tvAccountAction)
    }

    private fun initLoginAdapter() {
        viewPager.adapter = LoginAdapter(this, 2)
        viewPager.isUserInputEnabled = false
    }

    private fun initViewPager() {
        viewPager = findViewById(R.id.login_view_pager)

        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when(position) {
                    0 -> {
                        tvPageTitle.text = resources.getText(R.string.sign_in)
                        tvAccountAction.text = resources.getText(R.string.sign_up_underlined)
                        tvActionPrompt.text = resources.getText(R.string.don_t_have_an_account)
                    }
                    1 -> {
                        tvPageTitle.text = resources.getText(R.string.register)
                        tvAccountAction.text = resources.getText(R.string.log_in_underlined)
                        tvActionPrompt.text = resources.getText(R.string.already_have_an_account)
                    }
                }
                super.onPageSelected(position)
            }
        })
    }
}