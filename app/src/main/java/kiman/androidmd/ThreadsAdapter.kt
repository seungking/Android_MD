package kiman.androidmd

import android.R.attr.key
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxrelay2.PublishRelay


class ThreadsAdapter : ListAdapter<Email.EmailThread, EmailViewHolder>(Email.EmailThread.ItemDiffer()) {


  var ctx: Context? = null

  fun ThreadsAdapter(ctx: Context?) {
    this.ctx = ctx
  }

  val itemClicks = PublishRelay.create<Email.EmailThreadClicked>()!!

  init {
    setHasStableIds(true)
  }

  @SuppressLint("NewApi")
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailViewHolder {
    val threadLayout = LayoutInflater.from(parent.context).inflate(R.layout.list_email_thread, parent, false)
    ctx = threadLayout.context
    return EmailViewHolder(threadLayout, itemClicks)
  }

  override fun onBindViewHolder(holder: EmailViewHolder, position: Int) {

    holder.emailThread = getItem(position)
    holder.itemView.setOnClickListener{
      Log.d("Log1","Position in adapter : " + position.toString())
      val pref: SharedPreferences = PreferenceManager
        .getDefaultSharedPreferences(ctx)
      var editor  = pref.edit()
      editor.putInt("select",position)
      editor.commit()
      itemClicks.accept(Email.EmailThreadClicked(getItem(position), position))
    }
    holder.render()
  }

  override fun getItemId(position: Int): Long {
    return position.toLong()
  }
}

open class EmailViewHolder(
    itemView: View,
    itemClicks: PublishRelay<Email.EmailThreadClicked>
) : RecyclerView.ViewHolder(itemView) {

  private val bylineTextView = itemView.findViewById<TextView>(R.id.emailthread_item_byline)
  private val subjectTextView = itemView.findViewById<TextView>(R.id.emailthread_item_subject)
  private val bodyTextView = itemView.findViewById<TextView>(R.id.emailthread_item_body)
  private val avatarImageView = itemView.findViewById<ImageView>(R.id.emailthread_item_avatar)

  lateinit var emailThread: Email.EmailThread

//  init {
//    itemView.setOnClickListener {
//      Log.d("Log1","1111");
//      itemClicks.accept(Email.EmailThreadClicked(emailThread, itemId))
//    }
//  }

  @SuppressLint("SetTextI18n")
  open fun render() {
    val latestEmail = emailThread.emails.last()
    bylineTextView.text = "${emailThread.sender.name} — ${latestEmail.timestamp}"

    subjectTextView.text = emailThread.subject

    bodyTextView.apply {
      text = latestEmail.excerpt.replace("\n", " ")
      visibility = if (latestEmail.showBodyInThreads) View.VISIBLE else View.GONE
    }

    avatarImageView.setImageBitmap(emailThread.sender.profileImageRes!!)
  }
}