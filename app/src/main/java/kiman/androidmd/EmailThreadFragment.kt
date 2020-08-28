package kiman.androidmd

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.suke.widget.SwitchButton
import kotlinx.android.synthetic.main.include_email_shipping_update.view.*
import me.saket.inboxrecyclerview.globalVisibleRect
import me.saket.inboxrecyclerview.page.ExpandablePageLayout
import me.saket.inboxrecyclerview.page.InterceptResult
import me.saket.inboxrecyclerview.page.SimplePageStateChangeCallbacks
import java.util.*

class EmailThreadFragment : Fragment() {

  private val emailThreadPage by lazy { view!!.parent as ExpandablePageLayout }
  private val scrollableContainer by lazy { view!!.findViewById<ScrollView>(R.id.emailthread_scrollable_container) }
  private val subjectTextView by lazy { view!!.findViewById<TextView>(R.id.emailthread_subject) }
  private val byline1TextView by lazy { view!!.findViewById<TextView>(R.id.emailthread_byline1) }
  private val avatarImageView by lazy { view!!.findViewById<ImageView>(R.id.emailthread_avatar) }
  private val bodyTextView by lazy { view!!.findViewById<TextView>(R.id.emailthread_body) }
  private val collapseButton by lazy { view!!.findViewById<ImageButton>(R.id.emailthread_collapse) }
  private val attachmentContainer by lazy { view!!.findViewById<ViewGroup>(R.id.emailthread_attachment_container) }
  private val switch_motion_fg by lazy { view!!.findViewById<SwitchButton>(R.id.switch_motion_fg) }
  private val remove_button by lazy { view!!.findViewById<ImageView>(R.id.detail_button2) }

  private val threadIds = BehaviorRelay.create<EmailThreadId>()
  private val onDestroys = PublishRelay.create<Any>()

  var appname = ArrayList<String>()
  var packagename = ArrayList<String>()
  var appicon = ArrayList<String>()
  var date = ArrayList<String>()
  var switch = ArrayList<String>()
  var patterns = ArrayList<String>()

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedState: Bundle?): View? {
    val view: View = inflater.inflate(R.layout.fragment_email_thread, container,
      false)
    return view
  }

  override fun onViewCreated(view: View, savedState: Bundle?) {
    super.onViewCreated(view, savedState)

    if (savedState != null) {
      onRestoreInstanceState(savedState)
    }

    collapseButton.setOnClickListener {
      requireActivity().onBackPressed()
    }

    emailThreadPage.pullToCollapseInterceptor = { downX, downY, upwardPull ->
      if (scrollableContainer.globalVisibleRect().contains(downX, downY).not()) {
        Log.d("Log1","emailthread1");
        InterceptResult.IGNORED
      } else {
        Log.d("Log1","emailthread2");
        val directionInt = if (upwardPull) +1 else -1
        val canScrollFurther = scrollableContainer.canScrollVertically(directionInt)
        when {
          canScrollFurther -> InterceptResult.INTERCEPTED
          else -> InterceptResult.IGNORED
        }
      }
    }

    emailThreadPage.addStateChangeCallbacks(object : SimplePageStateChangeCallbacks() {
      override fun onPageCollapsed() {
        Log.d("Log1","emailthread3 " + (activity as MainActivity).list.size.toString());
        scrollableContainer.scrollTo(0, 0)
      }
    })
  }

  override fun onDestroyView() {
    Log.d("Log1","emailthread4");
    onDestroys.accept(Any())
    super.onDestroyView()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    if (threadIds.hasValue()) {
      outState.putLong("thread_id", threadIds.value)
    }
    super.onSaveInstanceState(outState)
  }

  private fun onRestoreInstanceState(savedState: Bundle) {
    val retainedThreadId: Long? = savedState.getLong("thread_id")
    if (retainedThreadId != null) {
      threadIds.accept(retainedThreadId)
    }
  }

  fun populate(threadId: EmailThreadId) {
    val pref: SharedPreferences = PreferenceManager
      .getDefaultSharedPreferences(view?.context)
    val userObject = pref.getInt("select", 0)
    threadIds.accept(threadId)
    view?.let { render(it, userObject) }
  }

  @SuppressLint("SetTextI18n")
  private fun render(view: View, position : Int) {

    Log.d("Log1","render in fragment  position : "  + position);
    val managepref : ManagePref =  ManagePref()

    appname = managepref.getStringArrayPref(activity!!.applicationContext, "appname")
    packagename = managepref.getStringArrayPref(activity!!.applicationContext, "packagename")
    appicon = managepref.getStringArrayPref(activity!!.applicationContext, "appicon")
    date = managepref.getStringArrayPref(activity!!.applicationContext, "date")
    switch = managepref.getStringArrayPref(activity!!.applicationContext, "switch")
    patterns = managepref.getStringArrayPref(activity!!.applicationContext, "patterns")
    val list = mutableListOf<Email.EmailThread>();

    subjectTextView.text = appname.get(position)
    byline1TextView.text = packagename.get(position)

    bodyTextView.text = date.get(position)
    avatarImageView.setImageBitmap(managepref.StringToBitmap(appicon.get(position)))

    attachmentContainer.removeAllViews()

    if(switch.get(position)=="on") {
      switch_motion_fg.setChecked(true)
    };
    else {
      switch_motion_fg.setChecked(false)
    };

    switch_motion_fg.setOnCheckedChangeListener { CompoundButton, onSwitch ->
      if (onSwitch) {
        switch.set(position,"on")
        (activity as MainActivity).threadsAdapter.changeswitch(position,"on")
      };
      else {
        switch.set(position,"off")
        (activity as MainActivity).threadsAdapter.changeswitch(position,"off")
      };

      managepref.setStringArrayPref(activity!!.applicationContext, "switch", switch)

      (activity as MainActivity).updatepattern()
    }


    var view : View = View.inflate(context, R.layout.include_email_shipping_update, attachmentContainer)

    view.detail_button2.setOnClickListener {

      requireActivity().onBackPressed()

      appname.removeAt(position)
      packagename.removeAt(position)
      appicon.removeAt(position)
      date.removeAt(position)
      switch.removeAt(position)
      patterns.removeAt(position)

      managepref.setStringArrayPref(activity!!.applicationContext,"appname",appname)
      managepref.setStringArrayPref(activity!!.applicationContext,"packagename",packagename)
      managepref.setStringArrayPref(activity!!.applicationContext,"appicon",appicon)
      managepref.setStringArrayPref(activity!!.applicationContext,"date",date)
      managepref.setStringArrayPref(activity!!.applicationContext,"switch",switch)
      managepref.setStringArrayPref(activity!!.applicationContext,"patterns",patterns)


      (activity as MainActivity).updatepattern()
      (activity as MainActivity).list.removeAt(position)
      (activity as MainActivity).threadsAdapter.submitList((activity as MainActivity).list)
      (activity as MainActivity).threadsAdapter.notifyItemRemoved(position)
      (activity as MainActivity).threadsAdapter.notifyItemRangeChanged(0,(activity as MainActivity).list.size)

    }
  }
}
