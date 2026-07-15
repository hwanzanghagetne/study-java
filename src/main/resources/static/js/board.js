let currentPage = 0;

async function loadPosts(page) {
  const keyword = document.getElementById("keyword").value.trim();
  const searchType = document.getElementById("searchType").value;

  let url = `/api/posts?page=${page}&size=10&sort=createdAt,desc`;
  if (keyword) {
    url += `&keyword=${encodeURIComponent(keyword)}&searchType=${searchType}`;
  }

  const response = await fetch(url);

  if (!response.ok) {
    document.getElementById("message").textContent =
      `목록을 불러오지 못했습니다 (상태코드: ${response.status})`;
    return;
  }

  const pageData = await response.json();
  currentPage = pageData.number;

  const postList = document.getElementById("postList");
  postList.innerHTML = "";

  if (pageData.content.length === 0) {
    postList.innerHTML = "<li class=\"no-post\">검색 결과가 없습니다.</li>";
  }

  pageData.content.forEach((post) => {
    const li = document.createElement("li");
    const createdAt = new Date(post.createdAt).toLocaleString();
    li.innerHTML = `
      <a href="post-detail.html?id=${post.id}">${post.title}</a>
      <span class="post-meta">${post.authorNickname} · 조회 ${post.viewCount} · ${createdAt}</span>
    `;
    postList.appendChild(li);
  });

  document.getElementById("pageInfo").textContent =
    `${pageData.number + 1} / ${pageData.totalPages === 0 ? 1 : pageData.totalPages}`;
  document.getElementById("prevBtn").disabled = pageData.first;
  document.getElementById("nextBtn").disabled = pageData.last;
}

document.getElementById("prevBtn").addEventListener("click", () => {
  loadPosts(currentPage - 1);
});

document.getElementById("nextBtn").addEventListener("click", () => {
  loadPosts(currentPage + 1);
});

document.getElementById("searchForm").addEventListener("submit", (event) => {
  event.preventDefault();
  loadPosts(0);
});

document.getElementById("logoutBtn").addEventListener("click", async () => {
  await fetch("/api/members/logout", { method: "POST" });
  window.location.href = "login.html";
});

loadPosts(0);
