const postId = new URLSearchParams(window.location.search).get("id");

async function loadPost() {
  const response = await fetch(`/api/posts/${postId}`);

  if (!response.ok) {
    document.getElementById("message").textContent =
      `게시글을 불러오지 못했습니다 (상태코드: ${response.status})`;
    return;
  }

  const post = await response.json();

  document.getElementById("title").textContent = post.title;
  document.getElementById("content").textContent = post.content;

  const createdAt = new Date(post.createdAt).toLocaleString();
  document.getElementById("meta").textContent =
    `${post.authorNickname} · 조회 ${post.viewCount} · ${createdAt}`;

  const meResponse = await fetch("/api/members/me");
  if (meResponse.ok) {
    const myLoginId = await meResponse.text();
    if (myLoginId === post.authorLoginId) {
      document.getElementById("authorActions").style.display = "block";
      document.getElementById("editLink").href = `write.html?id=${postId}`;
    }
  }
}

document.getElementById("deleteBtn").addEventListener("click", async () => {
  if (!confirm("정말 삭제하시겠습니까?")) {
    return;
  }

  const response = await fetch(`/api/posts/${postId}`, { method: "DELETE" });

  if (response.ok) {
    window.location.href = "board.html";
  } else {
    const error = await response.json();
    document.getElementById("message").textContent =
      error.message || `삭제 실패 (상태코드: ${response.status})`;
  }
});

loadPost();
