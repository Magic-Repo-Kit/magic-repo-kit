package com.magicrepokit.oss.model;

import lombok.Data;

/**
 * MRKFile
 * 文件上传返回结果集
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
@Data
public class MRKFile {
	/**
	 * 文件地址
	 */
	private String link;
	/**
	 * 域名地址
	 */
	private String domain;
	/**
	 * 文件名
	 */
	private String name;
	/**
	 * 初始文件名
	 */
	private String originalName;
	/**
	 * 附件表ID
	 */
	private Long attachId;
}
