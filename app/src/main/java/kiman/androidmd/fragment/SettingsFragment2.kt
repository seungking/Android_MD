package kiman.androidmd.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.airbnb.lottie.LottieAnimationView
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.Constants
import com.anjlab.android.iab.v3.TransactionDetails
import com.google.android.play.core.review.ReviewManagerFactory
import es.dmoral.toasty.Toasty
import kiman.androidmd.MainActivity
import kiman.androidmd.OnBoardActivity
import kiman.androidmd.R
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment2: Fragment(),BillingProcessor.IBillingHandler{

    var bp: BillingProcessor? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val setting_lottie = view.findViewById<LottieAnimationView>(R.id.setting_lottie)
        setting_lottie.setAnimation("setting.json")
        setting_lottie.loop(true)
        setting_lottie.playAnimation()


        bp = BillingProcessor(
            context,
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2SyesnmGZxGjGlHvgFhVgX6lF12GWNDTaTx6hWgWkN2nH7VSBz5P5YPKLUBIDRsuZ/DtXLtC/SoO2dfAyrPdzxSqgkVHV6kOZ91jt8zOSu/iZ3ehXVhzNt8uQVDTLtyRDQ+5VHlxbFIbPumq/rtZ/BWoCwb9e/Jx3mIylKcgCM80StQ6QSd9pBxRZqymBT9BJCSMFEa0f11glX8aTTDLK+O7vh0aKJeLX1mALhdSPc/jCQB4EuY45MWFl3vwXXe7p2Itfqc64j4Rs8+qmjA8oBuaQncJgoY0jy0wiDl+KGiExI6sCdg5KWQjnen5Z0EmZyY093OydYw+0HhktQhgHwIDAQAB",
            this
        )

        val cat_motionLimit = view.findViewById<LinearLayout>(R.id.cat_motionLimit)
        val cat_appInfor = view.findViewById<LinearLayout>(R.id.cat_appInfor)
        cat_motionLimit?.setOnClickListener {view->
            Log.d("cat_motionLimit", "Activating")
            view.findNavController().navigate(R.id.action_limit_settings)
            (activity as MainActivity).setting_back.visibility = View.VISIBLE
        }
        cat_appInfor?.setOnClickListener {view->
            Log.d("cat_appInfor", "Activating")
            view.findNavController().navigate(R.id.action_appInfor_settings)
            (activity as MainActivity).setting_back.visibility = View.VISIBLE
        }
        val cat_feedback = view.findViewById<LinearLayout>(R.id.cat_feedback)
        cat_feedback?.setOnClickListener { view ->
            val intent = Intent(Intent.ACTION_SENDTO)
            val emailAddressss:Array<String> = arrayOf("email_example@gmail.com")
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, emailAddressss)
            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback")
            if (activity?.packageManager?.let { intent.resolveActivity(it) } != null) {
                startActivity(intent)
            }
        }
        val cat_review = view.findViewById<LinearLayout>(R.id.cat_review)
        cat_review?.setOnClickListener { view ->
            val manager = context?.let { ReviewManagerFactory.create(it) }
            //val manager = FakeReviewManager(context)
            val request = manager?.requestReviewFlow()
            request?.addOnCompleteListener { request ->
                if (request.isSuccessful) {
                    Log.d("review", "request is successful")
                    // We got the ReviewInfo object
                    val reviewInfo = request.result
                    val flow = activity?.let { manager.launchReviewFlow(it, reviewInfo) }
                    flow?.addOnCompleteListener { _ ->
                        Log.d("review", "addOnCompleteListener")
                        // The flow has finished. The API does not indicate whether the user
                        // reviewed or not, or even whether the review dialog was shown. Thus, no
                        // matter the result, we continue our app flow.
                    }
                } else {
                    Log.d("review", "request isn't successful")
                    // There was some problem, continue regardless of the result.
                }

            }

        }

        val cat_donation = view.findViewById<LinearLayout>(R.id.cat_donation)
        cat_donation?.setOnClickListener { view->
            bp!!.purchase(context as Activity?, "donation")
        }

        val cat_not = view.findViewById<LinearLayout>(R.id.cat_not)
        cat_not?.setOnClickListener { view->

            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            pref.edit().putBoolean("fromsetting",false).commit();
            startActivity(Intent(context, OnBoardActivity::class.java))
        }
        return view
    }

    override fun onProductPurchased(
        productId: String,
        details: TransactionDetails?
    ) {
        if (productId == "donation") {
            // TODO: 구매 해 주셔서 감사합니다! 메세지 보내기
            bp!!.isPurchased("donation")
            Toasty.success(requireContext(), "Thank you!", Toast.LENGTH_SHORT, true).show()
            bp!!.consumePurchase("donation")
        }
    }

    override fun onPurchaseHistoryRestored() {}

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        if (errorCode != Constants.BILLING_RESPONSE_RESULT_USER_CANCELED) {
            Toasty.info(requireContext(), "Billing Error.", Toast.LENGTH_SHORT, true).show()
        }
    }

    override fun onBillingInitialized() {}
}