const statusEl = document.getElementById("status");
const messageLog = document.getElementById("messageLog");
const roomIdDisplay = document.getElementById("roomIdDisplay");

let roomId = null;
let roomSubscription = null;

function logMessage(text) {
  const li = document.createElement("li");
  li.textContent = text;
  messageLog.appendChild(li);
}

const client = new StompJs.Client({
  brokerURL: `ws://${window.location.host}/ws`,
  onConnect: () => {
    statusEl.textContent = "연결됨";
  },
  onDisconnect: () => {
    statusEl.textContent = "연결 끊김";
  },
  onStompError: (frame) => {
    statusEl.textContent = `에러: ${frame.headers["message"]}`;
  },
});

client.activate();

document.getElementById("createRoomBtn").addEventListener("click", async () => {
  const targetLoginId = document.getElementById("targetLoginId").value;

  const response = await fetch("/api/chat/rooms/direct", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "same-origin",
    body: JSON.stringify({ targetLoginId }),
  });

  if (!response.ok) {
    logMessage(`방 생성 실패: ${response.status}`);
    return;
  }

  roomId = await response.json();
  roomIdDisplay.textContent = `roomId: ${roomId}`;

  if (roomSubscription) {
    roomSubscription.unsubscribe();
  }
  roomSubscription = client.subscribe(`/topic/room/${roomId}`, (message) => {
    const body = JSON.parse(message.body);
    logMessage(`[실시간] ${body.senderNickname}: ${body.content}`);
  });
});

document.getElementById("loadHistoryBtn").addEventListener("click", async () => {
  if (!roomId) {
    logMessage("먼저 방을 생성/조회하세요.");
    return;
  }

  const response = await fetch(`/api/chat/rooms/${roomId}/messages`, {
    credentials: "same-origin",
  });

  if (!response.ok) {
    logMessage(`내역 조회 실패: ${response.status}`);
    return;
  }

  const messages = await response.json();
  messageLog.innerHTML = "";
  messages.forEach((m) => {
    logMessage(`[내역] ${m.senderNickname}: ${m.content}`);
  });
});

document.getElementById("sendForm").addEventListener("submit", (event) => {
  event.preventDefault();

  if (!roomId) {
    logMessage("먼저 방을 생성/조회하세요.");
    return;
  }

  const input = document.getElementById("messageInput");
  const text = input.value;

  client.publish({
    destination: `/app/chat/${roomId}`,
    headers: { "content-type": "application/json" },
    body: JSON.stringify({ content: text }),
  });
  input.value = "";
});
