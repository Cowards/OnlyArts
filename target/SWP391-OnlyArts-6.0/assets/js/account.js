import http from "./http.js";

const sideBar = document.querySelector("#sidebar");
sideBar.addEventListener("click", (event) => {
  if (event.target.classList.contains("logout")) {
    localStorage.removeItem("authtoken");
    window.location.href = "home";
  }
});
