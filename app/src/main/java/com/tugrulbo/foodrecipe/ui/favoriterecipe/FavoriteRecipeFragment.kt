package com.tugrulbo.foodrecipe.ui.favoriterecipe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.tugrulbo.foodrecipe.R
import com.tugrulbo.foodrecipe.databinding.FragmentFavoriteRecipeBinding
import com.tugrulbo.foodrecipe.databinding.FragmentRecipeBinding
import com.tugrulbo.foodrecipe.ui.favoriterecipe.adapter.FavoriteRecipesAdapter
import com.tugrulbo.foodrecipe.ui.recipes.RecipeFragmentDirections
import com.tugrulbo.foodrecipe.ui.recipes.adapter.RecipeAdapter
import com.tugrulbo.foodrecipe.utils.showVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteRecipeFragment : Fragment() {

    private var _binding: FragmentFavoriteRecipeBinding?= null
    private val binding get() = _binding!!

    private lateinit var viewModel:FavoriteRecipesViewModel

    private lateinit var favoriteAdapter:FavoriteRecipesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFavoriteRecipeBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(requireActivity()).get(FavoriteRecipesViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecipeRecyclerView()
    }

    private fun setupRecipeRecyclerView(){
        showShimmerEffect()
        viewModel.readFavoriteRecipes.observe(viewLifecycleOwner,{favoriteEntities->
            if(favoriteEntities.isEmpty()){
                binding.rvFavorites.showVisibility(false)
                binding.ivNoData.showVisibility(true)
                binding.tvNoData.showVisibility(true)
            }else{
                favoriteAdapter.setData(favoriteEntities)
                binding.rvFavorites.showVisibility(true)
                binding.ivNoData.showVisibility(false)
                binding.tvNoData.showVisibility(false)
            }


        })
        favoriteAdapter = FavoriteRecipesAdapter{
            val action = FavoriteRecipeFragmentDirections.actionFavoriteRecipeFragmentToRecipeDetailFragment(it)
            findNavController().navigate(action)

        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val recipe = favoriteAdapter.favoriteRecipesList[position]
                viewModel.deleteFavoriteRecipe(recipe)
                view?.let {
                    Snackbar.make(it,"Deleted",Snackbar.LENGTH_LONG).apply {
                        setAction("Undo"){
                            viewModel.insertFavoriteRecipe(recipe)
                        }
                        show()
                    }
                }
            }

        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvFavorites)
        }
        binding.rvFavorites.adapter = favoriteAdapter
        binding.rvFavorites.layoutManager = LinearLayoutManager(requireContext())
        hideShimmerEffect()
    }

    private fun showShimmerEffect(){
        binding.rvFavorites.showShimmer()
    }

    private fun hideShimmerEffect(){
        binding.rvFavorites.hideShimmer()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }





}