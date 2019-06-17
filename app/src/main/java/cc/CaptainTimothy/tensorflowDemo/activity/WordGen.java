package cc.CaptainTimothy.tensorflowDemo.activity;

public class WordGen {

    private StringBuilder wordQuestion;     // used to print question
    private char wordAnswer;                // used to set correct answer
    private int dictNumber;
    private String[] dict = {
            "Nokia", "Apple", "ASUS",
            "Canon", "Google", "Microsoft",
            "Aigo", "Xiaomi", "Samsung",
            "Sony", "Lenovo", "IBM",
            "Sandisk", "Philips", "TOSHIBA",
            "Vivo", "OPPO", "HP",
            "Intel", "Nvidia", "Haier",
            "Panasonic"
    };    // TODO: try to read dictionary from file

    // generate a question
    public StringBuilder getQuestion(int lastChoice) {

        int deleteCharNumber;

        // decide which word to choose in dictionary
        do {
            dictNumber = (int) (Math.random() * dict.length);
        } while (dictNumber == lastChoice);

        // choose the word with number 'dictNumber'
        this.wordQuestion = new StringBuilder(dict[dictNumber]);

        // decide which char should be replaced
        deleteCharNumber = (int) (Math.random() * wordQuestion.length());

        // set 'wordAnswer' as the char which will be replaced
        this.wordAnswer = wordQuestion.charAt(deleteCharNumber);

        // replace the character
        wordQuestion.setCharAt(deleteCharNumber, '_');

        return this.wordQuestion;
    }

    // return correct answer
    public char getAnswer() {

        return this.wordAnswer;
    }

    public int getDictionaryNumber() {

        return this.dictNumber;
    }

    // check if user's answer is valid
    public boolean validateUserAnswer(String userAnswer) {

        if (userAnswer.length() != 1) {

            return false;
        } else {

            return true;
        }
    }

}
