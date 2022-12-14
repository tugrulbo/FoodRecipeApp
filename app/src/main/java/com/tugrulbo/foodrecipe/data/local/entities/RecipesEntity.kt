package com.tugrulbo.foodrecipe.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tugrulbo.foodrecipe.data.model.FoodRecipe
import com.tugrulbo.foodrecipe.utils.Constants.RECIPES_TABLE


@Entity(tableName = RECIPES_TABLE)
class RecipesEntity(
    var foodRecipe: FoodRecipe
) {

    @PrimaryKey(autoGenerate = false)
    var id:Int = 0

}