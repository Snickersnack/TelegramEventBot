package org.wilson.telegram;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;
import org.wilson.telegram.updatemethods.UpdateHandler;
import org.wilson.telegram.util.DateScheduler;

public class TelegramInitiator implements Runnable{

	public void run(){
    	ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            telegramBotsApi.registerBot(new UpdateHandler());
        } catch (TelegramApiException e) {
            BotLogger.error("Error: ", e);
        }
        

	}
}
