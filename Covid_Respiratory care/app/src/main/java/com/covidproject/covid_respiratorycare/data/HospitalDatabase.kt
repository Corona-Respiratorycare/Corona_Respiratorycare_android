package com.covidproject.covid_respiratorycare.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.covidproject.covid_respiratorycare.ui.Service.mapping.HospitalInfo

@Database(entities = [HospitalInfo::class], version = 1)
abstract class HospitalDatabase : RoomDatabase() {
    abstract fun HospitalInfoDao() : HospitalInfodb_Dao

    companion object {
        private var instance: HospitalDatabase? = null
        @Synchronized
        fun getInstance(context: Context): HospitalDatabase? {
            if (instance == null) {
                synchronized(HospitalDatabase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,HospitalDatabase::class.java,"hospital-database"//다른 데이터 베이스랑 이름겹치면 꼬임
                    ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
                }//Please provide the necessary Migration path via RoomDatabase ->fallbackToDestructiveMigration
            }
            return instance
        }
    }
}