package de.havre.copymeter.persitence;

import android.os.Environment;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.havre.copymeter.model.TallyConfig;
import roboguice.util.Ln;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alex on 08.06.14.
 */
@Singleton
public class ConfigTransportService {

    @Inject
    ConfigService configService;

    private String createFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmm");
        String timestamp = format.format(new Date());
        return "CopyMeter_" + timestamp + ".json";
    }

    public String exportConfig() {
        String fileName = createFileName();
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File file = new File(externalStorageDirectory, fileName);
        exportToFile(file);
        return fileName;
    }

    public void importConfig(File file) {
        importFromFile(file);
    }


    private void exportToFile(File myFile) {
        String json = new Gson().toJson(configService.getTallyConfig());
        Ln.d(json);
        FileOutputStream fos = null;
        try {
            myFile.createNewFile();
            fos = new FileOutputStream(myFile);
            PrintWriter pw = new PrintWriter(fos);
            pw.print(json);
            pw.close();
        } catch (Exception e) {
            Ln.e(e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Ln.e(e);
                }
            }
        }
    }

    private void importFromFile(File myFile) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(myFile);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
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
            fis.close();
            TallyConfig tallyConfig = new Gson().fromJson(stringBuilder.toString(), TallyConfig.class);
            configService.setTallyConfig(tallyConfig);
        } catch (Throwable e) {
            Ln.e(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    Ln.e(e);
                }
            }
        }
    }

}
