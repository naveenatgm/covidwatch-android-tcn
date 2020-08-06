package org.covidwatch.android.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import org.covidwatch.android.data.signedreport.SignedReport
import org.covidwatch.android.data.signedreport.SignedReportDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

//TODO: Remove ContactEvent when migration to TCN is complete
@Database(
    entities = [TemporaryContactNumber::class, SignedReport::class, Interaction::class],
    version = 2,
    exportSchema = false
)

@TypeConverters(DateConverter::class)
abstract class CovidWatchDatabase : RoomDatabase() {

    abstract fun temporaryContactNumberDAO(): TemporaryContactNumberDAO
    abstract fun signedReportDAO(): SignedReportDAO
    abstract fun interactionDAO(): InteractionDAO

    companion object {
        private const val NUMBER_OF_THREADS = 4
        val databaseWriteExecutor: ExecutorService =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS)

        @Volatile
        private var INSTANCE: CovidWatchDatabase? = null

        fun getInstance(context: Context): CovidWatchDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }


        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                CovidWatchDatabase::class.java, "covidwatch.db"
            ).fallbackToDestructiveMigration().build()

    }

}