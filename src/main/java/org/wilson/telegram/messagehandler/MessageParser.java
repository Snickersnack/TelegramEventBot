package org.wilson.telegram.messagehandler;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.File;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.wilson.telegram.client.UpdateHandler;
import org.wilson.telegram.config.BotConfig;
import org.wilson.telegram.messagehandler.usermessage.UserMessageHelper;
import org.wilson.telegram.templates.Commands;
import org.wilson.telegram.util.KeyboardBuilder;

/**
 * Service to parse commands
 * 
 */

// Takes in the message and parses for command
// Runs service based on command match

public class MessageParser extends UpdateHandler {

	protected String command;
	protected Message message;
	SendMessage sendMessageRequest;
	Long chatId;

	public MessageParser(){
		
	}
	
	public MessageParser(Message message) {
		this.message = message;
		this.chatId = message.getChatId();
		sendMessageRequest = new SendMessage();

	}


	public BotApiMethod<?> parse() throws TelegramApiException {

		
		if(message.hasPhoto()){
//			else if(update.getMessage().hasPhoto()){
//			
//			List<PhotoSize> imagelist = update.getMessage().getPhoto();
//			PhotoSize image = imagelist.get(2);
//			System.out.println("image file path " + image.getFilePath());
//			System.out.println(image.toString());
//			GetFile file = new GetFile();
//			file.setFileId(image.getFileId());
//			File tFile = getFile(file);
//			
//			java.io.File newFile = downloadFile(tFile);
//			
//			byte[] imageInByte = null;
//			BufferedImage img = null;
//			try {
//			    img = ImageIO.read(newFile);
//				ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			    ImageIO.write( img, "jpg", baos );
//				baos.flush();
//
//				imageInByte = baos.toByteArray();
//				baos.close();
//				
//			} catch (IOException e) {
//				System.out.println(e);
//			}
//
//			SendPhoto mess = new SendPhoto();
//			System.out.println("file name: " + newFile.getName());
//			ByteArrayInputStream bInput = new ByteArrayInputStream(imageInByte);
//			HashSet<EventModel> set = Cache.getInstance().getMasterEventMap().get(163396337);
//			for(EventModel event : set){
//				event.setImage(imageInByte);
//			}
//			
//			mess.setNewPhoto("test.png", bInput);
//			mess.setChatId(update.getMessage().getChatId());
//			mess.setCaption("👥  (" + "12" + "): ");
//			KeyboardBuilder build = new KeyboardBuilder();
//
//			mess.setReplyMarkup(build.buildReturnMenu());
//			sendPhoto(mess);
//			return null;
//
//		}
			return null;
		}else{
			command = message.getText().toLowerCase();
			sendMessageRequest.setChatId(message.getChatId());
			sendMessageRequest.setParseMode(BotConfig.MESSAGE_MARKDOWN);
			
			
			//Check for user commands
			
			if (command.startsWith(Commands.HELPCOMMAND)
					&& command.substring(0, 5).equals("/help")) {
				sendMessageRequest.setText(Commands.HELPTEXT);
			}
			else if(message.isUserMessage()){
				if(command.startsWith(Commands.VIEWCOMMAND)){
					String text = "View lists all events shared to a channel, not a user. Use /view in a channel";
					sendMessageRequest.setText(text);
				}else{
					UserMessageHelper userMessage = new UserMessageHelper(sendMessageRequest);
					return userMessage.parse(message);		
				}

								
				
			//Else it is a group command
			}else{

				if(command.startsWith(Commands.STARTCOMMAND) ){
					
					sendMessageRequest.setText("Message @" + BotConfig.BOTUSERNAME + " directly to use this command or click below");
					KeyboardBuilder keyboard = new KeyboardBuilder(1,1);
					InlineKeyboardButton button = new InlineKeyboardButton();
					button.setText("Your Events");
					button.setSwitchInlineQueryCurrentChat(" ");
					keyboard.addButton(button);
					InlineKeyboardMarkup markup = keyboard.buildMarkup();
					sendMessageRequest.setReplyMarkup(markup);
					
				}
				
				else if(command.startsWith(Commands.DELETEEVENTSCOMMAND) 
						|| command.startsWith(Commands.CLEAREVENTSCOMMAND) || command.startsWith(Commands.EDITCOMMAND) 
						|| command.startsWith(Commands.CANCELCOMMAND) || command.startsWith(Commands.MENUCOMMAND)
						|| command.startsWith(Commands.RESPONDEESCOMMAND)){
					
					sendMessageRequest.setText("Message @" + BotConfig.BOTUSERNAME + " directly to use this command");

				}
				else{
					
					GroupMessageHelper groupMessage = new GroupMessageHelper(sendMessageRequest);
					sendMessageRequest = groupMessage.parse(message);
				}
				
			}
			
		


			return sendMessageRequest;

		}
	}




}
