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

public final class WebServicesInfo {

	public static final String BASE_URL = "https://sucle-diego999.rhcloud.com";
	public static final String URL_GET_MESSAGE = BASE_URL + "/" + "getmsg.php"; // with GET parameters
	public static final String URL_LOGIN = BASE_URL + "/" + "login.php";
	public static final String URL_SEND_MESSAGE = BASE_URL + "/" + "sendmsg.php";
	
	public static final Set<String> MIME_IMAGE = new HashSet<String>(Arrays.asList(new String[]{"jpeg", "png", "gif", "jpg"}));
	public static final Set<String> MIME_AUDIO = new HashSet<String>(Arrays.asList(new String[]{"mp3", "3gp", "mp4", "m4a", "ogg", "wav"}));
	public static final Set<String> MIME_VIDEO = new HashSet<String>(Arrays.asList(new String[]{"mp4", "mov", "m4v", "webm", "3gp"}));
	
	public static class JSONKey
	{
		public static final Map<String, String> ERROR_MAP;
		static
		{
			Map<String, String> map = new HashMap<String, String>();
			map.put("400", "Token expiré ou inexistant");
			map.put("401", "Un ou plusieurs paramètres sont invalides");
			map.put("402", "Problème lors du transfert de fichier");
			map.put("403", "Fichier trop lourd");
			map.put("404", "Format non autorisé");
			map.put("405", "Message vide");
			map.put("406", "Message trop long");
			map.put("407", "Réseau social non reconnu");

			map.put("500", "Impossible de se connecter à la base de données");
			map.put("501", "Problème création user");
			map.put("502", "Problème création device");
			map.put("503", "Problème création social");
			map.put("504", "Problème créeation pièce jointe");
			
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
		public static final String MESSAGE_LAT = "lat";
		public static final String MESSAGE_LONG = "long";
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
