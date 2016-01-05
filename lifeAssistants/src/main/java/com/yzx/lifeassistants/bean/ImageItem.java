package com.yzx.lifeassistants.bean;

import java.io.Serializable;

public class ImageItem implements Serializable {
	/**
	 * @Fields serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 1173456185999622522L;
	private String imageId;
	private String thumbnailPath;
	private String imagePath;
	private Boolean isLocalPic;
	private String fileName;

	public Boolean getIsLocalPic() {
		return isLocalPic;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setIsLocalPic(Boolean isLocalPic) {
		this.isLocalPic = isLocalPic;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getThumbnailPath() {
		return thumbnailPath;
	}

	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

}
