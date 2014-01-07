package ch.hearc.android.sucle;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.content.res.Resources;

public final class WebServicesInfo {

	public static final String BASE_URL = "https://sucle-diego999.rhcloud.com";
	public static final String URL_GET_MESSAGE = BASE_URL + "/" + "getmsg.php"; // with GET parameters
	public static final String URL_LOGIN = BASE_URL + "/" + "login.php";
	public static final String URL_SEND_MESSAGE = BASE_URL + "/" + "sendmsg.php";
	
	public static final Set<String> MIME_IMAGE = new HashSet<String>(Arrays.asList(new String[]{"image/jpeg", "image/png", "image/gif", "image/jpg"}));
	public static final Set<String> MIME_AUDIO = new HashSet<String>(Arrays.asList(new String[]{"audio/mp3", "audio/3gp", "audio/mp4", "audio/m4a", "audio/ogg", "audio/wav"}));
	public static final Set<String> MIME_VIDEO = new HashSet<String>(Arrays.asList(new String[]{"video/mp4", "video/mov", "video/m4v", "video/webm", "video/3gpp"}));
	
	public static class JSONKey
	{
		public static final Map<Integer, String> ERROR_MAP;
		static
		{	
			Map<Integer, String> map = new HashMap<Integer, String>();
			String[] messages = Sucle.getAppContext().getResources().getStringArray(R.array.error_messages);
			int[] codes = Sucle.getAppContext().getResources().getIntArray(R.array.error_codes);
			
			assert(messages.length == codes.length);
			
			for(int i = 0; i < messages.length; ++i)
				map.put(codes[i], messages[i]);
			
			ERROR_MAP = Collections.unmodifiableMap(map);
		}
		
		public static final String STATUS = "status";
		public static final String STATUS_VALID = "OK";
		public static final String STATUS_NOT_VALID = "KO";
		public static final String ERROR_CODE = "error_code";
		public static final String MESSAGES = "messages";
				
		// Message
		public static final String MESSAGE_ID = "id";
		public static final String MESSAGE_USER = "user";
		public static final String MESSAGE_DEVICE = "device";
		public static final String MESSAGE_PARENT = "parent";
		public static final String MESSAGE_LAT = "lat";
		public static final String MESSAGE_LONG = "lon";
		public static final String MESSAGE_DATETIME = "datetime";
		public static final String MESSAGE_MESSAGE = "message";
		public static final String MESSAGE_MIME = "mime";
		public static final String MESSAGE_FILE ="file";
		
		// User
		public static final String USER_ID = "id";
		public static final String USER_INSCRIPTION = "inscription";
		public static final String USER_SOCIAL_ID = "social_id";
		public static final String USER_TYPE = "type";
		
		public static final String USER_TYPE_FACEBOOK = "FB";
		public static final String USER_TYPE_GOOGLEPLUS = "GP";
		
		// Device
		public static final String DEVICE_ID = "id";
		public static final String DEVICE_DEVICE_ID = "device_id";
		public static final String DEVICE_TYPE = "type";
		public static final String DEVICE_OS = "os_version";
		
	}
	
	private WebServicesInfo(){}
	
	public static String parseContent(InputStream content)
	{
		String jsonText = null;
		try 
		{
			InputStreamReader isr = new InputStreamReader(content, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			
			StringBuilder sb = new StringBuilder();
			String line = null;
			while((line = br.readLine()) != null)
				sb.append(line + "\n");
			jsonText = sb.toString();
		
			br.close();
			isr.close();
			
			return jsonText;
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return jsonText;
	}
}
