/*
 * A program to make a database of sanctuaries with animals and their proper diets
 * Author: Naomi Braun
 * October 13, 2017
 * Java, Project 2

 */

import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;

public class project2 {	
	
	public static void main(String[] args) {
		ArrayList<Sanctuary> shelters = new ArrayList<Sanctuary>();
		File f = null;
		boolean created = false;
		
		//Import sanctuary info from file
		System.out.println("Loading from file..........");
		try {
			f = new File("sanctuaries.txt");
			created = f.createNewFile();
			if(!created) {
				//read arrays from file
				Scanner input = new Scanner(f);
				int sheltFill = 0;
				String sanct = "";
				while (input.hasNext()) {
					System.out.println("Step 1");
					//Read sanctuary
					sanct = input.nextLine();
					System.out.println(sanct);
					Sanctuary shelter = new Sanctuary(sanct);
					shelters.set(sheltFill, shelter);
					
					//Fill animals until told to go to next sanctuary
					String animalLine = input.nextLine();
					int animIn = 0;
					while (!animalLine.equals("next")) {
						System.out.println("Step 2");
						shelter.animals.add(animalLine);						
						//Fill food
						String foodLine = input.nextLine();
						String[] foods = foodLine.split(",\\s*");
						for (int i=0; i < foods.length; i++) {
							System.out.println("Step 3");
							shelter.animalDB[animIn][i] = foods[i];
						}
						animIn++;
						animalLine = input.nextLine();
					}
				}
				input.close();
			}
			else {
				System.out.println("You have no information saved.");
			}		
		}
		catch (Exception ex) {
			System.out.println("There was an error reading/creating your input file.");
		}
		
		int sheltChoice = 0;
		Scanner scan = new Scanner(System.in);
		//User selects desired task
		while (sheltChoice != 3) {
			System.out.println("\n1. Add a shelter");
			System.out.println("2. View animals at or edit an existing shelter");
			System.out.println("3. Exit");
			System.out.print("Write the number beside what you would like to do: ");
			sheltChoice = scan.nextInt();
			scan.nextLine();
			
			//New Shelter
			if (sheltChoice == 1) {
				System.out.println("=== Adding a Shelter ===");
				System.out.print("Enter the name of the shelter: ");
				String shelt = scan.nextLine();
				int newLoc = searchSanctuaries(shelters, shelt);
				if (newLoc == -1) {
					Sanctuary shelter = new Sanctuary(shelt);
					shelters.add(shelter);
					System.out.println("Shelter added.");
				}
				else {
					System.out.println("You have already added this shelter.");
				}
							
			}
			//Search existing shelter
			else if (sheltChoice == 2) {
				if (shelters.isEmpty()) {
					System.out.println("You don't seem to have any sanctuaries added. Please add some.");
					continue;
				}
				else {
					System.out.print("Enter the shelter you are searching for: ");
					String shelt = scan.nextLine();
					
					int sheltFind = searchSanctuaries(shelters, shelt);
					if (sheltFind == -1) {
						System.out.println("This shelter doesn't exist in the database yet. Select 1 at the menu to add it.");
					}
					else {
						int aniChoice = 0;
						Sanctuary workingShelt = shelters.get(sheltFind);
						while(aniChoice != 3) {							
							System.out.println("\n1. Add an animal");
							System.out.println("2. View foods an existing animal can eat");
							System.out.println("3. Change shelter");
							System.out.print("Write the number beside what you would like to do: ");
							aniChoice = scan.nextInt();
							scan.nextLine();
							
							//Enter Data
							if (aniChoice == 1) {
								String newAni = "";
								char moreAni = 'y';
								//Add a new animal
								while (moreAni == 'y' || moreAni == 'Y') {
									System.out.println("=== Adding an Animal ===");
									System.out.print("Enter the name of the animal: ");
									newAni = scan.next();
									
									workingShelt.animals.add(newAni);
									
									//Enter food for animal
									System.out.println("=== Adding Foods ===");
									workingShelt.addFoods(newAni);
									
									workingShelt.aniIndex++;
									
									//Only allow user to choose no if there are ten animals
									if (workingShelt.aniIndex >= 2) {
										System.out.print("Would you like to add another animal (y/n): ");
										moreAni = scan.next().charAt(0);
									}
									
									//If there are 100 animals, cut the user off
									if (workingShelt.aniIndex >= 100) {
										System.out.println("You have reached the max number of animals for this shelter.");
										moreAni = 'n';
									}
								}
							}		
							
							//Retrieve Data
							else if (aniChoice == 2) {
								if(workingShelt.animals.isEmpty()) {
									System.out.println("You don't seem to have any animals added. Please add some.");
									continue;
								}
								else {									
									//Ask for animal									
									System.out.print("Enter the animal you are searching for: ");
									String aniTarget = scan.nextLine();
									
									//Match input
									int indTarget = workingShelt.findAniIndex(aniTarget);
									
									if (indTarget == -1) {
										System.out.println("It seems this animal does not exist yet. Press 1 at the menu to add it.");
									}
									else {
										//Display foods
										workingShelt.printFoods(aniTarget, indTarget);
									}
									
								}
							}	
							
							//Stop working on current sanctuary
							else {
								break;
							}
						}						
					}
				}				
			}			
			else {
				//Save information to file
				try {
					PrintWriter output = new PrintWriter(f);
					//Print sanctuary name
					for (Sanctuary s : shelters) {
						output.println(s.name);
						//Print animals
						int aniCount = 0;
						for(String a : s.animals) {
							output.println(a);
							//Print foods
							for (int c=0; c<20; c++) {
								if (s.animalDB[aniCount][c] != null) {
									output.print(s.animalDB[aniCount][c] + ", ");
								}
							}
							output.println("");
							aniCount++;
						}
						//Print next to move on to next sanctuary
						output.println("next");
					}
					output.close();
				}
				catch (Exception ex) {
					System.out.println("Something went wrong saving your data.");
				}
				
				//Exit program
				System.exit(0);
			}
			
			
		}
		scan.close();
		
	}
	
	public static int searchSanctuaries(ArrayList<Sanctuary> shelters, String target) {
		System.out.println("Searching for " + target + "...");
		int match = -1;
		int index = 0;
		
		for (Sanctuary s : shelters) {
			if (s.name.equals(target)) {			
				match = index;
				System.out.println("Shelter found.");
			}
			index++;
		}

		return match;
	}
}

class Sanctuary {
	int aniIndex = 0;
	String name = "";
	ArrayList<String> animals = new ArrayList<String>();
	String[][] animalDB = new String[100][20];		
	
	Sanctuary(String title) {
		name = title;
	}	
	
	public void addFoods(String animal) {
		Scanner in = new Scanner(System.in);
		String meal = "";
		
		for(int i=0; i<20; i++) {
			System.out.print("Enter a food that a(n) " + animal + " can eat (leave blank to move on): ");
			meal = in.nextLine();	

			if (!meal.isEmpty()) {
				animalDB[aniIndex][i] = meal;				
			}
			else {
				break;
			}
			
			if (i == 19) {
				System.out.println("You have reached the max number of foods.");
			}
		}
	}
	
	public int findAniIndex(String target) {
		int targIndex = -1;
		
		for(int i=0; i<animals.size(); i++) {
			System.out.println(i);
			if (target.equals(animals.get(i))) {
				targIndex = i;
			}
		}
		return targIndex;
	}
	
	public void printFoods(String animal, int index) {
		System.out.println("Food for " + animal + ": ");
		for (int c=0; c<20; c++) {
			if (animalDB[index][c] != null)
				System.out.println(animalDB[index][c]);
		}
	}
}
