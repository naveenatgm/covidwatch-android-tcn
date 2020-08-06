package org.covidwatch.android.data

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface InteractionDAO {
    @get:Query("SELECT * FROM interactions")
    val all: List<Interaction>

    @Query("SELECT * FROM interactions ORDER BY last_seen DESC")
    fun allIntractionsSortedByDescTimestamp(): LiveData<List<Interaction>>

    @Query("SELECT * FROM interactions WHERE is_notified = 1")
    fun getAllNotifiedInteractions(): LiveData<List<Interaction>>

    @Query("SELECT * FROM interactions WHERE is_ended = 0 AND is_notified = 1 ORDER BY distance_in_feet ASC Limit 1")
    fun getNearbyInteractions(): LiveData<List<Interaction>>

    @get:Query("SELECT * FROM interactions ORDER BY interaction_start DESC")
    val pagedAllSortedByDescTimestamp: DataSource.Factory<Int, Interaction>

    @Query("UPDATE interactions SET is_ended = 1, interaction_end = :currentDate WHERE last_seen < :lastSeen AND is_ended = 0")
    fun closeOldInteraction(lastSeen: Date, currentDate: Date)

    /*
    @Query("SELECT * FROM interactions WHERE device_id = :device_id")
    fun findByID(device_id: String): List<Interaction>

    @Query("SELECT * FROM interactions WHERE device_id = :device_id AND interaction_ended = 0 ORDER BY last_seen DESC Limit 1")
    fun findLastOpenInteractionsByID(device_id: String): List<Interaction>
    */

    @Query("SELECT * FROM temporary_contact_numbers WHERE bytes = :bytes LIMIT 1")
    fun findByPrimaryKey(bytes: ByteArray): TemporaryContactNumber?

   @Query("SELECT * FROM interactions WHERE bytes = :bytes")
   fun findByID(bytes: ByteArray): List<Interaction>

   @Query("SELECT * FROM interactions WHERE bytes = :bytes AND is_ended = 0 ORDER BY last_seen DESC Limit 1")
   fun findLastOpenInteractionsByID(bytes: ByteArray): List<Interaction>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(interaction: Interaction)

    @Update
    fun update(interaction: Interaction)

    @Query("DELETE FROM interactions")
    fun deleteAll()

    @Query("DELETE FROM interactions WHERE last_seen < :lastSeen")
    fun deleteOldInteractions(lastSeen: Date)
}