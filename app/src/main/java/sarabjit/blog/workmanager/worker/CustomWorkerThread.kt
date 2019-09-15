package sarabjit.blog.workmanager.worker

import android.content.Context
import android.support.annotation.NonNull
import androidx.work.Worker
import androidx.work.WorkerParameters

class CustomWorkerThread(@NonNull val context: Context, @NonNull workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    companion object {
        val DATA_PARAM = "ARG_DATA_PARAM"
        val DATA_VALUE = "This is Test Data"
    }

    override fun doWork(): Result {
        // Data is fetched here
        val inputValue = inputData.getString(DATA_PARAM)

        if (inputValue.equals(DATA_VALUE)) {
            println("Value Matched");
            return Result.success()
        } else {
            println("Value din't Matched");
            return Result.retry()
        }
    }
}