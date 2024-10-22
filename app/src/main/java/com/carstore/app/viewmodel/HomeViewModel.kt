package com.carstore.app.viewmodel


import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.carstore.app.db.CarsEntity
import com.carstore.app.models.Car
import com.carstore.app.repository.CarDatabaseRepository
import com.carstore.app.util.Constants.Companion.BRAND_STORAGE_NAME
import com.carstore.app.util.Constants.Companion.DESCRIPTION_STORAGE_NAME
import com.carstore.app.util.Constants.Companion.IMAGE_STORAGE_NAME
import com.carstore.app.util.Constants.Companion.IS_NEW
import com.carstore.app.util.Constants.Companion.LOCATION_STORAGE_NAME
import com.carstore.app.util.Constants.Companion.MODEL_STORAGE_NAME
import com.carstore.app.util.Constants.Companion.PRICE_STORAGE_NAME
import com.carstore.app.util.Constants.Companion.UID_OF_SHARING_USER
import com.carstore.app.util.Constants.Companion.YEAR_STORAGE_NAME
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor (private val repository : CarDatabaseRepository,application: Application) : AndroidViewModel(application) {

    private var _loadingProgressBar : MutableLiveData<Boolean> = MutableLiveData()
    val loadingProgressBar : LiveData<Boolean> = _loadingProgressBar

    private var _allCarPost: MutableLiveData<List<Car>> = MutableLiveData()
    val allCarPost: LiveData<List<Car>> = _allCarPost

    private var _postIdAndCarPostHashMap: MutableLiveData<HashMap<Car, String>> = MutableLiveData()
    val postIdAndCarPostHashMap: LiveData<HashMap<Car, String>> = _postIdAndCarPostHashMap

    fun showAllPost(firestore: FirebaseFirestore) {
        _loadingProgressBar.value = true
        val documentIDAndCarPostList = HashMap<Car, String>()
        val allCarPostList = mutableListOf<Car>()
        if (hasInternetConnection()) {
            try {
                viewModelScope.launch{
                    firestore.collection("posts").get().addOnSuccessListener {
                        for (documentSnapshot in it.documents) {
                            val documentId = documentSnapshot.id
                            val carPost =
                                documentSnapshot.data?.let { value -> convertMapToCarClass(value) }
                            if (carPost != null) {
                                _loadingProgressBar.value = false
                                documentIDAndCarPostList[carPost] = documentId
                                allCarPostList.add(carPost)
                            }
                        }
                        _postIdAndCarPostHashMap.value = documentIDAndCarPostList
                        _allCarPost.value = allCarPostList

                        viewModelScope.launch (Dispatchers.IO){
                            repository.insertData(CarsEntity(allCarPostList))
                        }
                    }
                }

            }catch (e: Exception) {
                Toast.makeText(getApplication(),e.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }else{
            viewModelScope.launch {
                    repository.getAllCar().collect{ carEntity ->
                        if (carEntity.car.isNotEmpty()){
                            _loadingProgressBar.value = false
                            _allCarPost.value = carEntity.car
                        }else {
                            Toast.makeText(getApplication(),"Database is empty",Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

    }


    private fun convertMapToCarClass(map: Map<String, Any>): Car {
        val uidOfSharingUser = map[UID_OF_SHARING_USER] as String
        val model = map[MODEL_STORAGE_NAME] as String
        val brand = map[BRAND_STORAGE_NAME] as String
        val description = map[DESCRIPTION_STORAGE_NAME] as String
        val location = map[LOCATION_STORAGE_NAME] as String
        val new = map[IS_NEW] as Boolean
        val price = map[PRICE_STORAGE_NAME] as Long
        val year = map[YEAR_STORAGE_NAME] as Long
        val image = map[IMAGE_STORAGE_NAME] as List<String>

        return Car(uidOfSharingUser,
            model,
            price.toInt(),
            year.toInt(),
            brand,
            location,
            description,
            image,
            new
        )
    }


    private fun hasInternetConnection() : Boolean  {
        val connectivityManager = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?:return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ->true
            else ->false
        }
    }
}