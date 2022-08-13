package com.tugrulbo.foodrecipe.ui.favoriterecipe

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tugrulbo.foodrecipe.data.Repository
import com.tugrulbo.foodrecipe.data.local.entities.FavoritesEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FavoriteRecipesViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
):AndroidViewModel(application){

    val readFavoriteRecipes: LiveData<List<FavoritesEntity>> = repository.localDataSource.readFavoriteRecipe().asLiveData()

    fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.localDataSource.insertFavoriteRecipes(favoritesEntity)
    }

    fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.localDataSource.deleteFavoriteRecipe(favoritesEntity)
    }

    fun deleteAllFavorites() = viewModelScope.launch(Dispatchers.IO) {
        repository.localDataSource.deleteAllFavorites()
    }

}