package org.wilson.telegram.messagehandler;

import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.wilson.telegram.client.Cache;
import org.wilson.telegram.client.UpdateHandler;
import org.wilson.telegram.config.BotConfig;
import org.wilson.telegram.templates.Commands;

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
		if(message.isUserMessage()){
				UserMessageHelper userMessage = new UserMessageHelper(sendMessageRequest);
				return userMessage.parse(message);	
							
			
		//Else it is a group command
		}else{

			if(command.startsWith(Commands.STARTCOMMAND) || command.startsWith(Commands.DELETEEVENTSCOMMAND) 
					|| command.startsWith(Commands.CLEAREVENTSCOMMAND) || command.startsWith(Commands.EDITCOMMAND) 
					|| command.startsWith(Commands.CANCELCOMMAND)){
				sendMessageRequest.setText("<i>Message bot directly to create, edit and remove events</i>");
			}
			else{
				
				GroupMessageHelper groupMessage = new GroupMessageHelper(sendMessageRequest);
				sendMessageRequest = groupMessage.parse(message);
			}
			
		}
		
	


		return sendMessageRequest;

	}



}
