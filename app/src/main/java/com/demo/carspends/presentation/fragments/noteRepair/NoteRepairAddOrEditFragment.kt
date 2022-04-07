package com.demo.carspends.presentation.fragments.noteRepair

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.demo.carspends.R
import com.demo.carspends.databinding.NoteRepairAddEditFragmentBinding
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.utils.getFormattedDate
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
    override var setupBinds: (() -> Unit)? = {
        setupFieldsBind()
        setupCanCloseScreenBind()
    }


    /**
     * Validation
     */
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


    /**
     * Bind functions
     */
    private fun setupCanCloseScreenBind() =
        viewModel::canCloseScreen bind { if (it) viewModel.goBack() }

    private fun setupFieldsBind() {
        with(viewModel) {
            with(binding) {
                ::nTitle bind { it?.let { it1 -> nraefTietName.setText(it1) } }
                ::nPrice bind { it?.let { it1 -> nraefTietAmountValue.setText(it1) } }
                ::nMileage bind { it?.let { it1 -> nraefTietMileageValue.setText(it1) } }
                ::nDate bind { nraefTvDateValue.text = getFormattedDate(it) }
            }
        }
    }


    /**
     * Listener functions
     */
    private fun setupDatePickerListener() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val cal = GregorianCalendar.getInstance()
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                viewModel.nDate = cal.time.time
            }

        binding.nraefDateLayout.setOnClickListener {
            val cCal = GregorianCalendar.getInstance().apply { timeInMillis = viewModel.nDate }
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
                        with(binding) {
                            viewModel.addOrEditNoteItem(
                                nraefTietName.text.toString(),
                                nraefTietAmountValue.text.toString(),
                                nraefTietMileageValue.text.toString()
                            )
                        }
                    }
                    override fun onValidateFailed(errors: List<String>) {}
                }
                validate(titleValidation, amountValidation, mileageValidation)
            }
        }
    }


    /**
     * Additional functions
     */
    private fun getArgs() {
        val args = requireArguments()
        if (!args.containsKey(MODE_KEY)) throw Exception("Empty mode argument for NoteRepairAddOrEditFragment")

        val type = args.getString(MODE_KEY)
        if (type != EDIT_MODE && type != ADD_MODE) throw Exception("Unknown mode argument for NoteRepairAddOrEditFragment: $type")

        if (!args.containsKey(CAR_ID_KEY)) throw Exception("CarItem id must be implemented for NoteRepairAddOrEditFragment")
        viewModel.cId = args.getInt(CAR_ID_KEY, CarItem.UNDEFINED_ID)
        if (type == EDIT_MODE && !args.containsKey(ID_KEY)) throw Exception("NoteItem id must be implemented for NoteRepairAddOrEditFragment")
        viewModel.nId = args.getInt(ID_KEY, NoteItem.UNDEFINED_ID)
    }


    /**
     * Base functions to make class work as fragment
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
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
