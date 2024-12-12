package com.dicoding.tumoranger.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.dicoding.tumoranger.R
import com.dicoding.tumoranger.databinding.FragmentSettingsBinding
import com.dicoding.tumoranger.ui.login.LoginActivity
import java.util.*

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: android.content.SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using View Binding
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

        // Handle Logout Button
        binding.buttonLogout.setOnClickListener {
            logout()
        }

        // Placeholder for Profile Image
        binding.profileImage.setImageResource(R.drawable.ic_profile_placeholder)

        // Set default values for the radio buttons
        setInitialTheme()
        setInitialLanguage()

        // Handle Manage Account Button
        binding.buttonManageAccount.setOnClickListener {
            // Handle account management logic here
        }

        // Listen to language selection change
        binding.radioGroupLanguage.setOnCheckedChangeListener { _, checkedId ->
            val newLanguage = when (checkedId) {
                binding.radioEnglish.id -> "en"
                binding.radioIndonesian.id -> "in"
                else -> return@setOnCheckedChangeListener
            }
            val currentLanguage = sharedPreferences.getString("selected_language", Locale.getDefault().language)
            if (newLanguage != currentLanguage) {
                saveLanguage(newLanguage)
                applyLanguage(newLanguage)
            }
        }


        // Listen to appearance selection change
        binding.radioGroupAppearance.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.radioSystemDefault.id -> saveTheme("system")
                binding.radioLight.id -> saveTheme("light")
                binding.radioDark.id -> saveTheme("dark")
            }
        }

        return root
    }

    private fun setInitialTheme() {
        val savedTheme = sharedPreferences.getString("selected_theme", "system")
        when (savedTheme) {
            "light" -> binding.radioGroupAppearance.check(binding.radioLight.id)
            "dark" -> binding.radioGroupAppearance.check(binding.radioDark.id)
            else -> binding.radioGroupAppearance.check(binding.radioSystemDefault.id)
        }
    }

    private fun saveTheme(theme: String) {
        sharedPreferences.edit().putString("selected_theme", theme).apply()
        when (theme) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        Toast.makeText(requireContext(), "Theme changed to $theme", Toast.LENGTH_SHORT).show()
    }

    private fun setInitialLanguage() {
        val savedLanguage = sharedPreferences.getString("selected_language", Locale.getDefault().language)
        Log.d("SettingsFragment", "Saved language in SharedPreferences: $savedLanguage")

        when (savedLanguage) {
            "en" -> binding.radioGroupLanguage.check(binding.radioEnglish.id)
            "in" -> binding.radioGroupLanguage.check(binding.radioIndonesian.id)
        }
    }

    private fun saveLanguage(languageCode: String) {
        // Simpan bahasa yang dipilih ke SharedPreferences
        sharedPreferences.edit()
            .putString("selected_language", languageCode)
            .apply()
    }

    private fun changeLanguage(languageCode: String) {
        Log.d("SettingsFragment", "Changing language to: $languageCode")
        sharedPreferences.edit().putString("selected_language", languageCode).apply()
        applyLanguage(languageCode)

        requireActivity().recreate()
        Toast.makeText(requireContext(), "Language changed to $languageCode", Toast.LENGTH_SHORT).show()
    }

    private fun applyLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        // Terapkan konfigurasi baru menggunakan createConfigurationContext
        requireActivity().apply {
            baseContext.createConfigurationContext(config)
        }
        Log.d("SettingsFragment", "Configuration locale after applying: ${config.locales[0]}")

        // Restart aktivitas untuk menerapkan perubahan bahasa
        val refreshIntent = Intent(requireContext(), requireActivity()::class.java)
        startActivity(refreshIntent)
        requireActivity().finish()

        // Tampilkan toast konfirmasi
        Toast.makeText(requireContext(), "Language changed to $languageCode", Toast.LENGTH_SHORT).show()
    }


    private fun logout() {
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
