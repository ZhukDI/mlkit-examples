package com.github.zhukdi.mlkit_examples

import android.app.AlertDialog
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.github.zhukdi.mlkit_examples.helper.TextGraphic
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.wonderkiln.camerakit.*
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var waiting_dialog : AlertDialog

    override fun onResume() {
        super.onResume()
        cam_view.start()
    }

    override fun onPause() {
        super.onPause()
        cam_view.stop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        waiting_dialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Please wait...")
            .setCancelable(false)
            .build()

        camera_btn.setOnClickListener {
            cam_view.start()
            cam_view.captureImage()
            graphic_overlay.clear()
        }

        cam_view.addCameraKitListener(object: CameraKitEventListener {
            override fun onVideo(p0: CameraKitVideo?) {

            }

            override fun onEvent(p0: CameraKitEvent?) {

            }

            override fun onError(p0: CameraKitError?) {

            }


            override fun onImage(p0: CameraKitImage?) {
                waiting_dialog.show()

                var bitmap = p0!!.bitmap
                bitmap = Bitmap.createScaledBitmap(bitmap, cam_view.width, cam_view.height, false)
                cam_view.stop()

                recognizeText(bitmap)
            }

        })

    }

    private fun recognizeText(bitmap: Bitmap?) {
        val image = FirebaseVisionImage.fromBitmap(bitmap!!)
        val options = FirebaseVisionCloudTextRecognizerOptions.Builder()
            .setLanguageHints(Arrays.asList("en"))
            .build()
        val textRecognizer = FirebaseVision.getInstance()
            .getCloudTextRecognizer(options)
        textRecognizer.processImage(image)
            .addOnSuccessListener { result -> processTextResult(result) }
            .addOnFailureListener {
                    e -> Log.d("ERROR", e.message)
                    waiting_dialog.dismiss()
            }
    }

    private fun processTextResult(result: FirebaseVisionText) {
        val blocks = result.textBlocks
        if (blocks.size == 0) {
            Toast.makeText(this, "No Text Found", Toast.LENGTH_SHORT).show()
            return
        }
        graphic_overlay.clear()
        for (i in blocks.indices) {
            val lines = blocks[i].lines
            for (j in lines.indices) {
                val elements = lines[j].elements
                for (k in elements.indices) {
                    val textGraphic = TextGraphic(graphic_overlay, elements[k])
                    graphic_overlay.add(textGraphic)
                }
            }
        }

        waiting_dialog.dismiss()
    }
}
