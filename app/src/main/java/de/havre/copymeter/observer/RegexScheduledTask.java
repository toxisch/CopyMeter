package de.havre.copymeter.observer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.havre.copymeter.model.Counter;
import de.havre.copymeter.model.Printer;
import de.havre.copymeter.model.PrinterState;
import roboguice.util.Ln;

/**
 * Created by alex on 16.06.14.
 */
public class RegexScheduledTask implements Runnable {

    private Printer printer;

    private PrinterObserverListener printerObserverListener;

    public RegexScheduledTask(Printer printer, PrinterObserverListener printerObserverListener) {
        this.printer = printer;
        this.printerObserverListener = printerObserverListener;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(this.printer.getRegExUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            //connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("Accept-Language", "de-DE");
            connection.connect();
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String content = "", line;
            while ((line = rd.readLine()) != null) {
                content += line + "\n";
            }
            if (content != null) {
                for (Counter c : printer.getCounterList()) {
                    String regex = c.getRegex();
                    final Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
                    final Matcher matcher = pattern.matcher(content);
                    matcher.find();
                    String result = matcher.group(1);
                    System.out.println(result); // Prints String I want to extract
                    if (result != null) {
                        try {
                            int counte = Integer.parseInt(result);
                            c.updateCounter(counte);
                        } catch (Exception e) {
                            Ln.e("The regex result '" + result + "' is not a number");
                            printer.setPrinterState(PrinterState.OFFLINE);
                        }
                    } else {
                        Ln.e("The regex result '" + c.getRegex() + "' was null");
                        printer.setPrinterState(PrinterState.OFFLINE);
                    }
                }
            } else {
                Ln.e("The http uri result of '" + printer.getRegExUrl() + "' was null");
                printer.setPrinterState(PrinterState.OFFLINE);

            }
        } catch (Exception e) {
            Ln.e(e);
        }

        printerObserverListener.copyCounterNotification(printer);
    }

}
