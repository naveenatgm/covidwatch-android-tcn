package org.covidwatch.android.domain

import androidx.lifecycle.LiveData
import org.covidwatch.android.data.Interaction
import org.covidwatch.android.data.InteractionDAO

class InteractionRepository(private val interactDao: InteractionDAO) {

    val allInteractions: LiveData<List<Interaction>> = interactDao.allIntractionsSortedByDescTimestamp()
    val allNotifiedInteractions: LiveData<List<Interaction>> = interactDao.getAllNotifiedInteractions()
    val nearbyInteractions: LiveData<List<Interaction>> = interactDao.getNearbyInteractions()
 //   val nearbyInteractionsByTime: LiveData<List<Interaction>> = interactDao.getNearbyInteractionsByTime()
    //val countbyInteractionsByTime: Int = interactDao.getCountByTime()

    suspend fun insert(intr: Interaction) {
        interactDao.insert(intr)
    }
}