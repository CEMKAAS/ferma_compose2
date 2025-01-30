package com.zaroslikov.fermacompose2.ui.add


import android.net.Uri
import android.app.Application
import android.content.ContentResolver
import androidx.core.content.ContextCompat.startActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zaroslikov.fermacompose2.data.ItemsRepository
import com.zaroslikov.fermacompose2.data.ferma.ProjectTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class ProjectAddViewModel(val itemsRepository: ItemsRepository) : ViewModel() {

   private var countProject by mutableIntStateOf(0)

    fun countProject(): Int {
        viewModelScope.launch {
            countProject = itemsRepository.getCountRowProject()
                .filterNotNull()
                .first()
                .toInt()
        }
        return countProject
    }


    suspend fun insertTable(item: ProjectTable) {
        itemsRepository.insertProject(item)
    }


    val selectedImageIndex: MutableState<Int?> = mutableStateOf(null)

    val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            saveImageToDatabase(uri)
        }
    }

    fun selectImage(index: Int?) {
        selectedImageIndex.value = index
    }

    fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun saveImageToDatabase(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val byteArray = getBytesFromUri(uri)
            val imageEntity = ImageEntity(imageData = byteArray)
            itemsRepository.insert(imageEntity)
        }
    }

    private suspend fun getBytesFromUri(uri: Uri): ByteArray? = withContext(Dispatchers.IO) {
        val contentResolver: ContentResolver = getApplication<Application>().contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        return@withContext inputStream?.use {
            ByteArrayOutputStream().apply {
                it.copyTo(this)
            }.toByteArray()
        }
    }

}