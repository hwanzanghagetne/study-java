fetch("/api/members/me").then((response) => {
  if (response.ok) {
    window.location.href = "board.html";
  } else {
    window.location.href = "login.html";
  }
});
