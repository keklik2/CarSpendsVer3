package com.demo.carspends.utils.ui.baseFragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.demo.carspends.CarSpendsApp
import com.demo.carspends.ViewModelFactory
import com.demo.carspends.utils.dialogs.AppAlertDialog
import com.demo.carspends.utils.dialogs.AppDialogContainer
import com.demo.carspends.utils.dialogs.AppItemDialogContainer
import com.demo.carspends.utils.dialogs.AppItemPickDialog
import com.demo.carspends.utils.ui.baseViewModel.BaseViewModel
import me.aartikov.sesame.property.PropertyObserver
import javax.inject.Inject

abstract class BaseFragment(layout: Int): Fragment(layout), PropertyObserver {

    override val propertyObserverLifecycleOwner: LifecycleOwner
        get() = viewLifecycleOwner

    /**
     * Methods & variables that must be override
     */
    abstract val binding: ViewBinding
    abstract val vm: BaseViewModel
    abstract var setupListeners: (() -> Unit)?
    abstract var setupBinds: (() -> Unit)?


    /**
     * Methods & variables that are independent of input data
     */
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    val component by lazy {
        (requireActivity().application as CarSpendsApp).component
    }

    fun makeToast(message: String, isLong: Boolean = false) {
        Toast.makeText(requireActivity(), message, if(isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
    }

    fun makeAlert(container: AppDialogContainer) {
        AppAlertDialog(container).show(childFragmentManager, DIALOG_TAG)
    }

    fun makeItemListDialog(container: AppItemDialogContainer) {
        AppItemPickDialog(container).show(childFragmentManager, DIALOG_TAG)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners?.invoke()
        setupBinds?.invoke()
    }

    override fun onResume() {
        super.onResume()

        vm.showAlert bind { makeAlert(it) }
//        vm.showDatePicker bind {
//            DatePickerDialog(
//                requireContext(),
//                it.onDateSetListener,
//                it.calendar.get(Calendar.YEAR),
//                it.calendar.get(Calendar.MONTH),
//                it.calendar.get(Calendar.DAY_OF_MONTH)
//            ).show()
//        }
        vm.showToast bind { makeToast(it) }
        vm.showToastLong bind { makeToast(it, true) }
        vm.showItemListDialog bind { makeItemListDialog(it) }
    }

    companion object {
        private const val DIALOG_TAG = "DialogTag"
        private const val TAG = "vmTag"
    }
}
