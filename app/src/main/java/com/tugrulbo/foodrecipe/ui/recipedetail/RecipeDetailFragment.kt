package com.tugrulbo.foodrecipe.ui.recipedetail

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.tugrulbo.foodrecipe.R
import com.tugrulbo.foodrecipe.data.local.entities.FavoritesEntity
import com.tugrulbo.foodrecipe.databinding.FragmentRecipeDetailBinding
import com.tugrulbo.foodrecipe.ui.recipedetail.adapter.IngredientsAdapter
import dagger.hilt.android.AndroidEntryPoint
import org.jsoup.Jsoup
import java.lang.Exception

@AndroidEntryPoint
class RecipeDetailFragment : Fragment() {

    private var _binding :FragmentRecipeDetailBinding? = null
    private val binding get()= _binding!!
    private lateinit var viewModel: RecipeDetailViewModel

    private val args by navArgs<RecipeDetailFragmentArgs>()
    private val ingredientsAdapter by lazy { IngredientsAdapter() }

    private var recipeSaved = false
    private var savedRecipeId = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRecipeDetailBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(requireActivity()).get(RecipeDetailViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeData()
        checkSavedRecipes()
        onClicks()
        setupIngredientsRecyclerview()
        args.result.extendedIngredients?.let {
            ingredientsAdapter.setData(it)
        }

    }

    private fun initializeData() {
        val data = args.result
        binding.ivMainImage.load(data.image)
        binding.tvTitle.text = data.title

        binding.tvSummary.text =
            Html.fromHtml(data.summary, Html.FROM_HTML_MODE_COMPACT)

        binding.tvLikes.text = data.aggregateLikes.toString()
        binding.tvTime.text = data.readyInMinutes.toString()


       if(data.vegan){
            binding.ivVegan.setColorFilter(ContextCompat.getColor(requireContext(),R.color.green))
            binding.tvVegan.setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
        }

        if (data.cheap){
            binding.ivCheap.setColorFilter(ContextCompat.getColor(requireContext(),R.color.green))
            binding.tvCheap.setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
        }

        if (data.dairyFree){
            binding.ivDairyFree.setColorFilter(ContextCompat.getColor(requireContext(),R.color.green))
            binding.tvDairyFree.setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
        }

        if (data.glutenFree){
            binding.ivGlutenFree.setColorFilter(ContextCompat.getColor(requireContext(),R.color.green))
            binding.tvGlutenFree.setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
        }
        if (data.vegetarian){
            binding.ivVegetarien.setColorFilter(ContextCompat.getColor(requireContext(),R.color.green))
            binding.tvVegetarien.setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
        }

        if (data.veryHealthy){
            binding.ivHealthy.setColorFilter(ContextCompat.getColor(requireContext(),R.color.green))
            binding.tvHealthy.setTextColor(ContextCompat.getColor(requireContext(),R.color.green))
        }
    }

    private fun setupIngredientsRecyclerview(){
        binding.rvIngredients.adapter = ingredientsAdapter
        binding.rvIngredients.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun onClicks(){
        binding.ivBack.setOnClickListener {
            navigateBack()
        }

        binding.tvIngredients.setOnClickListener {
            it.background = ContextCompat.getDrawable(requireContext(),R.drawable.custom_tabbar_item_clicked)
            binding.tvInstructions.setBackgroundResource(0)
        }

        binding.tvInstructions.setOnClickListener {
            it.background = ContextCompat.getDrawable(requireContext(),R.drawable.custom_tabbar_item_clicked)
            binding.tvIngredients.setBackgroundResource(0)
        }

        binding.ivFavourite.setOnClickListener {
           if(recipeSaved){
               removeFavoritesRecipe()
           }else{
               saveToFavorites()
           }
        }
    }

    private fun saveToFavorites(){
        val favoritesEntity = FavoritesEntity(
            0,
            args.result
        )
        viewModel.insertFavoriteRecipe(favoritesEntity)
        binding.ivFavourite.setColorFilter(ContextCompat.getColor(requireContext(),R.color.yellow))
        showSnackBar("Recipe saved.")
        recipeSaved = true
    }

    private fun checkSavedRecipes(){
        viewModel.readFavoriteRecipes.observe(viewLifecycleOwner,{favoritesEntity->
            try {
                for (savedRecipe in favoritesEntity){
                    if(savedRecipe.result.id == args.result.id){
                        binding.ivFavourite.setColorFilter(ContextCompat.getColor(requireContext(),R.color.yellow))
                        savedRecipeId = savedRecipe.id
                        recipeSaved = true
                    }
                }

            }catch (e:Exception){
                Log.d("RecipeDetailFragment", "checkSavedRecipes: ${e.localizedMessage} ")
            }
        })
    }

    private fun removeFavoritesRecipe(){
        val favoritesEntity = FavoritesEntity(
            savedRecipeId,
            args.result
        )
        viewModel.deleteFavoriteRecipe(favoritesEntity)
        binding.ivFavourite.setColorFilter(ContextCompat.getColor(requireContext(),R.color.black))
        showSnackBar("Removed from favorites.")
        recipeSaved = false

    }

    private fun showSnackBar(message:String){
        Snackbar.make(
            binding.detailsLayout,
            message,
            Snackbar.LENGTH_LONG
        ).setAction("Ok"){}
            .show()
    }

    private fun navigateBack(){
        findNavController().popBackStack()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}