import androidx.annotation.experimental.UseExperimental
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

@UseExperimental(markerClass = ExperimentalGetImage::class)
class SmileAnalyzer(
    private val onSmileDetected: (smiling: Boolean) -> Unit
) : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {
        image.image?.let {
            val imageValue = InputImage.fromMediaImage(it, image.imageInfo.rotationDegrees)
            val options = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
                .setLandmarkMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
                .build()
            val detector = FaceDetection.getClient(options)

            detector.process(imageValue)
                .addOnSuccessListener { faces ->
                    var smiling = true

                    for (face in faces) {
                        if (face.smilingProbability == null ||
                            (face.smilingProbability != null && face.smilingProbability!! < 0.9)
                        ) {
                            smiling = false
                            break;
                        }
                    }

                    if (faces.size > 0) {
                        onSmileDetected(smiling)
                    } else {
                        onSmileDetected(false)
                    }

                    image.image?.close()
                    image.close()
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    image.image?.close()
                    image.close()
                }
        }
    }
}
