package com.example.coffeeshopapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.coffeeshopapp.repository.AnalyticsRepository

class AnalyticsViewModel(
    private val repository: AnalyticsRepository = AnalyticsRepository()
) : ViewModel() {
    fun loadDailyRevenue(onResult: (Result<Double>) -> Unit) {
        repository.getDailyRevenue(onResult)
    }

    fun loadMonthlyRevenue(onResult: (Result<Double>) -> Unit) {
        repository.getMonthlyRevenue(onResult)
    }

    fun loadYearlyRevenue(onResult: (Result<Double>) -> Unit) {
        repository.getYearlyRevenue(onResult)
    }

    fun loadTopDrinks(onResult: (Result<List<Pair<String, Int>>>) -> Unit) {
        repository.getTopDrinks(onResult)
    }

    fun loadDailyRevenueSeries(onResult: (Result<List<Pair<String, Double>>>) -> Unit) {
        repository.getDailyRevenueSeries(onResult)
    }

    fun loadMonthlyRevenueSeries(onResult: (Result<List<Pair<String, Double>>>) -> Unit) {
        repository.getMonthlyRevenueSeries(onResult)
    }
}
