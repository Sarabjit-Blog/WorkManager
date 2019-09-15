package sarabjit.blog.workmanager


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.work.*
import kotlinx.android.synthetic.main.activity_main.*
import sarabjit.blog.workmanager.utils.Constants
import sarabjit.blog.workmanager.worker.CustomPeriodicWorkerThread
import sarabjit.blog.workmanager.worker.CustomWorkerThread
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    lateinit var mWorkManager: WorkManager;
    lateinit var oneTimeWorkerRequest: OneTimeWorkRequest
    lateinit var oneTimeWorkerRequestWithBackUpPolicy: OneTimeWorkRequest
    lateinit var periodicWorkerRequest: PeriodicWorkRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val key = intent.getStringExtra(Constants.KEY)
        performWorkerAction(key)
    }

    private fun performWorkerAction(key: String) {
        if (key.equals(Constants.ONE_TIME)) {
            periodic_status.visibility = View.GONE
            createOneTimeWorkerJobWithConstraints()
            makeToast(getString(R.string.constraint_network_charging))
        } else if (key.equals(Constants.PERIODIC)) {
            one_time_status.visibility = View.GONE
            createPeriodicWorkerJob()
        } else if (key.equals(Constants.ONE_TIME_BACKUP_POLICY)) {
            periodic_status.visibility = View.GONE
            createOneTimeWorkerJobWithBackUpPolicyAndConstraints()
            makeToast(getString(R.string.constraint_network_charging))
        } else {
            periodic_status.visibility = View.GONE
            enqueWorkingSequence()
        }
    }

    private fun makeToast(@NonNull text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    fun createOneTimeWorkerJobWithConstraints() {
        mWorkManager = WorkManager.getInstance()

        val constraints: Constraints =
            Constraints.Builder().setRequiresCharging(true)
                .setRequiredNetworkType(NetworkType.CONNECTED).build()

        val data = Data.Builder()
        data.putString(CustomWorkerThread.DATA_PARAM, CustomWorkerThread.DATA_VALUE)

        oneTimeWorkerRequest =
            OneTimeWorkRequestBuilder<CustomWorkerThread>().setInputData(data.build())
                .setConstraints(constraints)
                .build()

        mWorkManager.getWorkInfoByIdLiveData(oneTimeWorkerRequest.getId()).observe(
            this, Observer {
                printStatusOfWorkerThread(it, one_time_status, false)
            })

        mWorkManager.enqueueUniqueWork(
            Constants.UNIQUE_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            oneTimeWorkerRequest
        )
    }

    fun createOneTimeWorkerJobWithBackUpPolicyAndConstraints() {
        mWorkManager = WorkManager.getInstance()

        val data = Data.Builder()
        data.putString(CustomWorkerThread.DATA_PARAM, CustomWorkerThread.DATA_VALUE)

        val constraints: Constraints =
            Constraints.Builder().setRequiresCharging(true)
                .setRequiredNetworkType(NetworkType.CONNECTED).build()

        oneTimeWorkerRequestWithBackUpPolicy =
            OneTimeWorkRequestBuilder<CustomWorkerThread>().setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                ).setInputData(data.build()).build()

        mWorkManager.getWorkInfoByIdLiveData(oneTimeWorkerRequestWithBackUpPolicy.getId())
            .observe(this, Observer {
                printStatusOfWorkerThread(it, one_time_status, true)
            })

        mWorkManager.enqueueUniqueWork(
            Constants.UNIQUE_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            oneTimeWorkerRequestWithBackUpPolicy
        )
    }

    fun createPeriodicWorkerJob() {
        mWorkManager = WorkManager.getInstance()

        periodicWorkerRequest =
            PeriodicWorkRequestBuilder<CustomPeriodicWorkerThread>(
                16,
                TimeUnit.MINUTES
            ).build();

        mWorkManager.getWorkInfoByIdLiveData(periodicWorkerRequest.getId()).observe(
            this, Observer {
                printStatusOfWorkerThread(it, periodic_status, false)
            })
        mWorkManager.enqueueUniquePeriodicWork(
            Constants.UNIQUE_WORK_PERIODIC_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkerRequest
        )
        makeToast(getString(R.string.work_policy_keep))

    }

    fun enqueWorkingSequence() {
        mWorkManager = WorkManager.getInstance()

        val data = Data.Builder()
        data.putString(CustomWorkerThread.DATA_PARAM, CustomWorkerThread.DATA_VALUE)

        oneTimeWorkerRequest =
            OneTimeWorkRequestBuilder<CustomWorkerThread>().setInputData(data.build())
                .build()


        oneTimeWorkerRequestWithBackUpPolicy =
            OneTimeWorkRequestBuilder<CustomWorkerThread>()
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                ).setInputData(data.build()).build()

        mWorkManager.getWorkInfoByIdLiveData(oneTimeWorkerRequestWithBackUpPolicy.getId())
            .observe(this,
                Observer {
                    printStatusOfWorkerThread(it, one_time_status, true)
                })
        mWorkManager.getWorkInfoByIdLiveData(oneTimeWorkerRequest.getId()).observe(this,
            Observer {
                printStatusOfWorkerThread(it, one_time_status, false)
            })

        mWorkManager.beginWith(oneTimeWorkerRequest).then(oneTimeWorkerRequestWithBackUpPolicy)
            .enqueue()

    }

    fun printStatusOfWorkerThread(
        workInfo: WorkInfo?,
        textView: TextView,
        isWithBackOffPolicy: Boolean
    ) {
        workInfo.let {
            when (it?.state) {
                WorkInfo.State.ENQUEUED -> {
                    textView.append(
                        if (isWithBackOffPolicy) getString(R.string.enqueued_with_backup)
                        else getString(R.string.enqueued)
                    );
                }
                WorkInfo.State.RUNNING -> {
                    textView.append(
                        if (isWithBackOffPolicy) getString(R.string.running_with_backup)
                        else getString(R.string.running)
                    )
                }
                WorkInfo.State.BLOCKED -> {

                    textView.append(
                        if (isWithBackOffPolicy) getString(R.string.blocked_with_backup)
                        else getString(R.string.blocked)
                    )
                }
                WorkInfo.State.SUCCEEDED -> {
                    textView.append(
                        if (isWithBackOffPolicy) getString(R.string.succeeded_with_backup)
                        else getString(R.string.succeeded)
                    )
                }
                WorkInfo.State.FAILED -> {
                    textView.append(
                        if (isWithBackOffPolicy) getString(R.string.failed_with_backup)
                        else getString(R.string.failed)
                    )
                }
                WorkInfo.State.CANCELLED -> {
                    textView.append(
                        if (isWithBackOffPolicy) getString(R.string.cancelled_with_backup)
                        else getString(R.string.cancelled)
                    )
                }
            }
        }
    }
}
