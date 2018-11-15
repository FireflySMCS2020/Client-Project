package apfly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import javax.mail.MessagingException;

import com.itextpdf.text.DocumentException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

//Andrew Zhong 5/6/18 Client Project Testing
//Reads MER spreadsheet file and creates PDF
//data expected to be 1 item per line and in the following format: ID,Alpha_name,PD,TCHL,TestName,APExam

public class DataReader {
	
	public static ArrayList<Teacher> readData(String fileAddress) {
		File file = new File(fileAddress);
		Scanner reader;
		ArrayList<String[]> exams = new ArrayList<String[]>();	//stores data as arrays in arraylists
		ArrayList<Teacher> teachList = new ArrayList<Teacher>();
		
		try {								//create file reader
			reader = new Scanner(file);
		}
		catch(FileNotFoundException e){	
			reader = new Scanner("FileNotFound");
		}
		reader.nextLine();
		
		while(reader.hasNextLine()) {	//go through each item
			String line = reader.nextLine();
			line = line.substring(1,line.length() - 1); //trim non-delimiter quotation marks
			String delim = "\",\"";				//      "," delimeter
			exams.add(split(line, delim));		//each exam is an array of ID,Alpha_name,PD,TCHL,TestName,APExam
		}
		
		System.out.println(exams.size());
		
		Collections.sort(exams, (String[] item1, String[] item2) -> item1[3].compareTo(item2[3]));	//sort by teacher
		
		ArrayList<String[]> noTeach = new ArrayList<String[]>();
		
		for(int ind = 0; ind < exams.size(); ind++) {	//move items to corresponding to Teacher object's array
			int lastSame = ind;			
			String name = exams.get(ind)[3];
			while(lastSame < exams.size() && exams.get(lastSame)[3].equals(name)) {	//goes to last item with this teacher
				lastSame++;
			}
			if(name.indexOf("No ") == 0 || name.indexOf("no ") == 0 || name.trim().equals("")){	//if no teacher, add to array for the end
				System.out.println(name);
				noTeach.addAll(new ArrayList<String[]>(exams.subList(ind, lastSame)));
			}
			else{
				teachList.add(new Teacher(new ArrayList<String[]>(exams.subList(ind, lastSame))));	//make new Teacher object
			}
			
			ind = lastSame - 1;
		}
		
		if(noTeach.size() > 0) {
			teachList.add(new Teacher(noTeach));
		}
		Collections.sort(teachList, (Teacher t1, Teacher t2) -> t1.name.compareTo(t2.name));	//sort teachers alphabetically
		
		reader.close();

		return teachList;
	}
	
	public static String[] split(String s, String d) {	//splits s with delimiter d and returns list of strings, string.split() didn't work b/c of consecutive delimiters
		ArrayList<String> data = new ArrayList<String>();
		int i;
		for(i = 0; s.indexOf(d, i + 1) != -1; i = s.indexOf(d, i + 1)) {	//i is start of delimiter and find next instance of d
			String metaData = s.substring(i, s.indexOf(d, i + 1));
			if(metaData.indexOf(d) == 0) {
				metaData = metaData.substring(d.length());
			}
			data.add(metaData);
		}
		data.add(s.substring(i + 3));					//no delimiter at end, have to manually get the rest
		
		String[] output = new String[data.size()];	//move arraylist data to array
		for(i = 0; i < data.size(); i++) {
			output[i] = data.get(i);
		}
		return output;
	}

	public static void main(String[] args) {		//for testing purposes
		ArrayList<Teacher> teachers = readData("test_data.mer");
		try {
		teachers.get(0).makePDF();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		/*//for(Teacher t : teachers) {
			//System.out.println(t.teacherEmail);
			try{
				//System.out.println(t.makePDF(""));
				teachers.get(0).teacherEmail = "fireflySMCS2020@gmail.com";
				teachers.get(0).sendEmail("fireflySMCS2020@gmail.com", "lightningbug", "");
			}
			catch(IOException e) {
				System.out.println("Error creating PDF writer");
				e.printStackTrace();
			}
			catch(DocumentException e) {
				System.out.println("Error creating document");
				e.printStackTrace();
			}
			catch(MessagingException e) {
				System.out.println("Messaging error");
				e.printStackTrace();
			}
		//}	*/
	}

}
