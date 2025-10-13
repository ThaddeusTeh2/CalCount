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
import com.dx.calcount.R
import com.dx.calcount.data.model.FoodItem
import com.dx.calcount.databinding.FragmentAddEditItemBinding
import kotlinx.coroutines.launch

/**
 * this frag handles adding or editing a FoodItem entry.
 * -> handles both creation and modification depending on nav args.
 * linked to a parent Meal by mealId passed through navigation.
 */
class AddEditItemFragment : Fragment() {

    // view binding handle (nullable lifecycle safe)
    private var _binding: FragmentAddEditItemBinding? = null
    private val binding get() = _binding!!

    // Safe args for navigation (contains mealId and foodId)
    private val args: AddEditItemFragmentArgs by navArgs()

    // ViewModel for meal and item data ops
    private val viewModel: MealViewModel by viewModels()

    // will store which meal d food item belongs to
    private var mealId: Int = 0

    /**
     * inflate layout for this fragment using ViewBinding.
     * j returning the bound root view.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * after view is created?
     * handle validating inputs, and saving new/edited items.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val foodId = args.foodId
        mealId = args.mealId

        if (foodId != -1) {
            // load existing item and prefill form
            viewModel.fetchItemById(foodId) { item ->
                if (_binding == null) return@fetchItemById
                if (item != null) {
                    binding.aeItemTftiEdittextName.setText(item.name)
                    binding.aeItemTftiEdittextCals.setText(item.calories.toString())
                } else {
                    Toast.makeText(requireContext(), getString(R.string.error_failed_load_item), Toast.LENGTH_SHORT).show()
                }
            }
        }

        /**
         * Main save button logic:
         * - Validates user input
         * - Creates or updates FoodItem depending on mode
         * - Persists to DB via ViewModel
         * - Shows toast and returns to previous screen
         */
        binding.aeItemBtnSave.setOnClickListener {
            val name = binding.aeItemTftiEdittextName.text.toString().trim()
            val caloriesText = binding.aeItemTftiEdittextCals.text.toString().trim()

            // validation chain — fail fast on empty or invalid input
            if (name.isEmpty()) {
                Toast.makeText(requireContext(), getString(com.dx.calcount.R.string.error_food_name_required), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (caloriesText.isEmpty()) {
                Toast.makeText(requireContext(), getString(com.dx.calcount.R.string.error_calories_required), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val calories = caloriesText.toIntOrNull()
            if (calories == null || calories < 0) {
                Toast.makeText(requireContext(), getString(com.dx.calcount.R.string.error_invalid_calories), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (mealId == -1) {
                Toast.makeText(requireContext(), getString(com.dx.calcount.R.string.error_invalid_meal_id), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // build model object 4 persistence
            val foodItem = FoodItem(
                id = if (foodId != -1) foodId else 0,
                mealOwnerId = mealId,
                name = name,
                calories = calories
            )

            /**
             * Async DB call through ViewModel.
             * handles insert && update flows.
             */
            lifecycleScope.launch {
                if (foodId != -1) {
                    // Update existing item
                    viewModel.updateItem(foodItem)
                    Toast.makeText(requireContext(), getString(com.dx.calcount.R.string.toast_item_updated), Toast.LENGTH_SHORT).show()
                } else {
                    // Create new item
                    viewModel.createItem(foodItem) { newItemId ->
                        Toast.makeText(requireContext(), getString(com.dx.calcount.R.string.toast_item_created), Toast.LENGTH_SHORT).show()
                    }
                }
                // rtn back to previous screen after saving
                findNavController().navigateUp()
            }
        }
    }

    /**
     * lifecycle cleanup — clear binding reference (avoid memory leaks)
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}