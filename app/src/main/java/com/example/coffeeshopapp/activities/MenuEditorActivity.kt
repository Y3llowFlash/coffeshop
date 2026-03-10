package com.example.coffeeshopapp.activities

import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.ScrollView
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

        setContentView(createContentView())
        loadMenuItems(userId)
    }

    private fun createContentView(): LinearLayout {
        val recyclerView = RecyclerView(this).apply {
            layoutManager = LinearLayoutManager(this@MenuEditorActivity)
            adapter = this@MenuEditorActivity.adapter
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }

        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            addView(Button(context).apply {
                text = "+ Add Coffee"
                setOnClickListener {
                    showMenuItemDialog(null)
                }
            })

            addView(recyclerView)
        }
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

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 24, 32, 24)
        }

        val nameInput = createTextInput("Name", existingItem?.name.orEmpty())
        val descriptionInput = createTextInput("Description", existingItem?.description.orEmpty())
        val priceInput = createTextInput(
            "Price",
            existingItem?.price?.toString().orEmpty(),
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        )

        val categoryGroup = RadioGroup(this).apply {
            orientation = RadioGroup.VERTICAL
        }
        val rbCoffee = RadioButton(this).apply { text = "coffee"; id = View.generateViewId() }
        val rbNonCoffee = RadioButton(this).apply { text = "non_coffee"; id = View.generateViewId() }
        val rbEats = RadioButton(this).apply { text = "eats"; id = View.generateViewId() }
        categoryGroup.addView(rbCoffee)
        categoryGroup.addView(rbNonCoffee)
        categoryGroup.addView(rbEats)

        val temperatureGroup = RadioGroup(this).apply {
            orientation = RadioGroup.VERTICAL
        }
        val rbHot = RadioButton(this).apply { text = "hot"; id = View.generateViewId() }
        val rbIced = RadioButton(this).apply { text = "iced"; id = View.generateViewId() }
        temperatureGroup.addView(rbHot)
        temperatureGroup.addView(rbIced)

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
            "eats" -> categoryGroup.check(rbEats.id)
            "non_coffee" -> categoryGroup.check(rbNonCoffee.id)
            else -> categoryGroup.check(rbCoffee.id)
        }

        when (existingTemperature) {
            "iced" -> temperatureGroup.check(rbIced.id)
            else -> temperatureGroup.check(rbHot.id)
        }

        fun updateTemperatureState() {
            val isEats = categoryGroup.checkedRadioButtonId == rbEats.id
            temperatureGroup.isEnabled = !isEats
            rbHot.isEnabled = !isEats
            rbIced.isEnabled = !isEats
            if (isEats) {
                temperatureGroup.clearCheck()
            } else if (temperatureGroup.checkedRadioButtonId == View.NO_ID) {
                temperatureGroup.check(rbHot.id)
            }
        }

        categoryGroup.setOnCheckedChangeListener { _, _ ->
            updateTemperatureState()
        }
        updateTemperatureState()

        imagePreview = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                420
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

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

        container.addView(nameInput)
        container.addView(descriptionInput)
        container.addView(priceInput)
        container.addView(TextView(this).apply { text = "Drink Category" })
        container.addView(categoryGroup)
        container.addView(TextView(this).apply {
            text = "Temperature"
            setPadding(0, 16, 0, 0)
        })
        container.addView(temperatureGroup)
        container.addView(imagePreview)
        container.addView(Button(this).apply {
            text = "Upload Image"
            setOnClickListener {
                imagePicker.launch("image/*")
            }
        })
        refreshPreview()

        val dialogView = ScrollView(this).apply {
            addView(container)
        }

        AlertDialog.Builder(this)
            .setTitle(if (existingItem == null) "Add Coffee" else "Edit Coffee")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                saveMenuItem(
                    existingItem,
                    nameInput,
                    descriptionInput,
                    priceInput,
                    categoryGroup,
                    temperatureGroup,
                    rbCoffee.id,
                    rbNonCoffee.id,
                    rbEats.id,
                    rbHot.id,
                    rbIced.id
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveMenuItem(
        existingItem: CoffeeModel?,
        nameInput: EditText,
        descriptionInput: EditText,
        priceInput: EditText,
        categoryGroup: RadioGroup,
        temperatureGroup: RadioGroup,
        coffeeId: Int,
        nonCoffeeId: Int,
        eatsId: Int,
        hotId: Int,
        icedId: Int
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
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
            return
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

    private fun createTextInput(
        hint: String,
        value: String,
        inputType: Int = InputType.TYPE_CLASS_TEXT
    ): EditText {
        return EditText(this).apply {
            this.hint = hint
            setText(value)
            this.inputType = inputType
        }
    }
}
