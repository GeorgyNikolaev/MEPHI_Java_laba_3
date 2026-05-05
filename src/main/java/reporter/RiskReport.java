package reporter;

import domain.*;
import enums.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RiskReport implements MissionReporter {

    @Override
    public Report generate(Mission mission) {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════════════════╗\n");
        sb.append("║                 АНАЛИТИЧЕСКИЙ ОТЧЕТ ПО РИСКАМ                ║\n");
        sb.append("╚══════════════════════════════════════════════════════════════╝\n\n");

        sb.append("🎯 МИССИЯ: ").append(mission.getMissionId())
                .append(" | Дата: ").append(mission.getDate())
                .append(" | Локация: ").append(mission.getLocation()).append("\n\n");

        List<RiskFactor> risks = new ArrayList<>();

        // 1. Оценка угрозы проклятия
        if (mission.getCurse() != null) {
            ThreatLevel threat = mission.getCurse().getThreatLevel();
            int weight = getThreatWeight(threat);
            risks.add(new RiskFactor("Уровень проклятия", formatEnum(threat), weight, "Определяет базовый потенциал опасности объекта"));
        }

        // 2. Активность противника
        EnemyActivity enemy = mission.getEnemyActivity();
        if (enemy != null) {
            risks.add(new RiskFactor("Мобильность противника", formatEnum(enemy.getMobility()),
                    enemy.getMobility() == MobilityLevel.HIGH || enemy.getMobility() == MobilityLevel.VARIABLE ? 3 : 1,
                    "Высокая мобильность усложняет контроль периметра"));
            risks.add(new RiskFactor("Риск эскалации", formatEnum(enemy.getEscalationRisk()),
                    enemy.getEscalationRisk() == EscalationRisk.HIGH || enemy.getEscalationRisk() == EscalationRisk.CRITICAL ? 3 : 1,
                    "Определяет вероятность перерастания конфликта"));
            if (!enemy.getAttackPatterns().isEmpty()) {
                risks.add(new RiskFactor("Сложность паттернов атак", String.valueOf(enemy.getAttackPatterns().size()), 2,
                        "Нестандартные тактики требуют адаптации стратегии"));
            }
        }

        // 3. Условия среды
        EnvironmentConditions env = mission.getEnvironmentConditions();
        if (env != null) {
            int envRisk = 0;
            if (env.getVisibility() == VisibilityLevel.POOR || env.getVisibility() == VisibilityLevel.ZERO) envRisk += 2;
            if (env.getTimeOfDay() == TimeOfDay.NIGHT || env.getTimeOfDay() == TimeOfDay.MIDNIGHT) envRisk += 1;
            if (env.getCursedEnergyDensity() != null && env.getCursedEnergyDensity() > 80) envRisk += 2;
            if (envRisk > 0) {
                risks.add(new RiskFactor("Неблагоприятные условия", String.valueOf(envRisk), envRisk,
                        "Снижает эффективность обнаружения и навигации"));
            }
        }

        // 4. Гражданские риски
        CivilianImpact civ = mission.getCivilianImpact();
        if (civ != null) {
            if ((civ.getInjured() != null && civ.getInjured() > 5) || (civ.getMissing() != null && civ.getMissing() > 0)) {
                risks.add(new RiskFactor("Потери среди гражданских", "Высокие", 3,
                        "Необходима эвакуация и медицинское сопровождение"));
            }
        }

        // Вывод матрицы рисков
        sb.append("📊 МАТРИЦА РИСКОВ\n");
        sb.append(String.format("%-30s | %-10s | %-6s | %s\n", "ФАКТОР", "ЗНАЧЕНИЕ", "ВЕС", "ОБОСНОВАНИЕ"));
        sb.append("-".repeat(85)).append("\n");
        int totalWeight = 0;
        for (RiskFactor r : risks) {
            sb.append(String.format("%-30s | %-10s | %-6d | %s\n", r.name(), r.value(), r.weight(), r.reason()));
            totalWeight += r.weight();
        }
        sb.append("-".repeat(85)).append("\n");
        sb.append("ИТОГОВЫЙ ИНДЕКС РИСКА: ").append(totalWeight).append("\n\n");

        // Рекомендации
        sb.append("🛡️ РЕКОМЕНДАЦИИ ПО СНИЖЕНИЮ РИСКОВ\n");
        if (totalWeight >= 8) sb.append("🔴 КРИТИЧЕСКИЙ УРОВЕНЬ: Требуется усиленный состав команды, барьеры 3-го класса, подготовка мемориальных отрядов.\n");
        else if (totalWeight >= 5) sb.append("🟡 ПОВЫШЕННЫЙ УРОВЕНЬ: Стандартный протокол с усиленным контролем периметра и гражданских.\n");
        else sb.append("🟢 СТАНДАРТНЫЙ УРОВЕНЬ: Плановое выполнение по регламенту.\n");

        if (mission.getEnemyActivity() != null && mission.getEnemyActivity().getMobility() == MobilityLevel.HIGH) {
            sb.append("• Развернуть мобильные группы перехвата\n");
        }
        if (mission.getEnvironmentConditions() != null && mission.getEnvironmentConditions().getVisibility() != null &&
                mission.getEnvironmentConditions().getVisibility().ordinal() >= VisibilityLevel.POOR.ordinal()) {
            sb.append("• Использовать техники расширенного восприятия\n");
        }

        sb.append("\n═══════════════════════════════════════════════════════════════\n");
        sb.append("Сформировано: ").append(java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));

        return new Report(sb.toString(), ReportType.RISK);
    }

    @Override
    public ReportType getType() {
        return ReportType.RISK;
    }

    private int getThreatWeight(ThreatLevel level) {
        return switch (level) {
            case SPECIAL_GRADE -> 4;
            case HIGH -> 3;
            case MEDIUM -> 2;
            case LOW -> 1;
            default -> 0;
        };
    }

    private String formatEnum(Enum<?> e) {
        return e != null ? e.name().replace('_', ' ') : "N/A";
    }

    private record RiskFactor(String name, String value, int weight, String reason) {}
}