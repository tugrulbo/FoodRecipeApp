package com.tugrulbo.foodrecipe.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tugrulbo.foodrecipe.data.model.Result
import com.tugrulbo.foodrecipe.utils.Constants.FAVORITE_RECIPES_TABLE

@Entity(tableName = FAVORITE_RECIPES_TABLE)
class FavoritesEntity(
    @PrimaryKey(autoGenerate = true)
    var id:Int,
    var result:Result
)
