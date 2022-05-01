package it.polito.ma.g14.timebank.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import it.polito.ma.g14.timebank.models.TimeSlotREPO
import kotlin.concurrent.thread

class TimeSlotVM(application: Application) : AndroidViewModel(application) {

    val repo = TimeSlotREPO(application)

    val timeSlots = repo.timeSlots()

    fun getTimeSlot(id: Long) = repo.getTimeSlotByID(id)

    fun addTimeSlot(title:String, description: String, date: String, from: String, to: String, location: String) {
        thread {
            repo.insertTimeSlot(title, description, date, from, to, location)
        }
    }

    fun editTimeSlot(id: Long, title:String, description: String, date: String, from: String, to: String, location: String) {
        thread {
            repo.updateTimeSlot(id, title, description, date, from, to, location)
        }
    }

    fun deleteTimeSlot(id: Long) {
        thread {
            repo.deleteTimeSlotByID(id)
        }
    }

}