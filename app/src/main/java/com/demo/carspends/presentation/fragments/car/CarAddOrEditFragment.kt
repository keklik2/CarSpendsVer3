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
import com.demo.carspends.utils.ui.baseFragment.BaseFragment
import io.github.anderscheow.validator.Validator
import io.github.anderscheow.validator.rules.common.NotBlankRule
import io.github.anderscheow.validator.rules.common.NotEmptyRule
import io.github.anderscheow.validator.validation
import io.github.anderscheow.validator.validator

class CarAddOrEditFragment : BaseFragment(R.layout.car_add_edit_fragment) {
    override val binding: CarAddEditFragmentBinding by viewBinding()
    override val viewModel: CarAddOrEditViewModel by viewModels { viewModelFactory }
    override var setupListeners: (() -> Unit)? = {
        setupTextChangeListeners()
        setupApplyButtonOnClickListener()
    }
    override var setupBinds: (() -> Unit)? = {
        setupFieldsBind()
        setupCanCloseScreenBind()
    }

    private lateinit var launchMode: String


    /**
     * Validation functions
     */
    private val titleValidation by lazy {
        validation(binding.tilCarName) {
            rules {
                +NotEmptyRule(ERR_EMPTY_TITLE)
                +NotBlankRule(ERR_BLANK_TITLE)
            }
        }
    }
    private val mileageValidation by lazy {
        validation(binding.tilMileageValue) {
            rules {
                +NotEmptyRule(ERR_EMPTY_MILEAGE)
                +NotBlankRule(ERR_BLANK_MILEAGE)
            }
        }
    }
    private val engineCapacityValidation by lazy {
        validation(binding.tilEngineVolume) {
            rules {
                +NotEmptyRule(ERR_EMPTY_ENGINE_CAPACITY)
                +NotBlankRule(ERR_BLANK_ENGINE_CAPACITY)
            }
        }
    }
    private val powerValidation by lazy {
        validation(binding.tilPower) {
            rules {
                +NotEmptyRule(ERR_EMPTY_POWER)
                +NotBlankRule(ERR_BLANK_POWER)
            }
        }
    }


    /**
     * Binds functions
     */
    private fun setupCanCloseScreenBind() =
        viewModel::canCloseScreen bind { if (it) viewModel.goToHomeScreen() }

    private fun setupFieldsBind() {
        with(viewModel) {
            with(binding) {
                ::cTitle bind { it?.let { it1 -> tietCarName.setText(it1) } }
                ::cMileage bind { it?.let { it1 -> tietMileageValue.setText(it1) } }
                ::cEngineCapacity bind { it?.let { it1 -> tietEngineVolume.setText(it1) } }
                ::cPower bind { it?.let { it1 -> tietPower.setText(it1) } }
            }
        }
    }


    /**
     * Listener functions
     */
    private fun setupTextChangeListeners() {
        binding.tietCarName.addTextChangedListener {
            validator(requireActivity()) {
                listener = object : Validator.OnValidateListener {
                    override fun onValidateFailed(errors: List<String>) {}
                    override fun onValidateSuccess(values: List<String>) {}
                }
                validate(titleValidation)
            }
        }

        binding.tietMileageValue.addTextChangedListener {
            validator(requireActivity()) {
                listener = object : Validator.OnValidateListener {
                    override fun onValidateFailed(errors: List<String>) {}
                    override fun onValidateSuccess(values: List<String>) {}
                }
                validate(mileageValidation)
            }
        }

        binding.tietEngineVolume.addTextChangedListener {
            validator(requireActivity()) {
                listener = object : Validator.OnValidateListener {
                    override fun onValidateFailed(errors: List<String>) {}
                    override fun onValidateSuccess(values: List<String>) {}
                }
                validate(engineCapacityValidation)
            }
        }

        binding.tietPower.addTextChangedListener {
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
        binding.buttonApply.setOnClickListener {
            validator(requireActivity()) {
                listener = object : Validator.OnValidateListener {
                    override fun onValidateSuccess(values: List<String>) {
                        addOrEditCar()
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


    /**
     * Additional functions
     */
    private fun setupBackPresser() {
        if (launchMode == ADD_MODE) {
            requireActivity().onBackPressedDispatcher.addCallback(object :
                OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    AlertDialog.Builder(requireActivity())
                        .setTitle(R.string.dialog_exit_title)
                        .setMessage(
                            getString(R.string.dialog_exit_car)
                        )
                        .setPositiveButton(R.string.button_apply) { _, _ ->
                            validator(requireActivity()) {
                                listener = object : Validator.OnValidateListener {
                                    override fun onValidateSuccess(values: List<String>) {
                                        addOrEditCar()
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
                            viewModel.exit()
                        }
                        .setNegativeButton(R.string.button_deny) { _, _ -> }
                        .show()
                }
            })
        }
    }

    private fun addOrEditCar() {
        with(binding) {
            viewModel.addOrEditCar(
                tietCarName.text.toString(),
                tietMileageValue.text.toString(),
                tietEngineVolume.text.toString(),
                tietPower.text.toString()
            )
        }
    }

    private fun getArgs() {
        val args = requireArguments()
        if (!args.containsKey(MODE_KEY)) throw Exception("Empty mode argument for CarRepairAddOrEditFragment")

        val type = args.getString(MODE_KEY)
        if (type != EDIT_MODE && type != ADD_MODE) throw Exception("Unknown mode argument for CarRepairAddOrEditFragment: $type")

        launchMode = type
        if (launchMode == EDIT_MODE && !args.containsKey(ID_KEY))
            throw Exception("CarItem id must be implemented for CarRepairAddOrEditFragment")
        viewModel.cId = args.getInt(ID_KEY, CarItem.UNDEFINED_ID)
    }


    /**
     * Basic functions to make class work as Fragment
     */
    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackPresser()
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
