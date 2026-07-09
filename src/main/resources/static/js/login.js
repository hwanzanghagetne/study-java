document
  .getElementById("loginForm")
  .addEventListener("submit", async function (event) {
    event.preventDefault();

    const loginId = document.getElementById("loginId").value;
    const password = document.getElementById("password").value;

    const response = await fetch("/api/members/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ loginId, password }),
    });

    if (response.ok) {
      document.getElementById("message").textContent = "로그인 성공!";
      window.location.href = "write.html";
    } else {
      document.getElementById("message").textContent = "로그인 실패";
    }
  });
