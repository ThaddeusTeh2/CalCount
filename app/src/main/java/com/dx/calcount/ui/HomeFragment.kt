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
import com.dx.calcount.databinding.FragmentHomeBinding
import com.dx.calcount.ui.adapter.DayMealAdapter
import com.dx.calcount.ui.adapter.DayMealItem
import kotlinx.coroutines.launch

/**
 * main boi showing all meals grouped by day
 * acts like central hub for adding, editing, and viewing meals and their items.
 */
class HomeFragment : Fragment() {

    // View binding for accessing layout elements
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Adapter that displays a list of days, each with its meals
    private lateinit var dayMealAdapter: DayMealAdapter

    // Shared ViewModel for all meal-related operations
    private val viewModel: MealViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // inflate layout 4 this screen
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // set up the main adapter for the RecyclerView
        // includes click actions for opening, editing, adding, or deleting a meal
        // init adapter with on-card actions
        dayMealAdapter = DayMealAdapter(
            onOpen = { meal ->
                // nav to the detailed view of this meal
                val action = HomeFragmentDirections.actionHomeFragmentToMealDetailFragment(meal.id)
                findNavController().navigate(action)
            },
            onAddItem = { meal ->
                // nav to Add/Edit Food Item screen for this meal
                val action = HomeFragmentDirections.actionHomeFragmentToAddEditItemFragment(mealId = meal.id)
                findNavController().navigate(action)
            },
            onEdit = { meal ->
                // nav to edit this meal’s info (e.g. name)
                val action = HomeFragmentDirections.actionHomeFragmentToAddEditMealFragment(meal.id)
                findNavController().navigate(action)
            },
            onDelete = { meal ->
                // del this meal and refresh the list
                viewModel.deleteMeal(meal)
            }
        )
        // implement adapter to RecyclerView && set layout
        binding.rvMeals.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dayMealAdapter
        }
        // observe meals for multiple days (past & future meals)
        lifecycleScope.launch {
            viewModel.mealsForMultipleDays().collect { dayMealItems ->
                // updoot list
                dayMealAdapter.submitList(dayMealItems)
                // show/hide empty state - check if binding is still valid
                if (_binding != null) {
                    val hasAnyMeals = dayMealItems.any { it is DayMealItem.MealItem }
                    binding.llEmpty.visibility = if (hasAnyMeals) View.GONE else View.VISIBLE
                }
            }
        }
        // top Add Meal button (should always b visible)
        binding.btnAddMealTop.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToAddEditMealFragment()
            findNavController().navigate(action)
        }
    }
    // clear binding when view is destroyed 2 prevent memory leaks
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}