package com.example.coffeeshopapp

object MockData {
    // A hardcoded list of coffees
    val menuList = listOf(
        CoffeeModel(
            id = 1,
            name = "Espresso",
            description = "Strong, concentrated coffee served in small shots.",
            price = 3.50,
            imageResId = R.drawable.coffee_espresso // We will add these images next!
        ),
        CoffeeModel(
            id = 2,
            name = "Cappuccino",
            description = "Equal parts espresso, steamed milk, and milk foam.",
            price = 4.50,
            imageResId = R.drawable.coffee_cappuccino
        ),
        CoffeeModel(
            id = 3,
            name = "Latte",
            description = "Espresso with steamed milk and a light layer of foam.",
            price = 5.00,
            imageResId = R.drawable.coffee_latte
        ),
        CoffeeModel(
            id = 4,
            name = "Mocha",
            description = "Espresso with chocolate syrup and steamed milk.",
            price = 5.50,
            imageResId = R.drawable.coffee_mocha
        ),
        CoffeeModel(
            id = 5,
            name = "Cold Brew",
            description = "Coffee brewed with cold water for a smooth flavor.",
            price = 4.00,
            imageResId = R.drawable.coffee_coldbrew
        )
    )
}