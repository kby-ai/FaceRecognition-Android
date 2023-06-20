package com.kbyai.facerecognition

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.preference.*


class SettingsActivity : AppCompatActivity() {

    companion object {
        const val DEFAULT_CAMERA_LENS = "front"
        const val DEFAULT_LIVENESS_THRESHOLD = "0.7"
        const val DEFAULT_IDENTIFY_THRESHOLD = "0.8"
        const val DEFAULT_LIVENESS_LEVEL = "0"

        @JvmStatic
        fun getLivenessThreshold(context: Context): Float {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPreferences.getString("liveness_threshold", SettingsActivity.DEFAULT_LIVENESS_THRESHOLD)!!.toFloat()
        }

        @JvmStatic
        fun getIdentifyThreshold(context: Context): Float {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPreferences.getString("identify_threshold", SettingsActivity.DEFAULT_IDENTIFY_THRESHOLD)!!.toFloat()
        }

        @JvmStatic
        fun getCameraLens(context: Context): Int {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val cameraLens = sharedPreferences.getString("camera_lens", SettingsActivity.DEFAULT_CAMERA_LENS)
            if(cameraLens == "back") {
                return CameraSelector.LENS_FACING_BACK
            } else {
                return CameraSelector.LENS_FACING_FRONT
            }
        }

        @JvmStatic
        fun getLivenessLevel(context: Context): Int {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val livenessLevel = sharedPreferences.getString("liveness_level", SettingsActivity.DEFAULT_LIVENESS_LEVEL)
            if(livenessLevel == "0") {
                return 0
            } else {
                return 1
            }
        }
    }

    lateinit var dbManager: DBManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbManager = DBManager(this)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val cameraLensPref = findPreference<ListPreference>("camera_lens")
            val livenessThresholdPref = findPreference<EditTextPreference>("liveness_threshold")
            val livenessLevelPref = findPreference<ListPreference>("liveness_level")
            val identifyThresholdPref = findPreference<EditTextPreference>("identify_threshold")
            val buttonRestorePref = findPreference<Preference>("restore_default_settings")

            livenessThresholdPref?.setOnPreferenceChangeListener{ preference, newValue ->
                val stringPref = newValue as String
                try {
                    if(stringPref.toFloat() < 0.0f || stringPref.toFloat() > 1.0f) {
                        Toast.makeText(context, getString(R.string.invalid_value), Toast.LENGTH_SHORT).show()
                        false
                    } else {
                        true
                    }
                } catch (e:Exception) {
                    Toast.makeText(context, getString(R.string.invalid_value), Toast.LENGTH_SHORT).show()
                    false
                }
            }

            identifyThresholdPref?.setOnPreferenceChangeListener{ preference, newValue ->
                val stringPref = newValue as String
                try {
                    if(stringPref.toFloat() < 0.0f || stringPref.toFloat() > 1.0f) {
                        Toast.makeText(context, getString(R.string.invalid_value), Toast.LENGTH_SHORT).show()
                        false
                    } else {
                        true
                    }
                } catch (e:Exception) {
                    Toast.makeText(context, getString(R.string.invalid_value), Toast.LENGTH_SHORT).show()
                    false
                }
            }

            buttonRestorePref?.setOnPreferenceClickListener {

                cameraLensPref?.value = SettingsActivity.DEFAULT_CAMERA_LENS
                livenessLevelPref?.value = SettingsActivity.DEFAULT_LIVENESS_LEVEL
                livenessThresholdPref?.text = SettingsActivity.DEFAULT_LIVENESS_THRESHOLD
                identifyThresholdPref?.text = SettingsActivity.DEFAULT_IDENTIFY_THRESHOLD

                Toast.makeText(activity, getString(R.string.restored_default_settings), Toast.LENGTH_LONG).show()
                true
            }

            val buttonClearPref = findPreference<Preference>("clear_all_person")
            buttonClearPref?.setOnPreferenceClickListener {
                val settingsActivity = activity as SettingsActivity
                settingsActivity.dbManager.clearDB()

                Toast.makeText(activity, getString(R.string.cleared_all_person), Toast.LENGTH_LONG).show()
                true
            }
        }
    }
}