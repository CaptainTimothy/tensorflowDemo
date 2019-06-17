package cc.CaptainTimothy.tensorflowDemo

import android.app.Activity
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class DigitsDetector(activity: Activity) {

    private val TAG = this.javaClass.simpleName

    // The tensorflow lite file
    private lateinit var tflite: Interpreter

    // Input byte buffer
    private lateinit var inputBuffer: ByteBuffer

    // Output array [batch_size, 10]
    private lateinit var mnistOutput: Array<FloatArray>

    init {

        try {
            tflite = Interpreter(loadModelFile(activity))

            inputBuffer = ByteBuffer.allocateDirect(
                    BYTE_SIZE_OF_FLOAT * DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE)
            inputBuffer.order(ByteOrder.nativeOrder())
            mnistOutput = Array(DIM_BATCH_SIZE) { FloatArray(NUMBER_LENGTH) }
            Log.d(TAG, "Created a Tensorflow Lite MNIST Classifier.")
        } catch (e: IOException) {
            Log.e(TAG, "IOException loading the tflite file failed.")
        }

    }

    /**
     * Load the model file from the assets folder
     */
    @Throws(IOException::class)
    private fun loadModelFile(activity: Activity): MappedByteBuffer {

        val fileDescriptor = activity.assets.openFd(MODEL_PATH)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    /**
     * Classifies the number with the mnist model.
     *
     * @param bitmap
     * @return the identified number
     */
    fun classify(bitmap: Bitmap): Int {

        if (tflite == null) {
            Log.e(TAG, "Image classifier has not been initialized; Skipped.")
        }

        preProcess(bitmap)
        runModel()
        return postProcess()
    }

    /**
     * Converts it into the Byte Buffer to feed into the model
     *
     * @param bitmap
     */
    private fun preProcess(bitmap: Bitmap?) {

        if (bitmap == null || inputBuffer == null) {
            return
        }

        // Reset the image data
        inputBuffer.rewind()

        val width = bitmap.width
        val height = bitmap.height

        // The bitmap shape should be 28 x 28
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            // Set 0 for white and 255 for black pixels
            val pixel = pixels[i]
            // The color of the input is black so the blue channel will be 0xFF.
            val channel = pixel and 0xff
            inputBuffer.putFloat((0xff - channel).toFloat())
        }
    }

    /**
     * Run the TFLite model
     */
    private fun runModel() = tflite.run(inputBuffer, mnistOutput)

    /**
     * Go through the output and find the number that was identified.
     *
     * @return the number that was identified (returns -1 if one wasn't found)
     */
    private fun postProcess(): Int {

        for (i in 0 until mnistOutput[0].size) {
            val value = mnistOutput[0][i]
            if (value == 1f) {
                return i
            }
        }

        return -1
    }

    companion object {

        private val MODEL_PATH = "mnist.tflite"

        // Specify the output size
        private val NUMBER_LENGTH = 10

        // Specify the input size
        private val DIM_BATCH_SIZE = 1
        private val DIM_IMG_SIZE_X = 28
        private val DIM_IMG_SIZE_Y = 28
        private val DIM_PIXEL_SIZE = 1

        // Number of bytes to hold a float (32 bits / float) / (8 bits / byte) = 4 bytes / float
        private val BYTE_SIZE_OF_FLOAT = 4
    }
}