/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package com.magicrepokit.oss.config;

import cn.hutool.core.util.StrUtil;
import com.magicrepokit.oss.QiniuTemplate;
import com.magicrepokit.oss.prop.OssProperties;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Qiniu配置类
 *
 * @author Chill
 */
@AllArgsConstructor
@Configuration
public class QiniuConfiguration {

	private final OssProperties ossProperties;

	/**
	 * 增加七牛云配置region的方式 目前仅支持huadong  huanan  huabei  beimei xinjiapo
	 * 默认不配置采用原有的方式实现 支持配置对应的region
	 */
	@Bean
	public com.qiniu.storage.Configuration qnConfiguration() {
		Region regin = Region.autoRegion();
		if (StrUtil.isNotBlank(ossProperties.getRegion())) {
			switch (ossProperties.getRegion()) {
				case "huadong":
					regin = Region.huadong();
					break;
				case "huabei":
					regin = Region.huabei();
					break;
				case "huanan":
					regin = Region.huanan();
					break;
				case "beimei":
					regin = Region.beimei();
					break;
				case "xinjiapo":
					regin = Region.xinjiapo();
					break;
				default:
					regin = Region.autoRegion();
					break;
			}
		}
		return new com.qiniu.storage.Configuration(regin);
	}

	@Bean
	public Auth auth() {
		if(ossProperties.getEnabled()){
			return Auth.create(ossProperties.getAccessKey(), ossProperties.getSecretKey());
		}
		return null;
	}

	@Bean
	public UploadManager uploadManager(com.qiniu.storage.Configuration cfg) {
		return new UploadManager(cfg);
	}

	@Bean
	public BucketManager bucketManager(com.qiniu.storage.Configuration cfg) {
		if(ossProperties.getEnabled()){
			return new BucketManager(Auth.create(ossProperties.getAccessKey(), ossProperties.getSecretKey()), cfg);
		}
		return null;
	}

	@Bean
	public QiniuTemplate qiniuTemplate(Auth auth, UploadManager uploadManager, BucketManager bucketManager) {
		return new QiniuTemplate(auth, uploadManager, bucketManager, ossProperties);
	}

}
