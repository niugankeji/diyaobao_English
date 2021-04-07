package com.leman.diyaobao;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/*
 * 
 * @author Roboman-Zhengxin Cheng
 *
 */
public class HttpConnection {
	
	//public static final String httpURL = "http://101.200.209.105/remote/";						//121.42.171.224
	//public static final String httpURL = "http://172.16.190.100:82/remote/";
	public static final String httpURL = "http://www.eos.bnu.edu.cn:8080/remote/";
	public static String HttpGetAction(String url, Map<String, String> Info, String encode) {
		if (Info == null) {
			return null;
		}
		String HttpUrl = url;
		boolean First = true;
		for (Map.Entry<String, String> entry : Info.entrySet()) {
			if (First) {
				HttpUrl += "?"+entry.getKey()+"="+entry.getValue();
				First = false;
			}else {
				HttpUrl += "&"+entry.getKey()+"="+entry.getValue();
			}
		}
		return HttpGetAction(HttpUrl, encode);
	}



	public static String HttpGetAction(String HttpUrl, String encode) {
		if (HttpUrl == null) {
			return null;
		}

		// HttpGet连接对象
		HttpGet httpRequest = new HttpGet(HttpUrl);
		// 取得HttpClient对象
		HttpClient httpclient = new DefaultHttpClient();

		//设置超时时间
		HttpParams params = httpclient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 6000);
		HttpConnectionParams.setSoTimeout(params, 6000);

		try {
			// 请求HttpClient，取得HttpResponse
			HttpResponse httpResponse = httpclient.execute(httpRequest);			
			int ResponseCode = httpResponse.getStatusLine().getStatusCode();			
			if (ResponseCode == HttpStatus.SC_OK) 
			{
				//取得返回的字符串
				return EntityUtils.toString(httpResponse.getEntity(), encode);
			}else {
				return "Error code:" + ResponseCode;
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	//用apache接口实现http的post提交数据
	public static String HttpPostAction(String url, String xmlStr, String encode) { //encode = "utf-8"
		try {
			// 实现将请求的参数封装到表单中，即请求体中
			StringEntity  entity = new StringEntity(xmlStr);	    	
			entity.setContentType("application/xml; charset=UTF-8");
			// 使用post方式提交数据
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(entity);
			// 执行post请求，并获取服务器端的响应HttpResponse
			HttpClient httpclient = new DefaultHttpClient();
			//DefaultHttpClient httpclient = new DefaultHttpClient();

			// 设置超时时间
			HttpParams params = httpclient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 5000);
			HttpConnectionParams.setSoTimeout(params, 20000);

			HttpResponse httpResponse = httpclient.execute(httpPost);

			// 获取服务器端返回的状态码和输入流，将输入流转换成字符串
			int ResponseCode = httpResponse.getStatusLine().getStatusCode();
			if (ResponseCode == HttpStatus.SC_OK) {
				return EntityUtils.toString(httpResponse.getEntity(), encode);
			} else {
				return "Error code:" + ResponseCode;
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	//用apache接口实现http的post提交数据
	public static String HttpPostAction(String url, Map<String, String> Info, String encode) { //encode = "utf-8"
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		if (Info != null && !Info.isEmpty()) {
			for (Map.Entry<String, String> entry : Info.entrySet())  {
				list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));	        
			}
		}else {
			return null;
		}

		try  {
			// 实现将请求的参数封装到表单中，即请求体中
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, encode);
			// 使用post方式提交数据
			HttpPost httpPost = new HttpPost(url);

			httpPost.setEntity(entity);
			// 执行post请求，并获取服务器端的响应HttpResponse
			DefaultHttpClient client = new DefaultHttpClient();

			// 设置超时时间
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 3000);
			HttpConnectionParams.setSoTimeout(params, 5000);

			HttpResponse httpResponse = client.execute(httpPost);

			// 获取服务器端返回的状态码和输入流，将输入流转换成字符串
			int ResponseCode = httpResponse.getStatusLine().getStatusCode();
			if (ResponseCode == HttpStatus.SC_OK) {
				return EntityUtils.toString(httpResponse.getEntity(), encode);
			} else {
				return "Error code:" + ResponseCode;
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 *把从输入流InputStream按指定编码格式encode变成字符串String
	 *
	 * InputStream inputStream = httpResponse.getEntity().getContent();
	 * return InputStream2Str(inputStream, encode);
	 */
	public static String InputStream2Str(InputStream inputStream, String encode)  {
		String result = null;
		if (inputStream != null) {
			// ByteArrayOutputStream 一般叫做内存流
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			byte[] data = new byte[1024];
			int len = 0;
			try {
				while ((len = inputStream.read(data)) != -1) {
					byteArrayOutputStream.write(data, 0, len);
				}
				result = new String(byteArrayOutputStream.toByteArray(), encode);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}


	//将base64打包的String字符串解码成Bitmap图片
	public static Bitmap StringtoBitmap(String str){
		if(str == null) {
			return null;
		}
		//将字符串转换成Bitmap类型
		Bitmap bitmap = null;
		try {
			byte[] bitmapArray;
			bitmapArray = Base64.decode(str, Base64.DEFAULT);
			bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	//将Bitmap图片编码成JPEG并打包成base64格式的String字符串
	public static String BitmaptoString(Bitmap bitmap){
		if (bitmap == null) {
			return null;
		}
		//将Bitmap转换成字符串
		String str = null;
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 90, bStream);
		byte[] bytes = bStream.toByteArray();
		str = Base64.encodeToString(bytes, Base64.DEFAULT);
		return str;
	}
	
	//以指定的大小读取图片，且对于大图同样合适，size可以认为是宽和高的最大值
	public static Bitmap getThumbnail(String FilePath, int size){
		BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
		onlyBoundsOptions.inJustDecodeBounds = true;
		onlyBoundsOptions.inDither = true;								//optional
//		onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;	//optional
		BitmapFactory.decodeFile(FilePath, onlyBoundsOptions);
		
		if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
			return null;
		int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;
		double ratio = (originalSize > size) ? (((double)originalSize)/size) : 1.0;
		int mSize = (int) Math.floor(ratio);			//Integer.highestOneBit((int)Math.floor(ratio));
		if(mSize==0)
			mSize = 1;
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = mSize;
		bitmapOptions.inDither = true;							//optional
//		bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
		Bitmap bitmap = BitmapFactory.decodeFile(FilePath, bitmapOptions);

		return bitmap;
	}



	//以指定的大小读取图片，且对于大图同样合适，size可以认为是宽和高的最大值
	public static Bitmap getThumbnail(ContentResolver cr, Uri uri, int size) throws FileNotFoundException, IOException {
		//ContentResolver resolver = getContentResolver();
		InputStream input = cr.openInputStream(uri);
		BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
		onlyBoundsOptions.inJustDecodeBounds = true;
		onlyBoundsOptions.inDither = true;							//optional
		onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
		BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
		input.close();
		if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
			return null;
		int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;
		double ratio = (originalSize > size) ? (originalSize / size) : 1.0;
		int mSize = Integer.highestOneBit((int) Math.floor(ratio));
		if(mSize==0)
			mSize = 1;
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = mSize;
		bitmapOptions.inDither=true;							//optional
		bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
		input = cr.openInputStream(uri);
		Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
		input.close();

		return bitmap;
	}

	//以指定的大小读取图片，且对于大图同样合适，size可以认为是宽和高的最大值
	public static Bitmap getThumbnail(Bitmap bitmap, int size) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());

		BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
		onlyBoundsOptions.inJustDecodeBounds = true;
		onlyBoundsOptions.inDither = true;							//optional
		onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
		BitmapFactory.decodeStream(is, null, onlyBoundsOptions);

		if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
			return null;
		int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;
		double ratio = (originalSize > size) ? (originalSize / size) : 1.0;
		int mSize = Integer.highestOneBit((int) Math.floor(ratio));
		if(mSize==0)
			mSize = 1;

		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = mSize;
		bitmapOptions.inDither = true;							//optional
		bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
		is = new ByteArrayInputStream(baos.toByteArray());
		Bitmap newBitmap = BitmapFactory.decodeStream(is, null, bitmapOptions);

		return newBitmap;
	}


	public static String saveBitmap(Bitmap mBitmap, String filePath)
	{
		String[] dirs = filePath.split("/");
		String fileDir = Environment.getExternalStorageDirectory().getPath();
		for(int i=0; i<dirs.length-1; i++){
			fileDir += "/" + dirs[i];
			File file = new File(fileDir);
			if (!file.exists()) {
				file.mkdir();
		        if (!file.exists()) {
		        	return null;
				}
			}
		}
		String fileName = fileDir + "/" + dirs[dirs.length-1];
		//图像文件
        File imgFile = new File(fileName);
        if (imgFile.exists()) {
			imgFile.delete();
		}
        try {
			FileOutputStream out = new FileOutputStream(imgFile);
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 92, out);
			out.flush();
			out.close();
			return fileName;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
	}


	public static String getAndroidUUID() {
		return java.util.UUID.randomUUID().toString().substring(0, 30);
		//s.substring(0,8)+s.substring(9,13)+s.substring(14,18)+s.substring(19,23)+s.substring(24);
	}

}




