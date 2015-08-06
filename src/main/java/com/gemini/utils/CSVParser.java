package com.gemini.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CSVParser {

	private static final Logger l = LoggerFactory.getLogger(CSVParser.class);
	private @Value("${csvFile}") String csvFile;
	private @Value("${csvSeparator}") String csvSeparator;
	private static JSONArray data = new JSONArray();
	
	public void parse() {
		
		BufferedReader br = null;
		String line = "";
	 
		try {
			l.debug("CSV File to be read: {}", csvFile);
			br = new BufferedReader(
					new InputStreamReader(
							CSVParser.class.getClassLoader().getResourceAsStream(csvFile)
							)
					);
			while ((line = br.readLine()) != null) {
				JSONObject entry = new JSONObject();
				entry.put("eid", line.split(csvSeparator)[0]);
				entry.put("name", line.split(csvSeparator)[1]);
				entry.put("phone", line.split(csvSeparator)[2]);
				entry.put("status", Integer.parseInt(line.split(csvSeparator)[3]));
				data.put(entry);
			}
			
			l.debug("Successfully parsed file");
			
		} catch (FileNotFoundException e) {
			l.error("Error in parsing: {}", new Object[] {e.getStackTrace()});
		} catch (IOException e) {
			l.error("Error in parsing: {}", new Object[] {e.getStackTrace()});
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					l.error("Error in closing resource: {}", 
							new Object[] {e.getStackTrace()});
				}
			}
		}
		
	  }
	
	public static JSONArray getData(){
		return data;
	}
	
	public static void main(String[] args) {
		CSVParser parser = new CSVParser();
		parser.parse();
	}
}
