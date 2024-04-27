
package com.magicrepokit.oss.prop;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 对象存储配置类
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "oss")
@Component
public class OssProperties {

	/**
	 * 是否启用
	 */
	private Boolean enabled=false;

	/**
	 * 对象存储服务的URL
	 */
	private String endpoint="";

	/**
	 * 区域简称 TencentCOS/Amazon S3 需要
	 */
	private String region="";

	/**
	 * Access key就像用户ID，可以唯一标识你的账户
	 */
	private String accessKey="";

	/**
	 * Secret key是你账户的密码
	 */
	private String secretKey="";

	/**
	 * 默认的存储桶名称
	 */
	private String bucketName = "default";

}
