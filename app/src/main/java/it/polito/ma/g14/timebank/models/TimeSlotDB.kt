package it.polito.ma.g14.timebank.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TimeSlot::class], version = 1)
abstract class TimeSlotDB : RoomDatabase() {
    abstract fun timeSlotDAO() : TimeSlotDAO

    companion object {
        @Volatile
        private var INSTANCE: TimeSlotDB? = null
        fun getDatabase(context: Context): TimeSlotDB =
            (INSTANCE ?:
            synchronized(this) {
                val i = INSTANCE ?: Room.databaseBuilder(context.applicationContext, TimeSlotDB::class.java, "time_slots_database").build()
                INSTANCE = i
                INSTANCE
            })!!
    }
}