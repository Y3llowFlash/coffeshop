package com.example.coffeeshopapp.activities

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.coffeeshopapp.R
import com.example.coffeeshopapp.formatMMK
import com.example.coffeeshopapp.model.CoffeeModel
import com.example.coffeeshopapp.viewmodel.CartViewModel

class DetailActivity : AppCompatActivity() {
    private lateinit var cartViewModel: CartViewModel
    private var quantity = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]

        val img: ImageView = findViewById(R.id.imgDetailCoffee)
        val tvName: TextView = findViewById(R.id.tvDetailName)
        val tvPrice: TextView = findViewById(R.id.tvDetailPrice)
        val tvDesc: TextView = findViewById(R.id.tvDetailDesc)
        val btnMinusQuantity: ImageButton = findViewById(R.id.btnMinusQuantity)
        val btnPlusQuantity: ImageButton = findViewById(R.id.btnPlusQuantity)
        val tvQuantityValue: TextView = findViewById(R.id.tvQuantityValue)
        val btnAdd: Button = findViewById(R.id.btnAddCart)

        @Suppress("DEPRECATION")
        val coffee = intent.getSerializableExtra(MainActivity.COFFEE_EXTRA) as? CoffeeModel

        if (coffee != null) {
            fun updateQuantityText() {
                tvQuantityValue.text = quantity.toString()
            }

            when {
                coffee.imageUrl.isNotBlank() -> {
                    Glide.with(this)
                        .load(coffee.imageUrl)
                        .into(img)
                }
                coffee.imageDrawable.isNotBlank() -> {
                    val resId = resources.getIdentifier(
                        coffee.imageDrawable,
                        "drawable",
                        packageName
                    )
                    if (resId != 0) {
                        img.setImageResource(resId)
                    } else {
                        img.setImageResource(coffee.imageResId)
                    }
                }
                else -> img.setImageResource(coffee.imageResId)
            }
            tvName.text = coffee.name
            tvPrice.text = formatMMK(coffee.price)
            tvDesc.text = coffee.description
            updateQuantityText()

            btnPlusQuantity.setOnClickListener {
                quantity++
                updateQuantityText()
            }

            btnMinusQuantity.setOnClickListener {
                if (quantity > 1) {
                    quantity--
                    updateQuantityText()
                }
            }

            btnAdd.setOnClickListener {
                cartViewModel.addItem(coffee, quantity)
                cartViewModel.saveCart(this)
                Toast.makeText(
                    this,
                    "Added $quantity ${coffee.name} to Cart!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(this, "Coffee not found.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
