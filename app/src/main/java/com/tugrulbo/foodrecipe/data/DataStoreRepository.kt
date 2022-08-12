package com.tugrulbo.foodrecipe.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.tugrulbo.foodrecipe.utils.Constants.DEFAULT_DIET_TYPE
import com.tugrulbo.foodrecipe.utils.Constants.DEFAULT_MEAL_TYPE
import com.tugrulbo.foodrecipe.utils.Constants.PREFERENCES_DIET_TYPE
import com.tugrulbo.foodrecipe.utils.Constants.PREFERENCES_DIET_TYPE_ID
import com.tugrulbo.foodrecipe.utils.Constants.PREFERENCES_MEAL_TYPE
import com.tugrulbo.foodrecipe.utils.Constants.PREFERENCES_MEAL_TYPE_ID
import com.tugrulbo.foodrecipe.utils.Constants.PREFERENCES_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

@ActivityRetainedScoped
class DataStoreRepository @Inject constructor(
    @ApplicationContext context: Context
) {

    private object PreferencesKeys{
        val selectedMealType = stringPreferencesKey(PREFERENCES_MEAL_TYPE)
        val selectedMealTypeId = intPreferencesKey(PREFERENCES_MEAL_TYPE_ID)
        val selectedDietType = stringPreferencesKey(PREFERENCES_DIET_TYPE)
        val selectedDietTypeId = intPreferencesKey(PREFERENCES_DIET_TYPE_ID)
    }

    private val Context.dataStore by preferencesDataStore(PREFERENCES_NAME)
    private val dataStore: DataStore<Preferences> = context.dataStore

    suspend fun saveMealAndDietType(mealType:String,mealTypeId:Int, dietType:String,dietTypeId:Int){
        dataStore.edit { preferences->
            preferences[PreferencesKeys.selectedDietType] = dietType
            preferences[PreferencesKeys.selectedDietTypeId] = dietTypeId
            preferences[PreferencesKeys.selectedMealType] = mealType
            preferences[PreferencesKeys.selectedMealTypeId] = mealTypeId
        }

    }

    val readMealAndDietType: Flow<MealAndDietType> = dataStore.data
        .catch { exception->
            if(exception is IOException){
                emit(emptyPreferences())
            }else{
                throw exception
            }
        }.map { preferences->
            val selectedMealType = preferences[PreferencesKeys.selectedMealType] ?: DEFAULT_MEAL_TYPE
            val selectedMealTypeId = preferences[PreferencesKeys.selectedMealTypeId] ?: 0
            val selectedDietType = preferences[PreferencesKeys.selectedDietType] ?: DEFAULT_DIET_TYPE
            val selectedDietTypeId = preferences[PreferencesKeys.selectedDietTypeId] ?: 0

            MealAndDietType(
                selectedMealType,
                selectedMealTypeId,
                selectedDietType,
                selectedDietTypeId
            )
        }

}

data class MealAndDietType(
    val selectedMealType:String,
    val selectedMealTypeId:Int,
    val selectedDietType:String,
    val selectedDietTypeId:Int
)