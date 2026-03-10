package com.example.coffeeshopapp.model

import com.example.coffeeshopapp.R

object MockData {

    val menuList = listOf(

        CoffeeModel(
            id = 1,
            name = "Espresso",
            description = "Strong, concentrated coffee served in small shots.",
            price = 3000.0,
            types = listOf("coffee", "hot"),
            imageResId = R.drawable.coffee_espresso,
            imageDrawable = "coffee_espresso"
        ),

        CoffeeModel(
            id = 2,
            name = "Cappuccino",
            description = "Equal parts espresso, steamed milk, and milk foam.",
            price = 4500.0,
            types = listOf("coffee", "hot"),
            imageResId = R.drawable.coffee_cappuccino,
            imageDrawable = "coffee_cappuccino"
        ),

        CoffeeModel(
            id = 3,
            name = "Latte",
            description = "Espresso with steamed milk and a light foam layer.",
            price = 5000.0,
            types = listOf("coffee", "hot"),
            imageResId = R.drawable.coffee_latte,
            imageDrawable = "coffee_latte"
        ),

        CoffeeModel(
            id = 4,
            name = "Mocha",
            description = "Espresso blended with chocolate syrup and steamed milk.",
            price = 5500.0,
            types = listOf("coffee", "hot"),
            imageResId = R.drawable.coffee_mocha,
            imageDrawable = "coffee_mocha"
        ),

        CoffeeModel(
            id = 5,
            name = "Cold Brew",
            description = "Coffee brewed slowly with cold water for smooth taste.",
            price = 4000.0,
            types = listOf("coffee", "iced"),
            imageResId = R.drawable.coffee_coldbrew,
            imageDrawable = "coffee_coldbrew"
        ),

        CoffeeModel(
            id = 6,
            name = "Americano",
            description = "Espresso diluted with hot water for a smooth bold cup.",
            price = 3500.0,
            types = listOf("coffee", "hot"),
            imageResId = R.drawable.coffee_espresso,
            imageDrawable = "coffee_espresso"
        ),

        CoffeeModel(
            id = 7,
            name = "Flat White",
            description = "Double espresso with velvety microfoam milk.",
            price = 4500.0,
            types = listOf("coffee", "hot"),
            imageResId = R.drawable.coffee_latte,
            imageDrawable = "coffee_latte"
        ),

        CoffeeModel(
            id = 8,
            name = "Macchiato",
            description = "Espresso topped with a small dollop of milk foam.",
            price = 4000.0,
            types = listOf("coffee", "hot"),
            imageResId = R.drawable.coffee_cappuccino,
            imageDrawable = "coffee_cappuccino"
        ),

        CoffeeModel(
            id = 9,
            name = "Affogato",
            description = "Vanilla ice cream topped with a hot espresso shot.",
            price = 6000.0,
            types = listOf("coffee", "hot"),
            imageResId = R.drawable.coffee_mocha,
            imageDrawable = "coffee_mocha"
        ),

        CoffeeModel(
            id = 10,
            name = "Iced Americano",
            description = "Chilled espresso served with water over ice.",
            price = 4000.0,
            types = listOf("coffee", "iced"),
            imageResId = R.drawable.coffee_coldbrew,
            imageDrawable = "coffee_coldbrew"
        ),

        CoffeeModel(
            id = 11,
            name = "Caramel Macchiato",
            description = "Vanilla syrup, steamed milk, espresso and caramel drizzle.",
            price = 5500.0,
            types = listOf("coffee", "hot"),
            imageResId = R.drawable.caramel_macchiato,
            imageDrawable = "caramel_macchiato"
        ),

        CoffeeModel(
            id = 12,
            name = "Iced Latte",
            description = "Espresso with chilled milk served over ice.",
            price = 5000.0,
            types = listOf("coffee", "iced"),
            imageResId = R.drawable.iced_latte,
            imageDrawable = "iced_latte"
        ),

        CoffeeModel(
            id = 13,
            name = "Nitro Cold Brew",
            description = "Cold brew infused with nitrogen for creamy texture.",
            price = 5500.0,
            types = listOf("coffee", "iced"),
            imageResId = R.drawable.nitro_cold_brew,
            imageDrawable = "nitro_cold_brew"
        ),

        CoffeeModel(
            id = 14,
            name = "Cortado",
            description = "Equal parts espresso and warm milk.",
            price = 4000.0,
            types = listOf("coffee", "hot"),
            imageResId = R.drawable.cortado,
            imageDrawable = "cortado"
        ),

        CoffeeModel(
            id = 15,
            name = "Ristretto",
            description = "A shorter, more concentrated espresso shot.",
            price = 3000.0,
            types = listOf("coffee", "hot"),
            imageResId = R.drawable.ristretto,
            imageDrawable = "ristretto"
        ),

        CoffeeModel(
            id = 16,
            name = "Lungo",
            description = "An espresso pulled longer for a milder flavor.",
            price = 3500.0,
            types = listOf("coffee", "hot"),
            imageResId = R.drawable.lungo,
            imageDrawable = "lungo"
        ),

        CoffeeModel(
            id = 17,
            name = "Doppio",
            description = "A double shot of espresso.",
            price = 4000.0,
            types = listOf("coffee", "hot"),
            imageResId = R.drawable.doppio,
            imageDrawable = "doppio"
        ),

        CoffeeModel(
            id = 18,
            name = "Iced Mocha",
            description = "Espresso, chocolate syrup, milk and ice blended together.",
            price = 5500.0,
            types = listOf("coffee", "iced"),
            imageResId = R.drawable.iced_mocha,
            imageDrawable = "iced_mocha"
        ),

        CoffeeModel(
            id = 19,
            name = "Matcha Latte",
            description = "Finely ground green tea powder mixed with steamed milk.",
            price = 5000.0,
            types = listOf("non_coffee", "hot"),
            imageResId = R.drawable.matcha_latte,
            imageDrawable = "matcha_latte"
        ),

        CoffeeModel(
            id = 20,
            name = "Iced Matcha Latte",
            description = "Sweetened matcha green tea with cold milk and ice.",
            price = 5500.0,
            types = listOf("non_coffee", "iced"),
            imageResId = R.drawable.iced_matcha_latte,
            imageDrawable = "iced_matcha_latte"
        ),

        CoffeeModel(
            id = 21,
            name = "Lemonade",
            description = "Classic refreshing lemonade served over ice.",
            price = 3500.0,
            types = listOf("non_coffee", "iced"),
            imageResId = R.drawable.lemonade,
            imageDrawable = "lemonade"
        ),

        CoffeeModel(
            id = 22,
            name = "Strawberry Lemonade",
            description = "Sweet lemonade blended with strawberries.",
            price = 4000.0,
            types = listOf("non_coffee", "iced"),
            imageResId = R.drawable.strawberry_lemonade,
            imageDrawable = "strawberry_lemonade"
        ),

        CoffeeModel(
            id = 23,
            name = "Peach Iced Tea",
            description = "Chilled black tea infused with peach flavor.",
            price = 3500.0,
            types = listOf("non_coffee", "iced"),
            imageResId = R.drawable.peach_iced_tea,
            imageDrawable = "peach_iced_tea"
        ),

        CoffeeModel(
            id = 24,
            name = "Passionfruit Iced Tea",
            description = "Refreshing iced tea with tropical passionfruit.",
            price = 4000.0,
            types = listOf("non_coffee", "iced"),
            imageResId = R.drawable.passionfruit_iced_tea,
            imageDrawable = "passionfruit_iced_tea"
        ),

        CoffeeModel(
            id = 25,
            name = "Berry Hibiscus Refresher",
            description = "Hibiscus tea mixed with sweet berry flavors.",
            price = 4500.0,
            types = listOf("non_coffee", "iced"),
            imageResId = R.drawable.berry_hibiscus_refresher,
            imageDrawable = "berry_hibiscus_refresher"
        ),

        CoffeeModel(
            id = 26,
            name = "Italian Cream Soda",
            description = "Sparkling soda mixed with flavored syrup and cream.",
            price = 4500.0,
            types = listOf("non_coffee", "iced"),
            imageResId = R.drawable.italian_cream_soda,
            imageDrawable = "italian_cream_soda"
        ),

        CoffeeModel(
            id = 27,
            name = "Mango Smoothie",
            description = "Blended mango with milk and ice for tropical sweetness.",
            price = 5500.0,
            types = listOf("non_coffee", "iced"),
            imageResId = R.drawable.mango_smoothie,
            imageDrawable = "mango_smoothie"
        ),

        CoffeeModel(
            id = 28,
            name = "Strawberry Smoothie",
            description = "Creamy blended strawberries and milk.",
            price = 5000.0,
            types = listOf("non_coffee", "iced"),
            imageResId = R.drawable.strawberry_smoothie,
            imageDrawable = "strawberry_smoothie"
        ),

        CoffeeModel(
            id = 29,
            name = "Watermelon Smoothie",
            description = "Refreshing blended watermelon drink.",
            price = 5000.0,
            types = listOf("non_coffee", "iced"),
            imageResId = R.drawable.watermelon_smoothie,
            imageDrawable = "watermelon_smoothie"
        ),

        CoffeeModel(
            id = 30,
            name = "Tiramisu",
            description = "Classic Italian dessert made with coffee-soaked ladyfingers, mascarpone cream, and cocoa powder.",
            price = 6500.0,
            types = listOf("eats"),
            imageResId = R.drawable.tiramisu,
            imageDrawable = "tiramisu"
        ),

        CoffeeModel(
        id = 31,
        name = "Red Velvet Cake",
        description = "Soft red velvet sponge layered with rich cream cheese frosting.",
        price = 6000.0,
        types = listOf("eats"),
        imageResId = R.drawable.red_velvet_cake,
        imageDrawable = "red_velvet_cake"
        ),

        CoffeeModel(
        id = 32,
        name = "Chocolate Cake",
        description = "Moist chocolate sponge cake topped with smooth chocolate ganache.",
        price = 6000.0,
        types = listOf("eats"),
        imageResId = R.drawable.chocolate_cake,
        imageDrawable = "chocolate_cake"
        ),

        CoffeeModel(
        id = 33,
        name = "French Fries",
        description = "Crispy golden potato fries served hot.",
        price = 3500.0,
        types = listOf("eats"),
        imageResId = R.drawable.french_fries,
        imageDrawable = "french_fries"
        ),

        CoffeeModel(
        id = 34,
        name = "Cheese Sandwich",
        description = "Toasted sandwich filled with melted cheese and buttered bread.",
        price = 4500.0,
        types = listOf("eats"),
        imageResId = R.drawable.cheese_sandwich,
        imageDrawable = "cheese_sandwich"
        ),)


}