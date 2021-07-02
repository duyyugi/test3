package com.arviet.arproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.arviet.arproject.adapter.ActionAdapter;
import com.arviet.arproject.model.ARContent;
import com.arviet.arproject.model.Action;
import com.arviet.arproject.model.AnchorNodeFatherID;
import com.arviet.arproject.model.CustomArFragment;
import com.arviet.arproject.model.Marker;
import com.arviet.arproject.model.TextARContent;
import com.google.android.filament.ColorGrading;
import com.google.android.filament.Engine;
import com.google.android.filament.filamat.MaterialBuilder;
import com.google.android.filament.filamat.MaterialPackage;
import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.animation.ModelAnimator;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.EngineInstance;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.RenderableInstance;
import com.google.ar.sceneform.rendering.Renderer;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LearnActivity extends AppCompatActivity implements Scene.OnUpdateListener,ArFragment.OnViewCreatedListener {
    private CustomArFragment arFragment;
    private TextView textView;
    private List <Marker> markerList;
    private boolean scanned = false;
    private ArrayList<AnchorNode> anchorNodeArrayList = new ArrayList<>();
    private int currentMarkerID = 0;
    private Material plainVideoMaterial;
    private List<MediaPlayer> mediaPlayers = new ArrayList<>();
    private List<ObjectAnimator> audioObjectAnimators = new ArrayList<>();
    private Renderable modelRenderable;
    private boolean videoCreated = false;
    private boolean videoRendered = false;
    private ArrayList<AnchorNodeFatherID> anchorNodeFatherIDList = new ArrayList<>();
    AnchorNode currentAnchorNode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        arFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        arFragment.getArSceneView().getScene().addOnUpdateListener(this);
        arFragment.getPlaneDiscoveryController().hide();
        arFragment.getPlaneDiscoveryController().setInstructionView(null);
        ImageView imv_back = findViewById(R.id.imv_back);
        imv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LearnActivity.this,LessonListAcitivity.class));
            }
        });
    }

    public void setupDatabase(Config config, Session session){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LearnActivity.this);
        String markerListString = preferences.getString("markerlist",null);
        Type type = new TypeToken<List<Marker>>(){}.getType();
        markerList = new Gson().fromJson(markerListString,type);
        AugmentedImageDatabase aid = new AugmentedImageDatabase(session);
        for (Marker marker: markerList){
            List<Action> actionList = marker.getActionList();
            Bitmap bitmap = getBitmapFromURL(marker.getURL());
            aid.addImage(""+marker.getMarkerID(),bitmap,bitmap.getWidth()*0.0002645833f);
        }
        config.setAugmentedImageDatabase(aid);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (MediaPlayer mediaPlayer : mediaPlayers) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
    }
    @Override
    public void onViewCreated(ArFragment arFragment, ArSceneView arSceneView) {
        // Currently, the tone-mapping should be changed to FILMIC
        // because with other tone-mapping operators except LINEAR
        // the inverseTonemapSRGB function in the materials can produce incorrect results.
        // The LINEAR tone-mapping cannot be used together with the inverseTonemapSRGB function.
        Renderer renderer = arSceneView.getRenderer();

        if (renderer != null) {
            renderer.getFilamentView().setColorGrading(
                    new ColorGrading.Builder()
                            .toneMapping(ColorGrading.ToneMapping.FILMIC)
                            .build(EngineInstance.getEngine().getFilamentEngine())
            );
        }
    }
    @Override
    public void onUpdate(FrameTime frameTime) {
        Frame frame = arFragment.getArSceneView().getArFrame();
        Collection<AugmentedImage> images = frame.getUpdatedTrackables(AugmentedImage.class);
        for (AugmentedImage image: images){
            if(image.getTrackingMethod()== AugmentedImage.TrackingMethod.FULL_TRACKING){
                for (Marker marker: markerList){
                    if (marker.getMarkerID() == Integer.parseInt(image.getName()) && scanned == false){
                        Anchor anchor = image.createAnchor(image.getCenterPose());
                        Log.i("posee",image.getCenterPose().toString());
                        List<Action> actionList = marker.getActionList();
                        List<ARContent> arContentList = actionList.get(0).getArContentList();
                        for (ARContent arContent : arContentList){
                            String URL = arContent.getURL();
                            char lastExtension = URL.charAt(URL.length()-1);
                            if (lastExtension=='4'){
                                createVideo(arContent,anchor,image);
                            }
                            else if (lastExtension=='3'){
                                createAudio(arContent,anchor,image);
                            }
                            else if(URL.equals("text")){
                                create2DText(arContent,anchor,image);
                            }
                            else if(lastExtension=='g'){
                                createImage(arContent,anchor,image);
                            } else if(lastExtension=='b'){
                                create3DModel(arContent,anchor,image);
                            }
                        }
                        ActionAdapter adapter = new ActionAdapter(LearnActivity.this,R.layout.line_button_action,actionList);
                        ListView listViewAction = findViewById(R.id.listView_button);
                        listViewAction.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        Button btnNewContent = findViewById(R.id.btn_newContent);
                        btnNewContent.setVisibility(View.VISIBLE);
                        Button btn_content = findViewById(R.id.btn_content);
                        btnNewContent.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                for (AnchorNode anchorNode: anchorNodeArrayList){
                                    arFragment.getArSceneView().getScene().removeChild(anchorNode);
                                    anchorNode.getAnchor().detach();
                                }
                                for (MediaPlayer mediaPlayer : mediaPlayers) {
                                    mediaPlayer.stop();
                                    mediaPlayer.reset();
                                }
                                anchorNodeArrayList.clear();
                                listViewAction.setVisibility(View.INVISIBLE);
                                btnNewContent.setVisibility(View.INVISIBLE);
                                btn_content.setVisibility(View.INVISIBLE);
                                ImageView imv_pause = findViewById(R.id.imv_pause);
                                ImageView imv_play = findViewById(R.id.imv_play);
                                ImageView imv_reset= findViewById(R.id.imv_reset);
                                imv_pause.setVisibility(View.INVISIBLE);
                                imv_play.setVisibility(View.INVISIBLE);
                                imv_reset.setVisibility(View.INVISIBLE);
                                for (MediaPlayer mediaPlayer : mediaPlayers) {
                                    mediaPlayer.stop();
                                    mediaPlayer.reset();
                                }
                                scanned = false;
                            }
                        });
                        btn_content.setVisibility(View.VISIBLE);
                        btn_content.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (listViewAction.getVisibility()==View.VISIBLE){
                                    listViewAction.setVisibility(View.INVISIBLE);
                                }else{
                                    listViewAction.setVisibility(View.VISIBLE);
                                }
                                listViewAction.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        for (AnchorNode anchorNode: anchorNodeArrayList){
                                            arFragment.getArSceneView().getScene().removeChild(anchorNode);
                                            anchorNode.getAnchor().detach();
                                        }
                                        anchorNodeArrayList.clear();
                                        for (MediaPlayer mediaPlayer : mediaPlayers) {
                                            mediaPlayer.stop();
                                            mediaPlayer.reset();
                                        }
                                        mediaPlayers.clear();
                                        ImageView imv_pause = findViewById(R.id.imv_pause);
                                        ImageView imv_play = findViewById(R.id.imv_play);
                                        ImageView imv_reset= findViewById(R.id.imv_reset);
                                        imv_pause.setVisibility(View.INVISIBLE);
                                        imv_play.setVisibility(View.INVISIBLE);
                                        imv_reset.setVisibility(View.INVISIBLE);
                                        // Render lai
                                        Action thisAction = actionList.get(position);
                                        List<ARContent> thisARContentList = thisAction.getArContentList();
                                        for (ARContent arContent : thisARContentList){
                                            String URL = arContent.getURL();
                                            char lastExtension = URL.charAt(URL.length()-1);
                                            if (lastExtension=='4'){
                                                createVideo(arContent,anchor,image);
                                            }
                                            else if (lastExtension=='3'){
                                                createAudio(arContent,anchor,image);
                                            }
                                            else if(URL.equals("text")){
                                                create2DText(arContent,anchor,image);
                                            }
                                            else if(lastExtension=='g'){
                                                createImage(arContent,anchor,image);
                                            } else if(lastExtension == 'b'){
                                                create3DModel(arContent,anchor,image);
                                            }
                                        }
                                    }
                                });
                            }
                        });
                        Log.i("he", anchor.getPose().toString());
                        scanned = true;
                    }
                }
            }
        }
    }

    private void createAudio(ARContent arContent, Anchor anchor, AugmentedImage image) {
        AnchorNode anchorNode = setPropertyAnchorNode(arContent,anchor,image);
        ModelRenderable.builder()
                .setSource(this,Uri.parse("models/speaker.glb"))
                .setIsFilamentGltf(true)
                .build()
                .thenAccept(renderable -> placeAudio(renderable,anchorNode,arContent))
                .exceptionally(throwable -> {
                    Toast.makeText(this, "Unable to load model", Toast.LENGTH_LONG).show();
                    return null;
                });
    }
    private void placeAudio(ModelRenderable modelRenderable, AnchorNode anchorNode, ARContent arContent){
        modelRenderable.setShadowCaster(false);
        modelRenderable.setShadowReceiver(false);
        anchorNode.setRenderable(modelRenderable);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        anchorNode.setOnTapListener(((hitTestResult, motionEvent) -> {
            for (MediaPlayer mediaPlayer1 : mediaPlayers) {
                mediaPlayer1.stop();
                mediaPlayer1.reset();
            }
            for (ObjectAnimator objectAnimator1 : audioObjectAnimators) {
                objectAnimator1.pause();
            }
            MediaPlayer mediaPlayer = MediaPlayer.create(this, Uri.parse(arContent.getURL()));
            mediaPlayer.setLooping(true);
            mediaPlayers.add(mediaPlayer);
            ImageView imv_pause = findViewById(R.id.imv_pause);
            ImageView imv_play = findViewById(R.id.imv_play);
            ImageView imv_reset= findViewById(R.id.imv_reset);
            ObjectAnimator playSound = ModelAnimator.ofAnimation(anchorNode.getRenderableInstance(),0);
            audioObjectAnimators.add(playSound);
            imv_pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaPlayer.pause();
                    playSound.cancel();
                }
            });
            imv_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaPlayer.start();
                    playSound.start();
                }
            });
            imv_reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                    playSound.start();
                }
            });
            imv_pause.setVisibility(View.VISIBLE);
            imv_play.setVisibility(View.VISIBLE);
            imv_reset.setVisibility(View.VISIBLE);
//            anchorNode.getRenderableInstance().animate(true).start();
//            playSound.cancel();
        }));
        setRelationship(arContent,anchorNode);
        anchorNodeArrayList.add(anchorNode);
    }
    // render 3D model
    private void create3DModel(ARContent arContent, Anchor anchor, AugmentedImage image) {
        AnchorNode anchorNode = setPropertyAnchorNode(arContent,anchor,image);
        ModelRenderable.builder()
                .setSource(this,Uri.parse(arContent.getURL()))
                .setIsFilamentGltf(true)
                .build()
                .thenAccept(renderable -> place3DModel(renderable,anchorNode,arContent,anchor))
                .exceptionally(throwable -> {
                    Toast.makeText(this, "Unable to load model", Toast.LENGTH_LONG).show();
                    return null;
                });
    }
    private void place3DModel(ModelRenderable modelRenderable, AnchorNode anchorNode, ARContent arContent, Anchor anchor){
        modelRenderable.setShadowCaster(false);
        modelRenderable.setShadowReceiver(false);
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
        transformableNode.setParent(anchorNode);
        transformableNode.setRenderable(modelRenderable);
        transformableNode.select();
//        anchorNode.setRenderable(modelRenderable);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
//        transformableNode.getRenderableInstance().animate(true).start();
        if (arContent.getFatherARContent() != null){
            anchorNode.setEnabled(false);
            AnchorNodeFatherID anchorNodeFatherID = new AnchorNodeFatherID(anchorNode,arContent.getFatherARContent().getContentID());
            anchorNodeFatherIDList.add(anchorNodeFatherID);
        }
        setRelationship(arContent,anchorNode);
        anchorNodeArrayList.add(anchorNode);
    }

    private AnchorNode setPropertyAnchorNode(ARContent arContent, Anchor anchor, AugmentedImage image) {
        AnchorNode anchorNode = new AnchorNode();
//        // get world postition
        float[] translation = anchor.getPose().getTranslation();
        float[] rotation = anchor.getPose().getRotationQuaternion();
        Vector3 scale = new Vector3(arContent.getxScale(),arContent.getyScale(),arContent.getzScale());
        // cal rotation
        Quaternion worldQuaternion = new Quaternion(rotation[0],rotation[1],rotation[2],rotation[3]);
        Quaternion rotationX = Quaternion.axisAngle(new Vector3(1.0f, 0.0f, 0.0f), (arContent.getxRotation()*180)/(float)Math.PI);
        Quaternion rotationY = Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), (arContent.getyRotation()*180)/(float)Math.PI);
        Quaternion rotationZ = Quaternion.axisAngle(new Vector3(0.0f, 0.0f, 1.0f), (arContent.getzRotation()*180)/(float)Math.PI);
        Quaternion combineRotationX = Quaternion.multiply(worldQuaternion,rotationX);
        Quaternion combineRotationY = Quaternion.multiply(combineRotationX,rotationY);
        Quaternion combineRotationZ = Quaternion.multiply(combineRotationY,rotationZ);
        float[] worldPosition = new float[]{translation[0],translation[1],translation[2]};
        float[] worldRotation = new float[]{rotation[0],rotation[1],rotation[2],rotation[3]};
        Pose pose1 = new Pose(worldPosition,worldRotation);
        float[] vc = new float[]{arContent.getxPosition(),arContent.getyPosition(),arContent.getzPosition()};
        float[] vc1 = new float[]{1,1,1};
        vc1 = pose1.transformPoint(vc);
        float[] rollPosition = new float[]{vc1[0],vc1[1],vc1[2]};
        float[] rollRotation = new float[]{combineRotationZ.x,combineRotationZ.y,combineRotationZ.z,combineRotationZ.w};
        Pose pose2 = new Pose(rollPosition,rollRotation);
        Anchor anchor1 = image.createAnchor(pose2);
        anchorNode.setWorldScale(scale);
        anchorNode.setAnchor(anchor1);
        return anchorNode;
    }
    private AnchorNode setImageAnchorNode(ARContent arContent, Anchor anchor, AugmentedImage image) {
        AnchorNode anchorNode = new AnchorNode();
//        // get world postition
        float[] translation = anchor.getPose().getTranslation();
        float[] rotation = anchor.getPose().getRotationQuaternion();
        // cal rotation
        float deviceWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        float deviceHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        //get image size
        Bitmap imageFromURL = getBitmapFromURL(arContent.getURL());
        float imageWidth = imageFromURL.getWidth();
        float imageHeight = imageFromURL.getHeight();
        float dpi =  getResources().getDisplayMetrics().density;
        float applyScale = (float) ((250*dpi)/3779.5275591);
        Vector3 scale = new Vector3(arContent.getxScale()*applyScale,arContent.getyScale()*applyScale,arContent.getzScale()*applyScale);
        // cal rotation
        Quaternion worldQuaternion = new Quaternion(rotation[0],rotation[1],rotation[2],rotation[3]);
        Quaternion rotationX = Quaternion.axisAngle(new Vector3(1.0f, 0.0f, 0.0f), (arContent.getxRotation()*180)/(float)Math.PI);
        Quaternion rotationY = Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), (arContent.getyRotation()*180)/(float)Math.PI);
        Quaternion rotationZ = Quaternion.axisAngle(new Vector3(0.0f, 0.0f, 1.0f), (arContent.getzRotation()*180)/(float)Math.PI);
        Quaternion combineRotationX = Quaternion.multiply(worldQuaternion,rotationX);
        Quaternion combineRotationY = Quaternion.multiply(combineRotationX,rotationY);
        Quaternion combineRotationZ = Quaternion.multiply(combineRotationY,rotationZ);
        float[] worldPosition = new float[]{translation[0],translation[1],translation[2]};
        float[] worldRotation = new float[]{rotation[0],rotation[1],rotation[2],rotation[3]};
        Pose pose1 = new Pose(worldPosition,worldRotation);
        float[] vc = new float[]{arContent.getxPosition(),arContent.getyPosition(),arContent.getzPosition()};
        float[] vc1 = new float[]{1,1,1};
        vc1 = pose1.transformPoint(vc);
        float[] rollPosition = new float[]{vc1[0],vc1[1],vc1[2]};
        float[] rollRotation = new float[]{combineRotationZ.x,combineRotationZ.y,combineRotationZ.z,combineRotationZ.w};
        Pose pose2 = new Pose(rollPosition,rollRotation);
        Anchor anchor1 = image.createAnchor(pose2);
        anchorNode.setWorldScale(scale);
        anchorNode.setAnchor(anchor1);
        return anchorNode;
    }
    private void create2DText(ARContent arContent, Anchor anchor, AugmentedImage image) {
        AnchorNode anchorNode = setPropertyAnchorNode(arContent,anchor,image);
        ViewRenderable
                .builder()
                .setView(LearnActivity.this,R.layout.custom_text)
                .setVerticalAlignment(ViewRenderable.VerticalAlignment.CENTER)
                .setHorizontalAlignment(ViewRenderable.HorizontalAlignment.CENTER)
                .build()
                .thenAccept(viewRenderable -> placeTextView(viewRenderable,anchorNode,arContent));
    }
    private void placeTextView(ViewRenderable viewRenderable, AnchorNode anchorNode,ARContent arContent) {
        viewRenderable.setShadowCaster(false);
        viewRenderable.setShadowReceiver(false);
        View viewCustomText = viewRenderable.getView();
        TextView txvCustom = viewCustomText.findViewById(R.id.txv_customText);
        // Set custom text
        TextARContent textARContent = arContent.getTextARContent();
        txvCustom.setText(textARContent.getText());
        txvCustom.setTextColor(Color.parseColor(textARContent.getColor()));
        if(textARContent.getIsTransparent()==1){
            txvCustom.setBackgroundColor(Color.TRANSPARENT);
        }else{
            txvCustom.setBackgroundColor(Color.parseColor(textARContent.getBackgroundColor()));
        }
        float textSize = textARContent.getSize();
        float scaleText = textSize/20;
        float px20dp = dpFromPx(this,20);
        txvCustom.setTextSize(px20dp);
        int paddingTop = (int)20/2;
        int paddingLeft = (int) 20/2*5/4;
        txvCustom.setPadding(paddingLeft,paddingTop,paddingLeft,paddingTop);
        if(textARContent.getAlignment().equals("left")){
            txvCustom.setGravity(Gravity.LEFT);
        } else if(textARContent.getAlignment().equals("right")){
            txvCustom.setGravity(Gravity.RIGHT);
        } else if (textARContent.getAlignment().equals("center")){
            txvCustom.setGravity(Gravity.CENTER);
        }
        float dpi =  getResources().getDisplayMetrics().density;
        float applyScale = (float) ((250*dpi)/3779.5275591);
        Vector3 scale = new Vector3(arContent.getxScale()*applyScale*scaleText,arContent.getyScale()*applyScale*scaleText,arContent.getzScale()*applyScale*scaleText);
        anchorNode.setWorldScale(scale);
//        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
//        transformableNode.setParent(anchorNode);
//        transformableNode.setRenderable(modelRenderable);
//        transformableNode.select();
        anchorNode.setRenderable(modelRenderable);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        anchorNode.setRenderable(viewRenderable);
        anchorNodeArrayList.add(anchorNode);
        setRelationship(arContent,anchorNode);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
    }

    private void setRelationship(ARContent arContent, AnchorNode anchorNode) {
        if (arContent.getFatherARContent() != null){
            anchorNode.setEnabled(false);
            AnchorNodeFatherID anchorNodeFatherID = new AnchorNodeFatherID(anchorNode,arContent.getFatherARContent().getContentID());
            anchorNodeFatherIDList.add(anchorNodeFatherID);
        }
        char filenametail = arContent.getFilename().charAt(arContent.getFilename().length()-1);
        char mp4filenametail = '4';
        char mp3filenametail = '3';
        if(arContent.getFatherARContent()==null){
            if(Character.compare(filenametail,mp4filenametail)!=0&&Character.compare(filenametail,mp3filenametail)!=0)
            {
                anchorNode.setOnTapListener(((hitTestResult, motionEvent) -> {
                    if(arContent.isChildHidden()==false){
                        anchorNodeFatherIDList.forEach(anchorNodeFatherID -> {
                            if (anchorNodeFatherID.getFatherID() == arContent.getContentID()){
                                anchorNodeFatherID.getAnchorNode().setEnabled(true);
                            }
                        });
                        arContent.setChildHidden(true);
                    }else{
                        anchorNodeFatherIDList.forEach(anchorNodeFatherID -> {
                            if (anchorNodeFatherID.getFatherID() == arContent.getContentID()){
                                anchorNodeFatherID.getAnchorNode().setEnabled(false);
                            }
                        });
                        arContent.setChildHidden(false);
                    }
                }));
            }
        }
    }

    private void createImage(ARContent arContent, Anchor anchor, AugmentedImage image) {
        AnchorNode anchorNode = setImageAnchorNode(arContent,anchor,image);
        ViewRenderable
                .builder()
                .setView(LearnActivity.this,R.layout.custom_image)
                .setVerticalAlignment(ViewRenderable.VerticalAlignment.CENTER)
                .setHorizontalAlignment(ViewRenderable.HorizontalAlignment.CENTER)
                .build()
                .thenAccept(viewRenderable -> placeImage(viewRenderable,anchorNode,arContent));
    }
    private void placeImage(ViewRenderable viewRenderable, AnchorNode anchorNode, ARContent arContent) {
        viewRenderable.setShadowCaster(false);
        viewRenderable.setShadowReceiver(false);
        View viewCustomImage = viewRenderable.getView();
        ImageView imvCustom = viewCustomImage.findViewById(R.id.imv_customeImage);
        Bitmap bitmap = getBitmapFromURL(arContent.getURL());
        // calculate scale. img = 300dpx300dp
        float viewImageWidth = pxFromDp(this,300);
        float viewImageHeight = viewImageWidth;
        float imageWidth = bitmap.getWidth();
        float imageHeight = bitmap.getHeight();
        float scaleImage = imageHeight/imageWidth;
        float scaleImageAndView = imageWidth/viewImageWidth;
        // get scale
        Vector3 anchorNodeScale = anchorNode.getWorldScale();
        Vector3 newScale = new Vector3(anchorNodeScale.x*scaleImageAndView,anchorNodeScale.y*scaleImageAndView,anchorNodeScale.z*scaleImageAndView);
        anchorNode.setWorldScale(new Vector3(newScale));
        imvCustom.requestLayout();
        imvCustom.getLayoutParams().width = (int) viewImageWidth;
        imvCustom.getLayoutParams().height = (int) (viewImageWidth*scaleImage);
        imvCustom.setImageBitmap(bitmap);

//        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
//        transformableNode.setParent(anchorNode);
//        transformableNode.setRenderable(modelRenderable);
//        transformableNode.select();
        anchorNode.setRenderable(viewRenderable);
        anchorNodeArrayList.add(anchorNode);
        setRelationship(arContent,anchorNode);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
    }
    private void createVideo(ARContent arContent, Anchor anchor,AugmentedImage image){
        AnchorNode anchorNode = setPropertyAnchorNode(arContent,anchor,image);
        ModelRenderable.builder()
                .setSource(this, Uri.parse("models/vertical_plane_1920x1080_center.glb"))
                .setIsFilamentGltf(true)
                .build()
                .thenAccept(model -> placeVideo(model,anchorNode,arContent))
                .exceptionally(
                        throwable -> {
                            Toast.makeText(this, "Unable to load cmm", Toast.LENGTH_LONG).show();
                            return null;
                        });
    }

    private void placeVideo(ModelRenderable renderable, AnchorNode anchorNode, ARContent arContent) {
        renderable.setShadowCaster(false);
        renderable.setShadowReceiver(false);

        createVideoMaterial();

        ExternalTexture externalTexture = new ExternalTexture();
        RenderableInstance renderableInstance;
        anchorNode.setRenderable(renderable);
        renderableInstance = anchorNode.getRenderableInstance();
        Vector3 anchorNodeScale = anchorNode.getWorldScale();
        Vector3 newScale = new Vector3(anchorNodeScale.x*0.26f,anchorNodeScale.y*0.26f,anchorNodeScale.z*0.26f);
        anchorNode.setWorldScale(new Vector3(newScale));

        renderableInstance.setMaterial(plainVideoMaterial);
        renderableInstance.getMaterial().setExternalTexture("videoTexture", externalTexture);
        anchorNodeArrayList.add(anchorNode);
        anchorNode.setOnTapListener(((hitTestResult, motionEvent) -> {
            for (MediaPlayer mediaPlayer1 : mediaPlayers) {
                mediaPlayer1.stop();
                mediaPlayer1.reset();
            }
            MediaPlayer mediaPlayer = MediaPlayer.create(LearnActivity.this, Uri.parse(arContent.getURL()));
            mediaPlayer.setLooping(true);
            mediaPlayer.setSurface(externalTexture.getSurface());
            mediaPlayer.setSurface(externalTexture.getSurface());
            mediaPlayers.add(mediaPlayer);
            ImageView imv_pause = findViewById(R.id.imv_pause);
            ImageView imv_play = findViewById(R.id.imv_play);
            ImageView imv_reset= findViewById(R.id.imv_reset);
            imv_pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaPlayer.pause();
                }
            });
            imv_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaPlayer.start();
                }
            });
            imv_reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                }
            });
            imv_pause.setVisibility(View.VISIBLE);
            imv_play.setVisibility(View.VISIBLE);
            imv_reset.setVisibility(View.VISIBLE);
        }));
        anchorNode.setParent(arFragment.getArSceneView().getScene());
    }

    private void createVideoMaterial() {
        Engine filamentEngine = EngineInstance.getEngine().getFilamentEngine();
        MaterialBuilder.init();
        MaterialBuilder materialBuilder = new MaterialBuilder()
                // By default, materials are generated only for DESKTOP. Since we're an Android
                // app, we set the platform to MOBILE.
                .platform(MaterialBuilder.Platform.MOBILE)
                .name("Plain Video Material")
                .require(MaterialBuilder.VertexAttribute.UV0)
                // Defaults to UNLIT because it's the only emissive one
                .shading(MaterialBuilder.Shading.UNLIT)
                .doubleSided(true)
                .samplerParameter(MaterialBuilder.SamplerType.SAMPLER_EXTERNAL, MaterialBuilder.SamplerFormat.FLOAT
                        , MaterialBuilder.SamplerPrecision.DEFAULT, "videoTexture")
                .optimization(MaterialBuilder.Optimization.NONE);

        // When compiling more than one material variant, it is more efficient to pass an Engine
        // instance to reuse the Engine's job system
        MaterialPackage plainVideoMaterialPackage = materialBuilder
                .blending(MaterialBuilder.BlendingMode.OPAQUE)
                .material("void material(inout MaterialInputs material) {\n" +
                        "    prepareMaterial(material);\n" +
                        "    material.baseColor = texture(materialParams_videoTexture, getUV0()).rgba;\n" +
                        "}\n")
                .build(filamentEngine);
        if (plainVideoMaterialPackage.isValid()) {
            ByteBuffer buffer = plainVideoMaterialPackage.getBuffer();
            Material.builder()
                    .setSource(buffer)
                    .build()
                    .thenAccept(material -> {
                            plainVideoMaterial = material;
                    })
                    .exceptionally(
                            throwable -> {
                                Toast.makeText(this, "Unable to load material", Toast.LENGTH_LONG).show();
                                return null;
                            });
        }
        MaterialBuilder.shutdown();
    }

    public static Bitmap getBitmapFromURL(String src){
        try {
            URL url = new URL(src);

            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            return null;
        }
    }

    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}