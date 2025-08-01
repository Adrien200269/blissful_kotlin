package com.adrien.blissfulcake.di

import android.content.Context
import com.adrien.blissfulcake.data.repository.*
import com.adrien.blissfulcake.ui.viewmodel.*

object DependencyProvider {
    private var authViewModel: AuthViewModel? = null
    private var cakeViewModel: CakeViewModel? = null
    private var cartViewModel: CartViewModel? = null
    private var favoritesViewModel: FavoritesViewModel? = null
    private var orderViewModel: OrderViewModel? = null

    fun provideAuthViewModel(context: Context): AuthViewModel {
        return authViewModel ?: AuthViewModel(UserRepository()).also { authViewModel = it }
    }

    fun provideCakeViewModel(context: Context): CakeViewModel {
        return cakeViewModel ?: CakeViewModel(CakeRepository.getInstance()).also { cakeViewModel = it }
    }

    fun provideCartViewModel(context: Context): CartViewModel {
        return cartViewModel ?: CartViewModel(CartRepository()).also { cartViewModel = it }
    }

    fun provideFavoritesViewModel(context: Context): FavoritesViewModel {
        return if (favoritesViewModel != null) {
            println("DEBUG: DependencyProvider - Reusing existing FavoritesViewModel")
            favoritesViewModel!!
        } else {
            println("DEBUG: DependencyProvider - Creating new FavoritesViewModel")
            FavoritesViewModel(FavoritesRepository()).also { favoritesViewModel = it }
        }
    }

    fun provideOrderViewModel(context: Context): OrderViewModel {
        return if (orderViewModel != null) {
            println("DEBUG: DependencyProvider - Reusing existing OrderViewModel")
            orderViewModel!!
        } else {
            println("DEBUG: DependencyProvider - Creating new OrderViewModel")
            OrderViewModel(OrderRepository()).also { orderViewModel = it }
        }
    }
} 