package org.wilson.telegram.client;

import java.net.URL;
import java.net.URLClassLoader;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.logging.BotLogger;
import org.wilson.telegram.util.DateUtil.DateScheduler;

/**
 * Main initialization class
 * 
 */

public class Main {

    public static void main(String[] args) throws TelegramApiRequestException {
    	

        
    	ApiContextInitializer.init();
    	Cache.getInstance().init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            telegramBotsApi.registerBot(new UpdateHandler());
        } catch (TelegramApiException e) {
            BotLogger.error("Error: ", e);
        }
        Thread dateThread = new Thread(new DateScheduler());
        dateThread.start();
    }
}
