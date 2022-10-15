package hr.algebra.betterdose


import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import hr.algebra.betterdose.fragments.LoginFragment
import hr.algebra.betterdose.fragments.RegisterFragment


class LoginAdapter(activity: AppCompatActivity, private val itemsCount: Int) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = itemsCount

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> LoginFragment()
            1 -> RegisterFragment()
            else -> LoginFragment()
        }
    }

}