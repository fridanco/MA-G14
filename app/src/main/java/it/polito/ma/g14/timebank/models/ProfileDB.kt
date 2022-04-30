package it.polito.ma.g14.timebank.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Profile::class, Skill::class], version = 1)
abstract class ProfileDB : RoomDatabase() {
    abstract fun profileDAO() : ProfileDAO

    companion object {
        @Volatile
        private var INSTANCE: ProfileDB? = null
        fun getDatabase(context: Context): ProfileDB =
            (INSTANCE ?:
            synchronized(this) {
                val i = INSTANCE ?: Room.databaseBuilder(context.applicationContext, ProfileDB::class.java, "profile_database").build()
                INSTANCE = i
                INSTANCE
            })!!
    }
}