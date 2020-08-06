package org.covidwatch.android.presentation.test

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.covidwatch.android.presentation.test.model.TestDate
import org.covidwatch.android.presentation.util.combineAndCompute
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class TestQuestionsViewModel : ViewModel() {

    private val mFirestore: FirebaseFirestore
    private val _isTested = MutableLiveData<Boolean>()
    val isTested: LiveData<Boolean> get() = _isTested
    var timestamp: String = "na"

    private val simpleDateFormat: SimpleDateFormat by lazy {
        SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
    }
    private val _testDate = MutableLiveData<TestDate>()
    val testDate: LiveData<TestDate> get() = _testDate

    private val _isReportButtonVisible: MediatorLiveData<Boolean>
    val isReportButtonVisible: LiveData<Boolean> get() = _isReportButtonVisible

    init {
        _isReportButtonVisible = _isTested.combineAndCompute(_testDate) { isTested, testDate ->
            isTested && testDate.isChecked
        }
        _testDate.value = TestDate(simpleDateFormat.format(Date()), false)

        mFirestore = FirebaseFirestore.getInstance();
    }

    fun onDateSelected(date: Date) {
        _testDate.value = TestDate(simpleDateFormat.format(date), true)

        updateFirestore()
    }

    fun onRadioButtonClicked(isTested: Boolean) {
        _isTested.value = isTested

        updateFirestore()
    }

    fun updateFirestore(){
        val pattern = "yyyy.MM.dd G 'at' HH:mm:ss z"
        val simpleDateFormat = SimpleDateFormat(pattern)
        timestamp = simpleDateFormat.format(Date())

        //timestamp = SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z", Locale.getDefault()).format(new Date())
        val testquestions = mFirestore.collection("testquestions")
        testquestions.add(this)
    }
}