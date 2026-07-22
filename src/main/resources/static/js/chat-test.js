const statusEl = document.getElementById("status");
const messageLog = document.getElementById("messageLog");

function logMessage(text) {
  const li = document.createElement("li");
  li.textContent = text;
  messageLog.appendChild(li);
}

const client = new StompJs.Client({
  brokerURL: `ws://${window.location.host}/ws`,
  onConnect: () => {
    statusEl.textContent = "연결됨";
    client.subscribe("/topic/echo", (message) => {
      logMessage(`받음: ${message.body}`);
    });
  },
  onDisconnect: () => {
    statusEl.textContent = "연결 끊김";
  },
  onStompError: (frame) => {
    statusEl.textContent = `에러: ${frame.headers["message"]}`;
  },
});

client.activate();

document.getElementById("sendForm").addEventListener("submit", (event) => {
  event.preventDefault();

  const input = document.getElementById("messageInput");
  const text = input.value;

  client.publish({ destination: "/app/echo", body: text });
  logMessage(`보냄: ${text}`);
  input.value = "";
});
