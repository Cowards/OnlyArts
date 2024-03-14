import http from "./http.js";

const authtoken = localStorage.getItem("authtoken");
const getAccountInfo = async () => {
  const AD = [
    "index",
    "dashboard",
    "artwork",
    "notification",
    "account",
    "contact",
    "about",
    "logout",
  ];
  const CR = [
    "index",
    "dashboard",
    "artwork",
    "request",
    "notification",
    "account",
    "contact",
    "about",
    "logout",
  ];
  const CT = [
    "index",
    "dashboard",
    "artwork",
    "cart",
    "notification",
    "account",
    "contact",
    "about",
    "logout",
  ];
  const GS = ["index", "artwork", "contact", "about", "login"];
  try {
    const account = await http.send("GET", "/api/v1/authentication/account");
    const sideBar = document.querySelector("#sidebar");
    const tabList = sideBar.querySelectorAll("li");
    const accountInfo = JSON.parse(account);
    tabList.forEach((tab) => {
      if (accountInfo.roleId === "AD") {
        if (!AD.includes(tab.id)) tab.style.display = "none";
      } else if (accountInfo.roleId === "CR") {
        if (!CR.includes(tab.id)) tab.style.display = "none";
      } else if (accountInfo.roleId === "CT") {
        if (!CT.includes(tab.id)) tab.style.display = "none";
      } else {
        if (!GS.includes(tab.id)) tab.style.display = "none";
      }
    });
  } catch (error) {
    const sideBar = document.querySelector("#sidebar");
    const tabList = sideBar.querySelectorAll("li");
    tabList.forEach((tab) => {
      if (!GS.includes(tab.id)) tab.style.display = "none";
    });
  }
};
getAccountInfo();

export default getAccountInfo;
