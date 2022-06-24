package com.vladimir_x.easyenglishlearn.ui.word_selection

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.vladimir_x.easyenglishlearn.Constants
import com.vladimir_x.easyenglishlearn.R
import com.vladimir_x.easyenglishlearn.databinding.FragmentWordSelectionBinding
import com.vladimir_x.easyenglishlearn.model.Word
import com.vladimir_x.easyenglishlearn.ui.ExerciseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WordSelectionFragment : Fragment(R.layout.fragment_word_selection) {
    private var _binding: FragmentWordSelectionBinding? = null
    private val binding get() = _binding!!
    private var wordSelectionAdapter: WordSelectionAdapter? = null
    private val viewModel: WordSelectionViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWordSelectionBinding.bind(view)
        initView()
        subscribeToLiveData()
    }

    private fun initView() {
        with(binding) {
            rvWordsChoice.apply {
                layoutManager = LinearLayoutManager(activity)
                wordSelectionAdapter = WordSelectionAdapter { checked, word ->
                    viewModel.onItemCheckBoxChange(checked, word)
                }
                adapter = wordSelectionAdapter
            }
            tvCategoryName.text = viewModel.categoryName
            btnStart.setOnClickListener { viewModel.onBtnStartClick() }
            cbChooseAll.setOnCheckedChangeListener { _, checked ->
                viewModel.onChooseAllChange(checked)
            }
        }
    }

    private fun showMessage() {
        val message = getString(R.string.wsa_toast_min_words_count, Constants.ANSWERS_COUNT)
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    private fun subscribeToLiveData() {
        with(viewModel) {
            wordsLiveData.observe(viewLifecycleOwner) { wordList: List<Word> ->
                wordSelectionAdapter?.setWordList(wordList)
            }

            messageLiveData.observe(viewLifecycleOwner) { showMessage() }

            choiceDialogLiveData.observe(viewLifecycleOwner) { categoryName ->
                categoryName?.let { showDialog(categoryName) }
            }

            selectedWordsLiveData.observe(viewLifecycleOwner) { dto ->
                dto?.let { startExercise(dto) }
            }
        }
    }

    private fun showDialog(categoryName: String) {
        childFragmentManager.setFragmentResultListener(
            Constants.EXERCISE_CHOICE_FRAGMENT,
            viewLifecycleOwner
        ) { _, bundle ->
            val exerciseChoiceDto =
                bundle.getSerializable(Constants.EXERCISE_CHOICE_KEY) as ExerciseChoiceDto
            viewModel.sendDTO(exerciseChoiceDto)
        }

        val dialogFragment = ExerciseChoiceFragment.newInstance(categoryName)
        dialogFragment.show(
            childFragmentManager,
            Constants.EXERCISE_CHOICE_FRAGMENT
        )

    }

    private fun startExercise(dto: WordSelectionDto) {
        val intent = Intent(activity, ExerciseActivity::class.java)
        intent.putExtra(Constants.EXERCISE_TYPE, dto.exercise)
        intent.putParcelableArrayListExtra(Constants.SELECTED_WORDS, dto.selectedWordList)
        intent.putExtra(Constants.TRANSLATION_DIRECTION, dto.isTranslationDirection)
        startActivity(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance(categoryName: String?): Fragment {
            val args = Bundle()
            args.putString(Constants.ARG_CATEGORY_NAME, categoryName)
            val fragment: Fragment = WordSelectionFragment()
            fragment.arguments = args
            return fragment
        }
    }
}