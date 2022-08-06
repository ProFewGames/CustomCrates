package xyz.ufactions.customcrates.dialog;

import org.bukkit.entity.Player;

import java.util.Optional;

public interface Dialog {

    Dialog askQuestion(Question question);

    Optional<Question> nextQuestion();

    Question getCurrentQuestion();

    Player getPlayer();

    void promptQuestion();

    void begin();

    void end();
}