package com.janhelmich.coeus;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.janhelmich.ar.smarthome.ARDevice;
import com.janhelmich.ar.smarthome.ThreeAxisController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity {

    public static final String MODE_USE = "MODE_USE";
    public static final String MODE_EDIT = "MODE_EDIT";
    /**
     * Constants section for use in this class. First one is mode types for the Activity.
     */



    private ArFragment fragment;

    private PointerDrawable pointer = new PointerDrawable();
    private boolean isTracking;
    private boolean isHitting;

    private AnchorNode lastAnchor;

    private ObjectConnector connector;

    private static String mode;


    // The controls for the edit mode
    private ThreeAxisController axisController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragment = (ArFragment)
                getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);

        fragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            fragment.onUpdate(frameTime);
            onUpdate();
        });

        initializeGallery();
        connector = new ObjectConnector();

        // Specify all remaining floating action buttons, so they can be passed to nodes
        // for on selected programming.
        axisController = new ThreeAxisController(
                findViewById(R.id.top),
                findViewById(R.id.bottom),
                findViewById(R.id.left),
                findViewById(R.id.right),
                findViewById(R.id.up),
                findViewById(R.id.down),
                findViewById(R.id.d_pad),
                findViewById(R.id.control_pad));

        MainActivity.mode = MODE_EDIT;
        changeMode(MODE_EDIT);

    }

    private void onUpdate() {
        boolean trackingChanged = updateTracking();
        View contentView = findViewById(android.R.id.content);
        if (trackingChanged) {
            if (isTracking) {
                contentView.getOverlay().add(pointer);
            } else {
                contentView.getOverlay().remove(pointer);
            }
            contentView.invalidate();
        }

        if (isTracking) {
            boolean hitTestChanged = updateHitTest();
            if (hitTestChanged) {
                pointer.setEnabled(isHitting);
                contentView.invalidate();
            }
        }
    }

    private boolean updateTracking() {
        Frame frame = fragment.getArSceneView().getArFrame();
        boolean wasTracking = isTracking;
        isTracking = frame != null &&
                frame.getCamera().getTrackingState() == TrackingState.TRACKING;
        return isTracking != wasTracking;
    }

    private boolean updateHitTest() {
        Frame frame = fragment.getArSceneView().getArFrame();
        android.graphics.Point pt = getScreenCenter();
        List<HitResult> hits;
        boolean wasHitting = isHitting;
        isHitting = false;
        if (frame != null) {
            hits = frame.hitTest(pt.x, pt.y);
            for (HitResult hit : hits) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane &&
                        ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                    isHitting = true;
                    break;
                }
            }
        }
        return wasHitting != isHitting;
    }

    private android.graphics.Point getScreenCenter() {
        View vw = findViewById(android.R.id.content);
        return new android.graphics.Point(vw.getWidth()/2, vw.getHeight()/2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.change_mode) {
            if (mode.equals(MODE_USE)) {
                this.changeMode(MODE_EDIT);
            } else {
                this.changeMode(MODE_USE);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeGallery() {
        LinearLayout gallery = findViewById(R.id.gallery_layout);

        ImageView andy = new ImageView(this);
        andy.setImageResource(R.drawable.droid_thumb);
        andy.setContentDescription("andy");
        andy.setOnClickListener(view ->{addObject(Uri.parse("andy.sfb"));});
        gallery.addView(andy);

//        ImageView cabin = new ImageView(this);
//        cabin.setImageResource(R.drawable.cabin_thumb);
//        cabin.setContentDescription("cabin");
//        cabin.setOnClickListener(view ->{addObject(Uri.parse("Cabin.sfb"));});
//        gallery.addView(cabin);
//
//        ImageView house = new ImageView(this);
//        house.setImageResource(R.drawable.house_thumb);
//        house.setContentDescription("house");
//        house.setOnClickListener(view ->{addObject(Uri.parse("House.sfb"));});
//        gallery.addView(house);
//
//        ImageView igloo = new ImageView(this);
//        igloo.setImageResource(R.drawable.igloo_thumb);
//        igloo.setContentDescription("igloo");
//        igloo.setOnClickListener(view ->{addObject(Uri.parse("igloo.sfb"));});
//        gallery.addView(igloo);

        ImageView lightbulb = new ImageView(this);
        lightbulb.setImageResource(R.drawable.igloo_thumb);
        lightbulb.setContentDescription("lightbulb");
        lightbulb.setOnClickListener(view ->{addObject(Uri.parse("lightbulb.sfb"));});
        gallery.addView(lightbulb);

        ImageView button = new ImageView(this);
        button.setImageResource(R.drawable.house_thumb);
        button.setContentDescription("button");
        button.setOnClickListener(view ->{addObject(Uri.parse("button.sfb"));});
        gallery.addView(button);

        ImageView sensor = new ImageView(this);
        sensor.setImageResource(R.drawable.cabin_thumb);
        sensor.setContentDescription("sensor");
        sensor.setOnClickListener(view ->{addObject(Uri.parse("sensor.sfb"));});
        gallery.addView(sensor);
    }

    private void addObject(Uri model) {
        Frame frame = fragment.getArSceneView().getArFrame();
        android.graphics.Point pt = getScreenCenter();
        List<HitResult> hits;
        if (frame != null) {
            hits = frame.hitTest(pt.x, pt.y);
            for (HitResult hit : hits) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane &&
                        ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                    placeObject(fragment, hit.createAnchor(), model);
                    break;

                }
            }
        }
    }

    private void placeObject(ArFragment fragment, Anchor anchor, Uri model) {
        CompletableFuture<Void> renderableFuture =
                ModelRenderable.builder()
                        .setSource(fragment.getContext(), model)
                        .build()
                        .thenAccept(renderable -> addNodeToScene(fragment, anchor, renderable))
                        .exceptionally((throwable -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setMessage(throwable.getMessage())
                                    .setTitle("Codelab error!");
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            return null;
                        }));

        //(new Handler()).postDelayed(anchor::detach, 5000);
    }

    private void addNodeToScene(ArFragment fragment, Anchor anchor, Renderable renderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        ARDevice node = new ARDevice("nodeName", renderable, this, axisController);
        node.setParent(anchorNode);
        fragment.getArSceneView().getScene().addChild(anchorNode);

        addAnchorLine(anchorNode);
    }

    private void addAnchorLine(AnchorNode a2) {
        if (lastAnchor != null) {
            connector.addLine(lastAnchor, a2, this);
        }
        lastAnchor = a2;
    }

    private void changeMode(String mode) {
        if (MainActivity.mode.equals(MODE_USE)) {
            axisController.setInvisible();
            this.findViewById(R.id.gallery_layout).setVisibility(View.GONE);
        } else {
            axisController.setVisible();
            this.findViewById(R.id.gallery_layout).setVisibility(View.VISIBLE);
        }
        MainActivity.mode = mode;
    }

    public static String getMode() {
        return MainActivity.mode;
    }

}
