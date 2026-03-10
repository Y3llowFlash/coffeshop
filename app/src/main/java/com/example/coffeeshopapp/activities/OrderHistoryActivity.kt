package com.example.coffeeshopapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshopapp.R
import com.example.coffeeshopapp.OrderHistoryAdapter
import com.example.coffeeshopapp.viewmodel.OrderHistoryViewModel
import com.google.firebase.auth.FirebaseAuth

class OrderHistoryActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: OrderHistoryViewModel
    private lateinit var adapter: OrderHistoryAdapter
    private lateinit var loadingIndicator: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java).apply {
                putExtra(LoginActivity.EXTRA_DESTINATION, OrderHistoryActivity::class.java.name)
            })
            finish()
            return
        }

        viewModel = ViewModelProvider(this)[OrderHistoryViewModel::class.java]
        adapter = OrderHistoryAdapter()

        setContentView(R.layout.activity_order_history)

        findViewById<RecyclerView>(R.id.rvOrderHistory).apply {
            layoutManager = LinearLayoutManager(this@OrderHistoryActivity)
            adapter = this@OrderHistoryActivity.adapter
        }
        loadingIndicator = findViewById(R.id.progressOrderHistory)

        findViewById<Button>(R.id.btnBackToMenu).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        loadOrders()
    }

    private fun loadOrders() {
        setLoading(true)
        viewModel.loadUserOrders { result ->
            runOnUiThread {
                setLoading(false)
                if (result.isSuccess) {
                    adapter.submitList(result.getOrDefault(emptyList()))
                } else {
                    Toast.makeText(this, "Failed to load order history", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        if (!::loadingIndicator.isInitialized) return
        loadingIndicator.visibility = if (isLoading && adapter.itemCount == 0) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}
