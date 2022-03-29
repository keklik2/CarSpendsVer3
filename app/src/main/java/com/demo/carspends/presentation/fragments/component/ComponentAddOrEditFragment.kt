package com.demo.carspends.presentation.fragments.component

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.demo.carspends.R
import com.demo.carspends.databinding.ComponentAddEditFragmentBinding
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.component.ComponentItem
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.utils.getFormattedDate
import com.demo.carspends.utils.ui.BaseFragment
import java.util.*

class ComponentAddOrEditFragment: BaseFragment(R.layout.component_add_edit_fragment) {
    override val binding: ComponentAddEditFragmentBinding by viewBinding()
    override val viewModel: ComponentAddOrEditViewModel by viewModels { viewModelFactory }
    override var setupListeners: (() -> Unit)? = {
        setupDatePickerListener()
        setupTitleTextChangeListener()
        setupAmountTextChangeListener()
        setupMileageTextChangeListener()
    }
    override var setupObservers: (() -> Unit)? = {
        setupErrorObserver()
        setupCanCloseScreenObserver()
        setupDateObserver()
    }

    private lateinit var launchMode: String
    private var componentId = ComponentItem.UNDEFINED_ID
    private var carId = CarItem.UNDEFINED_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chooseMode()
    }

    private fun setupDatePickerListener() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val cal = GregorianCalendar.getInstance()
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                viewModel.setComponentDate(cal.time.time)
            }

        binding.caefDateLayout.setOnClickListener {
            val cCal = GregorianCalendar.getInstance().apply {
                viewModel.componentDate.value?.let {
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
        binding.caefTietName.addTextChangedListener {
            viewModel.resetTitleError()
        }
    }

    private fun setupAmountTextChangeListener() {
        binding.caefTietResourceValue.addTextChangedListener {
            viewModel.resetResourceError()
        }
    }

    private fun setupMileageTextChangeListener() {
        binding.caefTietMileageValue.addTextChangedListener {
            viewModel.resetMileageError()
        }
    }

    private fun setupDateObserver() {
        viewModel.componentDate.observe(viewLifecycleOwner) {
            binding.caefTvDateValue.text = getFormattedDate(it)
        }
    }

    private fun setupCanCloseScreenObserver() {
        viewModel.canCloseScreen.observe(viewLifecycleOwner) {
            viewModel.goBack()
        }
    }

    private fun setupErrorObserver() {
        viewModel.errorTitleInput.observe(viewLifecycleOwner) {
            binding.caefTilName.error = if(it) getString(ERR_TITLE)
            else null
        }

        viewModel.errorResourceInput.observe(viewLifecycleOwner) {
            binding.caefTilResourceValue.error = if (it) getString(ERR_RESOURCE)
            else null
        }

        viewModel.errorMileageInput.observe(viewLifecycleOwner) {
            binding.caefTilMileageValue.error = if (it) getString(ERR_MILEAGE)
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
        viewModel.carsList.observe(viewLifecycleOwner) {
            binding.caefTietMileageValue.setText(it[0].mileage.toString())
        }
        binding.caefButtonApply.setOnClickListener {
            viewModel.addComponentItem(
                binding.caefTietName.text.toString(),
                binding.caefTietResourceValue.text.toString(),
                binding.caefTietMileageValue.text.toString()
            )
        }
    }

    private fun editNoteMode() {
        viewModel.setItem(componentId)
        viewModel.componentItem.observe(viewLifecycleOwner) {
            with(binding) {
                caefTietName.setText(it.title)
                caefTietResourceValue.setText(it.resourceMileage.toString())
                caefTietMileageValue.setText(it.startMileage.toString())
            }
        }

        binding.caefButtonApply.setOnClickListener {
            viewModel.editNoteItem(
                binding.caefTietName.text.toString(),
                binding.caefTietResourceValue.text.toString(),
                binding.caefTietMileageValue.text.toString()
            )
        }
    }

    private fun getArgs() {
        val args = requireArguments()
        if (!args.containsKey(MODE_KEY)) throw Exception("Empty mode argument for ComponentAddOrEditFragment")

        val type = args.getString(MODE_KEY)
        if (type != EDIT_MODE && type != ADD_MODE) throw Exception("Unknown mode argument for ComponentAddOrEditFragment: $type")

        launchMode = type
        if (!args.containsKey(CAR_ID_KEY)) throw Exception("CarItem id must be implemented for ComponentAddOrEditFragment")
        carId = args.getInt(CAR_ID_KEY, CarItem.UNDEFINED_ID)
        if (launchMode == EDIT_MODE && !args.containsKey(ID_KEY)) throw Exception("ComponentItem id must be implemented for ComponentAddOrEditFragment")
        componentId = args.getInt(ID_KEY, NoteItem.UNDEFINED_ID)
    }



    /** Basic functions to make Class work as Fragment */
    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    companion object {
        private const val ERR_TITLE = R.string.inappropriate_title
        private const val ERR_RESOURCE = R.string.inappropriate_resource
        private const val ERR_MILEAGE = R.string.inappropriate_mileage

        private const val MODE_KEY = "mode_component"
        private const val ID_KEY = "id_component"
        private const val CAR_ID_KEY = "id_car"

        private const val EDIT_MODE = "edit_mode"
        private const val ADD_MODE = "add_mode"

        fun newAddInstance(carId: Int): ComponentAddOrEditFragment {
            return ComponentAddOrEditFragment().apply {
                arguments = Bundle().apply {
                    putString(MODE_KEY, ADD_MODE)
                    putInt(CAR_ID_KEY, carId)
                }
            }
        }

        fun newEditInstance(carId: Int, id: Int): ComponentAddOrEditFragment {
            return ComponentAddOrEditFragment().apply {
                arguments = Bundle().apply {
                    putString(MODE_KEY, EDIT_MODE)
                    putInt(CAR_ID_KEY, carId)
                    putInt(ID_KEY, id)
                }
            }
        }
    }
}
