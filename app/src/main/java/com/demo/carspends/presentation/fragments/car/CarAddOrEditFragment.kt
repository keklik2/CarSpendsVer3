package com.demo.carspends.presentation.fragments.car

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.demo.carspends.R
import com.demo.carspends.databinding.CarAddEditFragmentBinding
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.utils.getFormattedDoubleAsStrForDisplay
import com.demo.carspends.utils.getFormattedIntAsStrForDisplay
import com.demo.carspends.utils.ui.BaseFragment

class CarAddOrEditFragment :
    BaseFragment(R.layout.car_add_edit_fragment) {
    override val binding: CarAddEditFragmentBinding by viewBinding()
    override val viewModel: CarAddOrEditViewModel by viewModels { viewModelFactory }
    override var setupListeners: (() -> Unit)? = {
        setupNameTextChangeListener()
        setupMileageTextChangeListener()
        setupEngineCapacityTextChangeListener()
        setupPowerTextChangeListener()
    }
    override var setupObservers: (() -> Unit)? = {
        setupErrorObservers()
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
        binding.carefTietCarName.addTextChangedListener {
            viewModel.resetNameError()
        }
    }

    private fun setupMileageTextChangeListener() {
        binding.carefTietMileageValue.addTextChangedListener {
            viewModel.resetMileageError()
        }
    }

    private fun setupEngineCapacityTextChangeListener() {
        binding.carefTietEngineCapacity.addTextChangedListener {
            viewModel.resetEngineCapacityError()
        }
    }

    private fun setupPowerTextChangeListener() {
        binding.carefTietPower.addTextChangedListener {
            viewModel.resetPowerError()
        }
    }

    private fun setupErrorObservers() {
        viewModel.errorNameInput.observe(viewLifecycleOwner) {
            binding.carefTilCarName.error = if (it) getString(ERR_TITLE)
            else null
        }

        viewModel.errorMileageInput.observe(viewLifecycleOwner) {
            binding.carefTilMileageValue.error = if (it) getString(ERR_RESOURCE)
            else null
        }

        viewModel.errorEngineCapacityInput.observe(viewLifecycleOwner) {
            binding.carefTilEngineCapacity.error = if (it) getString(ERR_MILEAGE)
            else null
        }

        viewModel.errorPowerInput.observe(viewLifecycleOwner) {
            binding.carefTilPower.error = if (it) getString(ERR_MILEAGE)
            else null
        }
    }

    private fun setupCanCloseScreenObserver() {
        viewModel.canCloseScreen.observe(viewLifecycleOwner) {
            if (launchMode == ADD_MODE) viewModel.exit()
            else viewModel.goToHomeScreen()
        }
    }


    /** Additional functions */
    private fun setupBackPresser() {
        if (launchMode == ADD_MODE) {
            requireActivity().onBackPressedDispatcher.addCallback(object :
                OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    AlertDialog.Builder(requireActivity())
                        .setMessage(
                            getString(R.string.dialog_exit_car)
                        )
                        .setPositiveButton(R.string.button_apply) { _, _ ->
                            addModeApplyClickListener()
                            viewModel.exit()
                        }
                        .setNegativeButton(R.string.button_deny) { _, _ ->
                        }
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
        with(binding) {
            carefTvStatistics.visibility = View.INVISIBLE
            carefAllFuelLayout.visibility = View.INVISIBLE
            carefAllFuelPriceLayout.visibility = View.INVISIBLE
            carefMomentFuelLayout.visibility = View.INVISIBLE
            carefAvgFuelLayout.visibility = View.INVISIBLE
            carefMileagePriceLayout.visibility = View.INVISIBLE
            carefAllPriceLayout.visibility = View.INVISIBLE
            carefAllMileageLayout.visibility = View.INVISIBLE
        }
        addModeApplyClickListener()
    }

    private fun addModeApplyClickListener() {
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
                carefTvAvgFuel.text = String.format(
                    getString(R.string.text_measure_gas_charge_for_formatting),
                    getFormattedDoubleAsStrForDisplay(it.avgFuel)
                )
                carefTvMomentFuel.text = String.format(
                    getString(R.string.text_measure_gas_charge_for_formatting),
                    getFormattedDoubleAsStrForDisplay(it.momentFuel)
                )
                carefTvAllFuel.text = String.format(
                    getString(R.string.text_measure_gas_volume_unit_for_formatting),
                    getFormattedDoubleAsStrForDisplay(it.allFuel)
                )
                carefTvAllFuelPrice.text = String.format(
                    getString(R.string.text_measure_currency_for_formatting),
                    getFormattedDoubleAsStrForDisplay(it.fuelPrice)
                )
                carefTvMileagePrice.text = String.format(
                    getString(R.string.text_measure_currency_for_formatting),
                    getFormattedDoubleAsStrForDisplay(it.milPrice)
                )
                carefTvAllPrice.text = String.format(
                    getString(R.string.text_measure_currency_for_formatting),
                    getFormattedDoubleAsStrForDisplay(it.allPrice)
                )
                carefTvAllMileage.text = String.format(
                    getString(R.string.text_measure_mileage_unit_for_formatting),
                    getFormattedIntAsStrForDisplay(it.allMileage)
                )
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
    }

    companion object {
        // Text Fields error text
        private const val ERR_TITLE = R.string.inappropriate_title
        private const val ERR_RESOURCE = R.string.inappropriate_resource
        private const val ERR_MILEAGE = R.string.inappropriate_mileage

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
