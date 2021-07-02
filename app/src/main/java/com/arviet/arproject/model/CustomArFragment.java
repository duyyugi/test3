package com.arviet.arproject.model;

import com.arviet.arproject.LearnActivity;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;

public class CustomArFragment extends ArFragment {
    @Override
    protected Config getSessionConfiguration(Session session) {
        Config config = new Config(session);
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
        config.setFocusMode(Config.FocusMode.AUTO);
        config.setLightEstimationMode(Config.LightEstimationMode.DISABLED);
        session.configure(config);
        this.getArSceneView().setupSession(session);
        ((LearnActivity)getActivity()).setupDatabase(config,session);
        return config;
    }
}
