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
import java.time.LocalDate

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var dayMealAdapter: DayMealAdapter
    private val viewModel: MealViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // init adapter with on-card actions
        dayMealAdapter = DayMealAdapter(
            onOpen = { meal ->
                val action = HomeFragmentDirections.actionHomeFragmentToMealDetailFragment(meal.id)
                findNavController().navigate(action)
            },
            onAddItem = { meal ->
                val action = HomeFragmentDirections.actionHomeFragmentToAddEditItemFragment(mealId = meal.id)
                findNavController().navigate(action)
            },
            onEdit = { meal ->
                val action = HomeFragmentDirections.actionHomeFragmentToAddEditMealFragment(meal.id)
                findNavController().navigate(action)
            },
            onDelete = { meal ->
                viewModel.deleteMeal(meal)
            }
        )

        binding.rvMeals.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dayMealAdapter
        }

        // Observe meals for multiple days
        lifecycleScope.launch {
            viewModel.mealsForMultipleDays().collect { dayMealItems ->
                dayMealAdapter.submitList(dayMealItems)
                // Show/hide empty state - check if binding is still valid
                if (_binding != null) {
                    val hasAnyMeals = dayMealItems.any { it is DayMealItem.MealItem }
                    binding.llEmpty.visibility = if (hasAnyMeals) View.GONE else View.VISIBLE
                }
            }
        }



        // Always-visible top Add Meal button
        binding.btnAddMealTop.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToAddEditMealFragment()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}