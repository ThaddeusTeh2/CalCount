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

class MealDetailFragment : Fragment() {

    private var _binding: FragmentMealDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var foodAdapter: FoodAdapter
    private val viewModel: MealViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get meal ID from navigation arguments
        val mealId = arguments?.getInt("mealId") ?: 0

        foodAdapter = FoodAdapter { food ->
            val action = MealDetailFragmentDirections.actionMealDetailFragmentToAddEditItemFragment(food.id)
            findNavController().navigate(action)
        }

        binding.rvItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = foodAdapter
        }

        // Observe meal with items
        lifecycleScope.launch {
            viewModel.mealWithItems(mealId).collect { (meal, items) ->
                // Update toolbar title with meal name
                binding.detailToolbar.title = meal.name
                
                // Update adapter with food items
                foodAdapter.submitList(items)
                
                // Show/hide empty state
                binding.llDetailEmpty.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        binding.fabAdd.setOnClickListener {
            val action = MealDetailFragmentDirections.actionMealDetailFragmentToAddEditItemFragment()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}