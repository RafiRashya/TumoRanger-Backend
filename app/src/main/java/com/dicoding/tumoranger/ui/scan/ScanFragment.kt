package com.dicoding.tumoranger.ui.scan

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dicoding.tumoranger.R
import com.dicoding.tumoranger.databinding.FragmentScanBinding
import com.dicoding.tumoranger.ui.result.ResultActivity
import java.util.Calendar

class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!
    private lateinit var imageView: ImageView
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var addImageButton: Button
    private lateinit var analyzeButton: Button
    private lateinit var nameEditText: EditText
    private lateinit var birthdateEditText: EditText
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var scanViewModel: ScanViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        scanViewModel = ViewModelProvider(this).get(ScanViewModel::class.java)

        _binding = FragmentScanBinding.inflate(inflater, container, false)
        val root: View = binding.root

        imageView = binding.previewImageView
        addImageButton = binding.addImageButton
        analyzeButton = binding.analyzeButton
        nameEditText = binding.nameEditText
        birthdateEditText = binding.birthDateEditText
        genderRadioGroup = binding.genderRadioGroup

        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImage: Uri? = result.data?.data
                selectedImage?.let {
                    scanViewModel.setImageUri(it)
                }
            }
        }

        scanViewModel.imageUri.observe(viewLifecycleOwner, { uri ->
            imageView.setImageURI(uri)
            imageView.visibility = View.VISIBLE
            addImageButton.visibility = View.GONE
        })

        birthdateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        addImageButton.setOnClickListener {
            openGallery()
        }

        analyzeButton.setOnClickListener {
            analyzeImage()
        }

        return root
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            birthdateEditText.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun moveToResult(imageUri: Uri, name: String, birthdate: String, gender: String) {
        val intent = Intent(context, ResultActivity::class.java)
        intent.putExtra("IMAGE_URI", imageUri.toString())
        intent.putExtra("NAME", name)
        intent.putExtra("BIRTHDATE", birthdate)
        intent.putExtra("GENDER", gender)
        startActivity(intent)
    }

    private fun analyzeImage() {
        val name = nameEditText.text.toString().trim()
        val birthdate = birthdateEditText.text.toString().trim()
        val selectedGenderId = genderRadioGroup.checkedRadioButtonId
        val imageUri = scanViewModel.imageUri.value

        if (name.isEmpty() || birthdate.isEmpty() || selectedGenderId == -1) {
            showToast("Please fill in all fields")
            return
        }

        val gender = when (selectedGenderId) {
            R.id.maleRadioButton -> "Male"
            R.id.femaleRadioButton -> "Female"
            else -> ""
        }

        if (imageUri == null) {
            showToast("Please select an image first")
            return
        }

        // Add code to analyze the image
        moveToResult(imageUri, name, birthdate, gender)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}