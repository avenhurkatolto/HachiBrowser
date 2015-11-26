package com.geryon.InfChanHachi.utils;

import java.util.ArrayList;
import java.util.Arrays;

import android.util.Log;


public class Utils {

	public static String const_serveraddress = "http://8ch.net";
	public static String const_mediaserver = "http://8ch.net";
	public static String const_spoiler_global = "http://8ch.net/static/spoiler.jpg";	
	public static String const_postfix_spoiler = "/spoiler.jpg";
	public static String const_pdfimage = "http://8ch.net/static/file.png";


	public static ArrayList<String> strToArrayList (String input){
		ArrayList<String> output = new ArrayList<String>(Arrays.asList(input.split(" +")));
		for (int i = 0; i < output.size(); i++){
			Log.w(String.valueOf(i),output.get(i));

		}
		return output;
	}	
}