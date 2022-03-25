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
import com.demo.carspends.utils.getFormattedDate
import com.demo.carspends.utils.getFormattedDoubleAsStr
import com.demo.carspends.utils.ui.BaseFragmentWithEditingFinishedListener
import java.util.*

class NoteRepairAddOrEditFragment: BaseFragmentWithEditingFinishedListener(R.layout.note_repair_add_edit_fragment) {
    override val binding: NoteRepairAddEditFragmentBinding by viewBinding()
    override val viewModel: NoteRepairAddOrEditViewModel by viewModels { viewModelFactory }
    override var setupListeners: (() -> Unit)? = {
        setupDatePickerListener()
        setupTitleTextChangeListener()
        setupAmountTextChangeListener()
        setupMileageTextChangeListener()
    }
    override var setupObservers: (() -> Unit)? = {
        setupErrorObserver()
        setupCanCloseScreenObserver()
        setupNoteDateObserver()
    }


    private lateinit var launchMode: String
    private var noteId = NoteItem.UNDEFINED_ID
    private var carId = CarItem.UNDEFINED_ID

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
            DatePickerDialog(requireContext(), dateSetListener,
                cCal.get(Calendar.YEAR),
                cCal.get(Calendar.MONTH),
                cCal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setupTitleTextChangeListener() {
        binding.nraefTietName.addTextChangedListener {
            viewModel.resetTitleError()
        }
    }

    private fun setupAmountTextChangeListener() {
        binding.nraefTietAmountValue.addTextChangedListener {
            viewModel.resetTotalPriceError()
        }
    }

    private fun setupMileageTextChangeListener() {
        binding.nraefTietMileageValue.addTextChangedListener {
            viewModel.resetMileageError()
        }
    }

    private fun setupNoteDateObserver() {
        viewModel.noteDate.observe(viewLifecycleOwner) {
            binding.nraefTvDateValue.text = getFormattedDate(it)
        }
    }

    override fun setupCanCloseScreenObserver() {
        viewModel.canCloseScreen.observe(viewLifecycleOwner) {
            onEditingFinishedListener.onFinish()
        }
    }

    private fun setupErrorObserver() {
        viewModel.errorTitleInput.observe(viewLifecycleOwner) {
            binding.nraefTilName.error = if(it) ERR_TITLE
            else null
        }

        viewModel.errorTotalPriceInput.observe(viewLifecycleOwner) {
            binding.nraefTilAmountValue.error = if (it) ERR_AMOUNT
            else null
        }

        viewModel.errorMileageInput.observe(viewLifecycleOwner) {
            binding.nraefTilMileageValue.error = if (it) ERR_MILEAGE
            else null
        }
    }

    private fun chooseMode() {
        when(launchMode) {
            ADD_MODE -> addNoteMode()
            else -> editNoteMode()
        }
    }

    private fun addNoteMode() {
        viewModel.currCarItem.observe(viewLifecycleOwner) {
            binding.nraefTietMileageValue.setText(it.mileage.toString())
        }

        binding.nraefButtonApply.setOnClickListener {
            viewModel.addNoteItem(
                binding.nraefTietName.text.toString(),
                binding.nraefTietAmountValue.text.toString(),
                binding.nraefTietMileageValue.text.toString()
            )
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

        binding.nraefButtonApply.setOnClickListener {
            viewModel.editNoteItem(
                binding.nraefTietName.text.toString(),
                binding.nraefTietAmountValue.text.toString(),
                binding.nraefTietMileageValue.text.toString()
            )
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
        private const val ERR_TITLE = "Inappropriate title"
        private const val ERR_AMOUNT = "Inappropriate amount"
        private const val ERR_MILEAGE = "Inappropriate mileage"

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