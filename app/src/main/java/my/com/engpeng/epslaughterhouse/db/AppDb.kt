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
            SlaughterMortality::class,
            TempSlaughterDetail::class,
            TempSlaughterMortality::class,
            TableLog::class],
        version = 1,
        exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    abstract fun companyDao(): CompanyDao
    abstract fun locationDao(): LocationDao
    abstract fun slaughterDao(): SlaughterDao
    abstract fun slaughterDetailDao(): SlaughterDetailDao
    abstract fun slaughterMortalityDao(): SlaughterMortalityDao
    abstract fun tempSlaughterDetailDao(): TempSlaughterDetailDao
    abstract fun tempSlaughterMortalityDao(): TempSlaughterMortalityDao
    abstract fun tableLogDao(): TableLogDao
}