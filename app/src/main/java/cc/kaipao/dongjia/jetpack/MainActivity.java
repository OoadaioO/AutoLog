package cc.kaipao.dongjia.jetpack;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import androidx.navigation.fragment.NavHostFragment;
import cc.kaipao.dongjia.jetpack.ui.main.MainFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment fragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.my_nav_host_fragment);
        return fragment.getNavController().navigateUp();
    }

}
