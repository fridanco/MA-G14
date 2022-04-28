package it.polito.ma.g14.timebank.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import it.polito.ma.g14.timebank.models.TimeSlotREPO
import java.util.*
import kotlin.concurrent.thread

class TimeSlotVM(application: Application) : AndroidViewModel(application) {

    val repo = TimeSlotREPO(application)

    val timeSlots = repo.timeSlots()

    val count = repo.numTimeSlots()

    fun getTimeSlot(id: Long) = repo.getTimeSlotByID(id)

    fun addTimeSlot(title:String, description: String, dateTime: String, duration: Int, location: String) {
        thread {
            repo.insertTimeSlot(title, description, dateTime, duration, location)
        }
    }

    fun editTimeSlot(id: Long, title:String, description: String, dateTime: String, duration: Int, location: String) {
        thread {
            repo.updateTimeSlot(id, title, description, dateTime, duration, location)
        }
    }

}