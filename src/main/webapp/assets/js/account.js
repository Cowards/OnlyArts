const sendJson = async (method, url, body) => {
  try {
    const response = await fetch(url, {
      method: method,
      headers: {
        "Content-Type": "application/json",
        authtoken: localStorage.getItem("authtoken"),
      },
      body: body ? JSON.stringify(body) : undefined,
    });
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message);
    }
    if (response.status === 204) return;
    const data = await response.json(); // Parse the response as text
    return data; // Return the response as a string
  } catch (error) {
    console.log(error);
    throw error;
  }
};

const getAccountInfo = async () => {
  const tokenString = localStorage.getItem("authtoken");
  if (tokenString) {
    const user = await sendJson(
      "GET",
      "http://localhost:8080/OnlyArts/api/v1/authentication/account"
    );
    const accountImg = document.querySelector("#account-img");
    if (user.avatar) {
      const imageData = sendJson(
        "GET",
        "http://localhost:8080/OnlyArts/api/v1/image/{imageid}"
      );
      accountImg.src = `${imageData}`;
    } else {
      accountImg.src = "./assets/images/user.png";
    }
    if (user.roleId === "CT") {
      document.querySelector("#dashboard").style.display = "none";
    } else if (user.roleId === "CR" || user.roleId === "AD") {
      document.querySelector("#cart").style.display = "none";
    }
    document.querySelector("#login").style.display = "none";
    document.querySelector("#logout").style.display = "block";
  } else {
    document.querySelector("#login").style.display = "block";
    document.querySelector("#logout").style.display = "none";
    document.querySelector("#dashboard").style.display = "none";
    document.querySelector("#request").style.display = "none";
    document.querySelector("#cart").style.display = "none";
    document.querySelector("#account").style.display = "none";
  }
};
getAccountInfo();

document.querySelector("#logout").addEventListener("click", async (event) => {
  event.preventDefault();
  await sendJson(
    "DELETE",
    "http://localhost:8080/OnlyArts/api/v1/authentication/logout"
  );
  localStorage.removeItem("authtoken");
  location.reload();
});
