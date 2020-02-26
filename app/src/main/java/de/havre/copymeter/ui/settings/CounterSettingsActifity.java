package de.havre.copymeter.ui.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.inject.Inject;
import de.havre.copymeter.model.Counter;
import de.havre.copymeter.persitence.ConfigService;
import de.havre.copymeter.ui.R;
import de.havre.copymeter.validation.Validator;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

/**
 * Created by alex on 06.07.14.
 */
public class CounterSettingsActifity extends RoboActivity {

    @Inject
    public ConfigService configService;

    @Inject
    private Context context;

    @Inject
    private Validator validator;

    @InjectView(R.id.settings_counter_name)
    private EditText counterNameField;

    @InjectView(R.id.settings_counter_oid)
    private EditText counterOidField;

    @InjectView(R.id.settings_counter_regex)
    private EditText counterRegexField;

    @InjectView(R.id.deleteButton)
    private Button counterDeleteButton;

    private String printerId;

    private Counter counterOnEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_counter);

        // setup validator
        validator.addCheck(counterNameField, Validator.Rule.IS_NOT_EMPTY);
        validator.addCheck(counterOidField, Validator.Rule.IS_NOT_EMPTY);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!getIntent().hasExtra("counterId")) {
            printerId = getIntent().getExtras().getString("printerId");
            counterOnEdit = new Counter();
            counterDeleteButton.setEnabled(false);
        } else {
            printerId = getIntent().getExtras().getString("printerId");
            String counterId = getIntent().getExtras().getString("counterId");
            counterOnEdit = configService.getTallyConfig().getPrinter(printerId)
                    .getCounter(counterId);
            counterDeleteButton.setEnabled(true);
        }

        counterNameField.setText(counterOnEdit.getName());
        counterOidField.setText(counterOnEdit.getOid());
        counterRegexField.setText(counterOnEdit.getRegex());
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void onClickDelete(final View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        String msg = context.getResources().getString(R.string.settings_counter_dielaog_delete, counterOnEdit.getName());
        builder.setTitle(context.getText(R.string.common_warning));
        builder.setMessage(msg);
        builder.setPositiveButton(context.getText(R.string.common_delete),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        configService.getTallyConfig().getPrinter(printerId).deleteCounter(counterOnEdit.getId());
                        dialog.dismiss();
                        finish();
                    }
                });
        builder.setNegativeButton(context.getText(R.string.common_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void onClickOk(View v) {
        if (validator.validate()) {
            counterOnEdit.setName(counterNameField.getText().toString());
            counterOnEdit.setOid(counterOidField.getText().toString());
            counterOnEdit.setRegex(counterRegexField.getText().toString());
            configService.getTallyConfig().getPrinter(printerId).addCounter(counterOnEdit);
            finish();
        }
    }
}