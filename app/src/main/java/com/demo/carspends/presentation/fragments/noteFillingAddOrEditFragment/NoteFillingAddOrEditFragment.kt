package com.demo.carspends.presentation.fragments.noteFillingAddOrEditFragment

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import by.kirich1409.viewbindingdelegate.viewBinding
import com.demo.carspends.R
import com.demo.carspends.databinding.NoteFillingAddEditFragmentBinding
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.others.Fuel
import com.demo.carspends.utils.ui.BaseFragmentWithEditingFinishedListener
import com.demo.carspends.utils.getFormattedDate
import com.demo.carspends.utils.getFormattedDoubleAsStr
import java.lang.Exception
import java.util.*

class NoteFillingAddOrEditFragment : BaseFragmentWithEditingFinishedListener(R.layout.note_filling_add_edit_fragment) {
    override val binding: NoteFillingAddEditFragmentBinding by viewBinding()
    override val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[NoteFillingAddOrEditViewModel::class.java]
    }
    override var setupListeners: (() -> Unit)? = {
        setupDatePickerDialogListener()
        setupVolumeTextChangeListener()
        setupAmountTextChangeListener()
        setupPriceTextChangeListener()
        setupMileageTextChangeListener()
    }
    override var setupObservers: (() -> Unit)? = {
        setupCalcListener()
        setupErrorListener()
        setupCanCloseScreenObserver()
        setupNoteDateListener()
        setupLastFuelTypeListener()
    }

    private lateinit var launchMode: String
    private var noteId = NoteItem.UNDEFINED_ID
    private var carId = CarItem.UNDEFINED_ID
    private var lastChanged = CHANGED_NULL
    private var preLastChanged = CHANGED_NULL
    private var open = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCurrCarNote()
        setupFuelSpinnerAdapter()
        chooseMode()
    }

    private fun setupCurrCarNote() {
        viewModel.setCarItem(carId)
    }

    private fun setupDatePickerDialogListener() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val cal = GregorianCalendar.getInstance()
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                viewModel.setNoteDate(cal.time.time)
            }

        binding.nfaefDateLayout.setOnClickListener {
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

    private fun setupVolumeTextChangeListener() {
        binding.nfaefTietFuelVolume.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    preLastChanged = lastChanged
                    lastChanged = CHANGED_VOLUME
                }
            }

        binding.nfaefTietFuelVolume.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetVolumeError()

                if (open) {
                    open = false

                    if (lastChanged != CHANGED_NULL && preLastChanged != CHANGED_NULL) {
                        if (preLastChanged == CHANGED_AMOUNT) viewModel.calculatePrice(
                            binding.nfaefTietFuelAmount.text.toString(),
                            binding.nfaefTietFuelVolume.text.toString()
                        )
                        else if (preLastChanged == CHANGED_PRICE) viewModel.calculateAmount(
                            binding.nfaefTietFuelVolume.text.toString(),
                            binding.nfaefTietFuelPrice.text.toString()
                        )
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                open = true
            }

        })
    }

    private fun setupAmountTextChangeListener() {
        binding.nfaefTietFuelAmount.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    preLastChanged = lastChanged
                    lastChanged = CHANGED_AMOUNT
                }
            }

        binding.nfaefTietFuelAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetTotalPriceError()

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
            }

            override fun afterTextChanged(p0: Editable?) {
                open = true
            }

        })
    }

    private fun setupPriceTextChangeListener() {
        binding.nfaefTietFuelPrice.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    preLastChanged = lastChanged
                    lastChanged = CHANGED_PRICE
                }
            }

        binding.nfaefTietFuelPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetPriceError()

                if (open) {
                    open = false

                    if (lastChanged != CHANGED_NULL && preLastChanged != CHANGED_NULL) {
                        if (preLastChanged == CHANGED_VOLUME) viewModel.calculateAmount(
                            binding.nfaefTietFuelVolume.text.toString(),
                            binding.nfaefTietFuelPrice.text.toString()
                        )
                        else if (preLastChanged == CHANGED_AMOUNT) viewModel.calculateVolume(
                            binding.nfaefTietFuelAmount.text.toString(),
                            binding.nfaefTietFuelPrice.text.toString()
                        )
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                open = true
            }

        })
    }

    private fun setupMileageTextChangeListener() {
        binding.nfaefTietMileageValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetMileageError()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }

    private fun setupLastFuelTypeListener() {
        viewModel.lastFuelType.observe(viewLifecycleOwner) {
            binding.nfaefSpinnerFuelType.setSelection(viewModel.getFuelId(it))
        }
    }

    private fun setupNoteDateListener() {
        viewModel.noteDate.observe(viewLifecycleOwner) {
            binding.nfaefTvDateValue.text = getFormattedDate(it)
        }
    }

    override fun setupCanCloseScreenObserver() {
        viewModel.canCloseScreen.observe(viewLifecycleOwner) {
            onEditingFinishedListener.onFinish()
        }
    }

    private fun setupCalcListener() {
        viewModel.calcVolume.observe(viewLifecycleOwner) {
            binding.nfaefTietFuelVolume.setText(getFormattedDoubleAsStr(it))
        }

        viewModel.calcAmount.observe(viewLifecycleOwner) {
            binding.nfaefTietFuelAmount.setText(getFormattedDoubleAsStr(it))
        }

        viewModel.calcPrice.observe(viewLifecycleOwner) {
            binding.nfaefTietFuelPrice.setText(getFormattedDoubleAsStr(it))
        }
    }

    private fun setupErrorListener() {
        viewModel.errorVolumeInput.observe(viewLifecycleOwner) {
            if (it) binding.nfaefTilFuelVolume.error = ERR_VOLUME
            else binding.nfaefTilFuelVolume.error = null
        }

        viewModel.errorTotalPriceInput.observe(viewLifecycleOwner) {
            if (it) binding.nfaefTilFuelAmount.error = ERR_AMOUNT
            else binding.nfaefTilFuelAmount.error = null
        }

        viewModel.errorPriceInput.observe(viewLifecycleOwner) {
            if (it) binding.nfaefTilFuelPrice.error = ERR_PRICE
            else binding.nfaefTilFuelPrice.error = null
        }

        viewModel.errorMileageInput.observe(viewLifecycleOwner) {
            if (it) binding.nfaefTilMileageValue.error = ERR_MILEAGE
            else binding.nfaefTilMileageValue.error = null
        }
    }

    private fun setupFuelSpinnerAdapter() {
        // Setting Fuel enum values for spinner
        binding.nfaefSpinnerFuelType.adapter = ArrayAdapter(
            requireActivity(),
            R.layout.support_simple_spinner_dropdown_item,
            Fuel.values()
        )
    }

    private fun chooseMode() {
        when (launchMode) {
            ADD_MODE -> addNoteMode()
            else -> editNoteMode()
        }
    }

    private fun addNoteMode() {
        viewModel.setLastRefillFuelType()

        viewModel.currCarItem.observe(viewLifecycleOwner) {
            binding.nfaefTietMileageValue.setText(it.mileage.toString())
        }

        binding.nfaefButtonApply.setOnClickListener {
            viewModel.addNoteItem(
                binding.nfaefSpinnerFuelType.selectedItemPosition,
                binding.nfaefTietFuelVolume.text.toString(),
                binding.nfaefTietFuelAmount.text.toString(),
                binding.nfaefTietFuelPrice.text.toString(),
                binding.nfaefTietMileageValue.text.toString()
            )
        }
    }

    private fun editNoteMode() {
        viewModel.setItem(noteId)
        viewModel.noteItem.observe(viewLifecycleOwner) {
            with(binding) {
                nfaefSpinnerFuelType.setSelection(viewModel.getFuelId(it.fuelType))
                nfaefTietFuelVolume.setText(getFormattedDoubleAsStr(it.liters))
                nfaefTietFuelAmount.setText(getFormattedDoubleAsStr(it.totalPrice))
                nfaefTietFuelPrice.setText(getFormattedDoubleAsStr(it.price))
                nfaefTietMileageValue.setText(it.mileage.toString())
            }
        }

        binding.nfaefButtonApply.setOnClickListener {
            viewModel.editNoteItem(
                binding.nfaefSpinnerFuelType.selectedItemPosition,
                binding.nfaefTietFuelVolume.text.toString(),
                binding.nfaefTietFuelAmount.text.toString(),
                binding.nfaefTietFuelPrice.text.toString(),
                binding.nfaefTietMileageValue.text.toString()
            )
        }
    }

    private fun getArgs() {
        val args = requireArguments()
        if (!args.containsKey(MODE_KEY)) throw Exception("Empty mode argument for NoteFillingAddOrEditFragment")

        val type = args.getString(MODE_KEY)
        if (type != EDIT_MODE && type != ADD_MODE) throw Exception("Unknown mode argument for NoteFillingAddOrEditFragment: $type")

        launchMode = type
        if (!args.containsKey(CAR_ID_KEY)) throw Exception("CarItem id must be implemented for NoteFillingAddOrEditFragment")
        carId = args.getInt(CAR_ID_KEY, CarItem.UNDEFINED_ID)
        if (launchMode == EDIT_MODE && !args.containsKey(ID_KEY)) throw Exception("NoteItem id must be implemented for NoteFillingAddOrEditFragment")
        noteId = args.getInt(ID_KEY, NoteItem.UNDEFINED_ID)
    }

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    companion object {
        private const val CHANGED_NULL = -1
        private const val CHANGED_VOLUME = 10
        private const val CHANGED_AMOUNT = 20
        private const val CHANGED_PRICE = 30

        private const val ERR_VOLUME = "Inappropriate vol"
        private const val ERR_AMOUNT = "Inappropriate amount"
        private const val ERR_PRICE = "Inappropriate price"
        private const val ERR_MILEAGE = "Inappropriate mileage"

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