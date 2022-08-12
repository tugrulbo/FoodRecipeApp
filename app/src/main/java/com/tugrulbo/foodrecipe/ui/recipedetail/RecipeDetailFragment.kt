package com.tugrulbo.foodrecipe.ui.recipedetail

import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.tugrulbo.foodrecipe.R
import com.tugrulbo.foodrecipe.databinding.FragmentRecipeDetailBinding
import com.tugrulbo.foodrecipe.ui.recipedetail.adapter.IngredientsAdapter
import dagger.hilt.android.AndroidEntryPoint
import org.jsoup.Jsoup

@AndroidEntryPoint
class RecipeDetailFragment : Fragment() {

    private var _binding :FragmentRecipeDetailBinding? = null
    private val binding get()= _binding!!

    private val args by navArgs<RecipeDetailFragmentArgs>()

    private val ingredientsAdapter by lazy { IngredientsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRecipeDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeData()
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
    }

    private fun navigateBack(){
        findNavController().popBackStack()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}