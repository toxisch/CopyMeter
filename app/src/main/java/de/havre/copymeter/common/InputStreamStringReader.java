package de.havre.copymeter.common;

import roboguice.util.Ln;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by alex on 14.02.15.
 */
public class InputStreamStringReader {

    private InputStream inputStream;

    public InputStreamStringReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String readString() {
        String result = null;
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            final StringBuilder stringBuilder = new StringBuilder();
            boolean done = false;

            while (!done) {
                final String line = reader.readLine();
                done = (line == null);

                if (line != null) {
                    stringBuilder.append(line);
                }
            }
            reader.close();
            inputStream.close();
            result = stringBuilder.toString();

        } catch (Throwable e) {
            Ln.e(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Ln.e(e);
                }
            }
        }
        return result;
    }

}
