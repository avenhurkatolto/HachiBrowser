package com.geryon.InfChanHachi.model;

import android.util.Log;

import com.geryon.InfChanHachi.utils.Utils;


public class AttachmentModel {

	private String filename;
	private String ext;
	private String tim;

	public AttachmentModel(){
		filename = "";
		ext = "";
		tim = "";
	}

	public AttachmentModel(String filename, String ext, String tim){
		this.filename = filename;
		this.ext = ext;
		this.tim = tim;
	}

	public String getFilename() {
		return filename;
	}

	public String getExt() {
		return ext;
	}

	public String getTim() {
		return tim;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public void setTim(String tim) {
		this.tim = tim;
	}
	public String getFullName(){
		return this.tim+this.ext;
	}

	public String getOriginalFullName(){
		return this.filename+this.ext;
	}

	public String getFullPathImage(String boardName){
		if (this.tim.equals("")){
			return this.ext;
		}else {
			return Utils.const_mediaserver + "/" + boardName + "/src/" + this.tim + this.ext;
		}
	}
	public String getFullPathThumb(String boardName){
		Log.w("thumb", this.filename +" _" + this.ext + "_" + this.tim);
		if (this.tim.equals("")){
			return this.filename;

		}else if (this.ext.equals(".pdf")) {

			return Utils.const_pdfimage;

		}else{
			return Utils.const_mediaserver + "/" + boardName + "/thumb/" + this.tim + ".jpg";
		}
	}

	public String getContentType(){
		if (this.tim.equals("")){
			return "embed";
		
		} else if ((this.ext.equals(".jpg")) || (this.ext.equals(".jpeg")) || (this.ext.equals(".png")) || (this.ext.equals(".gif")) ){
			return "image";
		} else if (this.filename.equals(null)){
			return "null";
		} else {
			return this.ext;
		}

	}


	public String getFormattedTest(){
		return "filename: "+this.filename+this.ext+"\n"	
				+"tim: "+this.tim+"\n\n";

	}
}