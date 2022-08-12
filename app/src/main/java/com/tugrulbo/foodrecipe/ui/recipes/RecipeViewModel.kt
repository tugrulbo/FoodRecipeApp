package com.tugrulbo.foodrecipe.ui.recipes

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import android.util.Log
import androidx.lifecycle.*
import com.tugrulbo.foodrecipe.data.DataStoreRepository
import com.tugrulbo.foodrecipe.data.Repository
import com.tugrulbo.foodrecipe.data.local.RecipesEntity
import com.tugrulbo.foodrecipe.data.model.FoodRecipe
import com.tugrulbo.foodrecipe.data.model.Result
import com.tugrulbo.foodrecipe.utils.ConnectivityObserver
import com.tugrulbo.foodrecipe.utils.Constants.API_KEY
import com.tugrulbo.foodrecipe.utils.Constants.DEFAULT_DIET_TYPE
import com.tugrulbo.foodrecipe.utils.Constants.DEFAULT_MEAL_TYPE
import com.tugrulbo.foodrecipe.utils.Constants.DEFAULT_RECIPES_NUMBER
import com.tugrulbo.foodrecipe.utils.Constants.QUERY_API_KEY
import com.tugrulbo.foodrecipe.utils.Constants.QUERY_DIET
import com.tugrulbo.foodrecipe.utils.Constants.QUERY_FILL_INGREDIENTS
import com.tugrulbo.foodrecipe.utils.Constants.QUERY_INFO
import com.tugrulbo.foodrecipe.utils.Constants.QUERY_NUMBER
import com.tugrulbo.foodrecipe.utils.Constants.QUERY_SEARCH
import com.tugrulbo.foodrecipe.utils.Constants.QUERY_TYPE
import com.tugrulbo.foodrecipe.utils.NetworkConnectivityObserver
import com.tugrulbo.foodrecipe.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val repository: Repository,
    private val dataStoreRepository: DataStoreRepository,
    application: Application
):AndroidViewModel(application) {

    private lateinit var connectivityObserver: ConnectivityObserver
    var connectionStatus = MutableLiveData<ConnectivityObserver.Status>()

    private var mealType = DEFAULT_MEAL_TYPE
    private var dietType = DEFAULT_DIET_TYPE

    var recipesResponse:MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    var searchedRecipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()

    val readRecipe: LiveData<List<RecipesEntity>> = repository.localDataSource.readRecipes().asLiveData()
    val readMealAndDietType = dataStoreRepository.readMealAndDietType

    fun saveMealAndDietType(mealType:String, mealTypeId:Int, dietType:String,dietTypeId:Int){
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveMealAndDietType(mealType,mealTypeId,dietType,dietTypeId)
        }

    }

    private fun insertRecipes(recipesEntity: RecipesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.localDataSource.insertRecipes(recipesEntity)
        }


    fun getRecipes(queryMap: Map<String,String>) = viewModelScope.launch {
        getRecipesSafeCall(queryMap)
    }

    fun searchRecipes(searchQuery:Map<String,String>) = viewModelScope.launch {
        searchRecipesSafeCall(searchQuery)
    }

    private suspend fun searchRecipesSafeCall(searchQuery: Map<String, String>) {
        recipesResponse.value = NetworkResult.Loading()

        if(hasInternetConnection()){
            try {

                val response = repository.remoteDataSource.searchRecipes(searchQuery)
                searchedRecipesResponse.value = handleFoodRecipeResponse(response)
            }catch (e:Exception){
                searchedRecipesResponse.value = NetworkResult.Error("Recipes not found.")
                Log.e("MainViewmodel", "getRecipesSafeCall: ${e.localizedMessage} ", )
            }
        }else{
            searchedRecipesResponse.value = NetworkResult.Error("No internet connection")
        }
    }

    private suspend fun getRecipesSafeCall(queryMap: Map<String, String>){
        recipesResponse.value = NetworkResult.Loading()
        if(hasInternetConnection()){
            try {
                val response = repository.remoteDataSource.getRecipes(queryMap)
                recipesResponse.value = handleFoodRecipeResponse(response)

                val foodRecipe = recipesResponse.value!!.data
                if(foodRecipe != null){
                    offlineCacheRecipes(foodRecipe)
                }
            }catch (e:Exception){
                recipesResponse.value = NetworkResult.Error("Recipes not found.")
                Log.e("MainViewmodel", "getRecipesSafeCall: ${e.localizedMessage} ", )
            }
        }else{
            recipesResponse.value = NetworkResult.Error("No internet connection")
        }
    }

    private fun offlineCacheRecipes(foodRecipe: FoodRecipe) {
        val recipesEntity = RecipesEntity(foodRecipe)
        insertRecipes(recipesEntity)

    }

    private fun handleFoodRecipeResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe>? {
        when{
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("API key limited")
            }
            response.body()!!.results.isNullOrEmpty() -> {
                return NetworkResult.Error("Recipes not found.")
            }
            response.isSuccessful -> {
                val foodRecipe = response.body()
                return NetworkResult.Success(foodRecipe!!)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }

    private fun hasInternetConnection():Boolean{
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when{
            capabilities.hasTransport(TRANSPORT_WIFI) -> true
            capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
            else-> false

        }
    }


    fun applyQueries():HashMap<String,String>{
        val queries :HashMap<String,String> = HashMap()

        viewModelScope.launch(Dispatchers.IO) {
            readMealAndDietType.collect{ value->
                mealType = value.selectedMealType
                dietType = value.selectedDietType
            }
        }

        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_TYPE] = mealType
        queries[QUERY_DIET] = dietType
        queries[QUERY_INFO] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"

        return queries
    }

    fun applySearchQuery(searchQuery:String):HashMap<String,String>{
        val queries : HashMap<String,String> = HashMap()
        queries[QUERY_SEARCH] = searchQuery
        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_INFO] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"

        return queries
    }

    fun checkInternetConnection(context:Context){
        connectivityObserver = NetworkConnectivityObserver(context)
        viewModelScope.launch {
            connectivityObserver.observe().collect{status->
                Log.d("RecipesFragment", "checkInternetConnection: $status ")
                connectionStatus.value = status
            }
        }
    }

}