package kiman.androidmd

import android.annotation.SuppressLint
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
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import me.saket.inboxrecyclerview.globalVisibleRect
import me.saket.inboxrecyclerview.page.ExpandablePageLayout
import me.saket.inboxrecyclerview.page.InterceptResult
import me.saket.inboxrecyclerview.page.SimplePageStateChangeCallbacks

class EmailThreadFragment : Fragment() {

  private val emailThreadPage by lazy { view!!.parent as ExpandablePageLayout }
  private val scrollableContainer by lazy { view!!.findViewById<ScrollView>(R.id.emailthread_scrollable_container) }
  private val subjectTextView by lazy { view!!.findViewById<TextView>(R.id.emailthread_subject) }
  private val byline1TextView by lazy { view!!.findViewById<TextView>(R.id.emailthread_byline1) }
  private val avatarImageView by lazy { view!!.findViewById<ImageView>(R.id.emailthread_avatar) }
  private val bodyTextView by lazy { view!!.findViewById<TextView>(R.id.emailthread_body) }
  private val collapseButton by lazy { view!!.findViewById<ImageButton>(R.id.emailthread_collapse) }
  private val attachmentContainer by lazy { view!!.findViewById<ViewGroup>(R.id.emailthread_attachment_container) }

  private val threadIds = BehaviorRelay.create<EmailThreadId>()
  private val onDestroys = PublishRelay.create<Any>()


  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedState: Bundle?): View? {
//    return inflater.inflate(R.layout.fragment_email_thread, container, false)


    val view: View = inflater.inflate(R.layout.fragment_email_thread, container,
            false)

    return view
  }

  override fun onViewCreated(view: View, savedState: Bundle?) {
    super.onViewCreated(view, savedState)
    Log.d("Log1","3333");
    if (savedState != null) {
      onRestoreInstanceState(savedState)
    }

//    threadIds
//        .map { EmailRepository.thread(id = it) }
//        .takeUntil(onDestroys)
//        .subscribe { render(it) }

    collapseButton.setOnClickListener {
      val display = activity!!.windowManager.defaultDisplay
      var stageWidth = display.width
      var stageHeight = display.height

//      var emailitem : InboxRecyclerView.ExpandedItem = InboxRecyclerView.ExpandedItem.EMPTY.copy(locationOnScreen = Rect(0, 0, 0, 500))
//      emailThreadPage.animatePageExpandCollapse(false, stageWidth, stageHeight, emailitem)
//      emailThreadPage.collapse(emailitem)

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
        Log.d("Log1","emailthread3");
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
    threadIds.accept(threadId)
  }

  @SuppressLint("SetTextI18n")
  private fun render(emailThread: Email.EmailThread) {
    val latestEmail = emailThread.emails.last()
    Log.d("Log1","4444");

    subjectTextView.text = emailThread.subject
    byline1TextView.text = "${emailThread.sender.name} — ${latestEmail.timestamp}"

    bodyTextView.text = latestEmail.body
    avatarImageView.setImageBitmap(emailThread.sender.profileImageRes!!)

    renderAttachments(emailThread)
  }

  private fun renderAttachments(thread: Email.EmailThread) {
    attachmentContainer.removeAllViews()

    View.inflate(context, R.layout.include_email_shipping_update, attachmentContainer)
  }
}
