package com.vladimir_x.easyenglishlearn.exercises

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.vladimir_x.easyenglishlearn.Constants
import com.vladimir_x.easyenglishlearn.R
import com.vladimir_x.easyenglishlearn.databinding.FragmentWordConstructorBinding
import com.vladimir_x.easyenglishlearn.model.Word
import java.util.ArrayList

class WordConstructorFragment : Fragment() {
    private var viewModel: WordConstructorViewModel? = null
    private var _binding: FragmentWordConstructorBinding? = null
    private val binding get() = _binding!!
    private val newButtonListener = View.OnClickListener { v: View ->
        val letter = (v as Button).text.toString()
        viewModel?.onNewButtonClick(letter)
        //binding.wcfGridContainer.removeView(v)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWordConstructorBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[WordConstructorViewModel::class.java]
        if (savedInstanceState == null) {
            val translationDirection =
                arguments?.getBoolean(Constants.TRANSLATION_DIRECTION) ?: true
            val wordList =
                arguments?.getParcelableArrayList<Word>(Constants.SELECTED_WORDS) as List<Word>
            viewModel?.startExercise(wordList, translationDirection)
        }
        initView()
        subscribeToLiveData()
    }

    private fun initView() {
        with(binding) {
            wcfBtnClean.setOnClickListener {
                viewModel?.onButtonUndoClick()
            }
        }
    }

    private fun subscribeToLiveData() {
        viewModel?.exerciseCloseLiveData?.observe(viewLifecycleOwner) { closeFragment() }
        viewModel?.messageLiveData?.observe(viewLifecycleOwner) {
            it?.let { errorsCount: Int ->
                showMessage(errorsCount)
            }
        }
        viewModel?.charArrayLiveData?.observe(viewLifecycleOwner) { letters: List<Char> ->
            createButtons(letters)
            fillTexFields()
        }
    }

    private fun closeFragment() {
        requireActivity().onBackPressed()
    }

    private fun showMessage(errorsCount: Int) {
        val message = if (errorsCount < 0) {
            getString(R.string.wrong_answer)
        } else {
            getString(R.string.errors_count, errorsCount)
        }
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    private fun createButtons(letters: List<Char>) {
        binding.wcfGridContainer.removeAllViews()
        for (letter in letters) {
            val button = layoutInflater.inflate(
                R.layout.letter_button,
                binding.wcfGridContainer,
                false
            ) as Button
            button.apply {
                text = letter.toString()
                setOnClickListener(newButtonListener)
            }
            binding.wcfGridContainer.addView(button)
        }
    }

    private fun fillTexFields() {
        with(binding) {
            wcfTvQuestion.text = viewModel?.question
            wcfTvAnswer.text = viewModel?.answer
        }
    }

    companion object {
        fun newInstance(
            selectedWordList: ArrayList<Word>,
            translationDirection: Boolean
        ) = WordConstructorFragment().apply {
            arguments = createBundle(selectedWordList, translationDirection)
        }

        private fun createBundle(
            selectedWordList: ArrayList<Word>?,
            translationDirection: Boolean
        ) = Bundle().apply {
            putParcelableArrayList(Constants.SELECTED_WORDS, selectedWordList)
            putBoolean(Constants.TRANSLATION_DIRECTION, translationDirection)
        }
    }
}