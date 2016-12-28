package org.wilson.telegram.messagehandler;

import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.wilson.telegram.client.UpdateHandler;
import org.wilson.telegram.config.BotConfig;
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
				
				sendMessageRequest.setText("Message @EvePlannerBot directly to use this command or click below");
				KeyboardBuilder keyboard = new KeyboardBuilder(1,1);
				InlineKeyboardButton button = new InlineKeyboardButton();
				button.setText("Create events here");
				button.setSwitchInlineQueryCurrentChat(" ");
				keyboard.addButton(button);
				InlineKeyboardMarkup markup = keyboard.buildMarkup();
				sendMessageRequest.setReplyMarkup(markup);
				
			}
			
			else if(command.startsWith(Commands.DELETEEVENTSCOMMAND) 
					|| command.startsWith(Commands.CLEAREVENTSCOMMAND) || command.startsWith(Commands.EDITCOMMAND) 
					|| command.startsWith(Commands.CANCELCOMMAND) || command.startsWith(Commands.MENUCOMMAND)
					|| command.startsWith(Commands.RESPONDEESCOMMAND)){
				
				sendMessageRequest.setText("Message @EvePlannerBot directly to use this command");

			}
			else{
				
				GroupMessageHelper groupMessage = new GroupMessageHelper(sendMessageRequest);
				sendMessageRequest = groupMessage.parse(message);
			}
			
		}
		
	


		return sendMessageRequest;

	}



}
