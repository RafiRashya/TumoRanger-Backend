package com.dicoding.tumoranger.ui.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.tumoranger.api.response.Diagnosis

class ResultViewModel : ViewModel() {
    private val _diagnosis = MutableLiveData<Diagnosis>()
    val diagnosis: LiveData<Diagnosis> get() = _diagnosis

    fun setDiagnosis(diagnosis: Diagnosis) {
        _diagnosis.value = diagnosis
    }
}