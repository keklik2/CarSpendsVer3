package com.demo.carspends.presentation.fragments.noteExtra

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.demo.carspends.R
import com.demo.carspends.databinding.NoteExtraAddEditFragmentBinding
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.note.NoteItem.Companion.UNDEFINED_ID
import com.demo.carspends.utils.getFormattedDate
import com.demo.carspends.utils.getFormattedDoubleAsStr
import com.demo.carspends.utils.ui.BaseFragment
import io.github.anderscheow.validator.Validator
import io.github.anderscheow.validator.rules.common.NotBlankRule
import io.github.anderscheow.validator.rules.common.NotEmptyRule
import io.github.anderscheow.validator.validation
import io.github.anderscheow.validator.validator
import java.util.*

class NoteExtraAddOrEditFragment : BaseFragment(R.layout.note_extra_add_edit_fragment) {
    override val binding: NoteExtraAddEditFragmentBinding by viewBinding()
    override val viewModel: NoteExtraAddOrEditViewModel by viewModels { viewModelFactory }
    override var setupListeners: (() -> Unit)? = {
        setupDatePickerListener()
        setupOnAcceptButtonClickListener()

        setupTitleTextChangeListener()
        setupPriceTextChangeListener()
    }
    override var setupObservers: (() -> Unit)? = {
        setupNoteDateObserver()
        setupCanCloseScreenObserver()
    }


    private lateinit var launchMode: String
    private var noteId = UNDEFINED_ID
    private var carId = CarItem.UNDEFINED_ID

    private val titleValidation by lazy {
        validation(binding.neaefTilName) {
            rules {
                +NotEmptyRule(ERR_EMPTY_TITLE)
                +NotBlankRule(ERR_BLANK_TITLE)
            }
        }
    }
    private val amountValidation by lazy {
        validation(binding.neaefTilAmountValue) {
            rules {
                +NotEmptyRule(ERR_EMPTY_AMOUNT)
                +NotBlankRule(ERR_BLANK_AMOUNT)
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
        viewModel.setCarId(carId)
    }

    private fun setupNoteDateObserver() {
        viewModel.noteDate.observe(viewLifecycleOwner) {
            binding.neaefTvDateValue.text = getFormattedDate(it)
        }
    }

    private fun setupCanCloseScreenObserver() {
        viewModel.canCloseScreen.observe(viewLifecycleOwner) {
            viewModel.goBack()
        }
    }

    private fun setupDatePickerListener() {
        val dateSetListener = OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val cal = GregorianCalendar.getInstance()
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            viewModel.setNoteDate(cal.time.time)
        }

        binding.neaefDateLayout.setOnClickListener {
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

    private fun setupOnAcceptButtonClickListener() {
        binding.neaefButtonApply.setOnClickListener {
            validator(requireActivity()) {
                listener = object : Validator.OnValidateListener {
                    override fun onValidateSuccess(values: List<String>) {
                        when (launchMode) {
                            ADD_MODE -> {
                                with(binding) {
                                    viewModel.addNoteItem(
                                        neaefTietName.text.toString(),
                                        neaefTietAmountValue.text.toString()
                                    )
                                }
                            }
                            EDIT_MODE -> {
                                with(binding) {
                                    viewModel.editNoteItem(
                                        neaefTietName.text.toString(),
                                        neaefTietAmountValue.text.toString()
                                    )
                                }
                            }
                        }
                    }

                    override fun onValidateFailed(errors: List<String>) {}
                }
                validate(titleValidation, amountValidation)
            }
        }
    }

    private fun setupTitleTextChangeListener() {
        binding.neaefTietName.addTextChangedListener {
            validator(requireActivity()) {
                listener = object : Validator.OnValidateListener {
                    override fun onValidateSuccess(values: List<String>) {  }
                    override fun onValidateFailed(errors: List<String>) {  }
                }
                validate(titleValidation)
            }
        }
    }

    private fun setupPriceTextChangeListener() {
        binding.neaefTietAmountValue.addTextChangedListener {
            validator(requireActivity()) {
                listener = object : Validator.OnValidateListener {
                    override fun onValidateSuccess(values: List<String>) {  }
                    override fun onValidateFailed(errors: List<String>) {  }
                }
                validate(amountValidation)
            }
        }
    }

    private fun chooseMode() {
        when (launchMode) {
            EDIT_MODE -> editNoteMode()
            else -> Any()
        }
    }

    private fun editNoteMode() {
        viewModel.setItem(noteId)
        viewModel.noteItem.observe(viewLifecycleOwner) {
            with(binding) {
                neaefTietName.setText(it.title)
                neaefTietAmountValue.setText(getFormattedDoubleAsStr(it.totalPrice))
            }
        }
    }

    private fun getArgs() {
        val args = requireArguments()
        if (!args.containsKey(MODE_KEY)) throw Exception("Empty mode argument for NoteExtraAddOrEditFragment")

        val type = args.getString(MODE_KEY)
        if (type != EDIT_MODE && type != ADD_MODE) throw Exception("Unknown mode argument for NoteExtraAddOrEditFragment: $type")

        launchMode = type
        if (!args.containsKey(CAR_ID_KEY)) throw Exception("CarItem id must be implemented for NoteExtraAddOrEditFragment")
        carId = args.getInt(CAR_ID_KEY, CarItem.UNDEFINED_ID)
        if (launchMode == EDIT_MODE && !args.containsKey(ID_KEY)) throw Exception("NoteItem id must be implemented for NoteExtraAddOrEditFragment")
        noteId = args.getInt(ID_KEY, UNDEFINED_ID)
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

        private const val MODE_KEY = "mode_note"
        private const val CAR_ID_KEY = "id_car"
        private const val ID_KEY = "id_note"

        private const val EDIT_MODE = "edit_mode"
        private const val ADD_MODE = "add_mode"

        fun newAddInstance(carId: Int): NoteExtraAddOrEditFragment {
            return NoteExtraAddOrEditFragment().apply {
                arguments = Bundle().apply {
                    putString(MODE_KEY, ADD_MODE)
                    putInt(CAR_ID_KEY, carId)
                }
            }
        }

        fun newEditInstance(carId: Int, id: Int): NoteExtraAddOrEditFragment {
            return NoteExtraAddOrEditFragment().apply {
                arguments = Bundle().apply {
                    putString(MODE_KEY, EDIT_MODE)
                    putInt(CAR_ID_KEY, carId)
                    putInt(ID_KEY, id)
                }
            }
        }
    }
}
