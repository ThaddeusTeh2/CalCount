package com.dx.calcount.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dx.calcount.R
import com.dx.calcount.data.model.FoodItem
import com.dx.calcount.databinding.FragmentMealDetailBinding
import com.dx.calcount.ui.adapter.FoodAdapter

class MealDetailFragment : Fragment() {

    private var _binding: FragmentMealDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var foodAdapter: FoodAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        foodAdapter = FoodAdapter { food ->
            val action = MealDetailFragmentDirections.actionMealDetailFragmentToAddEditItemFragment(food.id)
            findNavController().navigate(action)
        }

        binding.rvItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = foodAdapter
        }

        // placeholder items
        val demoFoods = listOf(
            FoodItem(1, 1, "eggs", 150),
            FoodItem(2, 1, "toast", 120),
            FoodItem(3, 1, "white monster energy", 5)
        )
        foodAdapter.submitList(demoFoods)

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