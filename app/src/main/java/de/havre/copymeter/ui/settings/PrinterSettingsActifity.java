package de.havre.copymeter.ui.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.inject.Inject;
import de.havre.copymeter.model.Printer;
import de.havre.copymeter.persitence.ConfigService;
import de.havre.copymeter.service.SemiautomaticWizardService;
import de.havre.copymeter.ui.R;
import de.havre.copymeter.validation.Validator;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

/**
 * Created by alex on 06.07.14.
 */
public class PrinterSettingsActifity extends RoboActivity {

    @Inject
    private SemiautomaticWizardService semiautomaticWizardService;

    @Inject
    private ConfigService configService;

    @Inject
    private Validator validator;

    @Inject
    private Context context;

    private Printer printerOnEdit;

    @InjectView(R.id.settings_printer_number)
    private EditText printerNumberField;

    @InjectView(R.id.settings_printer_ip)
    private EditText printerIpField;

    @InjectView(R.id.settings_printer_name)
    private EditText printerNameField;

    @InjectView(R.id.settings_printer_snmpport)
    private EditText printerSnmpportField;

    @InjectView(R.id.settings_printer_regexuri)
    private EditText printerRegExUriField;

    @InjectView(R.id.deleteButton)
    private Button printerDeleteButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_printer);

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start,
                                       int end, Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart) +
                            source.subSequence(start, end) +
                            destTxt.substring(dend);
                    if (!resultingTxt.matches("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                        return "";
                    } else {
                        String[] splits = resultingTxt.split("\\.");
                        for (int i = 0; i < splits.length; i++) {
                            if (Integer.valueOf(splits[i]) > 255) {
                                return "";
                            }
                        }
                    }
                }
                return null;
            }
        };
        printerIpField.setFilters(filters);

        // setup validator
        validator.addCheck(printerNumberField, Validator.Rule.IS_NUMERIC);
        validator.addCheck(printerIpField, Validator.Rule.IS_IP_ADDRESS);
        validator.addCheck(printerNameField, Validator.Rule.IS_NOT_EMPTY);
        validator.addCheck(printerSnmpportField, Validator.Rule.IS_NUMERIC);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!getIntent().hasExtra("printerId")) {
            printerOnEdit = new Printer();
            printerDeleteButton.setEnabled(false);
        } else {
            String printerId = getIntent().getExtras().getString("printerId");
            printerOnEdit = configService.getTallyConfig().getPrinter(printerId);
            printerDeleteButton.setEnabled(true);
        }

        printerRegExUriField.setText(printerOnEdit.getRegExUrl());
        printerSnmpportField.setText(printerOnEdit.getPort());
        printerNumberField.setText(printerOnEdit.getNumber());
        printerNameField.setText(printerOnEdit.getName());
        printerIpField.setText(printerOnEdit.getIp());
    }

    private boolean validateForm() {
        if (validator.validate()) {
            if (printerOnEdit != null) {
                printerOnEdit.setNumber(printerNumberField.getText().toString());
                printerOnEdit.setName(printerNameField.getText().toString());
                printerOnEdit.setIp(printerIpField.getText().toString());
                printerOnEdit.setPort(printerSnmpportField.getText().toString());
                printerOnEdit.setRegExUrl(printerRegExUriField.getText().toString());
            }
            return true;
        }
        return false;
    }

    public void showCounterList(View v)
    {
        if (validateForm()) {
            configService.getTallyConfig().addPrinter(printerOnEdit);
            Intent intent = new Intent(this, CounterListSettingsActifity.class);
            intent.putExtra("printerId", printerOnEdit.getId());
            semiautomaticWizardService.cleanCache();
            startActivity(intent);
        }
    }

    public void onClickOk(View v) {
        if (validateForm()) {
            configService.getTallyConfig().addPrinter(printerOnEdit);
            finish();
        }
    }

    public void onClickDelete(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        String msg = context.getResources().getString(R.string.settings_printer_dielaog_delete, printerOnEdit.getName());
        builder.setTitle(context.getText(R.string.common_warning));
        builder.setMessage(msg);
        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton(context.getText(R.string.common_delete),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        configService.getTallyConfig().deletePrinter(printerOnEdit.getId());
                        dialog.dismiss();
                        finish();

                    }
                });
        builder.setNegativeButton(context.getText(R.string.common_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

}