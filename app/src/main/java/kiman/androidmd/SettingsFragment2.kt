package kiman.androidmd

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.testing.FakeReviewManager

class SettingsFragment2: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val cat_motionLimit = view.findViewById<LinearLayout>(R.id.cat_motionLimit)
        val cat_appInfor = view.findViewById<LinearLayout>(R.id.cat_appInfor)
        cat_motionLimit?.setOnClickListener {view->
            Log.d("cat_motionLimit", "Activating")
            view.findNavController().navigate(R.id.action_limit_settings)
        }
        cat_appInfor?.setOnClickListener {view->
            Log.d("cat_appInfor", "Activating")
            view.findNavController().navigate(R.id.action_appInfor_settings)
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

        }
        return view
    }

}