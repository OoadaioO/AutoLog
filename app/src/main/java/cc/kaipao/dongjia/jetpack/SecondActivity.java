package cc.kaipao.dongjia.jetpack;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

/**
 * <p>
 * email:xubo@idongjia.cn
 * time:2018/11/1
 *
 * @author xb
 */
public class SecondActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        findViewById(R.id.btnBack).setOnClickListener(view -> navigateUpCompat());

    }

    private void navigateUpCompat() {
        Intent upIntent = NavUtils.getParentActivityIntent(SecondActivity.this);
        if(upIntent != null) {
            if (NavUtils.shouldUpRecreateTask(SecondActivity.this, upIntent)) {
                TaskStackBuilder.create(SecondActivity.this)
                        .addNextIntentWithParentStack(upIntent)
                        .startActivities();
            }
            else {
                NavUtils.navigateUpTo(SecondActivity.this, upIntent);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            navigateUpCompat();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            navigateUpCompat();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
