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
}

loadPost();
