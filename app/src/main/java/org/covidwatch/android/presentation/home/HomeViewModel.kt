package org.covidwatch.android.presentation.home

import android.app.Application
import androidx.lifecycle.*
import org.covidwatch.android.R
import org.covidwatch.android.data.CovidWatchDatabase
import org.covidwatch.android.data.Interaction
import org.covidwatch.android.data.TemporaryContactNumberDAO
import org.covidwatch.android.data.signedreport.SignedReportsDownloader
import org.covidwatch.android.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userFlowRepository: UserFlowRepository,
    private val testedRepository: TestedRepository,
    private val signedReportsDownloader: SignedReportsDownloader,
    private val ensureTcnIsStartedUseCase: EnsureTcnIsStartedUseCase,
    tcnDao: TemporaryContactNumberDAO,
    application: Application
) : AndroidViewModel(application), EnsureTcnIsStartedPresenter {

    private val isUserTestedPositive: Boolean get() = testedRepository.isUserTestedPositive()
    private val _userTestedPositive = MutableLiveData<Unit>()
    val userTestedPositive: LiveData<Unit> get() = _userTestedPositive

    private val _infoBannerState = MutableLiveData<InfoBannerState>()
    val infoBannerState: LiveData<InfoBannerState> get() = _infoBannerState

    private val _contactWarningBannerState = MutableLiveData<ContactWarningBannerState>()
    val contactWarningBannerState: LiveData<ContactWarningBannerState> get() = _contactWarningBannerState

    private val intrcRepository: InteractionRepository
    val nearbyInteractions: LiveData<List<Interaction>>
   // val countByInteractions: Int

    private val _userFlow = MutableLiveData<UserFlow>()
    val userFlow: LiveData<UserFlow> get() = _userFlow

    private val _isRefreshing = MediatorLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    private val hasPossiblyInteractedWithInfected: LiveData<Boolean> =
        tcnDao.allSortedByDescTimestamp()
            .map { it.fold(false) { infected, tcn -> infected || tcn.wasPotentiallyInfectious } }
            .asLiveData()

    private val interactedWithInfectedObserver =
        Observer<Boolean> { hasPossiblyInteractedWithInfected ->
            if (hasPossiblyInteractedWithInfected && !isUserTestedPositive) {
                _contactWarningBannerState.value = ContactWarningBannerState.Visible(R.string.contact_alert_text)
            }
        }

    init {
        hasPossiblyInteractedWithInfected.observeForever(interactedWithInfectedObserver)

        val intrDao = CovidWatchDatabase.getInstance(application).interactionDAO()
        intrcRepository = InteractionRepository(intrDao)
        nearbyInteractions = intrcRepository.nearbyInteractions
       // nearbyInteractions = intrcRepository.nearbyInteractionsByTime
        //countByInteractions = intrcRepository.countbyInteractionsByTime
    }

    override fun onCleared() {
        super.onCleared()
        hasPossiblyInteractedWithInfected.removeObserver(interactedWithInfectedObserver)
    }

    override fun showLocationPermissionBanner() {
        _infoBannerState.postValue(InfoBannerState.Visible(R.string.allow_location_access))
    }

    override fun showEnableBluetoothBanner() {
        _infoBannerState.postValue(InfoBannerState.Visible(R.string.turn_bluetooth_on))
    }

    override fun hideBanner() {
        _infoBannerState.postValue(InfoBannerState.Hidden)
    }

    fun onStart() {
        val userFlow = userFlowRepository.getUserFlow()
        if (userFlow is FirstTimeUser) {
            userFlowRepository.updateFirstTimeUserFlow()
        }
        if (userFlow !is Setup) {
            checkIfUserTestedPositive()
            ensureTcnIsStarted()
        }
        _userFlow.value = userFlow
    }

    fun onRefreshRequested() {
        val state = signedReportsDownloader.executePublicSignedReportsRefresh()
        _isRefreshing.addSource(state) {
            _isRefreshing.value = !it
            if (_isRefreshing.value == false) {
                _isRefreshing.removeSource(state)
            }
        }
    }

    fun checkIfUserTestedPositive(): Boolean {
        if (isUserTestedPositive) {
            _userTestedPositive.value = Unit
            //_contactWarningBannerState.value = ContactWarningBannerState.Visible(R.string.reported_alert_text)
            return true
        } else {
            return false
        }
    }

    private fun ensureTcnIsStarted() {
        viewModelScope.launch(Dispatchers.IO) {
            ensureTcnIsStartedUseCase.execute(this@HomeViewModel)
        }
    }
}