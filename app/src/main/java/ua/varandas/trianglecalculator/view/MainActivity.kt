package ua.varandas.trianglecalculator.view

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.github.stephenvinouze.core.managers.KinAppManager
import com.github.stephenvinouze.core.models.KinAppProductType
import com.github.stephenvinouze.core.models.KinAppPurchase
import com.github.stephenvinouze.core.models.KinAppPurchaseResult
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.remove_ads_dialog.view.*
import org.jetbrains.anko.db.*
import ua.varandas.trianglecalculator.R
import ua.varandas.trianglecalculator.controller.Controller
import ua.varandas.trianglecalculator.database.*
import ua.varandas.trianglecalculator.enums.CorrectTriangle
import ua.varandas.trianglecalculator.ext.checkEditText
import ua.varandas.trianglecalculator.ext.notEmptyToFloat
import ua.varandas.trianglecalculator.ext.prefs
import ua.varandas.trianglecalculator.firebase.Ads
import ua.varandas.trianglecalculator.firebase.URLConnection
import ua.varandas.trianglecalculator.interfaces.IMainContract
import ua.varandas.trianglecalculator.model.Triangle


class MainActivity : AppCompatActivity(), IMainContract.IView {
    private val TAG = "MainActivity"

    private val RSA = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzzeb9qWvweOr3GbSwmZiqkj9P2XfYGZganzFyr1XgJ9zfDtQmNfwPIcSmkYYfXJPlnv+83PqeEiwnmEkXiA4TO9UHbfScU8rfCw6fPXJZYrGyBTyvN5Pzm/W4Ec3s+hSX5KnJh9a13TaB/qpUfh7IO3FfX9ulNe7g1qzmS1OGbLTGLMcHb2FfHsldp2l/XIBHkEUEiedTRTRvGGI6cuZp0sjacwxO0VRCZE6BBcFqjoJthikWMKtuUu1QTQMMDLvJMM7P2EX7Rm2JEy7zBlGcBc8hTqhJUBuS2i4ERjMzc8envBn365DYYuLz22UuVNMSXHfcU8IFBDgjbyGwZ3ubQIDAQAB"

    private lateinit var controller: Controller
    private lateinit var triangle: Triangle
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        addOrCloseAD()

        controller = Controller(this)
        triangle = Triangle()
        insertDBTableTriangle()
        triangle = selectDBTableTriangle()
        triangle_view.triangle = selectDBTableTriangle()
        Log.d(TAG, "База данных $DATABASE_NAME загружена в модель")


        setTriangleToTextView()
        checkEdit()

        btn_calculate.setOnClickListener { calculate() }
        btn_ubrat_reclamu.setOnClickListener { showAdsDialog() }

    }


    private fun showAdsDialog() {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.remove_ads_dialog, null)
        val builder = AlertDialog.Builder(this).setView(mDialogView)
        val showDialod = builder.show()

        mDialogView.ads_cancel.setOnClickListener { showDialod.dismiss() }
        mDialogView.ads_ok.setOnClickListener {
            val check = mDialogView.radioGroup.checkedRadioButtonId
            when (check) {
                R.id.radioFreeRemove -> addRevardedAD()
                R.id.radioCashRemove -> removeAdsForever()
            }
            showDialod.dismiss()
        }
    }

    private fun removeAdsForever() {
    }

    private fun isTimeUp(): Boolean {
        return System.currentTimeMillis() > prefs.estimatedAdsTime
    }

    override fun setTriangle(): Triangle {
        return triangle.apply {
            A = editText_A.notEmptyToFloat()
            B = editText_B.notEmptyToFloat()
            C = editText_C.notEmptyToFloat()

            uA = editText_angle_a.notEmptyToFloat()
            uB = editText_angle_b.notEmptyToFloat()
            uC = editText_angle_c.notEmptyToFloat()
        }
    }

    private fun calculate() {
        triangle = controller.calculate()

        when (triangle.correctTriangle) {
            CorrectTriangle.IS_TRIANGLE -> {
                message_text.visibility = View.GONE
                data_text.visibility = View.VISIBLE
                triangle_view.updateCanvas()

                updateDBTableTriangle()
                addInterstitialAD()
            }
            CorrectTriangle.IS_NOT_TRIANGLE -> {
                data_text.visibility = View.INVISIBLE
                message_text.visibility = View.VISIBLE
                message_text.setTextColor(ContextCompat.getColor(this, R.color.colorAllert))
                message_text.text = resources.getText(R.string.message_not_triangle)
            }
            CorrectTriangle.INCORRECT_DATA -> {
                data_text.visibility = View.INVISIBLE
                message_text.visibility = View.VISIBLE
                message_text.setTextColor(ContextCompat.getColor(this, R.color.colorAllert))
                message_text.text = resources.getText(R.string.message_not_data)
            }

        }

        setTriangleToTextView()
        triangle_view.triangle = triangle
        addOrCloseAD()
    }

    private fun setTriangleToTextView() {
        text_a.text = String.format("A: %1$.2f", triangle.A)
        text_b.text = String.format("B: %1$.2f", triangle.B)
        text_c.text = String.format("C: %1$.2f", triangle.C)

        text_A.text = String.format("α: %1$.2f", triangle.uA)
        text_B.text = String.format("β: %1$.2f", triangle.uB)
        text_C.text = String.format("γ: %1$.2f", triangle.uC)

        text_P.text = String.format("P: %1$.2f", triangle.P)
        text_S.text = String.format("S: %1$.2f", triangle.S)
    }

    private fun checkEdit() {
        checkBox_A.checkEditText(editText_A)
        checkBox_B.checkEditText(editText_B)
        checkBox_C.checkEditText(editText_C)
        checkBox_angle_a.checkEditText(editText_angle_a)
        checkBox_angle_b.checkEditText(editText_angle_b)
        checkBox_angle_c.checkEditText(editText_angle_c)
    }

    private fun insertDBTableTriangle() {
        database.use {
            insert(TABLE_NAME_TRIANGLE, ID to 1, COL_A to triangle.A, COL_B to triangle.B, COL_C to triangle.C)
        }
    }

    private fun updateDBTableTriangle() {
        database.use {
            update(TABLE_NAME_TRIANGLE, ID to 1, COL_A to triangle.A, COL_B to triangle.B, COL_C to triangle.C).whereArgs("id = {Id}", "Id" to 1).exec()
            Log.d(TAG, "Таблица $TABLE_NAME_TRIANGLE в базе $DATABASE_NAME обновлена")
        }
    }

    private fun selectDBTableTriangle(): Triangle {
        return database.use {
            select(TABLE_NAME_TRIANGLE).exec {
                parseSingle(object : MapRowParser<Triangle> {
                    override fun parseRow(columns: Map<String, Any?>): Triangle {
                        val A = columns.getValue(COL_A).toString().toFloat()
                        val B = columns.getValue(COL_B).toString().toFloat()
                        val C = columns.getValue(COL_C).toString().toFloat()
                        return Triangle().apply {
                            this.A = A
                            this.B = B
                            this.C = C
                        }.calculate()
                    }
                })
            }
        }
    }


    private fun addInterstitialAD() {
        if (Ads.mInterstitialAd !== null && Ads.mInterstitialAd!!.isLoaded) {
            Log.d(TAG, "Реклама загружена")
            Ads.mInterstitialAd!!.show()
        } else {
            Log.d(TAG, "Реклама не загружена")
        }
    }

    private fun addRevardedAD() {
        if (Ads.mRewardedVideoAd !== null && Ads.mRewardedVideoAd!!.isLoaded) {
            Log.d(TAG, "Реклама загружена")
            Ads.mRewardedVideoAd!!.show()
        } else {
            Log.d(TAG, "Реклама не загружена")
            val toast = Toast.makeText(this, resources.getText(R.string.toast), Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }
    }

    private fun addOrCloseAD() {
        //btn_ubrat_reclamu.visibility = View.VISIBLE
        if (URLConnection.isNetAvailable && isTimeUp() && !prefs.isAdsDisabled) {
            Ads.enableAds(this)
        } else {
            Ads.disableAds(this)
        }
    }

}

