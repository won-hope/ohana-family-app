import { useState } from "react";
import { Button, Text, View } from "react-native";
import { savePushToken } from "../src/lib/device";
import { registerForPushTokenAsync } from "../src/lib/notifications";

export default function Home() {
  const [msg, setMsg] = useState("ready");

  const onRegister = async () => {
    try {
      setMsg("requesting...");
      const token = await registerForPushTokenAsync();
      await savePushToken(token);
      setMsg(`saved token: ${token}`);
    } catch (e: any) {
      setMsg(`error: ${e?.message ?? String(e)}`);
    }
  };

  return (
    <View
      style={{
        flex: 1,
        alignItems: "center",
        justifyContent: "center",
        gap: 12,
      }}
    >
      <Text>Ohana Push 테스트</Text>
      <Button title="푸시 토큰 등록" onPress={onRegister} />
      <Text style={{ paddingHorizontal: 16 }}>{msg}</Text>
    </View>
  );
}
