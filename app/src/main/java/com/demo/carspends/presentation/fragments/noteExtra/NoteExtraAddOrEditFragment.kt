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
import com.demo.carspends.utils.ui.BaseFragmentWithEditingFinishedListener
import java.util.*

class NoteExtraAddOrEditFragment: BaseFragmentWithEditingFinishedListener(R.layout.note_extra_add_edit_fragment) {
    override val binding: NoteExtraAddEditFragmentBinding by viewBinding()
    override val viewModel: NoteExtraAddOrEditViewModel by viewModels {viewModelFactory}
    override var setupListeners: (() -> Unit)? = {
        setupDatePickerListener()
        setupTitleTextChangeListener()
        setupPriceTextChangeListener()
    }
    override var setupObservers: (() -> Unit)? = {
        setupErrorObservers()
        setupNoteDateObserver()
        setupCanCloseScreenObserver()
    }


    private lateinit var launchMode: String
    private var noteId = UNDEFINED_ID
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
        viewModel.setCarId(carId)
    }

    private fun setupErrorObservers() {
        viewModel.errorTitleInput.observe(viewLifecycleOwner) {
            binding.neaefTilName.error = if (it) ERR_TITLE
            else null
        }

        viewModel.errorPriceInput.observe(viewLifecycleOwner) {
            binding.neaefTilAmountValue.error = if (it) ERR_PRICE
            else null
        }
    }

    private fun setupNoteDateObserver() {
        viewModel.noteDate.observe(viewLifecycleOwner) {
            binding.neaefTvDateValue.text = getFormattedDate(it)
        }
    }

    override fun setupCanCloseScreenObserver() {
        viewModel.canCloseScreen.observe(viewLifecycleOwner) {
            onEditingFinishedListener.onFinish()
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
            DatePickerDialog(requireContext(), dateSetListener,
                cCal.get(Calendar.YEAR),
                cCal.get(Calendar.MONTH),
                cCal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setupTitleTextChangeListener() {
        binding.neaefTietName.addTextChangedListener {
            viewModel.resetTitleError()
        }
    }

    private fun setupPriceTextChangeListener() {
        binding.neaefTietAmountValue.addTextChangedListener {
            viewModel.resetPriceError()
        }
    }

    private fun chooseMode() {
        when(launchMode) {
            ADD_MODE -> addNoteMode()
            else -> editNoteMode()
        }
    }

    private fun addNoteMode() {
        binding.neaefButtonApply.setOnClickListener {
            viewModel.addNoteItem(
                binding.neaefTietName.text.toString(),
                binding.neaefTietAmountValue.text.toString()
            )
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

        binding.neaefButtonApply.setOnClickListener {
            viewModel.editNoteItem(
                binding.neaefTietName.text.toString(),
                binding.neaefTietAmountValue.text.toString()
            )
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
        private const val ERR_TITLE = "Inappropriate name"
        private const val ERR_PRICE = "Inappropriate amount"

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
