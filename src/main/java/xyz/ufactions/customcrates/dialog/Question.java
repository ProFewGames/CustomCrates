package xyz.ufactions.customcrates.dialog;

import lombok.Builder;
import lombok.Getter;
import xyz.ufactions.customcrates.libs.F;

import java.util.function.Predicate;

@Builder(builderMethodName = "create")
@Getter
public class Question {

    public static QuestionBuilder create(String question) {
        return new QuestionBuilder().question(F.format(question));
    }

    private String question;
    private boolean repeatIfFailed;
    @Builder.Default
    private boolean stripColor = true;
    private Predicate<String> inputPredicate;
}