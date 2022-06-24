package com.vladimir_x.easyenglishlearn.ui.exercises

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.vladimir_x.easyenglishlearn.Constants
import com.vladimir_x.easyenglishlearn.ui.State
import com.vladimir_x.easyenglishlearn.ui.State.CompletedState
import com.vladimir_x.easyenglishlearn.ui.State.DataState
import com.vladimir_x.easyenglishlearn.ui.State.ErrorState
import com.vladimir_x.easyenglishlearn.model.Answer
import com.vladimir_x.easyenglishlearn.model.Word

abstract class ExerciseViewModel(
    state: SavedStateHandle
) : ViewModel() {
    private var iteration = 0
    private var errorCount = 0
    private var question: String = ""
    var translationDirection = true
    var currentWord: Word? = null
    val wordList = mutableListOf<Word>()

    private val _exerciseState = MutableLiveData<State>(State.IdleState)
    val exerciseState: LiveData<State>
        get() = _exerciseState

    private val isExerciseOver: Boolean
        get() = iteration >= wordList.size

    init {
        val translationDirection =
            state.get<Boolean>(Constants.TRANSLATION_DIRECTION) ?: true
        val wordList: List<Word> =
            state.get<ArrayList<Word>>(Constants.SELECTED_WORDS) as? List<Word>
                ?: emptyList()
        this.wordList.addAll(wordList)
        this.translationDirection = translationDirection
        errorCount = 0
        iteration = 0
    }

    open fun prepareQuestionAndAnswers() {
        currentWord = wordList[iteration].also {
            question = if (translationDirection) it.lexeme else it.translation
        }
    }

    fun convertWordToQuestion(word: Word?): String =
        if (translationDirection) word?.lexeme ?: "" else word?.translation ?: ""

    fun checkAnswer(answer: CharSequence) {
        if (isAnswerCorrect(answer)) {
            iteration++
            if (isExerciseOver) {
                finishExercise()
            } else {
                prepareQuestionAndAnswers()
            }
        } else {
            errorCount++
            showError()
            prepareQuestionAndAnswers()
        }
    }

    fun sendData(data: DataDto) {
        changeState(DataState(data))
    }

    private fun changeState(state: State) {
        _exerciseState.value = state
    }

    private fun finishExercise() {
        changeState(CompletedState(errorCount))
    }

    private fun showError() {
        changeState(ErrorState())
    }

    private fun isAnswerCorrect(answer: CharSequence): Boolean =
        Answer(currentWord, answer, translationDirection).isCorrect
}