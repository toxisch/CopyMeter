package de.havre.copymeter.ui.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import com.google.inject.Inject;
import de.havre.copymeter.persitence.ConfigService;
import de.havre.copymeter.persitence.ConfigTransportService;
import de.havre.copymeter.ui.R;
import roboguice.activity.RoboActivity;
import roboguice.util.Ln;

import java.io.File;

/**
 * Created by alex on 06.07.14.
 */
public class ConfigImportActifity extends RoboActivity {

    @Inject
    private ConfigService configService;

    @Inject
    private Context context;

    @Inject
    private ConfigTransportService configTransportService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showFileChooser();
    }

    private static final int FILE_SELECT_CODE = 0;

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    File f = new File(uri.getPath());
                    Ln.e("Selected import File Uri: " + uri.toString());
                    configTransportService.importConfig(f);
                    String msg = context.getResources().getString(R.string.settings_import_success);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(true);
                    builder.setTitle(R.string.common_success);
                    builder.setMessage(msg);
                    builder.setInverseBackgroundForced(true);
                    builder.setCancelable(true)
                            .setPositiveButton(context.getText(R.string.common_ok),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            finish();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

}