package com.vladimir_x.easyenglishlearn.word_selection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.vladimir_x.easyenglishlearn.Constants
import com.vladimir_x.easyenglishlearn.ModelFactory
import com.vladimir_x.easyenglishlearn.databinding.FragmentExerciseChoiceBinding

class ExerciseChoiceFragment : DialogFragment() {
    private var viewModel: WordSelectionViewModel? = null
    private var _binding: FragmentExerciseChoiceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExerciseChoiceBinding.inflate(
            inflater,
            container,
            false
        )
        val categoryName = requireArguments().getString(Constants.ARG_CATEGORY_NAME) ?: ""
        viewModel = ModelFactory.getInstance(categoryName)?.let {
            ViewModelProvider(this, it).get(WordSelectionViewModel::class.java)
        }
        initView()

        viewModel?.closeDialogLiveData?.observe(viewLifecycleOwner) { closeDialog() }
        return binding.root
    }

    private fun initView() {
        with(binding) {
            ecfRgTranslationDirection.setOnCheckedChangeListener { _, checkedId ->
                viewModel?.onDirectionChanged(checkedId)
            }
            ecfBtnQuiz.setOnClickListener { viewModel?.onBtnQuizClick() }
            ecfBtnConstructor.setOnClickListener { viewModel?.onBtnConstructorClick() }
            ecfBtnCancel.setOnClickListener { viewModel?.onBtnCancelClick() }
        }
    }

    private fun closeDialog() {
        dismiss()
    }

    companion object {
        @JvmStatic
        fun newInstance(categoryName: String?): DialogFragment {
            val args = Bundle()
            args.putString(Constants.ARG_CATEGORY_NAME, categoryName)
            val fragment: DialogFragment = ExerciseChoiceFragment()
            fragment.arguments = args
            return fragment
        }
    }
}