const postId = new URLSearchParams(window.location.search).get("id");
const isEditMode = postId !== null;

if (isEditMode) {
  document.getElementById("pageTitle").textContent = "게시글 수정";
  document.getElementById("submitBtn").textContent = "수정";

  fetch(`/api/posts/${postId}`)
    .then((response) => response.json())
    .then((post) => {
      document.getElementById("title").value = post.title;
      document.getElementById("content").value = post.content;
    });
}

document
  .getElementById("writeForm")
  .addEventListener("submit", async function (event) {
    event.preventDefault();

    const title = document.getElementById("title").value;
    const content = document.getElementById("content").value;

    const response = await fetch(
      isEditMode ? `/api/posts/${postId}` : "/api/posts",
      {
        method: isEditMode ? "PUT" : "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ title, content }),
      }
    );

    if (response.ok) {
      const targetId = isEditMode ? postId : await response.json();
      window.location.href = `post-detail.html?id=${targetId}`;
    } else {
      const error = await response.json();
      document.getElementById("message").textContent =
        error.message || `실패 (상태코드: ${response.status})`;
    }
  });
