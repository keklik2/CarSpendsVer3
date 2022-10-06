package com.demo.carspends.presentation.fragments.car

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.demo.carspends.R
import com.demo.carspends.databinding.CarAddEditFragmentBinding
import com.demo.carspends.domain.car.CarItem
import com.demo.carspends.domain.note.NoteItem
import com.demo.carspends.utils.DOWNLOAD_CHANNEL_ID
import com.demo.carspends.utils.dialogs.AppDialogContainer
import com.demo.carspends.utils.files.fileSaver.DbSaver
import com.demo.carspends.utils.genericType
import com.demo.carspends.utils.ui.baseFragment.BaseFragment
import com.demo.carspends.utils.ui.tipShower.TipShower
import io.github.anderscheow.validator.Validator
import io.github.anderscheow.validator.rules.common.NotBlankRule
import io.github.anderscheow.validator.rules.common.NotEmptyRule
import io.github.anderscheow.validator.validation
import io.github.anderscheow.validator.validator


class CarAddOrEditFragment : BaseFragment(R.layout.car_add_edit_fragment) {
    private val dbNotesSaver = DbSaver<List<NoteItem>>(
        this,
        genericType<List<NoteItem>>()
    ) { vm.applyNotes(it) }

    override val binding: CarAddEditFragmentBinding by viewBinding()
    override val vm: CarAddOrEditViewModel by viewModels { viewModelFactory }
    override var setupListeners: (() -> Unit)? = {
        setupTextChangeListeners()
        setupDropCarListener()
        setupDbSaverListeners()

        setupApplyButtonOnClickListener()
    }
    override var setupBinds: (() -> Unit)? = {
        setupFieldsBind()
        setupCanCloseScreenBind()
        setupShowTipBind()
    }

    private lateinit var launchMode: String

    private val tipShower by lazy {
        TipShower(requireActivity())
    }


    /**
     *
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
        vm::canCloseScreen bind { if (it) vm.goToHomeScreen() }

    private fun setupFieldsBind() {
        with(vm) {
            with(binding) {
                ::cTitle bind { it?.let { it1 -> tietCarName.setText(it1) } }
                ::cMileage bind { it?.let { it1 -> tietMileageValue.setText(it1) } }
                ::cEngineCapacity bind { it?.let { it1 -> tietEngineVolume.setText(it1) } }
                ::cPower bind { it?.let { it1 -> tietPower.setText(it1) } }
            }
        }
    }

    private fun setupShowTipBind() {
        vm::tipsCount bind {
            if (it < vm.tips.size && vm.isFirstLaunch && launchMode != ADD_MODE)
                tipShower.showTip(vm.tips[it]) { vm.nextTip() }
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

    private fun setupDropCarListener() {
        binding.dropCarButton.setOnClickListener {
            // Dialog left moving to VM bc of custom button names
            AlertDialog.Builder(requireActivity())
                .setTitle(R.string.dialog_drop_car_title)
                .setMessage(getString(R.string.dialog_drop_car))
                .setPositiveButton(R.string.button_delete) { _, _ ->
                    AlertDialog.Builder(requireActivity())
                        .setTitle(R.string.dialog_delete_car_title)
                        .setMessage(getString(R.string.dialog_delete_car))
                        .setPositiveButton(R.string.button_apply) { _, _ -> vm.deleteCar() }
                        .setNegativeButton(R.string.button_deny) { _, _ -> }
                        .show()
                }
                .setNeutralButton(R.string.button_drop) { _, _ ->
                    AlertDialog.Builder(requireActivity())
                        .setTitle(R.string.dialog_delete_car_info_title)
                        .setMessage(getString(R.string.dialog_delete_car_info))
                        .setPositiveButton(R.string.button_apply) { _, _ -> vm.dropCar()  }
                        .setNegativeButton(R.string.button_deny) { _, _ -> }
                        .show()
                }
                .setNegativeButton(R.string.button_deny) { _, _ -> }
                .show()
        }
    }

    private fun setupDbSaverListeners() {
        binding.downloadButton.setOnClickListener {
            vm.saveNotes(dbNotesSaver)
        }

        binding.uploadButton.setOnClickListener {
            vm.downloadNotes(dbNotesSaver)
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
                    makeAlert(
                        AppDialogContainer(
                            title = getString(R.string.dialog_exit_title),
                            message = getString(R.string.dialog_exit_car),
                            onPositiveButtonClicked = {
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
                                vm.exit()
                            }
                        )
                    )
                }
            })
        }
    }

    private fun addOrEditCar() {
        with(binding) {
            vm.addOrEditCar(
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
        vm.cId = args.getInt(ID_KEY, CarItem.UNDEFINED_ID)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                DOWNLOAD_CHANNEL_ID,
                getString(R.string.notification_channel_download),
                importance
            ).apply {
                description = getString(R.string.notification_channel_download_description)
            }
            val notificationManager: NotificationManager =
                requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun hideDbButtons() {
        if (launchMode == ADD_MODE) {
            binding.downloadButton.visibility = View.INVISIBLE
            binding.uploadButton.visibility = View.INVISIBLE
            binding.dropCarButton.visibility = View.INVISIBLE
        }
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
        createNotificationChannel()
        hideDbButtons()
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
