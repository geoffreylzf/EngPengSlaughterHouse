package my.com.engpeng.epslaughterhouse.di

import androidx.work.*
import my.com.engpeng.epslaughterhouse.worker.UploadWorker

const val ID = "DATA_ID"
const val TABLE_NAME = "DATA_TABLE_NAME"

class WorkManagerModule {

    private val instance = WorkManager.getInstance()
    private val requiredNetwork = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

    fun enqueueUpload(tableName: String, id: Long) {

        val data = workDataOf(TABLE_NAME to tableName, ID to id)

        instance.enqueue(OneTimeWorkRequestBuilder<UploadWorker>()
                .setInputData(data)
                .setConstraints(requiredNetwork)
                .build())
    }

}