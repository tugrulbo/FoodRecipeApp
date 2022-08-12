package com.tugrulbo.foodrecipe.data.remote

import com.tugrulbo.foodrecipe.data.model.FoodRecipe
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface FoodRecipeApi {

    @GET("/recipes/complexSearch")
    suspend fun getRecipes(
        @QueryMap queries:Map<String,String>
    ):Response<FoodRecipe>


    @GET("/recipes/complexSearch")
    suspend fun searchRecipes(
        @QueryMap queries: Map<String, String>
    ):Response<FoodRecipe>
}