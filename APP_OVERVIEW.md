# CoffeeShop App Overview

## What this app does
CoffeeShop is a simple Android app with a menu, detail view, and cart flow:

1. **Main screen** shows a list of coffees.
2. Tapping a coffee opens a **detail screen**.
3. From details, users can add items to a global in-memory cart.
4. Users can open the **cart screen**, see grouped quantities + total, and checkout.

The current implementation is intentionally lightweight and uses hardcoded menu data and in-memory state (no database/network).

## App structure

### Data models
- `CoffeeModel`: menu item model (`id`, `name`, `description`, `price`, `imageResId`) and `Serializable` so it can be passed in an `Intent`.
- `CartItem`: wraps a `CoffeeModel` with mutable `quantity`.

### Data source
- `MockData.menuList`: static list of five coffees and drawable image references.

### Cart state
- `CartManager` is a singleton object storing `mutableListOf<CartItem>`.
- `addItem()` increments quantity when the same coffee already exists.
- `getCartItems()` returns the current list reference.
- `getTotalPrice()` multiplies each item price by quantity and sums.
- `clearCart()` empties the list.

### UI screens and navigation
- `MainActivity`
  - Sets up a vertical `RecyclerView` with `CoffeeAdapter` and `MockData.menuList`.
  - On item click, starts `DetailActivity` with `COFFEE_EXTRA`.
  - "View Cart" button opens `CartActivity`.

- `DetailActivity`
  - Reads `COFFEE_EXTRA` and renders image/name/price/description.
  - "Add to Cart" button adds the item to `CartManager` and shows a toast.

- `CartActivity`
  - Reads cart items from `CartManager`.
  - Displays them in `RecyclerView` with `CartAdapter`.
  - Computes total once in `onCreate` and renders it.
  - Checkout:
    - If empty cart: toast and return.
    - Else: success dialog with total, then clear cart + finish activity.

### RecyclerView adapters
- `CoffeeAdapter`: binds menu row image/name/price and invokes click callback.
- `CartAdapter`: binds cart rows with `name`, `quantity`, and row price (`price * qty`).

## Layout summary
- `activity_main.xml`: full-screen menu list + bottom "View Cart" button.
- `activity_detail.xml`: image, name, price, description, bottom "Add to Cart" button.
- `activity_cart.xml`: cart list with footer containing total and checkout button.
- `item_coffee.xml`/`item_cart.xml`: row layouts for menu/cart lists.

## Behavior notes / limitations
- Cart data is **in-memory only** (`CartManager` singleton), so process death loses cart.
- Total in `CartActivity` is calculated once in `onCreate`; if cart mutated while visible, UI won’t auto-refresh.
- `Serializable` works but `Parcelable` is typically preferred for Android performance.
- Strings/currency are mostly hardcoded in Kotlin/XML rather than fully externalized/localized.
- No persistence, backend, authentication, or payment integration.

## Suggested next improvements
1. Move all visible strings to `strings.xml` and use proper currency formatting with locale.
2. Replace `Serializable` with `Parcelable` for intent payloads.
3. Introduce ViewModel + state holder (`LiveData`/`StateFlow`) for reactive cart updates.
4. Persist cart via Room/DataStore.
5. Add unit tests for `CartManager` and UI tests for navigation/cart checkout flow.
