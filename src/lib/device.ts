import * as Application from "expo-application";
import { Platform } from "react-native";
import { supabase } from "./supabase";

export async function savePushToken(expoPushToken: string) {
  const deviceId =
    Application.getAndroidId?.() ||
    Application.getIosIdForVendorAsync?.() ||
    null;

  const { error } = await supabase.from("devices").upsert(
    {
      device_id: typeof deviceId === "string" ? deviceId : null,
      platform: Platform.OS,
      expo_push_token: expoPushToken,
    },
    { onConflict: "expo_push_token" },
  );

  if (error) throw error;
}
