package com.dicoding.tumoranger.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dicoding.tumoranger.databinding.FragmentSettingsBinding
import com.dicoding.tumoranger.ui.login.LoginActivity
import com.dicoding.tumoranger.R
import com.google.android.material.radiobutton.MaterialRadioButton

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        // Inflate the layout using View Binding
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Handle Logout Button
        binding.buttonLogout.setOnClickListener {
            logout()
        }

        // Placeholder for Profile Image
        binding.profileImage.setImageResource(R.drawable.ic_profile_placeholder)

        // Set default values for the radio buttons
        binding.radioGroupLanguage.check(binding.radioEnglish.id) // Set English as default
        binding.radioGroupAppearance.check(binding.radioSystemDefault.id) // Set System Default as default

        // Handle Manage Account Button
        binding.buttonManageAccount.setOnClickListener {
            // Handle account management logic here
        }

        // Listen to language selection change
        binding.radioGroupLanguage.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                binding.radioEnglish.id -> {
                    // Handle English selection
                }
                binding.radioZimbabwe.id -> {
                    // Handle Zimbabwe selection
                }
            }
        }

        // Listen to appearance selection change
        binding.radioGroupAppearance.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                binding.radioSystemDefault.id -> {
                    // Handle System Default selection
                }
                binding.radioLight.id -> {
                    // Handle Light theme selection
                }
                binding.radioDark.id -> {
                    // Handle Dark theme selection
                }
            }
        }

        // Observe ViewModel (if needed)
        settingsViewModel.text.observe(viewLifecycleOwner) {
            // Handle observed changes, if required
        }

        return root
    }

    private fun logout() {
        // Clear user data and navigate to login
        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            remove("auth_token")
            putBoolean("is_logged_in", false)
            apply()
        }

        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
