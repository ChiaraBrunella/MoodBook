package com.example.moodbook.ui.statistics


import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.Typeface.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.moodbook.R
import com.example.moodbook.databinding.FragmentStatisticsBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class StatisticsFragment : Fragment() {



    private var depressedPercent: Float = 0F
    private var angryPercent: Float = 0F
    private var happyPercent: Float = 0F
    private var sadPercent: Float = 0F
    private var sleepyPercent: Float = 0F
    private var neutralPercent: Float = 0F
    private var anxiousPercent: Float = 0F
    private var _binding: FragmentStatisticsBinding? = null
    private val db = FirebaseFirestore.getInstance()
    var mood = ArrayList<String?>()
    var moodid = ArrayList<Int>()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var sadCount: Int = 0
    private var happyCount: Int = 0
    private var depressedCount: Int = 0
    private var angryCount: Int = 0
    private var anxiousCount: Int = 0
    private var neutralCount: Int = 0
    private var sleepyCount: Int = 0


    // variable for our bar data.
    private lateinit var barData: BarData

    // variable for our bar data set.? = null

    // array list for storing entries.

    lateinit var arrayBars: Array <ProgressBar>
    private lateinit var arrayPercentage: Array <Float>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        val root: View = binding.root

       /* val textView: TextView = binding. statViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/
        //make sure to step up for current user
        val user = FirebaseAuth.getInstance().uid

        db.collection("users").document(user!!).collection("moodlog")
            .orderBy("id", Query.Direction.DESCENDING).addSnapshotListener(
                EventListener { docsnapshot, e ->

                    for (snapshot in docsnapshot!!) {
                        if (e != null) {
                            Log.w("Listen failed.", e)
                            return@EventListener
                        }
                        if (snapshot != null && snapshot.exists()) {

                            mood.add(snapshot.getString("moodtype"))
                            moodid.add(Integer.valueOf(snapshot["id"].toString()))
                            Log.d("", "Current data: " + snapshot.data)
                        } else {
                            Log.d("", "Current data: null")
                        }
                    }
                    if (moodid.size != 0) {
                        curmoodid = moodid[0]
                    } else {
                        curmoodid = 0
                    }
                    for (emo in mood) {
                        if (emo != null) {
                            Log.i("emo", emo)
                            when (emo){
                                "sad" -> sadCount++
                                "happy" -> happyCount++
                                "angry" -> angryCount++
                                "anxious" -> anxiousCount++
                                "neutral" -> neutralCount++
                                "depressed" -> depressedCount++
                                "sleepy" -> sleepyCount++
                            }
                            Log.d("", "sadcount: " + sadCount)
                            Log.d("", "happycount: " + happyCount)
                            binding.angryStats.text = angryCount.toString()
                            binding.depressedStats.text = depressedCount.toString()
                            binding.anxiousStats.text = anxiousCount.toString()
                            binding.happyStats.text = happyCount.toString()
                            binding.neuStats.text = neutralCount.toString()
                            binding.sadStats.text = sadCount.toString()
                            binding.sleepyStats.text = sleepyCount.toString()

                            angryPercent = (angryCount*100/mood.size).toFloat()
                            sadPercent = (sadCount*100/mood.size).toFloat()
                            neutralPercent = (neutralCount*100/mood.size).toFloat()
                            happyPercent = (happyCount*100/mood.size).toFloat()
                            depressedPercent = (depressedCount*100/mood.size).toFloat()
                            sleepyPercent = (sleepyCount*100/mood.size).toFloat()
                            anxiousPercent = (anxiousCount*100/mood.size).toFloat()
                            Log.d("", "happy perc: " + happyPercent)
                            Log.d("", "mood size: " + mood.size)


                            // add a lot of colors to list
                            val colors: ArrayList<Int> = ArrayList()
                            colors.add(resources.getColor(R.color.happy))
                            colors.add(resources.getColor(R.color.sad))
                            colors.add(resources.getColor(R.color.yellow))
                            colors.add(resources.getColor(R.color.angry))
                            colors.add(resources.getColor(R.color.depressed))
                            colors.add(resources.getColor(R.color.neutral))
                            colors.add(resources.getColor(R.color.sleepy))
                            /* bar chart
                            // initializing variable for bar chart.
                            barChart = binding.barChart;

                            // creating a new array list
                            barEntriesArrayList = ArrayList <BarEntry>();

                            // adding new entry to our array list with bar
                            // entry and passing x and y axis value to it.
                            barEntriesArrayList.add( BarEntry(1f, happyPercent))
                            barEntriesArrayList.add(BarEntry(2f, sadPercent))
                            barEntriesArrayList.add(BarEntry(3f, flushedPercent))
                            barEntriesArrayList.add( BarEntry(4f, angryPercent))
                            barEntriesArrayList.add(BarEntry(5f, depressedPercent))
                            barEntriesArrayList.add(BarEntry(6f, neutralPercent))
                            barEntriesArrayList.add(BarEntry(6f, sleepyPercent))

                            // creating a new bar data set.
                            barDataSet =  BarDataSet(barEntriesArrayList, "moods");

                            // creating a new bar data and
                            // passing our bar data set.
                            barData = BarData(barDataSet);

                            // below line is to set data
                            // to our bar chart.
                            barChart.setData(barData);

                            // adding color to our bar data set.
                            barDataSet.setColors(colors);

                            // setting text color.
                            barDataSet!!.setValueTextColor(Color.BLACK);

                            // setting text size
                            barDataSet.setValueTextSize(16f);
                            barChart.getDescription().setEnabled(false);*/

                            binding.progbarHappy.max = 100

                            arrayBars = arrayOf(binding.progbarHappy,binding.progbarSad, binding.progbarAnxious, binding.progbarAngry, binding.progbarDepressed, binding.progbarNeutral, binding.progbarSleepy)
                            arrayPercentage = arrayOf(happyPercent, sadPercent, anxiousPercent, angryPercent, depressedPercent, neutralPercent, sleepyPercent)
                            for (bar in arrayBars){
                                val progressDrawable: Drawable =
                                   bar.getProgressDrawable().mutate()
                                progressDrawable.setColorFilter(colors[arrayBars.indexOf(bar)], PorterDuff.Mode.SRC_IN)
                                bar.setProgressDrawable(progressDrawable)

                                ObjectAnimator.ofInt(bar, "progress", 0, arrayPercentage[arrayBars.indexOf(bar)].toInt())
                                    .setDuration(500)
                                    .start()
                            }



                            // pie chart
                            val pieChart = binding.piechartMood
        // on below line we are setting user percent value,
        // setting description as enabled and offset for pie chart
        pieChart.setUsePercentValues(true)
        pieChart.getDescription().setEnabled(false)
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

        // on below line we are setting drag for our pie chart
        pieChart.setDragDecelerationFrictionCoef(0.95f)

        // on below line we are setting hole
        // and hole color for pie chart
        pieChart.setDrawHoleEnabled(true)
        pieChart.setHoleColor(Color.WHITE)

        // on below line we are setting circle color and alpha
        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)

        // on  below line we are setting hole radius
        pieChart.setHoleRadius(58f)
        pieChart.setTransparentCircleRadius(61f)

        // on below line we are setting center text
        pieChart.setDrawCenterText(true)

        // on below line we are setting
        // rotation for our pie chart
        pieChart.setRotationAngle(0f)

        // enable rotation of the pieChart by touch
        pieChart.setRotationEnabled(true)
        pieChart.setHighlightPerTapEnabled(true)

        // on below line we are setting animation for our pie chart
        pieChart.animateY(1400, Easing.EaseInOutQuad)

        // on below line we are disabling our legend for pie chart
        pieChart.legend.isEnabled = false
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)

        // on below line we are creating array list and
        // adding data to it to display in pie chart
        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry(happyPercent))
        entries.add(PieEntry(sadPercent))
        entries.add(PieEntry(anxiousPercent))
        entries.add(PieEntry(angryPercent))
        entries.add(PieEntry(depressedPercent))
        entries.add(PieEntry(neutralPercent))
        entries.add(PieEntry(sleepyPercent))

        // on below line we are setting pie data set
        val dataSet = PieDataSet(entries, "Mobile OS")

        // on below line we are setting icons.
        dataSet.setDrawIcons(false)

        // on below line we are setting slice for pie
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f



        // on below line we are setting colors.
        dataSet.colors = colors

        // on below line we are setting pie data set
                            val data = PieData(dataSet)
                            data.setValueFormatter(PercentFormatter())
                            data.setValueTextSize(13f)
                            data.setValueTypeface(Typeface.DEFAULT)
                            data.setValueTextColor(Color.WHITE)
                            pieChart.setData(data)

                            // undo all highlights
                            pieChart.highlightValues(null)

                            // loading chart
                            pieChart.invalidate()
                        }
                    }
                })

        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

                        companion object {
                    private var curmoodid = 0
                    fun getmoodid(): Int {
                        return curmoodid
                    }
                }

}