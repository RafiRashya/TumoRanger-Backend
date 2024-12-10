package com.dicoding.tumoranger.ui.scan

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.tumoranger.api.RetrofitClient
import com.dicoding.tumoranger.R
import com.dicoding.tumoranger.api.response.DiagnoseResponse
import com.dicoding.tumoranger.databinding.FragmentScanBinding
import com.dicoding.tumoranger.ui.result.ResultActivity
import com.dicoding.tumoranger.data.UserPreference
import com.dicoding.tumoranger.data.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
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
                    Log.d("ScanFragment", "Image selected: $it")
                }
            }
        }

        scanViewModel.imageUri.observe(viewLifecycleOwner, { uri ->
            imageView.setImageURI(uri)
            imageView.visibility = View.VISIBLE
            addImageButton.visibility = View.GONE
            Log.d("ScanFragment", "Image URI observed: $uri")
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
        Log.d("ScanFragment", "Gallery opened")
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            birthdateEditText.setText(selectedDate)
            Log.d("ScanFragment", "Date selected: $selectedDate")
        }, year, month, day)

        datePickerDialog.show()
        Log.d("ScanFragment", "DatePickerDialog shown")
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        Log.d("ScanFragment", "Toast shown: $message")
    }

    private fun moveToResult(imageUri: Uri, name: String, birthdate: String, gender: String, prediction: String, confidenceScore: Double) {
        val intent = Intent(context, ResultActivity::class.java)
        intent.putExtra("IMAGE_URI", imageUri.toString())
        intent.putExtra("NAME", name)
        intent.putExtra("BIRTHDATE", birthdate)
        intent.putExtra("GENDER", gender)
        intent.putExtra("PREDICTION", prediction)
        intent.putExtra("CONFIDENCE_SCORE", confidenceScore.toString())
        startActivity(intent)
        Log.d("ScanFragment", "Moving to ResultActivity with data: $name, $birthdate, $gender, $prediction, $confidenceScore")
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun bitmapToFile(bitmap: Bitmap, fileName: String): File {
        val file = File(requireContext().cacheDir, fileName)
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireContext().contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
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

        val filePath = getRealPathFromURI(imageUri)
        if (filePath == null) {
            showToast("Failed to get image file")
            return
        }

        val file = File(filePath)
        if (!file.exists()) {
            showToast("Failed to get image file")
            return
        }

        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val patientName = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val birthdateBody = birthdate.toRequestBody("text/plain".toMediaTypeOrNull())
        val genderBody = gender.toRequestBody("text/plain".toMediaTypeOrNull())

        lifecycleScope.launch {
            val token = UserPreference.getInstance(requireContext().dataStore).getUser().first().token
            if (token.isEmpty()) {
                showToast("Authentication token not found")
                return@launch
            }
            Log.d("ScanFragment", "Using token: $token")
            val apiService = RetrofitClient.getApiService(token)

            // Log the file details
            Log.d("ScanFragment", "File details: name=${file.name}, size=${file.length()}")
            Log.d("ScanFragment", "Form data: name=$name, birthdate=$birthdate, gender=$gender")

            val call = apiService.diagnose(body, patientName, birthdateBody, genderBody)
            Log.d("ScanFragment", "Analyzing image with data: $name, $birthdate, $gender")

            call.enqueue(object : Callback<DiagnoseResponse> {
                override fun onResponse(call: Call<DiagnoseResponse>, response: Response<DiagnoseResponse>) {
                    if (response.isSuccessful) {
                        val diagnosis = response.body()?.diagnosis
                        if (diagnosis != null) {
                            moveToResult(imageUri, name, birthdate, gender, diagnosis.prediction, diagnosis.confidenceScore)
                            Log.d("ScanFragment", "Diagnosis successful: ${diagnosis.prediction}, ${diagnosis.confidenceScore}")
                        } else {
                            showToast("Failed to get diagnosis")
                            Log.d("ScanFragment", "Failed to get diagnosis")
                        }
                    } else {
                        showToast("Failed to analyze image: ${response.message()}")
                        Log.d("ScanFragment", "Failed to analyze image: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<DiagnoseResponse>, t: Throwable) {
                    showToast("Failed to analyze image: ${t.message}")
                    Log.d("ScanFragment", "Failed to analyze image: ${t.message}")
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("ScanFragment", "View destroyed")
    }
}