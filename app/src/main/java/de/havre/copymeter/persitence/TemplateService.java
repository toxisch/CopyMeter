package de.havre.copymeter.persitence;

import android.content.Context;
import android.content.res.AssetManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.havre.copymeter.common.InputStreamStringReader;
import de.havre.copymeter.ui.R;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by alex on 03.03.15.
 */
@Singleton
public class TemplateService {
    @Inject
    AssetManager assetManager;

    @Inject
    Context context;

    public enum Template
    {
        SELECTOR("selector.html"),
        SUCCESS("success.html"),
        ERROR("error.html");

        private String page;

        Template(String page) {
            this.page = page;
        }
    }

    public String readTemplate(Template template) throws IOException {
        String language = context.getResources().getString(R.string.lang);

        InputStream ims = assetManager.open(language + "_" +template.page);
        InputStreamStringReader sr = new InputStreamStringReader(ims);
        final String theString = sr.readString();
        return theString;
    }
}
