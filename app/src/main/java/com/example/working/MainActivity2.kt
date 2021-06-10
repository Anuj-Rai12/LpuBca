package com.example.working

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.working.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {
    private var binding: ActivityMain2Binding? = null
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding?.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.noteFragment,
                R.id.booksFragment
            )
        )

        navController = navHostFragment.findNavController()
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding?.mybtnNag?.setupWithNavController(navController)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.i("MYTAG", "handleOnBackPressed: on Back Pressed")
                // activity?.finish()
                /*exitProcess(0)*/
                MainActivity2Directions.actionGlobalLoginScreen(true)
            }
        }
        this.onBackPressedDispatcher.addCallback(callback)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}