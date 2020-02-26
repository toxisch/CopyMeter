package de.havre.copymeter.ui.wizard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import com.google.inject.Inject;
import de.havre.copymeter.client.SnmpClientException;
import de.havre.copymeter.model.Counter;
import de.havre.copymeter.model.Printer;
import de.havre.copymeter.persitence.ConfigService;
import de.havre.copymeter.service.NoMatchingOidException;
import de.havre.copymeter.service.SemiautomaticWizardService;
import de.havre.copymeter.ui.R;
import roboguice.util.RoboAsyncTask;

import java.util.List;


public class CounterScanDialog extends RoboAsyncTask<List<String>> {

    private ProgressDialog dialog;

    private Counter counter = null;

    private String printerId = null;

    private Integer currentValue = null;

    private Activity context;

    private List<String> scanResult = null;

    @Inject
    public ConfigService configService;

    @Inject
    public SemiautomaticWizardService semiautomaticWizardService;

    public CounterScanDialog(Activity context, Integer currentValue, Counter counter, String printerId) {
        super(context);
        this.dialog = new ProgressDialog(context);
        this.counter = counter;
        this.printerId = printerId;
        this.currentValue = currentValue;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        String msg = context.getResources().getString(R.string.wizard_semi_wait, currentValue);
        this.dialog.setTitle(R.string.common_pleas_wait);
        this.dialog.setMessage(msg);
        this.dialog.setCancelable(true);
        this.dialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getText(R.string.common_cancel),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancel(true);
                    dialog.dismiss();
                }
            });
        this.dialog.show();
    }

    @Override
    public List<String> call() throws Exception {
        List<String> result = semiautomaticWizardService.resolveCounterOid(currentValue, printerId, scanResult);
        return result;
    }

    @Override
    protected void onException(Exception e) throws RuntimeException {
        super.onException(e);
        String message = null;
        Printer printerConfig = configService.getTallyConfig().getPrinter(printerId);
        if (e instanceof NoMatchingOidException)
        {
            e.printStackTrace();
            message = context.getResources().getString(R.string.error_counter_match_oid, currentValue);
            semiautomaticWizardService.cleanCache();
        }
        else if  (e instanceof SnmpClientException)
        {
            e.printStackTrace();
            message = context.getResources().getString(R.string.error_printer_connection, printerConfig.getIp());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setCancelable(true)
                .setTitle(context.getText(R.string.common_error))
                .setMessage(message)
                .setInverseBackgroundForced(true)
                .setCancelable(true)
                .setPositiveButton(context.getText(R.string.common_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //context.finish();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onSuccess(List<String> oid) {
        Printer printerConfig = configService.getTallyConfig().getPrinter(printerId);

        if (oid.size() == 1) {
            counter.setOid(oid.get(0));
            printerConfig.getCounterList().add(counter);
            context.finish();
        }
        else if (oid.size() > 1 && scanResult != null) {
            counter.setOid(oid.get(0));
            printerConfig.getCounterList().add(counter);
            context.finish();
        }
        else if(oid.size() > 1)
        {
            scanResult = oid;
            currentValue ++;
            String message = context.getResources().getString(R.string.wizard_semi_increment, counter.getName());
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setCancelable(true)
                    .setTitle(context.getText(R.string.common_warning))
                    .setMessage(message)
                    .setInverseBackgroundForced(true)
                    .setCancelable(true)
                    .setPositiveButton(context.getText(R.string.common_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    execute();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else
        {
            String message = context.getResources().getString(R.string.error_printer_connection, printerConfig.getIp());
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setCancelable(true)
                    .setTitle(context.getText(R.string.common_error))
                    .setMessage(message)
                    .setInverseBackgroundForced(true)
                    .setCancelable(true)
                    .setPositiveButton(context.getText(R.string.common_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    context.finish();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    protected void onFinally() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

}
