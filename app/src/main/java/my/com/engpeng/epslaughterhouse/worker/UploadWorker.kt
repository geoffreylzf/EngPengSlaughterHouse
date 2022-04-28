package my.com.engpeng.epslaughterhouse.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import my.com.engpeng.epslaughterhouse.db.AppDb
import my.com.engpeng.epslaughterhouse.di.ApiModule
import my.com.engpeng.epslaughterhouse.di.ID
import my.com.engpeng.epslaughterhouse.di.SharedPreferencesModule
import my.com.engpeng.epslaughterhouse.di.TABLE_NAME
import my.com.engpeng.epslaughterhouse.model.ShHang
import my.com.engpeng.epslaughterhouse.model.ShReceive
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

            val receList = mutableListOf<ShReceive>()
            val hangList = mutableListOf<ShHang>()

            when (tableName) {
                ShReceive.TABLE_NAME -> {
                    val rece = appDb.shReceiveDao().getById(id)

                    if (rece.isUpload == 1) {
                        return Result.success()
                    }

                    rece.shReceiveDetailList = appDb.shReceiveDetailDao().getAllByShReceiveId(rece.id!!)
                    rece.shReceiveMortalityList = appDb.shReceiveMortalityDao().getAllByShReceiveId(rece.id!!)

                    receList.add(rece)
                }
                ShHang.TABLE_NAME -> {
                    val hang = appDb.shHangDao().getById(id)

                    if (hang.isUpload == 1) {
                        return Result.success()
                    }

                    hang.shHangMortalityList = appDb.shHangMortalityDao().getAllByShHangId(hang.id!!)
                    hangList.add(hang)
                }
            }

            apiModule.provideApiService(spm.getIsLocal())
                    .uploadAsync(UploadBody(receList, hangList))
                    .await()
                    .result
                    .updateStatus(context, appDb)

        } catch (e: Exception) {
            return Result.failure()
        }
        return Result.success()
    }
}