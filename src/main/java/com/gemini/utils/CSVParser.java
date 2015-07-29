package com.gemini.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSVParser {

	private static final Logger l = LoggerFactory.getLogger(CSVParser.class);
	private static ConcurrentHashMap<String, String> directory = 
			new ConcurrentHashMap<String, String>();
	
	public ConcurrentHashMap<String, String> run() {
		 
		String csvFile = "../../../people.csv";
		BufferedReader br = null;
		String line = "";
		String csvSplitBy = ",";
	 
		try {
			br = new BufferedReader(new FileReader(getClass().getResource(csvFile).getPath()));
			while ((line = br.readLine()) != null) {
				directory.put(line.split(csvSplitBy)[0], line.split(csvSplitBy)[1]);
			}
			
			l.debug("Successfully parsed file");
			return directory;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	  }
	
	public static void main(String[] args) {
		CSVParser parser = new CSVParser();
		parser.run();
	}
}
