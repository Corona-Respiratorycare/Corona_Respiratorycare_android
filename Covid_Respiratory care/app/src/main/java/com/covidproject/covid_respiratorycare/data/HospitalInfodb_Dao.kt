package com.covidproject.covid_respiratorycare.data

import androidx.lifecycle.LiveData
import androidx.room.*
//import com.covidproject.covid_respiratorycare.ui.Service.mapping.HospitalInfo

@Dao
interface HospitalInfodb_Dao {
    @Insert
    fun insert(hospitalInfodb: ResultX)

    @Update
    fun update(hospitalInfodb: ResultX)

    @Delete
    fun delete(hospitalInfodb: ResultX)

    @Query("SELECT * FROM HospitalTable") // 테이블의 모든 값을 가져와라
    fun getallHospital(): LiveData<List<ResultX>>

    @Query("DELETE FROM HospitalTable")
    fun deleteAllHospital()
}