package com.example.coffeeshopapp.activities

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coffeeshopapp.MenuEditorAdapter
import com.example.coffeeshopapp.R
import com.example.coffeeshopapp.model.CoffeeModel
import com.example.coffeeshopapp.viewmodel.MenuEditorViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

class MenuEditorActivity : AppCompatActivity() {
    private lateinit var viewModel: MenuEditorViewModel
    private lateinit var adapter: MenuEditorAdapter
    private var pendingImageUri: Uri? = null
    private var imagePreview: ImageView? = null
    private var pendingDialogRefresh: (() -> Unit)? = null

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            pendingImageUri = uri
            pendingDialogRefresh?.invoke()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId.isNullOrBlank()) {
            Toast.makeText(this, "Please sign in to edit the menu", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        viewModel = ViewModelProvider(this)[MenuEditorViewModel::class.java]
        adapter = MenuEditorAdapter(
            onEditClick = { showMenuItemDialog(it) },
            onDeleteClick = { deleteMenuItem(userId, it) }
        )

        setContentView(R.layout.activity_menu_editor)

        findViewById<RecyclerView>(R.id.rvMenuItems).apply {
            layoutManager = LinearLayoutManager(this@MenuEditorActivity)
            adapter = this@MenuEditorActivity.adapter
        }

        findViewById<Button>(R.id.btnAddCoffee).setOnClickListener {
            showMenuItemDialog(null)
        }

        loadMenuItems(userId)
    }

    private fun loadMenuItems(userId: String) {
        viewModel.loadMenuItems(userId) { result ->
            runOnUiThread {
                if (result.isSuccess) {
                    adapter.submitList(result.getOrDefault(emptyList()))
                } else {
                    Toast.makeText(this, "Failed to load menu", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showMenuItemDialog(existingItem: CoffeeModel?) {
        pendingImageUri = null

        val dialogView = layoutInflater.inflate(R.layout.dialog_menu_item_editor, null)

        val nameInput = dialogView.findViewById<TextInputEditText>(R.id.etMenuItemName)
        val descriptionInput =
            dialogView.findViewById<TextInputEditText>(R.id.etMenuItemDescription)
        val priceInput = dialogView.findViewById<TextInputEditText>(R.id.etMenuItemPrice)
        val categoryGroup = dialogView.findViewById<RadioGroup>(R.id.rgDrinkCategory)
        val temperatureGroup = dialogView.findViewById<RadioGroup>(R.id.rgTemperature)
        val temperatureLabel = dialogView.findViewById<TextView>(R.id.tvTemperatureLabel)
        val rbHot = dialogView.findViewById<View>(R.id.rbTemperatureHot)
        val rbIced = dialogView.findViewById<View>(R.id.rbTemperatureIced)

        nameInput.setText(existingItem?.name.orEmpty())
        descriptionInput.setText(existingItem?.description.orEmpty())
        priceInput.setText(existingItem?.price?.toString().orEmpty())

        val existingCategory = when {
            existingItem?.types?.contains("eats") == true -> "eats"
            existingItem?.types?.contains("non_coffee") == true -> "non_coffee"
            else -> "coffee"
        }
        val existingTemperature = when {
            existingItem?.types?.contains("iced") == true -> "iced"
            existingItem?.types?.contains("hot") == true -> "hot"
            else -> null
        }

        when (existingCategory) {
            "eats" -> categoryGroup.check(R.id.rbCategoryEats)
            "non_coffee" -> categoryGroup.check(R.id.rbCategoryNonCoffee)
            else -> categoryGroup.check(R.id.rbCategoryCoffee)
        }

        when (existingTemperature) {
            "iced" -> temperatureGroup.check(R.id.rbTemperatureIced)
            else -> temperatureGroup.check(R.id.rbTemperatureHot)
        }

        fun updateTemperatureState() {
            val isEats = categoryGroup.checkedRadioButtonId == R.id.rbCategoryEats
            temperatureGroup.isEnabled = !isEats
            rbHot.isEnabled = !isEats
            rbIced.isEnabled = !isEats
            temperatureLabel.alpha = if (isEats) 0.45f else 1f
            temperatureGroup.alpha = if (isEats) 0.45f else 1f
            if (isEats) {
                temperatureGroup.clearCheck()
            } else if (temperatureGroup.checkedRadioButtonId == View.NO_ID) {
                temperatureGroup.check(R.id.rbTemperatureHot)
            }
        }

        categoryGroup.setOnCheckedChangeListener { _, _ ->
            updateTemperatureState()
        }
        updateTemperatureState()

        imagePreview = dialogView.findViewById(R.id.imgDialogPreview)

        fun refreshPreview() {
            val preview = imagePreview ?: return
            when {
                pendingImageUri != null -> preview.setImageURI(pendingImageUri)
                existingItem?.imageUrl?.isNotBlank() == true -> {
                    Glide.with(this).load(existingItem.imageUrl).into(preview)
                }
                existingItem?.imageResId ?: 0 != 0 -> {
                    preview.setImageResource(existingItem?.imageResId ?: 0)
                }
                else -> preview.setImageDrawable(null)
            }
        }

        pendingDialogRefresh = ::refreshPreview

        dialogView.findViewById<Button>(R.id.btnUploadImage).setOnClickListener {
            imagePicker.launch("image/*")
        }
        refreshPreview()

        val dialog = AlertDialog.Builder(this)
            .setTitle(if (existingItem == null) "Add Coffee" else "Edit Coffee")
            .setView(dialogView)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val didStartSave = saveMenuItem(
                    existingItem,
                    nameInput,
                    descriptionInput,
                    priceInput,
                    categoryGroup,
                    temperatureGroup,
                    R.id.rbCategoryCoffee,
                    R.id.rbCategoryNonCoffee,
                    R.id.rbCategoryEats,
                    R.id.rbTemperatureHot,
                    R.id.rbTemperatureIced
                )
                if (didStartSave) {
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun saveMenuItem(
        existingItem: CoffeeModel?,
        nameInput: TextInputEditText,
        descriptionInput: TextInputEditText,
        priceInput: TextInputEditText,
        categoryGroup: RadioGroup,
        temperatureGroup: RadioGroup,
        coffeeId: Int,
        nonCoffeeId: Int,
        eatsId: Int,
        hotId: Int,
        icedId: Int
    ): Boolean {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return false
        val name = nameInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()
        val price = priceInput.text.toString().toDoubleOrNull()
        val category = when (categoryGroup.checkedRadioButtonId) {
            coffeeId -> "coffee"
            nonCoffeeId -> "non_coffee"
            eatsId -> "eats"
            else -> null
        }
        val temperature = when (temperatureGroup.checkedRadioButtonId) {
            hotId -> "hot"
            icedId -> "iced"
            else -> null
        }
        val types = when (category) {
            "coffee", "non_coffee" -> listOfNotNull(category, temperature)
            "eats" -> listOf("eats")
            else -> emptyList()
        }

        if (name.isBlank() || description.isBlank() || price == null || types.isEmpty()) {
            Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show()
            return false
        }

        val item = CoffeeModel(
            id = existingItem?.id ?: UUID.randomUUID().hashCode(),
            name = name,
            description = description,
            price = price,
            types = types,
            imageUrl = existingItem?.imageUrl.orEmpty()
        )

        viewModel.saveMenuItem(userId, item, pendingImageUri) { result ->
            runOnUiThread {
                if (result.isSuccess) {
                    pendingImageUri = null
                    loadMenuItems(userId)
                    Toast.makeText(this, "Menu item saved", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to save menu item", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return true
    }

    private fun deleteMenuItem(userId: String, item: CoffeeModel) {
        viewModel.deleteMenuItem(userId, item.id.toString()) { result ->
            runOnUiThread {
                if (result.isSuccess) {
                    loadMenuItems(userId)
                    Toast.makeText(this, "Menu item deleted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to delete menu item", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
