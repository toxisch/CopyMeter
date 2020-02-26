package de.havre.copymeter.validation;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import de.havre.copymeter.ui.R;

/**
 * Created by alex on 11.08.14.
 */
public class Validator {

    @Inject
    private Context context;

    public enum Rule {
        IS_NUMERIC("\\d+", R.string.validator_is_numeric),
        IS_NOT_EMPTY(".+", R.string.validator_is_not_empty),
        IS_IP_ADDRESS("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b", R.string.validator_is_ip_address);

        private String regex;

        private int stringId;

        Rule(String regex, int stringId) {
            this.regex = regex;
            this.stringId = stringId;
        }

        public String getRegex() {
            return regex;
        }

        public int getStringId() {
            return stringId;
        }

    }

    private class Check {
        EditText editText;
        Rule rule;

        private Check(EditText editText, Rule rule) {
            this.editText = editText;
            this.rule = rule;

            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b)
                    {
                        validate();
                    }
                }
            });

        }

        boolean validate() {
            String text = editText.getText().toString();
            if (text == null || !text.matches(rule.getRegex())) {
                CharSequence message = context.getResources().getString(rule.stringId);
                editText.setError(message);
                return false;
            }
            editText.setError(null);
            return true;
        }

    }

    private List<Check> checkList = new ArrayList<Check>();

    public Validator addCheck(EditText editText, Rule rule) {
        checkList.add(new Check(editText, rule));
        return this;
    }

    public boolean validate() {
        boolean result = true;
        for (Check rule : checkList) {
            if (!rule.validate()) {
                result = false;
            }
        }
        return result;
    }
}
