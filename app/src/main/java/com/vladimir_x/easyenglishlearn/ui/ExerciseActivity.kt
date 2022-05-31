package com.vladimir_x.easyenglishlearn.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vladimir_x.easyenglishlearn.Constants
import com.vladimir_x.easyenglishlearn.R
import com.vladimir_x.easyenglishlearn.databinding.ActivityExerciseBinding
import com.vladimir_x.easyenglishlearn.ui.exercises.ConstructorFragment
import com.vladimir_x.easyenglishlearn.ui.exercises.QuizFragment
import com.vladimir_x.easyenglishlearn.model.Word
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class ExerciseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExerciseBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent
        val exerciseType = intent.getStringExtra(Constants.EXERCISE_TYPE)
        val selectedWordList =
            intent.getParcelableArrayListExtra<Word>(Constants.SELECTED_WORDS) as ArrayList<Word>
        val translationDirection = intent.getBooleanExtra(Constants.TRANSLATION_DIRECTION, true)
        val fm = supportFragmentManager
        var fragment = fm.findFragmentById(R.id.exercise_fragment_container)
        if (fragment == null) {
            fragment = when (exerciseType) {
                Constants.WORD_CONSTRUCTOR -> ConstructorFragment
                    .newInstance(selectedWordList, translationDirection)
                else -> QuizFragment
                    .newInstance(selectedWordList, translationDirection)
            }
            fm.beginTransaction()
                .add(R.id.exercise_fragment_container, fragment)
                .commit()
        }
    }
}