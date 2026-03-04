package com.example.coffeeshopapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button // Import Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Find the RecyclerView
        val rvCoffeeList = findViewById<RecyclerView>(R.id.rvCoffeeList)

        // 2. Setup the Layout Manager (Lists it vertically)
        rvCoffeeList.layoutManager = LinearLayoutManager(this)

        // 3. Setup the Adapter
        // We pass the MockData list and the click action
        val adapter = CoffeeAdapter(MockData.menuList) { selectedCoffee ->

            // Create the "Envelope" (Intent) to go to DetailActivity
            val intent = Intent(this, DetailActivity::class.java)

            // Put the selected coffee object inside the envelope
            intent.putExtra("COFFEE_EXTRA", selectedCoffee)

            // Send the envelope (Start the new screen)
            startActivity(intent)
        }

        rvCoffeeList.adapter = adapter

        // --- NEW CODE STARTS HERE (Day 2 Round 3) ---

        // 4. Find the "View Cart" Button (Make sure this ID exists in your XML!)
        // Note: You might need to check your activity_main.xml to ensure you added the button there.
        val btnViewCart = findViewById<Button>(R.id.btnViewCart)

        btnViewCart.setOnClickListener {
            // Open the Cart Screen
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        // --- NEW CODE ENDS HERE ---
    }
}