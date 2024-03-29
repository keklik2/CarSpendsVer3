package com.demo.carspends.presentation.fragments.statistics.numerous

import android.app.DatePickerDialog
import android.content.Context
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.demo.carspends.R
import com.demo.carspends.databinding.NumerousFragmentBinding
import com.demo.carspends.utils.getFormattedDate
import com.demo.carspends.utils.ui.baseFragment.BaseFragment
import com.demo.carspends.utils.ui.tipShower.TipShower
import java.util.*

class NumerousFragment: BaseFragment(R.layout.numerous_fragment) {
    override val binding: NumerousFragmentBinding by viewBinding()
    override val vm: NumerousViewModel by viewModels { viewModelFactory }
    override var setupListeners: (() -> Unit)? = {
        setupDatesClickListener()
        setupRestoreDateListener()
    }
    override var setupBinds: (() -> Unit)? = {
        setupFieldsBind()
        setupShowTipBind()
    }

    private val tipShower by lazy {
        TipShower(requireActivity())
    }


    /**
     * Binds
     */
    private fun setupFieldsBind() {
        with(vm) {
            with(binding) {
                ::sAvgFuel bind { tvAvgFuel.text = it }
                ::sMomentFuel bind { tvMomentFuel.text = it }
                ::sAllFuel bind { tvAllFuel.text = it }
                ::sFuelPrice bind { tvAllFuelPrice.text = it }
                ::sMileagePrice bind { tvMileagePrice.text = it }
                ::sAllPrice bind { tvAllPrice.text = it }
                ::sAllMileage bind { tvAllMileage.text = it }
                ::startDate bind { startDateIb.text = getFormattedDate(it) }
                ::endDate bind { endDateIb.text = getFormattedDate(it) }
            }
        }
    }

    private fun setupShowTipBind() {
        vm::tipsCount bind {
            if (it < vm.tips.size && vm.isFirstLaunch)
                tipShower.showTip(vm.tips[it]) { vm.nextTip() }
        }
    }


    /**
     * Listeners functions
     */
    private fun setupDatesClickListener() {
        binding.startDateIb.setOnClickListener {
            val dateSetListener =
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    val cal = GregorianCalendar.getInstance()
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    vm.startDate = cal.time.time
                }

                val cCal = GregorianCalendar.getInstance().apply { timeInMillis = vm.startDate }
                DatePickerDialog(
                    requireContext(), dateSetListener,
                    cCal.get(Calendar.YEAR),
                    cCal.get(Calendar.MONTH),
                    cCal.get(Calendar.DAY_OF_MONTH)
                ).show()
        }

        binding.endDateIb.setOnClickListener {
            val dateSetListener =
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    val cal = GregorianCalendar.getInstance()
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    vm.endDate = cal.time.time
                }

            val cCal = GregorianCalendar.getInstance().apply { timeInMillis = vm.endDate }
            DatePickerDialog(
                requireContext(), dateSetListener,
                cCal.get(Calendar.YEAR),
                cCal.get(Calendar.MONTH),
                cCal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupRestoreDateListener() {
        binding.dateRestoreButton.setOnClickListener { vm.restoreDates() }
    }


    /**
     * Additional functions
     */


    /**
     * Base functions to make class work as fragment
     */
    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }
}
