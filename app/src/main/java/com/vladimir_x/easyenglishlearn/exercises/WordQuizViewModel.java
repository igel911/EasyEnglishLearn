package com.vladimir_x.easyenglishlearn.exercises;

import androidx.lifecycle.LiveData;
import androidx.databinding.ObservableField;

import com.vladimir_x.easyenglishlearn.SingleLiveEvent;
import com.vladimir_x.easyenglishlearn.model.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.vladimir_x.easyenglishlearn.Constants.ANSWERS_COUNT;

public class WordQuizViewModel extends ExerciseViewModel {

    private SingleLiveEvent<Void> mClearRadioGroupLiveData;
    public final ObservableField<List<String>> answers = new ObservableField<>();


    public WordQuizViewModel() {
        mClearRadioGroupLiveData = new SingleLiveEvent<>();
    }

    public void onAnswerChecked(String answer) {
        mAnswerBuilder.append(answer);
        checkAnswer();
    }

    @Override
    void prepareQuestionAndAnswers() {
        super.prepareQuestionAndAnswers();

        List<String> answerList = new ArrayList<>();
        addAnswer(answerList, mCurrentWord);
        while (answerList.size() < ANSWERS_COUNT) {
            int randomIndex = new Random().nextInt(mWordList.size());
            Word tempWord = mWordList.get(randomIndex);
            if (isWordNotInAnswerList(answerList, tempWord)) addAnswer(answerList, tempWord);
        }
        Collections.shuffle(answerList);
        answers.set(answerList);
    }

    @Override
    void cleanPreviousData() {
        super.cleanPreviousData();
        mClearRadioGroupLiveData.call();
    }

    private boolean isWordNotInAnswerList(List<String> answerList, Word word) {
        return !answerList.contains(mTranslationDirection
                ? word.getTranslation()
                : word.getLexeme());
    }

    private void addAnswer(List<String> answerList, Word word) {
        answerList.add(mTranslationDirection
                ? word.getTranslation()
                : word.getLexeme());
    }

    LiveData<Void> getClearRadioGroupLiveData() {
        return mClearRadioGroupLiveData;
    }
}
