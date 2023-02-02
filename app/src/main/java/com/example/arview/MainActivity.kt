package com.example.arview

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

private const val MIN_OPENGL_VERSION = 3.1
class MainActivity : AppCompatActivity() {

    private var arFragment: ArFragment? = null
    private var objectRenderable: ModelRenderable? = null
    private var arModel: String? = null
    var anchorNode: AnchorNode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        arFragment = (supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment?)!!
        arModel = "https://cybermart-prod.s3.me-south-1.amazonaws.com/chair_4.glb"

        /**
         * First we need to check if device is supported for AR view or not
         */
        if (checkIsSupportedDeviceOrFinish(this)) {
            startInitialization()
        } else {
            Toast.makeText(this , "This Device is not support AR." , Toast.LENGTH_LONG).show()
        }
    }
    private fun startInitialization() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                try {

                    Toast.makeText(this, "downloading model", Toast.LENGTH_SHORT).show()

                    // Init renderable
                    loadModel()

                    // Set tap listener
                    arFragment!!.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane?, motionEvent: MotionEvent? ->
                        val anchor = hitResult.createAnchor()
                        if (anchorNode == null) {
                            anchorNode = AnchorNode(anchor)
                            anchorNode?.setParent(arFragment!!.arSceneView.scene)
                            createModel()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, 1011)
            }
        } catch (e: java.lang.Exception) {
            Log.d("TAG", e.printStackTrace().toString() + e.message.toString())
        }
    }

    private fun createModel() {
        try {
            if (anchorNode != null) {
                val node = TransformableNode(arFragment!!.transformationSystem)
                node.scaleController.maxScale = 1.0f
                node.scaleController.minScale = 0.01f
                node.scaleController.sensitivity = 0.1f
                node.setParent(anchorNode)
                node.renderable = objectRenderable

                node.select()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "something_went_wrong", Toast.LENGTH_SHORT).show()

        }
    }

    /**
     * Loading model from URL
     */

    private fun loadModel() {
        try {
            ModelRenderable.builder()
                .setSource(this,
                    RenderableSource.builder().setSource(
                        this,
                        Uri.parse(arModel),
                        RenderableSource.SourceType.GLB)
                        .setScale(0.75f)
                        .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                        .build())
                .setRegistryId(arModel)
                .build()
                .thenAccept { renderable: ModelRenderable ->
                    objectRenderable = renderable
                    Toast.makeText(this, "model_ready", Toast.LENGTH_SHORT).show()

                }
                .exceptionally { throwable: Throwable? ->
                    Log.i("Model", "cant load")
                    null
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Check if device has AR support or not
     */

    private fun checkIsSupportedDeviceOrFinish(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e("error", "Sceneform requires Android N or later")
            return false
        }
        val openGlVersionString = (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            .deviceConfigurationInfo
            .glEsVersion
        if (java.lang.Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e("error", "Sceneform requires OpenGL ES 3.1 later")
            return false
        }
        return true
    }
}
