package com.demo.carspends.presentation.fragments.noteFillingAddOrEditFragment

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.demo.carspends.R
import com.demo.carspends.databinding.NoteExtraAddEditFragmentBinding
import com.demo.carspends.databinding.NoteFillingAddEditFragmentBinding
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.domain.others.Fuel
import com.demo.carspends.presentation.fragments.OnEditingFinishedListener
import com.demo.carspends.presentation.fragments.noteExtraAddOrEditFragment.NoteExtraAddOrEditFragment
import com.demo.carspends.presentation.fragments.noteExtraAddOrEditFragment.NoteExtraAddOrEditViewModel
import com.demo.carspends.utils.getFormattedDate
import com.demo.carspends.utils.getFormattedDoubleAsStr
import java.lang.Exception
import java.util.*

class NoteFillingAddOrEditFragment: Fragment() {

    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    private var _binding: NoteFillingAddEditFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel by lazy {
        ViewModelProvider(this)[NoteFillingAddOrEditViewModel::class.java]
    }

    private lateinit var launchMode: String
    private var noteId = NoteItem.UNDEFINED_ID
    private val cal = GregorianCalendar()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnEditingFinishedListener) onEditingFinishedListener = context
        else throw Exception("Activity must implement OnEditingFinishedListener")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getArgs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFuelSpinnerAdapter()
        setupObservers()
        setupListeners()
        chooseMode()
    }

    private fun setupListeners() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                viewModel.setNoteDate(cal.time.time)
            }

        binding.nfaefDateLayout.setOnClickListener {
            DatePickerDialog(requireContext(), dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        setupVolumeTextChangeListener()
        setupAmountTextChangeListener()
        setupPriceTextChangeListener()
        setupMileageTextChangeListener()
    }

    private fun setupVolumeTextChangeListener() {
        binding.nfaefTietFuelVolume.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetVolumeError()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }

    private fun setupAmountTextChangeListener() {
        binding.nfaefTietFuelAmount.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetTotalPriceError()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }

    private fun setupPriceTextChangeListener() {
        binding.nfaefTietFuelPrice.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetPriceError()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }

    private fun setupMileageTextChangeListener() {
        binding.nfaefTietMileageValue.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetMileageError()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }

    private fun setupObservers() {
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

        viewModel.canCloseScreen.observe(viewLifecycleOwner) {
            onEditingFinishedListener.onFinish()
        }

        viewModel.noteDate.observe(viewLifecycleOwner) {
            binding.nfaefTvDateValue.text = getFormattedDate(it)
        }
    }

    private fun setupFuelSpinnerAdapter() {
        // Setting Fuel enum values for spinner
        binding.nfaefSpinnerFuelType.adapter = ArrayAdapter<Fuel>(requireActivity(), R.layout.support_simple_spinner_dropdown_item, Fuel.values())
    }

    private fun chooseMode() {
        when(launchMode) {
            ADD_MODE -> addNoteMode()
            else -> editNoteMode()
        }
    }

    private fun addNoteMode() {
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
        if (!args.containsKey(MODE_KEY)) throw Exception("Empty mode argument for NoteExtraAddOrEditFragment")

        val type = args.getString(MODE_KEY)
        if (type != EDIT_MODE && type != ADD_MODE) throw Exception("Unknown mode argument for NoteExtraAddOrEditFragment: $type")

        launchMode = type
        if (launchMode == EDIT_MODE && !args.containsKey(
                ID_KEY
            )) throw Exception("NoteItem id must be implemented for NoteExtraAddOrEditFragment")
        noteId = args.getInt(ID_KEY, NoteItem.UNDEFINED_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NoteFillingAddEditFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ERR_VOLUME = "Inappropriate vol"
        private const val ERR_AMOUNT = "Inappropriate amount"
        private const val ERR_PRICE = "Inappropriate price"
        private const val ERR_MILEAGE = "Inappropriate mileage"

        private const val MODE_KEY = "mode_note"
        private const val ID_KEY = "id_note"

        private const val EDIT_MODE = "edit_mode"
        private const val ADD_MODE = "add_mode"

        fun newAddInstance(): NoteFillingAddOrEditFragment {
            return NoteFillingAddOrEditFragment().apply {
                arguments = Bundle().apply {
                    putString(MODE_KEY, ADD_MODE)
                }
            }
        }

        fun newEditInstance(id: Int): NoteFillingAddOrEditFragment {
            return NoteFillingAddOrEditFragment().apply {
                arguments = Bundle().apply {
                    putString(MODE_KEY, EDIT_MODE)
                    putInt(ID_KEY, id)
                }
            }
        }
    }
}