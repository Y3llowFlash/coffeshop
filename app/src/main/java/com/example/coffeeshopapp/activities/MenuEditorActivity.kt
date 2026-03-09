package com.example.coffeeshopapp.activities

import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
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

        val typeCheckboxes = linkedMapOf(
            "coffee" to CheckBox(this).apply { text = "coffee"; isChecked = existingItem?.types?.contains("coffee") == true },
            "hot" to CheckBox(this).apply { text = "hot"; isChecked = existingItem?.types?.contains("hot") == true },
            "iced" to CheckBox(this).apply { text = "iced"; isChecked = existingItem?.types?.contains("iced") == true },
            "non_coffee" to CheckBox(this).apply { text = "non_coffee"; isChecked = existingItem?.types?.contains("non_coffee") == true }
        )

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
        container.addView(TextView(this).apply { text = "Types" })
        typeCheckboxes.values.forEach(container::addView)
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
                saveMenuItem(existingItem, nameInput, descriptionInput, priceInput, typeCheckboxes)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveMenuItem(
        existingItem: CoffeeModel?,
        nameInput: EditText,
        descriptionInput: EditText,
        priceInput: EditText,
        typeCheckboxes: Map<String, CheckBox>
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val name = nameInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()
        val price = priceInput.text.toString().toDoubleOrNull()
        val types = typeCheckboxes.filterValues { it.isChecked }.keys.toList()

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
