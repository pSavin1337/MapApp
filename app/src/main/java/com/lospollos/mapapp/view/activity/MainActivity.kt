package com.lospollos.mapapp.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.lospollos.mapapp.R

class MainActivity : AppCompatActivity() {

    lateinit var navController: NavController

    override fun onSupportNavigateUp()
            = findNavController(R.id.main_fragment).navigateUp()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = Navigation.findNavController(this, R.id.main_fragment)
    }
}