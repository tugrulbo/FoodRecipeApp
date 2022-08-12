package com.tugrulbo.foodrecipe.ui.recipes

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.tugrulbo.foodrecipe.R
import com.tugrulbo.foodrecipe.databinding.FragmentRecipeBinding
import com.tugrulbo.foodrecipe.ui.recipes.adapter.RecipeAdapter
import com.tugrulbo.foodrecipe.utils.ConnectivityObserver
import com.tugrulbo.foodrecipe.utils.NetworkResult
import com.tugrulbo.foodrecipe.utils.observeOnce
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipeFragment : Fragment() {

    private var _binding:FragmentRecipeBinding ?= null
    private val binding get() = _binding!!

    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var mAdapter :RecipeAdapter

    private val args by navArgs<RecipeFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRecipeBinding.inflate(inflater,container,false)
        recipeViewModel = ViewModelProvider(requireActivity()).get(RecipeViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecipeRecyclerView()
        readDatabase()
        onClicks()
        getSearchRecipes()
    }

    private fun onClicks(){
        binding.floatingButtonRecipesFilter.setOnClickListener {
            recipeViewModel.checkInternetConnection(requireContext())
            recipeViewModel.connectionStatus.observeOnce(viewLifecycleOwner,{status->
                if(status == ConnectivityObserver.Status.Available){
                    findNavController().navigate(R.id.action_recipeFragment_to_recipeFilterBottomSheetFragment)
                }else{
                    Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
                }
            })

        }
    }

    private fun setupRecipeRecyclerView(){
        mAdapter = RecipeAdapter{
            Toast.makeText(requireContext(), "${it.title}", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerview.adapter = mAdapter
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    private fun readDatabase() {
        lifecycleScope.launchWhenStarted {
            recipeViewModel.readRecipe.observeOnce(viewLifecycleOwner, Observer {database->
                if(database.isNotEmpty() && !args.backFromBottomSheet){
                    Log.d("RecipesFragment", "readDatabase called ")
                    mAdapter.setData(database[0].foodRecipe)
                    hideShimmerEffect()
                }else{
                    requestApi()
                }
            })
        }
    }

    private fun requestApi(){
        recipeViewModel.getRecipes(recipeViewModel.applyQueries())
        recipeViewModel.recipesResponse.observe(viewLifecycleOwner,{ response->
            when(response){
                is NetworkResult.Success->{
                    hideShimmerEffect()
                    response.data?.let {
                        mAdapter.setData(it)
                    }
                }
                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    loadDataFromCache()
                    Toast.makeText(requireContext(), response.message.toString(), Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.Loading->
                    showShimmerEffect()
            }
        })
    }

    private fun loadDataFromCache(){
        lifecycleScope.launch {
            recipeViewModel.readRecipe.observe(viewLifecycleOwner,{database->
                if(database.isNotEmpty()){
                    mAdapter.setData(database[0].foodRecipe)
                }
            })
        }
    }

    private fun getSearchRecipes(){
        binding.etSearch.addTextChangedListener { text->
            if(text.isNullOrEmpty().not()){
                searchRecipe(text.toString())
            }else if (text.toString().isEmpty()){
                binding.etSearch.clearFocus()
            }
        }

    }

    private fun searchRecipe(searchQuery:String){
        showShimmerEffect()
        recipeViewModel.searchRecipes(recipeViewModel.applySearchQuery(searchQuery))
        recipeViewModel.searchedRecipesResponse.observe(viewLifecycleOwner,{ response->
            when(response){
                is NetworkResult.Success->{
                    hideShimmerEffect()
                    response.data?.let {
                        mAdapter.setData(it)
                    }
                }
                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    loadDataFromCache()
                    Toast.makeText(requireContext(), response.message.toString(), Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.Loading->
                    showShimmerEffect()
            }
        })
    }


    private fun showShimmerEffect(){
        binding.recyclerview.showShimmer()
    }

    private fun hideShimmerEffect(){
        binding.recyclerview.hideShimmer()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}