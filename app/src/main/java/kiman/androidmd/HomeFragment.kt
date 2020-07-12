package kiman.androidmd

import android.R.attr.bitmap
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxrelay2.PublishRelay
import me.saket.inboxrecyclerview.InboxRecyclerView
import me.saket.inboxrecyclerview.dimming.TintPainter
import me.saket.inboxrecyclerview.page.ExpandablePageLayout
import java.util.*


class HomeFragment : Fragment(), MainActivity.IOnBackPressed {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar_home)
//        toolbar.inflateMenu(R.menu.menu_home)
//        toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected)
    }

    /////////////////////////////////////

    var recyclerView : InboxRecyclerView? = null
    var emailPageLayout : ExpandablePageLayout? = null

    val onDestroy = PublishRelay.create<Any>()
    val threadsAdapter = ThreadsAdapter()
    ////////////////////////////////////////

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_home, container, false)
        val view: View = inflater.inflate(R.layout.activity_inbox, container,
            false)
        recyclerView = view.findViewById(R.id.inbox_recyclerview)
        emailPageLayout = view.findViewById(R.id.inbox_email_thread_page)
        setupThreadList()
        setupThreadPage()
        return view
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
////        btn_notify.setOnClickListener { HandleNotifications.showNotification(requireContext()) }
//    }
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
////        val destination = when (item?.itemId) {
////            R.id.search -> R.id.action_search
////            else -> null
////        }
////
////        return if (destination != null) findNavController().navigate(destination).let { true }
////        else super.onOptionsItemSelected(item)
//    }

    override fun onDestroy() {
        onDestroy.accept(Any())
        super.onDestroy()
    }

    override fun onBackPressed(): Boolean {
        recyclerView?.collapse()

//        }
        return false;
    }

    @SuppressLint("CheckResult")
    private fun setupThreadList() {
        recyclerView!!.layoutManager = LinearLayoutManager(activity)
        recyclerView!!.expandablePage = emailPageLayout
        recyclerView!!.tintPainter = TintPainter.uncoveredArea(color = Color.WHITE, opacity = 0.65F)

        Log.d("Log1","setupthreadlist1");
        val managepref : ManagePref =  ManagePref()

        var appname = ArrayList<String>()
        var packagename = ArrayList<String>()
        var appicon = ArrayList<String>()
        var date = ArrayList<String>()
        Log.d("Log1","setupthreadlist2");
        appname = managepref.getStringArrayPref(activity!!.applicationContext, "appname")
        packagename = managepref.getStringArrayPref(activity!!.applicationContext, "packagename")
        appicon = managepref.getStringArrayPref(activity!!.applicationContext, "appicon")
        date = managepref.getStringArrayPref(activity!!.applicationContext, "date")
        Log.d("Log1","setupthreadlist3 appname size : " + appname.size + packagename.size + appicon.size + date.size);
        val list = mutableListOf<Email.EmailThread>();

        Log.d("Log1","insert start")
        for( i in 0 until (appname.size) ){
            Log.d("LOG1",i.toString())
            Log.d("Log1",appname.get(i) + "  " +  packagename.get(i));
            var temp : Email.EmailThread = Email.EmailThread(
                id = 0,
                sender = Email.Person("MOTION 1", managepref.StringToBitmap(appicon.get(i))),
                subject = appname.get(i),
                emails = listOf(
                    Email(
                        excerpt = date.get(i),
                        body = "MOTION 1",
                        timestamp = packagename.get(i))))
            list.add(temp)
        }
        Log.d("Log1","list size : " + list.size)

//        threadsAdapter.submitList(EmailRepository.threads())
        threadsAdapter.submitList(list)
        recyclerView!!.adapter = threadsAdapter

        threadsAdapter.itemClicks
            .takeUntil(onDestroy)
            .subscribe {
                recyclerView!!.expandItem(it.itemId)
            }
    }

    @SuppressLint("CheckResult")
    private fun setupThreadPage() {

        var threadFragment = activity?.supportFragmentManager?.findFragmentById(emailPageLayout!!.id) as EmailThreadFragment?
        if (threadFragment == null) {
            Log.d("Log1","6666")
            threadFragment = EmailThreadFragment()
        }

        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(emailPageLayout!!.id, threadFragment)
//                ?.commitNowAllowingStateLoss()
            ?.commit()

        threadsAdapter.itemClicks
            .map { it.thread.id }
            .takeUntil(onDestroy)
            .subscribe {
                threadFragment.populate(it)
            }
    }

}
