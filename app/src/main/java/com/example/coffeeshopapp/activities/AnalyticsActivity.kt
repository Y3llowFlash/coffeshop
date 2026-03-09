package com.example.coffeeshopapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.coffeeshopapp.R
import com.example.coffeeshopapp.viewmodel.AnalyticsViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth

class AnalyticsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: AnalyticsViewModel
    private lateinit var dailyLineChart: LineChart
    private lateinit var monthlyBarChart: BarChart
    private lateinit var topDrinksPieChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            navigateToLogin()
            return
        }

        viewModel = ViewModelProvider(this)[AnalyticsViewModel::class.java]
        setContentView(createContentView())
        loadAnalytics()
    }

    private fun createContentView(): LinearLayout {
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setPadding(48, 48, 48, 48)

            addView(TextView(context).apply {
                text = "Sales Analytics"
                textSize = 28f
                gravity = Gravity.CENTER
            })

            addView(createSectionLabel("Daily Revenue"))
            dailyLineChart = createLineChart()
            addView(dailyLineChart)

            addView(createSectionLabel("Monthly Revenue"))
            monthlyBarChart = createBarChart()
            addView(monthlyBarChart)

            addView(createSectionLabel("Top Drinks"))
            topDrinksPieChart = createPieChart()
            addView(topDrinksPieChart)

            addView(Button(context).apply {
                text = "Sign Out"
                setBackgroundColor(getColor(R.color.coffeeBrown))
                setTextColor(getColor(R.color.cream))
                setOnClickListener {
                    FirebaseAuth.getInstance().signOut()

                    val intent = Intent(this@AnalyticsActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intent)
                    finish()
                }
            })
        }

        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            addView(ScrollView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                addView(content)
            })
        }
    }

    private fun createSectionLabel(label: String): TextView {
        return TextView(this).apply {
            text = label
            textSize = 20f
            setPadding(0, 24, 0, 8)
        }
    }

    private fun createLineChart(): LineChart {
        return LineChart(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                650
            )
            description.isEnabled = false
            legend.form = Legend.LegendForm.LINE
            setTouchEnabled(true)
            setNoDataText("No revenue data")
            axisRight.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.labelRotationAngle = -45f
        }
    }

    private fun createBarChart(): BarChart {
        return BarChart(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                650
            )
            description.isEnabled = false
            legend.isEnabled = false
            setFitBars(true)
            setNoDataText("No monthly revenue data")
            axisRight.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.labelRotationAngle = -30f
        }
    }

    private fun createPieChart(): PieChart {
        return PieChart(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                750
            )
            description.isEnabled = false
            setUsePercentValues(false)
            setEntryLabelColor(getColor(android.R.color.white))
            setNoDataText("No drinks sold this month")
            legend.orientation = Legend.LegendOrientation.VERTICAL
            legend.isWordWrapEnabled = true
        }
    }

    private fun loadAnalytics() {
        viewModel.loadDailyRevenueSeries { result ->
            runOnUiThread {
                if (result.isSuccess) {
                    bindDailyRevenueChart(result.getOrDefault(emptyList()))
                } else {
                    showAnalyticsError()
                }
            }
        }

        viewModel.loadMonthlyRevenueSeries { result ->
            runOnUiThread {
                if (result.isSuccess) {
                    bindMonthlyRevenueChart(result.getOrDefault(emptyList()))
                } else {
                    showAnalyticsError()
                }
            }
        }

        viewModel.loadTopDrinks { result ->
            runOnUiThread {
                if (result.isSuccess) {
                    bindTopDrinksChart(result.getOrDefault(emptyList()))
                } else {
                    showAnalyticsError()
                }
            }
        }
    }

    private fun bindDailyRevenueChart(entries: List<Pair<String, Double>>) {
        val labels = entries.map { it.first.substringAfterLast('-') }
        val lineEntries = entries.mapIndexed { index, item ->
            Entry(index.toFloat(), item.second.toFloat())
        }

        val dataSet = LineDataSet(lineEntries, "Daily Revenue").apply {
            color = getColor(R.color.coffeeBrown)
            setCircleColor(getColor(R.color.coffeeBrown))
            lineWidth = 3f
            circleRadius = 4f
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        dailyLineChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        dailyLineChart.axisLeft.valueFormatter = CurrencyValueFormatter()
        dailyLineChart.data = LineData(dataSet)
        dailyLineChart.invalidate()
    }

    private fun bindMonthlyRevenueChart(entries: List<Pair<String, Double>>) {
        val labels = entries.map { it.first.substringAfter('-') }
        val barEntries = entries.mapIndexed { index, item ->
            BarEntry(index.toFloat(), item.second.toFloat())
        }

        val dataSet = BarDataSet(barEntries, "Monthly Revenue").apply {
            color = getColor(R.color.latte)
            setDrawValues(false)
        }

        monthlyBarChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        monthlyBarChart.axisLeft.valueFormatter = CurrencyValueFormatter()
        monthlyBarChart.data = BarData(dataSet).apply { barWidth = 0.7f }
        monthlyBarChart.invalidate()
    }

    private fun bindTopDrinksChart(entries: List<Pair<String, Int>>) {
        val pieEntries = entries.map { PieEntry(it.second.toFloat(), it.first) }
        val colors = listOf(
            getColor(R.color.coffeeBrown),
            getColor(R.color.latte),
            getColor(R.color.mochaText),
            getColor(R.color.coffeeShadow),
            getColor(android.R.color.holo_orange_light)
        )

        val dataSet = PieDataSet(pieEntries, "Top Drinks").apply {
            this.colors = colors
            valueTextColor = getColor(android.R.color.white)
            sliceSpace = 3f
        }

        topDrinksPieChart.data = PieData(dataSet).apply {
            setDrawValues(true)
        }
        topDrinksPieChart.centerText = "Top Drinks"
        topDrinksPieChart.invalidate()
    }

    private fun showAnalyticsError() {
        Toast.makeText(this, "Failed to load analytics", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java).apply {
            putExtra(LoginActivity.EXTRA_DESTINATION, AnalyticsActivity::class.java.name)
        })
        finish()
    }

    private class CurrencyValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return "${value} MMK"
        }
    }
}
