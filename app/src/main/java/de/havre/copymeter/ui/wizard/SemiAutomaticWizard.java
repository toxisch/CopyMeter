package de.havre.copymeter.ui.wizard;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.google.inject.Inject;
import de.havre.copymeter.model.Counter;
import de.havre.copymeter.persitence.ConfigService;
import de.havre.copymeter.ui.R;
import de.havre.copymeter.validation.Validator;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;


/**
 * Created by alex on 27.07.14.
 */
public class SemiAutomaticWizard extends RoboActivity {

    @Inject
    private ConfigService configService;

    @Inject
    private Validator validator;

    @InjectView(R.id.wizard_semi_counter_value)
    private EditText wizardSemiCounterValue;

    @InjectView(R.id.wizard_semi_counter_name)
    private EditText wizardSemiCounterName;

    private String printerId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        printerId = getIntent().getExtras().getString("printerId");
        setContentView(R.layout.wizard_semi);

        // setup validator
        //validator.addCheck(wizardSemiCounterValue, Validator.Rule.IS_NUMERIC);
        validator.addCheck(wizardSemiCounterName, Validator.Rule.IS_NOT_EMPTY);

    }

    public void onClickOk(View view) {
        if (validator.validate()) {

            if (wizardSemiCounterValue.getText().toString().trim().equals(""))
            {
                Counter newCounter = new Counter();
                newCounter.setName(wizardSemiCounterName.getText().toString());
                newCounter.setPriceTableId("1");
                String counterName = wizardSemiCounterName.getText().toString();
                new CounterDetectDialog(this, newCounter, printerId).execute();
            }
            else {
                Integer currentCounts = Integer.valueOf(wizardSemiCounterValue.getText().toString());
                Counter newCounter = new Counter();
                newCounter.setName(wizardSemiCounterName.getText().toString());
                newCounter.setPriceTableId("1");
                String counterName = wizardSemiCounterName.getText().toString();
                new CounterScanDialog(this, currentCounts, newCounter, printerId).execute();
            }
        }
    }

}
