package ua.varandas.trianglecalculator.firebase

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.Button
import com.google.android.gms.ads.*
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import org.jetbrains.anko.find
import ua.varandas.trianglecalculator.R
import ua.varandas.trianglecalculator.ext.prefs


object Ads {

    private const val TAG = "AdMob"
    private const val ADMOB_APP_ID = "ca-app-pub-6163958446304162~9593770830"
    private const val INTERSTITIAL_UNIT_ID = "ca-app-pub-6163958446304162/7906118797"
    private const val REWARDED_UNIT_ID = "ca-app-pub-6163958446304162/5476595680"

    var mInterstitialAd: InterstitialAd? = null
    var mRewardedVideoAd: RewardedVideoAd? = null

    private val adRequest: AdRequest
        get() = AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .addTestDevice("69DA1B2E4BFAC8EE86151137AFFCCD1B")
                .build()

    fun disableAds(activity: Activity) {

        val adView = activity.find<AdView>(R.id.adView)
        val ubratReclamuBtn = activity.find<Button>(R.id.btn_ubrat_reclamu)

        Log.d(TAG, "Реклама Отключена")
        ubratReclamuBtn.visibility = View.GONE
        adView.visibility = View.GONE
        mInterstitialAd = null
        mRewardedVideoAd = null

    }

    fun enableAds(activity: Activity){

        val adView = activity.find<AdView>(R.id.adView)
        val ubratReclamuBtn = activity.find<Button>(R.id.btn_ubrat_reclamu)

        Log.d(TAG, "Реклама включена")
        MobileAds.initialize(activity, ADMOB_APP_ID)
        adView.loadAd(adRequest)
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d(TAG, "Реклама загружена")
                ubratReclamuBtn.visibility = View.VISIBLE
                adView.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(p0: Int) {
                super.onAdFailedToLoad(p0)
                Log.d(TAG, "Реклама не загружена!  КОД_ОШИБКИ: $p0")
            }
        }

        mInterstitialAd = InterstitialAd(activity)
        mInterstitialAd!!.adUnitId = INTERSTITIAL_UNIT_ID
        mInterstitialAd!!.loadAd(adRequest)
        mInterstitialAd!!.adListener = object : AdListener() {

            override fun onAdClosed() {
                mInterstitialAd!!.loadAd(adRequest)
            }
        }
        loadRevardedAD(activity)

    }

    private fun loadRevardedAD(activity: Activity) {
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(activity)
        mRewardedVideoAd!!.loadAd(REWARDED_UNIT_ID, adRequest)
        mRewardedVideoAd!!.rewardedVideoAdListener = object : RewardedVideoAdListener {
            override fun onRewardedVideoAdClosed() {
                Log.d(TAG, "Видеореклама ЗАКРЫТА!")
            }

            override fun onRewardedVideoAdLeftApplication() {}

            override fun onRewardedVideoAdLoaded() {
                Log.d(TAG, "Видеореклама ЗАГРУЖЕНА")
            }

            override fun onRewardedVideoAdOpened() {
                Log.d(TAG, "Видеореклама ОТКРЫТА")
            }

            override fun onRewardedVideoCompleted() {
                Log.d(TAG, "Видеореклама ЗАВЕРШЕНА")
            }

            override fun onRewarded(p0: RewardItem?) {
                Log.d(TAG, "Видеореклама ПРОСМОТРЕНА $p0")
                prefs.estimatedAdsTime = System.currentTimeMillis() + ConstantHolder.DISABLE_2_HOURS
                Ads.disableAds(activity)
            }

            override fun onRewardedVideoStarted() {
                Log.d(TAG, "Видеореклама ЗАПУЩЕНА")
            }

            override fun onRewardedVideoAdFailedToLoad(p0: Int) {
                Log.d(TAG, "Видеореклама не загружена!  КОД_ОШИБКИ: $p0")
                mRewardedVideoAd!!.loadAd(REWARDED_UNIT_ID, adRequest)
            }
        }
    }

}