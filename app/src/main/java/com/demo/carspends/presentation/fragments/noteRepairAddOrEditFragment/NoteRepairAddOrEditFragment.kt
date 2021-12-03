package com.demo.carspends.presentation.fragments.noteRepairAddOrEditFragment

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.demo.carspends.databinding.NoteRepairAddEditFragmentBinding
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.presentation.fragments.OnEditingFinishedListener
import com.demo.carspends.utils.getFormattedDate
import com.demo.carspends.utils.getFormattedDoubleAsStr
import java.lang.Exception
import java.util.*

class NoteRepairAddOrEditFragment: Fragment() {
    private lateinit var onEditingFinishedListener: OnEditingFinishedListener

    private var _binding: NoteRepairAddEditFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel by lazy {
        ViewModelProvider(this)[NoteRepairAddOrEditViewModel::class.java]
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

        binding.nraefDateLayout.setOnClickListener {
            DatePickerDialog(requireContext(), dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        setupTitleTextChangeListener()
        setupAmountTextChangeListener()
        setupMileageTextChangeListener()
    }

    private fun setupTitleTextChangeListener() {
        binding.nraefTietName.addTextChangedListener(object: TextWatcher {
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
        binding.nraefTietAmountValue.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetTotalPriceError()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }

    private fun setupMileageTextChangeListener() {
        binding.nraefTietMileageValue.addTextChangedListener(object: TextWatcher {
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
        viewModel.errorTitleInput.observe(viewLifecycleOwner) {
            if(it) binding.nraefTilName.error = ERR_TITLE
            else binding.nraefTilName.error = null
        }

        viewModel.errorTotalPriceInput.observe(viewLifecycleOwner) {
            if (it) binding.nraefTilAmountValue.error = ERR_AMOUNT
            else binding.nraefTilAmountValue.error = null
        }

        viewModel.errorMileageInput.observe(viewLifecycleOwner) {
            if (it) binding.nraefTilMileageValue.error = ERR_MILEAGE
            else binding.nraefTilMileageValue.error = null
        }

        viewModel.canCloseScreen.observe(viewLifecycleOwner) {
            onEditingFinishedListener.onFinish()
        }

        viewModel.noteDate.observe(viewLifecycleOwner) {
            binding.nraefTvDateValue.text = getFormattedDate(it)
        }
    }

    private fun chooseMode() {
        when(launchMode) {
            ADD_MODE -> addNoteMode()
            else -> editNoteMode()
        }
    }

    private fun addNoteMode() {
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
                binding.nraefTietName.setText(it.title)
                binding.nraefTietAmountValue.setText(getFormattedDoubleAsStr(it.totalPrice))
                binding.nraefTietMileageValue.setText(it.mileage.toString())
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
        if (launchMode == EDIT_MODE && !args.containsKey(
                ID_KEY
            )) throw Exception("NoteItem id must be implemented for NoteRepairAddOrEditFragment")
        noteId = args.getInt(ID_KEY, NoteItem.UNDEFINED_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NoteRepairAddEditFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ERR_TITLE = "Inappropriate title"
        private const val ERR_AMOUNT = "Inappropriate amount"
        private const val ERR_MILEAGE = "Inappropriate mileage"

        private const val MODE_KEY = "mode_note"
        private const val ID_KEY = "id_note"

        private const val EDIT_MODE = "edit_mode"
        private const val ADD_MODE = "add_mode"

        fun newAddInstance(): NoteRepairAddOrEditFragment {
            return NoteRepairAddOrEditFragment().apply {
                arguments = Bundle().apply {
                    putString(MODE_KEY, ADD_MODE)
                }
            }
        }

        fun newEditInstance(id: Int): NoteRepairAddOrEditFragment {
            return NoteRepairAddOrEditFragment().apply {
                arguments = Bundle().apply {
                    putString(MODE_KEY, EDIT_MODE)
                    putInt(ID_KEY, id)
                }
            }
        }
    }
}