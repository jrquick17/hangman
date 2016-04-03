import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Hangman {
    String answered, guessed[] = new String[26], letter,
    posLetters[] = "a b c d e f g h i j k l m n o p q r s t u v w x y z".split(" ");
    boolean isComplete = false, multLetter, struckOut = false;
    int placement, strikes;
    double perLetters[] = new double[26];

    Random gen = new Random();
    Scanner scan = new Scanner(System.in);

    String[] answer, currentWordArray;
    Opponent opponent;

    public Hangman() {
        opponent = new Opponent();
    }

    public String findNextGuess(boolean show) {
        if (show) {
            System.out.println("Letter\tChance");
        }
        for (int let = 0, num = 0; let < 26; let++, num = 0)
        {
            for (int i = 0; i < opponent.getWords().size(); i++)
            {
                if (opponent.getWords().get(i).contains(posLetters[let]))
                    num++;
            }

            perLetters[let] = ((double)num/opponent.getWords().size()*100);

            if (show)
                System.out.println(posLetters[let] + ":\t" + perLetters[let]);
        }

        if (!show)
        {
            double maxAmt = -1;
            int maxInc = 0; 

            for (int i = 0; i < 26; i++)
            {
                if (maxAmt < perLetters[i] && guessed[i] == null)
                {
                    maxAmt = perLetters[i];
                    maxInc = i;
                }
            }

            guessed[maxInc] = posLetters[maxInc];
            System.out.println("I guess the letter " + posLetters[maxInc]);

            return posLetters[maxInc];
        }
        else
            return null;
    }

    public ArrayList<String> removeByLength(int length)
    {
        for (int i = 0; i < opponent.getWords().size(); i++)
        {
            if (opponent.getWords().get(i).length() > length || opponent.getWords().get(i).length() < length)
            {
                opponent.getWords().remove(i);
                i--;
            }
        }

        return opponent.getWords();
    }

    public ArrayList<String> removeByCorrectLetter(String letter, int placement) {
        for (int i = 0; i < opponent.getWords().size(); i++) {
            if (!opponent.getWords().get(i).substring(placement, placement+1).contains(letter)) {
                opponent.getWords().remove(i);
                i--;
            }
        }

        return opponent.getWords();
    }

    public ArrayList<String> removeByWrongLetter(String letter) {
        for (int i = 0; i < opponent.getWords().size(); i++) {
            if (opponent.getWords().get(i).contains(letter)) {
                opponent.getWords().remove(i);
                i--;
            }
        }

        return opponent.getWords();
    }

    public boolean alreadyGuessed(String letter) {
        for (int i = 0; i < 26; i++) {
            if (letter.equalsIgnoreCase(guessed[i])) {
                return true;
            }
        }

        return false;
    }

    public void addToGuessed(String letter)
    {
        for (int i = 0; i < posLetters.length; i++)
        {
            if (letter.equalsIgnoreCase(posLetters[i]))
                guessed[i] = letter;
        }
    }

    public void help() {
        System.out.println("How long is the word? ");
        currentWordArray = new String[scan.nextInt()];

        opponent.setWords(removeByLength(currentWordArray.length));

        System.out.println("Enter the known letter: ");
        letter = scan.next();
        System.out.println("Enter the placement: ");
        placement = scan.nextInt();
        currentWordArray[placement] = letter;
        addToGuessed(letter);

        opponent.setWords(removeByCorrectLetter(letter, placement));

        while (!isComplete) {
            System.out.println(WordService.toString(currentWordArray));

            findNextGuess(true);

            System.out.println("Did your guess work? ");
            answered = scan.next();
            if (answered.equalsIgnoreCase("No"))
            {
                System.out.println("What letter did you guess? ");
                letter = scan.next();
                opponent.setWords(removeByWrongLetter(letter));
            }
            else if (answered.equalsIgnoreCase("Yes"))
            {
                System.out.println("Enter the known letter: ");
                letter = scan.next();
                System.out.println("Enter the placement: ");
                placement = scan.nextInt();
                currentWordArray[placement] = letter;

                opponent.setWords(removeByCorrectLetter(letter, placement));
            } else if (answered.equalsIgnoreCase("Show")) {
                for (int i = 0; i < opponent.getWords().size(); i++) {
                    System.out.println(opponent.getWords().get(i));
                }
            }

            isComplete = checkIfComplete();
        }

        System.out.println("Thank me later.");
    }

    public void create() {
        System.out.println("How many letters are in your word? ");
        currentWordArray = new String[scan.nextInt()];
        opponent.setWords(removeByLength(currentWordArray.length));

        System.out.println("And what letter are you giving me? ");
        letter = scan.next();
        if (!letter.equalsIgnoreCase("None")) {
            System.out.println("Where is the letter located? ");
            placement = scan.nextInt();
            opponent.setWords(removeByCorrectLetter(letter, placement));
            addToGuessed(letter);
            currentWordArray[placement] = letter;
        }

        while (!isComplete && !struckOut) {
            System.out.println(WordService.toString(currentWordArray));

            letter = findNextGuess(false);

            System.out.println("Was I correct? ");
            answered = scan.next();

            if (answered.equalsIgnoreCase("Yes")) {
                multLetter = true;
                while (multLetter) {
                    System.out.println("Where is the letter located? ");
                    placement = scan.nextInt();
                    opponent.setWords(removeByCorrectLetter(letter, placement));
                    currentWordArray[placement] = letter;

                    if (!checkIfComplete()) {
                        System.out.println("Is there multiple places for that letter? ");
                        answered = scan.next();
                        if (answered.equalsIgnoreCase("no"))
                            multLetter = false;
                    } else {
                        multLetter = false;
                    }
                }
            } else if (answered.equalsIgnoreCase("no")) {
                opponent.setWords(removeByWrongLetter(letter));
                struckOut = addStrike();
            } else {
                System.out.println("Screwing up, eh?");
            }

            isComplete = checkIfComplete();
        }

        if (isComplete)
            System.out.println("Suck on that fool!");
        else if (struckOut)
            System.out.println("I'm not sure how, but we both know you're a cheating bastard!");
    }

    public boolean checkIfComplete() {
        for (int i = 0; i < currentWordArray.length; i++) {
            if (currentWordArray[i] == null) {
                return false;
            }
        }

        return true;
    }

    public boolean addStrike() {
        strikes++;

        System.out.println("Strike " + strikes + " of 6!");
        switch (strikes) {
            case 1:
                System.out.println("  ___\n /   |\n O   |\n     |\n     |\n     |\n ____|____");
                break;
            case 2:
                System.out.println("  ___\n /   |\n O   |\n |   |\n |   |\n     |\n ____|____");
                break;
            case 3:
                System.out.println("  ___\n /   |\n O   |\n |   |\n |   |\n  \\  |\n ____|____");
                break;
            case 4:
                System.out.println("  ___\n /   |\n O   |\n |   |\n |   |\n/ \\  |\n ____|____");
                break;
            case 5:
                System.out.println("  ___\n /   |\n O   |\n\\|   |\n |   |\n/ \\  |\n ____|____");
                break;
            case 6:
            default:
                System.out.println("  ___\n /   |\n O   |\n\\|/  |\n |   |\n/ \\  |\n ____|____");
                break;
        }
        
        return strikes == 6;
    }

    public boolean answerContains(String guess) {
        for (String letter : answer) {
            if (guess.equals(letter)) {
                return true;
            }
        }

        return false;
    }

    public void fillIn(String letter) {
        for (int i = 0; i < answer.length; i++) {
            if (answer[i].equals(letter)) {
                currentWordArray[i] = answer[i];
            }
        }
    }

    public boolean guess(String letter) {
        if (!this.alreadyGuessed(letter)) {
            this.addToGuessed(letter);

            if (this.answerContains(letter)) {
                this.fillIn(letter);
                return true;
            } else {
                this.addStrike();
            }
        } else {
            System.out.println("Cute, but you already guessed that.");
        }

        return false;
    }

    public boolean isStruckOut() {
        return strikes == 6;
    }

    public boolean isComplete() {
        for (String letter : currentWordArray) {
            if (letter == null) {
                return false;
            }
        }

        return true;
    }

    public boolean keepPlaying() {
        return !isStruckOut() && !this.isComplete();
    }

    public void play() {
        String currentWord = opponent.selectWord();
        currentWordArray = new String[currentWord.length()];
        answer = WordService.toArray(currentWord);

        System.out.println("Bet you can't guess my word, it's " + currentWordArray.length + " characters long.\nI'll even give you a letter.");
        this.fillIn(opponent.getFreebie(currentWord));

        while (this.keepPlaying()) {
            System.out.println(WordService.toString(currentWordArray));

            System.out.println("What letter would you like to guess? ");

            if (this.guess(scan.next())) {
                System.out.println("Lucky guess!");
            } else {
                System.out.println("Nope!");
            }
        }

        if (this.isComplete()) {
            System.out.println("You must have cheated!");
        } else if (this.isStruckOut()) {
            System.out.println("Didn't really expect much more from you honestly.\nThe word was " + WordService.toString(answer));
        }
    }
}
