package com.example.coffeeshopapp.repository

import com.example.coffeeshopapp.model.CoffeeModel
import com.example.coffeeshopapp.model.MockData

class CoffeeRepository {
    fun getCoffeeList(): List<CoffeeModel> = MockData.menuList

    fun getCoffeeById(id: Int): CoffeeModel? = MockData.menuList.find { it.id == id }
}
