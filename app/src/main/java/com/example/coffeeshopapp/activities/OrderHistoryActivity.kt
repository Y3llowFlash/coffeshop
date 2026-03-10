package com.example.coffeeshopapp.activities

import android.content.Intent
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshopapp.R
import com.example.coffeeshopapp.OrderHistoryAdapter
import com.example.coffeeshopapp.viewmodel.OrderHistoryViewModel
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class OrderHistoryActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: OrderHistoryViewModel
    private lateinit var adapter: OrderHistoryAdapter
    private lateinit var loadingIndicator: View
    private lateinit var summaryOrders: TextView
    private lateinit var summaryRevenue: TextView
    private lateinit var filterChips: List<Chip>
    private var currentFilter: OrderFilter = OrderFilter.Today
    private var selectedCustomDate: LocalDate? = null

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
        summaryOrders = findViewById(R.id.tvSummaryOrders)
        summaryRevenue = findViewById(R.id.tvSummaryRevenue)
        filterChips = listOf(
            findViewById(R.id.chipFilterToday),
            findViewById(R.id.chipFilterMonth),
            findViewById(R.id.chipFilterYear),
            findViewById(R.id.chipFilterCustom)
        )

        bindFilterChip(findViewById(R.id.chipFilterToday), OrderFilter.Today)
        bindFilterChip(findViewById(R.id.chipFilterMonth), OrderFilter.Month)
        bindFilterChip(findViewById(R.id.chipFilterYear), OrderFilter.Year)
        bindFilterChip(findViewById(R.id.chipFilterCustom), OrderFilter.Custom)
        updateFilterState()

        findViewById<Button>(R.id.btnBackToMenu).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        loadOrders()
    }

    private fun loadOrders() {
        setLoading(true)
        val range = resolveDateRange(currentFilter, selectedCustomDate)
        viewModel.loadUserOrders(range.startDate, range.endDateExclusive) { result ->
            runOnUiThread {
                setLoading(false)
                if (result.isSuccess) {
                    val orders = result.getOrDefault(emptyList())
                    adapter.submitList(orders)
                    updateSummary(range.summaryLabel, orders)
                } else {
                    Toast.makeText(this, "Failed to load order history", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun bindFilterChip(chip: Chip, filter: OrderFilter) {
        chip.setOnClickListener {
            if (filter == OrderFilter.Custom) {
                openDatePicker()
                return@setOnClickListener
            }

            currentFilter = filter
            updateFilterState()
            loadOrders()
        }
    }

    private fun openDatePicker() {
        val initialDate = selectedCustomDate ?: LocalDate.now()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedCustomDate = LocalDate.of(year, month + 1, dayOfMonth)
                currentFilter = OrderFilter.Custom
                updateFilterState()
                loadOrders()
            },
            initialDate.year,
            initialDate.monthValue - 1,
            initialDate.dayOfMonth
        ).show()
    }

    private fun updateFilterState() {
        filterChips.forEach { it.isChecked = false }
        when (currentFilter) {
            OrderFilter.Today -> findViewById<Chip>(R.id.chipFilterToday).isChecked = true
            OrderFilter.Month -> findViewById<Chip>(R.id.chipFilterMonth).isChecked = true
            OrderFilter.Year -> findViewById<Chip>(R.id.chipFilterYear).isChecked = true
            OrderFilter.Custom -> findViewById<Chip>(R.id.chipFilterCustom).isChecked = true
        }
    }

    private fun updateSummary(label: String, orders: List<com.example.coffeeshopapp.model.Order>) {
        val totalRevenue = orders.sumOf { it.totalPrice }
        summaryOrders.text = "Orders $label: ${orders.size}"
        summaryRevenue.text = "Revenue: ${com.example.coffeeshopapp.formatMMK(totalRevenue)}"
    }

    private fun setLoading(isLoading: Boolean) {
        if (!::loadingIndicator.isInitialized) return
        loadingIndicator.visibility = if (isLoading && adapter.itemCount == 0) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun resolveDateRange(filter: OrderFilter, customDate: LocalDate?): DateRange {
        val today = LocalDate.now()
        val zoneId = ZoneId.systemDefault()
        val (start, end, label) = when (filter) {
            OrderFilter.Today -> Triple(today, today.plusDays(1), "Today")
            OrderFilter.Month -> {
                val monthStart = today.withDayOfMonth(1)
                Triple(monthStart, monthStart.plusMonths(1), "This Month")
            }
            OrderFilter.Year -> {
                val yearStart = today.withDayOfYear(1)
                Triple(yearStart, yearStart.plusYears(1), "This Year")
            }
            OrderFilter.Custom -> {
                val selectedDate = customDate ?: today
                Triple(
                    selectedDate,
                    selectedDate.plusDays(1),
                    selectedDate.format(CUSTOM_DATE_FORMAT)
                )
            }
        }

        return DateRange(
            startDate = java.util.Date.from(start.atStartOfDay(zoneId).toInstant()),
            endDateExclusive = java.util.Date.from(end.atStartOfDay(zoneId).toInstant()),
            summaryLabel = label
        )
    }

    private data class DateRange(
        val startDate: java.util.Date,
        val endDateExclusive: java.util.Date,
        val summaryLabel: String
    )

    private enum class OrderFilter {
        Today,
        Month,
        Year,
        Custom
    }

    private companion object {
        val CUSTOM_DATE_FORMAT: DateTimeFormatter =
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
    }
}
