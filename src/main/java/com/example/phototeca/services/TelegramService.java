package com.example.phototeca.services;

import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface TelegramService {
    void init(SilentSender sender, DBContext db);
    void replyToStart(long chatId);
    void replyToButtons(long chatId, Message message);
    boolean userIsActive(Long chatId);
}
