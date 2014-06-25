package com.andrubyrne.support;

import org.junit.runners.model.InitializationError;
import org.robolectric.AndroidManifest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.res.Fs;

public class EschatonTestRunner extends RobolectricTestRunner {
    public EschatonTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected AndroidManifest getAppManifest(Config config) {
        String myAppPath = EschatonTestRunner.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath();
        String manifestPath = myAppPath + "../../AndroidManifest.xml";
        String resPath = myAppPath + "../../src/main/res";
        String assetPath = myAppPath + "../../assets";
        return createAppManifest(Fs.fileFromPath(manifestPath), Fs.fileFromPath(resPath), Fs.fileFromPath(assetPath));
    }
}