package com.example.coffeeshopapp.repository

import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AnalyticsRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun getDailyRevenue(onResult: (Result<Double>) -> Unit) {
        val todayKey = LocalDate.now().format(DAY_FORMATTER)
        getRevenueByKey("dayKey", todayKey, onResult)
    }

    fun getMonthlyRevenue(onResult: (Result<Double>) -> Unit) {
        val currentMonthKey = LocalDate.now().format(MONTH_FORMATTER)
        getRevenueByKey("monthKey", currentMonthKey, onResult)
    }

    fun getYearlyRevenue(onResult: (Result<Double>) -> Unit) {
        val currentYearKey = LocalDate.now().format(YEAR_FORMATTER)
        getRevenueByKey("yearKey", currentYearKey, onResult)
    }

    fun getTopDrinks(onResult: (Result<List<Pair<String, Int>>>) -> Unit) {
        val currentMonthKey = LocalDate.now().format(MONTH_FORMATTER)

        firestore.collection(ORDERS_COLLECTION)
            .whereEqualTo("monthKey", currentMonthKey)
            .get()
            .addOnSuccessListener { snapshot ->
                val drinkCounts = mutableMapOf<String, Int>()

                snapshot.documents.forEach { document ->
                    val items = document.get("items") as? List<Map<String, Any?>>
                    items.orEmpty().forEach { item ->
                        val name = item["name"] as? String ?: return@forEach
                        val quantity = (item["quantity"] as? Number)?.toInt() ?: 0
                        drinkCounts[name] = (drinkCounts[name] ?: 0) + quantity
                    }
                }

                val topDrinks = drinkCounts.entries
                    .sortedWith(
                        compareByDescending<Map.Entry<String, Int>> { it.value }
                            .thenBy { it.key }
                    )
                    .map { it.key to it.value }

                onResult(Result.success(topDrinks))
            }
            .addOnFailureListener { exception ->
                onResult(Result.failure(exception))
            }
    }

    fun getDailyRevenueSeries(onResult: (Result<List<Pair<String, Double>>>) -> Unit) {
        val currentMonthKey = LocalDate.now().format(MONTH_FORMATTER)

        firestore.collection(ORDERS_COLLECTION)
            .whereEqualTo("monthKey", currentMonthKey)
            .get()
            .addOnSuccessListener { snapshot ->
                val revenueByDay = linkedMapOf<String, Double>()
                val daysInMonth = LocalDate.now().lengthOfMonth()

                for (day in 1..daysInMonth) {
                    val dateKey = LocalDate.now().withDayOfMonth(day).format(DAY_FORMATTER)
                    revenueByDay[dateKey] = 0.0
                }

                snapshot.documents.forEach { document ->
                    val dayKey = document.getString("dayKey") ?: return@forEach
                    val totalPrice = document.getDouble("totalPrice") ?: 0.0
                    revenueByDay[dayKey] = (revenueByDay[dayKey] ?: 0.0) + totalPrice
                }

                onResult(Result.success(revenueByDay.entries.map { it.key to it.value }))
            }
            .addOnFailureListener { exception ->
                onResult(Result.failure(exception))
            }
    }

    fun getMonthlyRevenueSeries(onResult: (Result<List<Pair<String, Double>>>) -> Unit) {
        val currentYear = LocalDate.now().format(YEAR_FORMATTER)

        firestore.collection(ORDERS_COLLECTION)
            .whereEqualTo("yearKey", currentYear)
            .get()
            .addOnSuccessListener { snapshot ->
                val revenueByMonth = linkedMapOf<String, Double>()

                for (month in 1..12) {
                    val monthKey = String.format("%s-%02d", currentYear, month)
                    revenueByMonth[monthKey] = 0.0
                }

                snapshot.documents.forEach { document ->
                    val monthKey = document.getString("monthKey") ?: return@forEach
                    val totalPrice = document.getDouble("totalPrice") ?: 0.0
                    revenueByMonth[monthKey] = (revenueByMonth[monthKey] ?: 0.0) + totalPrice
                }

                onResult(Result.success(revenueByMonth.entries.map { it.key to it.value }))
            }
            .addOnFailureListener { exception ->
                onResult(Result.failure(exception))
            }
    }

    private fun getRevenueByKey(
        field: String,
        value: String,
        onResult: (Result<Double>) -> Unit
    ) {
        firestore.collection(ORDERS_COLLECTION)
            .whereEqualTo(field, value)
            .get()
            .addOnSuccessListener { snapshot ->
                val revenue = snapshot.documents.sumOf { document ->
                    document.getDouble("totalPrice") ?: 0.0
                }
                onResult(Result.success(revenue))
            }
            .addOnFailureListener { exception ->
                onResult(Result.failure(exception))
            }
    }

    private companion object {
        const val ORDERS_COLLECTION = "orders"
        val DAY_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val MONTH_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM")
        val YEAR_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy")
    }
}
