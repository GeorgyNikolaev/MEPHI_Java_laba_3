package reporter;

import domain.*;
import enums.*;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class StatisticalReport implements MissionReporter {

    @Override
    public Report generate(Mission mission) {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════════════════╗\n");
        sb.append("║               СТАТИСТИЧЕСКИЙ АНАЛИЗ МИССИИ                   ║\n");
        sb.append("╚══════════════════════════════════════════════════════════════╝\n\n");

        sb.append("📊 ОБЩИЕ МЕТРИКИ\n");
        sb.append("ID миссии   : ").append(mission.getMissionId()).append("\n");
        sb.append("Статус      : ").append(formatEnum(mission.getOutcome())).append("\n");
        sb.append("Длительность: ").append(calculateDuration(mission)).append("\n\n");

        // 1. Состав команды
        var sorcerers = mission.getSorcerers();
        sb.append("👥 СОСТАВ КОМАНДЫ (").append(sorcerers != null ? sorcerers.size() : 0).append(" чел.)\n");
        if (sorcerers != null && !sorcerers.isEmpty()) {
            var rankDist = sorcerers.stream()
                    .collect(Collectors.groupingBy(s -> s.getRank() != null ? s.getRank() : null, Collectors.counting()));
            rankDist.forEach((rank, count) -> sb.append("• ").append(formatEnum(rank)).append(": ").append(count).append("\n"));
        }
        sb.append("\n");

        // 2. Техники
        var techniques = mission.getTechniques();
        sb.append("⚡ ТЕХНИКИ (").append(techniques != null ? techniques.size() : 0).append(" применений)\n");
        if (techniques != null && !techniques.isEmpty()) {
            long totalTechDamage = techniques.stream()
                    .mapToLong(t -> t.getDamage() != null ? t.getDamage() : 0)
                    .sum();
            sb.append("Суммарный урон техник: ").append(formatMoney(totalTechDamage)).append("\n\n");

            var typeDist = techniques.stream()
                    .filter(t -> t.getTechnique() != null)
                    .collect(Collectors.groupingBy(t -> t.getTechnique().getType(), Collectors.counting()));
            sb.append("Распределение по типам:\n");
            typeDist.forEach((type, count) -> sb.append("• ").append(formatEnum(type)).append(": ").append(count).append("\n"));
            sb.append("\n");

            // Топ техник по урону
            var topTech = techniques.stream()
                    .filter(t -> t.getDamage() != null)
                    .sorted((a, b) -> Long.compare(b.getDamage(), a.getDamage()))
                    .limit(3)
                    .toList();
            if (!topTech.isEmpty()) {
                sb.append("Топ-3 по урону:\n");
                for (int i = 0; i < topTech.size(); i++) {
                    var t = topTech.get(i);
                    sb.append(i + 1).append(". ").append(t.getTechnique().getName())
                            .append(" (").append(formatMoney(t.getDamage())).append(")\n");
                }
            }
        }
        sb.append("\n");

        // 3. Экономика
        EconomicAssessment econ = mission.getEconomicAssessment();
        if (econ != null) {
            sb.append("💰 ЭКОНОМИКА\n");
            if (econ.getTotalDamageCost() != null) sb.append("Общий ущерб        : ").append(formatMoney(econ.getTotalDamageCost())).append("\n");
            if (econ.getRecoveryEstimateDays() != null) sb.append("Время восстановления: ").append(econ.getRecoveryEstimateDays()).append(" дн.\n");
            if (econ.getTotalDamageCost() != null && techniques != null) {
                long techDamage = techniques.stream().mapToLong(t -> t.getDamage() != null ? t.getDamage() : 0).sum();
                double efficiency = techDamage > 0 ? (double) econ.getTotalDamageCost() / techDamage : 0;
                sb.append("Коэф. эффективности: ").append(String.format("%.2f", efficiency)).append(" (ущерб/урон)\n");
            }
            sb.append("\n");
        }

        // 4. Хронология
        var timeline = mission.getOperationTimeline();
        if (timeline != null && !timeline.isEmpty()) {
            sb.append("⏱️ ХРОНОЛОГИЯ\n");
            sb.append("Всего событий: ").append(timeline.size()).append("\n");
            var eventDist = timeline.stream()
                    .collect(Collectors.groupingBy(e -> e.getType() != null ? e.getType() : OperationTimelineType.UNKNOWN, Collectors.counting()));
            eventDist.forEach((type, count) -> sb.append("• ").append(formatEnum(type)).append(": ").append(count).append("\n"));
            sb.append("\n");
        }

        sb.append("═══════════════════════════════════════════════════════════════\n");
        sb.append("Сформировано: ").append(java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));

        return new Report(sb.toString(), ReportType.STATISTICAL);
    }

    @Override
    public ReportType getType() {
        return ReportType.STATISTICAL;
    }

    private String calculateDuration(Mission mission) {
        var timeline = mission.getOperationTimeline();
        if (timeline == null || timeline.size() < 2) return "N/A (недостаточно данных)";
        var first = timeline.get(0).getTimestamp();
        var last = timeline.get(timeline.size() - 1).getTimestamp();
        if (first == null || last == null) return "N/A";
        long minutes = java.time.Duration.between(first, last).toMinutes();
        return minutes + " мин.";
    }

    private String formatEnum(Enum<?> e) {
        return e != null ? e.name().replace('_', ' ') : "N/A";
    }

    private String formatMoney(long amount) {
        return String.format("%,d ¥", amount);
    }
}