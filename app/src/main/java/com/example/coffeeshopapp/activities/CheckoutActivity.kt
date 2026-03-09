package com.example.coffeeshopapp.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.coffeeshopapp.model.CartItem
import com.example.coffeeshopapp.viewmodel.CartViewModel
import com.example.coffeeshopapp.viewmodel.CheckoutViewModel

class CheckoutActivity : AppCompatActivity() {
    private lateinit var cartViewModel: CartViewModel
    private lateinit var checkoutViewModel: CheckoutViewModel
    private lateinit var checkoutItems: List<CartItem>
    private var totalPrice: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]
        checkoutViewModel = ViewModelProvider(this)[CheckoutViewModel::class.java]
        cartViewModel.loadCart(this)

        checkoutItems = cartViewModel.getCartItems().map { it.copy() }
        totalPrice = cartViewModel.getTotalPrice()

        if (checkoutItems.isEmpty()) {
            Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setContentView(createContentView())
    }

    private fun createContentView(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setPadding(48, 64, 48, 48)

            addView(TextView(context).apply {
                text = "Checkout"
                textSize = 28f
                gravity = Gravity.CENTER
            })

            addView(TextView(context).apply {
                text = buildOrderSummary()
                textSize = 18f
                setPadding(0, 32, 0, 32)
            })

            addView(TextView(context).apply {
                text = "Total: $${String.format("%.2f", totalPrice)}"
                textSize = 22f
                setPadding(0, 0, 0, 32)
            })

            addView(Button(context).apply {
                text = "Place Order"
                setOnClickListener {
                    saveOrder()
                }
            })
        }
    }

    private fun buildOrderSummary(): String {
        return checkoutItems.joinToString(separator = "\n") { item ->
            "${item.coffee.name} x${item.quantity} - $${String.format("%.2f", item.coffee.price)}"
        }
    }

    private fun saveOrder() {
        checkoutViewModel.saveOrder(checkoutItems, totalPrice) { result ->
            runOnUiThread {
                if (result.isSuccess) {
                    showSuccessDialog()
                } else {
                    Toast.makeText(this, "Order save failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showSuccessDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Order Successful!")
        builder.setMessage(
            "Your coffee is being prepared.\nTotal: $${String.format("%.2f", totalPrice)}"
        )
        builder.setPositiveButton("OK") { _: DialogInterface, _: Int ->
            cartViewModel.clearCart()
            cartViewModel.saveCart(this)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        builder.setCancelable(false)
        builder.show()
    }
}
