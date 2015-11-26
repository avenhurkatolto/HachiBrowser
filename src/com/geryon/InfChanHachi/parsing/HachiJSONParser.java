package com.geryon.InfChanHachi.parsing;

import java.io.IOException;
import java.util.ArrayList;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geryon.InfChanHachi.model.AttachmentEmbedLayer;
import com.geryon.InfChanHachi.model.AttachmentModel;
import com.geryon.InfChanHachi.model.BoardModel;
import com.geryon.InfChanHachi.model.CatalogModel;
import com.geryon.InfChanHachi.model.CatalogThreadModel;
import com.geryon.InfChanHachi.model.MessageModel;
import com.geryon.InfChanHachi.model.ThreadModel;

public class HachiJSONParser {

	public static ArrayList<BoardModel> getBoardsInfo(String inStr) throws JsonParseException, JsonMappingException, IOException{


		ObjectMapper mMapper = new ObjectMapper();
		mMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		ArrayList<BoardModel> outBoardList = mMapper.readValue(inStr, new TypeReference<ArrayList<BoardModel>>(){});

		return outBoardList;
	}

	public static CatalogModel getCatalogModel(String inStr, String url) throws JsonProcessingException, IOException{
		ObjectMapper mMapper = new ObjectMapper();
		
		mMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		JsonNode rootNode = mMapper.readTree(inStr);

		System.out.println(rootNode.get(0).path("threads").isArray());
		
		CatalogModel cataModel = new CatalogModel(url);
		ArrayList<CatalogThreadModel> catalogThreadList = new ArrayList<CatalogThreadModel>();
		
		for (int i = 0; i<rootNode.size();i++){
		ArrayList<CatalogThreadModel> tempThreads = mMapper.readValue(rootNode.get(i).path("threads").traverse(), new TypeReference<ArrayList<CatalogThreadModel>>(){}) ;
		ArrayList<AttachmentModel> tempAttachs = mMapper.readValue(rootNode.get(i).path("threads").traverse(), new TypeReference<ArrayList<AttachmentModel>>(){});
		ArrayList<AttachmentModel> videoAttachs = mMapper.readValue(rootNode.get(i).path("threads").traverse() , new TypeReference<ArrayList<AttachmentModel>>(){});
		mergeAttachments(tempThreads, tempAttachs);
		catalogThreadList.addAll(tempThreads);
		}
		cataModel.setThreadList(catalogThreadList);

		return cataModel;
		
	} 
	

	public static ThreadModel getThreadModel(String inStr, String inUrl, String inTitle, String inBoard) throws JsonProcessingException, IOException{
		//String title = inStr.substring(inStr.indexOf("<title>")+6, inStr.indexOf("</title>"));
		

		ObjectMapper mMapper = new ObjectMapper();
		mMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		JsonNode postsNode = mMapper.readTree(inStr).path("posts");
		ThreadModel thread = new ThreadModel(inUrl, inTitle, inBoard);
		ArrayList<MessageModel> messages = new ArrayList<MessageModel>();

		for (int i = 0; i<postsNode.size();i++){
			System.out.println(i);
			JsonNode tempNode = postsNode.get(i);

			MessageModel tempMessage = mMapper.readValue(tempNode.traverse(), MessageModel.class);

			if (tempNode.has("filename")){
				AttachmentModel tempAttach = mMapper.readValue(tempNode.traverse(), AttachmentModel.class);
				tempMessage.setAttachment(tempAttach);
			}
			if (tempNode.has("extra_files")){
				System.out.println("isarray:"+tempNode.path("extra_files").isArray());
				System.out.println("extra_size: "+tempNode.path("extra_files").size());
				ArrayList<AttachmentModel> tempAttachs = mMapper.readValue(tempNode.path("extra_files").traverse(), new TypeReference<ArrayList<AttachmentModel>>(){});
				tempMessage.setAttachments(tempAttachs);
			}
			if (tempNode.has("embed")){
				System.out.println("Embed! "+tempNode.get("embed"));
				tempMessage.setAttachment(AttachmentEmbedLayer.createAttachment(tempNode.get("embed").asText()));				
			}
			messages.add(tempMessage);

		}
		thread.addMessagesBatched(messages);
		return thread;
	}

	private static ArrayList<CatalogThreadModel> mergeAttachments(ArrayList<CatalogThreadModel> tempThreads,
			ArrayList<AttachmentModel> tempAttachs) {
		
		if (tempThreads.size() == tempAttachs.size()){
		for (int i = 0; i<tempThreads.size(); i++){
			tempThreads.get(i).setAttachment(tempAttachs.get(i));
			
		}
		} else {
			Log.w("Parser", "Attachmentsize different: \n modelsize:"+String.valueOf(tempThreads.size())+"\n attachmentSize:"+String.valueOf(tempAttachs.size()));
		}
		return tempThreads;
	}	


}