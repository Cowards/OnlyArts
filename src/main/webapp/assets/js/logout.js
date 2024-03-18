import http from "./http.js";
const logout = async () => {
  try {
    await http.send("DELETE", "/api/v1/authentication/logout");
    localStorage.removeItem("authtoken");
    window.location.href = "home";
  } catch (error) {
    console.log(error);
  }
};
logout();
