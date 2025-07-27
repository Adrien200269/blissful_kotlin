package com.example.blissfulcakes.di

import android.content.Context
import com.example.blissfulcakes.data.database.BlissfulCakesDatabase
import com.example.blissfulcakes.data.repository.*
import com.example.blissfulcakes.ui.viewmodel.*

object DependencyProvider {
    private var database: BlissfulCakesDatabase? = null
    
    fun initialize(context: Context) {
        if (database == null) {
            database = BlissfulCakesDatabase.getDatabase(context)
        }
    }
    
    fun provideAuthViewModel(context: Context): AuthViewModel {
        initialize(context)
        val userDao = database!!.userDao()
        val userRepository = UserRepository(userDao)
        return AuthViewModel(userRepository)
    }
    
    fun provideCakeViewModel(context: Context): CakeViewModel {
        initialize(context)
        val cakeDao = database!!.cakeDao()
        val cakeRepository = CakeRepository(cakeDao)
        return CakeViewModel(cakeRepository)
    }
    
    fun provideCartViewModel(context: Context): CartViewModel {
        initialize(context)
        val cartDao = database!!.cartDao()
        val cakeDao = database!!.cakeDao()
        val cartRepository = CartRepository(cartDao, cakeDao)
        return CartViewModel(cartRepository)
    }
    
    fun provideOrderViewModel(context: Context): OrderViewModel {
        initialize(context)
        val orderDao = database!!.orderDao()
        val orderItemDao = database!!.orderItemDao()
        val orderRepository = OrderRepository(orderDao, orderItemDao)
        return OrderViewModel(orderRepository)
    }
} 