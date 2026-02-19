package com.ohana.ohanaserver.health.domain

object GrowthStandard {
    // WHO Growth Standards (Boys, Weight-for-age)
    // 개월수: [3%(하위), 15%, 50%(평균), 85%, 97%(상위)]
    private val BOY_WEIGHT_DATA = mapOf(
        0 to listOf(2.5, 2.9, 3.3, 3.9, 4.4),
        1 to listOf(3.4, 3.9, 4.5, 5.1, 5.8),
        2 to listOf(4.3, 4.9, 5.6, 6.3, 7.1),
        3 to listOf(5.0, 5.7, 6.4, 7.2, 8.0), // 👈 도현이 지금 여기
        4 to listOf(5.6, 6.2, 7.0, 7.8, 8.7),
        5 to listOf(6.0, 6.7, 7.5, 8.4, 9.3),
        6 to listOf(6.4, 7.1, 7.9, 8.8, 9.8),
        7 to listOf(6.7, 7.4, 8.3, 9.2, 10.3),
        8 to listOf(6.9, 7.7, 8.6, 9.6, 10.7),
        9 to listOf(7.1, 8.0, 8.9, 9.9, 11.0),
        10 to listOf(7.4, 8.2, 9.2, 10.2, 11.4),
        11 to listOf(7.6, 8.4, 9.4, 10.5, 11.7),
        12 to listOf(7.7, 8.6, 9.6, 10.8, 12.0)
    )

    fun analyzeWeight(months: Int, weight: Double): String {
        // 데이터 없으면(12개월 초과 등) 분석 불가
        val standards = BOY_WEIGHT_DATA[months] ?: return ""

        return when {
            weight < standards[0] -> "하위 3% 미만"
            weight < standards[1] -> "하위 3~15%"
            weight < standards[2] -> "평균 이하 (15~50%)"
            weight < standards[3] -> "평균 이상 (50~85%)"
            weight < standards[4] -> "상위 85~97%"
            else -> "상위 3% (우량아)"
        }
    }
}
