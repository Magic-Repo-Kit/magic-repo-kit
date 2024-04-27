package com.magicrepokit.oss;


import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.magicrepokit.oss.model.GPTFile;
import com.magicrepokit.oss.model.OssFile;
import com.magicrepokit.oss.prop.OssProperties;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * 七牛云存储操作模板
 * @author AuroraPixel
 * @github https://github.com/AuroraPixel
 */
@AllArgsConstructor
@Slf4j
public class QiniuTemplate implements OssTemplate {
	private final Auth auth;
	private final UploadManager uploadManager;
	private final BucketManager bucketManager;
	private final OssProperties ossProperties;

	@Override
	@SneakyThrows
	public void makeBucket(String bucketName) {
		if (!ArrayUtil.contains(bucketManager.buckets(), getBucketName(bucketName))) {
			bucketManager.createBucket(getBucketName(bucketName), Zone.autoZone().getRegion());
		}
	}

	@Override
	@SneakyThrows
	public void removeBucket(String bucketName) {

	}

	@Override
	@SneakyThrows
	public boolean bucketExists(String bucketName) {
		return ArrayUtil.contains(bucketManager.buckets(), getBucketName(bucketName));
	}

	@Override
	@SneakyThrows
	public void copyFile(String bucketName, String fileName, String destBucketName) {
		bucketManager.copy(getBucketName(bucketName), fileName, getBucketName(destBucketName), fileName);
	}

	@Override
	@SneakyThrows
	public void copyFile(String bucketName, String fileName, String destBucketName, String destFileName) {
		bucketManager.copy(getBucketName(bucketName), fileName, getBucketName(destBucketName), destFileName);
	}

	@Override
	@SneakyThrows
	public OssFile statFile(String fileName) {
		return statFile(ossProperties.getBucketName(), fileName);
	}

	@Override
	@SneakyThrows
	public OssFile statFile(String bucketName, String fileName) {
		FileInfo stat = bucketManager.stat(getBucketName(bucketName), fileName);
		OssFile ossFile = new OssFile();
		ossFile.setName(ObjectUtil.isEmpty(stat.key) ? fileName : stat.key);
		String downloadUrl = auth.privateDownloadUrl(fileLink(fileName),3600*10);
		ossFile.setLink(downloadUrl);
		ossFile.setHash(stat.hash);
		ossFile.setLength(stat.fsize);
		ossFile.setPutTime(new Date(stat.putTime / 10000));
		ossFile.setContentType(stat.mimeType);
		return ossFile;
	}

	@Override
	@SneakyThrows
	public String filePath(String fileName) {
		return getBucketName().concat("/").concat(fileName);
	}

	@Override
	@SneakyThrows
	public String filePath(String bucketName, String fileName) {
		return getBucketName(bucketName).concat("/").concat(fileName);
	}

	@Override
	@SneakyThrows
	public String fileLink(String fileName) {
		return "http://"+ossProperties.getEndpoint().concat("/").concat(fileName);
	}

	@Override
	@SneakyThrows
	public String fileLink(String bucketName, String fileName) {
		return ossProperties.getEndpoint().concat("/").concat(fileName);
	}

	@Override
	@SneakyThrows
	public GPTFile putFile(MultipartFile file) {
		return putFile(ossProperties.getBucketName(), file.getOriginalFilename(), file);
	}

	@Override
	@SneakyThrows
	public GPTFile putFile(String fileName, MultipartFile file) {
		return putFile(ossProperties.getBucketName(), fileName, file);
	}

	@Override
	@SneakyThrows
	public GPTFile putFile(String bucketName, String fileName, MultipartFile file) {
		return putFile(bucketName, fileName, file.getInputStream());
	}

	@Override
	@SneakyThrows
	public GPTFile putFile(String fileName, InputStream stream) {
		return putFile(ossProperties.getBucketName(), fileName, stream);
	}

	@Override
	@SneakyThrows
	public GPTFile putFile(String bucketName, String fileName, InputStream stream) {
		return put(bucketName, stream, fileName, false);
	}

	@SneakyThrows
	public GPTFile put(String bucketName, InputStream stream, String key, boolean cover) {
		makeBucket(bucketName);
		String originalName = key;
		key = getFileName(key);
		// 覆盖上传
		if (cover) {
			uploadManager.put(stream, key, getUploadToken(bucketName, key), null, null);
		} else {
			Response response = uploadManager.put(stream, key, getUploadToken(bucketName), null, null);
			int retry = 0;
			int retryCount = 5;
			while (response.needRetry() && retry < retryCount) {
				response = uploadManager.put(stream, key, getUploadToken(bucketName), null, null);
				retry++;
			}
		}
		GPTFile file = new GPTFile();
		file.setOriginalName(originalName);
		file.setName(key);
		file.setDomain(getOssHost());
		file.setLink(fileLink(bucketName, key));
		return file;
	}

	@Override
	@SneakyThrows
	public void removeFile(String fileName) {
		bucketManager.delete(getBucketName(), fileName);
	}

	@Override
	@SneakyThrows
	public void removeFile(String bucketName, String fileName) {
		bucketManager.delete(getBucketName(bucketName), fileName);
	}

	@Override
	@SneakyThrows
	public void removeFiles(List<String> fileNames) {
		fileNames.forEach(this::removeFile);
	}

	@Override
	@SneakyThrows
	public void removeFiles(String bucketName, List<String> fileNames) {
		fileNames.forEach(fileName -> removeFile(getBucketName(bucketName), fileName));
	}

	/**
	 * 获取私有存储文件输入流
	 *
	 * @param fileName 存储桶文件名称
	 * @return InputStream
	 */
	@Override
	public InputStream statFileStream(String fileName) {
		return statFileStream(ossProperties.getBucketName(), fileName);
	}

	/**
	 * 获取文件信息
	 *
	 * @param bucketName 存储桶名称
	 * @param fileName   存储桶文件名称
	 * @return InputStream
	 */
	@Override
	@SneakyThrows
	public InputStream statFileStream(String bucketName, String fileName) {
		String downloadUrl = auth.privateDownloadUrl(fileLink(fileName),3600*10);
		OkHttpClient client = new OkHttpClient();
		Request req = new Request.Builder().url(downloadUrl).build();
		okhttp3.Response resp = null;
		resp = client.newCall(req).execute();
		if (resp.isSuccessful()) {
			ResponseBody body = resp.body();
			return body.byteStream();
		}
		log.info("当前文件下载失败{}请检查文件是否存在或者文件是否为私有文件",fileName);
		return null;
	}

	/**
	 * 根据规则生成存储桶名称规则
	 *
	 * @return String
	 */
	private String getBucketName() {
		return getBucketName(ossProperties.getBucketName());
	}

	/**
	 * 根据规则生成存储桶名称规则
	 *
	 * @param bucketName 存储桶名称
	 * @return String
	 */
	private String getBucketName(String bucketName) {
		return "mrk"+"-"+bucketName;
	}

	/**
	 * 根据规则生成文件名称规则
	 *
	 * @param originalFilename 原始文件名
	 * @return string
	 */
	private String getFileName(String originalFilename) {
		return originalFilename;
	}

	/**
	 * 获取上传凭证，普通上传
	 */
	public String getUploadToken(String bucketName) {
		return auth.uploadToken(getBucketName(bucketName));
	}

	/**
	 * 获取上传凭证，覆盖上传
	 */
	private String getUploadToken(String bucketName, String key) {
		return auth.uploadToken(getBucketName(bucketName), key);
	}

	/**
	 * 获取域名
	 *
	 * @return String
	 */
	public String getOssHost() {
		return ossProperties.getEndpoint();
	}

}
