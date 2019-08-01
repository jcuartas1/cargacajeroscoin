package com.coinlogiq.updateatmsproyect.ui


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.coinlogiq.mylibrary2.ToolbarActivity
import com.coinlogiq.updateatmsproyect.R
import com.coinlogiq.updateatmsproyect.ui.activities.login.LoginActivity
import com.coinlogiq.updateatmsproyect.ui.adapters.PagerAdapter
import com.coinlogiq.updateatmsproyect.ui.extensions.gotoActivity
import com.coinlogiq.updateatmsproyect.ui.fragments.ChatFragment
import com.coinlogiq.updateatmsproyect.ui.fragments.FormAtmFragment
import com.coinlogiq.updateatmsproyect.ui.fragments.InfoFragment
import com.coinlogiq.updateatmsproyect.ui.fragments.RatesFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : ToolbarActivity() {

    private var prevBottomSelected: MenuItem? = null

    private lateinit var adapter: PagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolBarToLoad(toolbarView as Toolbar)

        setUpViewPager(getPagerAdapter())
        setUpBottomNavigationBar()
    }

    private fun getPagerAdapter() : PagerAdapter {
        adapter = PagerAdapter(supportFragmentManager)
        adapter.addFragment(InfoFragment())
        adapter.addFragment(ChatFragment())
        adapter.addFragment(RatesFragment())
        return adapter
    }

    private fun setUpViewPager (adapter: PagerAdapter){
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = adapter.count
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                if(prevBottomSelected == null){
                    bottom_navigation.menu.getItem(0).isChecked = false
                } else{
                    prevBottomSelected!!.isChecked = false
                }
                bottom_navigation.menu.getItem(position).isChecked = true
                prevBottomSelected = bottom_navigation.menu.getItem(position)
            }

        })
    }

    private fun setUpBottomNavigationBar(){
        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.bottom_nav_info ->{
                    viewPager.currentItem = 0; true
                }
                R.id.bottom_nav_chat ->{
                    viewPager.currentItem = 1; true
                }

                R.id.bottom_nav_rates ->{
                    viewPager.currentItem = 2; true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.general_options_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_log_out -> {
                FirebaseAuth.getInstance().signOut()
                gotoActivity<LoginActivity> {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


}
