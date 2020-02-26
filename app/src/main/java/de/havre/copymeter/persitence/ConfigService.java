package de.havre.copymeter.persitence;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import de.havre.copymeter.common.InputStreamStringReader;
import de.havre.copymeter.model.TallyConfig;
import roboguice.util.Ln;

/**
 * Created by alex on 08.06.14.
 */
@Singleton
public class ConfigService {

    private final static boolean clean = false;
    private final static boolean dev = false;

    private final static String TAG = TallyConfig.class.getName();

    @Inject
    private AssetManager assets;

    @Inject
    private Context context;

    private TallyConfig tallyConfig;

    public TallyConfig getTallyConfig() {
        if (tallyConfig == null) {
            try {
                load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tallyConfig;
    }

    // TODO: Known bug: replacing object makes problem. ArrayAdapter has already an reference. Reload has effect just after restart.
    public void setTallyConfig(TallyConfig tallyConfig) {
        this.tallyConfig = tallyConfig;
    }

    public void save() {
        FileOutputStream fos = null;
        try {
            String json = new Gson().toJson(tallyConfig);
            Ln.d(json);


            fos = context.openFileOutput("copyTallyConfig.ser", Context.MODE_PRIVATE);
            PrintWriter pw = new PrintWriter(fos);
            pw.print(json);
            pw.close();
        } catch (Throwable e) {
            Ln.e(e.getCause().getMessage());
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

    private void load() {

        if (clean) {
            tallyConfig = new TallyConfig();
            return;
        }

        InputStream fis = null;
        try {
            if (dev) fis = assets.open("default.json");
            else fis = context.openFileInput("copyTallyConfig.ser");

            InputStreamStringReader sr = new InputStreamStringReader(fis);
            final String theString = sr.readString();

            tallyConfig = new Gson().fromJson(theString, TallyConfig.class);
        } catch (Throwable e) {
            Ln.e(e);
            tallyConfig = new TallyConfig();
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
