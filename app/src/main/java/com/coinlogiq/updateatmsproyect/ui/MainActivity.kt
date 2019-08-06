package com.coinlogiq.updateatmsproyect.ui


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.coinlogiq.mylibrary2.ToolbarActivity
import com.coinlogiq.updateatmsproyect.R
import com.coinlogiq.updateatmsproyect.enums.PermissionStatusEnum
import com.coinlogiq.updateatmsproyect.ui.activities.login.LoginActivity
import com.coinlogiq.updateatmsproyect.ui.adapters.PagerAdapter
import com.coinlogiq.updateatmsproyect.ui.extensions.gotoActivity
import com.coinlogiq.updateatmsproyect.ui.extensions.toast
import com.coinlogiq.updateatmsproyect.ui.fragments.ChatFragment
import com.coinlogiq.updateatmsproyect.ui.fragments.FormAtmFragment
import com.coinlogiq.updateatmsproyect.ui.fragments.InfoFragment
import com.coinlogiq.updateatmsproyect.ui.fragments.RatesFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.auth.FirebaseAuth
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : ToolbarActivity() {

    private var prevBottomSelected: MenuItem? = null

    private lateinit var adapter: PagerAdapter

    private val TAG ="PermmissionDemo"

    private val REQUEST_CODE = 101
    private val permisoFineLocation = Manifest.permission.ACCESS_FINE_LOCATION
    private val permisoCourseLocation = Manifest.permission.ACCESS_COARSE_LOCATION

    var fusedLoctaionClient: FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolBarToLoad(toolbarView as Toolbar)

        setUpViewPager(getPagerAdapter())
        setUpBottomNavigationBar()

        //checkPermissions()
    }

    private fun getPagerAdapter() : PagerAdapter {
        adapter = PagerAdapter(supportFragmentManager)
        adapter.addFragment(InfoFragment())
        adapter.addFragment(FormAtmFragment())
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
                R.id.bottom_nav_form ->{
                    viewPager.currentItem = 1; true
                }
                R.id.bottom_nav_chat ->{
                    viewPager.currentItem = 2; true
                }

                R.id.bottom_nav_rates ->{
                    viewPager.currentItem = 3; true
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

    /*private fun checkPermissions(){
        val permisssion = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        if(permisssion != PackageManager.PERMISSION_GRANTED){
            Log.d("Permission Denied", permisssion.toString())
            makeRequest()
      }
    }

    private fun makeRequest(){
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE)
    }*/


    /*override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            REQUEST_CODE -> {
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Log.i(TAG,"Permission has been Denied by usser")
                }else{
                    Log.i(TAG, "Permission has been Granted by usse")
                }
            }
        }
    }*/


}
