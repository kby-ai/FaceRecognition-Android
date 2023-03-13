package com.kbyai.facerecognition

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Camera
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.kbyai.facesdk.FaceBox
import com.kbyai.facesdk.FaceSDK
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    companion object {
        private val SELECT_PHOTO_REQUEST_CODE = 1
    }

    private lateinit var dbManager: DBManager
    private lateinit var textWarning: TextView
    private lateinit var personAdapter: PersonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textWarning = findViewById<TextView>(R.id.textWarning)

        var ret = FaceSDK.setActivation(
            "jfeIR/8sT+yQbR4FzN0fLYlSnJFHC/YDgncLJRHFFXp/7XN9ONJPcPUOWbj762y2jM0qq2QVFKkU\n" +
            "Ae/vxlFXXcVFxxrz1EcQpnTf8tKdm978+s3GShWQgyWiE0mTHRDUa/7U9j0twL0pQ4X9STUA0LX3\n" +
            "6xCgI57o6LcSUW0uWbCdG6xSz5dd41ko9jsPEVoysNLn8utabTcDsX+PadCVSBR5wpmMk0fyJJnD\n" +
            "OCNv6YNgwE0IZKePQniqiGnG+Euw1hsmiCapuF7l+SnWYpWRGVaSQSak7NYW330JYQp0Rap2j4o4\n" +
            "LfNHn9/oipn8BJCEFGS34WnznALHDdnuupjs2A=="
        )

        if (ret == FaceSDK.SDK_SUCCESS) {
            ret = FaceSDK.init(assets)
        }

        if (ret != FaceSDK.SDK_SUCCESS) {
            textWarning.setVisibility(View.VISIBLE)
            if (ret == FaceSDK.SDK_LICENSE_KEY_ERROR) {
                textWarning.setText("Invalid license!")
            } else if (ret == FaceSDK.SDK_LICENSE_APPID_ERROR) {
                textWarning.setText("Invalid error!")
            } else if (ret == FaceSDK.SDK_LICENSE_EXPIRED) {
                textWarning.setText("License expired!")
            } else if (ret == FaceSDK.SDK_NO_ACTIVATED) {
                textWarning.setText("No activated!")
            } else if (ret == FaceSDK.SDK_INIT_ERROR) {
                textWarning.setText("Init error!")
            }
        }

        dbManager = DBManager(this)
        dbManager.loadPerson()

        personAdapter = PersonAdapter(this, DBManager.personList)
        val listView: ListView = findViewById<View>(R.id.listPerson) as ListView
        listView.setAdapter(personAdapter)

        findViewById<Button>(R.id.buttonEnroll).setOnClickListener {
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_PICK)
            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), SELECT_PHOTO_REQUEST_CODE)

        }

        findViewById<Button>(R.id.buttonIdentify).setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

        findViewById<Button>(R.id.buttonSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        findViewById<Button>(R.id.buttonAbout).setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

        personAdapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SELECT_PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                var bitmap: Bitmap = Utils.getCorrectlyOrientedImage(this, data?.data!!)
                var faceBoxes: List<FaceBox>? = FaceSDK.faceDetection(bitmap)

                if(faceBoxes.isNullOrEmpty()) {
                    Toast.makeText(this, getString(R.string.no_face_detected), Toast.LENGTH_SHORT).show()
                } else if (faceBoxes.size > 1) {
                    Toast.makeText(this, getString(R.string.multiple_face_detected), Toast.LENGTH_SHORT).show()
                } else {
                    val faceImage = Utils.cropFace(bitmap, faceBoxes[0])
                    val templates = FaceSDK.templateExtraction(bitmap, faceBoxes[0])

                    dbManager.insertPerson("Person" + Random.nextInt(10000, 20000), faceImage, templates)
                    personAdapter.notifyDataSetChanged()
                    Toast.makeText(this, getString(R.string.person_enrolled), Toast.LENGTH_SHORT).show()
                }
            } catch (e: java.lang.Exception) {
                //handle exception
                e.printStackTrace()
            }
        }
    }
}