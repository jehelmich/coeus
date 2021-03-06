package com.janhelmich.ar.smarthome;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.TextView;

import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.janhelmich.coeus.R;

public class ARDeviceInfoCard extends Node implements Node.OnTapListener {
    protected static final float INFO_CARD_Y_POS_COEFF = 0.55f;

    private final int layout;

    protected final String deviceId;
    protected final String deviceType;

    protected final Context context;


    public ARDeviceInfoCard(String deviceId, String deviceType, Context context) {
        super();
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.context = context;
        setOnTapListener(this);

        layout = R.layout.ar_device_info;
    }

    @Override
    public void onActivate() {
        if (getScene() == null) {
            throw new IllegalStateException("Scene is null!");
        }

        this.setLocalPosition(new Vector3(0.0f, INFO_CARD_Y_POS_COEFF, 0.0f));
        ViewRenderable.builder()
                .setView(context, layout)
                .build()
                .thenAccept(
                        (renderable) -> {
                            this.setRenderable(renderable);
                            TextView deviceNameTextView = renderable.getView().findViewById(R.id.ar_device_name);
                            deviceNameTextView.setText(this.deviceId);

                            TextView deviceTypeTextView = renderable.getView().findViewById(R.id.ar_device_type);
                            deviceTypeTextView.setText(this.deviceType);

                        })
                .exceptionally(
                        (throwable) -> {
                            throw new AssertionError("Could not load plane card view.", throwable);
                        });


    }


    @Override
    public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {

    }
}
