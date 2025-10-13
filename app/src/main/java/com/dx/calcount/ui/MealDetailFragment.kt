package com.dx.calcount.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dx.calcount.databinding.FragmentMealDetailBinding
import com.dx.calcount.ui.adapter.FoodAdapter
import kotlinx.coroutines.launch
/**
 * screen that shows the details of a selected meal.
 * displays a list of food items under that meal and allows editing or deleting them.
 */
class MealDetailFragment : Fragment() {

    // view binding to access UI elements safely
    private var _binding: FragmentMealDetailBinding? = null
    private val binding get() = _binding!!

    // adapter that shows the list of foods inside the meal
    private lateinit var foodAdapter: FoodAdapter
    // shared ViewModel to handle meal and food data
    private val viewModel: MealViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // inflate layout for this fragment
        _binding = FragmentMealDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get meal ID from navigation arguments
        val mealId = arguments?.getInt("mealId") ?: 0

        // get meal ID passed from the previous screen
        foodAdapter = FoodAdapter(
            onEdit = { food ->
                // navigate to Add/Edit screen for this food item
                val action = MealDetailFragmentDirections.actionMealDetailFragmentToAddEditItemFragment(foodId = food.id, mealId = mealId)
                findNavController().navigate(action)
            },
            onDelete = { food ->
                // delete the selected food item
                viewModel.deleteItem(food)
            }
        )

        // set up RecyclerView with adapter and layout manager
        binding.rvItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = foodAdapter
        }

        // observe meal && its items
        lifecycleScope.launch {
            viewModel.mealWithItems(mealId).collect { (meal, items) ->
                // chk binding is still valid b4 accessing views
                if (_binding != null) {
                    // show/update toolbar title with meal name
                    binding.detailToolbar.title = meal.name
                    
                    // update adapter with food items
                    foodAdapter.submitList(items)
                    
                    // show/hide empty state if no items
                    binding.llDetailEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }

        // lets user to tap the empty state to add a new food item
        binding.llDetailEmpty.setOnClickListener {
            val action = MealDetailFragmentDirections.actionMealDetailFragmentToAddEditItemFragment(mealId = mealId)
            findNavController().navigate(action)
        }
    }

    // mem management -> binding when view destroyed 2 prevent mem leaks
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}