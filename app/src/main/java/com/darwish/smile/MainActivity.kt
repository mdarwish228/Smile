package com.darwish.smile

import SmileAnalyzer
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Size
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 10
    }

    private lateinit var previewView: PreviewView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        previewView = findViewById(R.id.previewView)

        // Request camera permissions
        if (isCameraPermissionGranted()) {
            previewView.post { startCamera() }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (isCameraPermissionGranted()) {
                previewView.post { startCamera() }
            } else {
                Toast.makeText(
                    this,
                    "Camera permission is required.", Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        val cameraSelector =
            CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()
        val previewConfig = Preview.Builder()
            .setTargetResolution(Size(1000, 1000))
            .build()

        previewConfig.setSurfaceProvider(previewView.surfaceProvider)

        val imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .setTargetResolution(Size(1000, 1000))
            .build()
        val executor = ContextCompat.getMainExecutor(this)
        val imageAnalyzer = ImageAnalysis.Builder().build().also {
            it.setAnalyzer(executor, SmileAnalyzer { isSmiling ->
                if (isSmiling) {
                    val intent = Intent(this, SmileCount::class.java)
                    startActivity(intent)
                    it.clearAnalyzer()
                    finish()
                }
            })
        }

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                previewConfig,
                imageCapture,
                imageAnalyzer
            )
        }, executor)
    }

    private fun isCameraPermissionGranted(): Boolean {
        val selfPermission =
            ContextCompat.checkSelfPermission(baseContext, Manifest.permission.CAMERA)
        return selfPermission == PackageManager.PERMISSION_GRANTED
    }
}
