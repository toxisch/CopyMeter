package de.havre.copymeter.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.havre.copymeter.model.Counter;
import de.havre.copymeter.model.Printer;
import de.havre.copymeter.model.PrinterState;
import de.havre.copymeter.ui.R;

import java.util.List;

public class MyArrayAdapter extends ArrayAdapter<Printer> {


    private List<Printer> printerList;

    private Context context;

    public MyArrayAdapter(Context context, List<Printer> printerList) {
        super(context, R.layout.printerlayout, printerList);
        this.context = context;
        this.printerList = printerList;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Printer printer = printerList.get(position);

        View printerView = inflater.inflate(R.layout.printerlayout, null, true);
        TextView idView = (TextView) printerView.findViewById(R.id.number);
        idView.setText(printer.getNumber());

        if (printerView != null) {
            ImageView stateIconView = (ImageView) printerView.findViewById(R.id.icon);
            PrinterState state = printer.getPrinterState();
            switch (state) {
                case UPDATE:
                    stateIconView.setImageResource(R.drawable.progress);
                    break;
                case ONLINE:
                    stateIconView.setImageResource(R.drawable.okstatus);
                    break;
                case OFFLINE:
                    stateIconView.setImageResource(R.drawable.errorstatus);
                    break;
            }

            for (Counter counter : printer.getCounterList()) {
                View counterView = inflater.inflate(R.layout.counterlayout, null, true);

                TextView counterIdView = (TextView) counterView.findViewById(R.id.counterId);
                counterIdView.setText(counter.getName());

                LinearLayout layoutView = (LinearLayout) printerView.findViewById(R.id.layout);

                TextView findViewById = (TextView) counterView.findViewById(R.id.currentCount);
                if (findViewById != null) {
                    findViewById.setText(counter.calculateRelativeCountes() + "");
                }
                TextView lastCountView = (TextView) counterView.findViewById(R.id.lastCount);
                if (lastCountView != null) {
                    lastCountView.setText(counter.getLastCounts() + "");
                }

                layoutView.addView(counterView);
            }

            printerView.postInvalidate();
        }

        return printerView;
    }
}