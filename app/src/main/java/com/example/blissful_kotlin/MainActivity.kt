package com.example.blissful_kotlin

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val productSpinner = findViewById<Spinner>(R.id.product_spinner)
        val locationEditText = findViewById<EditText>(R.id.location_edit_text)
        val phoneEditText = findViewById<EditText>(R.id.phone_edit_text)
        val noteEditText = findViewById<EditText>(R.id.note_edit_text)
        val orderButton = findViewById<Button>(R.id.order_button)
        val confirmationText = findViewById<TextView>(R.id.confirmation_text)

        val products = arrayOf("Cake", "Cupcake", "Brownie")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, products)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        productSpinner.adapter = adapter

        orderButton.setOnClickListener {
            val product = productSpinner.selectedItem.toString()
            val location = locationEditText.text.toString()
            val phone = phoneEditText.text.toString()
            val note = noteEditText.text.toString()

            if (location.isBlank() || phone.isBlank()) {
                confirmationText.text = "Please enter your location and phone number."
            } else {
                confirmationText.text = "Order placed!\nProduct: $product\nLocation: $location\nPhone: $phone\nNote: $note"
            }
        }
    }
}
