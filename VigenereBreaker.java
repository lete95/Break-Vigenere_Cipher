import java.util.*;
import edu.duke.*;

public class VigenereBreaker {
  public String sliceString(String message, int whichSlice , int totalSlices){
      StringBuilder s = new StringBuilder();
      StringBuilder o = new StringBuilder(message);
      int i = 0;
      for (int k= whichSlice ;k< message.length(); k += totalSlices){
          s.insert(i,o.charAt(k));
          i++;
      }
      String slice = s.toString();
      return slice;
  }

  public int[] tryKeyLength(String encrypted, int klength, char mostCommon) {
     int[] key = new int[klength];
     for (int i=0; i < key.length; i++){
          String sliced = sliceString(encrypted,i,klength);
          CaesarCracker cc = new CaesarCracker();
          int k = cc.getKey(sliced);
          key[i]= k;
     }
     return key;
  }
  
  public void testTryKeyLength(){
       FileResource fr = new FileResource();
       String message = fr.asString();
       int[] array =tryKeyLength(message, 38, 'e');
       System.out.println(Arrays.toString(array));
  }
  
  public HashSet<String> readDictionary(FileResource fr){
      HashSet<String> hs = new HashSet<String>();
      for(String s : fr.lines())
      {
          s.toLowerCase();
          hs.add(s);
      }
      return hs;
  }
    
  public int countWords(String message , HashSet<String> dictionary){
      int count=0;
      for(String word : message.split("\\W+"))
      {
          word = word.toLowerCase();
          if(dictionary.contains(word)){   
              count++;
          }
      }
      return count;
   }
  
  public String breakForLanguage(String encrypted , HashSet<String> dictionary){
      int max = 0 ,c=1, i , length =0;
      int[] key2=new int[0];
      String ans = "";
      for(i=1 ; i<= 100 ; i++)
      {
          char commonLetter= mostCommonCharIn(dictionary);
          int[] key = tryKeyLength(encrypted,i,commonLetter);
          VigenereCipher vr = new  VigenereCipher(key);
          String decrypted = (vr.decrypt(encrypted));
          length = countWords(decrypted , dictionary);// this gives length as 0 all the time
          if(length > max){
              max = length;
              ans = decrypted; 
              key2=key;
          }
       }
      System.out.println("The key with most real words contains a number of words equal to: "+max);
      System.out.println("And the key is: "+Arrays.toString(key2));
      System.out.println(Arrays.toString(key2).length());
      return ans;      
  }
  
  public String breakForAllLangs(String encrypted, HashMap<String,HashSet<String>> languages){
      int countMaxWords = 0;//maxwords for all languages;
      int maxWords = 0;
      String decrypted = "";
      String language = "";
      for(String s : languages.keySet()){
          HashSet<String> dict = languages.get(s);
          String decrypto =  breakForLanguage(encrypted, dict);
          System.out.println("It happened in "+s+" language");
          maxWords = countWords(decrypto, dict);//this will give you maxWord in breakForLanguage
          if(maxWords > countMaxWords){
              countMaxWords = maxWords;
              decrypted = decrypto;
              language = s;
          }
      }
      System.out.println(" ");
      System.out.println("The best language is: "+language);
      return decrypted;
  }
    
  public char mostCommonCharIn(HashSet<String> dictionary){
      HashMap<Character,Integer> letterCounts = new HashMap<Character,Integer>();
      for(String word : dictionary){
          word.toLowerCase();
          for(char ch : word.toCharArray()) {
              if(!letterCounts.containsKey(ch)) {
                  letterCounts.put(ch, 1);
              }
              else {       
                  int freq = letterCounts.get(ch);
                  letterCounts.put(ch,freq +1);
              }
          }
      }
      char maxCh = highestChar(letterCounts);
      return maxCh;
  }
    
  private char highestChar(HashMap<Character, Integer> map){
      char maxCh ='\0';
      int maxLetter = 0;
      for(char ch : map.keySet()){
          int value = map.get(ch);
          if(maxLetter == 0){
              maxLetter = value;
              maxCh = ch;
          }
          else if(value > maxLetter){
              maxLetter = value;
              maxCh = ch;
          }
      }
      return maxCh;
  }
  
  public void breakVigenereEnglish () {
      FileResource fr= new FileResource();
      String encrypt=fr.asString();
      HashSet<String> dictionary = new HashSet<String>();
      FileResource fr2 = new FileResource("English");
      dictionary = readDictionary(fr2); 
      //System.out.println("Encrypted message_ "+encrypt); 
      String decrypt =  breakForLanguage(encrypt,dictionary);
      System.out.println("Decrypted message: "+decrypt.substring(0,43)); 
  }
                    
  public void breakVigenereFull() {
      FileResource fr = new FileResource();// remember call file given  here
      String encrypted = fr.asString();
      String[] langDict = new String[]{"Danish","English","Dutch","French","German","Italian","Portuguese","Spanish"};
      HashMap<String,HashSet<String>> myMap = new HashMap<String,HashSet<String>>();
      for(int i = 0; i < langDict.length; i++){
          String lang = langDict[i];
          FileResource fri = new FileResource(lang); // fri = fr1, fr2,fr3
          HashSet<String> dicWordi = readDictionary(fri);
          myMap.put(lang,dicWordi);
      }
      String decrypted = breakForAllLangs(encrypted, myMap);
      System.out.println(" ");
      System.out.println(decrypted);
  }
    
}
