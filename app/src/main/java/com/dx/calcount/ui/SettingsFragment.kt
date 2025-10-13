package com.dx.calcount.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.dx.calcount.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // inflate
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.maintenanceCalories.observe(viewLifecycleOwner) { value ->
            // null safety
            if (_binding != null) {
                binding.settingTftiEdittext.setText(value.toString())
            }
        }

        // listener for saving
        binding.settingBtnSave.setOnClickListener {
            val text = binding.settingTftiEdittext.text?.toString()?.trim().orEmpty()
            val value = text.toIntOrNull()
            // null safety & value validation
            if (value == null || value <= 0) {
                Toast.makeText(requireContext(),
                    getString(com.dx.calcount.R.string.error_invalid_calories),
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.setMaintenanceCalories(value)
            Toast.makeText(requireContext(), getString(com.dx.calcount.R.string.toast_saved), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}