package my.com.engpeng.epslaughterhouse.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.di.ApiModule
import my.com.engpeng.epslaughterhouse.di.ID
import my.com.engpeng.epslaughterhouse.di.SharedPreferencesModule
import my.com.engpeng.epslaughterhouse.di.TABLE_NAME
import my.com.engpeng.epslaughterhouse.model.Operation
import my.com.engpeng.epslaughterhouse.model.Trip
import my.com.engpeng.epslaughterhouse.model.UploadBody
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class UploadWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params), KoinComponent {

    private val appDb: AppDb by inject()
    private val apiModule: ApiModule by inject()
    private val spm: SharedPreferencesModule by inject()
    private val context: Context by inject()

    override suspend fun doWork(): Result {
        try {
            val tableName = inputData.getString(TABLE_NAME)
            val id = inputData.getLong(ID, 0)

            val tripList = mutableListOf<Trip>()
            val operList = mutableListOf<Operation>()

            when (tableName) {
                Trip.TABLE_NAME -> {
                    val trip = appDb.tripDao().getById(id)

                    if (trip.isUpload == 1) {
                        return Result.success()
                    }

                    trip.tripDetailList = appDb.tripDetailDao().getAllByTripId(trip.id!!)
                    trip.tripMortalityList = appDb.tripMortalityDao().getAllByTripId(trip.id!!)

                    tripList.add(trip)
                }
                Operation.TABLE_NAME -> {
                    val oper = appDb.operationDao().getById(id)

                    if (oper.isUpload == 1) {
                        return Result.success()
                    }

                    oper.operationMortalityList = appDb.operationMortalityDao().getAllByOperationId(oper.id!!)
                    operList.add(oper)
                }
            }

            apiModule.provideApiService(spm.getIsLocal())
                    .uploadAsync(UploadBody(spm.getUniqueId(), tripList, operList))
                    .await()
                    .result
                    .updateStatus(context, appDb)

        } catch (e: Exception) {
            return Result.failure()
        }
        return Result.success()
    }
}