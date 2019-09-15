package sarabjit.blog.workmanager.worker

import android.content.Context
import android.support.annotation.NonNull
import androidx.work.Worker
import androidx.work.WorkerParameters


class CustomPeriodicWorkerThread(@NonNull val context: Context,
                                 @NonNull workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    override fun doWork(): Result {
        //Do Your periodic work here:

        return Result.success()
    }

    override fun onStopped() {
        super.onStopped()
        //If you want to perform any action here
    }
}