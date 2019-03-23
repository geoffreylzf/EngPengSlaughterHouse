package my.com.engpeng.epslaughterhouse.db

import androidx.room.Database
import androidx.room.RoomDatabase
import my.com.engpeng.epslaughterhouse.model.*

@Database(
        entities = [
            Company::class,
            Location::class,
            Doc::class,
            Trip::class,
            TripDetail::class,
            TripMortality::class,
            TempTripDetail::class,
            TempTripMortality::class,
            TempOperationMortality::class,
            Operation::class,
            OperationMortality::class,
            TableLog::class,
            Log::class],
        version = 1,
        exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    abstract fun companyDao(): CompanyDao
    abstract fun locationDao(): LocationDao
    abstract fun docDao(): DocDao
    abstract fun operationDao(): OperationDao
    abstract fun operationMortalityDao(): OperationMortalityDao
    abstract fun tripDao(): TripDao
    abstract fun tripDetailDao(): TripDetailDao
    abstract fun tripMortalityDao(): TripMortalityDao
    abstract fun tempTripDetailDao(): TempTripDetailDao
    abstract fun tempTripMortalityDao(): TempTripMortalityDao
    abstract fun tempOperationMortalityDao(): TempOperationMortalityDao
    abstract fun tableLogDao(): TableLogDao
    abstract fun logDao(): LogDao
    abstract fun utilDao(): UtilDao
}