package my.com.engpeng.epslaughterhouse.db

import androidx.room.Database
import androidx.room.RoomDatabase
import my.com.engpeng.epslaughterhouse.model.*

const val DATABASE_NAME = "engpeng_sh.db"

@Database(
        entities = [
            Company::class,
            Location::class,
            Doc::class,
            ShReceive::class,
            ShReceiveDetail::class,
            ShReceiveMortality::class,
            TempShReceiveDetail::class,
            TempShReceiveMortality::class,
            TempShHangMortality::class,
            ShHang::class,
            ShHangMortality::class,
            TableLog::class,
            Log::class],
        version = 1,
        exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    abstract fun companyDao(): CompanyDao
    abstract fun locationDao(): LocationDao
    abstract fun docDao(): DocDao
    abstract fun shHangDao(): ShHangDao
    abstract fun shHangMortalityDao(): ShHangMortalityDao
    abstract fun shReceiveDao(): ShReceiveDao
    abstract fun shReceiveDetailDao(): ShReceiveDetailDao
    abstract fun shReceiveMortalityDao(): ShReceiveMortalityDao
    abstract fun tempShReceiveDetailDao(): TempShReceiveDetailDao
    abstract fun tempShReceiveMortalityDao(): TempShReceiveMortalityDao
    abstract fun tempShHangMortalityDao(): TempShHangMortalityDao
    abstract fun tableLogDao(): TableLogDao
    abstract fun logDao(): LogDao
    abstract fun utilDao(): UtilDao
}