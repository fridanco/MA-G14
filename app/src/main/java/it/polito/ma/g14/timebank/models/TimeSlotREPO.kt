package it.polito.ma.g14.timebank.models

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.util.*

class TimeSlotREPO(application: Application) {
    private val timeSlotDAO = TimeSlotDB.getDatabase(application).timeSlotDAO()

    fun numTimeSlots() : LiveData<Int> = timeSlotDAO.countTimeSlots()

    fun timeSlots() : LiveData<List<TimeSlot>> = timeSlotDAO.getAllTimeSlots()

    fun getTimeSlotByID(id : Long) : LiveData<TimeSlot> = timeSlotDAO.getTimeSlotByID(id)

    fun insertTimeSlot(title:String, description: String, dateTime: String, duration: Int, location: String){
        val ts = TimeSlot().also {
            it.title = title
            it.description = description
            it.dateTime = dateTime
            it.duration = duration
            it.location = location
        }
        timeSlotDAO.insertTimeSlot(ts)
    }

    fun updateTimeSlot(id: Long, title:String, description: String, dateTime: String, duration: Int, location: String){
        val ts = TimeSlot().also {
            it.id = id
            it.title = title
            it.description = description
            it.dateTime = dateTime
            it.duration = duration
            it.location = location
        }
        timeSlotDAO.updateTimeSlot(ts)
    }

}