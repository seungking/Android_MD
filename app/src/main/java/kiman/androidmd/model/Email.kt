package kiman.androidmd.model

import android.graphics.Bitmap
import androidx.recyclerview.widget.DiffUtil

typealias EmailThreadId = Long

data class Email(
    val body: String,
    val excerpt: String = body,
    val showBodyInThreads: Boolean = true,
    val timestamp: String
) {

    //Person
    data class Person(
        val name: String,
        val profileImageRes: Bitmap? = null
    )

    //EmailThread
    data class EmailThread(
        val id: EmailThreadId,
        val sender: Person,
        val subject: String,
        val emails: List<Email>,
        var active : String
    ) {

        //ItemDiffer
        class ItemDiffer : DiffUtil.ItemCallback<EmailThread>() {
            override fun areItemsTheSame(oldItem: EmailThread, newItem: EmailThread) = oldItem.subject == newItem.subject
            override fun areContentsTheSame(oldItem: EmailThread, newItem: EmailThread) = oldItem == newItem
        }
    }

    //EmailThreadClicked
    data class EmailThreadClicked(
        val thread: EmailThread,
        val itemId: Int
    )

}


