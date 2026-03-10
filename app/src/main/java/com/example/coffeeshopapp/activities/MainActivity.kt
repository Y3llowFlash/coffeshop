package com.example.coffeeshopapp.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshopapp.CoffeeAdapter
import com.example.coffeeshopapp.R
import com.google.android.material.chip.Chip
import com.example.coffeeshopapp.viewmodel.CartViewModel
import com.example.coffeeshopapp.viewmodel.CoffeeViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var coffeeViewModel: CoffeeViewModel
    private lateinit var cartViewModel: CartViewModel
    private lateinit var coffeeAdapter: CoffeeAdapter
    private lateinit var etSearchCoffee: EditText
    private lateinit var filterButtons: List<Chip>
    private lateinit var loadingIndicator: View
    private lateinit var cartBadgeText: TextView
    private var selectedType: String = "all"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        coffeeViewModel = ViewModelProvider(this)[CoffeeViewModel::class.java]
        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]
        cartViewModel.loadCart(this)

        val rvCoffeeList = findViewById<RecyclerView>(R.id.rvCoffeeList)
        rvCoffeeList.layoutManager = LinearLayoutManager(this)

        coffeeAdapter = CoffeeAdapter(
            emptyList(),
            onItemClick = { selectedCoffee ->
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra(COFFEE_EXTRA, selectedCoffee)
                startActivity(intent)
            },
            onAddToCartClick = { selectedCoffee ->
                cartViewModel.addItem(selectedCoffee)
                cartViewModel.saveCart(this)
                updateCartBadge(cartViewModel.getCartItemCount())
                Toast.makeText(this, "Added ${selectedCoffee.name} to Cart!", Toast.LENGTH_SHORT)
                    .show()
            }
        )
        rvCoffeeList.adapter = coffeeAdapter
        loadingIndicator = findViewById(R.id.progressMainMenu)
        cartBadgeText = findViewById(R.id.tvCartBadge)

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
            findViewById(R.id.btnFilterNonCoffee),
            findViewById(R.id.btnFilterEats),
            findViewById(R.id.btnFilterHot),
            findViewById(R.id.btnFilterIced)
        )

        bindFilterButton(findViewById(R.id.btnFilterAll), "all")
        bindFilterButton(findViewById(R.id.btnFilterCoffee), "coffee")
        bindFilterButton(findViewById(R.id.btnFilterNonCoffee), "non_coffee")
        bindFilterButton(findViewById(R.id.btnFilterEats), "eats")
        bindFilterButton(findViewById(R.id.btnFilterHot), "hot")
        bindFilterButton(findViewById(R.id.btnFilterIced), "iced")
        updateFilterButtonState()

        val imgCart = findViewById<ImageView>(R.id.imgCart)
        imgCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        val imgMenuEditor = findViewById<ImageView>(R.id.imgMenuEditor)
        imgMenuEditor.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser

            if (user == null) {
                Toast.makeText(
                    this,
                    "Please sign in to edit menu",
                    Toast.LENGTH_SHORT
                ).show()

                startActivity(Intent(this, LoginActivity::class.java))
                return@setOnClickListener
            }

            startActivity(Intent(this, MenuEditorActivity::class.java))
        }

        val btnViewCart = findViewById<Button>(R.id.btnViewCart)
        btnViewCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        val btnOrderHistory = findViewById<Button>(R.id.btnOrderHistory)
        btnOrderHistory.setOnClickListener {
            openOrderHistory()
        }

        val btnAnalytics = findViewById<Button>(R.id.btnAnalytics)
        btnAnalytics.setOnClickListener {
            openAnalytics()
        }

        updateCartBadge(cartViewModel.getCartItemCount())
        reloadMenu()
    }

    override fun onResume() {
        super.onResume()
        cartViewModel.loadCart(this)
        updateCartBadge(cartViewModel.getCartItemCount())
        reloadMenu()
    }

    fun openAnalytics() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(
                this,
                "Please sign in to use Sales Analytics",
                Toast.LENGTH_SHORT
            ).show()

            startActivity(Intent(this, LoginActivity::class.java).apply {
                putExtra(LoginActivity.EXTRA_DESTINATION, AnalyticsActivity::class.java.name)
            })
            return
        }

        startActivity(Intent(this, AnalyticsActivity::class.java))
    }

    fun openOrderHistory() {
        openProtectedScreen(OrderHistoryActivity::class.java)
    }

    private fun bindFilterButton(button: Chip, type: String) {
        button.setOnClickListener {
            selectedType = type
            updateFilterButtonState()
            applyFilters()
        }
    }

    private fun applyFilters() {
        if (!::etSearchCoffee.isInitialized) return
        coffeeAdapter.filter(etSearchCoffee.text?.toString().orEmpty(), selectedType)
    }

    private fun reloadMenu() {
        setLoading(true)
        coffeeViewModel.loadMenu(this) { menu ->
            runOnUiThread {
                coffeeAdapter.updateData(menu)
                applyFilters()
                setLoading(false)
            }
        }
    }

    private fun updateFilterButtonState() {
        filterButtons.forEach { it.isChecked = false }
        filterButtons.find { it.tag == selectedType }?.isChecked = true
    }

    private fun setLoading(isLoading: Boolean) {
        if (!::loadingIndicator.isInitialized) return
        loadingIndicator.visibility = if (isLoading && coffeeAdapter.itemCount == 0) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun updateCartBadge(count: Int) {
        if (!::cartBadgeText.isInitialized) return
        if (count > 0) {
            cartBadgeText.visibility = View.VISIBLE
            cartBadgeText.text = if (count > 99) "99+" else count.toString()
        } else {
            cartBadgeText.visibility = View.GONE
        }
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
        const val COFFEE_EXTRA = "coffee"
    }
}
