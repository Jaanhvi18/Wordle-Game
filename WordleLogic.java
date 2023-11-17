import java.awt.Color;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class WordleLogic{
  
  //Toggle DEBUG MODE On/Off
  public static final boolean DEBUG_MODE = false;  
  //Toggle WARM_UP On/Off
  public static final boolean WARM_UP = false;
  
  
  private static final String FILENAME = "englishWords5.txt";
  //Number of words in the words.txt file
  private static final int WORDS_IN_FILE = 5758; // Review BJP 6.1 for  
  
  //Use for generating random numbers!
  private static final Random rand = new Random();
  
    
	public static final int MAX_ATTEMPTS = 6;
	public static final int WORD_LENGTH = 5; 
	                               
	
	private static final char EMPTY_CHAR = WordleView.EMPTY_CHAR;  
	
 
  //************       Color Values       ************
  
  //Green (right letter in the right place)
  private static final Color CORRECT_COLOR = new Color(53, 209, 42); 
  //Yellow (right letter in the wrong place)
  private static final Color WRONG_PLACE_COLOR = new Color(235, 216, 52); 
  //Dark Gray (letter doesn't exist in the word)
  private static final Color WRONG_COLOR = Color.DARK_GRAY; 
  //Light Gray (default keyboard key color, letter hasn't been checked yet)
  private static final Color NOT_CHECKED_COLOR = new Color(160, 163, 168); 
  
  private static final Color DEFAULT_BGCOLOR = Color.BLACK;

  //***************************************************
  
  //************      Class variables     ************

  //Add them as necessary (I have some but less than 5)

  public static int current_row = 0;
  public static int current_col = 0;
  public static char[] current_word = new char[WORD_LENGTH];
  public static String secret = null;
  public static Set <String> word_set = new HashSet<String>();
  public static Set <String> checked_words = new HashSet<String>();
  //***************************************************

  
  
  //************      Class methods     ************

  // There are 6 already defined below, with 5 of them to be completed.
  // Add class helper methods as necessary. Our solution has 12 of them total.
  
  
  // Complete for 3.1.1
  public static void warmUp() {
  	WordleView.setCellLetter(0,0,'C');
    WordleView.setCellColor(0,0,CORRECT_COLOR);
    
    WordleView.setCellLetter(1,2,'O');
    WordleView.setCellColor(1,2,WRONG_COLOR);

    WordleView.setCellLetter(3,3,'S');
    

    WordleView.setCellLetter(5,4,'C');
    WordleView.setCellColor(5,4,WRONG_PLACE_COLOR);
  	
  	
  	
  	
  }
  
  
  
  //This function gets called ONCE when the game is very first launched
  //before the player has the opportunity to do anything.
  //
  //Returns the chosen mystery word the user needs to guess
  public static String init() throws FileNotFoundException {
 
  	try{
      try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
        String read_line = reader.readLine();
        ArrayList<String> mystery_word = new ArrayList<String>();
        while(read_line!= null){
        mystery_word.add(read_line);
        word_set.add(read_line);
        read_line = reader.readLine();

        }
        String element = mystery_word.get(rand.nextInt(WORDS_IN_FILE));
        return element;
      }
       
       
      
    } 
    
    catch(Exception e){
    }

    
    return null; //placeholder: return dummy
  }
  
  
  
  //This function gets called everytime the user inputs 'Backspace'
  //pressing the physical or virtual keyboard.
  // call on Backspace input
  public static void deleteLetter(){
  	if (DEBUG_MODE) {
  		System.out.println("in deleteLetter()");
  	}
    System.out.println(current_col);
    if (current_col > 0){
      WordleView.setCellLetter(current_row,--current_col, EMPTY_CHAR);
      current_word[current_col] = ' ';
      System.out.println(Arrays.toString(current_word));
    }
  	
  	
  	
  	
  
  }

  public static int curWordCount(char check){
    int count = 0;
    for (int i = 0; i < WORD_LENGTH; i++){
      if (current_word[i] == check)
        count++;
    }
    return count;
  }
  public static int secWordCount(char check){
    int count = 0;
    for (int i = 0; i < WORD_LENGTH; i++){
      if (secret.charAt(i) == check)
        count++;
    }
    return count;
  }



  public static boolean change_colors(){
    Set<Character> checked = new HashSet<Character>();
    boolean flag = true;
    char hold1, hold2; int inWord, inSecret;
    for (int i = 0; i < WORD_LENGTH; i++){
      hold1 = current_word[i]; hold2 = secret.charAt(i);
      if (checked.contains(hold1))
        continue;
      inWord = curWordCount(hold1); inSecret = secWordCount(hold1);    
        if (hold1 == hold2){
          WordleView.setCellColor(current_row, i, CORRECT_COLOR);
          update_keyboard(i, CORRECT_COLOR);
        }
        else if (inWord == 1 && inSecret == 1){
          WordleView.setCellColor(current_row, i, WRONG_PLACE_COLOR);
          update_keyboard(i, WRONG_PLACE_COLOR);
          flag = false;
        }
        else if (inWord == 1 && inSecret > 1){
          WordleView.setCellColor(current_row, i, WRONG_PLACE_COLOR);
          update_keyboard(i, WRONG_PLACE_COLOR);
          flag = false;
        }
          
        else if (inWord > 1 && inSecret == 1){
          oneWord(hold1); flag = false;
          checked.add(hold1);
        }

        else if (inWord > 1 && inSecret > 1){
          if (flag == true)
            flag = handleDuplicates(hold1, inWord, inSecret);
          handleDuplicates(hold1, inWord, inSecret);
          checked.add(hold1);
        }

        else if (inSecret == 0){
          WordleView.setCellColor(current_row, i, WRONG_COLOR);
          update_keyboard(i, WRONG_COLOR);
          flag = false;
        }
        
    }
    return flag;
  }

  public static void oneWord(char key){
    boolean hold = false;
    for (int i = 0; i < WORD_LENGTH; i++){
      if (current_word[i] == key)
        WordleView.setCellColor(current_row, i, WRONG_COLOR);
    }

    for (int i = 0; i < WORD_LENGTH; i++){
      if (current_word[i] == key && secret.charAt(i) == key){
        WordleView.setCellColor(current_row, i, CORRECT_COLOR);
        WordleView.setKeyboardColor(key, CORRECT_COLOR);
        hold = true;
        break;
      }
    }
    if (hold)
      return;
    for (int i = 0; i < WORD_LENGTH; i++){
      if (current_word[i] == key){
        WordleView.setCellColor(current_row, i, WRONG_PLACE_COLOR);
        WordleView.setKeyboardColor(key, WRONG_PLACE_COLOR);
        break;
      }
    }
    
  }

  public static boolean handleDuplicates(char key, int inWord, int inSecret){
    boolean flag = true; int i = 0;
    System.out.println(inWord + "  " + inSecret);
    while (inSecret > 0 && inWord > 0 && i < WORD_LENGTH){
      System.out.println(current_word[i]);
      if (current_word[i] == secret.charAt(i) && secret.charAt(i) == key){
        WordleView.setCellColor(current_row, i, CORRECT_COLOR);
        WordleView.setKeyboardColor(key, CORRECT_COLOR);
        inWord --; inSecret--;
      }
      else if (current_word[i] == key){
        WordleView.setCellColor(current_row, i, WRONG_PLACE_COLOR);
        WordleView.setKeyboardColor(key, WRONG_PLACE_COLOR);
        inWord --; inSecret --; flag = false;
      }
      i++;
    }
    while (inWord > 0){
      for (i = 0; i < WORD_LENGTH; i++){
        if (current_word[i] == secret.charAt(i) && secret.charAt(i) == key){
          WordleView.setCellColor(current_row, i, WRONG_COLOR);
          inWord --; flag = false;
        }
      }
    }
      return flag;
  }

  public static void update_keyboard(int col, Color clr){
    if (clr == CORRECT_COLOR)
      WordleView.setKeyboardColor(current_word[col], CORRECT_COLOR);
    else if (clr == WRONG_PLACE_COLOR){
      if (WordleView.getKeyboardColor(current_word[col]) != CORRECT_COLOR){
        WordleView.setKeyboardColor(current_word[col], WRONG_PLACE_COLOR);
      }
    }
    else if (WordleView.getKeyboardColor(current_word[col]).equals(NOT_CHECKED_COLOR)){
      WordleView.setKeyboardColor(current_word[col], clr);
    }
  }

  
  
  //This function gets called everytime the player inputs 'Enter'
  //pressing the physical or virtual keyboard.  
  public static void checkLetters() {
  	if (DEBUG_MODE) {
  		System.out.println("in checkLetters()");
  	}
    if (current_col!= WORD_LENGTH)
      return;
    
    String hold = "";
    for (int i = 0; i < WORD_LENGTH; i++)
      hold += current_word[i];
    hold = hold.toLowerCase();
    if (!word_set.contains(hold) || checked_words.contains(hold)){
      WordleView.wiggleRow(current_row);
      return;
    }
    boolean flag = change_colors();
    checked_words.add(hold);
    if (!flag && current_row < MAX_ATTEMPTS-1){
      current_col = 0;
      current_row++;
    }
    else
      WordleView.gameOver(flag);
  }
  	
  
  
  
  //This function gets called everytime the player types a valid letter
  //on the keyboard or clicks one of the letter keys on the 
  //graphical keyboard interface.  
  //The key pressed is passed in as a char uppercase for letter
  public static void inputLetter(char key){
  	
  	//Some placeholder debugging code...
  	System.out.println("Letter pressed!: " + key);
  	if (current_col < WORD_LENGTH){
      WordleView.setCellLetter(current_row,current_col++, key);
      current_word[current_col-1] = key;
    }
  	
  	if (WARM_UP) {
  		
  		System.out.println("A row should wiggle");
  		
  		
  		
  		
  	} 
  
  	
  	
  	
  	
  }  
  
  
  
  //Initializes and launches the game logic and its GUI window
	public static void main(String[] args) throws FileNotFoundException {              
		
		if (!WARM_UP) {
			//Calls to intialize the game logic and pick the secret word
			secret = WordleLogic.init();
      secret = secret.toUpperCase();
		}
		
		//Creates the game window
		WordleView.create(secret);
		
		if (WARM_UP) {
			warmUp();

		} 
	}       
}