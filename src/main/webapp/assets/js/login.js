const loginForm = document.querySelector(".login-form");
const loginErrorMessage = document.querySelector("#login-error-message");
loginForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  const email = document.querySelector("#email").value;
  const password = document.querySelector("#password").value;
  const loginInfo = {
    email: email,
    password: password,
  };
  try {
    const data = await send(
      "POST",
      "http://localhost:8080/OnlyArts/api/v1/authentication/login",
      loginInfo
    );
    localStorage.setItem("authtoken", data);
    window.location.href = `${api}/index.html`;
  } catch (error) {
    error = error.toString().replace("Error: ", "");
    loginErrorMessage.innerHTML = `${error}`;
  }
});
