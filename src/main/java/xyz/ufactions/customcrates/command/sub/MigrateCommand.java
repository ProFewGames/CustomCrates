package xyz.ufactions.customcrates.command.sub;

import org.bukkit.command.CommandSender;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.command.SubCommand;
import xyz.ufactions.customcrates.file.migrator.CrateFileMigrator;
import xyz.ufactions.customcrates.libs.F;

import java.util.Collections;
import java.util.List;

public class MigrateCommand extends SubCommand {

    public MigrateCommand(CustomCrates plugin) {
        super(plugin, "Migrate any files using an old deprecated structure to a new structure.", "customcrates.command.migrate", new String[]{
                "migrate"
        });
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        sender.sendMessage(F.format("Commencing File Migration..."));
        CrateFileMigrator migrator = CrateFileMigrator.create(this.plugin);
        int changes = migrator.performMigration(true);
        plugin.reload();
        sender.sendMessage(F.format(String.format("Migration completed. Total lines edited '%s'.", changes)));
        return true;
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        return Collections.emptyList();
    }
}