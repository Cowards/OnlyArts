document.addEventListener("DOMContentLoaded", async () => {
  const menuBar = document.querySelector("#header");
  menuBar.addEventListener("click", (event) => {
    if (event.target.classList.contains("bx-menu")) {
      document.getElementById("sidebar").childNodes[0].childNodes[1].src =
        sidebar.classList.toggle("hide")
          ? "./assets/images/logo-no-text.png"
          : "./assets/images/logo.png";
    }
  });
});

window.addEventListener("resize", () => {
  const windowWidth =
    window.innerWidth ||
    document.documentElement.clientWidth ||
    document.body.clientWidth;
  const sideBar = document.querySelector("#sidebar");
  if (windowWidth <= 1280) {
    if (!sideBar.classList.contains("hide"))
      sideBar.classList.toggle("hide", true);
  } else {
    if (sideBar.classList.contains("hide"))
      sidebar.classList.toggle("hide", false);
  }
  sideBar.childNodes[0].childNodes[1].src = sidebar.classList.contains("hide")
    ? "./assets/images/logo-no-text.png"
    : "./assets/images/logo.png";
});


