package com.dx.calcount.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dx.calcount.data.model.Meal
import com.dx.calcount.databinding.FragmentHomeBinding
import com.dx.calcount.ui.adapter.MealAdapter
import java.time.LocalDateTime

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var mealAdapter: MealAdapter

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

        // temp fake data until DB is wired
        val demoMeals = listOf(
            Meal(1, "Breakfast", LocalDateTime.now(), 500),
            Meal(2, "Lunch", LocalDateTime.now(), 750),
            Meal(3, "Dinner", LocalDateTime.now(), 600),
        )
        mealAdapter.submitList(demoMeals)

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