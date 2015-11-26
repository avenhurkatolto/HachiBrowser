package com.geryon.InfChanHachi.model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class AttachmentEmbedLayer {
	private String embed;

	public AttachmentEmbedLayer(){
		embed = "";
	}

	public String getEmbed() {
		return embed;
	}

	public void setEmbed(String embed) {
		this.embed = embed;
	}

	public static AttachmentModel createAttachment(String EmbedStr){
		Document doc = Jsoup.parse(EmbedStr);
		/*System.out.println(doc.select("img").attr("src"));
		System.out.println(doc.select("a").attr("href"));*/
		AttachmentModel attach = new AttachmentModel("http:"+doc.select("img").attr("src"), doc.select("a").attr("href"), "");
		return attach;
		
		
	}

}

