package com.tugrulbo.foodrecipe.ui.recipes.bottomsheetfilter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.tugrulbo.foodrecipe.R
import com.tugrulbo.foodrecipe.databinding.RecipeFilterBottomSheetBinding
import com.tugrulbo.foodrecipe.ui.recipes.RecipeViewModel
import com.tugrulbo.foodrecipe.utils.Constants.DEFAULT_DIET_TYPE
import com.tugrulbo.foodrecipe.utils.Constants.DEFAULT_MEAL_TYPE
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class RecipeFilterBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var recipesViewModel: RecipeViewModel
    private lateinit var binding: RecipeFilterBottomSheetBinding
    private var mealTypeChip = DEFAULT_MEAL_TYPE
    private var mealTypeChipId = 0
    private var dietTypeChip = DEFAULT_DIET_TYPE
    private var dietTypeChipId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipesViewModel = ViewModelProvider(requireActivity()).get(RecipeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = RecipeFilterBottomSheetBinding.inflate(inflater)

        recipesViewModel.readMealAndDietType.asLiveData().observe(viewLifecycleOwner, {value->
            mealTypeChip = value.selectedMealType
            dietTypeChip = value.selectedDietType

            updateChip(value.selectedMealTypeId,binding.mealTypeChipGroup)
            updateChip(value.selectedDietTypeId,binding.dietTypeChipGroup)
        })

        binding.mealTypeChipGroup.setOnCheckedChangeListener { group, selectedChipId ->
            val chip = group.findViewById<Chip>(selectedChipId)
            val selectedMealType = chip.text.toString().lowercase(Locale.ROOT)
            mealTypeChip = selectedMealType
            mealTypeChipId = selectedChipId
        }

        binding.dietTypeChipGroup.setOnCheckedChangeListener { group, checkedId ->
            val chip = group.findViewById<Chip>(checkedId)
            val selectedMealType = chip.text.toString().lowercase(Locale.ROOT)
            dietTypeChip = selectedMealType
            dietTypeChipId = checkedId
        }

        binding.applyBtn.setOnClickListener {
            recipesViewModel.saveMealAndDietType(
                mealTypeChip,
                mealTypeChipId,
                dietTypeChip,
                dietTypeChipId
            )

            val action = RecipeFilterBottomSheetFragmentDirections.actionRecipeFilterBottomSheetFragmentToRecipeFragment(true)
            findNavController().navigate(action)
        }
        return  binding.root
    }

    private fun updateChip(chipId: Int, chipGroup: ChipGroup) {

        if(chipId != 0){
            try {
                chipGroup.findViewById<Chip>(chipId).isChecked = true
            }catch (e:Exception){
                Log.d("RecipesBottomSheet", "updateChip: ${e.localizedMessage.toString()} ")
            }
        }
    }

}