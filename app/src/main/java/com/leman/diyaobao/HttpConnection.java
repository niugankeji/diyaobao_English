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

		// HttpGet????????????
		HttpGet httpRequest = new HttpGet(HttpUrl);
		// ??????HttpClient??????
		HttpClient httpclient = new DefaultHttpClient();

		//??????????????????
		HttpParams params = httpclient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 6000);
		HttpConnectionParams.setSoTimeout(params, 6000);

		try {
			// ??????HttpClient?????????HttpResponse
			HttpResponse httpResponse = httpclient.execute(httpRequest);			
			int ResponseCode = httpResponse.getStatusLine().getStatusCode();			
			if (ResponseCode == HttpStatus.SC_OK) 
			{
				//????????????????????????
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

	//???apache????????????http???post????????????
	public static String HttpPostAction(String url, String xmlStr, String encode) { //encode = "utf-8"
		try {
			// ????????????????????????????????????????????????????????????
			StringEntity  entity = new StringEntity(xmlStr);	    	
			entity.setContentType("application/xml; charset=UTF-8");
			// ??????post??????????????????
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(entity);
			// ??????post???????????????????????????????????????HttpResponse
			HttpClient httpclient = new DefaultHttpClient();
			//DefaultHttpClient httpclient = new DefaultHttpClient();

			// ??????????????????
			HttpParams params = httpclient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 5000);
			HttpConnectionParams.setSoTimeout(params, 20000);

			HttpResponse httpResponse = httpclient.execute(httpPost);

			// ?????????????????????????????????????????????????????????????????????????????????
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

	//???apache????????????http???post????????????
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
			// ????????????????????????????????????????????????????????????
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, encode);
			// ??????post??????????????????
			HttpPost httpPost = new HttpPost(url);

			httpPost.setEntity(entity);
			// ??????post???????????????????????????????????????HttpResponse
			DefaultHttpClient client = new DefaultHttpClient();

			// ??????????????????
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 3000);
			HttpConnectionParams.setSoTimeout(params, 5000);

			HttpResponse httpResponse = client.execute(httpPost);

			// ?????????????????????????????????????????????????????????????????????????????????
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
	 *???????????????InputStream?????????????????????encode???????????????String
	 *
	 * InputStream inputStream = httpResponse.getEntity().getContent();
	 * return InputStream2Str(inputStream, encode);
	 */
	public static String InputStream2Str(InputStream inputStream, String encode)  {
		String result = null;
		if (inputStream != null) {
			// ByteArrayOutputStream ?????????????????????
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


	//???base64?????????String??????????????????Bitmap??????
	public static Bitmap StringtoBitmap(String str){
		if(str == null) {
			return null;
		}
		//?????????????????????Bitmap??????
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

	//???Bitmap???????????????JPEG????????????base64?????????String?????????
	public static String BitmaptoString(Bitmap bitmap){
		if (bitmap == null) {
			return null;
		}
		//???Bitmap??????????????????
		String str = null;
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 90, bStream);
		byte[] bytes = bStream.toByteArray();
		str = Base64.encodeToString(bytes, Base64.DEFAULT);
		return str;
	}
	
	//???????????????????????????????????????????????????????????????size????????????????????????????????????
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



	//???????????????????????????????????????????????????????????????size????????????????????????????????????
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

	//???????????????????????????????????????????????????????????????size????????????????????????????????????
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
		//????????????
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




