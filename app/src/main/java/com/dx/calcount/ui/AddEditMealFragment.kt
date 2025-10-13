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

/**
 * fragment 4 creating/editing a meal.
 * handles both adding a new meal and updating an existing one. (im NOT extending frm a base fragment)
 */
class AddEditMealFragment : Fragment() {

    // view binding 4 this layout
    private var _binding: FragmentAddEditMealBinding? = null
    private val binding get() = _binding!!

    // navarg: tells us if we're editing an existing meal or creating a new one
    private val args: AddEditMealFragmentArgs by navArgs()

    // ViewModel handles all meal-related database operations
    private val viewModel: MealViewModel by viewModels()

    // stores selected date and time 4d meal (def val null <- care)
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
        // if meal ID passed, we editing an existing meal
        if (mealId != -1) {
            // load existing meal data
            lifecycleScope.launch {
                viewModel.mealWithItems(mealId).collect { (meal, _) ->
                    // check if binding is still valid before accessing views
                    if (_binding != null) {
                        // fill input fields with existing meal data
                        binding.aeMealEdittextName.setText(meal.name)
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                        binding.aeMealEdittextTime.setText(meal.date.format(formatter))
                        selectedDateTime = meal.date
                    }
                }
            }
        }

        // show date+time pickers when time field clicked
        binding.aeMealEdittextTime.setOnClickListener {
            showDateTimePicker()
        }

        // save lol
        binding.aeMealBtnSave.setOnClickListener {
            val name = binding.aeMealEdittextName.text.toString().trim()

            // validation -> name cannot be empty
            if (name.isEmpty()) {
                Toast.makeText(requireContext(), getString(com.dx.calcount.R.string.error_meal_name_required), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // use selected date, or set to current time if none chosen
            val date = selectedDateTime ?: LocalDateTime.now()

            // create Meal object — this gets passed to the ViewModel for saving
            val meal = Meal(
                id = if (mealId != -1) mealId else 0,
                name = name,
                date = date,
                totalCalories = 0 // always start at 0, FoodItems update this later
            )

            // save meal via ViewModel
            lifecycleScope.launch {
                // if meal exists
                if (mealId != -1) {
                    // update existing meal
                    viewModel.updateMeal(meal)
                    Toast.makeText(requireContext(), getString(com.dx.calcount.R.string.toast_meal_updated), Toast.LENGTH_SHORT).show()
                } else {
                    // create new meal
                    viewModel.createMeal(meal) { newMealId ->
                        Toast.makeText(requireContext(), getString(com.dx.calcount.R.string.toast_meal_created), Toast.LENGTH_SHORT).show()
                    }
                }
                findNavController().navigateUp()
            }
        }
    }

    /**
     * opens a date picker, then a time picker, and stores the selected date/time.
     * updates the text field to show what the user picked.
     * partial help from GPT
     */
    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()
        // date picker first
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                // after picking a date, show time picker
                TimePickerDialog(
                    requireContext(),
                    { _, hour, minute ->
                        // save combined date && time
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

    // mem management, prevents mem leak by killing binding
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}