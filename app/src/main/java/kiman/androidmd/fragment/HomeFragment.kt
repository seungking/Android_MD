package kiman.androidmd.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxrelay2.PublishRelay
import kiman.androidmd.model.Email
import kiman.androidmd.MainActivity
import kiman.androidmd.service.ManagePref
import kiman.androidmd.R
import me.saket.inboxrecyclerview.InboxRecyclerView
import me.saket.inboxrecyclerview.dimming.TintPainter
import me.saket.inboxrecyclerview.page.ExpandablePageLayout
import java.util.*


class HomeFragment : Fragment(), MainActivity.IOnBackPressed {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar_home)
//        toolbar.inflateMenu(R.menu.menu_home)
//        toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected)
    }

    /////////////////////////////////////

    var recyclerView : InboxRecyclerView? = null
    var emailPageLayout : ExpandablePageLayout? = null

    val onDestroy = PublishRelay.create<Any>()

    ////////////////////////////////////////

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(
            R.layout.activity_inbox, container,
            false)
        recyclerView = view.findViewById(R.id.inbox_recyclerview)
        emailPageLayout = view.findViewById(R.id.inbox_email_thread_page)
        setupThreadList()
        setupThreadPage()
        return view
    }

    override fun onDestroy() {
        onDestroy.accept(Any())
        super.onDestroy()
    }

    override fun onBackPressed(): Boolean {
        recyclerView?.collapse()
        return false;
    }

    @SuppressLint("CheckResult")
    fun setupThreadList() {

        (activity as MainActivity).list.clear()

        recyclerView!!.layoutManager = LinearLayoutManager(activity)
        recyclerView!!.expandablePage = emailPageLayout
        recyclerView!!.tintPainter = TintPainter.uncoveredArea(color = Color.WHITE, opacity = 0.65F)

        Log.d("Log1","setupthreadlist");
        val managepref : ManagePref =
            ManagePref()

        var appname = ArrayList<String>()
        var packagename = ArrayList<String>()
        var appicon = ArrayList<String>()
        var date = ArrayList<String>()
        var switch = ArrayList<String>()
        var patterns = ArrayList<String>()
        appname = managepref.getStringArrayPref(activity!!.applicationContext, "appname")
        packagename = managepref.getStringArrayPref(activity!!.applicationContext, "packagename")
        appicon = managepref.getStringArrayPref(activity!!.applicationContext, "appicon")
        date = managepref.getStringArrayPref(activity!!.applicationContext, "date")
        switch = managepref.getStringArrayPref(activity!!.applicationContext, "switch")
        patterns = managepref.getStringArrayPref(activity!!.applicationContext, "patterns")

        for( i in 0 until (appname.size) ){
            var temp : Email.EmailThread =
                Email.EmailThread(
                    id = 0,
                    sender = Email.Person(
                        "MOTION 1",
                        managepref.StringToBitmap(appicon.get(i))
                    ),
                    subject = appname.get(i),
                    emails = listOf(
                        Email(
                            excerpt = date.get(i),
                            body = "MOTION 1",
                            timestamp = packagename.get(i)
                        )
                    ),
                    active = switch.get(i)
                )
            (activity as MainActivity).list.add(temp)
        }
        Log.d("Log1","list size : " + (activity as MainActivity).list.size)

        (activity as MainActivity).threadsAdapter.submitList((activity as MainActivity).list)
        recyclerView!!.adapter = (activity as MainActivity).threadsAdapter

        (activity as MainActivity).threadsAdapter.itemClicks
            .takeUntil(onDestroy)
            .subscribe {
                recyclerView!!.expandItem(0)
            }
    }

    @SuppressLint("CheckResult")
    fun setupThreadPage() {

        var threadFragment = activity?.supportFragmentManager?.findFragmentById(emailPageLayout!!.id) as EmailThreadFragment?
        if (threadFragment == null) {
            threadFragment = EmailThreadFragment()
        }

        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(emailPageLayout!!.id, threadFragment)
            ?.commit()

        (activity as MainActivity).threadsAdapter.itemClicks
            .map { it.thread.id }
            .takeUntil(onDestroy)
            .subscribe {
                threadFragment.populate(it)
            }
    }

}
