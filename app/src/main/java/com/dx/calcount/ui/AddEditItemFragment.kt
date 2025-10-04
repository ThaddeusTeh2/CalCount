package com.dx.calcount.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dx.calcount.data.model.FoodItem
import com.dx.calcount.databinding.FragmentAddEditItemBinding
import kotlinx.coroutines.launch

class AddEditItemFragment : Fragment() {

    private var _binding: FragmentAddEditItemBinding? = null
    private val binding get() = _binding!!

    private val args: AddEditItemFragmentArgs by navArgs()
    private val viewModel: MealViewModel by viewModels()
    private var mealId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val foodId = args.foodId
        mealId = args.mealId

        if (foodId != -1) {
            // Load existing food item data
            lifecycleScope.launch {
                // For now, we'll need to get the meal ID from navigation arguments
                // This would need to be passed from the previous fragment
                // For editing, we'd need to load the specific food item
                // This is a simplified version - in a real app you'd have a getFoodItem method
            }
        }

        binding.aeItemBtnSave.setOnClickListener {
            val name = binding.aeItemTftiEdittextName.text.toString().trim()
            val caloriesText = binding.aeItemTftiEdittextCals.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Food name required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (caloriesText.isEmpty()) {
                Toast.makeText(requireContext(), "Calories required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val calories = caloriesText.toIntOrNull()
            if (calories == null || calories < 0) {
                Toast.makeText(requireContext(), "Invalid calories", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (mealId == -1) {
                Toast.makeText(requireContext(), "Invalid meal ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val foodItem = FoodItem(
                id = if (foodId != -1) foodId else 0,
                mealOwnerId = mealId,
                name = name,
                calories = calories
            )

            // Save food item via ViewModel
            lifecycleScope.launch {
                if (foodId != -1) {
                    // Update existing item
                    viewModel.updateItem(foodItem)
                    Toast.makeText(requireContext(), "Food item updated", Toast.LENGTH_SHORT).show()
                } else {
                    // Create new item
                    viewModel.createItem(foodItem) { newItemId ->
                        Toast.makeText(requireContext(), "Food item created", Toast.LENGTH_SHORT).show()
                    }
                }
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}