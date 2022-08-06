package xyz.ufactions.customcrates.variable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VariableReplacerPack {

    private final VariableReplacer[] variableReplacers;

    public static Builder create() {
        return new Builder();
    }

    public String replace(String string) {
        for (VariableReplacer variableReplacer : this.variableReplacers)
            string = variableReplacer.replace(string);
        return string;
    }

    public static final class Builder {

        private final List<VariableReplacer> variableReplacerList;

        public Builder() {
            this.variableReplacerList = new ArrayList<>();
        }

        public Builder apply(VariableReplacer variableReplacer) {
            this.variableReplacerList.add(variableReplacer);
            return this;
        }

        public Builder apply(String variable, String result) {
            return apply(string -> string.replaceAll("%" + variable + "%", result));
        }

        public VariableReplacerPack build() {
            return new VariableReplacerPack(this.variableReplacerList.toArray(new VariableReplacer[0]));
        }
    }
}