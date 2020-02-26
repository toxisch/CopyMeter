package de.havre.copymeter.ui.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.inject.Inject;
import de.havre.copymeter.model.Printer;
import de.havre.copymeter.persitence.ConfigService;
import de.havre.copymeter.persitence.ConfigTransportService;
import de.havre.copymeter.ui.R;
import roboguice.activity.RoboListActivity;

import java.util.List;

/**
 * Created by alex on 06.07.14.
 */
public class PrinterListSettingsActifity extends RoboListActivity {

    @Inject
    public ConfigService configService;

    @Inject
    private ConfigTransportService configTransportService;

    @Inject
    private Context context;

    private ArrayAdapter<Printer> myArrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_printer_list);

        List<Printer> printerList = configService.getTallyConfig().getPrinterList();

        myArrayAdapter = new ArrayAdapter<Printer>(
                this,
                R.layout.settings_printer_listelement,
                R.id.settings_main_listelement_text,
                printerList);
        this.setListAdapter(myArrayAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        myArrayAdapter.notifyDataSetChanged();
    }

    public void addPrinter(final View v) {
        Intent intent = new Intent(this, PrinterSettingsActifity.class);
        startActivity(intent);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Printer p = configService.getTallyConfig().getPrinterList().get(position);
        Intent intent = new Intent(this, PrinterSettingsActifity.class);
        intent.putExtra("printerId", p.getId());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_export:
                String fileName = configTransportService.exportConfig();
                String msg = context.getResources().getString(R.string.settings_export_success, fileName);
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
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
                break;
            case R.id.menu_import:
                Intent myIntent2 = new Intent(this, ConfigImportActifity.class);
                startActivity(myIntent2);
                break;
            case R.id.menu_resetModel:
                configService.getTallyConfig().resetModelCache();
                myArrayAdapter.notifyDataSetChanged();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}