import java.util.*; 
import java.io.BufferedReader; 
import java.io.FileReader; 
import java.io.IOException; 

public class CFG2CNF {
 
  private Map < String, List < String >> cfgMap = new HashMap < > ();
  public CFG2CNF() {
    convertCFGtoMap();
    System.out.println("CFG is :");
    printMap();
  }
  public void start() {
    eliminateEpsilon();   
    eliminateUnitProduction(); 
    onlyTwoTerminalandOneVariable(); 
    breakVariableLongerThanTwo(); 
  }


  private void convertCFGtoMap() {
    try (BufferedReader fileReader = new BufferedReader(new FileReader("src/CFG.txt"))) {
      String line = fileReader.readLine();
      line = fileReader.readLine();
      while (line != null) {
        String[] tempArray = line.split("-|\\|");
        String variable = tempArray[0].trim();
        String[] production = Arrays.copyOfRange(tempArray, 1, tempArray.length);

        List < String > productionList = new ArrayList < > ();

        for (String s: production) {
          s = s.trim();
          productionList.add(s);
        }
        cfgMap.put(variable, productionList);

        line = fileReader.readLine();
      }
    } catch (IOException e) {

      e.printStackTrace();
    }
  }

  private void printMap() {

    Iterator it = cfgMap.entrySet().iterator();

    while (it.hasNext()) {

      Map.Entry pair = (Map.Entry) it.next();

      String variable = (String) pair.getKey();

      List < String > productions = (List < String > ) pair.getValue();

      StringBuilder sb = new StringBuilder();

      sb.append(variable).append(" → ");

      for (int i = 0; i < productions.size(); i++) {

        sb.append(productions.get(i));

        if (i < productions.size() - 1) {
          sb.append(" | ");
        }
      }

      System.out.println(sb.toString());
    }

    System.out.println(" ");
  }




  private void eliminateEpsilon() {

    System.out.println("Eliminating Epsilon....");

    for (int l = 0; l < cfgMap.size(); l++) {

      Iterator itr = cfgMap.entrySet().iterator();

      String epsilonFound = "";

      while (itr.hasNext()) {

        Map.Entry entry = (Map.Entry) itr.next();

        String variable = entry.getKey().toString();

        List < String > productions = (List < String > ) entry.getValue();

        if (productions.contains("€")) {

          if (productions.size() > 1) {
            productions.remove("€");

            epsilonFound = variable;
          } else { 
            epsilonFound = variable;
            cfgMap.remove(epsilonFound);
          }
        }
      }

      itr = cfgMap.entrySet().iterator();

      while (itr.hasNext()) {

        Map.Entry entry = (Map.Entry) itr.next();

        List < String > productions = (List < String > ) entry.getValue();

        for (int i = 0; i < productions.size(); i++) {

          String production = productions.get(i);

          for (int j = 0; j < production.length(); j++) { 
            if (epsilonFound.equals(Character.toString(production.charAt(j)))) {
              String deletedProduction = "";
              if (production.length() == 2) {
                deletedProduction = production.replace(epsilonFound, "");
              } else if (production.length() == 3 || production.length() == 4) { 
                deletedProduction = new StringBuilder(production).deleteCharAt(j).toString();
              } else {
                if (!productions.contains("€")) {
                  productions.add("€");
                }
                continue;
              }
              // if the modified production is not already in the list of productions for the current variable, add it to the list
              if (!productions.contains(deletedProduction)) {
                productions.add(deletedProduction);
              }
            }
          }
        }
      }
    }
    // print the updated Map data structure to the console
    printMap();
  }

  //method to remove duplicate keys and values from the Map data structure
  private void removeDuplicateKeyValue() {
    //create an iterator to loop through the entries in the Map data structure
    Iterator itr3 = cfgMap.entrySet().iterator();
    //continue looping until there are no more entries in the Map data structure
    while (itr3.hasNext()) {
      //get the next entry in the Map data structure
      Map.Entry entry = (Map.Entry) itr3.next();
      //store the value (list of productions) of the entry as an ArrayList of strings
      ArrayList < String > productionRow = (ArrayList < String > ) entry.getValue();
      //loop through the list of productions for the current variable
      for (int i = 0; i < productionRow.size(); i++) {
        //if the current production contains the current variable, remove the variable from the production
        if (productionRow.get(i).contains(entry.getKey().toString())) {
          productionRow.remove(entry.getKey().toString());
        }
      }
    }
  }
  
  private void deleteDuplicatedRow(Map<String, List<String>> hashtable) {
	    Iterator<String> iterator = hashtable.keySet().iterator();
	    while (iterator.hasNext()) {
	      String key = iterator.next();
	      List<String> values = hashtable.get(key);
	      if (hashtable.keySet().contains(values.get(0)) && values.size() == 1) {
	        iterator.remove();
	      }
	    }
	  }

  //method to eliminate unit productions from the Map data structure
  private void eliminateUnitProduction() {
    System.out.println("Eliminating Unit Production");
    //remove any duplicate keys and values from the Map data structure
    removeDuplicateKeyValue();
    //loop through the Map data structure
    for (int m = 0; m < cfgMap.size(); m++) {
      //create an iterator to loop through the entries in the Map data structure
      Iterator itr4 = cfgMap.entrySet().iterator();
      //continue looping until there are no more entries in the Map data structure
      while (itr4.hasNext()) {
        //get the next entry in the Map data structure
        Map.Entry entry = (Map.Entry) itr4.next();
        //store the key (variable) of the entry as a string
        String key = entry.getKey().toString();
        //store the value (list of productions) of the entry as a list of strings
        List < String > productionList = (List < String > ) entry.getValue();
        //loop through the list of productions for the current variable
        for (int i = 0; i < productionList.size(); i++) {
          //store the current production as a string
          String temp = productionList.get(i);
          //if the Map data structure contains a key that is equal to the current production, do the following:
          if (cfgMap.containsKey(temp)) {
            //store the key of the current entry as a string
            key = entry.getKey().toString();
            //get the list of productions for the current production (the key in the Map data structure)
            List < String > productionValue = cfgMap.get(temp);
            //remove the current production from the list of productions for the current variable
            productionList.remove(temp);
            //loop through the list of productions for the current production
            for (int l = 0; l < productionValue.size(); l++) {
              //add each production from the list of productions for the current production to the list of productions for the current variable
              cfgMap.get(key).add(productionValue.get(l));
            }
          }
        }
      }
    }
    //print the updated Map data structure to the console
    printMap();
  }

  //check if the given key is present as a production value in any other key's production list
  private Boolean checkDuplicateInProductionList(Map < String, List < String >> map, String key) {
    Iterator itr = map.entrySet().iterator();
    //iterate through the map and check if the given key is present in any other production list
    while (itr.hasNext()) {
      Map.Entry entry = (Map.Entry) itr.next();
      List < String > productionList = (List < String > ) entry.getValue();
      //check if the given key is present in the production list and it is the only production in the list
      if (productionList.size() == 1 && productionList.get(0).equals(key)) {
        return false;
      }
    }
    return true;
  }

  private void onlyTwoTerminalandOneVariable() {
    // Prints message to console
    System.out.println("Eliminate terminals");

    // Create temporary map to store new variables and their corresponding productions
    Map < String, List < String >> tempList = new HashMap < > ();

    // Set ascii value of Z as starting point for creating new variables
    // Variables will be created in the order Z, Y, X, W...
    int asciiBegin = 90;

    // Iterate through each entry in the cfgMap
    for (Map.Entry < String, List < String >> entry: cfgMap.entrySet()) {
      // Get the current key (variable) and its corresponding production list
      String key = entry.getKey();
      List < String > productionList = entry.getValue();

      // Iterate through each production in the production list
      for (int i = 0; i < productionList.size(); i++) {
        // Get the current production
        String temp = productionList.get(i);
        // Initialize newProduction to null
        String newProduction = null;

        // If production has length 3, set newProduction to the second and third characters
        if (temp.length() == 3) {
          newProduction = temp.substring(1, 3);
        }
        // If production has length 2, set newProduction to the second character
        else if (temp.length() == 2) {
          newProduction = Character.toString(temp.charAt(1));
        }
        // If production has length 4, set newProduction to the second and fourth characters
        else if (temp.length() == 4) {
          newProduction = temp.substring(0, 2) + temp.substring(2, 4);
        }

        // If newProduction is not null and does not exist in either tempList or cfgMap, create a new variable and add it to tempList
        if (newProduction != null && checkDuplicateInProductionList(tempList, newProduction) && checkDuplicateInProductionList(cfgMap, newProduction)) {
          List < String > newVariable = new ArrayList < > ();
          newVariable.add(newProduction);
          key = Character.toString((char) asciiBegin);
          tempList.put(key, newVariable);
          asciiBegin--;
        }
      }
    }

    // Add the new variables and their corresponding productions from tempList to cfgMap
    cfgMap.putAll(tempList);

    deleteDuplicatedRow(cfgMap);
    
    // Print cfgMap to the console
    printMap();
  }

  private void breakVariableLongerThanTwo() {
    System.out.println("Break variable strings longer than 2 ");

    // Iterate over the entries in the map
    for (int l = 0; l < cfgMap.size(); l++) {
      // Obtain an iterator for the map entries
      Iterator itr = cfgMap.entrySet().iterator();

      // Create a list to store the keys of map entries with single element ArrayList values
      ArrayList < String > keyList = new ArrayList < > ();

      // Iterate over the map entries to find keys with single element ArrayList values
      while (itr.hasNext()) {
        Map.Entry entry = (Map.Entry) itr.next();
        ArrayList < String > productionRow = (ArrayList < String > ) entry.getValue();

        // If the value associated with the current key is a single element ArrayList, add the key to keyList
        if (productionRow.size() < 2) {
          keyList.add(entry.getKey().toString());
        }
      }

      // Iterate over the map entries again
      for (Map.Entry entry: cfgMap.entrySet()) {
        ArrayList < String > productionList = (ArrayList < String > ) entry.getValue();

        // If the value associated with the current key is a multi-element ArrayList
        if (productionList.size() > 1) {
          // Iterate over the elements in the ArrayList
          for (int i = 0; i < productionList.size(); i++) {
            String temp = productionList.get(i);

            // Iterate over the characters in the current element
            for (int j = 0; j < temp.length(); j++) {
              // If the element has a length greater than two
              if (temp.length() > 2) {
                // Create substrings of the element
                String stringToBeReplaced1 = temp.substring(j, temp.length());
                String stringToBeReplaced2 = temp.substring(0, temp.length() - j);

                // Iterate over the keys in keyList
                for (String key: keyList) {
                  // Obtain the value associated with the current key
                  String value = cfgMap.get(key).get(0);

                  // If either of the substrings matches the value, replace it with the key in the element
                  if (stringToBeReplaced1.equals(value)) {
                    temp = temp.replace(stringToBeReplaced1, key);
                  } else if (stringToBeReplaced2.equals(value)) {
                    temp = temp.replace(stringToBeReplaced2, key);
                  }
                }
              }
              // If the element has a length of two
              else if (temp.length() == 2) {
                // Iterate over the keys in keyList
                for (String key: keyList) {
                  // Obtain the value associated with the current key
                  String value = cfgMap.get(key).get(0);

                  // Iterate over the characters in the element
                  for (int pos = 0; pos < temp.length(); pos++) {
                    String tempChar = Character.toString(temp.charAt(pos));

                    // If the current character in the element matches the value, replace it with the key in the element
                    if (value.equals(tempChar)) {
                      temp = temp.replace(tempChar, key);
                    }
                  }
                }
              }
            }

            // Remove the original element from the ArrayList
            cfgMap.get(entry.getKey().toString()).remove(i);

            // If the modified element is not already in the ArrayList, add it
            if (!cfgMap.get(entry.getKey().toString()).contains(temp)) {
              cfgMap.get(entry.getKey().toString()).add(i, temp);
            }
          }
        }
        // If the value associated with the current key is a single element ArrayList
        else if (productionList.size() == 1) {
          // Iterate over the elements in the ArrayList
          for (int i = 0; i < productionList.size(); i++) {
            String temp = productionList.get(i);

            // If the element has a length of two
            if (temp.length() == 2) {
              // Iterate over the keys in keyList
              for (String key: keyList) {
                // Obtain the value associated with the current key
                String value = cfgMap.get(key).get(0);

                // Iterate over the characters in the element
                for (int pos = 0; pos < temp.length(); pos++) {
                  String tempChar = Character.toString(temp.charAt(pos));

                  // If the current character in the element matches the value, replace it with the key in the element
                  if (value.equals(tempChar)) {
                    temp = temp.replace(tempChar, key);
                  }
                }
              }

              // Remove the original element from the ArrayList
              cfgMap.get(entry.getKey().toString()).remove(i);

              // If the modified element is not already in the ArrayList, add it
              if (!cfgMap.get(entry.getKey().toString()).contains(temp)) {
                cfgMap.get(entry.getKey().toString()).add(i, temp);
              }
            }
          }
        }
      }
    }
    printMap();
  }
}