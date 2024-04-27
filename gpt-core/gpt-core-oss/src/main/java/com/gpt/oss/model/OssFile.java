package com.gpt.oss.model;

import lombok.Data;

import java.util.Date;

/**
 * oss文件实体类
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
@Data
public class OssFile {
	/**
	 * 文件地址
	 */
	private String link;
	/**
	 * 文件名
	 */
	private String name;
	/**
	 * 文件hash值
	 */
	public String hash;
	/**
	 * 文件大小
	 */
	private long length;
	/**
	 * 文件上传时间
	 */
	private Date putTime;
	/**
	 * 文件contentType
	 */
	private String contentType;
}
