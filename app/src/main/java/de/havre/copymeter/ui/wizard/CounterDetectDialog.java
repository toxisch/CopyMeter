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

import java.util.ArrayList;
import java.util.Map;

public class CounterDetectDialog extends RoboAsyncTask<Map<String, Integer>> {

    private ProgressDialog dialog;

    private Counter counter = null;

    private String printerId = null;

    private Map<String, Integer> model = null;

    private Activity context;

    @Inject
    public ConfigService configService;

    @Inject
    public SemiautomaticWizardService semiautomaticWizardService;

    public CounterDetectDialog(Activity context, Counter counter, String printerId) {
        super(context);
        this.dialog = new ProgressDialog(context);
        this.counter = counter;
        this.printerId = printerId;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {

        String msg = context.getResources().getString(R.string.wizard_auto_init);
        if (model == null) {
            msg = context.getResources().getString(R.string.wizard_auto_lookup);
        }

        this.dialog.setTitle(R.string.common_pleas_wait);
        this.dialog.setMessage(msg);
        this.dialog.setCancelable(true);
        this.dialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getText(R.string.common_cancel),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        this.dialog.show();
    }

    @Override
    public Map<String, Integer>call() throws Exception {

        Map<String, Integer> result = semiautomaticWizardService.findCounters(model, printerId);
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
            message = context.getResources().getString(R.string.error_counter_match_oid, "");
            semiautomaticWizardService.cleanCache();
        }
        else if  (e instanceof SnmpClientException)
        {
            e.printStackTrace();
            message = context.getResources().getString(R.string.error_printer_connection, printerConfig.getIp());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle(context.getText(R.string.common_error));
        builder.setMessage(message);
        builder.setInverseBackgroundForced(true);
        builder.setCancelable(true)
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

    @Override
    protected void onSuccess(Map<String, Integer> neuModel) {
        Printer printerConfig = configService.getTallyConfig().getPrinter(printerId);

        if (model == null)
        {
            model = neuModel;
            String message = context.getResources().getString(R.string.wizard_auto_increment, counter.getName());
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
            if (neuModel.keySet().size() > 0) {
                ArrayList<String> oids = new ArrayList<String>(neuModel.keySet());
                counter.setOid(oids.get(0));
                printerConfig.getCounterList().add(counter);
                context.finish();
            }
            else
            {
                String message = context.getResources().getString(R.string.error_counter_match);
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
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
    }

    @Override
    protected void onFinally() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

}
