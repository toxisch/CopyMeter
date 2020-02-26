package de.havre.copymeter.ui.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.inject.Inject;
import de.havre.copymeter.model.Counter;
import de.havre.copymeter.persitence.ConfigService;
import de.havre.copymeter.ui.R;
import de.havre.copymeter.ui.wizard.SemiAutomaticWizard;
import roboguice.activity.RoboListActivity;

import java.util.List;

/**
 * Created by alex on 06.07.14.
 */
public class CounterListSettingsActifity extends RoboListActivity {

    @Inject
    private ConfigService configService;

    @Inject
    private Context context;

    private String printerId;

    private ArrayAdapter<Counter> myArrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        printerId = getIntent().getExtras().getString("printerId");
        List<Counter> counterList = configService.getTallyConfig().getPrinter(printerId).getCounterList();
        setContentView(R.layout.settings_counter_list);
        myArrayAdapter = new ArrayAdapter<Counter>(
                this,
                R.layout.settings_counter_listelement,
                R.id.settings_counter_listelement_text,
                counterList);
        this.setListAdapter(myArrayAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        myArrayAdapter.notifyDataSetChanged();
    }

    public void addCounter(final View v)
    {
        CharSequence items[] = new CharSequence[]{
                context.getText(R.string.wizard_selection_semiautomatic),
                context.getText(R.string.wizard_selection_manual)
                };
        AlertDialog adb = new AlertDialog.Builder(this)
                .setSingleChoiceItems(items, 0, null)
                .setPositiveButton(context.getText(R.string.common_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        switch (selectedPosition) {
                            case 0: {
                                Intent intent = new Intent(v.getContext(), SemiAutomaticWizard.class);
                                intent.putExtra("printerId", printerId);
                                startActivity(intent);
                                break;
                            }
                            case 1: {
                                Intent intent = new Intent(v.getContext(), CounterSettingsActifity.class);
                                intent.putExtra("printerId", printerId);
                                startActivity(intent);
                                break;
                            }
                        }
                    }
                })
                .setNegativeButton(context.getText(R.string.common_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Counter p = configService.getTallyConfig().getPrinter(printerId).getCounterList().get(position);

        Intent intent = new Intent(this, CounterSettingsActifity.class);
        intent.putExtra("printerId", printerId);
        intent.putExtra("counterId", p.getId());
        startActivity(intent);
    }


}