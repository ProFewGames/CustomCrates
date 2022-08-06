package xyz.ufactions.customcrates.dialog;

import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.ufactions.customcrates.file.LanguageFile;
import xyz.ufactions.customcrates.libs.F;

import java.util.LinkedList;
import java.util.Optional;

class DialogImpl implements Dialog {

    private final LinkedList<Question> questions;
    private final DialogManager dialogManager;
    @Getter
    private final Player player;

    public DialogImpl(DialogManager dialogManager, Player player) {
        this.questions = new LinkedList<>();
        this.dialogManager = dialogManager;
        this.player = player;
    }

    @Override
    public Dialog askQuestion(Question question) {
        this.questions.addLast(question);
        return this;
    }

    @Override
    public Optional<Question> nextQuestion() {
        this.questions.removeFirst();
        return Optional.ofNullable(this.questions.isEmpty() ? null : this.questions.getFirst());
    }

    @Override
    public Question getCurrentQuestion() {
        return this.questions.getFirst();
    }

    @Override
    public void promptQuestion() {
        this.player.sendMessage(getCurrentQuestion().getQuestion());
    }

    @Override
    public void begin() {
        if (this.questions.isEmpty()) return;
        this.dialogManager.setDialog(this);

        this.player.sendTitle(F.color(dialogManager.getPlugin().getLanguage().getString(LanguageFile.LanguagePath.DIALOG_TITLE)), F.color(dialogManager.getPlugin().getLanguage().getString(LanguageFile.LanguagePath.DIALOG_SUBTITLE)), 0, 20 * 60 * 60, 0);
        promptQuestion();
    }

    @Override
    public void end() {
        this.player.resetTitle();
        this.dialogManager.removeDialog(player);
    }
}