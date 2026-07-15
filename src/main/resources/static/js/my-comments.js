let currentPage = 0;

async function loadComments(page) {
  const response = await fetch(`/api/members/me/comments?page=${page}&size=10&sort=createdAt,desc`);

  if (!response.ok) {
    document.getElementById("message").textContent =
      `목록을 불러오지 못했습니다 (상태코드: ${response.status})`;
    return;
  }

  const pageData = await response.json();
  currentPage = pageData.number;

  const commentList = document.getElementById("commentList");
  commentList.innerHTML = "";

  if (pageData.content.length === 0) {
    commentList.innerHTML = "<li class=\"no-post\">아직 작성한 댓글이 없습니다.</li>";
  }

  pageData.content.forEach((comment) => {
    const li = document.createElement("li");
    const createdAt = new Date(comment.createdAt).toLocaleString();

    const link = document.createElement("a");
    link.href = `post-detail.html?id=${comment.postId}`;
    link.textContent = comment.content;

    const meta = document.createElement("span");
    meta.className = "post-meta";
    meta.textContent = `${comment.postTitle} · ${createdAt}`;

    li.appendChild(link);
    li.appendChild(meta);
    commentList.appendChild(li);
  });

  document.getElementById("pageInfo").textContent =
    `${pageData.number + 1} / ${pageData.totalPages === 0 ? 1 : pageData.totalPages}`;
  document.getElementById("prevBtn").disabled = pageData.first;
  document.getElementById("nextBtn").disabled = pageData.last;
}

document.getElementById("prevBtn").addEventListener("click", () => {
  loadComments(currentPage - 1);
});

document.getElementById("nextBtn").addEventListener("click", () => {
  loadComments(currentPage + 1);
});

loadComments(0);
