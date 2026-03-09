package com.example.coffeeshopapp.model

import com.example.coffeeshopapp.R

object MockData {
    val menuList = listOf(
        CoffeeModel(
            id = 1,
            name = "Espresso",
            description = "Strong, concentrated coffee served in small shots.",
            price = 3.50,
            types = listOf("coffee", "hot"),
            imageResId = R.drawable.coffee_espresso,
            imageDrawable = "coffee_espresso"
        ),
        CoffeeModel(
            id = 2,
            name = "Cappuccino",
            description = "Equal parts espresso, steamed milk, and milk foam.",
            price = 4.50,
            types = listOf("coffee", "hot"),
            imageResId = R.drawable.coffee_cappuccino,
            imageDrawable = "coffee_cappuccino"
        ),
        CoffeeModel(
            id = 3,
            name = "Latte",
            description = "Espresso with steamed milk and a light layer of foam.",
            price = 5.00,
            types = listOf("coffee", "hot"),
            imageResId = R.drawable.coffee_latte,
            imageDrawable = "coffee_latte"
        ),
        CoffeeModel(
            id = 4,
            name = "Mocha",
            description = "Espresso with chocolate syrup and steamed milk.",
            price = 5.50,
            types = listOf("coffee", "hot"),
            imageResId = R.drawable.coffee_mocha,
            imageDrawable = "coffee_mocha"
        ),
        CoffeeModel(
            id = 5,
            name = "Cold Brew",
            description = "Coffee brewed with cold water for a smooth flavor.",
            price = 4.00,
            types = listOf("coffee", "iced"),
            imageResId = R.drawable.coffee_coldbrew,
            imageDrawable = "coffee_coldbrew"
        ),
        CoffeeModel(
            id = 6,
            name = "Americano",
            description = "Espresso shots diluted with hot water for a bold, smooth cup.",
            price = 3.75,
            types = listOf("coffee", "hot"),
            imageResId = R.drawable.coffee_espresso,
            imageDrawable = "coffee_espresso"
        ),
        CoffeeModel(
            id = 7,
            name = "Flat White",
            description = "Double espresso with silky microfoam and a velvety texture.",
            price = 4.75,
            types = listOf("coffee", "hot"),
            imageResId = R.drawable.coffee_latte,
            imageDrawable = "coffee_latte"
        ),
        CoffeeModel(
            id = 8,
            name = "Macchiato",
            description = "Espresso topped with a small dollop of foamed milk.",
            price = 4.25,
            types = listOf("coffee", "hot"),
            imageResId = R.drawable.coffee_cappuccino,
            imageDrawable = "coffee_cappuccino"
        ),
        CoffeeModel(
            id = 9,
            name = "Affogato",
            description = "A scoop of vanilla ice cream with a hot espresso shot poured over.",
            price = 5.75,
            types = listOf("non_coffee", "hot"),
            imageResId = R.drawable.coffee_mocha,
            imageDrawable = "coffee_mocha"
        ),
        CoffeeModel(
            id = 10,
            name = "Iced Americano",
            description = "Chilled espresso and water served over ice for a crisp finish.",
            price = 4.25,
            types = listOf("coffee", "iced"),
            imageResId = R.drawable.coffee_coldbrew,
            imageDrawable = "coffee_coldbrew"
        )
    )
}
