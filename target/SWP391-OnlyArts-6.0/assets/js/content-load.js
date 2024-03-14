document.addEventListener("DOMContentLoaded", async (event) => {
  await fetch("./assets/components/header.html")
    .then((response) => response.text())
    .then((data) => {
      document.querySelector("#header").innerHTML = data;
    });
  await fetch("./assets/components/sidebar.html")
    .then((response) => response.text())
    .then((data) => {
      const parser = new DOMParser();
      const doc = parser.parseFromString(data, "text/html");
      const currentUrl = window.location.href;
      const nav = doc.querySelectorAll("li");
      nav.forEach((item) => {
        if (currentUrl.includes(item.id)) {
          item.className += " active";
        }
      });
      doc.body.childNodes.forEach((node) => {
        document.querySelector("#sidebar").appendChild(node);
      });

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
      sideBar.childNodes[0].childNodes[1].src = sidebar.classList.contains(
        "hide"
      )
        ? "./assets/images/logo-no-text.png"
        : "./assets/images/logo.png";
    });
});
