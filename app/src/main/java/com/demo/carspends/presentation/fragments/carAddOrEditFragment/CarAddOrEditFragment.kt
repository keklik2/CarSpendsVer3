package com.demo.carspends.presentation.fragments.carAddOrEditFragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.demo.carspends.R
import com.demo.carspends.databinding.CarAddEditFragmentBinding
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.presentation.fragments.OnEditingFinishedListener
import com.demo.carspends.utils.getFormattedDoubleAsStrForDisplay
import com.demo.carspends.utils.getFormattedIntAsStrForDisplay
import com.demo.carspends.utils.ui.BaseFragmentWithEditingFinishedListener

class CarAddOrEditFragment : BaseFragmentWithEditingFinishedListener(R.layout.car_add_edit_fragment) {
    override val binding: CarAddEditFragmentBinding by viewBinding()
    override val viewModel: CarAddOrEditViewModel by viewModels { viewModelFactory }
    override var setupListeners: (() -> Unit)? = {
        setupNameTextChangeListener()
        setupMileageTextChangeListener()
        setupEngineCapacityTextChangeListener()
        setupPowerTextChangeListener()
    }
    override var setupObservers: (() -> Unit)? = {
        setupTextFieldsErrorsObserver()
        setupCanCloseScreenObserver()
    }

    private lateinit var launchMode: String
    private var carId = CarItem.UNDEFINED_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chooseMode()
        setupBackPresser()
    }

    private fun setupNameTextChangeListener() {
        binding.carefTietCarName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetNameError()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }

    private fun setupMileageTextChangeListener() {
        binding.carefTietMileageValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetMileageError()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }

    private fun setupEngineCapacityTextChangeListener() {
        binding.carefTietEngineCapacity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetEngineCapacityError()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }

    private fun setupPowerTextChangeListener() {
        binding.carefTietPower.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetPowerError()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }

    private fun setupTextFieldsErrorsObserver() {
        viewModel.errorNameInput.observe(viewLifecycleOwner) {
            if (it) binding.carefTilCarName.error = ERR_TITLE
            else binding.carefTilCarName.error = null
        }

        viewModel.errorMileageInput.observe(viewLifecycleOwner) {
            if (it) binding.carefTilMileageValue.error = ERR_RESOURCE
            else binding.carefTilMileageValue.error = null
        }

        viewModel.errorEngineCapacityInput.observe(viewLifecycleOwner) {
            if (it) binding.carefTilEngineCapacity.error = ERR_MILEAGE
            else binding.carefTilEngineCapacity.error = null
        }

        viewModel.errorPowerInput.observe(viewLifecycleOwner) {
            if (it) binding.carefTilPower.error = ERR_MILEAGE
            else binding.carefTilPower.error = null
        }
    }

    override fun setupCanCloseScreenObserver() {
        viewModel.canCloseScreen.observe(viewLifecycleOwner) {
            onEditingFinishedListener.onFinish()
        }
    }


    /** Additional functions */
    private fun setupBackPresser() {
        if (launchMode == ADD_MODE) {
            requireActivity().onBackPressedDispatcher.addCallback(object :
                OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Toast.makeText(requireActivity(), "Заполните все данные", Toast.LENGTH_LONG)
                        .show()
                }
            })
        }
    }

    private fun chooseMode() {
        when (launchMode) {
            ADD_MODE -> addNoteMode()
            else -> editNoteMode()
        }
    }

    private fun addNoteMode() {
        binding.carefButtonApply.setOnClickListener {
            viewModel.addCar(
                binding.carefTietCarName.text.toString(),
                binding.carefTietMileageValue.text.toString(),
                binding.carefTietEngineCapacity.text.toString(),
                binding.carefTietPower.text.toString()
            )
        }
    }

    private fun editNoteMode() {

        viewModel.setItem(carId)
        viewModel.carrItem.observe(viewLifecycleOwner) {
            with(binding) {
                carefTietCarName.setText(it.title)
                carefTietMileageValue.setText(it.mileage.toString())
                carefTietEngineCapacity.setText(it.engineVolume.toString())
                carefTietPower.setText(it.power.toString())
                "${getFormattedDoubleAsStrForDisplay(it.avgFuel)} ${getString(R.string.text_measure_gas_charge)}"
                    .also { carefTvAvgFuel.text = it }
                "${getFormattedDoubleAsStrForDisplay(it.momentFuel)} ${getString(R.string.text_measure_gas_charge)}"
                    .also { carefTvMomentFuel.text = it }
                "${getFormattedDoubleAsStrForDisplay(it.allFuel)} ${getString(R.string.text_measure_gas_volume_unit)}"
                    .also { carefTvAllFuel.text = it }
                "${getFormattedDoubleAsStrForDisplay(it.fuelPrice)} ${getString(R.string.text_measure_currency)}"
                    .also { carefTvAllFuelPrice.text = it }
                "${getFormattedDoubleAsStrForDisplay(it.milPrice)} ${getString(R.string.text_measure_currency)}"
                    .also { carefTvMileagePrice.text = it }
                "${getFormattedDoubleAsStrForDisplay(it.allPrice)} ${getString(R.string.text_measure_currency)}"
                    .also { carefTvAllPrice.text = it }
                "${getFormattedIntAsStrForDisplay(it.allMileage)} ${getString(R.string.text_measure_mileage_unit)}"
                    .also { carefTvAllMileage.text = it }
            }
        }

        binding.carefButtonApply.setOnClickListener {
            viewModel.editCar(
                binding.carefTietCarName.text.toString(),
                binding.carefTietMileageValue.text.toString(),
                binding.carefTietEngineCapacity.text.toString(),
                binding.carefTietPower.text.toString()
            )
        }
    }

    private fun getArgs() {
        val args = requireArguments()
        if (!args.containsKey(MODE_KEY)) throw Exception("Empty mode argument for CarRepairAddOrEditFragment")

        val type = args.getString(MODE_KEY)
        if (type != EDIT_MODE && type != ADD_MODE) throw Exception("Unknown mode argument for CarRepairAddOrEditFragment: $type")

        launchMode = type
        if (launchMode == EDIT_MODE && !args.containsKey(
                ID_KEY
            )
        ) throw Exception("CarItem id must be implemented for CarRepairAddOrEditFragment")
        carId = args.getInt(ID_KEY, CarItem.UNDEFINED_ID)
    }



    /** Basic functions to make class work as Fragment */
    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
        if (context is OnEditingFinishedListener) onEditingFinishedListener = context
        else throw Exception("Activity must implement OnEditingFinishedListener")
    }

    companion object {
        // Text Fields error text
        private const val ERR_TITLE = "Inappropriate title"
        private const val ERR_RESOURCE = "Inappropriate resource"
        private const val ERR_MILEAGE = "Inappropriate mileage"

        // Bundle Arguments constants
        private const val MODE_KEY = "mode_car"
        private const val ID_KEY = "id_car"

        private const val EDIT_MODE = "edit_mode"
        private const val ADD_MODE = "add_mode"

        fun newAddInstance(): CarAddOrEditFragment {
            return CarAddOrEditFragment().apply {
                arguments = Bundle().apply {
                    putString(MODE_KEY, ADD_MODE)
                }
            }
        }

        fun newEditInstance(id: Int): CarAddOrEditFragment {
            return CarAddOrEditFragment().apply {
                arguments = Bundle().apply {
                    putString(MODE_KEY, EDIT_MODE)
                    putInt(ID_KEY, id)
                }
            }
        }
    }
}