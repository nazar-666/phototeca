package com.example.phototeca.services.impl;

import com.example.phototeca.enums.UserState;
import com.example.phototeca.services.DataUpdaterService;
import com.example.phototeca.services.TelegramService;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.util.Map;

import static com.example.phototeca.constants.Constants.CHAT_STATES;
import static com.example.phototeca.constants.Constants.START_TEXT;
import static com.example.phototeca.enums.UserState.*;

@Service
public class TelegramServiceImpl implements TelegramService {
    private SilentSender sender;
    private Map<Long, UserState> chatStates;

    private final DataUpdaterService dataUpdaterService;

    public TelegramServiceImpl(DataUpdaterService dataUpdaterService) {
        this.dataUpdaterService = dataUpdaterService;
    }

    @Override
    public void init(SilentSender sender, DBContext db) {
        this.sender = sender;
        this.chatStates = db.getMap(CHAT_STATES);
    }

    public void replyToStart(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(START_TEXT);
        sender.execute(message);
        chatStates.put(chatId, AWAITING_NAME);
    }

    public void replyToButtons(long chatId, Message message) {
        if (message.getText().equalsIgnoreCase("/stop")) {
            dataUpdaterService.stopDataComparison();
            stopChat(chatId);
        }

        switch (chatStates.get(chatId)) {
            case AWAITING_NAME:
                replyToName(chatId, message);
                dataUpdaterService.startDataComparison();
                break;
            case MONITORING:
                //TODO send changed data to telegram
                dataUpdaterService.getCryptosChangesOverThreshold();
                break;
            default:
                unexpectedMessage(chatId);
                break;
        }
    }

    private void replyToName(long chatId, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Hello " + message.getText() + ". I'm going to monitor crypto changes!");
        sender.execute(sendMessage);
    }

    private void unexpectedMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("I did not expect that.");
        sender.execute(sendMessage);
    }

    private void stopChat(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Thank you for being with me. See you soon!\n" +
                "Press /start to run me again");
        chatStates.remove(chatId);
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        sender.execute(sendMessage);
    }

    public boolean userIsActive(Long chatId) {
        return chatStates.containsKey(chatId);
    }
}
