package org.covidwatch.android.domain

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import org.covidwatch.android.data.CovidWatchDatabase
import org.covidwatch.android.data.Interaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InteractionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InteractionRepository
    val allInteractions: LiveData<List<Interaction>>
    val allNotifiedInteractions: LiveData<List<Interaction>>

    init {
        val intrDao = CovidWatchDatabase.getInstance(application).interactionDAO()
        repository = InteractionRepository(intrDao)
        allInteractions = repository.allInteractions
        allNotifiedInteractions = repository.allNotifiedInteractions
    }

    fun insert(word: Interaction) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(word)
    }
}