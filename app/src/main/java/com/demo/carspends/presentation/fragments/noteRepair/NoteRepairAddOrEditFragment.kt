package com.demo.carspends.presentation.fragments.noteRepair

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.demo.carspends.R
import com.demo.carspends.databinding.NoteRepairAddEditFragmentBinding
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.presentation.fragments.noteExtra.NoteExtraAddOrEditFragment
import com.demo.carspends.utils.getFormattedDate
import com.demo.carspends.utils.getFormattedDoubleAsStr
import com.demo.carspends.utils.ui.BaseFragment
import io.github.anderscheow.validator.Validator
import io.github.anderscheow.validator.rules.common.NotBlankRule
import io.github.anderscheow.validator.rules.common.NotEmptyRule
import io.github.anderscheow.validator.validation
import io.github.anderscheow.validator.validator
import java.util.*

class NoteRepairAddOrEditFragment : BaseFragment(R.layout.note_repair_add_edit_fragment) {
    override val binding: NoteRepairAddEditFragmentBinding by viewBinding()
    override val viewModel: NoteRepairAddOrEditViewModel by viewModels { viewModelFactory }
    override var setupListeners: (() -> Unit)? = {
        setupDatePickerListener()
        setupTextChangeListeners()
        setupApplyButtonClickListener()
    }
    override var setupObservers: (() -> Unit)? = {
        setupCanCloseScreenObserver()
        setupNoteDateObserver()
    }

    private lateinit var launchMode: String
    private var noteId = NoteItem.UNDEFINED_ID
    private var carId = CarItem.UNDEFINED_ID


    private val titleValidation by lazy {
        validation(binding.nraefTilName) {
            rules {
                +NotEmptyRule(ERR_EMPTY_TITLE)
                +NotBlankRule(ERR_BLANK_TITLE)
            }
        }
    }
    private val amountValidation by lazy {
        validation(binding.nraefTilAmountValue) {
            rules {
                +NotEmptyRule(ERR_EMPTY_AMOUNT)
                +NotBlankRule(ERR_BLANK_AMOUNT)
            }
        }
    }
    private val mileageValidation by lazy {
        validation(binding.nraefTilMileageValue) {
            rules {
                +NotEmptyRule(ERR_EMPTY_MILEAGE)
                +NotBlankRule(ERR_BLANK_MILEAGE)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCurrCarNote()
        chooseMode()
    }

    private fun setupCurrCarNote() {
        viewModel.setCarItem(carId)
    }

    private fun setupDatePickerListener() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val cal = GregorianCalendar.getInstance()
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                viewModel.setNoteDate(cal.time.time)
            }

        binding.nraefDateLayout.setOnClickListener {
            val cCal = GregorianCalendar.getInstance().apply {
                viewModel.noteDate.value?.let {
                    timeInMillis = it
                }
            }
            DatePickerDialog(
                requireContext(), dateSetListener,
                cCal.get(Calendar.YEAR),
                cCal.get(Calendar.MONTH),
                cCal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupTextChangeListeners() {
        binding.nraefTietName.addTextChangedListener {
            validator(requireActivity()) {
                listener = object : Validator.OnValidateListener {
                    override fun onValidateSuccess(values: List<String>) {}
                    override fun onValidateFailed(errors: List<String>) {}
                }
                validate(titleValidation)
            }
        }

        binding.nraefTietAmountValue.addTextChangedListener {
            validator(requireActivity()) {
                listener = object : Validator.OnValidateListener {
                    override fun onValidateSuccess(values: List<String>) {}
                    override fun onValidateFailed(errors: List<String>) {}
                }
                validate(amountValidation)
            }
        }

        binding.nraefTietMileageValue.addTextChangedListener {
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
        binding.nraefButtonApply.setOnClickListener {
            validator(requireActivity()) {
                listener = object : Validator.OnValidateListener {
                    override fun onValidateSuccess(values: List<String>) {
                        when (launchMode) {
                            ADD_MODE -> {
                                with(binding) {
                                    viewModel.addNoteItem(
                                        nraefTietName.text.toString(),
                                        nraefTietAmountValue.text.toString(),
                                        nraefTietMileageValue.text.toString()
                                    )
                                }
                            }
                            EDIT_MODE -> {
                                with(binding) {
                                    viewModel.editNoteItem(
                                        nraefTietName.text.toString(),
                                        nraefTietAmountValue.text.toString(),
                                        nraefTietMileageValue.text.toString()
                                    )
                                }
                            }
                        }
                    }

                    override fun onValidateFailed(errors: List<String>) {}
                }
                validate(titleValidation, amountValidation, mileageValidation)
            }
        }
    }

    private fun setupNoteDateObserver() {
        viewModel.noteDate.observe(viewLifecycleOwner) {
            binding.nraefTvDateValue.text = getFormattedDate(it)
        }
    }

    private fun setupCanCloseScreenObserver() {
        viewModel.canCloseScreen.observe(viewLifecycleOwner) {
            viewModel.goBack()
        }
    }

    private fun chooseMode() {
        when (launchMode) {
            ADD_MODE -> addNoteMode()
            else -> editNoteMode()
        }
    }

    private fun addNoteMode() {
        viewModel.currCarItem.observe(viewLifecycleOwner) {
            binding.nraefTietMileageValue.setText(it.mileage.toString())
        }
    }

    private fun editNoteMode() {
        viewModel.setItem(noteId)
        viewModel.noteItem.observe(viewLifecycleOwner) {
            with(binding) {
                nraefTietName.setText(it.title)
                nraefTietAmountValue.setText(getFormattedDoubleAsStr(it.totalPrice))
                nraefTietMileageValue.setText(it.mileage.toString())
            }
        }
    }

    private fun getArgs() {
        val args = requireArguments()
        if (!args.containsKey(MODE_KEY)) throw Exception("Empty mode argument for NoteRepairAddOrEditFragment")

        val type = args.getString(MODE_KEY)
        if (type != EDIT_MODE && type != ADD_MODE) throw Exception("Unknown mode argument for NoteRepairAddOrEditFragment: $type")

        launchMode = type
        if (!args.containsKey(CAR_ID_KEY)) throw Exception("CarItem id must be implemented for NoteRepairAddOrEditFragment")
        carId = args.getInt(CAR_ID_KEY, CarItem.UNDEFINED_ID)
        if (launchMode == EDIT_MODE && !args.containsKey(ID_KEY)) throw Exception("NoteItem id must be implemented for NoteRepairAddOrEditFragment")
        noteId = args.getInt(ID_KEY, NoteItem.UNDEFINED_ID)
    }

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    companion object {
        private const val ERR_EMPTY_TITLE = R.string.inappropriate_empty_title
        private const val ERR_BLANK_TITLE = R.string.blank_validation
        private const val ERR_EMPTY_AMOUNT = R.string.inappropriate_empty_amount
        private const val ERR_BLANK_AMOUNT = R.string.blank_validation
        private const val ERR_EMPTY_MILEAGE = R.string.inappropriate_empty_mileage
        private const val ERR_BLANK_MILEAGE = R.string.blank_validation

        private const val MODE_KEY = "mode_note"
        private const val ID_KEY = "id_note"
        private const val CAR_ID_KEY = "id_car"

        private const val EDIT_MODE = "edit_mode"
        private const val ADD_MODE = "add_mode"

        fun newAddInstance(carId: Int): NoteRepairAddOrEditFragment {
            return NoteRepairAddOrEditFragment().apply {
                arguments = Bundle().apply {
                    putString(MODE_KEY, ADD_MODE)
                    putInt(CAR_ID_KEY, carId)
                }
            }
        }

        fun newEditInstance(carId: Int, id: Int): NoteRepairAddOrEditFragment {
            return NoteRepairAddOrEditFragment().apply {
                arguments = Bundle().apply {
                    putString(MODE_KEY, EDIT_MODE)
                    putInt(CAR_ID_KEY, carId)
                    putInt(ID_KEY, id)
                }
            }
        }
    }
}
