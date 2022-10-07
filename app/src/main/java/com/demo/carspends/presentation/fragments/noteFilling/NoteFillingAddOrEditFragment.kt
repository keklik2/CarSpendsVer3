package com.demo.carspends.presentation.fragments.noteFilling

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.demo.carspends.R
import com.demo.carspends.databinding.FragmentNoteFillingAddEditBinding
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.utils.getFormattedDate
import com.demo.carspends.utils.ui.baseFragment.NoteAddOrEditFragment
import io.github.anderscheow.validator.Validator
import io.github.anderscheow.validator.rules.common.NotBlankRule
import io.github.anderscheow.validator.rules.common.NotEmptyRule
import io.github.anderscheow.validator.validation
import io.github.anderscheow.validator.validator
import java.util.*

class NoteFillingAddOrEditFragment :
    NoteAddOrEditFragment(R.layout.fragment_note_filling_add_edit) {
    override val binding: FragmentNoteFillingAddEditBinding by viewBinding()
    override val vm: NoteFillingAddOrEditViewModel by viewModels { viewModelFactory }
    override var setupListeners: (() -> Unit)? = {
        setupDatePickerDialogListener()
        setupVolumeTextChangeListener()
        setupAmountTextChangeListener()
        setupPriceTextChangeListener()
        setupMileageTextChangeListener()
        setupApplyButtonClickListener()
        setupPicturesPickerListener()
        setupFuelTypeListener()
    }
    override var setupBinds: (() -> Unit)? = {
        setupFieldsBind()
        setupNoteDateBind()
        setupFuelTypeBind()
        setupPicturesRecyclerViewBind()
        setupCanCloseScreenBind()
    }

    var lastChanged = CHANGED_NULL
    var preLastChanged = CHANGED_NULL
    var open = true


    /**
     * Validation
     */
    private val fuelVolumeValidation by lazy {
        validation(binding.tilFuelLiters) {
            rules {
                +NotEmptyRule(ERR_EMPTY_VOLUME)
                +NotBlankRule(ERR_BLANK_VOLUME)
            }
        }
    }
    private val fuelAmountValidation by lazy {
        validation(binding.tilFuelTotalPrice) {
            rules {
                +NotEmptyRule(ERR_EMPTY_AMOUNT)
                +NotBlankRule(ERR_BLANK_AMOUNT)
            }
        }
    }
    private val fuelPriceValidation by lazy {
        validation(binding.tilFuelPrice) {
            rules {
                +NotEmptyRule(ERR_EMPTY_PRICE)
                +NotBlankRule(ERR_BLANK_PRICE)
            }
        }
    }
    private val mileageValidation by lazy {
        validation(binding.tilMileageValue) {
            rules {
                +NotEmptyRule(ERR_EMPTY_MILEAGE)
                +NotBlankRule(ERR_BLANK_MILEAGE)
            }
        }
    }


    /**
     * Binds
     */
    override fun setupPicturesRecyclerViewBind() {
        binding.picturesRv.adapter = pictureAdapter
        vm::pictures bind {
            pictureAdapter.submitList(it)
        }
    }

    private fun setupFuelTypeBind() {
        vm::fuelType bind { binding.btnFuelType.text = it }
        vm::fuelImage bind {
            binding.btnFuelType.setCompoundDrawablesWithIntrinsicBounds(
                null, null, requireActivity().getDrawable(it), null
            )
        }
    }

    private fun setupNoteDateBind() =
        vm::nDate bind { binding.dateIb.text = getFormattedDate(it) }

    private fun setupCanCloseScreenBind() =
        vm::canCloseScreen bind { if (it) vm.goBack() }

    private fun setupFieldsBind() {
        with(vm) {
            with(binding) {
                ::nVolume bind { it?.let { tietFuelLiters.setText(it) } }
                ::nPrice bind { it?.let { tietFuelPrice.setText(it) } }
                ::nTotalPrice bind { it?.let { tietFuelTotalPrice.setText(it) } }
                ::nMileage bind { it?.let { tietMileageValue.setText(it) } }
            }
        }
    }


    /**
     * Listeners
     */
    private fun setupFuelTypeListener() = binding.btnFuelType.setOnClickListener {
        vm.changeFuelType()
    }

    private fun setupPicturesPickerListener() = binding.addImgButton.setOnClickListener {
        openPicturesPicker()
    }

    private fun setupDatePickerDialogListener() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val cal = GregorianCalendar.getInstance()
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                vm.nDate = cal.time.time
            }

        binding.dateIb.setOnClickListener {
            val cCal = GregorianCalendar.getInstance().apply { timeInMillis = vm.nDate }
            DatePickerDialog(
                requireContext(), dateSetListener,
                cCal.get(Calendar.YEAR),
                cCal.get(Calendar.MONTH),
                cCal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupVolumeTextChangeListener() {
        binding.tietFuelLiters.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    preLastChanged = lastChanged
                    lastChanged = CHANGED_VOLUME
                }
            }

        binding.tietFuelLiters.addTextChangedListener {
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
                    if (preLastChanged == CHANGED_AMOUNT) {
                        vm.calculatePrice(
                            binding.tietFuelTotalPrice.text.toString(),
                            binding.tietFuelLiters.text.toString()
                        )
                    } else if (preLastChanged == CHANGED_PRICE) {
                        vm.calculateTotalPrice(
                            binding.tietFuelLiters.text.toString(),
                            binding.tietFuelPrice.text.toString()
                        )
                    }
                }
            }
            open = true
        }
    }

    private fun setupAmountTextChangeListener() {
        binding.tietFuelTotalPrice.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    preLastChanged = lastChanged
                    lastChanged = CHANGED_AMOUNT
                }
            }

        binding.tietFuelTotalPrice.addTextChangedListener {
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
                    if (preLastChanged == CHANGED_VOLUME) {
                        vm.calculatePrice(
                            binding.tietFuelTotalPrice.text.toString(),
                            binding.tietFuelLiters.text.toString()
                        )
                    } else if (preLastChanged == CHANGED_PRICE) {
                        vm.calculateVolume(
                            binding.tietFuelTotalPrice.text.toString(),
                            binding.tietFuelPrice.text.toString()
                        )
                    }
                }
            }
            open = true
        }
    }

    private fun setupPriceTextChangeListener() {
        binding.tietFuelPrice.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    preLastChanged = lastChanged
                    lastChanged = CHANGED_PRICE
                }
            }

        binding.tietFuelPrice.addTextChangedListener {
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
                    if (preLastChanged == CHANGED_VOLUME) {
                        vm.calculateTotalPrice(
                            binding.tietFuelLiters.text.toString(),
                            binding.tietFuelPrice.text.toString()
                        )
                    } else if (preLastChanged == CHANGED_AMOUNT) {
                        vm.calculateVolume(
                            binding.tietFuelTotalPrice.text.toString(),
                            binding.tietFuelPrice.text.toString()
                        )
                    }
                }
            }
            open = true
        }
    }

    private fun setupMileageTextChangeListener() {
        binding.tietMileageValue.addTextChangedListener {
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
        binding.buttonApply.setOnClickListener {
            validator(requireActivity()) {
                listener = object : Validator.OnValidateListener {
                    override fun onValidateSuccess(values: List<String>) {
                        with(binding) {
                            vm.addOrEditNoteItem(
                                tietFuelLiters.text.toString(),
                                tietFuelTotalPrice.text.toString(),
                                tietFuelPrice.text.toString(),
                                tietMileageValue.text.toString()
                            )
                        }
                    }

                    override fun onValidateFailed(errors: List<String>) {}
                }
                validate(
                    mileageValidation,
                    fuelPriceValidation,
                    fuelAmountValidation,
                    fuelVolumeValidation
                )
            }
        }
    }

    private fun getArgs() {
        val args = requireArguments()
        if (!args.containsKey(MODE_KEY)) throw Exception("Empty mode argument for NoteFillingAddOrEditFragment")

        val type = args.getString(MODE_KEY)
        if (type != EDIT_MODE && type != ADD_MODE) throw Exception("Unknown mode argument for NoteFillingAddOrEditFragment: $type")

        if (!args.containsKey(CAR_ID_KEY)) throw Exception("CarItem id must be implemented for NoteFillingAddOrEditFragment")
        vm.carId = args.getInt(CAR_ID_KEY, CarItem.UNDEFINED_ID)
        if (type == EDIT_MODE && !args.containsKey(ID_KEY)) throw Exception("NoteItem id must be implemented for NoteFillingAddOrEditFragment")
        vm.noteId = args.getInt(ID_KEY, NoteItem.UNDEFINED_ID)
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
