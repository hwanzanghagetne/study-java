document
  .getElementById("signupForm")
  .addEventListener("submit", async function (event) {
    event.preventDefault();

    const loginId = document.getElementById("loginId").value;
    const password = document.getElementById("password").value;
    const name = document.getElementById("name").value;
    const nickname = document.getElementById("nickname").value;
    const email = document.getElementById("email").value;

    const response = await fetch("/api/members/signup", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ loginId, password, name, nickname, email }),
    });

    if (response.ok) {
      document.getElementById("message").textContent =
        "가입 완료! 로그인 페이지로 이동합니다.";
      window.location.href = "login.html";
    } else {
      document.getElementById("message").textContent =
        `가입 실패 (상태코드: ${response.status})`;
    }
  });
