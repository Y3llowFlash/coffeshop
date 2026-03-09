package com.example.coffeeshopapp.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshopapp.CoffeeAdapter
import com.example.coffeeshopapp.R
import com.google.firebase.auth.FirebaseAuth
import com.example.coffeeshopapp.viewmodel.CartViewModel
import com.example.coffeeshopapp.viewmodel.CoffeeViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var coffeeViewModel: CoffeeViewModel
    private lateinit var cartViewModel: CartViewModel
    private lateinit var coffeeAdapter: CoffeeAdapter
    private lateinit var etSearchCoffee: EditText
    private lateinit var filterButtons: List<Button>
    private var selectedType: String = "all"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        coffeeViewModel = ViewModelProvider(this)[CoffeeViewModel::class.java]
        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]
        cartViewModel.loadCart(this)

        val rvCoffeeList = findViewById<RecyclerView>(R.id.rvCoffeeList)
        rvCoffeeList.layoutManager = LinearLayoutManager(this)

        val coffeeList = coffeeViewModel.getCoffeeList()
        coffeeAdapter = CoffeeAdapter(
            coffeeList,
            onItemClick = { selectedCoffee ->
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra(COFFEE_ID_EXTRA, selectedCoffee.id)
                startActivity(intent)
            },
            onAddToCartClick = { selectedCoffee ->
                cartViewModel.addItem(selectedCoffee)
                cartViewModel.saveCart(this)
                Toast.makeText(this, "Added ${selectedCoffee.name} to Cart!", Toast.LENGTH_SHORT)
                    .show()
            }
        )
        rvCoffeeList.adapter = coffeeAdapter

        etSearchCoffee = findViewById(R.id.etSearchCoffee)
        etSearchCoffee.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applyFilters()
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })

        filterButtons = listOf(
            findViewById(R.id.btnFilterAll),
            findViewById(R.id.btnFilterCoffee),
            findViewById(R.id.btnFilterHot),
            findViewById(R.id.btnFilterIced),
            findViewById(R.id.btnFilterNonCoffee)
        )

        bindFilterButton(findViewById(R.id.btnFilterAll), "all")
        bindFilterButton(findViewById(R.id.btnFilterCoffee), "coffee")
        bindFilterButton(findViewById(R.id.btnFilterHot), "hot")
        bindFilterButton(findViewById(R.id.btnFilterIced), "iced")
        bindFilterButton(findViewById(R.id.btnFilterNonCoffee), "non_coffee")
        updateFilterButtonState()

        val imgCart = findViewById<ImageView>(R.id.imgCart)
        imgCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        val btnViewCart = findViewById<Button>(R.id.btnViewCart)
        btnViewCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    fun openAnalytics() {
        openProtectedScreen(AnalyticsActivity::class.java)
    }

    fun openOrderHistory() {
        openProtectedScreen(OrderHistoryActivity::class.java)
    }

    private fun bindFilterButton(button: Button, type: String) {
        button.setOnClickListener {
            selectedType = type
            updateFilterButtonState()
            applyFilters()
        }
    }

    private fun applyFilters() {
        coffeeAdapter.filter(etSearchCoffee.text?.toString().orEmpty(), selectedType)
    }

    private fun updateFilterButtonState() {
//        for (button in filterButtons) {
//            button.isSelected = button.tag == selectedType
//
//        }
        filterButtons.forEach { it.isSelected = false }
        filterButtons.find { it.tag == selectedType }?.isSelected = true
    }

    private fun openProtectedScreen(destination: Class<*>) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val intent = if (currentUser == null) {
            Intent(this, LoginActivity::class.java).apply {
                putExtra(LoginActivity.EXTRA_DESTINATION, destination.name)
            }
        } else {
            Intent(this, destination)
        }
        startActivity(intent)
    }

    companion object {
        const val COFFEE_ID_EXTRA = "COFFEE_ID_EXTRA"
    }
}
