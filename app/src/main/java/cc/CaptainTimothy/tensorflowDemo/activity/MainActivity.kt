package cc.CaptainTimothy.tensorflowDemo.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.widget.Toast
import cc.CaptainTimothy.tensorflowDemo.DigitsDetector
import cc.CaptainTimothy.tensorflowDemo.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //private val PIXEL_WIDTH = 28        // TODO: uncommit this when ready
    var numOfCorrect = 0
    var numOfWrong = 0
    var questionPrint = StringBuilder()
    var lastChoice = -1

    private lateinit var mnistClassifier: DigitsDetector
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mnistClassifier = DigitsDetector(this)
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)

        //paintView.init(metrics)        // TODO: uncommit this when ready
        val questionGeneration = WordGen()

        // get question
        questionPrint = questionGeneration.getQuestion(lastChoice)

        // get dictionary number
        lastChoice = questionGeneration.dictionaryNumber

        // get answer
        var answer = questionGeneration.answer

        // store last question
        question.setText(questionPrint)

        check.setText("Reset C/W  $numOfCorrect/$numOfWrong")
        button_detect.setOnClickListener {

            // get user answer
            // TODO: use tensorflow to get the answer
            var userAnswer = editText.text.toString()

            // check user's answer
            if (questionGeneration.validateUserAnswer(userAnswer)) {

                // convert answer to char
                var userAnswerChar = userAnswer.toCharArray()

                if (userAnswerChar[0] == answer) {

                    // if it's correct
                    Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show()
                    numOfCorrect++
                } else {

                    // if it's wrong
                    Toast.makeText(this, "Wrong", Toast.LENGTH_SHORT).show()
                    numOfWrong++
                }

                // refresh whole page to print new question and a clear board
                editText.text.clear()                                           // TODO: remove this line when ready
                //paintView.clear()                                             // TODO: uncommit this later when ready
                questionPrint = questionGeneration.getQuestion(lastChoice)      // refresh question
                lastChoice = questionGeneration.dictionaryNumber                // save dictionary number
                answer = questionGeneration.answer                              // refresh answer
                question.setText(questionPrint.toString())                      // redraw new question
                check.setText("Reset C/W  $numOfCorrect/$numOfWrong")           // redraw 'reset' button
            } else {

                Toast.makeText(this, "Please enter only ONE character", Toast.LENGTH_SHORT).show()
            }

        }

        // clear user answer area
        button_clear.setOnClickListener {

            //paintView.clear()        // TODO: uncommit this when ready
            editText.text.clear()      // TODO: remove this when ready
        }

        // clear when tap 'reset'
        check.setOnClickListener {

            numOfCorrect = 0
            numOfWrong = 0
            check.setText("Reset C/W  $numOfCorrect/$numOfWrong")
        }
    }
}
