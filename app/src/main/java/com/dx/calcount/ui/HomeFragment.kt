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
import com.dx.calcount.ui.adapter.MealAdapter
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var mealAdapter: MealAdapter
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

        // init adapter
        mealAdapter = MealAdapter { meal ->
            // navigate to meal detail on click
            val action = HomeFragmentDirections.actionHomeFragmentToMealDetailFragment(meal.id)
            findNavController().navigate(action)
        }

        binding.rvMeals.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mealAdapter
        }

        // Observe meals for today
        lifecycleScope.launch {
            viewModel.mealsFor(LocalDate.now()).collect { meals ->
                mealAdapter.submitList(meals)
                // Show/hide empty state - check if binding is still valid
                if (_binding != null) {
                    binding.llEmpty.visibility = if (meals.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }

        // bind FAB to on click listener -> add/edit meal
        binding.fabAdd.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToAddEditMealFragment()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}