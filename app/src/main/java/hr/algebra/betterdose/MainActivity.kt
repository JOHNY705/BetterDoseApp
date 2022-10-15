package hr.algebra.betterdose

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import hr.algebra.betterdose.fragments.LocationFragment
import hr.algebra.betterdose.fragments.MedicineDiaryFragment
import hr.algebra.betterdose.fragments.MedicineSearchFragment
import hr.algebra.betterdose.fragments.ProfileFragment
import kotlinx.android.synthetic.main.activity_main.*

private const val NUM_PAGES = 4

class MainActivity : FragmentActivity() {
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.main_fragment_container)

        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter
        viewPager.isUserInputEnabled = false

        bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ic_location -> viewPager.setCurrentItem(0, true)
                R.id.ic_diary -> viewPager.setCurrentItem(1, true)
                R.id.ic_search -> viewPager.setCurrentItem(2, true)
                R.id.ic_person -> viewPager.setCurrentItem(3, true)
            }
            true
        }
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> LocationFragment()
                1 -> MedicineDiaryFragment()
                2 -> MedicineSearchFragment()
                3 -> ProfileFragment()
                else -> MedicineSearchFragment()
            }
        }
    }
}