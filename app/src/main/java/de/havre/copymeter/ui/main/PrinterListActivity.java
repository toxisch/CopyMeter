package de.havre.copymeter.ui.main;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.*;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.google.inject.Inject;
import de.havre.copymeter.model.Counter;
import de.havre.copymeter.model.Printer;
import de.havre.copymeter.observer.PrinterObserver;
import de.havre.copymeter.observer.PrinterObserverListener;
import de.havre.copymeter.persitence.ConfigService;
import de.havre.copymeter.persitence.ImportExportService;
import de.havre.copymeter.ui.R;
import de.havre.copymeter.ui.settings.PrinterListSettingsActifity;
import roboguice.activity.RoboListActivity;
import roboguice.util.Ln;

import java.io.IOException;
import java.util.List;

@TargetApi(3)
public class PrinterListActivity extends RoboListActivity implements PrinterObserverListener {

    static final String SKU_PREMIUM = "premium";
    private static final int SINGLE_RESET_DIALOG = 1;
    @Inject
    public ConfigService configService;
    @Inject
    PrinterObserver printerObserver;
    @Inject
    ImportExportService server;
    private MyArrayAdapter myArrayAdapter;
    private Printer printerOnFocus;
    private Handler mHandler;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        printerObserver.removeAll();
        configService.save();
    }

    @Override
    protected void onResume() {
        super.onResume();
        printerObserver.registerPrinter(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        printerObserver.removeAll();

    }

    /**
     * Called when the activity is first created.
     */
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        /* disable standby */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        this.setContentView(R.layout.mainlayout);

     	/* setup config */
        List<Printer> printerList = configService.getTallyConfig().getPrinterList();

        myArrayAdapter = new MyArrayAdapter(this, printerList);
        this.setListAdapter(myArrayAdapter);

        mHandler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message inputMessage) {
                // Gets the image task from the incoming Message object.
                myArrayAdapter.notifyDataSetInvalidated();
            }

        };

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    private void reset() {
            Log.v("TAG", "reset printer printerOnFocus " + printerOnFocus.getId());
            printerOnFocus.resetToSnapshot();
            configService.save();
            copyCounterNotification(printerOnFocus);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        printerOnFocus = configService.getTallyConfig().getPrinterList().get(position);
        View resultView = this.getLayoutInflater().inflate(R.layout.result, null, true);
        TableLayout table = (TableLayout)resultView.findViewById(R.id.result_table);

        printerOnFocus.makeSnapshot();
        reset();

//        for (Counter counter : printerOnFocus.getCounterList()) {
//            TextView label = new TextView(this);
//            label.setText(counter.getName() + ":   ");
//            label.setTextSize(30);
//
//            TextView value = new TextView(this);
//            value.setText("" + counter.calculateRelativeCountes());
//            value.setTextSize(30);
//
//            TableRow tableRow = new TableRow(this);
//            tableRow.addView(label);
//            tableRow.addView(value);
//            table.addView(tableRow);
//        }
//
//        AlertDialog alert = new AlertDialog.Builder(this)
//                .setView(resultView)
//                .setNegativeButton(this.getText(R.string.common_cancel), new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        dialog.dismiss();
//                    }
//                })
//                .setPositiveButton(this.getText(R.string.common_reset), new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        reset();
//                        dialog.dismiss();
//                    }
//                })
//                .show();

    }

    public void globalReset(final View v)
    {
        for (Printer printerModel : configService.getTallyConfig().getPrinterList()) {
            Log.v("TAG", "reset global");
            printerModel.reset();
            copyCounterNotification(printerModel);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.global_reset:
                this.globalReset(null);
                break;
            case R.id.info_page:
                Intent myIntent1 = new Intent(this, InfoActifity.class);
                startActivity(myIntent1);
                break;
            case R.id.preferences:
                Intent myIntent2 = new Intent(this, PrinterListSettingsActifity.class);
                startActivity(myIntent2);
                break;
            case R.id.importexport:
                String entryPoint = null;
                try {
                    entryPoint = server.startService();
                } catch (IOException e) {
                    Ln.e(e);
                }
                String msg = getResources().getString(R.string.importexport_message, entryPoint);
                AlertDialog alert = new AlertDialog.Builder(this)
                        .setMessage(msg)
                        .setTitle(R.string.importexport)
                        .setPositiveButton(this.getText(R.string.common_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                try {
                                    server.stopService();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;


        }

        return super.onOptionsItemSelected(item);
    }

    private void updateApp() {
        Intent updateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://havre.de/CopyTallyAd.apk"));
        startActivity(updateIntent);
    }


    @Override
    public void copyCounterNotification(Printer printerModel) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Ln.d( "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        // Pass on the activity result to the helper for handling
    }

}