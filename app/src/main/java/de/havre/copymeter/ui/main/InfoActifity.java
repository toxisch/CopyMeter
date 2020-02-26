package de.havre.copymeter.ui.main;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import de.havre.copymeter.ui.R;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

/**
 * Created by alex on 06.07.14.
 */
public class InfoActifity extends RoboActivity {

    @InjectView(R.id.version)
    private TextView versionTextField;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            String version = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            versionTextField.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}