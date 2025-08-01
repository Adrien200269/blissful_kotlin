package com.adrien.blissfulcake.di

import android.content.Context
import com.adrien.blissfulcake.data.repository.*
import com.adrien.blissfulcake.ui.viewmodel.*

object DependencyProvider {
    fun provideAuthViewModel(context: Context): AuthViewModel {
        val userRepository = UserRepository()
        return AuthViewModel(userRepository)
    }

    fun provideCakeViewModel(context: Context): CakeViewModel {
        val cakeRepository = CakeRepository.getInstance()
        return CakeViewModel(cakeRepository)
    }

    fun provideCartViewModel(context: Context): CartViewModel {
        val cartRepository = CartRepository()
        return CartViewModel(cartRepository)
    }

    fun provideFavoritesViewModel(context: Context): FavoritesViewModel {
        val favoritesRepository = FavoritesRepository()
        return FavoritesViewModel(favoritesRepository)
    }

    fun provideOrderViewModel(context: Context): OrderViewModel {
        val orderRepository = OrderRepository()
        return OrderViewModel(orderRepository)
    }
} 