document
  .getElementById("writeForm")
  .addEventListener("submit", async function (event) {
    event.preventDefault();

    const title = document.getElementById("title").value;
    const content = document.getElementById("content").value;

    const response = await fetch("/api/posts", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ title, content }),
    });

    if (response.ok) {
      const postId = await response.json();
      document.getElementById("message").textContent =
        `작성 완료! 게시글 번호: ${postId}`;
    } else {
      document.getElementById("message").textContent =
        `작성 실패 (상태코드: ${response.status})`;
    }
  });
