package com.dx.calcount.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.dx.calcount.data.model.Meal
import com.dx.calcount.databinding.FragmentAddEditMealBinding
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class AddEditMealFragment : Fragment() {

    private var _binding: FragmentAddEditMealBinding? = null
    private val binding get() = _binding!!

    private val args: AddEditMealFragmentArgs by navArgs()
    private val viewModel: MealViewModel by viewModels()
    private var selectedDateTime: LocalDateTime? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditMealBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mealId = args.mealId
        if (mealId != -1) {
            // Load existing meal data
            lifecycleScope.launch {
                viewModel.mealWithItems(mealId).collect { (meal, _) ->
                    binding.aeMealEdittextName.setText(meal.name)
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                    binding.aeMealEdittextTime.setText(meal.date.format(formatter))
                    selectedDateTime = meal.date
                }
            }
        }

        // Open date+time pickers when time field clicked
        binding.aeMealEdittextTime.setOnClickListener {
            showDateTimePicker()
        }

        binding.aeMealBtnSave.setOnClickListener {
            val name = binding.aeMealEdittextName.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Meal name required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val date = selectedDateTime ?: LocalDateTime.now()

            val meal = Meal(
                id = if (mealId != -1) mealId else 0,
                name = name,
                date = date,
                totalCalories = 0 // always start at 0, updated from FoodItems later
            )

            // Save meal via ViewModel
            lifecycleScope.launch {
                if (mealId != -1) {
                    // Update existing meal
                    viewModel.updateMeal(meal)
                    Toast.makeText(requireContext(), "Meal updated", Toast.LENGTH_SHORT).show()
                } else {
                    // Create new meal
                    viewModel.createMeal(meal) { newMealId ->
                        Toast.makeText(requireContext(), "Meal created", Toast.LENGTH_SHORT).show()
                    }
                }
                findNavController().navigateUp()
            }
        }
    }

    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                TimePickerDialog(
                    requireContext(),
                    { _, hour, minute ->
                        selectedDateTime = LocalDateTime.of(year, month + 1, dayOfMonth, hour, minute)

                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                        binding.aeMealEdittextTime.setText(selectedDateTime!!.format(formatter))
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}