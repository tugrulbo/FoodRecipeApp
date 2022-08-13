package com.tugrulbo.foodrecipe.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tugrulbo.foodrecipe.data.local.entities.FavoritesEntity
import com.tugrulbo.foodrecipe.data.local.entities.RecipesEntity


@Database(entities = [RecipesEntity::class, FavoritesEntity::class], version = 3, exportSchema = false)
@TypeConverters(RecipesTypeConverter::class)
abstract class RecipesDatabase: RoomDatabase() {

    abstract fun recipesDao():RecipesDAO
}