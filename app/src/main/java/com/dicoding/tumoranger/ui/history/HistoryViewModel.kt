import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.tumoranger.api.RetrofitClient
import com.dicoding.tumoranger.api.response.DiagnosisHistoryItem
import com.dicoding.tumoranger.api.response.DiagnosisHistoryResponse
import com.dicoding.tumoranger.data.UserPreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryViewModel(private val userPreference: UserPreference) : ViewModel() {

    private val _historyList = MutableLiveData<List<DiagnosisHistoryItem>>()
    val historyList: LiveData<List<DiagnosisHistoryItem>> get() = _historyList

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun fetchDiagnosisHistory() {
        viewModelScope.launch {
            val token = userPreference.getUser().first().token
            if (token.isEmpty()) {
                _errorMessage.value = "Authentication token not found"
                return@launch
            }

            val apiService = RetrofitClient.getApiService(token)
            apiService.getDiagnosisHistory("Bearer $token").enqueue(object : Callback<DiagnosisHistoryResponse> {
                override fun onResponse(call: Call<DiagnosisHistoryResponse>, response: Response<DiagnosisHistoryResponse>) {
                    if (response.isSuccessful) {
                        _historyList.value = response.body()?.data ?: emptyList()
                    } else {
                        _errorMessage.value = "Failed to retrieve history: ${response.message()}"
                    }
                }

                override fun onFailure(call: Call<DiagnosisHistoryResponse>, t: Throwable) {
                    _errorMessage.value = "Failed to retrieve history: ${t.message}"
                }
            })
        }
    }
}