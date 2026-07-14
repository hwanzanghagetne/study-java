async function loadProfile() {
  const response = await fetch("/api/members/me/profile");

  if (!response.ok) {
    document.getElementById("profileMessage").textContent =
      `정보를 불러오지 못했습니다 (상태코드: ${response.status})`;
    return;
  }

  const member = await response.json();
  document.getElementById("loginId").value = member.loginId;
  document.getElementById("nickname").value = member.nickname;
  document.getElementById("email").value = member.email;
}

document
  .getElementById("profileForm")
  .addEventListener("submit", async (event) => {
    event.preventDefault();

    const nickname = document.getElementById("nickname").value;
    const email = document.getElementById("email").value;

    const response = await fetch("/api/members/me", {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ nickname, email }),
    });

    if (response.ok) {
      document.getElementById("profileMessage").textContent = "저장되었습니다.";
    } else {
      const error = await response.json();
      document.getElementById("profileMessage").textContent =
        error.message || `저장 실패 (상태코드: ${response.status})`;
    }
  });

document
  .getElementById("passwordForm")
  .addEventListener("submit", async (event) => {
    event.preventDefault();

    const currentPassword = document.getElementById("currentPassword").value;
    const newPassword = document.getElementById("newPassword").value;

    const response = await fetch("/api/members/me/password", {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ currentPassword, newPassword }),
    });

    if (response.ok) {
      document.getElementById("passwordMessage").textContent =
        "비밀번호가 변경되었습니다.";
      document.getElementById("passwordForm").reset();
    } else {
      const error = await response.json();
      document.getElementById("passwordMessage").textContent =
        error.message || `변경 실패 (상태코드: ${response.status})`;
    }
  });

loadProfile();
