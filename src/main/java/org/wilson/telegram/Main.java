package org.wilson.telegram;

import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.wilson.telegram.util.DateScheduler;

/**
 * Main initialization class
 * 
 */

public class Main {

    public static void main(String[] args) throws TelegramApiRequestException {
    	TelegramInitiator telegram = new TelegramInitiator();
    	Thread bot = new Thread(telegram);
    	bot.start();
        Thread dateThread = new Thread(new DateScheduler());
        dateThread.start();
    }
}
