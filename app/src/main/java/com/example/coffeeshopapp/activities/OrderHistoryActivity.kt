package com.example.coffeeshopapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshopapp.OrderHistoryAdapter
import com.example.coffeeshopapp.viewmodel.OrderHistoryViewModel
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast

class OrderHistoryActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: OrderHistoryViewModel
    private lateinit var adapter: OrderHistoryAdapter

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

        setContentView(createContentView())
        loadOrders()
    }

    private fun createContentView(): LinearLayout {
        val recyclerView = RecyclerView(this).apply {
            layoutManager = LinearLayoutManager(this@OrderHistoryActivity)
            adapter = this@OrderHistoryActivity.adapter
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }

        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setPadding(32, 48, 32, 32)

            addView(TextView(context).apply {
                text = "Order History"
                textSize = 28f
                gravity = Gravity.CENTER
            })

            addView(recyclerView)

            addView(Button(context).apply {
                text = "Back to Menu"
                setOnClickListener {
                    startActivity(Intent(this@OrderHistoryActivity, MainActivity::class.java))
                    finish()
                }
            })
        }
    }

    private fun loadOrders() {
        viewModel.loadUserOrders { result ->
            runOnUiThread {
                if (result.isSuccess) {
                    adapter.submitList(result.getOrDefault(emptyList()))
                } else {
                    Toast.makeText(this, "Failed to load order history", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
