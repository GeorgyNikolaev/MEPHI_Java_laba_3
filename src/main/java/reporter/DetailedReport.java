package reporter;

import domain.*;
import enums.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class DetailedReport implements MissionReporter {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    public Report generate(Mission mission) {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════════════════╗\n");
        sb.append("║               ДЕТАЛЬНЫЙ ОТЧЕТ О МИССИИ                       ║\n");
        sb.append("╚══════════════════════════════════════════════════════════════╝\n\n");

        // 1. Основная информация
        sb.append("📍 ОСНОВНЫЕ ДАННЫЕ\n");
        sb.append("ID миссии      : ").append(mission.getMissionId()).append("\n");
        sb.append("Дата           : ").append(mission.getDate() != null ? mission.getDate().format(DATE_FMT) : "N/A").append("\n");
        sb.append("Локация        : ").append(mission.getLocation()).append("\n");
        sb.append("Результат      : ").append(formatEnum(mission.getOutcome())).append("\n");
        if (mission.getDamageCost() != null) {
            sb.append("Общий ущерб    : ").append(formatMoney(mission.getDamageCost())).append("\n");
        }
        if (mission.getComment() != null) {
            sb.append("Комментарий    : ").append(mission.getComment()).append("\n");
        }
        sb.append("\n");

        // 2. Проклятие
        Curse curse = mission.getCurse();
        if (curse != null) {
            sb.append("👹 ИНФОРМАЦИЯ О ПРОКЛЯТИИ\n");
            sb.append("Название       : ").append(curse.getName()).append("\n");
            sb.append("Уровень угрозы : ").append(formatEnum(curse.getThreatLevel())).append("\n");
            sb.append("\n");
        }

        // 3. Участники
        List<Sorcerer> sorcerers = mission.getSorcerers();
        if (sorcerers != null && !sorcerers.isEmpty()) {
            sb.append("🧙 УЧАСТНИКИ МИССИИ (").append(sorcerers.size()).append(")\n");
            for (Sorcerer s : sorcerers) {
                sb.append("• ").append(s.getName())
                        .append(" (").append(formatEnum(s.getRank())).append(")\n");
            }
            sb.append("\n");
        }

        // 4. Техники
        List<TechniqueUsage> techniques = mission.getTechniques();
        if (techniques != null && !techniques.isEmpty()) {
            sb.append("⚡ ИСПОЛЬЗОВАННЫЕ ТЕХНИКИ (").append(techniques.size()).append(")\n");
            for (TechniqueUsage t : techniques) {
                Technique tech = t.getTechnique();
                sb.append("• ").append(tech != null ? tech.getName() : "Неизвестно")
                        .append(" [").append(formatEnum(tech != null ? tech.getType() : null)).append("]\n");
                if (t.getOwner() != null) sb.append("  Владелец: ").append(t.getOwner().getName()).append("\n");
                if (t.getDamage() != null) sb.append("  Урон: ").append(formatMoney(t.getDamage())).append("\n");
            }
            sb.append("\n");
        }

        // 5. Расширяющие блоки (Environment, Enemy, Civilian, Economic, Timeline)
        EnvironmentConditions env = mission.getEnvironmentConditions();
        if (env != null) {
            sb.append("🌍 УСЛОВИЯ СРЕДЫ\n");
            sb.append("Погода         : ").append(formatEnum(env.getWeather())).append("\n");
            sb.append("Время суток    : ").append(formatEnum(env.getTimeOfDay())).append("\n");
            sb.append("Видимость      : ").append(formatEnum(env.getVisibility())).append("\n");
            if (env.getCursedEnergyDensity() != null) {
                sb.append("Плотность энергии: ").append(env.getCursedEnergyDensity()).append("%\n");
            }
            sb.append("\n");
        }

        EnemyActivity enemy = mission.getEnemyActivity();
        if (enemy != null) {
            sb.append("👿 АКТИВНОСТЬ ПРОТИВНИКА\n");
            sb.append("Тип поведения  : ").append(formatEnum(enemy.getBehaviorType())).append("\n");
            sb.append("Мобильность    : ").append(formatEnum(enemy.getMobility())).append("\n");
            sb.append("Риск эскалации : ").append(formatEnum(enemy.getEscalationRisk())).append("\n");
            if (!enemy.getTargetPriority().isEmpty()) {
                sb.append("Приоритеты     : ").append(String.join(", ", enemy.getTargetPriority())).append("\n");
            }
            if (!enemy.getAttackPatterns().isEmpty()) {
                sb.append("Паттерны атак  :\n");
                enemy.getAttackPatterns().forEach(p -> sb.append("  - ").append(p).append("\n"));
            }
            if (!enemy.getCountermeasuresUsed().isEmpty()) {
                sb.append("Контрмеры      :\n");
                enemy.getCountermeasuresUsed().forEach(m-> sb.append("  - ").append(m).append("\n"));
            }
            sb.append("\n");
        }

        List<EnemyAction> enemyActions = mission.getEnemyActions();
        if (enemyActions != null && !enemyActions.isEmpty()) {
            sb.append("\uD83D\uDC63️ ДЕЙСТВИЯ ПРОТИВНИКА\n");
            enemyActions.forEach(event -> {
                sb.append("• ").append(event != null ? event.getName() : "Неизвестно")
                        .append(" [").append(formatEnum(event != null ? event.getType() : null)).append("]\n");
            });
            sb.append("\n");
        }

        CivilianImpact civ = mission.getCivilianImpact();
        if (civ != null) {
            sb.append("👥 ВЛИЯНИЕ НА ГРАЖДАНСКИХ\n");
            if (civ.getEvacuated() != null) sb.append("Эвакуировано   : ").append(civ.getEvacuated()).append("\n");
            if (civ.getInjured() != null) sb.append("Пострадавшие   : ").append(civ.getInjured()).append("\n");
            if (civ.getMissing() != null) sb.append("Пропавшие      : ").append(civ.getMissing()).append("\n");
            sb.append("\n");
        }

        EconomicAssessment econ = mission.getEconomicAssessment();
        if (econ != null) {
            sb.append("💰 ЭКОНОМИЧЕСКАЯ ОЦЕНКА\n");
            if (econ.getTotalDamageCost() != null) sb.append("Общий ущерб    : ").append(formatMoney(econ.getTotalDamageCost())).append("\n");
            if (econ.getInfrastructureDamage() != null) sb.append("Инфраструктура : ").append(formatMoney(econ.getInfrastructureDamage())).append("\n");
            if (econ.getCommercialDamage() != null) sb.append("Коммерческий   : ").append(formatMoney(econ.getCommercialDamage())).append("\n");
            if (econ.getTransportDamage() != null) sb.append("Транспорт      : ").append(formatMoney(econ.getTransportDamage())).append("\n");
            if (econ.getRecoveryEstimateDays() != null) sb.append("Восстановление: ").append(econ.getRecoveryEstimateDays()).append(" дн.\n");
            sb.append("Страховка      : ").append(econ.getInsuranceCovered() != null && econ.getInsuranceCovered() ? "Да" : "Нет").append("\n");
            sb.append("\n");
        }

        // 6. Хронология
        List<OperationTimeline> timeline = mission.getOperationTimeline();
        if (timeline != null && !timeline.isEmpty()) {
            sb.append("⏱️ ХРОНОЛОГИЯ ОПЕРАЦИИ\n");
            timeline.forEach(event -> {
                String time = event.getTimestamp() != null ? event.getTimestamp().format(TIME_FMT) : "??:??";
                sb.append("[").append(time).append("] ")
                        .append(formatEnum(event.getType())).append(": ")
                        .append(event.getDescription()).append("\n");
            });
            sb.append("\n");
        }

        sb.append("═══════════════════════════════════════════════════════════════\n");
        sb.append("Отчет сформирован: ").append(java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));

        return new Report(sb.toString(), ReportType.DETAILED);
    }

    @Override
    public ReportType getType() {
        return ReportType.DETAILED;
    }

    private String formatEnum(Enum<?> e) {
        return e != null ? e.name().replace('_', ' ') : "N/A";
    }

    private String formatMoney(long amount) {
        return String.format("%,d ¥", amount);
    }
}