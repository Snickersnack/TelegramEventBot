package org.wilson.telegram.commandprocesses;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.File;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Sticker;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.wilson.telegram.config.BotConfig;
import org.wilson.telegram.messagehandler.MessageParser;
import org.wilson.telegram.models.ResponseModel;
import org.wilson.telegram.templates.EventRespondees;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ImgurHelper extends MessageParser {

	Message message;
	Long chatId;

	public ImgurHelper(Message message) {
		this.message = message;
		this.chatId = message.getChatId();
	}

	public static String uploadImage(String input) throws IOException {
//		System.out.println("upload via binary");
//
//		// HttpGet("https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/?key="
//		// + steamKey + "&steamids=" + steamId + "&format=json");
//		Object responseObject = null;
//		HttpGet getRequest = null;
//		String response = null;
//		String apiUrl = "imgur.com";
//		String apiProtocol = "http";
//		
//		try {
//
//			URIBuilder builder = new URIBuilder();
//			builder.setScheme(apiProtocol).setHost(apiUrl);
//			builder.setPath("/");
//			builder.setParameter("key", this.steamKey);
//
//			for (NameValuePair nvp : request.getSteamParameters()) {
//				builder.setParameter(nvp.getName(), nvp.getValue());
//
//			}
//
//			builder.setParameter("format", "json");
//			URI uri = builder.build();
//			
////			System.out.println(uri.toASCIIString());
//			getRequest = new HttpGet(uri);
//			
//			entity = client.execute(getRequest).getEntity();
//			
//			//check if entity is null
//			if (entity == null ){
//				System.out.println("Entity null");
//			}
//		}catch(Exception e){
//			
//		}
		return "asdf";

	}

	//add handling for when image doesn't uploda
	public String upload(String telegramurl) throws Exception {
		SendMessage sendMessageRequest = new SendMessage();
		sendMessageRequest.setChatId(chatId);
		sendMessageRequest.setText("<i>Uploading...</i>");
		sendMessageRequest.setParseMode(BotConfig.MESSAGE_MARKDOWN);
		try {
			sendMessage(sendMessageRequest);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		URL url;
		HttpsURLConnection conn;
		
		url = new URL("https://api.imgur.com/3/image");
		conn = (HttpsURLConnection) url.openConnection();

		String data = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(telegramurl, "UTF-8");

		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Authorization", "Client-ID "+ BotConfig.IMGURCLIENTID);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		
//		Socket clientSocket = SSLSocketFactory.getDefault().createSocket(host, port);
		try{
			conn.connect();
		}catch(SocketException e){
			try{
				conn.disconnect();
				conn.connect();
			}catch(SocketException b){
				throw b;
			}
		}

		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(data);
		wr.flush();	
		wr.close();


		// Get the response
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

		String line = rd.readLine();
		ObjectMapper mapper = new ObjectMapper();
		Object response = null;
		try {
			response = mapper.readValue(line, ResponseModel.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResponseModel newtest = (ResponseModel) response;
		line = newtest.getData().getLink();
		System.out.println(line);
		rd.close();

		conn.disconnect();
		
		return line;

	}

	private String processSticker() throws Exception  {
		Sticker sticker = message.getSticker();
		String telegramUrl = runWithoutFilePath(sticker);
		String imgurLink = null;
		
		imgurLink = upload(telegramUrl);
	
		return imgurLink;
	}

	private String processPhoto() throws Exception {
		PhotoSize image = null;
		List<PhotoSize> imagelist = message.getPhoto();
		image = imagelist.get(2);
		String telegramUrl = null;
		String imgurLink = null;
		if (!image.hasFilePath()) {
			telegramUrl = runWithoutFilePath(image);
		}else{
			telegramUrl  = BotConfig.GETFILEURL + BotConfig.BOTTOKEN + "/" + image.getFilePath();
		}
	
		imgurLink = upload(telegramUrl);
		
		return imgurLink;
		
	}


	
	
	public String post() throws Exception {
		String imgurUrl = null;
		if(message.hasPhoto()){
			imgurUrl = processPhoto();
		}else{
			imgurUrl = processSticker();
		}
		return imgurUrl;

	}

	public String runWithoutFilePath(Sticker sticker) {

		GetFile file = new GetFile();
		file.setFileId(sticker.getFileId());
		String filePath = null;
		try {
			File tFile = getFile(file);
			filePath = File.getFileUrl(BotConfig.BOTTOKEN, tFile.getFilePath());
			System.out.println("File url after download: " + filePath);
		} catch (Exception e) {
			System.out.println(e);
		}
		return filePath;
	}
	
	public String runWithoutFilePath(PhotoSize image) {

		GetFile file = new GetFile();
		file.setFileId(image.getFileId());
		String filePath = null;
		try {
			File tFile = getFile(file);
			filePath = File.getFileUrl(BotConfig.BOTTOKEN, tFile.getFilePath());
			System.out.println("File url after download: " + filePath);
		} catch (Exception e) {
			System.out.println(e);
		}
		return filePath;
	}

}
