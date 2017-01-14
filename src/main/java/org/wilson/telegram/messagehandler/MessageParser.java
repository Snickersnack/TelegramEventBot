package org.wilson.telegram.messagehandler;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.File;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Sticker;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.wilson.telegram.client.UpdateHandler;
import org.wilson.telegram.commandprocesses.ImgurHelper;
import org.wilson.telegram.config.BotConfig;
import org.wilson.telegram.messagehandler.usermessage.UserMessageHelper;
import org.wilson.telegram.models.Data;
import org.wilson.telegram.templates.Commands;
import org.wilson.telegram.util.KeyboardBuilder;
import org.wilson.telegram.util.DateUtil.DateEventCleanup;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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


			command = message.getText().toLowerCase();
			sendMessageRequest.setChatId(message.getChatId());
			sendMessageRequest.setParseMode(BotConfig.MESSAGE_MARKDOWN);
			
			
			//Check for user commands
			
			if (command.startsWith(Commands.HELPCOMMAND)
					&& command.substring(0, 5).equals("/help")) {
				sendMessageRequest.setText(Commands.HELPTEXT);
			}
			else if(message.isUserMessage()){
				UserMessageHelper userMessage = new UserMessageHelper(sendMessageRequest);
				return userMessage.parse(message);		

								
				
			//Else it is a group command
			}else{

				if(command.startsWith(Commands.STARTCOMMAND) ){
					
					sendMessageRequest.setText("Message @" + BotConfig.BOTUSERNAME + " directly to use this command or click below");
					KeyboardBuilder keyboard = new KeyboardBuilder(1,1);
					InlineKeyboardButton button = new InlineKeyboardButton();
					button.setText("View your events");
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





