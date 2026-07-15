const postId = new URLSearchParams(window.location.search).get("id");
let myLoginId = null;
let isAuthor = false;

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
      isAuthor = true;
      document.getElementById("authorActions").style.display = "block";
      document.getElementById("editLink").href = `write.html?id=${postId}`;
      document.getElementById("fileUploadForm").style.display = "block";
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

    const isCommentAuthor = comment.authorLoginId === myLoginId;

    const editButtonHtml = isCommentAuthor
      ? `<button class="comment-edit-btn" data-id="${comment.id}">수정</button>`
      : "";
    const deleteButtonHtml = isCommentAuthor
      ? `<button class="comment-delete-btn" data-id="${comment.id}">삭제</button>`
      : "";

    li.innerHTML = `
      <div class="comment-header">
        <span class="comment-author">${comment.authorNickname}</span>
        <span class="comment-date">${createdAt}</span>
        <div class="comment-actions">
          ${editButtonHtml}
          ${deleteButtonHtml}
        </div>
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

  document.querySelectorAll(".comment-edit-btn").forEach((btn) => {
    btn.addEventListener("click", () => {
      const commentId = btn.dataset.id;
      const li = btn.closest("li");
      const contentEl = li.querySelector(".comment-content");
      const originalContent = contentEl.textContent;

      const form = document.createElement("div");
      form.className = "comment-edit-form";

      const textarea = document.createElement("textarea");
      textarea.className = "comment-edit-textarea";
      textarea.value = originalContent;

      const actions = document.createElement("div");
      actions.className = "comment-edit-actions";

      const saveBtn = document.createElement("button");
      saveBtn.type = "button";
      saveBtn.className = "comment-save-btn";
      saveBtn.textContent = "저장";

      const cancelBtn = document.createElement("button");
      cancelBtn.type = "button";
      cancelBtn.className = "comment-cancel-btn";
      cancelBtn.textContent = "취소";

      actions.appendChild(saveBtn);
      actions.appendChild(cancelBtn);
      form.appendChild(textarea);
      form.appendChild(actions);

      contentEl.replaceWith(form);
      textarea.focus();

      cancelBtn.addEventListener("click", () => {
        loadComments();
      });

      saveBtn.addEventListener("click", async () => {
        const newContent = textarea.value;

        const response = await fetch(`/api/comments/${commentId}`, {
          method: "PATCH",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ content: newContent }),
        });

        if (response.ok) {
          loadComments();
        } else {
          const error = await response.json();
          document.getElementById("commentMessage").textContent =
            error.message || `댓글 수정 실패 (상태코드: ${response.status})`;
        }
      });
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

async function loadFiles() {
  const response = await fetch(`/api/posts/${postId}/files`);

  if (!response.ok) {
    document.getElementById("fileMessage").textContent =
      `첨부파일을 불러오지 못했습니다 (상태코드: ${response.status})`;
    return;
  }

  const files = await response.json();
  const fileList = document.getElementById("fileList");
  fileList.innerHTML = "";

  if (files.length === 0) {
    fileList.innerHTML = "<li class=\"no-file\">첨부된 파일이 없습니다.</li>";
    return;
  }

  files.forEach((file) => {
    const li = document.createElement("li");
    const fileUrl = `/api/files/${file.id}`;
    const isImage = /\.(jpg|jpeg|png|gif|webp|bmp)$/i.test(file.originalFileName);

    const deleteButtonHtml = isAuthor
      ? `<button class="file-delete-btn" data-id="${file.id}">삭제</button>`
      : "";

    if (isImage) {
      li.innerHTML = `
        <a href="${fileUrl}" target="_blank">
          <img src="${fileUrl}" alt="${file.originalFileName}" class="file-thumbnail">
        </a>
        <span class="file-name">${file.originalFileName}</span>
        ${deleteButtonHtml}
      `;
    } else {
      li.innerHTML = `
        <a href="${fileUrl}" target="_blank" class="file-name">${file.originalFileName}</a>
        ${deleteButtonHtml}
      `;
    }

    fileList.appendChild(li);
  });

  document.querySelectorAll(".file-delete-btn").forEach((btn) => {
    btn.addEventListener("click", async () => {
      if (!confirm("파일을 삭제하시겠습니까?")) {
        return;
      }

      const fileId = btn.dataset.id;
      const response = await fetch(`/api/files/${fileId}`, {
        method: "DELETE",
      });

      if (response.ok) {
        loadFiles();
      } else {
        const error = await response.json();
        document.getElementById("fileMessage").textContent =
          error.message || `파일 삭제 실패 (상태코드: ${response.status})`;
      }
    });
  });
}

document
  .getElementById("fileForm")
  .addEventListener("submit", async (event) => {
    event.preventDefault();

    const fileInput = document.getElementById("fileInput");
    if (fileInput.files.length === 0) {
      document.getElementById("fileMessage").textContent = "파일을 선택하세요.";
      return;
    }

    const formData = new FormData();
    for (const file of fileInput.files) {
      formData.append("files", file);
    }

    const response = await fetch(`/api/posts/${postId}/files`, {
      method: "POST",
      body: formData,
    });

    if (response.ok) {
      document.getElementById("fileMessage").textContent = "업로드 완료!";
      fileInput.value = "";
      loadFiles();
    } else {
      const error = await response.json();
      document.getElementById("fileMessage").textContent =
        error.message || `업로드 실패 (상태코드: ${response.status})`;
    }
  });

loadPost().then(loadComments).then(loadFiles);
