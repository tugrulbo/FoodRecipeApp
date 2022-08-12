package com.tugrulbo.foodrecipe.data.remote

import com.tugrulbo.foodrecipe.data.model.FoodRecipe
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val foodRecipeApi: FoodRecipeApi
) {

    fun getRecipes(queries: Map<String,String>):Response<FoodRecipe>{
        return foodRecipeApi.getRecipes(queries)
    }
}