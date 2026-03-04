package com.example.coffeeshopapp

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // 1. Get the views
        val img: ImageView = findViewById(R.id.imgDetailCoffee)
        val tvName: TextView = findViewById(R.id.tvDetailName)
        val tvPrice: TextView = findViewById(R.id.tvDetailPrice)
        val tvDesc: TextView = findViewById(R.id.tvDetailDesc)
        val btnAdd: Button = findViewById(R.id.btnAddCart)

        // 2. Unwrap the Envelope (Get the Coffee object)
        val coffee = intent.getSerializableExtra("COFFEE_EXTRA") as? CoffeeModel

        // 3. Display the data (Check if it's not null first)
        if (coffee != null) {
            img.setImageResource(coffee.imageResId)
            tvName.text = coffee.name
            tvPrice.text = "$${coffee.price}"
            tvDesc.text = coffee.description

            // 4. Handle Button Click (UPDATED FOR DAY 2)
            btnAdd.setOnClickListener {
                // --- NEW LOGIC START ---

                // Add the specific coffee to our global cart manager
                CartManager.addItem(coffee)

                // Show feedback to the user
                Toast.makeText(this, "Added ${coffee.name} to Cart!", Toast.LENGTH_SHORT).show()

                // Optional: If you want to go back to the menu immediately after adding, uncomment the line below:
                // finish()

                // --- NEW LOGIC END ---
            }
        }
    }
}