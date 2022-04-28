package it.polito.ma.g14.timebank.models

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TimeSlotDAO {

    @Query("SELECT * FROM time_slots")
    fun getAllTimeSlots() : LiveData<List<TimeSlot>>

    @Query("SELECT * FROM time_slots WHERE id=:timeSlotID")
    fun getTimeSlotByID(timeSlotID: Long) : LiveData<TimeSlot>

    @Query("SELECT COUNT(*) FROM time_slots")
    fun countTimeSlots() : LiveData<Int>

    @Insert
    fun insertTimeSlot(timeSlot: TimeSlot)

    @Query("DELETE FROM time_slots WHERE id=:timeSlotID")
    fun deleteTimeSlotByID(timeSlotID: Long)

    @Update
    fun updateTimeSlot(timeSlot: TimeSlot)
}
