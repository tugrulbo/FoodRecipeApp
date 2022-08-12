package com.tugrulbo.foodrecipe.data.local

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val recipesDAO: RecipesDAO
) {


    fun readRecipes(): Flow<List<RecipesEntity>> {
        return recipesDAO.readRecipes()
    }

    suspend fun insertRecipes(recipesEntity: RecipesEntity){
        recipesDAO.insertRecipes(recipesEntity)
    }
}