package sarabjit.blog.workmanager

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.selection_activity.*
import sarabjit.blog.workmanager.utils.Constants

class SelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.selection_activity)
        val intent = Intent(this, MainActivity::class.java)
        one_time.setOnClickListener {
            intent.putExtra(Constants.KEY, Constants.ONE_TIME);
            startActivity(intent)
        }
        periodic.setOnClickListener {
            intent.putExtra(Constants.KEY, Constants.PERIODIC);
            startActivity(intent)
        }

        one_time_with_backup.setOnClickListener {
            intent.putExtra(Constants.KEY, Constants.ONE_TIME_BACKUP_POLICY);
            startActivity(intent)
        }

        sequencing.setOnClickListener {
            intent.putExtra(Constants.KEY, Constants.ONE_TIME_SEQUENCING);
            startActivity(intent)
        }

    }
}