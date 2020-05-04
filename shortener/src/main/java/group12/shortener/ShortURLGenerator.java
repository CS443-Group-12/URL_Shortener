import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;
import java.util.Random;

public class ShortURLGenerator {
	private String[] chars = new String[] {"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f",
			"g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","A","B","C","D","E","F",
			"G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	private String shortUrlType [] = new String[] {"Derive from URL", "Random"};
	private int shortUrlLength = 7; // By default length is 7
	
	public String generateShortURL(String url, String shoUrlType, int shoUrlLength){
		if(isValidURL(url)) {
			String shortURL = "";
			setShortUrlLength(shoUrlLength);
			
			if(shoUrlType.toLowerCase().equals(shortUrlType[0].toLowerCase())) {
//				System.out.println("Short URL is derived form oroginal URL");
				shortURL += generateShortUrlFromOrigUrl(url);
			}
			else {
//				System.out.println("Short URL is created Randomly");
				shortURL += generateShortUrlRandom();
			}
//			System.out.println("Orig URL: " + url);
//			System.out.println("Short URL: " + shortURL);
			
			return shortURL;
		}
		return "URL is not valid";
	}
	
	/**
	 * Function to check either URL is valid or not
	 */
	private boolean isValidURL(String url) {
		boolean res = false;
		int response = 404;
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			response = connection.getResponseCode();
			connection.setInstanceFollowRedirects(false);
			while (connection.getResponseCode() / 100 == 3) {
				url = connection.getHeaderField("location");
				connection = (HttpURLConnection) new URL(url).openConnection();
			}
			
		}
		catch (MalformedURLException e) {
			res = false;
	    } 
		catch (IOException e) {
			res = false;
		}
		
		if(response == 404)
			res = false;
		else
			res = true;
		return res;
	}
	
	/**
	 * Function to generate random Short URL
	 */
	private String generateShortUrlRandom() {
		Random rand = new Random();
		String shoUrl = "";
		for (int i = 0; i < shortUrlLength; i++) {
			int index = rand.nextInt(chars.length);
			shoUrl += chars[index];
		}
		return shoUrl;
	}
	
	/**
	 * Function to derive Short URL from the Original URL
	 */
	private String generateShortUrlFromOrigUrl(String origUrl) {
		String hashedUrl = hashURL(origUrl);
		BigInteger hashedToInt = convertHashedUrlToInteger(hashedUrl);
//		System.out.println("hashed to in: " + hashedToInt);
		String currTimeStr = "" + System.currentTimeMillis();
//		System.out.println("curr time: " + currTimeStr);
		BigInteger currTimeInt = new BigInteger(currTimeStr);
//		System.out.println("curr time int: " + currTimeInt);
		hashedToInt = hashedToInt.add(currTimeInt);
//		System.out.println("hashed to in: " + hashedToInt);
		String shortConvertion = convertHashedIntUrlToShortUrl(hashedToInt);
		
		return shortConvertion;
	}
	
	/**
	 * Function to hash Original URL
	 */
	private String hashURL(String url) {
		String hashedUrl = "";
		MessageDigest md;
		byte[] urlByte;
		BigInteger valueInt;
		StringBuilder valueStr;
		try {
			md = MessageDigest.getInstance("SHA-1");
			urlByte = md.digest(url.getBytes(StandardCharsets.UTF_8));
			valueInt = new BigInteger(1, urlByte);
			valueStr = new StringBuilder(valueInt.toString(16));
			hashedUrl = valueStr.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hashedUrl;
	}
	
	/**
	 * Function to convert hashed Original URL to a BigInteger
	 */
	private BigInteger convertHashedUrlToInteger(String hashedUrl) {
		String intNo = "";
		for(int i = 0; i < hashedUrl.length(); i++) {
			String ch = hashedUrl.substring(i, i+1);
			int chIndex = -1;
			for (int j = 0; j < chars.length; j++) {
				if(ch.equals(chars[j])) {
					chIndex = j;
					j = chars.length;
				}
			}
			if(chIndex == -1)
				chIndex = 0;
			intNo += chIndex;
		}
		BigInteger hashedIntUrl = new BigInteger(intNo);
		return hashedIntUrl;
	}
	
	/**
	 * Function to get a short url from the BigInteger 
	 */
	private String convertHashedIntUrlToShortUrl(BigInteger hashedIntUrl) {
		String shortUrl = "";
		String div = "";
		if(shortUrlLength == 5)
			div += "200000000000";
		if(shortUrlLength == 6)
			div += "20000000000";
		if(shortUrlLength == 7)
			div += "200000000";
		if(shortUrlLength == 8)
			div += "20000000";
		if(shortUrlLength == 9)
			div += "2000000";
		if(shortUrlLength == 10)
			div += "500000";
		
		BigInteger divider = new BigInteger(div);
		while(hashedIntUrl.intValue() != 0) {
			int modResult = hashedIntUrl.mod(divider).intValue();
			if(modResult < 0)
				modResult *= -1;
			int index = modResult % chars.length;
			shortUrl += chars[index];
			hashedIntUrl = hashedIntUrl.divide(divider);
		}
		return shortUrl;
	}
	
	/**
	 * Function to set Short URL length
	 */
	private void setShortUrlLength(int urlLength) {
		shortUrlLength = urlLength;
	}

}
