package com.demo.carspends.presentation.fragments.component

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.demo.carspends.R
import com.demo.carspends.databinding.ComponentAddEditFragmentBinding
import com.demo.carspends.domain.component.ComponentItem
import com.demo.carspends.utils.getFormattedDate
import com.demo.carspends.utils.ui.baseFragment.BaseFragment
import io.github.anderscheow.validator.Validator
import io.github.anderscheow.validator.rules.common.NotBlankRule
import io.github.anderscheow.validator.rules.common.NotEmptyRule
import io.github.anderscheow.validator.validation
import io.github.anderscheow.validator.validator
import java.util.*

class ComponentAddOrEditFragment : BaseFragment(R.layout.component_add_edit_fragment) {
    override val binding: ComponentAddEditFragmentBinding by viewBinding()
    override val viewModel: ComponentAddOrEditViewModel by viewModels { viewModelFactory }
    override var setupListeners: (() -> Unit)? = {
        setupDatePickerListener()
        setupTitleTextChangeListener()
        setupAmountTextChangeListener()
        setupMileageTextChangeListener()
        setupOnAcceptButtonClickListener()
    }
    override var setupBinds: (() -> Unit)? = {
        setupCanCloseScreenBind()
        setupFieldsBind()
        setupDateBind()
    }

    private val titleValidation by lazy {
        validation(binding.caefTilName) {
            rules {
                +NotEmptyRule(ERR_EMPTY_TITLE)
                +NotBlankRule(ERR_BLANK_TITLE)
            }
        }
    }
    private val mileageValidation by lazy {
        validation(binding.caefTilMileageValue) {
            rules {
                +NotEmptyRule(ERR_EMPTY_MILEAGE)
                +NotBlankRule(ERR_BLANK_MILEAGE)
            }
        }
    }
    private val resourceValidation by lazy {
        validation(binding.caefTilResourceValue) {
            rules {
                +NotEmptyRule(ERR_EMPTY_RESOURCE)
                +NotBlankRule(ERR_BLANK_RESOURCE)
            }
        }
    }


    /**
     * Bind methods are to make fragment observe values from viewModel
     */
    private fun setupDateBind() = viewModel::cDate bind { binding.caefTvDateValue.text = getFormattedDate(it) }
    private fun setupCanCloseScreenBind() = viewModel::canCloseScreen bind { if (it) viewModel.goBack() }
    private fun setupFieldsBind() {
        with(viewModel) {
            ::cTitle bind { it?.let { binding.caefTietName.setText(it) } }
            ::cResourceMileage bind { it?.let { binding.caefTietResourceValue.setText(it) } }
            ::cStartMileage bind { it?.let { binding.caefTietMileageValue.setText(it) } }
        }
    }


    /**
     * Listener methods are to get feedback from users
     */
    private fun setupDatePickerListener() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val cal = GregorianCalendar.getInstance()
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                viewModel.cDate = cal.time.time
            }

        binding.caefDateLayout.setOnClickListener {
            val cCal = GregorianCalendar.getInstance().apply {
                timeInMillis = viewModel.cDate
            }
            DatePickerDialog(
                requireContext(), dateSetListener,
                cCal.get(Calendar.YEAR),
                cCal.get(Calendar.MONTH),
                cCal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupTitleTextChangeListener() {
        binding.caefTietName.addTextChangedListener {
            validator(requireActivity()) {
                listener = object : Validator.OnValidateListener {
                    override fun onValidateFailed(errors: List<String>) {}
                    override fun onValidateSuccess(values: List<String>) {}
                }
                validate(titleValidation)
            }
        }
    }

    private fun setupAmountTextChangeListener() {
        binding.caefTietResourceValue.addTextChangedListener {
            validator(requireActivity()) {
                listener = object : Validator.OnValidateListener {
                    override fun onValidateFailed(errors: List<String>) {}
                    override fun onValidateSuccess(values: List<String>) {}
                }
                validate(resourceValidation)
            }
        }
    }

    private fun setupMileageTextChangeListener() {
        binding.caefTietMileageValue.addTextChangedListener {
            validator(requireActivity()) {
                listener = object : Validator.OnValidateListener {
                    override fun onValidateFailed(errors: List<String>) {}
                    override fun onValidateSuccess(values: List<String>) {}
                }
                validate(mileageValidation)
            }
        }
    }

    private fun setupOnAcceptButtonClickListener() {
        binding.caefButtonApply.setOnClickListener {
            validator(requireActivity()) {
                listener = object : Validator.OnValidateListener {
                    override fun onValidateSuccess(values: List<String>) {
                        with(binding) {
                            viewModel.addOrEditComponentItem(
                                caefTietName.text.toString(),
                                caefTietResourceValue.text.toString(),
                                caefTietMileageValue.text.toString()
                            )
                        }
                    }
                    override fun onValidateFailed(errors: List<String>) {}
                }
                validate(resourceValidation, titleValidation, mileageValidation)
            }
        }
    }


    /** Basic functions to make Class work as Fragment */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
    }

    private fun getArgs() {
        val args = requireArguments()
        if (!args.containsKey(MODE_KEY)) throw Exception("Empty mode argument for ComponentAddOrEditFragment")

        val type = args.getString(MODE_KEY)
        if (type != EDIT_MODE && type != ADD_MODE) throw Exception("Unknown mode argument for ComponentAddOrEditFragment: $type")

        /**
         * This check must be used when multiple cars allowed
         */
//        if (!args.containsKey(CAR_ID_KEY)) throw Exception("CarItem id must be implemented for ComponentAddOrEditFragment")
//        carId = args.getInt(CAR_ID_KEY, CarItem.UNDEFINED_ID)
        if (type == EDIT_MODE && !args.containsKey(ID_KEY)) throw Exception("ComponentItem id must be implemented for ComponentAddOrEditFragment")
        viewModel.cId = args.getInt(ID_KEY, ComponentItem.UNDEFINED_ID)
    }

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }


    companion object {
        private const val ERR_EMPTY_TITLE = R.string.inappropriate_empty_title
        private const val ERR_BLANK_TITLE = R.string.blank_validation
        private const val ERR_EMPTY_RESOURCE = R.string.inappropriate_empty_resource
        private const val ERR_BLANK_RESOURCE = R.string.blank_validation
        private const val ERR_EMPTY_MILEAGE = R.string.inappropriate_empty_mileage
        private const val ERR_BLANK_MILEAGE = R.string.blank_validation

        private const val MODE_KEY = "mode_component"
        private const val ID_KEY = "id_component"
        private const val CAR_ID_KEY = "id_car"

        private const val EDIT_MODE = "edit_mode"
        private const val ADD_MODE = "add_mode"

        fun newAddInstance(carId: Int): ComponentAddOrEditFragment {
            return ComponentAddOrEditFragment().apply {
                arguments = Bundle().apply {
                    putString(MODE_KEY, ADD_MODE)
                    putInt(CAR_ID_KEY, carId)
                }
            }
        }

        fun newEditInstance(carId: Int, id: Int): ComponentAddOrEditFragment {
            return ComponentAddOrEditFragment().apply {
                arguments = Bundle().apply {
                    putString(MODE_KEY, EDIT_MODE)
                    putInt(CAR_ID_KEY, carId)
                    putInt(ID_KEY, id)
                }
            }
        }
    }
}
