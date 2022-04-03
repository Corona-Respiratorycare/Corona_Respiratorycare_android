package com.covidproject.covid_respiratorycare.data

import androidx.room.*
import com.covidproject.covid_respiratorycare.ui.Service.mapping.HospitalInfo

@Dao
interface HospitalInfodb_Dao {
    @Insert
    fun insert(hospitalInfodb: HospitalInfo)

    @Update
    fun update(hospitalInfodb: HospitalInfo)

    @Delete
    fun delete(hospitalInfodb: HospitalInfo)

    @Query("SELECT * FROM HospitalTable") // 테이블의 모든 값을 가져와라
    fun getallHospital(): List<HospitalInfo>

    @Query("DELETE FROM HospitalTable")
    fun deleteAllHospital()
}