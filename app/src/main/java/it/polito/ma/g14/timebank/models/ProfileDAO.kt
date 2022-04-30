package it.polito.ma.g14.timebank.models

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ProfileDAO {

    @Query("SELECT * FROM profile")
    fun getProfile() : LiveData<Profile>

    @Query("SELECT COUNT(*) FROM profile")
    fun isProfileCreated() : LiveData<Int>

    @Insert
    fun createProfile(profile: Profile)

    @Update
    fun updateProfile(profile: Profile)

    @Query("SELECT * FROM skills")
    fun getSkills() : LiveData<List<Skill>>

    @Insert
    fun addSkill(skill: Skill)

    @Delete
    fun removeSkill(skill: Skill)
}
