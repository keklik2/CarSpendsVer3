package com.demo.carspends.presentation.fragments.noteFilling

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.demo.carspends.R
import com.demo.carspends.databinding.NoteFillingAddEditFragmentBinding
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.others.Fuel
import com.demo.carspends.utils.getFormattedDate
import com.demo.carspends.utils.getFormattedDoubleAsStr
import com.demo.carspends.utils.ui.BaseFragment
import io.github.anderscheow.validator.Validator
import io.github.anderscheow.validator.rules.common.NotBlankRule
import io.github.anderscheow.validator.rules.common.NotEmptyRule
import io.github.anderscheow.validator.validation
import io.github.anderscheow.validator.validator
import java.util.*

class NoteFillingAddOrEditFragment :
    BaseFragment(R.layout.note_filling_add_edit_fragment) {
    override val binding: NoteFillingAddEditFragmentBinding by viewBinding()
    override val viewModel: NoteFillingAddOrEditViewModel by viewModels { viewModelFactory }
    override var setupListeners: (() -> Unit)? = {
        setupDatePickerDialogListener()
        setupVolumeTextChangeListener()
        setupAmountTextChangeListener()
        setupPriceTextChangeListener()
        setupMileageTextChangeListener()
        setupApplyButtonClickListener()
    }
    override var setupBinds: (() -> Unit)? = {
        setupFieldsBind()
        setupNoteDateBind()
        setupLastFuelTypeBind()

        setupCanCloseScreenBind()
    }

    var lastChanged = CHANGED_NULL
    var preLastChanged = CHANGED_NULL
    var open = true


    /**
     * Validation
     */
    private val fuelVolumeValidation by lazy {
        validation(binding.nfaefTilFuelVolume) {
            rules {
                +NotEmptyRule(ERR_EMPTY_VOLUME)
                +NotBlankRule(ERR_BLANK_VOLUME)
            }
        }
    }
    private val fuelAmountValidation by lazy {
        validation(binding.nfaefTilFuelAmount) {
            rules {
                +NotEmptyRule(ERR_EMPTY_AMOUNT)
                +NotBlankRule(ERR_BLANK_AMOUNT)
            }
        }
    }
    private val fuelPriceValidation by lazy {
        validation(binding.nfaefTilFuelPrice) {
            rules {
                +NotEmptyRule(ERR_EMPTY_PRICE)
                +NotBlankRule(ERR_BLANK_PRICE)
            }
        }
    }
    private val mileageValidation by lazy {
        validation(binding.nfaefTilMileageValue) {
            rules {
                +NotEmptyRule(ERR_EMPTY_MILEAGE)
                +NotBlankRule(ERR_BLANK_MILEAGE)
            }
        }
    }


    /**
     * Listener functions
     */
    private fun setupDatePickerDialogListener() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val cal = GregorianCalendar.getInstance()
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                viewModel.nDate = cal.time.time
            }

        binding.nfaefDateLayout.setOnClickListener {
            val cCal = GregorianCalendar.getInstance().apply { timeInMillis = viewModel.nDate }
            DatePickerDialog(
                requireContext(), dateSetListener,
                cCal.get(Calendar.YEAR),
                cCal.get(Calendar.MONTH),
                cCal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupVolumeTextChangeListener() {
        binding.nfaefTietFuelVolume.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    preLastChanged = lastChanged
                    lastChanged = CHANGED_VOLUME
                }
            }

        binding.nfaefTietFuelVolume.addTextChangedListener {
            validator(requireActivity()) {
                listener = object : Validator.OnValidateListener {
                    override fun onValidateSuccess(values: List<String>) {}
                    override fun onValidateFailed(errors: List<String>) {}
                }
                validate(fuelVolumeValidation)
            }


            if (open) {
                open = false

                if (lastChanged != CHANGED_NULL && preLastChanged != CHANGED_NULL) {
                    if (preLastChanged == CHANGED_AMOUNT) viewModel.calculatePrice(
                        binding.nfaefTietFuelAmount.text.toString(),
                        binding.nfaefTietFuelVolume.text.toString()
                    )
                    else if (preLastChanged == CHANGED_PRICE) viewModel.calculateTotalPrice(
                        binding.nfaefTietFuelVolume.text.toString(),
                        binding.nfaefTietFuelPrice.text.toString()
                    )
                }
            }
            open = true
        }
    }

    private fun setupAmountTextChangeListener() {
        binding.nfaefTietFuelAmount.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    preLastChanged = lastChanged
                    lastChanged = CHANGED_AMOUNT
                }
            }

        binding.nfaefTietFuelAmount.addTextChangedListener {

            validator(requireActivity()) {
                listener = object : Validator.OnValidateListener {
                    override fun onValidateSuccess(values: List<String>) {}
                    override fun onValidateFailed(errors: List<String>) {}
                }
                validate(fuelAmountValidation)
            }

            if (open) {
                open = false

                if (lastChanged != CHANGED_NULL && preLastChanged != CHANGED_NULL) {
                    if (preLastChanged == CHANGED_VOLUME) viewModel.calculatePrice(
                        binding.nfaefTietFuelAmount.text.toString(),
                        binding.nfaefTietFuelVolume.text.toString()
                    )
                    else if (preLastChanged == CHANGED_PRICE) viewModel.calculateVolume(
                        binding.nfaefTietFuelAmount.text.toString(),
                        binding.nfaefTietFuelPrice.text.toString()
                    )
                }
            }
            open = true
        }
    }

    private fun setupPriceTextChangeListener() {
        binding.nfaefTietFuelPrice.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    preLastChanged = lastChanged
                    lastChanged = CHANGED_PRICE
                }
            }

        binding.nfaefTietFuelPrice.addTextChangedListener {

            validator(requireActivity()) {
                listener = object : Validator.OnValidateListener {
                    override fun onValidateSuccess(values: List<String>) {}
                    override fun onValidateFailed(errors: List<String>) {}
                }
                validate(fuelPriceValidation)
            }

            if (open) {
                open = false

                if (lastChanged != CHANGED_NULL && preLastChanged != CHANGED_NULL) {
                    if (preLastChanged == CHANGED_VOLUME) viewModel.calculateTotalPrice(
                        binding.nfaefTietFuelVolume.text.toString(),
                        binding.nfaefTietFuelPrice.text.toString()
                    )
                    else if (preLastChanged == CHANGED_AMOUNT) viewModel.calculateVolume(
                        binding.nfaefTietFuelAmount.text.toString(),
                        binding.nfaefTietFuelPrice.text.toString()
                    )
                }
            }
            open = true
        }
    }

    private fun setupMileageTextChangeListener() {
        binding.nfaefTietMileageValue.addTextChangedListener {
            validator(requireActivity()) {
                listener = object : Validator.OnValidateListener {
                    override fun onValidateSuccess(values: List<String>) {}
                    override fun onValidateFailed(errors: List<String>) {}
                }
                validate(mileageValidation)
            }
        }
    }

    private fun setupApplyButtonClickListener() {
        binding.nfaefButtonApply.setOnClickListener {
            validator(requireActivity()) {
                listener = object : Validator.OnValidateListener {
                    override fun onValidateSuccess(values: List<String>) {
                        with(binding) {
                            viewModel.addOrEditNoteItem(
                                nfaefSpinnerFuelType.selectedItemPosition,
                                nfaefTietFuelVolume.text.toString(),
                                nfaefTietFuelAmount.text.toString(),
                                nfaefTietFuelPrice.text.toString(),
                                nfaefTietMileageValue.text.toString()
                            )
                        }
                    }

                    override fun onValidateFailed(errors: List<String>) {}
                }
                validate(mileageValidation, fuelPriceValidation, fuelAmountValidation, fuelVolumeValidation)
            }
        }
    }

    private fun setupLastFuelTypeBind() =
        viewModel::lastFuelType bind {
            binding.nfaefSpinnerFuelType.setSelection(viewModel.getFuelId(it))
        }
    private fun setupNoteDateBind() =
        viewModel::nDate bind { binding.nfaefTvDateValue.text = getFormattedDate(it) }
    private fun setupCanCloseScreenBind() =
        viewModel::canCloseScreen bind { if (it) viewModel.goBack() }
    private fun setupFieldsBind() {
        with(viewModel) {
            with(binding) {
                ::nVolume bind { it?.let { nfaefTietFuelVolume.setText(it) } }
                ::nPrice bind { it?.let { nfaefTietFuelPrice.setText(it) } }
                ::nTotalPrice bind { it?.let { nfaefTietFuelAmount.setText(it) } }
                ::nMileage bind { it?.let { nfaefTietMileageValue.setText(it) } }
            }
        }
    }

    private fun setupFuelSpinnerAdapter() {
        // Setting Fuel enum values for spinner
        binding.nfaefSpinnerFuelType.adapter = ArrayAdapter(
            requireActivity(),
            R.layout.support_simple_spinner_dropdown_item,
            Fuel.values().map { it.toString(requireContext()) }
        )
    }

    private fun getArgs() {
        val args = requireArguments()
        if (!args.containsKey(MODE_KEY)) throw Exception("Empty mode argument for NoteFillingAddOrEditFragment")

        val type = args.getString(MODE_KEY)
        if (type != EDIT_MODE && type != ADD_MODE) throw Exception("Unknown mode argument for NoteFillingAddOrEditFragment: $type")

        if (!args.containsKey(CAR_ID_KEY)) throw Exception("CarItem id must be implemented for NoteFillingAddOrEditFragment")
        viewModel.cId = args.getInt(CAR_ID_KEY, CarItem.UNDEFINED_ID)
        if (type == EDIT_MODE && !args.containsKey(ID_KEY)) throw Exception("NoteItem id must be implemented for NoteFillingAddOrEditFragment")
        viewModel.nId = args.getInt(ID_KEY, NoteItem.UNDEFINED_ID)
    }


    /**
     * Base functions to make class work as fragment
     */
    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFuelSpinnerAdapter()
    }


    companion object {
        private const val CHANGED_NULL = -1
        private const val CHANGED_VOLUME = 10
        private const val CHANGED_AMOUNT = 20
        private const val CHANGED_PRICE = 30

        private const val ERR_EMPTY_VOLUME = R.string.inappropriate_empty_volume
        private const val ERR_BLANK_VOLUME = R.string.blank_validation
        private const val ERR_EMPTY_AMOUNT = R.string.inappropriate_empty_amount
        private const val ERR_BLANK_AMOUNT = R.string.blank_validation
        private const val ERR_EMPTY_PRICE = R.string.inappropriate_empty_price
        private const val ERR_BLANK_PRICE = R.string.blank_validation
        private const val ERR_EMPTY_MILEAGE = R.string.inappropriate_empty_mileage
        private const val ERR_BLANK_MILEAGE = R.string.blank_validation

        private const val MODE_KEY = "mode_note"
        private const val ID_KEY = "id_note"
        private const val CAR_ID_KEY = "id_car"

        private const val EDIT_MODE = "edit_mode"
        private const val ADD_MODE = "add_mode"

        fun newAddInstance(carId: Int): NoteFillingAddOrEditFragment {
            return NoteFillingAddOrEditFragment().apply {
                arguments = Bundle().apply {
                    putString(MODE_KEY, ADD_MODE)
                    putInt(CAR_ID_KEY, carId)
                }
            }
        }

        fun newEditInstance(carId: Int, id: Int): NoteFillingAddOrEditFragment {
            return NoteFillingAddOrEditFragment().apply {
                arguments = Bundle().apply {
                    putString(MODE_KEY, EDIT_MODE)
                    putInt(CAR_ID_KEY, carId)
                    putInt(ID_KEY, id)
                }
            }
        }
    }
}
