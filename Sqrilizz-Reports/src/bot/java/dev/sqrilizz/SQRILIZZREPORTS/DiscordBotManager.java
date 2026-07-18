package dev.sqrilizz.SQRILIZZREPORTS;

import java.util.List;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.interactions.InteractionHook;

public final class DiscordBotManager extends ListenerAdapter {

    private static volatile JDA jda;

    public static synchronized void initialize() {
        if (jda != null) return;
        String token = Main.getInstance().getConfig().getString("discord.bot.token", "");
        boolean enabled = Main.getInstance().getConfig().getBoolean("discord.bot.enabled", false);
        if (!enabled || token.isBlank() || "YOUR_BOT_TOKEN".equals(token)) return;
        try {
            jda = JDABuilder.createDefault(token).addEventListeners(new DiscordBotManager()).build();
            Main.getInstance().getLogger().info("Discord bot is connecting");
        } catch (IllegalArgumentException exception) {
            Main.getInstance().getLogger().warning("Discord bot was not started: " + exception.getMessage());
        }
    }

    public static synchronized void reload() {
        shutdown();
        initialize();
    }

    public static synchronized void shutdown() {
        if (jda != null) {
            jda.shutdown();
            jda = null;
        }
    }

    public static void sendReport(ReportManager.Report report, boolean bugReport) {
        JDA activeJda = jda;
        if (activeJda == null) return;
        String channelId = Main.getInstance().getConfig().getString("discord.bot.channel-id", "");
        if (channelId.isBlank() || "YOUR_CHANNEL_ID".equals(channelId)) return;
        TextChannel channel = activeJda.getTextChannelById(channelId);
        if (channel == null) return;

        channel.sendMessageEmbeds(createReportEmbed(report, bugReport, "Открыт", null).build())
            .setComponents(ActionRow.of(
                Button.success("reports:resolve:" + report.id, "Решён"),
                Button.danger("reports:not-a-bug:" + report.id, bugReport ? "Не баг" : "Ложный репорт")
            ))
            .queue(
                ignored -> {},
                error -> Main.getInstance().getLogger().warning("Discord bot could not send report: " + error.getMessage())
            );
    }

    private static EmbedBuilder createReportEmbed(
        ReportManager.Report report,
        boolean bugReport,
        String status,
        String moderator
    ) {
        EmbedBuilder embed = new EmbedBuilder()
            .setTitle(bugReport ? "Новый баг-репорт" : "Новая жалоба")
            .setColor(bugReport ? 0xFFA500 : 0xFF0000)
            .setThumbnail(getPlayerHeadUrl(bugReport ? report.reporter : report.target))
            .addField("ID", String.valueOf(report.id), true)
            .addField("Отправитель", report.isAnonymous ? "Аноним" : report.reporter, true)
            .addField(bugReport ? "Категория" : "На кого", bugReport ? extractCategory(report.reason) : report.target, true)
            .addField(bugReport ? "Описание" : "Причина", report.reason, false)
            .addField("Статус", status, true)
            .setFooter("Sqrilizz-Reports • #" + report.id)
            .setTimestamp(java.time.Instant.ofEpochMilli(report.timestamp));
        if (moderator != null) {
            embed.addField("Решение принял", moderator, true);
        }
        return embed;
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String[] parts = event.getComponentId().split(":", 3);
        if (parts.length != 3 || !"reports".equals(parts[0])) return;
        if (!canModerate(event.getMember())) {
            event.reply("Недостаточно прав для управления репортами.").setEphemeral(true).queue();
            return;
        }
        long reportId;
        try {
            reportId = Long.parseLong(parts[2]);
        } catch (NumberFormatException exception) {
            event.reply("Некорректный ID репорта.").setEphemeral(true).queue();
            return;
        }
        String action = parts[1];
        String moderator = event.getUser().getName();
        event.deferReply(true).queue(hook ->
            Main.runTask(() -> processAction(event, hook, reportId, action, moderator))
        );
    }

    private static void processAction(
        ButtonInteractionEvent event,
        InteractionHook hook,
        long reportId,
        String action,
        String moderator
    ) {
        ReportManager.Report report = ReportManager.getReportById(reportId);
        if (report == null || report.isResolved()) {
            hook.sendMessage("Этот репорт уже закрыт или не существует.").setEphemeral(true).queue();
            return;
        }
        boolean bugReport = "BUG_REPORT".equals(report.target);
        boolean resolved = "resolve".equals(action);
        if (!resolved && !"not-a-bug".equals(action)) {
            hook.sendMessage("Неизвестное действие.").setEphemeral(true).queue();
            return;
        }
        ReportManager.Status status = resolved
            ? ReportManager.Status.RESOLVED
            : (bugReport ? ReportManager.Status.NOT_A_BUG : ReportManager.Status.FALSE_REPORT);
        ReportManager.updateStatus(reportId, "Discord: " + moderator, status);
        String statusLabel = resolved ? "Решён" : (bugReport ? "Не баг" : "Ложный репорт");
        Message message = event.getMessage();
        message.editMessageEmbeds(createReportEmbed(report, bugReport, statusLabel, moderator).build())
            .setComponents(ActionRow.of(
                Button.secondary("reports:closed:" + report.id, statusLabel + ": " + moderator).asDisabled()
            ))
            .queue(
                ignored -> {},
                error -> Main.getInstance().getLogger().warning("Discord bot could not close report card: " + error.getMessage())
            );
        hook.sendMessage(resolved ? "Репорт отмечен как решённый." : "Репорт отмечен как не баг/ложный.").setEphemeral(true).queue();
    }

    private static boolean canModerate(Member member) {
        if (member == null) return false;
        if (member.hasPermission(Permission.ADMINISTRATOR)) return true;
        List<String> roles = Main.getInstance().getConfig().getStringList("discord.bot.mod-roles");
        return member.getRoles().stream().anyMatch(role -> roles.contains(role.getId()));
    }

    private static String extractCategory(String reason) {
        int closing = reason.indexOf(']');
        return reason.startsWith("[") && closing > 1 ? reason.substring(1, closing) : "Другое";
    }

    private static String getPlayerHeadUrl(String playerName) {
        if (playerName == null || playerName.isBlank() || "BUG_REPORT".equals(playerName)) return null;
        return "https://api.mcheads.org/head/" +
            URLEncoder.encode(playerName, StandardCharsets.UTF_8).replace("+", "%20") +
            "/128";
    }
}
