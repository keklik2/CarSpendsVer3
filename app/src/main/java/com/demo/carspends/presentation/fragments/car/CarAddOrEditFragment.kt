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
import com.demo.carspends.presentation.fragments.component.ComponentAddOrEditFragment
import com.demo.carspends.utils.getFormattedDoubleAsStrForDisplay
import com.demo.carspends.utils.getFormattedIntAsStrForDisplay
import com.demo.carspends.utils.ui.BaseFragment
import io.github.anderscheow.validator.Validator
import io.github.anderscheow.validator.rules.common.NotBlankRule
import io.github.anderscheow.validator.rules.common.NotEmptyRule
import io.github.anderscheow.validator.validation
import io.github.anderscheow.validator.validator

class CarAddOrEditFragment :
    BaseFragment(R.layout.car_add_edit_fragment) {
    override val binding: CarAddEditFragmentBinding by viewBinding()
    override val viewModel: CarAddOrEditViewModel by viewModels { viewModelFactory }
    override var setupListeners: (() -> Unit)? = {
        setupTextChangeListeners()
        setupApplyButtonOnClickListener()
    }
    override var setupObservers: (() -> Unit)? = {
        setupCanCloseScreenObserver()
    }

    private lateinit var launchMode: String
    private var carId = CarItem.UNDEFINED_ID

    private val titleValidation by lazy {
        validation(binding.carefTilCarName) {
            rules {
                +NotEmptyRule(ERR_EMPTY_TITLE)
                +NotBlankRule(ERR_BLANK_TITLE)
            }
        }
    }
    private val mileageValidation by lazy {
        validation(binding.carefTilMileageValue) {
            rules {
                +NotEmptyRule(ERR_EMPTY_MILEAGE)
                +NotBlankRule(ERR_BLANK_MILEAGE)
            }
        }
    }
    private val engineCapacityValidation by lazy {
        validation(binding.carefTilEngineCapacity) {
            rules {
                +NotEmptyRule(ERR_EMPTY_ENGINE_CAPACITY)
                +NotBlankRule(ERR_BLANK_ENGINE_CAPACITY)
            }
        }
    }
    private val powerValidation by lazy {
        validation(binding.carefTilPower) {
            rules {
                +NotEmptyRule(ERR_EMPTY_POWER)
                +NotBlankRule(ERR_BLANK_POWER)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chooseMode()
        setupBackPresser()
    }

    private fun setupTextChangeListeners() {
        binding.carefTietCarName.addTextChangedListener {
            validator(requireActivity()) {
                listener = object : Validator.OnValidateListener {
                    override fun onValidateFailed(errors: List<String>) {}
                    override fun onValidateSuccess(values: List<String>) {}
                }
                validate(titleValidation)
            }
        }

        binding.carefTietMileageValue.addTextChangedListener {
            validator(requireActivity()) {
                listener = object : Validator.OnValidateListener {
                    override fun onValidateFailed(errors: List<String>) {}
                    override fun onValidateSuccess(values: List<String>) {}
                }
                validate(mileageValidation)
            }
        }

        binding.carefTietEngineCapacity.addTextChangedListener {
            validator(requireActivity()) {
                listener = object : Validator.OnValidateListener {
                    override fun onValidateFailed(errors: List<String>) {}
                    override fun onValidateSuccess(values: List<String>) {}
                }
                validate(engineCapacityValidation)
            }
        }

        binding.carefTietPower.addTextChangedListener {
            validator(requireActivity()) {
                listener = object : Validator.OnValidateListener {
                    override fun onValidateFailed(errors: List<String>) {}
                    override fun onValidateSuccess(values: List<String>) {}
                }
                validate(powerValidation)
            }
        }
    }

    private fun setupApplyButtonOnClickListener() {
        binding.carefButtonApply.setOnClickListener {
            validator(requireActivity()) {
                listener = object : Validator.OnValidateListener {
                    override fun onValidateSuccess(values: List<String>) {
                        when (launchMode) {
                            ADD_MODE -> addCar()
                            EDIT_MODE -> editCar()
                        }
                    }
                    override fun onValidateFailed(errors: List<String>) {}
                }
                validate(
                    titleValidation,
                    mileageValidation,
                    powerValidation,
                    engineCapacityValidation
                )
            }
        }
    }

    private fun addCar() {
        with(binding) {
            viewModel.addCar(
                carefTietCarName.text.toString(),
                carefTietMileageValue.text.toString(),
                carefTietEngineCapacity.text.toString(),
                carefTietPower.text.toString()
            )
        }
    }

    private fun editCar() {
        with(binding) {
            viewModel.editCar(
                carefTietCarName.text.toString(),
                carefTietMileageValue.text.toString(),
                carefTietEngineCapacity.text.toString(),
                carefTietPower.text.toString()
            )
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
                            addCar()
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
        private const val ERR_EMPTY_TITLE = R.string.inappropriate_empty_title
        private const val ERR_BLANK_TITLE = R.string.blank_validation
        private const val ERR_EMPTY_MILEAGE = R.string.inappropriate_empty_mileage
        private const val ERR_BLANK_MILEAGE = R.string.blank_validation
        private const val ERR_EMPTY_ENGINE_CAPACITY = R.string.inappropriate_empty_engine_capacity
        private const val ERR_BLANK_ENGINE_CAPACITY = R.string.blank_validation
        private const val ERR_EMPTY_POWER = R.string.inappropriate_empty_power
        private const val ERR_BLANK_POWER = R.string.blank_validation

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
