package com.ohana.ohanaserver.health.domain

enum class VaccineType(val koName: String) {
    HEP_B("B형간염"),
    BCG("BCG(결핵)"),
    DTAP("DTaP(디프테리아/파상풍/백일해)"),
    IPV("IPV(폴리오)"),
    HIB("Hib(뇌수막염)"),
    PCV("PCV(폐렴구균)"),
    RV("로타바이러스"),
    MMR("MMR(홍역/유행성이하선염/풍진)"),
    VAR("수두"),
    HEP_A("A형간염"),
    IJEV("일본뇌염(사백신)") // 생백신은 선택사항이라 표준인 사백신 기준
}

enum class VaccinationSchedule(
    val vaccine: VaccineType,
    val dose: Int,
    val monthOffset: Long // 생후 N개월
) {
    // --- 0개월 (출생) ---
    HEP_B_1(VaccineType.HEP_B, 1, 0),

    // --- 1개월 (4주) ---
    BCG_1(VaccineType.BCG, 1, 1),
    HEP_B_2(VaccineType.HEP_B, 2, 1),

    // --- 2개월 (기본 접종 시작) ---
    DTAP_1(VaccineType.DTAP, 1, 2),
    IPV_1(VaccineType.IPV, 1, 2),
    HIB_1(VaccineType.HIB, 1, 2),
    PCV_1(VaccineType.PCV, 1, 2),
    RV_1(VaccineType.RV, 1, 2),

    // --- 4개월 ---
    DTAP_2(VaccineType.DTAP, 2, 4),
    IPV_2(VaccineType.IPV, 2, 4),
    HIB_2(VaccineType.HIB, 2, 4),
    PCV_2(VaccineType.PCV, 2, 4),
    RV_2(VaccineType.RV, 2, 4),

    // --- 6개월 ---
    HEP_B_3(VaccineType.HEP_B, 3, 6),
    DTAP_3(VaccineType.DTAP, 3, 6),
    IPV_3(VaccineType.IPV, 3, 6),
    HIB_3(VaccineType.HIB, 3, 6),
    PCV_3(VaccineType.PCV, 3, 6),
    RV_3(VaccineType.RV, 3, 6),

    // --- 12개월 (돌) ---
    HIB_4(VaccineType.HIB, 4, 12),
    PCV_4(VaccineType.PCV, 4, 12),
    MMR_1(VaccineType.MMR, 1, 12),
    VAR_1(VaccineType.VAR, 1, 12),
    HEP_A_1(VaccineType.HEP_A, 1, 12),
    IJEV_1(VaccineType.IJEV, 1, 12),

    // --- 15~18개월 ---
    DTAP_4(VaccineType.DTAP, 4, 15),
    IJEV_2(VaccineType.IJEV, 2, 13), // 1차 후 1개월 뒤 (보통 13개월)
    HEP_A_2(VaccineType.HEP_A, 2, 18), // 1차 후 6개월 뒤 (18개월)

    // --- 만 4~6세 (추가 접종) ---
    MMR_2(VaccineType.MMR, 2, 48), // 48개월(4세)
    DTAP_5(VaccineType.DTAP, 5, 48),
    IPV_4(VaccineType.IPV, 4, 48),
    IJEV_3(VaccineType.IJEV, 3, 24); // 24개월(2세) - 사백신 3차
}
