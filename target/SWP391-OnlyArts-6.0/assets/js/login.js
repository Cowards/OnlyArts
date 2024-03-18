import http from "./http.js";

const loginForm = document.querySelector(".login-form");
const loginErrorMessage = document.querySelector("#login-error-message");

loginForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const formData = new FormData(loginForm);
  const email = formData.get("email");
  const password = formData.get("password");
  try {
    const data = await http.send("POST", `/api/v1/authentication/login`, {
      email,
      password,
    });
    localStorage.setItem("authtoken", data);
    window.location.href = "home";
  } catch (error) {
    loginErrorMessage.textContent = error.message;
  }
});
