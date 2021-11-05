/*
 * Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.ar.core.examples.java.helloar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
//import android.graphics.Matrix;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ammarptn.debug.gdrive.lib.GDriveDebugViewActivity;
import com.ammarptn.gdriverest.DriveServiceHelper;
import com.ammarptn.gdriverest.GoogleDriveFileHolder;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import static com.ammarptn.gdriverest.DriveServiceHelper.getGoogleDriveService;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Camera;
import com.google.ar.core.CameraIntrinsics;
import com.google.ar.core.Config;
import com.google.ar.core.Config.InstantPlacementMode;
import com.google.ar.core.DepthPoint;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.InstantPlacementPoint;
import com.google.ar.core.LightEstimate;
import com.google.ar.core.Plane;
import com.google.ar.core.Point;
import com.google.ar.core.Point.OrientationMode;
import com.google.ar.core.PointCloud;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingFailureReason;
import com.google.ar.core.TrackingState;
import com.google.ar.core.examples.java.common.helpers.CameraPermissionHelper;
import com.google.ar.core.examples.java.common.helpers.DepthSettings;
import com.google.ar.core.examples.java.common.helpers.DisplayRotationHelper;
import com.google.ar.core.examples.java.common.helpers.FullScreenHelper;
import com.google.ar.core.examples.java.common.helpers.InstantPlacementSettings;
import com.google.ar.core.examples.java.common.helpers.SnackbarHelper;
import com.google.ar.core.examples.java.common.helpers.TapHelper;
import com.google.ar.core.examples.java.common.helpers.TrackingStateHelper;
import com.google.ar.core.examples.java.common.samplerender.Framebuffer;
import com.google.ar.core.examples.java.common.samplerender.GLError;
import com.google.ar.core.examples.java.common.samplerender.Mesh;
import com.google.ar.core.examples.java.common.samplerender.SampleRender;
import com.google.ar.core.examples.java.common.samplerender.Shader;
import com.google.ar.core.examples.java.common.samplerender.Texture;
import com.google.ar.core.examples.java.common.samplerender.VertexBuffer;
import com.google.ar.core.examples.java.common.samplerender.arcore.BackgroundRenderer;
import com.google.ar.core.examples.java.common.samplerender.arcore.PlaneRenderer;
import com.google.ar.core.examples.java.common.samplerender.arcore.SpecularCubemapFilter;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.NotYetAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;
import java.util.List;
import android.graphics.YuvImage;
import android.media.MediaPlayer.TrackInfo;

/**
 * This is a simple example that shows how to create an augmented reality (AR) application using the
 * ARCore API. The application will display any detected planes and will allow the user to tap on a
 * plane to place a 3D model.
 */
public class HelloArActivity extends AppCompatActivity implements SampleRender.Renderer {
  private static final String TAG = HelloArActivity.class.getSimpleName();
  private TextView email;
  private static final String SEARCHING_PLANE_MESSAGE = "Searching for surfaces...";
  private static final String WAITING_FOR_TAP_MESSAGE = "Tap on a surface to place an object.";
  private int count1=0;
  // See the definition of updateSphericalHarmonicsCoefficients for an explanation of these
  // constants.
  private static final float[] sphericalHarmonicFactors = {
          0.282095f,
          -0.325735f,
          0.325735f,
          -0.325735f,
          0.273137f,
          -0.273137f,
          0.078848f,
          -0.273137f,
          0.136569f,
  };

  private static final float Z_NEAR = 0.1f;
  private static final float Z_FAR = 100f;

  private static final int CUBEMAP_RESOLUTION = 16;
  private static final int CUBEMAP_NUMBER_OF_IMPORTANCE_SAMPLES = 32;

  // Rendering. The Renderers are created here, and initialized when the GL surface is created.
  private GLSurfaceView surfaceView;

  private boolean installRequested;
  Set<String> uploaded = new HashSet<String> ();
  private Session session;
  private final SnackbarHelper messageSnackbarHelper = new SnackbarHelper();
  private DisplayRotationHelper displayRotationHelper;
  private final TrackingStateHelper trackingStateHelper = new TrackingStateHelper(this);
  private TapHelper tapHelper;
  private SampleRender render;

  private PlaneRenderer planeRenderer;
  private BackgroundRenderer backgroundRenderer;
  private Framebuffer virtualSceneFramebuffer;
  private boolean hasSetTextureNames = false;

  private static final int REQUEST_CODE_SIGN_IN = 100;
  private GoogleSignInClient mGoogleSignInClient;
  private DriveServiceHelper mDriveServiceHelper;
  private Button login;

  private LinearLayout gDriveAction;

  private final DepthSettings depthSettings = new DepthSettings();
  private boolean[] depthSettingsMenuDialogCheckboxes = new boolean[2];

  private final InstantPlacementSettings instantPlacementSettings = new InstantPlacementSettings();
  private boolean[] instantPlacementSettingsMenuDialogCheckboxes = new boolean[1];
  // Assumed distance from the device camera to the surface on which user will try to place objects.
  // This value affects the apparent scale of objects while the tracking method of the
  // Instant Placement point is SCREENSPACE_WITH_APPROXIMATE_DISTANCE.
  // Values in the [0.2, 2.0] meter range are a good choice for most AR experiences. Use lower
  // values for AR experiences where users are expected to place objects on surfaces close to the
  // camera. Use larger values for experiences where the user will likely be standing and trying to
  // place an object on the ground or floor in front of them.
  private static final float APPROXIMATE_DISTANCE_METERS = 2.0f;

  // Point Cloud
  private VertexBuffer pointCloudVertexBuffer;
  private Mesh pointCloudMesh;
  private Shader pointCloudShader;
  // Keep track of the last point cloud rendered to avoid updating the VBO if point cloud
  // was not changed.  Do this using the timestamp since we can't compare PointCloud objects.
  private long lastPointCloudTimestamp = 0;

  // Virtual object (ARCore pawn)
  private Mesh virtualObjectMesh;
  private Shader virtualObjectShader;
  private final ArrayList<Anchor> anchors = new ArrayList<>();

  // Environmental HDR
  private Texture dfgTexture;
  private SpecularCubemapFilter cubemapFilter;

  // Temporary matrix allocated here to reduce number of allocations for each frame.
  private final float[] modelMatrix = new float[16];
  private final float[] viewMatrix = new float[16];
  private final float[] projectionMatrix = new float[16];
  private final float[] modelViewMatrix = new float[16]; // view x model
  private final float[] modelViewProjectionMatrix = new float[16]; // projection x view x model
  private final float[] sphericalHarmonicsCoefficients = new float[9 * 3];
  private final float[] viewInverseMatrix = new float[16];
  private final float[] worldLightDirection = {0.0f, 0.0f, 0.0f, 0.0f};
  private final float[] viewLightDirection = new float[4]; // view x world light direction

  //writing to a text file
  public int Number;
  public int NumberP;

  @Override
  protected void onStart() {
    super.onStart();
    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
    if (account == null) {
      //signIn();
    } else {
      email.setText(account.getEmail());
      mDriveServiceHelper = new DriveServiceHelper(getGoogleDriveService(getApplicationContext(), account, "appName"));
    }
  }

  private void signIn() {

    mGoogleSignInClient = buildGoogleSignInClient();
    startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
  }

  private GoogleSignInClient buildGoogleSignInClient() {
    GoogleSignInOptions signInOptions =
            new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestScopes(Drive.SCOPE_FILE)
                    .requestEmail()
                    .build();
    return GoogleSignIn.getClient(getApplicationContext(), signInOptions);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
    switch (requestCode) {
      case REQUEST_CODE_SIGN_IN:
        if (resultCode == Activity.RESULT_OK && resultData != null) {
          handleSignInResult(resultData);
        }
        break;
    }
    super.onActivityResult(requestCode, resultCode, resultData);
  }

  public void test() {
    System.out.println("test");
  }

  private void handleSignInResult(Intent result) {
    GoogleSignIn.getSignedInAccountFromIntent(result)
            .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
              @Override
              public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                Log.d(TAG, "Signed in as " + googleSignInAccount.getEmail());
                email.setText(googleSignInAccount.getEmail());

                mDriveServiceHelper = new DriveServiceHelper(getGoogleDriveService(getApplicationContext(), googleSignInAccount, "appName"));

                Log.d(TAG, "handleSignInResult: " + mDriveServiceHelper);
              }
            })
            .addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Unable to sign in.", e);
              }
            });
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    surfaceView = findViewById(R.id.surfaceview);
    displayRotationHelper = new DisplayRotationHelper(/*context=*/ this);
    initView();

    //writing pose to a text file
    File root = new File(Environment.getExternalStorageDirectory(),"My Foleder");


    // Set up touch listener.
    tapHelper = new TapHelper(/*context=*/ this);
    surfaceView.setOnTouchListener(tapHelper);

    // Set up renderer.
    render = new SampleRender(surfaceView, this, getAssets());

    installRequested = false;

    depthSettings.onCreate(this);
    instantPlacementSettings.onCreate(this);
    ImageButton settingsButton = findViewById(R.id.settings_button);
    settingsButton.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                PopupMenu popup = new PopupMenu(HelloArActivity.this, v);
                popup.setOnMenuItemClickListener(HelloArActivity.this::settingsMenuClick);
                popup.inflate(R.menu.settings_menu);
                popup.show();
              }
            });
  }

  private void initView() {
    email = findViewById(R.id.email);
    //gDriveAction = findViewById(com.ammarptn.gdriverest.R.id.g_drive_action);
  }
  /** Menu button to launch feature specific settings. */
  protected boolean settingsMenuClick(MenuItem item) {
    if (item.getItemId() == R.id.depth_settings) {
      launchDepthSettingsMenuDialog();
      return true;
    } else if (item.getItemId() == R.id.instant_placement_settings) {
      launchInstantPlacementSettingsMenuDialog();
      return true;
    }
    return false;
  }

  public void onClick() {

    if (mDriveServiceHelper == null) {
      return;
    }
    try{
      System.out.println("______________________**********************************************************************************_______________");
      File directoryPath = new File("/data/data/com.google.ar.core.examples.java.helloar/files/Images");
      //List of all files and directories
      String contents[] = directoryPath.list();
      System.out.println("List of files and directories in the specified directory:");
      for(int i=0; i<contents.length; i++) {
        System.out.println(contents[i]);
        String imgpath = "/data/data/com.google.ar.core.examples.java.helloar/files/Images/" + i + ".jpg";
        String txtpath = "/data/data/com.google.ar.core.examples.java.helloar/files/Pose/" + i + ".txt";
        if(!uploaded.contains(contents[i])){
          mDriveServiceHelper.uploadFile(new java.io.File(imgpath), "image/jpeg", "1Khh325NyLnwuXasucfMfB5Wm-unRj6l5")
          //mDriveServiceHelper.uploadFile(new java.io.File("/data/data/com.google.ar.core.examples.java.helloar/files/Images/0.jpg"), "image/jpeg", "1Khh325NyLnwuXasucfMfB5Wm-unRj6l5")
                  //mDriveServiceHelper.uploadFile(new java.io.File(getApplicationContext().getFilesDir(), "dummy.txt"), "text/plain", null)
                  .addOnSuccessListener(new OnSuccessListener<GoogleDriveFileHolder>() {
                    @Override
                    public void onSuccess(GoogleDriveFileHolder googleDriveFileHolder) {
                      Gson gson = new Gson();
                      Log.d(TAG, "onSuccess: " + gson.toJson(googleDriveFileHolder));
                    }
                  })
                  .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                      Log.d(TAG, "onFailure: " + e.getMessage());
                    }
                  });
          mDriveServiceHelper.uploadFile(new java.io.File(txtpath), "text/plain", "1ar4509kEIyDg2PZ7B4qgqeSQioH7LOf5")
          //mDriveServiceHelper.uploadFile(new java.io.File("/data/data/com.google.ar.core.examples.java.helloar/files/Pose/0.txt"), "text/plain", "1ar4509kEIyDg2PZ7B4qgqeSQioH7LOf5")
                  //mDriveServiceHelper.uploadFile(new java.io.File(getApplicationContext().getFilesDir(), "dummy.txt"), "text/plain", null)
                  .addOnSuccessListener(new OnSuccessListener<GoogleDriveFileHolder>() {
                    @Override
                    public void onSuccess(GoogleDriveFileHolder googleDriveFileHolder) {
                      Gson gson = new Gson();
                      Log.d(TAG, "onSuccess: " + gson.toJson(googleDriveFileHolder));
                    }
                  })
                  .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                      Log.d(TAG, "onFailure: " + e.getMessage());
                    }
                  });
        }
        uploaded.add(contents[i]);
      }
    }

    catch(Exception e){
      System.out.println("*************************_________________________****************");
      System.out.println("In the errror");
    }


    /*mDriveServiceHelper.uploadFile(new java.io.File("/data/data/com.google.ar.core.examples.java.helloar/files/Images/0.jpg"), "image/jpeg", "1Khh325NyLnwuXasucfMfB5Wm-unRj6l5")
            //mDriveServiceHelper.uploadFile(new java.io.File(getApplicationContext().getFilesDir(), "dummy.txt"), "text/plain", null)
            .addOnSuccessListener(new OnSuccessListener<GoogleDriveFileHolder>() {
              @Override
              public void onSuccess(GoogleDriveFileHolder googleDriveFileHolder) {
                Gson gson = new Gson();
                Log.d(TAG, "onSuccess: " + gson.toJson(googleDriveFileHolder));
              }
            })
            .addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
              }
            });
    mDriveServiceHelper.uploadFile(new java.io.File("/data/data/com.google.ar.core.examples.java.helloar/files/Pose/0.txt"), "text/plain", "1ar4509kEIyDg2PZ7B4qgqeSQioH7LOf5")
            //mDriveServiceHelper.uploadFile(new java.io.File(getApplicationContext().getFilesDir(), "dummy.txt"), "text/plain", null)
            .addOnSuccessListener(new OnSuccessListener<GoogleDriveFileHolder>() {
              @Override
              public void onSuccess(GoogleDriveFileHolder googleDriveFileHolder) {
                Gson gson = new Gson();
                Log.d(TAG, "onSuccess: " + gson.toJson(googleDriveFileHolder));
              }
            })
            .addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
              }
            });*/
  }

  @Override
  protected void onDestroy() {
    if (session != null) {
      // Explicitly close ARCore Session to release native resources.
      // Review the API reference for important considerations before calling close() in apps with
      // more complicated lifecycle requirements:
      // https://developers.google.com/ar/reference/java/arcore/reference/com/google/ar/core/Session#close()
      session.close();
      session = null;
    }

    super.onDestroy();
  }

  @Override
  protected void onResume() {
    super.onResume();

    if (session == null) {
      Exception exception = null;
      String message = null;
      try {
        switch (ArCoreApk.getInstance().requestInstall(this, !installRequested)) {
          case INSTALL_REQUESTED:
            installRequested = true;
            return;
          case INSTALLED:
            break;
        }

        // ARCore requires camera permissions to operate. If we did not yet obtain runtime
        // permission on Android M and above, now is a good time to ask the user for it.
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
          CameraPermissionHelper.requestCameraPermission(this);
          return;
        }

        // Create the session.
        session = new Session(/* context= */ this);
      } catch (UnavailableArcoreNotInstalledException
              | UnavailableUserDeclinedInstallationException e) {
        message = "Please install ARCore";
        exception = e;
      } catch (UnavailableApkTooOldException e) {
        message = "Please update ARCore";
        exception = e;
      } catch (UnavailableSdkTooOldException e) {
        message = "Please update this app";
        exception = e;
      } catch (UnavailableDeviceNotCompatibleException e) {
        message = "This device does not support AR";
        exception = e;
      } catch (Exception e) {
        message = "Failed to create AR session";
        exception = e;
      }

      if (message != null) {
        messageSnackbarHelper.showError(this, message);
        Log.e(TAG, "Exception creating session", exception);
        return;
      }
    }

    // Note that order matters - see the note in onPause(), the reverse applies here.
    try {
      configureSession();
      // To record a live camera session for later playback, call
      // `session.startRecording(recordingConfig)` at anytime. To playback a previously recorded AR
      // session instead of using the live camera feed, call
      // `session.setPlaybackDatasetUri(Uri)` before calling `session.resume()`. To
      // learn more about recording and playback, see:
      // https://developers.google.com/ar/develop/java/recording-and-playback
      session.resume();
    } catch (CameraNotAvailableException e) {
      messageSnackbarHelper.showError(this, "Camera not available. Try restarting the app.");
      session = null;
      return;
    }

    surfaceView.onResume();
    displayRotationHelper.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
    if (session != null) {
      // Note that the order matters - GLSurfaceView is paused first so that it does not try
      // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
      // still call session.update() and get a SessionPausedException.
      displayRotationHelper.onPause();
      surfaceView.onPause();
      session.pause();
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
    super.onRequestPermissionsResult(requestCode, permissions, results);
    if (!CameraPermissionHelper.hasCameraPermission(this)) {
      // Use toast instead of snackbar here since the activity will exit.
      Toast.makeText(this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
              .show();
      if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
        // Permission denied with checking "Do not ask again".
        CameraPermissionHelper.launchPermissionSettings(this);
      }
      finish();
    }
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus);
  }

  @Override
  public void onSurfaceCreated(SampleRender render) {
    // Prepare the rendering objects. This involves reading shaders and 3D model files, so may throw
    // an IOException.
    try {
      planeRenderer = new PlaneRenderer(render);
      backgroundRenderer = new BackgroundRenderer(render);
      virtualSceneFramebuffer = new Framebuffer(render, /*width=*/ 1, /*height=*/ 1);

      cubemapFilter =
              new SpecularCubemapFilter(
                      render, CUBEMAP_RESOLUTION, CUBEMAP_NUMBER_OF_IMPORTANCE_SAMPLES);
      // Load DFG lookup table for environmental lighting
      dfgTexture =
              new Texture(
                      render,
                      Texture.Target.TEXTURE_2D,
                      Texture.WrapMode.CLAMP_TO_EDGE,
                      /*useMipmaps=*/ false);
      // The dfg.raw file is a raw half-float texture with two channels.
      final int dfgResolution = 64;
      final int dfgChannels = 2;
      final int halfFloatSize = 2;

      ByteBuffer buffer =
              ByteBuffer.allocateDirect(dfgResolution * dfgResolution * dfgChannels * halfFloatSize);
      try (InputStream is = getAssets().open("models/dfg.raw")) {
        is.read(buffer.array());
      }
      // SampleRender abstraction leaks here.
      GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, dfgTexture.getTextureId());
      GLError.maybeThrowGLException("Failed to bind DFG texture", "glBindTexture");
      GLES30.glTexImage2D(
              GLES30.GL_TEXTURE_2D,
              /*level=*/ 0,
              GLES30.GL_RG16F,
              /*width=*/ dfgResolution,
              /*height=*/ dfgResolution,
              /*border=*/ 0,
              GLES30.GL_RG,
              GLES30.GL_HALF_FLOAT,
              buffer);
      GLError.maybeThrowGLException("Failed to populate DFG texture", "glTexImage2D");

      // Point cloud
      pointCloudShader =
              Shader.createFromAssets(
                      render, "shaders/point_cloud.vert", "shaders/point_cloud.frag", /*defines=*/ null)
                      .setVec4(
                              "u_Color", new float[] {31.0f / 255.0f, 188.0f / 255.0f, 210.0f / 255.0f, 1.0f})
                      .setFloat("u_PointSize", 5.0f);
      // four entries per vertex: X, Y, Z, confidence
      pointCloudVertexBuffer =
              new VertexBuffer(render, /*numberOfEntriesPerVertex=*/ 4, /*entries=*/ null);
      final VertexBuffer[] pointCloudVertexBuffers = {pointCloudVertexBuffer};
      pointCloudMesh =
              new Mesh(
                      render, Mesh.PrimitiveMode.POINTS, /*indexBuffer=*/ null, pointCloudVertexBuffers);

      // Virtual object to render (ARCore pawn)
      Texture virtualObjectAlbedoTexture =
              Texture.createFromAsset(
                      render,
                      "models/pawn_albedo.png",
                      Texture.WrapMode.CLAMP_TO_EDGE,
                      Texture.ColorFormat.SRGB);
      Texture virtualObjectPbrTexture =
              Texture.createFromAsset(
                      render,
                      "models/pawn_roughness_metallic_ao.png",
                      Texture.WrapMode.CLAMP_TO_EDGE,
                      Texture.ColorFormat.LINEAR);
      virtualObjectMesh = Mesh.createFromAsset(render, "models/pawn.obj");
      virtualObjectShader =
              Shader.createFromAssets(
                      render,
                      "shaders/environmental_hdr.vert",
                      "shaders/environmental_hdr.frag",
                      /*defines=*/ new HashMap<String, String>() {
                        {
                          put(
                                  "NUMBER_OF_MIPMAP_LEVELS",
                                  Integer.toString(cubemapFilter.getNumberOfMipmapLevels()));
                        }
                      })
                      .setTexture("u_AlbedoTexture", virtualObjectAlbedoTexture)
                      .setTexture("u_RoughnessMetallicAmbientOcclusionTexture", virtualObjectPbrTexture)
                      .setTexture("u_Cubemap", cubemapFilter.getFilteredCubemapTexture())
                      .setTexture("u_DfgTexture", dfgTexture);
    } catch (IOException e) {
      Log.e(TAG, "Failed to read a required asset file", e);
      messageSnackbarHelper.showError(this, "Failed to read a required asset file: " + e);
    }
  }

  @Override
  public void onSurfaceChanged(SampleRender render, int width, int height) {
    displayRotationHelper.onSurfaceChanged(width, height);
    virtualSceneFramebuffer.resize(width, height);
  }

//  private void initView() {
//    email = findViewById(com.google.ar.core.examples.java.R.id.email);
//  }

  @Override
  public void onDrawFrame(SampleRender render) {
    if (session == null) {
      return;
    }

    // Texture names should only be set once on a GL thread unless they change. This is done during
    // onDrawFrame rather than onSurfaceCreated since the session is not guaranteed to have been
    // initialized during the execution of onSurfaceCreated.
    if (!hasSetTextureNames) {
      session.setCameraTextureNames(
              new int[] {backgroundRenderer.getCameraColorTexture().getTextureId()});
      hasSetTextureNames = true;
    }

    // -- Update per-frame state

    // Notify ARCore session that the view size changed so that the perspective matrix and
    // the video background can be properly adjusted.
    displayRotationHelper.updateSessionIfNeeded(session);

    // Obtain the current frame from ARSession. When the configuration is set to
    // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
    // camera framerate.
    Frame frame;
    try {
      frame = session.update();
    } catch (CameraNotAvailableException e) {
      Log.e(TAG, "Camera not available during onDrawFrame", e);
      messageSnackbarHelper.showError(this, "Camera not available. Try restarting the app.");
      return;
    }
    Camera camera = frame.getCamera();

    // Update BackgroundRenderer state to match the depth settings.
    try {
      backgroundRenderer.setUseDepthVisualization(
              render, depthSettings.depthColorVisualizationEnabled());
      backgroundRenderer.setUseOcclusion(render, depthSettings.useDepthForOcclusion());
    } catch (IOException e) {
      Log.e(TAG, "Failed to read a required asset file", e);
      messageSnackbarHelper.showError(this, "Failed to read a required asset file: " + e);
      return;
    }
    // BackgroundRenderer.updateDisplayGeometry must be called every frame to update the coordinates
    // used to draw the background camera image.
    backgroundRenderer.updateDisplayGeometry(frame);

    if (camera.getTrackingState() == TrackingState.TRACKING
            && (depthSettings.useDepthForOcclusion()
            || depthSettings.depthColorVisualizationEnabled())) {
      try (Image depthImage = frame.acquireDepthImage()) {
        backgroundRenderer.updateCameraDepthTexture(depthImage);
      } catch (NotYetAvailableException e) {
        // This normally means that depth data is not available yet. This is normal so we will not
        // spam the logcat with this.
      }
    }

    // Handle one tap per frame.
    handleTap(frame, camera);

    // Keep the screen unlocked while tracking, but allow it to lock when tracking stops.
    trackingStateHelper.updateKeepScreenOnFlag(camera.getTrackingState());

    // Show a message based on whether tracking has failed, if planes are detected, and if the user
    // has placed any objects.
    String message = null;
    if (camera.getTrackingState() == TrackingState.PAUSED) {
      if (camera.getTrackingFailureReason() == TrackingFailureReason.NONE) {
        message = SEARCHING_PLANE_MESSAGE;
      } else {
        message = TrackingStateHelper.getTrackingFailureReasonString(camera);
      }
    } else if (hasTrackingPlane()) {
      if (anchors.isEmpty()) {
        message = WAITING_FOR_TAP_MESSAGE;
      }
    } else {
      message = SEARCHING_PLANE_MESSAGE;
    }
    if (message == null) {
      messageSnackbarHelper.hide(this);
    } else {
      messageSnackbarHelper.showMessage(this, message);
    }

    // -- Draw background

    if (frame.getTimestamp() != 0) {
      // Suppress rendering if the camera did not produce the first frame yet. This is to avoid
      // drawing possible leftover data from previous sessions if the texture is reused.
      backgroundRenderer.drawBackground(render);
    }

    // If not tracking, don't draw 3D objects.
    if (camera.getTrackingState() == TrackingState.PAUSED) {
      return;
    }

    // -- Draw non-occluded virtual objects (planes, point cloud)

    // Get projection matrix.
    camera.getProjectionMatrix(projectionMatrix, 0, Z_NEAR, Z_FAR);

    // Get camera matrix and draw.
    camera.getViewMatrix(viewMatrix, 0);

    // Visualize tracked points.
    // Use try-with-resources to automatically release the point cloud.
    try (PointCloud pointCloud = frame.acquirePointCloud()) {
      if (pointCloud.getTimestamp() > lastPointCloudTimestamp) {
        pointCloudVertexBuffer.set(pointCloud.getPoints());
        lastPointCloudTimestamp = pointCloud.getTimestamp();
      }
      Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
      pointCloudShader.setMat4("u_ModelViewProjection", modelViewProjectionMatrix);
      render.draw(pointCloudMesh, pointCloudShader);
    }

    // Visualize planes.
    planeRenderer.drawPlanes(
            render,
            session.getAllTrackables(Plane.class),
            camera.getDisplayOrientedPose(),
            projectionMatrix);

    // -- Draw occluded virtual objects

    // Update lighting parameters in the shader
    updateLightEstimation(frame.getLightEstimate(), viewMatrix);

    // Visualize anchors created by touch.
    render.clear(virtualSceneFramebuffer, 0f, 0f, 0f, 0f);
    for (Anchor anchor : anchors) {
      if (anchor.getTrackingState() != TrackingState.TRACKING) {
        continue;
      }

      // Get the current pose of an Anchor in world space. The Anchor pose is updated
      // during calls to session.update() as ARCore refines its estimate of the world.
      anchor.getPose().toMatrix(modelMatrix, 0);

      // Calculate model/view/projection matrices
      Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
      Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);

      // Update shader properties and draw
      virtualObjectShader.setMat4("u_ModelView", modelViewMatrix);
      virtualObjectShader.setMat4("u_ModelViewProjection", modelViewProjectionMatrix);
      render.draw(virtualObjectMesh, virtualObjectShader, virtualSceneFramebuffer);
    }

    // Compose the virtual scene with the background.
    backgroundRenderer.drawVirtualScene(render, virtualSceneFramebuffer, Z_NEAR, Z_FAR);
  }

  /********************************************************/
  private void writeToFile(String message)
  {
    try {
      @SuppressLint("WrongConstant") OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("todolist.txt",
              Context.MODE_PRIVATE));
      outputStreamWriter.write(message);
      outputStreamWriter.close();

    } catch(FileNotFoundException e)
    {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void writeFile(String message)
  {
    String FILENAME = String.valueOf(Number);
    FileOutputStream fos = null;

    try {
      fos = openFileOutput(FILENAME, MODE_PRIVATE);
      fos.write(message.getBytes());
      //Toast.makeText(this,"Saved to"+getFilesDir()+"/", Toast.LENGTH_SHORT).show();
    } catch(FileNotFoundException e) {
      e.printStackTrace();
      System.out.println("FileNotFound.........................");
    }
    catch(IOException e) {
      e.printStackTrace();
    }

    finally {
      if(fos!=null){
        try{
          fos.close();
        } catch(IOException e){
          e.printStackTrace();
        }
      }
    }

  }

  public void wrtieFileOnInternalStorage(Context mcoContext,String sFileName, String sBody){
    File file = new File(mcoContext.getFilesDir(),"mydir");
    if(!file.exists()){
      file.mkdir();
    }

    try{
      File gpxfile = new File(file, sFileName);
      FileWriter writer = new FileWriter(gpxfile);
      writer.append(sBody);
      writer.flush();
      writer.close();

    }catch (Exception e){

    }
  }

  public void writeToFile(String fileName, String content) {

      File poseFolder = new File("/data/user/0/com.google.ar.core.examples.java.helloar/files", "Pose");
      poseFolder.mkdirs();
//    //System.out.println(Environment.getExternalStorageDirectory());
//    final File photo= new File(imagesFolder, String.valueOf(NumberP)+".jpg");
//
//    try
//    {
//      FileOutputStream fos=new FileOutputStream(photo.getPath());
//      fos.write(jpeg);
//      fos.close();
//    }
//    catch(Exception e)
//    {
//      System.out.println("Error in storing images");
//    }

    //File path = getApplicationContext().getFilesDir();
    //System.out.println("Inside writeToFile");
    //System.out.println(path);
    try {
      FileOutputStream writer = new FileOutputStream(new File("/data/user/0/com.google.ar.core.examples.java.helloar/files/Pose", fileName+".txt"));
      writer.write(content.getBytes());
      writer.close();
      //Toast.makeText(getApplicationContext(), "wrote to file: "+fileName, Toast.LENGTH_SHORT).show();
    } catch(Exception e) {
      System.out.println("Error in storing text files");
      e.printStackTrace();
    }
  }

  /******************************************************************/
  private void getData(Frame frame,Camera camera){
    Pose pose = frame.getAndroidSensorPose();

    Pose thispose;

    thispose = pose.extractTranslation();
    float[] translation = thispose.getTranslation();
    thispose = pose.extractRotation();
    float[] orientation = thispose.getRotationQuaternion();

    float[] t = translation;
    float[] q = orientation;
    float[][] r1 = new float[3][3];

    r1[0][0] = 2 * (q[0] * q[0] + q[1] * q[1]) - 1;
    r1[0][1] = 2 * (q[1] * q[2] - q[0] * q[3]);
    r1[0][2] = 2 * (q[1] * q[3] + q[0] * q[2]);

    r1[1][0] = 2 * (q[1] * q[2] + q[0] * q[3]);
    r1[1][1] = 2 * (q[0] * q[0] + q[2] * q[2]) - 1;
    r1[1][2] = 2 * (q[2] * q[3] - q[0] * q[1]);

    r1[2][0] = 2 * (q[1] * q[3] - q[0] * q[2]);
    r1[2][1] = 2 * (q[2] * q[3] + q[0] * q[1]);
    r1[2][2] = 2 * (q[0] * q[0] + q[3] * q[3]) - 1;

    CameraIntrinsics cam;
    cam = camera.getImageIntrinsics();
    float[] offset;
    float[] focal;
    offset = cam.getPrincipalPoint();
    focal = cam.getFocalLength();

    String extrinsicsMatrix="";
    String intrinsicsMatrix="";


    DecimalFormat dfrmt = new DecimalFormat();
    dfrmt.setMaximumFractionDigits(18);
    for(int i=0;i<3;i++) {
      for (int j = 0; j < 4; j++) {
        if(j==3)
          extrinsicsMatrix = extrinsicsMatrix + dfrmt.format(t[i]) + " ";
        else
          extrinsicsMatrix = extrinsicsMatrix + dfrmt.format(r1[i][j]) + " ";
      }
      extrinsicsMatrix += "\n";
    }
    extrinsicsMatrix += dfrmt.format(0)+" "+dfrmt.format(0.0)+" "+dfrmt.format(0)+" "+dfrmt.format(0)+"\n";

    float zero = 0;
    intrinsicsMatrix = dfrmt.format(focal[0])+" "+dfrmt.format(0.0)+" "+dfrmt.format(offset[0])+"\n";
    intrinsicsMatrix += dfrmt.format(zero)+" "+dfrmt.format(focal[1])+" "+dfrmt.format(offset[1])+"\n";
    intrinsicsMatrix += dfrmt.format(zero)+" "+dfrmt.format(0.0)+" "+dfrmt.format(1.0)+"\n";

    System.out.println(extrinsicsMatrix+" "+intrinsicsMatrix);
    count1+=1;
    if(count1==1)
      writeToFile("Intrinsics",intrinsicsMatrix);
    writeToFile(Integer.toString(count1),extrinsicsMatrix);
      /*float[] fl = new float[16];
      camera.getViewMatrix(fl,0);
      for(int i=0;i<16;i++) {
        System.out.println(fl[i]);
      }*/
  }

  public static byte[] YUV420toNV21(Image image) {
    byte[] nv21;
    ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
    ByteBuffer uBuffer = image.getPlanes()[1].getBuffer();
    ByteBuffer vBuffer = image.getPlanes()[2].getBuffer();

    int ySize = yBuffer.remaining();
    int uSize = uBuffer.remaining();
    int vSize = vBuffer.remaining();

    nv21 = new byte[ySize + uSize + vSize];

    //U and V are swapped
    yBuffer.get(nv21, 0, ySize);
    vBuffer.get(nv21, ySize, vSize);
    uBuffer.get(nv21, ySize + vSize, uSize);

    return nv21;
  }

  public static byte[] NV21toJPEG(byte[] nv21, int width, int height) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    YuvImage yuv = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
    yuv.compressToJpeg(new Rect(0, 0, width, height), 1000, out);
    return out.toByteArray();
  }




  public Bitmap imageToBitmap(Image image, float rotationDegrees) {

    System.out.println("Reached bitmap -------------------------------------------------");

    //assert (image.getFormat() == ImageFormat.NV21);

    // NV21 is a plane of 8 bit Y values followed by interleaved  Cb Cr
    ByteBuffer ib = ByteBuffer.allocate(image.getHeight() * image.getWidth() * 2);
    System.out.println("Reached 812 -------------------------------------------------");

    ByteBuffer y = image.getPlanes()[0].getBuffer();
    System.out.println("Reached 815 -------------------------------------------------");
    ByteBuffer cr = image.getPlanes()[1].getBuffer();
    ByteBuffer cb = image.getPlanes()[2].getBuffer();
    ib.put(y);
    ib.put(cb);
    ib.put(cr);

    YuvImage yuvImage = new YuvImage(ib.array(),
            ImageFormat.NV21, image.getWidth(), image.getHeight(), null);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    yuvImage.compressToJpeg(new Rect(0, 0,
            image.getWidth(), image.getHeight()), 50, out);
    byte[] imageBytes = out.toByteArray();
    Bitmap bm = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    Bitmap bitmap = bm;
    // On android the camera rotation and the screen rotation
    // are off by 90 degrees, so if you are capturing an image
    // in "portrait" orientation, you'll need to rotate the image.
    /*if (rotationDegrees != 0) {
      //Matrix matrix = new Matrix();
      //matrix.postRotate(rotationDegrees);
//      android.graphics.Matrix matrix = android.graphics.Matrix.IDENTITY_MATRIX;
//      Matrix matrix =
      Matrix matrix = new Matrix();
      matrix.transposeM(0.0, 0, 90, 0);
      Bitmap scaledBitmap = Bitmap.createScaledBitmap(bm,
              bm.getWidth(), bm.getHeight(), true);
      bitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
              scaledBitmap.getWidth(), scaledBitmap.getHeight(), android.graphics.Matrix.IDENTITY_MATRIX, true);
    }*/
    System.out.println("Reached bitmap return --------------------------------------");
    return bitmap;
  }

  public void SavePhotoTask(byte [] jpeg){
    File imagesFolder = new File("/data/user/0/com.google.ar.core.examples.java.helloar/files", "Images");
    imagesFolder.mkdirs();
    //System.out.println(Environment.getExternalStorageDirectory());
    final File photo= new File(imagesFolder, String.valueOf(NumberP)+".jpg");

    try
    {
      FileOutputStream fos=new FileOutputStream(photo.getPath());
      fos.write(jpeg);
      fos.close();
    }
    catch(Exception e)
    {
      System.out.println("Error in storing images");
    }
  }


  public void SavePoseTask(String pose1){
    File poseFolder = new File("/data/user/0/com.google.ar.core.examples.java.helloar/files", "Pose");
    //File poseFolder = new File("/storage/emulated/0", "Pose");
    poseFolder.mkdirs();
    //System.out.println(Environment.getExternalStorageDirectory());
    final File pose= new File(poseFolder, String.valueOf(Number)+".txt");

    try
    {
      FileOutputStream fos=new FileOutputStream(pose.getPath());
      fos.write(pose1.getBytes());
      System.out.println("*********************Pose saved");
      fos.close();
    }
    catch(Exception e)
    {
      System.out.println("********************Error occured while saving pose");
    }
  }




  // Handle only one tap per frame, as taps are usually low frequency compared to frame rate.
  private void handleTap(Frame frame, Camera camera) {
    MotionEvent tap = tapHelper.poll();
    if (tap != null && camera.getTrackingState() == TrackingState.TRACKING) {
      List<HitResult> hitResultList;
      if (instantPlacementSettings.isInstantPlacementEnabled()) {
        hitResultList =
                frame.hitTestInstantPlacement(tap.getX(), tap.getY(), APPROXIMATE_DISTANCE_METERS);
      } else {
        hitResultList = frame.hitTest(tap);
      }
      for (HitResult hit : hitResultList) {
        // If any plane, Oriented Point, or Instant Placement Point was hit, create an anchor.
        Trackable trackable = hit.getTrackable();
        //System.out.println(camera.getPose());
        //System.out.println("camera intrinsic");
        //System.out.println(camera.getImageIntrinsics());
        /*float[][] mat_4x4 = new float[4][4];
        float[] fl = new float[16];
        camera.getViewMatrix(fl,0);
        String s = "";
        for(int i=0;i<16;i++) {
          s = s + " " + Float.toString(fl[i]);
          //System.out.println(fl[i]);
        }
        writeToFile(String.valueOf(Number), s);
        Number+=1;
        System.out.println("************************************************");*/
        //getData(frame, camera);

        //System.out.println(s);
        //mat_4x4 =

        //***************************************************
        float[] mat_4x4 = new float[16];
        frame.getCamera().getPose().toMatrix(mat_4x4,0);
        String s = "";
        for(int i=0;i<16;i++) {
          s = s + " " + Float.toString(mat_4x4[i]);
          //System.out.println(fl[i]);
        }
        writeToFile(String.valueOf(Number), s);
        //SavePoseTask(s);
        Number +=1;

        onClick();

        try {
          Image image = frame.acquireCameraImage();
          Bitmap temp;
          temp = imageToBitmap(image, 90);
          //Bitmap bmp = null;
          ByteArrayOutputStream stream = new ByteArrayOutputStream();
          temp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
          byte[] byteArray = stream.toByteArray();
          SavePhotoTask(byteArray);
          NumberP+=1;
          image.close();
          //byte[] jpegByteArray = getJpegFromImage(image);
          //temp = YUV420toNV21(image);
          //System.out.println("*******************");
          //System.out.println(image.getClass());
          //System.out.println("********************");
        }
        catch(NotYetAvailableException e) {
          e.printStackTrace();
        }


        // If a plane was hit, check that it was hit inside the plane polygon.
        // DepthPoints are only returned if Config.DepthMode is set to AUTOMATIC.

        if ((trackable instanceof Plane
                && ((Plane) trackable).isPoseInPolygon(hit.getHitPose())
                && (PlaneRenderer.calculateDistanceToPlane(hit.getHitPose(), camera.getPose()) > 0))
                || (trackable instanceof Point
                && ((Point) trackable).getOrientationMode()
                == OrientationMode.ESTIMATED_SURFACE_NORMAL)
                || (trackable instanceof InstantPlacementPoint)
                || (trackable instanceof DepthPoint)) {
          // Cap the number of objects created. This avoids overloading both the
          // rendering system and ARCore.
          if (anchors.size() >= 20) {
            anchors.get(0).detach();
            anchors.remove(0);
          }

          // Adding an Anchor tells ARCore that it should track this position in
          // space. This anchor is created on the Plane to place the 3D model
          // in the correct position relative both to the world and to the plane.
          anchors.add(hit.createAnchor());
          // For devices that support the Depth API, shows a dialog to suggest enabling
          // depth-based occlusion. This dialog needs to be spawned on the UI thread.
          this.runOnUiThread(this::showOcclusionDialogIfNeeded);

          // Hits are sorted by depth. Consider only closest hit on a plane, Oriented Point, or
          // Instant Placement Point.
          break;
        }
      }
    }
  }

  /**
   * Shows a pop-up dialog on the first call, determining whether the user wants to enable
   * depth-based occlusion. The result of this dialog can be retrieved with useDepthForOcclusion().
   */
  private void showOcclusionDialogIfNeeded() {
    boolean isDepthSupported = session.isDepthModeSupported(Config.DepthMode.AUTOMATIC);
    if (!depthSettings.shouldShowDepthEnableDialog() || !isDepthSupported) {
      return; // Don't need to show dialog.
    }

    // Asks the user whether they want to use depth-based occlusion.
    new AlertDialog.Builder(this)
            .setTitle(R.string.options_title_with_depth)
            .setMessage(R.string.depth_use_explanation)
            .setPositiveButton(
                    R.string.button_text_enable_depth,
                    (DialogInterface dialog, int which) -> {
                      depthSettings.setUseDepthForOcclusion(true);
                    })
            .setNegativeButton(
                    R.string.button_text_disable_depth,
                    (DialogInterface dialog, int which) -> {
                      depthSettings.setUseDepthForOcclusion(false);
                    })
            .show();
  }

  private void launchInstantPlacementSettingsMenuDialog() {
    resetSettingsMenuDialogCheckboxes();
    Resources resources = getResources();
    new AlertDialog.Builder(this)
            .setTitle(R.string.options_title_instant_placement)
            .setMultiChoiceItems(
                    resources.getStringArray(R.array.instant_placement_options_array),
                    instantPlacementSettingsMenuDialogCheckboxes,
                    (DialogInterface dialog, int which, boolean isChecked) ->
                            instantPlacementSettingsMenuDialogCheckboxes[which] = isChecked)
            .setPositiveButton(
                    R.string.done,
                    (DialogInterface dialogInterface, int which) -> applySettingsMenuDialogCheckboxes())
            .setNegativeButton(
                    android.R.string.cancel,
                    (DialogInterface dialog, int which) -> resetSettingsMenuDialogCheckboxes())
            .show();
  }

  /** Shows checkboxes to the user to facilitate toggling of depth-based effects. */
  private void launchDepthSettingsMenuDialog() {
    // Retrieves the current settings to show in the checkboxes.
    resetSettingsMenuDialogCheckboxes();

    // Shows the dialog to the user.
    Resources resources = getResources();
    if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
      // With depth support, the user can select visualization options.
      new AlertDialog.Builder(this)
              .setTitle(R.string.options_title_with_depth)
              .setMultiChoiceItems(
                      resources.getStringArray(R.array.depth_options_array),
                      depthSettingsMenuDialogCheckboxes,
                      (DialogInterface dialog, int which, boolean isChecked) ->
                              depthSettingsMenuDialogCheckboxes[which] = isChecked)
              .setPositiveButton(
                      R.string.done,
                      (DialogInterface dialogInterface, int which) -> applySettingsMenuDialogCheckboxes())
              .setNegativeButton(
                      android.R.string.cancel,
                      (DialogInterface dialog, int which) -> resetSettingsMenuDialogCheckboxes())
              .show();
    } else {
      // Without depth support, no settings are available.
      new AlertDialog.Builder(this)
              .setTitle(R.string.options_title_without_depth)
              .setPositiveButton(
                      R.string.done,
                      (DialogInterface dialogInterface, int which) -> applySettingsMenuDialogCheckboxes())
              .show();
    }
  }

  private void applySettingsMenuDialogCheckboxes() {
    depthSettings.setUseDepthForOcclusion(depthSettingsMenuDialogCheckboxes[0]);
    depthSettings.setDepthColorVisualizationEnabled(depthSettingsMenuDialogCheckboxes[1]);
    instantPlacementSettings.setInstantPlacementEnabled(
            instantPlacementSettingsMenuDialogCheckboxes[0]);
    configureSession();
  }

  private void resetSettingsMenuDialogCheckboxes() {
    depthSettingsMenuDialogCheckboxes[0] = depthSettings.useDepthForOcclusion();
    depthSettingsMenuDialogCheckboxes[1] = depthSettings.depthColorVisualizationEnabled();
    instantPlacementSettingsMenuDialogCheckboxes[0] =
            instantPlacementSettings.isInstantPlacementEnabled();
  }

  /** Checks if we detected at least one plane. */
  private boolean hasTrackingPlane() {
    for (Plane plane : session.getAllTrackables(Plane.class)) {
      if (plane.getTrackingState() == TrackingState.TRACKING) {
        return true;
      }
    }
    return false;
  }

  /** Update state based on the current frame's light estimation. */
  private void updateLightEstimation(LightEstimate lightEstimate, float[] viewMatrix) {
    if (lightEstimate.getState() != LightEstimate.State.VALID) {
      virtualObjectShader.setBool("u_LightEstimateIsValid", false);
      return;
    }
    virtualObjectShader.setBool("u_LightEstimateIsValid", true);

    Matrix.invertM(viewInverseMatrix, 0, viewMatrix, 0);
    virtualObjectShader.setMat4("u_ViewInverse", viewInverseMatrix);

    updateMainLight(
            lightEstimate.getEnvironmentalHdrMainLightDirection(),
            lightEstimate.getEnvironmentalHdrMainLightIntensity(),
            viewMatrix);
    updateSphericalHarmonicsCoefficients(
            lightEstimate.getEnvironmentalHdrAmbientSphericalHarmonics());
    cubemapFilter.update(lightEstimate.acquireEnvironmentalHdrCubeMap());
  }

  private void updateMainLight(float[] direction, float[] intensity, float[] viewMatrix) {
    // We need the direction in a vec4 with 0.0 as the final component to transform it to view space
    worldLightDirection[0] = direction[0];
    worldLightDirection[1] = direction[1];
    worldLightDirection[2] = direction[2];
    Matrix.multiplyMV(viewLightDirection, 0, viewMatrix, 0, worldLightDirection, 0);
    virtualObjectShader.setVec4("u_ViewLightDirection", viewLightDirection);
    virtualObjectShader.setVec3("u_LightIntensity", intensity);
  }

  private void updateSphericalHarmonicsCoefficients(float[] coefficients) {
    // Pre-multiply the spherical harmonics coefficients before passing them to the shader. The
    // constants in sphericalHarmonicFactors were derived from three terms:
    //
    // 1. The normalized spherical harmonics basis functions (y_lm)
    //
    // 2. The lambertian diffuse BRDF factor (1/pi)
    //
    // 3. A <cos> convolution. This is done to so that the resulting function outputs the irradiance
    // of all incoming light over a hemisphere for a given surface normal, which is what the shader
    // (environmental_hdr.frag) expects.
    //
    // You can read more details about the math here:
    // https://google.github.io/filament/Filament.html#annex/sphericalharmonics

    if (coefficients.length != 9 * 3) {
      throw new IllegalArgumentException(
              "The given coefficients array must be of length 27 (3 components per 9 coefficients");
    }

    // Apply each factor to every component of each coefficient
    for (int i = 0; i < 9 * 3; ++i) {
      sphericalHarmonicsCoefficients[i] = coefficients[i] * sphericalHarmonicFactors[i / 3];
    }
    virtualObjectShader.setVec3Array(
            "u_SphericalHarmonicsCoefficients", sphericalHarmonicsCoefficients);
  }

  /** Configures the session with feature settings. */
  private void configureSession() {
    Config config = session.getConfig();
    config.setLightEstimationMode(Config.LightEstimationMode.ENVIRONMENTAL_HDR);
    if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
      config.setDepthMode(Config.DepthMode.AUTOMATIC);
    } else {
      config.setDepthMode(Config.DepthMode.DISABLED);
    }
    if (instantPlacementSettings.isInstantPlacementEnabled()) {
      config.setInstantPlacementMode(InstantPlacementMode.LOCAL_Y_UP);
    } else {
      config.setInstantPlacementMode(InstantPlacementMode.DISABLED);
    }
    session.configure(config);
  }
}
