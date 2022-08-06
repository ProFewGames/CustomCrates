package xyz.ufactions.customcrates.variable;

@FunctionalInterface
public interface VariableReplacer {

    VariableReplacer DEFAULT = string -> string;

    String replace(String string);
}