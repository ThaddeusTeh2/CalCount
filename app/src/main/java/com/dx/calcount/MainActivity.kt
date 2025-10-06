package com.dx.calcount

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dx.calcount.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.findNavController()

        binding.navView.setupWithNavController(navController)

        // attempt fix for keyboard taking up whole screen in settings


        // listens for keyboard
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) {_, insets ->
            val isKeyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime())

            // hide bottom nav if keyboard is onscreen
            binding.navView.visibility = if (isKeyboardVisible) View.GONE else View.VISIBLE

            insets
        }
    }
}

/*
I suck at lambda and the keyboard fix was help with AI, this is the explanation

The full block of code is a function that you are passing to setOnApplyWindowInsetsListener.
This listener expects a function that takes two arguments:

A View object (the view the listener is attached to).

A WindowInsetsCompat object (the data about the keyboard, status bars, etc.).

The lambda syntax defines this function inline.

{ parameters -> body }: This is the general structure.
 The curly braces {} contain the whole lambda.
 The arrow -> separates the parameters from the function's body (the code to be executed).

The Parameters: _ and insets
_, insets is the list of parameters for this function.

insets: This is a name you give to the second parameter passed into the function, which is the WindowInsetsCompat object.
Your code uses this object with insets.isVisible(...), so it needs a name.

_ (Underscore): This is a special convention in Kotlin.
The underscore is used for a parameter that you do not need to use inside the function's body. In this case, the first parameter provided by the listener is the View itself (binding.root). Since your code doesn't need to reference that View object inside the lambda, you use _ to signify that you are intentionally ignoring it. This improves code readability.

In short, you are telling the program: "Define a function that accepts two parameters.
 Ignore the first one, and name the second one insets so I can use it."
  */
