package com.bleapplication.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bleapplication.R
import com.bleapplication.modules.PreferenceManager
import kotlinx.android.synthetic.main.activity_intro_slider.*


class IntroSliderActivity : AppCompatActivity() {
    private var myViewPagerAdapter: MyViewPagerAdapter? = null
    private var layouts: IntArray? = null
    private var dotsLayout: LinearLayout? = null
    private var prefManager: PreferenceManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Checking for first time launch - before calling setContentView()
        prefManager = PreferenceManager(this)
//        if (!prefManager!!.isFirstTimeLaunch()) {
//            launchHomeScreen()
//            finish()
//        }
        setContentView(R.layout.activity_intro_slider)
        dotsLayout = findViewById<LinearLayout>(R.id.layoutDots)
        // layouts of all intro sliders
        layouts = intArrayOf(
            R.layout.activity_intro_slider1,
            R.layout.activity_intro_slider2,
            R.layout.activity_intro_slider3
        )
        myViewPagerAdapter = MyViewPagerAdapter()
        view_pager!!.adapter = myViewPagerAdapter
        view_pager!!.addOnPageChangeListener(viewPagerPageChangeListener)

        // adding bottom dots
        addBottomDots(0)

        // get reference to textview
        val btn_skip = findViewById(R.id.btn_skip) as TextView
        btn_skip.setOnClickListener { launchActivity() }
    }

    private fun addBottomDots(currentPage: Int) {
        var dots: Array<TextView?> = arrayOfNulls(layouts!!.size)

        dotsLayout!!.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]!!.text = Html.fromHtml("&#8226;")
            dots[i]!!.textSize = 35f
            dots[i]!!.setTextColor(resources.getColor(R.color.inactive_dot_color))
            dotsLayout!!.addView(dots[i])
        }

        if (dots.isNotEmpty())
            dots[currentPage]!!.setTextColor(resources.getColor(android.R.color.black))
    }

    private fun launchActivity() {
        prefManager!!.setLaunched(false)
        startActivity(Intent(this, PhoneNumberVerificationActivity::class.java))
        finish()
    }

    //	viewpager change listener
    private var viewPagerPageChangeListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {

        override fun onPageSelected(position: Int) {
            addBottomDots(position)
            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts!!.size - 1) {
                // last page. make button text to GOT IT
//                btn_next!!.text = getString(R.string.start)
//                btn_skip!!.visibility = View.GONE
            } else {
                // still pages are left
//                btn_next!!.text = getString(R.string.next)
//                btn_skip!!.visibility = View.VISIBLE
            }
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {
        }

        override fun onPageScrollStateChanged(arg0: Int) {
        }
    }

    /**
     * View pager adapter
     */
    inner class MyViewPagerAdapter : PagerAdapter() {
        private var layoutInflater: LayoutInflater? = null

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = layoutInflater!!.inflate(layouts!![position], container, false)
            container.addView(view)
            return view
        }

        override fun getCount(): Int {
            return layouts!!.size
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View
            container.removeView(view)
        }
    }
}
