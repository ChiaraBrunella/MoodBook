package com.example.moodbook

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.moodbook.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView


open class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)


        val drawerLayout: DrawerLayout = binding.drawerLayout

        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_moodtracker, R.id.nav_Habits, R.id.nav_ToDo, R.id.nav_Statistiche, R.id.nav_profilo
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // gestione passaggio da homefragment a moodFragment e HabitFragment dopo inserimento nuova voce

        val num_fragment: Int
        val extras = intent.extras
        if (extras != null) {

            num_fragment = extras.getInt("num_fragment")
            Log.i("numero frammento", num_fragment.toString())
            if (num_fragment == 2) {
                navController.navigate(R.id.nav_home_to_nav_moodtracker)

            }  else if (num_fragment == 3) {
                navController.navigate(R.id.nav_home_to_nav_habitracker)
            }
            else if (num_fragment == 4) {
                navController.navigate(R.id.nav_home_to_nav_toDo)
            }


        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_logout -> {
            val b = AlertDialog.Builder(this)
            b.setMessage("Are you sure you want to sign out?")
            b.setCancelable(true)
            b.setNegativeButton("Yes") { dialog, which ->
                dialog.cancel()
                val i = Intent(this, LoginActivity::class.java)
                /* val sharedPref =
                     this.getActivity()?.getSharedPreferences("com.example.moodbook", Context.MODE_PRIVATE)
                 val prefEditor = sharedPref?.edit()
                 val userId = sharedPref?.getString("isLogged", null)
                 prefEditor?.putString("isLogged", null)
                 prefEditor?.commit()*/
                startActivity(i)
                }
                b.setPositiveButton("No") { dialog, which -> dialog.dismiss() }
                val a = b.create()
                a.show()

                true
        }
        else -> super.onOptionsItemSelected(item)
    }
}