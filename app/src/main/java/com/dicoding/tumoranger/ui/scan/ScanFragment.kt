package com.dicoding.tumoranger.ui.scan

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dicoding.tumoranger.databinding.FragmentScanBinding

class ScanFragment : Fragment() {

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!
    private lateinit var imageView: ImageView
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var addImageButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val scanViewModel = ViewModelProvider(this).get(ScanViewModel::class.java)

        _binding = FragmentScanBinding.inflate(inflater, container, false)
        val root: View = binding.root

        imageView = binding.previewImageView
        addImageButton = binding.addImageButton
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

        addImageButton.setOnClickListener {
            openGallery()
        }

        binding.analyzeButton.setOnClickListener {
            analyzeImage()
        }

        return root
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun analyzeImage() {
        // Add code to analyze the image
    }

    private fun saveToHistory() {
        // Add code to save the image to history
    }

    private fun showToast(message: String) {
        // Add code to show toast message
    }

    private fun moveToResult() {
        // Add code to move to result fragment
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}