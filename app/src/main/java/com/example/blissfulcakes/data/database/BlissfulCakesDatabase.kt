package com.example.blissfulcakes.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.blissfulcakes.data.dao.*
import com.example.blissfulcakes.data.model.*
import com.example.blissfulcakes.data.util.DateConverter

@Database(
    entities = [
        User::class,
        Cake::class,
        CartItem::class,
        Order::class,
        OrderItem::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class BlissfulCakesDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun cakeDao(): CakeDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao

    companion object {
        @Volatile
        private var INSTANCE: BlissfulCakesDatabase? = null

        fun getDatabase(context: Context): BlissfulCakesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BlissfulCakesDatabase::class.java,
                    "blissful_cakes_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 