package com.dicoding.tumoranger.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dicoding.tumoranger.R
import com.dicoding.tumoranger.databinding.FragmentSettingsBinding
import com.dicoding.tumoranger.ui.login.LoginActivity
import com.dicoding.tumoranger.data.UserPreference
import com.dicoding.tumoranger.data.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var userPreference: UserPreference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this)[SettingsViewModel::class.java]

        // Inflate the layout using View Binding
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize UserPreference
        userPreference = UserPreference.getInstance(requireContext().dataStore)

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
        binding.radioGroupLanguage.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.radioEnglish.id -> {
                    changeLanguage("en")
                }
                binding.radioIndonesian.id -> {
                    changeLanguage("id")
                }
            }
        }

        // Listen to appearance selection change
        binding.radioGroupAppearance.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.radioSystemDefault.id -> {
                          }
                binding.radioLight.id -> {
                    saveTheme("light")
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                binding.radioDark.id -> {
                    saveTheme("dark")
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
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

    private fun changeLanguage(languageCode: String) {
        Log.d("SettingsFragment", "Changing language to: $languageCode")

        // Simpan bahasa yang dipilih di preferences
        CoroutineScope(Dispatchers.IO).launch {
            try {
                userPreference.saveLanguage(languageCode)
                Log.d("SettingsFragment", "Language saved successfully")
            } catch (e: Exception) {
                Log.e("SettingsFragment", "Error saving language: ${e.localizedMessage}")
            }
        }

        // Tentukan locale berdasarkan kode bahasa yang dipilih
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        // Set konfigurasi baru untuk locale
        val config = resources.configuration
        config.setLocale(locale)

        // Terapkan perubahan konfigurasi menggunakan createConfigurationContext
        val context = requireContext().createConfigurationContext(config)

        // Memperbarui resource strings untuk bahasa baru
        val resources = context.resources

        // Restart aplikasi dengan konteks baru untuk menerapkan perubahan bahasa
        val intent = Intent(context, activity?.javaClass)
        startActivity(intent)
        activity?.finish()

        // Tampilkan toast untuk konfirmasi perubahan bahasa
        Toast.makeText(requireContext(), "Language changed to $languageCode", Toast.LENGTH_SHORT).show()
    }

    private fun saveTheme(theme: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Simpan tema ke preferences
                userPreference.saveTheme(theme)

                // Terapkan tema langsung tanpa restart activity
                when (theme) {
                    "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }

            } catch (e: Exception) {
                Log.e("SettingsFragment", "Error saving theme: ${e.localizedMessage}")
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
