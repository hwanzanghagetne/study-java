const postId = new URLSearchParams(window.location.search).get("id");
let myLoginId = null;

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
    myLoginId = await meResponse.text();
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

async function loadComments() {
  const response = await fetch(`/api/posts/${postId}/comments`);

  if (!response.ok) {
    document.getElementById("commentMessage").textContent =
      `댓글을 불러오지 못했습니다 (상태코드: ${response.status})`;
    return;
  }

  const comments = await response.json();
  const commentList = document.getElementById("commentList");
  commentList.innerHTML = "";

  comments.forEach((comment) => {
    const createdAt = new Date(comment.createdAt).toLocaleString();
    const li = document.createElement("li");

    const deleteButtonHtml =
      comment.authorLoginId === myLoginId
        ? `<button class="comment-delete-btn" data-id="${comment.id}">삭제</button>`
        : "";

    li.innerHTML = `
      <div class="comment-header">
        <span class="comment-author">${comment.authorNickname}</span>
        <span class="comment-date">${createdAt}</span>
        ${deleteButtonHtml}
      </div>
      <p class="comment-content">${comment.content}</p>
    `;
    commentList.appendChild(li);
  });

  document.querySelectorAll(".comment-delete-btn").forEach((btn) => {
    btn.addEventListener("click", async () => {
      if (!confirm("댓글을 삭제하시겠습니까?")) {
        return;
      }

      const commentId = btn.dataset.id;
      const response = await fetch(`/api/comments/${commentId}`, {
        method: "DELETE",
      });

      if (response.ok) {
        loadComments();
      } else {
        const error = await response.json();
        document.getElementById("commentMessage").textContent =
          error.message || `댓글 삭제 실패 (상태코드: ${response.status})`;
      }
    });
  });
}

document
  .getElementById("commentForm")
  .addEventListener("submit", async (event) => {
    event.preventDefault();

    const content = document.getElementById("commentContent").value;

    const response = await fetch(`/api/posts/${postId}/comments`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ content }),
    });

    if (response.ok) {
      document.getElementById("commentContent").value = "";
      document.getElementById("commentMessage").textContent = "";
      loadComments();
    } else {
      const error = await response.json();
      document.getElementById("commentMessage").textContent =
        error.message || `댓글 작성 실패 (상태코드: ${response.status})`;
    }
  });

loadPost().then(loadComments);
