package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RecipeEntity::class, ShoppingItemEntity::class, PantryItemEntity::class], version = 6, exportSchema = false)
abstract class ChefDatabase : RoomDatabase() {
    abstract fun chefDao(): ChefDao

    companion object {
        @Volatile
        private var INSTANCE: ChefDatabase? = null

        fun getDatabase(context: Context): ChefDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChefDatabase::class.java,
                    "chef_ai_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
