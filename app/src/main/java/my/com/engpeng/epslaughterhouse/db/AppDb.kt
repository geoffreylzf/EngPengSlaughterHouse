package my.com.engpeng.epslaughterhouse.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import my.com.engpeng.epslaughterhouse.model.*

@Database(
        entities = [
            Company::class,
            Location::class,
            Slaughter::class,
            SlaughterDetail::class,
            TempSlaughterDetail::class,
            TempSlaughterMortality::class,
            TableLog::class],
        version = 1,
        exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    companion object {
        var INSTANCE: AppDb? = null
        fun getInstance(context: Context): AppDb {
            if (INSTANCE == null) {
                synchronized(AppDb::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, AppDb::class.java, "engpeng.db").build()
                }
            }
            return INSTANCE!!
        }
    }


    abstract fun companyDao(): CompanyDao
    abstract fun locationDao(): LocationDao
    abstract fun tempSlaughterDetailDao(): TempSlaughterDetailDao
    abstract fun tempSlaughterMortalityDao(): TempSlaughterMortalityDao
    abstract fun tableLogDao(): TableLogDao
}