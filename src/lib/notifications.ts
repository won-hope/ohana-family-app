import Constants from "expo-constants";
import * as Device from "expo-device";
import * as Notifications from "expo-notifications";
import { Platform } from "react-native";

export async function registerForPushTokenAsync() {
  // 🛑 [수정됨] 에뮬레이터 테스트를 위해 이 부분은 잠시 주석 처리!
  // if (!Device.isDevice) {
  //   throw new Error("푸시는 실기기에서만 토큰이 발급돼요 (Expo Go 포함).");
  // }

  // 대신 경고 로그 정도만 남기자
  if (!Device.isDevice) {
    console.log(
      "⚠️ 에뮬레이터 감지됨: 안드로이드(Google Play 포함)가 아니면 푸시가 안 될 수 있어.",
    );
  }

  const { status: existingStatus } = await Notifications.getPermissionsAsync();
  let finalStatus = existingStatus;

  if (existingStatus !== "granted") {
    const { status } = await Notifications.requestPermissionsAsync();
    finalStatus = status;
  }
  if (finalStatus !== "granted") {
    throw new Error("알림 권한이 거부되었어요.");
  }

  // EAS 프로젝트 ID 가져오기 (이건 아주 좋아)
  const projectId =
    Constants.expoConfig?.extra?.eas?.projectId ??
    Constants.easConfig?.projectId;

  if (!projectId) throw new Error("EAS projectId가 없습니다.");

  // 토큰 발급
  const token = (await Notifications.getExpoPushTokenAsync({ projectId })).data;

  // 안드로이드 채널 설정 (필수)
  if (Platform.OS === "android") {
    await Notifications.setNotificationChannelAsync("default", {
      name: "default",
      importance: Notifications.AndroidImportance.MAX,
      vibrationPattern: [0, 250, 250, 250],
      lightColor: "#FF231F7C",
    });
  }

  return token;
}
