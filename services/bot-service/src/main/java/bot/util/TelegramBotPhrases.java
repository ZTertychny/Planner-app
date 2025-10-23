package bot.util;

public class TelegramBotPhrases {
    public final static String START = "Привет! Введи адрес назначения (куда едешь), например: Москва, Кутузовский проспект 32";
    public final static String ASK_ORIGINAL = "Теперь введи адресс точки, откуда поедешь, например: Москва, Кутузовский проспект 32";
    public final static String ASK_TIME = "Теперь укажи, во сколько тебе нужно быть в месте назначения? Формат 09:00";
    public final static String DONE = """
            Готово!
            Назначение: %s
            Точка отбытия: %s
            Время прибытия: %s
            Ожидай напоминания!
            """;
    public final static String EXIT = "Все настройки приняты! Спасибо! Ожидай напоминания!";

}
